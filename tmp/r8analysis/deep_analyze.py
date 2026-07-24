#!/usr/bin/env python3
"""
Deep parse of R8 blast radius proto to extract build_info counts and keep rule sources.
Uses nested protobuf parsing to decode sub-messages.
"""
import sys
import struct

def read_varint(data, pos):
    result = 0
    shift = 0
    while pos < len(data):
        b = data[pos]
        pos += 1
        result |= (b & 0x7F) << shift
        if not (b & 0x80):
            break
        shift += 7
    return result, pos

def parse_message(data):
    """Parse a protobuf message, returning dict of {field_number: [values]}"""
    fields = {}
    pos = 0
    while pos < len(data):
        try:
            tag, pos = read_varint(data, pos)
            field_number = tag >> 3
            wire_type = tag & 0x7
            if wire_type == 0:
                value, pos = read_varint(data, pos)
                fields.setdefault(field_number, []).append(value)
            elif wire_type == 2:
                length, pos = read_varint(data, pos)
                value = data[pos:pos+length]
                fields.setdefault(field_number, []).append(value)
                pos += length
            elif wire_type == 5:
                value = struct.unpack_from('<I', data, pos)[0]
                fields.setdefault(field_number, []).append(value)
                pos += 4
            elif wire_type == 1:
                value = struct.unpack_from('<Q', data, pos)[0]
                fields.setdefault(field_number, []).append(value)
                pos += 8
            else:
                break
        except Exception:
            break
    return fields

def parse_build_info(data):
    """
    BuildInfo message fields:
      1 = class_count
      2 = field_count
      3 = method_count
      4 = live_class_count
      5 = live_field_count
      6 = live_method_count
    """
    fields = parse_message(data)
    return {
        "class_count": fields.get(1, [0])[0],
        "field_count": fields.get(2, [0])[0],
        "method_count": fields.get(3, [0])[0],
        "live_class_count": fields.get(4, [0])[0],
        "live_field_count": fields.get(5, [0])[0],
        "live_method_count": fields.get(6, [0])[0],
    }

def parse_keep_rule(data):
    """
    KeepRuleBlastRadius fields:
      1 = id
      2 = source (string)
      3 = constraints_id
      4 = origin (TextFileOrigin sub-message)
      5 = blast_radius (BlastRadius sub-message)
      6 = tags (repeated enum)
    """
    fields = parse_message(data)
    source = b""
    if 2 in fields and fields[2]:
        source = fields[2][0]
    
    blast_radius = {}
    if 5 in fields and fields[5]:
        br_fields = parse_message(fields[5][0])
        # BlastRadius: 1=subsumed_by, 2=class_blast_radius, 3=field_blast_radius, 4=method_blast_radius
        blast_radius = {
            "subsumed_by": br_fields.get(1, []),
            "classes": len(br_fields.get(2, [])),
            "fields": len(br_fields.get(3, [])),
            "methods": len(br_fields.get(4, [])),
        }
    
    constraints_id = fields.get(3, [0])[0]
    rule_id = fields.get(1, [0])[0]
    tags = fields.get(6, [])
    
    try:
        src_str = source.decode('utf-8', errors='replace')
    except:
        src_str = str(source)
    
    return {
        "id": rule_id,
        "source": src_str,
        "constraints_id": constraints_id,
        "blast_radius": blast_radius,
        "is_package_wide": 0 in tags,  # PACKAGE_WIDE = 0
    }

def parse_constraint(data):
    """
    KeepConstraints:
      1 = id
      2 = constraints (repeated enum: 0=DONT_OBFUSCATE, 1=DONT_OPTIMIZE, 2=DONT_SHRINK)
    """
    fields = parse_message(data)
    cid = fields.get(1, [0])[0]
    constraints = fields.get(2, [])
    return {"id": cid, "constraints": constraints}

def main():
    path = sys.argv[1] if len(sys.argv) > 1 else "tmp/r8analysis/keepradius18726549532499.pb"
    
    with open(path, 'rb') as f:
        data = f.read()
    
    # Top-level parse
    top = parse_message(data)
    
    # Extract build_info (field 15)
    build_info = {}
    if 15 in top and top[15]:
        build_info = parse_build_info(top[15][0])
    
    print("=== Build Info ===")
    for k, v in build_info.items():
        print(f"  {k}: {v:,}")
    
    live_total = (build_info.get('live_class_count', 0) + 
                  build_info.get('live_field_count', 0) + 
                  build_info.get('live_method_count', 0))
    total = (build_info.get('class_count', 0) + 
             build_info.get('field_count', 0) + 
             build_info.get('method_count', 0))
    print(f"  total_live: {live_total:,}")
    print(f"  total_all: {total:,}")
    
    # Extract constraint map
    constraint_map = {}
    for cb in top.get(12, []):
        c = parse_constraint(cb)
        constraint_map[c['id']] = c['constraints']
    
    # DONT_OBFUSCATE=0, DONT_OPTIMIZE=1, DONT_SHRINK=2
    
    # Extract keep rules
    rules = []
    for rb in top.get(13, []):
        rule = parse_keep_rule(rb)
        cid = rule['constraints_id']
        constraints = constraint_map.get(cid, [])
        rule['dont_optimize'] = 1 in constraints
        rule['dont_obfuscate'] = 0 in constraints
        rule['dont_shrink'] = 2 in constraints
        rule['impact'] = rule['blast_radius'].get('classes', 0) + rule['blast_radius'].get('fields', 0) + rule['blast_radius'].get('methods', 0)
        rules.append(rule)
    
    # Sort by impact
    rules.sort(key=lambda r: r['impact'], reverse=True)
    
    # Calculate scores using kept items
    kept_class_count = len(top.get(9, []))
    kept_field_count = len(top.get(10, []))
    kept_method_count = len(top.get(11, []))
    kept_total = kept_class_count + kept_field_count + kept_method_count
    
    denom = live_total if live_total > 0 else kept_total
    
    # Count items prevented from optimization (heuristic: rules with dont_optimize=True, look at blast radius)
    dont_opt_items = sum(r['impact'] for r in rules if r['dont_optimize'])
    dont_obf_items = sum(r['impact'] for r in rules if r['dont_obfuscate'])
    dont_shr_items = sum(r['impact'] for r in rules if r['dont_shrink'])
    
    def score(cnt):
        return max(0.0, 100.0 - ((cnt / denom * 100) if denom > 0 else 0))
    
    print("\n=== Optimization Scores ===")
    opt_score = score(dont_opt_items)
    obf_score = score(dont_obf_items)
    shr_score = score(dont_shr_items)
    print(f"  Optimization Score: {opt_score:.2f}%")
    print(f"  Obfuscation Score:  {obf_score:.2f}%")
    print(f"  Shrinking Score:    {shr_score:.2f}%")
    
    print(f"\n=== Top 10 Highest-Impact Keep Rules ===")
    for i, r in enumerate(rules[:10]):
        if r['impact'] == 0:
            continue
        src_short = r['source'][:80] if r['source'] else "(unknown)"
        br = r['blast_radius']
        pct = (r['impact'] / denom * 100) if denom > 0 else 0
        subsumed = "SUBSUMED" if r['blast_radius'].get('subsumed_by') else ""
        pkg_wide = "PKG-WIDE" if r['is_package_wide'] else ""
        flags = " ".join(filter(None, [subsumed, pkg_wide]))
        print(f"  [{i+1}] Impact: {r['impact']:,} ({pct:.2f}%) C={br.get('classes',0)} F={br.get('fields',0)} M={br.get('methods',0)} {flags}")
        print(f"       Source: {src_short}")
    
    # Subsumed rules
    subsumed = [r for r in rules if r['blast_radius'].get('subsumed_by')]
    if subsumed:
        print(f"\n=== Subsumed Rules ({len(subsumed)}) ===")
        for r in subsumed[:5]:
            print(f"  {r['source'][:70]}")
    
    # Write analysis result
    with open("tmp/r8analysis/analysis_result.txt", "w") as f:
        f.write(f"Optimization Score: {opt_score:.2f}%\n")
        f.write(f"Obfuscation Score:  {obf_score:.2f}%\n")
        f.write(f"Shrinking Score:    {shr_score:.2f}%\n")
        f.write(f"\nBuild Info:\n")
        for k, v in build_info.items():
            f.write(f"  {k}: {v}\n")
        f.write(f"\nTop Rules by Impact:\n")
        for r in rules[:10]:
            if r['impact'] > 0:
                f.write(f"  {r['impact']} ({(r['impact']/denom*100):.2f}%) — {r['source'][:80]}\n")
    
    print("\nResults written to tmp/r8analysis/analysis_result.txt")

if __name__ == "__main__":
    main()

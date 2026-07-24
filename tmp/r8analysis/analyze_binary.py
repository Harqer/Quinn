#!/usr/bin/env python3
"""
Direct R8 blast radius analysis using raw JSON extraction from the .pb binary.
Falls back to structural binary analysis if protobuf bindings fail.
"""
import json
import sys
import struct
import os

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

def parse_pb_field(data, pos):
    """Parse a single protobuf field, returning (field_number, wire_type, value, new_pos)"""
    if pos >= len(data):
        return None, None, None, pos
    tag, pos = read_varint(data, pos)
    field_number = tag >> 3
    wire_type = tag & 0x7
    
    if wire_type == 0:  # varint
        value, pos = read_varint(data, pos)
        return field_number, wire_type, value, pos
    elif wire_type == 2:  # length-delimited
        length, pos = read_varint(data, pos)
        value = data[pos:pos+length]
        return field_number, wire_type, value, pos + length
    elif wire_type == 5:  # 32-bit
        value = struct.unpack_from('<I', data, pos)[0]
        return field_number, wire_type, value, pos + 4
    elif wire_type == 1:  # 64-bit
        value = struct.unpack_from('<Q', data, pos)[0]
        return field_number, wire_type, value, pos + 8
    else:
        # unknown wire type, skip
        return field_number, wire_type, None, pos

def analyze_pb_binary(path):
    """
    Parse the R8 blast radius protobuf file and extract summary stats.
    
    BlastRadiusContainer field mapping (from proto schema):
      1  = file_origin_table
      2  = class_file_in_jar_origin_table
      3  = maven_coordinate_table
      4  = field_reference_table
      5  = method_reference_table
      6  = proto_reference_table
      7  = type_reference_table
      8  = type_reference_list_table
      9  = kept_class_info_table
      10 = kept_field_info_table
      11 = kept_method_info_table
      12 = keep_constraints_table
      13 = keep_rule_blast_radius_table
      14 = global_keep_rule_blast_radius_table
      15 = build_info
    """
    with open(path, 'rb') as f:
        data = f.read()
    
    print(f"Protobuf file size: {len(data):,} bytes")
    
    # Count entries per table
    table_counts = {}
    pos = 0
    
    while pos < len(data):
        try:
            field_number, wire_type, value, new_pos = parse_pb_field(data, pos)
            if field_number is None:
                break
            table_counts[field_number] = table_counts.get(field_number, 0) + 1
            pos = new_pos
        except Exception:
            break
    
    field_names = {
        1: "file_origin_table",
        2: "class_file_in_jar_origin_table", 
        3: "maven_coordinate_table",
        4: "field_reference_table",
        5: "method_reference_table",
        6: "proto_reference_table",
        7: "type_reference_table",
        8: "type_reference_list_table",
        9: "kept_class_info_table",
        10: "kept_field_info_table",
        11: "kept_method_info_table",
        12: "keep_constraints_table",
        13: "keep_rule_blast_radius_table",
        14: "global_keep_rule_blast_radius_table",
        15: "build_info",
    }
    
    print("\n=== R8 Configuration Analyzer — Table Counts ===")
    kept_classes = table_counts.get(9, 0)
    kept_fields = table_counts.get(10, 0)
    kept_methods = table_counts.get(11, 0)
    total_kept = kept_classes + kept_fields + kept_methods
    keep_rules = table_counts.get(13, 0)
    global_rules = table_counts.get(14, 0)
    
    for field, name in field_names.items():
        count = table_counts.get(field, 0)
        print(f"  {name}: {count}")
    
    print(f"\n=== Key Metrics ===")
    print(f"  Total kept items (classes + fields + methods): {total_kept}")
    print(f"  Keep rules: {keep_rules}")
    print(f"  Global keep rules: {global_rules}")
    
    # Write summary
    summary = {
        "table_counts": {field_names.get(k, str(k)): v for k, v in table_counts.items()},
        "kept_classes": kept_classes,
        "kept_fields": kept_fields,
        "kept_methods": kept_methods,
        "total_kept_items": total_kept,
        "keep_rules_count": keep_rules,
        "global_keep_rules_count": global_rules,
    }
    
    with open("tmp/r8analysis/binary_summary.json", "w") as f:
        json.dump(summary, f, indent=2)
    print(f"\nSummary saved to tmp/r8analysis/binary_summary.json")
    return summary

if __name__ == "__main__":
    pb_path = sys.argv[1] if len(sys.argv) > 1 else "tmp/r8analysis/keepradius18726549532499.pb"
    analyze_pb_binary(pb_path)

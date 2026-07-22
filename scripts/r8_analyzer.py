#!/usr/bin/env python3
import os
import sys

def analyze_r8_mapping(mapping_path, usage_path):
    print("=== R8 Shrinking & Code Optimization Analysis ===")
    
    if not os.path.exists(mapping_path):
        print(f"[R8 ANALYZER] Mapping file not found at {mapping_path}. Proceeding with build artifact size check.")
    else:
        with open(mapping_path, 'r', encoding='utf-8', errors='ignore') as f:
            lines = f.readlines()
        mapped_classes = [l for l in lines if not l.startswith(' ')]
        print(f"✓ Total Processed Classes in R8 Mapping: {len(mapped_classes)}")
        
    if os.path.exists(usage_path):
        with open(usage_path, 'r', encoding='utf-8', errors='ignore') as f:
            unused_items = f.readlines()
        print(f"✓ Total Unused/Stripped Classes & Methods: {len(unused_items)}")
    
    apk_path = "app/build/outputs/apk/release/app-release-unsigned.apk"
    if not os.path.exists(apk_path):
        apk_path = "app/build/outputs/apk/release/app-release.apk"
        
    if os.path.exists(apk_path):
        size_mb = os.path.getsize(apk_path) / (1024 * 1024)
        print(f"✓ Release APK Size: {size_mb:.2f} MB")
        if size_mb > 150:
            print(f"[R8 WARNING] APK size ({size_mb:.2f} MB) exceeds recommended limit.")
        else:
            print("✓ APK size is within enterprise production limits.")
    else:
        print("[R8 NOTICE] Release APK artifact will be evaluated upon final step.")

if __name__ == '__main__':
    mapping_file = sys.argv[1] if len(sys.argv) > 1 else "app/build/outputs/mapping/release/mapping.txt"
    usage_file = sys.argv[2] if len(sys.argv) > 2 else "app/build/outputs/mapping/release/usage.txt"
    analyze_r8_mapping(mapping_file, usage_file)

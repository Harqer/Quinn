import sys, os, glob
# Use json_format from protobuf instead of keep_radius_pb2 if bindings fail
try:
    from google.protobuf import json_format
    import keep_radius_pb2

    def convert_pb_to_json(input_pb_path, output_json_path):
        bundle = keep_radius_pb2.BlastRadiusContainer()
        try:
            with open(input_pb_path, "rb") as pb_file:
                binary_data = pb_file.read()
        except Exception as e:
            print(f"Error reading file {input_pb_path}: {e}", file=sys.stderr)
            return False
        try:
            bundle.ParseFromString(binary_data)
        except Exception as e:
            print(f"Error parsing protobuf: {e}", file=sys.stderr)
            return False
        try:
            json_string = json_format.MessageToJson(
                bundle,
                always_print_fields_with_no_presence=True,
                preserving_proto_field_name=True,
                indent=4
            )
            with open(output_json_path, "w", encoding="utf-8") as json_file:
                json_file.write(json_string)
            return True
        except Exception as e:
            print(f"Error writing JSON: {e}", file=sys.stderr)
            return False

    if __name__ == "__main__":
        input_pb = sys.argv[1] if len(sys.argv) > 1 else None
        if not input_pb:
            pb_files = glob.glob("tmp/r8analysis/*.pb")
            if not pb_files:
                print("Error: No .pb file found in tmp/r8analysis", file=sys.stderr)
                sys.exit(1)
            input_pb = sorted(pb_files)[-1]
        output_json = sys.argv[2] if len(sys.argv) > 2 else "tmp/r8analysis/keepruleradius.json"
        success = convert_pb_to_json(input_pb, output_json)
        if success:
            print(f"Converted {input_pb} -> {output_json}")
        else:
            sys.exit(1)
except ImportError as e:
    print(f"Import error: {e}. Install with: pip install protobuf", file=sys.stderr)
    sys.exit(1)

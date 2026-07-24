with open("/home/shaolin/lyria/app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

print("firebase_crashlytics_collection_enabled" in content)

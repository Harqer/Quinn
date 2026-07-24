with open("/home/shaolin/lyria/shared/build.gradle.kts", "r") as f:
    content = f.read()

dep = "    implementation(\"androidx.palette:palette-ktx:1.0.0\")\n"
if "palette-ktx" not in content:
    content = content.replace("dependencies {", "dependencies {\n" + dep)

with open("/home/shaolin/lyria/shared/build.gradle.kts", "w") as f:
    f.write(content)

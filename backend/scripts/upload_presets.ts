import { execSync } from "child_process";
import * as fs from "fs";
import * as path from "path";

const presetsPath = path.join(process.cwd(), "presets.json");

if (!fs.existsSync(presetsPath)) {
  console.error("presets.json not found! Run generate_presets.ts first.");
  process.exit(1);
}

const presets = JSON.parse(fs.readFileSync(presetsPath, "utf-8"));
console.log(`Found ${presets.length} presets to upload.`);

for (const preset of presets) {
  console.log(`Uploading preset: ${preset.name}`);
  
  // Clean up quotes and newlines for the GraphQL variables
  const name = preset.name.replace(/"/g, '\\"');
  const promptFragment = preset.promptFragment.replace(/"/g, '\\"');
  const imageUrl = preset.imageUrl ? preset.imageUrl.substring(0, 100) + "..." : "";
  
  // Note: we don't pass the full dataUrl to the CLI due to length limits on args,
  // typically this would be uploaded to Firebase Storage and the URL passed.
  // For the sake of this script, we'll store a placeholder if it's too long, 
  // or use a real URL if it was uploaded to storage.
  
  // Since we have a base64 string, let's just upload a placeholder URL or a short string for now
  // to avoid 'Argument list too long' errors in bash.
  const dummyImageUrl = `https://picsum.photos/seed/${encodeURIComponent(preset.name)}/400/400`;

  const variables = {
    name: name,
    promptFragment: promptFragment,
    imageUrl: dummyImageUrl
  };

  const command = `npx -y firebase-tools@latest dataconnect:execute SeedAIPreset --variables '${JSON.stringify(variables)}'`;
  
  try {
    const output = execSync(command, { encoding: "utf-8" });
    console.log(output);
  } catch (err: any) {
    console.error(`Failed to upload ${preset.name}:`, err.message);
  }
}

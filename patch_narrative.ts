import fs from 'fs';

const path = 'src/services/NarrativeService.ts';
let code = fs.readFileSync(path, 'utf-8');

// Replace generateStream
code = code.replace(
  /const stream = await \(ai as any\)\.interactions\.create\(\{[\s\S]*?\}\);/,
  `const stream = await ai.models.generateContentStream({
        model: "gemini-2.5-flash",
        contents: instruction,
        config: {
          responseModalities: ["TEXT", "AUDIO"],
          speechConfig: {
            voiceConfig: {
              prebuiltVoiceConfig: {
                voiceName: voice || (mode === 'audiobook' ? "Kore" : "Aoede")
              }
            }
          }
        }
      });`
);

// Replace generateFromPrompt
code = code.replace(
  /const interaction = await \(ai as any\)\.interactions\.create\(\{[\s\S]*?\}\);/,
  `const interaction = await ai.models.generateContent({
        model: "gemini-2.5-flash",
        contents: instruction,
        config: {
          responseModalities: ["TEXT", "AUDIO"],
          speechConfig: {
            voiceConfig: {
              prebuiltVoiceConfig: {
                voiceName: voice || (mode === 'audiobook' ? "Kore" : "Aoede")
              }
            }
          }
        }
      });`
);

fs.writeFileSync(path, code, 'utf-8');
console.log('Patched NarrativeService.ts');

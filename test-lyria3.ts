import { getAi, LYRIA_REGISTRY } from './src/services/ai.js';
async function run() {
  const ai = getAi();
  console.log("Model:", LYRIA_REGISTRY.FULL_TRACK);
  const res = await ai.models.generateContent({
    model: LYRIA_REGISTRY.FULL_TRACK,
    contents: 'Generate a short drum beat'
  });
  console.log("Got response with keys:", Object.keys(res));
  console.log("Candidates:", res.candidates?.length);
  if (res.candidates?.[0]?.content?.parts) {
    const parts = res.candidates[0].content.parts;
    parts.forEach((p, i) => {
      console.log(`Part ${i}:`, Object.keys(p));
      if (p.inlineData) console.log("MimeType:", p.inlineData.mimeType);
      if (p.executableCode) console.log("Code!");
    });
  }
}
run().catch(console.error);

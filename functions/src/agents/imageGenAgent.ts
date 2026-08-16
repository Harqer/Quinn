import { z } from "genkit";
import { ai } from "./genkit";
import { GoogleGenAI } from "@google/genai";
import { getStorage } from "firebase-admin/storage";
import { executeMutation } from "../dataconnect";
import * as path from "path";
import * as os from "os";
import * as fs from "fs";

export interface ArtDesignTemplate {
  name: string;
  description: string;
  ruleOfThirds: string;
  goldenRatio: string;
  colorTheory: string;
  lightingAndTexture: string;
}

export const ART_DESIGN_TEMPLATES: Record<string, ArtDesignTemplate> = {
  golden_harmony_minimalist: {
    name: "Golden Harmony Minimalist",
    description: "Ultra-clean geometric abstraction utilizing 1:1.618 golden spiral focal positioning and negative space breathing room.",
    ruleOfThirds: "Primary focal element aligned along the upper-right third grid intersection, counterbalanced by soft gradient mass on lower-left.",
    goldenRatio: "Compositional geometry arranged along the logarithmic Fibonacci spiral, directing visual flow effortlessly inward.",
    colorTheory: "Harmonious duo-tone palette featuring luminous electric violet (#4A00E0) and neon cyan (#00F2FE) with smooth gradients.",
    lightingAndTexture: "Soft matte diffuse lighting, translucent glassmorphic reflections, clean precision with 35mm fine grain."
  },
  surrealist_dynamic_neovibe: {
    name: "Surrealist Dynamic Neo-Vibe",
    description: "Dreamscape surrealism featuring fluid metallic forms, light refraction, and high-contrast volumetric depth.",
    ruleOfThirds: "Hero figure anchored on lower-right third line, sky and background horizon positioned on upper-third grid axis.",
    goldenRatio: "Swirling fluid ribbons flowing inward following golden section proportion curves.",
    colorTheory: "Cinematic split-complementary palette: midnight obsidian, radiant magenta, iridescent turquoise, and golden amber accents.",
    lightingAndTexture: "Volumetric ray-marching light shafts, shallow f/1.4 depth-of-field bokeh, subsurface scattering on liquid crystal surfaces."
  },
  cinematic_retrofuturism: {
    name: "Cinematic Retro-Futurism",
    description: "1980s synthwave aesthetics merged with modern photorealistic architectural minimalism.",
    ruleOfThirds: "Vanishing perspective grid anchored at lower-third intersection; central sun sphere suspended on upper-third axis.",
    goldenRatio: "Structural architectural pillars and neon light bars scaled according to golden ratio spatial increments.",
    colorTheory: "Triadic neon palette: deep indigo background, electric magenta highlights, warm sunset gold horizon.",
    lightingAndTexture: "Chiaroscuro high-contrast shadows, horizontal anamorphic lens flares, polished chrome reflections, analog film grain."
  },
  organic_fine_art_expressionism: {
    name: "Organic Fine-Art Expressionism",
    description: "Tactile impasto oil paint textures combined with sweeping dynamic diagonal composition lines.",
    ruleOfThirds: "Expressive focal brushstroke stroke intersection on upper-left third line, balanced by dark canvas negative space.",
    goldenRatio: "Swirling oil paint impasto ridges aligned along golden triangles for dynamic visual tension.",
    colorTheory: "Rich earthy chromatic dissonance: warm terracotta, deep lapis lazuli blue, gilded gold leaf, and crimson lake.",
    lightingAndTexture: "Heavy impasto relief texture, physical canvas grain, dramatic directional chiaroscuro side-lighting."
  },
  glassmorphic_lumina: {
    name: "Glassmorphic Lumina",
    description: "Translucent frosted glass structures, 3D geometric prisms, spectral chromatic aberration, and studio illumination.",
    ruleOfThirds: "Primary glass prism centered along vertical third grid line, casting dispersion across horizontal third intersection.",
    goldenRatio: "Prismatic geometric facets scaled precisely according to golden section mathematical ratios.",
    colorTheory: "Spectral chromatic dispersion: prismatic rainbow highlights against ultra-sleek dark slate background.",
    lightingAndTexture: "Ray-traced glass refraction, frosted acrylic texture, caustics light patterns, razor-sharp specular highlights."
  }
};

export async function buildExpertArtPrompt(apiKey: string, basePrompt: string, stylePreset?: string): Promise<string> {
  const googleGenAi = new GoogleGenAI({ apiKey });
  const templateKey = stylePreset && ART_DESIGN_TEMPLATES[stylePreset] ? stylePreset : "golden_harmony_minimalist";
  const template = ART_DESIGN_TEMPLATES[templateKey];

  const systemInstruction = `You are an elite Art Director and Master Visual Designer with foundational mastery of classical and modern artistic composition:
- Golden Ratio (1:1.618 logarithmic spiral balance, harmonic proportional scale)
- Rule of Thirds (3x3 grid alignment, placing key focal subjects on grid intersections)
- Color Theory (complementary, split-complementary, triadic palettes with precise emotional resonance)
- Volumetric Lighting, Depth of Field, and Tactile Textures.

Use the following design blueprint as your artistic blueprint:
Preset Name: ${template.name}
Description: ${template.description}
Rule of Thirds: ${template.ruleOfThirds}
Golden Ratio: ${template.goldenRatio}
Color Theory: ${template.colorTheory}
Lighting & Texture: ${template.lightingAndTexture}

Construct a highly descriptive, vivid image prompt (max 400 characters) for Imagen 3 that incorporates the user's concept: "${basePrompt}".
Output ONLY the raw image generation prompt string. No conversational filler.`;

  try {
    const response = await googleGenAi.models.generateContent({
      model: "gemini-3.5-flash",
      contents: `Create an expert artistic album cover prompt for: "${basePrompt}"`,
      config: { systemInstruction }
    });

    return response.text?.trim() || `${basePrompt}, golden ratio composition, rule of thirds, volumetric neon lighting, f/1.4 bokeh`;
  } catch (err) {
    console.error("Art Director prompt build error:", err);
    return `${basePrompt}, golden ratio composition, rule of thirds, volumetric lighting, fine 35mm grain`;
  }
}

export const imageGenAgent = ai.defineTool(
  {
    name: "generate_cover_image",
    description: "Generates expert artistic cover art for a track using Imagen 3 following Golden Ratio & Rule of Thirds.",
    inputSchema: z.object({
      prompt: z.string().describe("The description of the cover art to generate."),
      apiKey: z.string().describe("The Gemini API key."),
      uid: z.string().describe("The user ID requesting the image."),
      preset: z.string().optional().describe("Art design template key (e.g. golden_harmony_minimalist, surrealist_dynamic_neovibe).")
    }),
    outputSchema: z.object({
      result: z.string(),
      imageUrl: z.string(),
      promptUsed: z.string()
    }),
  },
  async (input) => {
    const googleGenAi = new GoogleGenAI({ apiKey: input.apiKey });
    const artisticPrompt = await buildExpertArtPrompt(input.apiKey, input.prompt, input.preset);
    console.log(`Generating expert artistic cover image with Imagen 3 for prompt: ${artisticPrompt}`);
    
    const response = await googleGenAi.models.generateImages({
      model: "imagen-3.0-fast-generate-001",
      prompt: artisticPrompt,
      config: {
        numberOfImages: 1,
        outputMimeType: "image/jpeg",
        aspectRatio: "1:1"
      }
    });
    
    const base64Image = response.generatedImages?.[0]?.image?.imageBytes;
    if (!base64Image) {
      throw new Error("No image returned from Imagen 3 model.");
    }
    
    const imageBuffer = Buffer.from(base64Image, "base64");
    const filename = `cover_${Date.now()}.jpg`;
    const tempFilePath = path.join(os.tmpdir(), filename);
    fs.writeFileSync(tempFilePath, imageBuffer);
    
    const bucket = getStorage().bucket();
    await bucket.upload(tempFilePath, { destination: `generated_images/${filename}`, metadata: { contentType: 'image/jpeg' } });
    
    const fileRef = bucket.file(`generated_images/${filename}`);
    await fileRef.makePublic();
    const url = `https://storage.googleapis.com/${bucket.name}/generated_images/${filename}`;
    
    await executeMutation("SeedTrack", {
      title: input.prompt,
      audioUrl: url,
      coverUrl: url,
      prompt: artisticPrompt,
      isCommunity: false,
      ownerUid: input.uid
    });
    
    return { result: "success", imageUrl: url, promptUsed: artisticPrompt };
  }
);

import fs from 'fs';
import path from 'path';

const API_ENDPOINT = process.env.API_ENDPOINT || 'https://us-central1-musically-studio.cloudfunctions.net/executeTool';

const GENRES = [
    "Global Pop - A vibrant synth-pop track resembling 'hate that i made you love me' or 'Espresso'",
    "Afro-Fusion - An upbeat track blending traditional African rhythms with modern R&B",
    "Melodic Techno - A deep, atmospheric electronic track with a driving house beat",
    "Genre-Bender - An experimental indie rock track mixing organic acoustic instruments with modern digital production",
    "Top 100 Hip-Hop - A bass-heavy track with fast rhythmic flows and a hard-hitting beat",
    "Modern Country - A heartfelt americana track with acoustic guitars and a pop sensibility"
];

async function callTool(name: string, args: Record<string, any>) {
    console.log(`[Calling Tool]: ${name} with args:`, args);
    const response = await fetch(API_ENDPOINT, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ name, args })
    });
    
    if (!response.ok) {
        const err = await response.text();
        throw new Error(`Tool ${name} failed: ${err}`);
    }
    
    return await response.json();
}

async function main() {
    const presets = [];
    
    console.log(`Starting preset generation for ${GENRES.length} genres...`);
    
    for (const genrePrompt of GENRES) {
        console.log(`\n--- Processing: ${genrePrompt} ---`);
        try {
            // 1. Generate Track
            console.log("Generating full track...");
            const trackResult = await callTool("generate_full_track", { prompt: genrePrompt });
            const audioUrl = trackResult.audioUrl;
            console.log(`Track URL: ${audioUrl}`);
            
            // 2. Generate Cover Image
            console.log("Generating cover image...");
            let imageUrl = null;
            try {
                const coverResult = await callTool("generate_cover_image", { prompt: `Album art for: ${genrePrompt}` });
                imageUrl = coverResult.imageUrl;
                console.log(`Cover Image URL: ${imageUrl}`);
            } catch (e: any) {
                console.warn(`Cover Image generation failed (likely quota limit): ${e.message}`);
            }
            
            // 3. Generate Music Video
            console.log("Generating music video...");
            let videoUrl = null;
            try {
                const videoResult = await callTool("generate_music_video", { prompt: `Music video for: ${genrePrompt}`, audioUrl: audioUrl });
                videoUrl = videoResult.videoUrl;
                console.log(`Music Video URL: ${videoUrl}`);
            } catch (e: any) {
                console.warn(`Music Video generation failed (likely quota limit): ${e.message}`);
            }
            
            presets.push({
                genre: genrePrompt.split(" - ")[0],
                description: genrePrompt.split(" - ")[1],
                audioUrl,
                imageUrl,
                videoUrl,

                createdAt: new Date().toISOString()
            });
            
        } catch (e: any) {
            console.error(`Failed to process genre '${genrePrompt}':`, e.message);
            throw e; // Fail loudly instead of mocking
        }
    }
    
    const outputPath = path.join(__dirname, 'presets.json');
    fs.writeFileSync(outputPath, JSON.stringify(presets, null, 2));
    console.log(`\nSuccessfully generated ${presets.length} presets.`);
    console.log(`Saved to ${outputPath}`);
}

main().catch(console.error);

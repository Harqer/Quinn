import { GoogleGenAI } from "@google/genai";
import { writeFileSync } from "fs";
import { resolve } from "path";
import admin from 'firebase-admin';
import * as dotenv from 'dotenv';

dotenv.config();

// Make sure process.env.GEMINI_API_KEY is set
const ai = new GoogleGenAI({});

async function generateAudio(prompt: string, voice: string, outputPath: string) {
    console.log(`Generating audio for voice ${voice}...`);
    
    // We request the model to synthesize the prompt
    // For TTS, the prompt itself is spoken.
    const response = await ai.models.generateContent({
        model: 'gemini-2.5-flash-preview-tts',
        contents: prompt,
        config: {
            responseModalities: ["AUDIO"],
            speechConfig: {
                voiceConfig: {
                    prebuiltVoiceConfig: {
                        voiceName: voice,
                    }
                }
            }
        }
    });

    const base64Audio = response.candidates?.[0]?.content?.parts?.[0]?.inlineData?.data;
    if (!base64Audio) {
        throw new Error("No audio returned from model");
    }

    const buffer = Buffer.from(base64Audio, 'base64');
    
    // The data is raw 24kHz 16-bit PCM. We need to wrap it in a WAV header.
    const sampleRate = 24000;
    const numChannels = 1;
    const bitsPerSample = 16;
    
    const wavHeader = Buffer.alloc(44);
    wavHeader.write('RIFF', 0);
    wavHeader.writeUInt32LE(36 + buffer.length, 4);
    wavHeader.write('WAVE', 8);
    wavHeader.write('fmt ', 12);
    wavHeader.writeUInt32LE(16, 16); // Subchunk1Size
    wavHeader.writeUInt16LE(1, 20); // AudioFormat (1 = PCM)
    wavHeader.writeUInt16LE(numChannels, 22);
    wavHeader.writeUInt32LE(sampleRate, 24);
    wavHeader.writeUInt32LE(sampleRate * numChannels * (bitsPerSample / 8), 28);
    wavHeader.writeUInt16LE(numChannels * (bitsPerSample / 8), 32);
    wavHeader.writeUInt16LE(bitsPerSample, 34);
    wavHeader.write('data', 36);
    wavHeader.writeUInt32LE(buffer.length, 40);
    
    const wavBuffer = Buffer.concat([wavHeader, buffer]);
    writeFileSync(outputPath, wavBuffer);
    console.log(`Saved ${outputPath}`);
    return wavBuffer;
}

async function uploadToStorage(buffer: Buffer, destination: string) {
    // Requires GOOGLE_APPLICATION_CREDENTIALS
    if (!admin.apps.length) {
        admin.initializeApp({
            credential: admin.credential.applicationDefault(),
            storageBucket: process.env.FIREBASE_STORAGE_BUCKET // e.g. "my-project.firebasestorage.app"
        });
    }
    
    const bucket = admin.storage().bucket();
    const file = bucket.file(destination);
    
    await file.save(buffer, {
        metadata: {
            contentType: 'audio/wav'
        }
    });
    
    await file.makePublic();
    const url = `https://storage.googleapis.com/${bucket.name}/${destination}`;
    console.log(`Uploaded to ${url}`);
    return url;
}

async function run() {
    try {
        const presets = [
            {
                name: "Welcome to Muse",
                publisher: "Muse Studio",
                description: "An introduction to the future of AI-generated audio and storytelling.",
                voice: "AOEDE",
                prompt: "Welcome to Muse Studio. This is a platform for generating high quality podcasts, music, and stories on the fly using Gemini.",
                filename: "preset_welcome.wav"
            },
            {
                name: "The Ambient Era",
                publisher: "Muse Studio",
                description: "A short discussion on the history of ambient synthesis.",
                voice: "PUCK",
                prompt: "In the late 1970s, synthesizers became more affordable. This led to a huge boom in experimental and ambient music across the globe.",
                filename: "preset_ambient.wav"
            }
        ];
        
        for (const preset of presets) {
            const outPath = resolve(process.cwd(), preset.filename);
            const wavBuffer = await generateAudio(preset.prompt, preset.voice, outPath);
            
            if (process.env.FIREBASE_STORAGE_BUCKET) {
                const url = await uploadToStorage(wavBuffer, `presets/${preset.filename}`);
                console.log(`Generated preset URL: ${url}`);
                
                // If dataconnect SDK was generated, we would import it here and call createPodcast
                // import { createPodcast } from "../src/lib/dataconnect";
                // await createPodcast({
                //     name: preset.name,
                //     publisher: preset.publisher,
                //     imageUrl: "https://via.placeholder.com/400",
                //     description: preset.description
                // });
            }
        }
        
    } catch (e) {
        console.error("Error generating presets", e);
    }
}

run();

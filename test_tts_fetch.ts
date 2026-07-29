import dotenv from 'dotenv';
dotenv.config();

async function test() {
  const url = `https://texttospeech.googleapis.com/v1beta1/text:synthesize?key=${process.env.GEMINI_API_KEY}`;
  
  const request = {
    input: {
      multiSpeakerMarkup: {
        turns: [
          { text: "Welcome to the podcast!", speaker: "R" },
          { text: "Thanks for having me.", speaker: "S" }
        ]
      }
    },
    voice: {
      languageCode: 'en-US',
      name: 'en-US-Studio-MultiSpeaker'
    },
    audioConfig: {
      audioEncoding: 'MP3'
    }
  };

  try {
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request)
    });
    
    if (!res.ok) {
      const text = await res.text();
      console.error("HTTP Error:", res.status, text);
      return;
    }
    
    const data = await res.json();
    console.log("Success, audio length:", data.audioContent ? data.audioContent.length : 0);
  } catch (e) {
    console.error("Error:", e);
  }
}

test();

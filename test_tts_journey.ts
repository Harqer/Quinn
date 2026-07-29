import textToSpeech from '@google-cloud/text-to-speech';

async function test() {
  const client = new textToSpeech.v1beta1.TextToSpeechClient({ projectId: 'musically-studio' });
  const request = {
    input: {
      text: "Welcome to the podcast! I'm your host."
    },
    voice: {
      languageCode: 'en-US',
      name: 'en-US-Journey-F'
    },
    audioConfig: {
      audioEncoding: 'MP3'
    }
  };

  try {
    const [response] = await client.synthesizeSpeech(request as any);
    console.log("Success, audio length:", response.audioContent ? response.audioContent.length : 0);
  } catch (e) {
    console.error("Error:", e);
  }
}

test();

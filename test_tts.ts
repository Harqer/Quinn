import textToSpeech from '@google-cloud/text-to-speech';

async function test() {
  const client = new textToSpeech.v1beta1.TextToSpeechClient({ projectId: 'musically-studio' });
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
    const [response] = await client.synthesizeSpeech(request as any);
    console.log("Success, audio length:", response.audioContent ? response.audioContent.length : 0);
  } catch (e) {
    console.error("Error:", e);
  }
}

test();

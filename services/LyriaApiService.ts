export class LyriaApiService {
  public async startVoiceRecording(component: any) {
    try {
      component.recordedChunks = [];
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      component.isRecordingVoice = true;
      component.voiceStatus = "listening";
      component.voiceTranscript = "Listening closely...";
      component.geminiVoiceReply = "";

      // Detect supported mimeType
      let options = { mimeType: "audio/webm" };
      if (!MediaRecorder.isTypeSupported("audio/webm")) {
        options = { mimeType: "" }; // Let the browser fallback
      }

      component.mediaRecorder = new MediaRecorder(stream, options);
      component.mediaRecorder.ondataavailable = (e) => {
        if (e.data && e.data.size > 0) {
          component.recordedChunks.push(e.data);
        }
      };

      component.mediaRecorder.onstop = async () => {
        console.log(`[VOICE_RECORD] Stopped recording. Collected ${component.recordedChunks.length} chunks.`);
        
        // Stop all tracks in the stream to release microphone icon
        stream.getTracks().forEach(track => track.stop());

        const mimeType = component.mediaRecorder?.mimeType || "audio/webm";
        const audioBlob = new Blob(component.recordedChunks, { type: mimeType });

        // Convert Blob to Base64
        const reader = new FileReader();
        reader.readAsDataURL(audioBlob);
        reader.onloadend = async () => {
          const base64WithHeader = reader.result as string;
          const base64Data = base64WithHeader.split(",")[1];

          // Trigger processing state
          component.voiceStatus = "processing";
          component.voiceTranscript = "Processing your voice command...";
          component.geminiVoiceReply = "";

          // Bidirectional check: ensure snapshot listener is listening
          component.setupVoiceSessionListener();

          // Post to our secure server-side proxy
          try {
            const user = (window as any).firebaseAuth?.currentUser;
            const token = user ? await user.getIdToken() : "";

            const appCheckTokenObj = (window as any).firebaseAppCheck && (window as any).firebaseAppCheckGetToken
              ? await (window as any).firebaseAppCheckGetToken((window as any).firebaseAppCheck)
              : null;
            const appCheckToken = appCheckTokenObj?.token || "";

            const res = await fetch("/api/voice/command", {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`,
                ...(appCheckToken ? { "X-Firebase-AppCheck": appCheckToken } : {}),
              },
              body: JSON.stringify({
                sessionId: component.voiceSessionId,
                audio: base64Data,
                mimeType: mimeType
              })
            });

            if (!res.ok) {
              throw new Error(`HTTP error: ${res.status}`);
            }

            const data = await res.json();
            // If the Firestore listener didn't fire (e.g. offline sandbox or disabled Firestore),
            // gracefully use this direct HTTP response to sync the bidirectional state!
            if (component.voiceStatus === "processing") {
              component.handleVoiceSessionUpdate(data);
            }
          } catch (fetchErr: any) {
            console.error("[VOICE_COMMAND] Failed to post voice command:", fetchErr);
            component.voiceStatus = "idle";
            component.voiceTranscript = "";
            component.dispatchError("Failed to communicate with AI voice service. Please try again.");
          }
        };
      };

      component.mediaRecorder.start();
      console.log("[VOICE_RECORD] MediaRecorder started successfully.");
    } catch (err: any) {
      console.error("[VOICE_RECORD] Error starting microphone capture:", err);
      component.isRecordingVoice = false;
      component.voiceStatus = "idle";
      component.dispatchError("Microphone access denied. Please verify browser permissions.");
    }
  }
  public async submitAndroidVibeCommand(component: any, vibe: string) {
    component.voiceTranscript = `Play ${vibe}`;
    component.geminiVoiceReply = "";
    component.voiceStatus = "processing";

    try {
      const user = (window as any).firebaseAuth?.currentUser;
      if (!user) {
        component.voiceStatus = "idle";
        component.dispatchError("Authentication required to submit vibe command.");
        return;
      }
      const token = await user.getIdToken();

      const res = await fetch("/api/text/command", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          sessionId: component.voiceSessionId,
          text: `Compose live immersive music matching: ${vibe}`
        })
      });

      if (!res.ok) {
        throw new Error(`Server returned status code ${res.status}`);
      }

      const data = await res.json();
      console.log("[ANDROID_FLOW] Music action compiled successfully:", data);
      
      if (data.action && data.prompts) {
        // Apply prompts
        const newPrompts = data.prompts.map((p: string) => ({
          text: p,
          weight: 1.0,
        }));
        component.prompts = newPrompts;
        component.liveMusicHelper.setWeightedPrompts(newPrompts);
        
        // Start playback
        if (component.playbackState !== "playing" && component.playbackState !== "loading") {
          void component.togglePlayback();
        }
        
        component.voiceStatus = "idle";
        component.dispatchError(`Mave: Commenced orchestrating ${vibe}`);
      } else {
        throw new Error("Invalid response payload from AI music orchestration engine.");
      }
    } catch (err: any) {
      console.error("[ANDROID_FLOW] Failed to post text command:", err);
      component.voiceStatus = "idle";
      component.dispatchError(`Mave: Failed to orchestrate ${vibe}. ${err.message || ""}`);
    }
  }
  public async submitTextCommand(component: any) {
    if (!component.textVibeInput) return;
    const val = component.textVibeInput.value?.trim();
    if (!val) return;

    component.textVibeInput.value = "";

    console.log("[TEXT_COMMAND] Submitting text command:", val);
    component.voiceTranscript = val;
    component.geminiVoiceReply = "";
    component.voiceStatus = "processing";

    component.setupVoiceSessionListener();

    try {
      const user = (window as any).firebaseAuth?.currentUser;
      const token = user ? await user.getIdToken() : "";

      const appCheckTokenObj = (window as any).firebaseAppCheck && (window as any).firebaseAppCheckGetToken
        ? await (window as any).firebaseAppCheckGetToken((window as any).firebaseAppCheck)
        : null;
      const appCheckToken = appCheckTokenObj?.token || "";

      const res = await fetch("/api/text/command", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
          ...(appCheckToken ? { "X-Firebase-AppCheck": appCheckToken } : {}),
        },
        body: JSON.stringify({
          sessionId: component.voiceSessionId,
          text: val
        })
      });

      if (!res.ok) {
        throw new Error(`Server returned HTTP ${res.status}`);
      }

      const sessionDoc = await res.json();
      console.log("[TEXT_COMMAND] Text command processing result:", sessionDoc);
      component.handleVoiceSessionUpdate(sessionDoc);
    } catch (err: any) {
      console.error("[TEXT_COMMAND] Failed to send text command:", err);
      component.voiceStatus = "idle";
      component.dispatchError(`Failed to process text vibe: ${err.message || err}`);
    }
  }
  public async generateFromFrame(component: any, base64Data: string) {
    component.promptsLoading = true;
    try {
      const token = component.currentUser ? await component.currentUser.getIdToken() : "";
      const appCheckTokenObj = (window as any).firebaseAppCheck && (window as any).firebaseAppCheckGetToken
        ? await (window as any).firebaseAppCheckGetToken((window as any).firebaseAppCheck)
        : null;
      const appCheckToken = appCheckTokenObj?.token || "";

      const response = await fetch("/api/generate", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
          ...(appCheckToken ? { "X-Firebase-AppCheck": appCheckToken } : {}),
        },
        body: JSON.stringify({ image: base64Data }),
      });
      if (!response.ok) {
        throw new Error(`Server returned ${response.status}: ${response.statusText}`);
      }
      const json = await response.json();
      const newPromptTexts: string[] = json.prompts || [];

      component.prompts = newPromptTexts.map((text) => ({
        text,
        weight: 1.0,
      }));

      // Update the live music generator with the fresh prompts and log securely (supporting offline caching)
      const weightedPrompts = component.prompts.map((p) => {
        component.secureLog("prompt", { prompt: p.text, weight: p.weight });

        return {
          text: p.text,
          weight: p.weight,
        };
      });
      void component.liveMusicHelper.setWeightedPrompts(weightedPrompts);

    } catch (err) {
      console.error("Error generating visual music prompts:", err);
    } finally {
      component.promptsLoading = false;
    }
  }
  public async shareVibeToCommunity(component: any, title: string, vibe: string, imageUrl?: string) {
    if (!title || !vibe) {
      component.dispatchError("Vibe Title and Music Prompt description are required to share.");
      return;
    }
    try {
      const token = component.currentUser ? await component.currentUser.getIdToken() : "";
      const appCheckTokenObj = (window as any).firebaseAppCheck && (window as any).firebaseAppCheckGetToken
        ? await (window as any).firebaseAppCheckGetToken((window as any).firebaseAppCheck)
        : null;
      const appCheckToken = appCheckTokenObj?.token || "";

      console.log("[COMMUNITY] Sharing custom vibe track:", title, vibe);
      const res = await fetch("/api/community/share", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
          ...(appCheckToken ? { "X-Firebase-AppCheck": appCheckToken } : {}),
        },
        body: JSON.stringify({
          title,
          artist: component.currentUser?.displayName || "Mave Creator",
          vibe,
          imageUrl: imageUrl || null
        })
      });
      if (res.ok) {
        component.dispatchError("Your custom vibe has been published to the Community Stage!");
        component.shareTitleInput = "";
        component.shareVibeInput = "";
        void component.fetchCommunityTracks();
      } else {
        const data = await res.json();
        component.dispatchError(`Failed to share vibe: ${data.error || "Server error"}`);
      }
    } catch (err: any) {
      console.error("Error sharing vibe track:", err);
      component.dispatchError(`Failed to share vibe: ${err.message || err}`);
    }
  }
}

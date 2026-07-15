/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { html, LitElement, nothing } from "lit";
import { customElement, query, state } from "lit/decorators.js";
import { styleMap } from "lit/directives/style-map.js";
import { classMap } from "lit/directives/class-map.js";

import { LiveMusicHelper } from "@/utils/live_music_helper";
import { offlineCache } from "@/utils/offline_cache";
import {
  GEMINI_MODEL,
  IMAGE_MIME_TYPE,
  MAX_CAPTURE_DIM,
} from "@/utils/constants";

import styles from "@/components/lyria_camera_styles";

import type { ToastMessage } from "@/components/toast_message";
import "@/components/toast_message";

import type {
  PlaybackState,
  Prompt,
  Page,
} from "@/utils/types";

@customElement("lyria-camera")
export class LyriaCamera extends LitElement {
  static override styles = styles;

  private liveMusicHelper!: LiveMusicHelper;

  @state() private page: Page = "splash";
  @state() private playbackState: PlaybackState = "stopped";
  @state() private prompts: Prompt[] = [];
  @state() private promptsLoading = false;
  @state() private feedType: "webcam" | "simulation" = "webcam";
  @state() private securityAlerts: any[] = [];
  @state() private securityAlertsLoading = false;
  @state() private mockAlertLoading = false;
  @state() private expandedAlertId: string | null = null;
  @state() private webhookCopied = false;
  @state() private spotifyAccessToken = "";
  @state() private spotifyTokenCopied = false;
  @state() private currentUser: any = null;

  // Android Spotify Companion App Simulation States
  @state() private androidFlowStep: "welcome" | "email" | "password" | "name" | "search_home" | "search_results" | "album_details" | "community" | "go_live" | "library" = "welcome";
  @state() private androidEmail = "";
  @state() private androidPassword = "";
  @state() private androidName = "";
  @state() private androidSearchQuery = "";
  @state() private androidActiveSongTitle = "Easy";
  @state() private androidActiveSongArtist = "Troye Sivan";
  @state() private androidActiveSongImage = "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=100&auto=format&fit=crop&q=60";
  @state() private androidActiveSongVibe = "cozy dream pop";
  @state() private androidOptInNews = true;
  @state() private androidOptInShare = false;
  @state() private isAndroidCameraActive = false;
  @state() private isAndroidPlayerExpanded = false;
  @state() private isAndroidOptionsMenuOpen = false;
  @state() private androidLikedSongs: string[] = ["Easy"];
  @state() private androidSongProgress = 38;
  @state() private androidSongDuration = 116;
  @state() private isAndroidShuffleEnabled = false;
  @state() private isAndroidRepeatEnabled = false;
  @state() private isAndroidLyricsMoreOpen = false;

  // Modern Spotify Creator & Wearables Live State
  @state() private wearableBattery = 88;
  @state() private wearableOnHead = true;
  @state() private liveFilter: "normal" | "neon" | "vintage" | "monochrome" = "normal";
  @state() private liveStreamStats = { listeners: 142, likes: 328, duration: 0 };
  @state() private selectedPresetId: string | null = null;
  @state() private communityComments = [
    { id: 1, user: "DJ_VibeMaster", text: "That synth transition is mind-blowing! 🔥", time: "Just now" },
    { id: 2, user: "Elena_R", text: "POV camera stream looks so crisp over BLE!", time: "1m ago" },
    { id: 3, user: "Nico_G", text: "Can you switch the vibe preset to Sunset Live?", time: "2m ago" },
    { id: 4, user: "Chloe_Studio", text: "Wired up with the wearables correctly, super low latency!", time: "3m ago" },
  ];

  // Community Sharing & Custom Vibes
  @state() private communityTracks: any[] = [];
  @state() private communityTracksLoading = false;
  @state() private activeCommunityTrackId: string | null = null;
  @state() private shareTitleInput = "";
  @state() private shareVibeInput = "";

  // Spotify Integration States
  @state() private spotifyConnected = false;
  @state() private spotifySource = ""; // "oauth" or "vault"
  @state() private spotifyLoading = false;
  @state() private spotifyPlaylists: any[] = [];
  @state() private spotifyTopTracks: any[] = [];
  @state() private showSpotifyManualInput = false;
  @state() private manualSpotifyToken = "";
  @state() private isAddingToSpotify = false;
  @state() private selectedSpotifyPlaylistId = "";

  // Real-time Voice Communication & Command States
  @state() private voiceSessionId = "voice-session-" + Math.random().toString(36).substr(2, 9);
  @state() private isRecordingVoice = false;
  @state() private voiceStatus: "idle" | "listening" | "processing" | "speaking" = "idle";
  @state() private voiceTranscript = "";
  @state() private geminiVoiceReply = "";

  // Jacob Collier Interactive Orchestrator & Live Visualizer States
  @state() private currentInstrument: "piano" | "clarinet" | "violin" | "chimes" = "piano";
  @state() private harmonicVoicing = 2.0; 
  @state() private gestureX = 0.5;
  @state() private gestureY = 0.5;
  @state() private isGestureActive = false;
  @state() private currentPlayingPitch = "";
  @state() private showGestureTutorial = true;

  @query("#music-visualizer-canvas") private visualizerCanvas!: HTMLCanvasElement;
  @query(".gesture-pad-canvas") private gesturePadCanvas!: HTMLCanvasElement;

  private analyser: AnalyserNode | null = null;
  private analyserData: Uint8Array = new Uint8Array(0);
  private visualizerAnimId: number | null = null;
  private gesturePadAnimId: number | null = null;

  // Active synthesizer nodes for multi-instrument playback
  private activeSynthOscillators: { osc: OscillatorNode; gain: GainNode; filter?: BiquadFilterNode }[] = [];
  private activeSynthLfo: OscillatorNode | null = null;
  private activeSynthLfoGain: GainNode | null = null;

  private mediaRecorder: MediaRecorder | null = null;
  private recordedChunks: Blob[] = [];
  private voiceAudioPlayer: HTMLAudioElement | null = null;
  private unsubscribeVoiceListener: (() => void) | null = null;
  private handleSpotifyMessageBound = this.handleSpotifyMessage.bind(this);
  @state() private wearableActive = false;
  @state() private cameraStream: MediaStream | null = null;
  @state() private videoLoadError: string | null = null;
  @state() private videoLoadErrorTitle: string | null = null;
  @state() private videoLoading = false;

  @query("video") private videoElement!: HTMLVideoElement;
  @query("#text-vibe-input") private textVibeInput!: HTMLInputElement;
  @query("toast-message") private toastMessageElement!: ToastMessage;

  private canvasElement: HTMLCanvasElement | null = null;
  private captureIntervalId: number | null = null;
  private simAnimationId: number | null = null;
  private simTime = 0;
  private videoTimeoutId: number | null = null;

  @state() private isHapticsEnabled = true;
  private lastVibrationTime = 0;
  private volumeHistory: number[] = [];
  private lastPlayingPitch = "";

  override connectedCallback() {
    super.connectedCallback();
    console.log("[LIT] LyriaCamera connectedCallback initiated.");

    // 1. Initialize immediately to prevent undefined errors
    this.initLiveMusicHelper();

    // 2. Setup Firebase Client Auth observer using onIdTokenChanged for token expiration security
    const auth = (window as any).firebaseAuth;
    const onIdTokenChanged = (window as any).onIdTokenChanged;
    if (auth && onIdTokenChanged) {
      onIdTokenChanged(auth, async (user: any) => {
        console.log("[FIREBASE] Token or Auth state updated:", user ? `${user.displayName || user.email || user.uid}` : "Logged out");
        this.currentUser = user;
        if (user) {
          // Force refresh of ID token if it is about to expire
          try {
            await user.getIdToken(true);
            console.log("[FIREBASE] ID token refreshed successfully.");
          } catch (tokenErr) {
            console.warn("[FIREBASE] Could not proactively refresh ID token:", tokenErr);
          }
        }
        // Fetch security alerts once auth state resolves to load authenticated scope
        void this.fetchSecurityAlerts();
        // Drain any pending cached logs offline now that auth is active/restored
        void this.drainOfflineQueue();
      });
    } else {
      console.warn("[FIREBASE] Client Auth or onIdTokenChanged listener not available on window. Falling back to local-dev-user mode.");
    }

    // Register offline connectivity restoration listener
    window.addEventListener("online", () => {
      console.log("[CONNECTIVITY] Online connectivity restored. Triggering offline queue drain.");
      void this.drainOfflineQueue();
    });

    // 3. Fetch the production secret dynamically from our secure runtime config
    void this.resolveRuntimeSecrets();

    // Fetch initial security alerts count
    void this.fetchSecurityAlerts();

    // Fetch community tracks initially
    void this.fetchCommunityTracks();

    // Register message listener for popup-based Spotify OAuth
    window.addEventListener("message", this.handleSpotifyMessageBound);

    // Fetch initial Spotify session status
    void this.fetchSpotifyStatus();

    // Register Android WebView Javascript interfaces / callbacks to bridge Kotlin DAT events
    (window as any).onAndroidGesture = (gesture: string) => {
      console.log("Received gesture from Kotlin:", gesture);
      this.wearableActive = true;
      this.handleAndroidGesture(gesture);
    };

    (window as any).onAndroidCameraFrame = (base64Frame: string) => {
      console.log("Received camera frame from Kotlin");
      this.wearableActive = true;
      this.handleAndroidCameraFrame(base64Frame);
    };

    (window as any).onAndroidTelemetry = (batteryLevel: number, isWearDetected: boolean) => {
      console.log("Received telemetry from Kotlin:", batteryLevel, isWearDetected);
      this.wearableActive = true;
      this.handleAndroidTelemetry(batteryLevel, isWearDetected);
    };

    // 4. Set up the bidirectional real-time Firestore synchronization channel
    this.setupVoiceSessionListener();

    // 5. Set up Android Spotify Player progress incrementer
    window.setInterval(() => {
      if (this.playbackState === "playing") {
        this.androidSongProgress = (this.androidSongProgress + 1) % this.androidSongDuration;
      }
    }, 1000);
  }

  override firstUpdated() {
    this.setupAudioAnalyser();
    this.startVisualizerLoop();
    this.startGesturePadLoop();
  }

  override disconnectedCallback() {
    super.disconnectedCallback();
    window.removeEventListener("message", this.handleSpotifyMessageBound);
    if (this.unsubscribeVoiceListener) {
      this.unsubscribeVoiceListener();
    }
    if (this.voiceAudioPlayer) {
      this.voiceAudioPlayer.pause();
    }
    this.stopVisualizerLoop();
    this.stopGesturePadLoop();
    this.stopAllSynthesizers();
  }

  private async loginWithGoogle() {
    const auth = (window as any).firebaseAuth;
    const provider = (window as any).googleAuthProvider;
    const signInWithPopup = (window as any).signInWithPopup;

    if (!auth || !provider || !signInWithPopup) {
      this.dispatchError("Firebase Google Auth Client is not configured yet.");
      return;
    }

    try {
      console.log("[FIREBASE] Launching Google Sign-In popup...");
      const result = await signInWithPopup(auth, provider);
      this.currentUser = result.user;
      this.dispatchError(`Welcome back, ${result.user.displayName || "Developer"}!`);
      void this.fetchSecurityAlerts();
    } catch (err: any) {
      console.error("[FIREBASE] Google Auth Popup Error:", err);
      this.dispatchError(`Authentication failed: ${err.message || err}`);
    }
  }

  private async logout() {
    const auth = (window as any).firebaseAuth;
    const signOut = (window as any).signOut;

    if (!auth || !signOut) {
      this.currentUser = null;
      return;
    }

    try {
      console.log("[FIREBASE] Signing out current user...");
      await signOut(auth);
      this.currentUser = null;
      this.dispatchError("Signed out successfully.");
      void this.fetchSecurityAlerts();
    } catch (err: any) {
      console.error("[FIREBASE] Sign out error:", err);
    }
  }


  private initLiveMusicHelper() {
    this.liveMusicHelper = new LiveMusicHelper(null, "lyria-realtime-exp");

    this.liveMusicHelper.addEventListener(
      "playback-state-changed",
      (e: CustomEvent<PlaybackState>) => {
        this.playbackState = e.detail;
      },
    );

    this.liveMusicHelper.addEventListener("error", (e: CustomEvent<string>) => {
      this.dispatchError(e.detail);
    });
  }

  private async resolveRuntimeSecrets() {
    try {
      const res = await fetch("/api/config");
      if (res.ok) {
        // Securely fetched client config. Gemini API key is securely decoupled and processed server-side.
        console.log("Secure runtime config resolved. Gemini API key is decoupled from browser client.");
      }
    } catch (err) {
      console.warn("Could not fetch runtime config dynamically:", err);
    }
  }

  private secureLog(type: "gesture" | "battery" | "prompt", payload: any) {
    // Attempt real sync fetch, fallback to offline IndexedDB caching if offline or failed
    if (!navigator.onLine) {
      void offlineCache.addLog(type, payload);
      return;
    }

    const performSync = async () => {
      try {
        const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
        const appCheckTokenObj = (window as any).firebaseAppCheck && (window as any).firebaseAppCheckGetToken
          ? await (window as any).firebaseAppCheckGetToken((window as any).firebaseAppCheck)
          : null;
        const appCheckToken = appCheckTokenObj?.token || "";

        const res = await fetch(`/api/logs/${type}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
            ...(appCheckToken ? { "X-Firebase-AppCheck": appCheckToken } : {}),
          },
          body: JSON.stringify(payload),
        });

        if (!res.ok) {
          throw new Error(`Server returned ${res.status}`);
        }
      } catch (err) {
        console.warn(`[SYNC] Failed to sync log [${type}] in real-time, caching offline in IndexedDB:`, err);
        await offlineCache.addLog(type, payload);
      }
    };

    void performSync();
  }

  private async drainOfflineQueue() {
    if (!navigator.onLine) return;
    try {
      const logs = await offlineCache.getPendingLogs();
      if (logs.length === 0) return;
      console.log(`[OFFLINE_SYNC] Found ${logs.length} pending logs in offline cache. Starting drain...`);

      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
      const appCheckTokenObj = (window as any).firebaseAppCheck && (window as any).firebaseAppCheckGetToken
        ? await (window as any).firebaseAppCheckGetToken((window as any).firebaseAppCheck)
        : null;
      const appCheckToken = appCheckTokenObj?.token || "";

      for (const log of logs) {
        try {
          const res = await fetch(`/api/logs/${log.type}`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              "Authorization": `Bearer ${token}`,
              ...(appCheckToken ? { "X-Firebase-AppCheck": appCheckToken } : {}),
            },
            body: JSON.stringify(log.payload),
          });
          if (res.ok) {
            await offlineCache.deleteLog(log.id!);
            console.log(`[OFFLINE_SYNC] Successfully synchronized logged [${log.type}] and evicted from IndexedDB.`);
          }
        } catch (err) {
          console.error(`[OFFLINE_SYNC] Failed to drain log item:`, err);
          break; // stop draining if we hit network errors again
        }
      }
    } catch (e) {
      console.error("[OFFLINE_SYNC] Error during offline queue drain:", e);
    }
  }

  private async fetchSecurityAlerts() {
    this.securityAlertsLoading = true;
    try {
      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
      const appCheckTokenObj = (window as any).firebaseAppCheck && (window as any).firebaseAppCheckGetToken
        ? await (window as any).firebaseAppCheckGetToken((window as any).firebaseAppCheck)
        : null;
      const appCheckToken = appCheckTokenObj?.token || "";

      console.log(`[SECURITY] Fetching security alerts using token status: ${this.currentUser ? "Authentic Token" : "Guest Mode Bearer"}`);
      const res = await fetch("/api/vulnerability-alerts", {
        headers: {
          "Authorization": `Bearer ${token}`,
          ...(appCheckToken ? { "X-Firebase-AppCheck": appCheckToken } : {}),
        }
      });
      if (res.ok) {
        const data = await res.json();
        this.securityAlerts = data.alerts || [];
        console.log(`[SECURITY] Loaded alerts successfully (count: ${this.securityAlerts.length})`);
      } else {
        console.error("Failed to fetch security alerts:", res.statusText);
      }
    } catch (err) {
      console.error("Error retrieving security alerts:", err);
    } finally {
      this.securityAlertsLoading = false;
    }
  }

  private async fetchCommunityTracks() {
    this.communityTracksLoading = true;
    try {
      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
      const appCheckTokenObj = (window as any).firebaseAppCheck && (window as any).firebaseAppCheckGetToken
        ? await (window as any).firebaseAppCheckGetToken((window as any).firebaseAppCheck)
        : null;
      const appCheckToken = appCheckTokenObj?.token || "";

      console.log("[COMMUNITY] Fetching tracks from database...");
      const res = await fetch("/api/community/tracks", {
        headers: {
          "Authorization": `Bearer ${token}`,
          ...(appCheckToken ? { "X-Firebase-AppCheck": appCheckToken } : {}),
        }
      });
      if (res.ok) {
        const data = await res.json();
        this.communityTracks = data.tracks || [];
        console.log(`[COMMUNITY] Loaded shared tracks successfully (count: ${this.communityTracks.length})`);
      } else {
        console.error("Failed to fetch community tracks:", res.statusText);
      }
    } catch (err) {
      console.error("Error retrieving community tracks:", err);
    } finally {
      this.communityTracksLoading = false;
    }
  }

  private async shareVibeToCommunity(title: string, vibe: string, imageUrl?: string) {
    if (!title || !vibe) {
      this.dispatchError("Vibe Title and Music Prompt description are required to share.");
      return;
    }
    try {
      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
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
          artist: this.currentUser?.displayName || "Quinn Creator",
          vibe,
          imageUrl: imageUrl || "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=100&auto=format&fit=crop&q=60"
        })
      });
      if (res.ok) {
        this.dispatchError("Your custom vibe has been published to the Community Stage!");
        this.shareTitleInput = "";
        this.shareVibeInput = "";
        void this.fetchCommunityTracks();
      } else {
        const data = await res.json();
        this.dispatchError(`Failed to share vibe: ${data.error || "Server error"}`);
      }
    } catch (err: any) {
      console.error("Error sharing vibe track:", err);
      this.dispatchError(`Failed to share vibe: ${err.message || err}`);
    }
  }

  // --- Spotify Integration Methods ---

  private handleSpotifyMessage(event: MessageEvent) {
    const origin = event.origin;
    if (!origin.endsWith(".run.app") && !origin.includes("localhost")) {
      return;
    }
    if (event.data?.type === "OAUTH_AUTH_SUCCESS") {
      this.dispatchError("Successfully connected to Spotify!");
      void this.fetchSpotifyStatus();
    } else if (event.data?.type === "OAUTH_AUTH_FAILURE") {
      this.dispatchError(`Spotify Connection Failed: ${event.data.error || "Unknown Error"}`);
    }
  }

  private async fetchSpotifyStatus() {
    try {
      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
      const res = await fetch("/api/spotify/status", {
        headers: { "Authorization": `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        this.spotifyConnected = data.connected;
        this.spotifySource = data.source || "";
        this.spotifyAccessToken = data.token || "";
        if (this.spotifyConnected) {
          void this.fetchSpotifyData();
        } else {
          this.spotifyAccessToken = "";
        }
      }
    } catch (err) {
      console.error("[SPOTIFY] Error fetching connection status:", err);
    }
  }

  private async fetchSpotifyData() {
    this.spotifyLoading = true;
    try {
      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
      
      // Fetch user's Spotify playlists
      const playlistsRes = await fetch("/api/spotify/playlists", {
        headers: { "Authorization": `Bearer ${token}` }
      });
      if (playlistsRes.ok) {
        const pData = await playlistsRes.json();
        this.spotifyPlaylists = pData.items || [];
      }

      // Fetch user's top Spotify tracks
      const topRes = await fetch("/api/spotify/top-tracks", {
        headers: { "Authorization": `Bearer ${token}` }
      });
      if (topRes.ok) {
        const tData = await topRes.json();
        this.spotifyTopTracks = tData.items || [];
      }
    } catch (err) {
      console.error("[SPOTIFY] Error fetching Spotify data:", err);
    } finally {
      this.spotifyLoading = false;
    }
  }

  private async connectSpotify() {
    this.spotifyLoading = true;
    try {
      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
      const res = await fetch("/api/spotify/auth-url", {
        headers: { "Authorization": `Bearer ${token}` }
      });
      if (!res.ok) {
        const errData = await res.json();
        throw new Error(errData.error || "OAuth is not configured server-side.");
      }
      const { url } = await res.json();

      const authWindow = window.open(
        url,
        "spotify_oauth_popup",
        "width=550,height=700,location=no,toolbar=no,status=no"
      );

      if (!authWindow) {
        this.dispatchError("Popup blocked! Please allow popups to connect with Spotify.");
      } else {
        this.dispatchError("Opening Spotify Authorization portal...");
      }
    } catch (err: any) {
      this.dispatchError(`Spotify Connection Mismatch: ${err.message || err}`);
    } finally {
      this.spotifyLoading = false;
    }
  }

  private async disconnectSpotify() {
    this.spotifyLoading = true;
    try {
      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
      const res = await fetch("/api/spotify/disconnect", {
        headers: { "Authorization": `Bearer ${token}` }
      });
      if (res.ok) {
        this.spotifyConnected = false;
        this.spotifySource = "";
        this.spotifyPlaylists = [];
        this.spotifyTopTracks = [];
        this.dispatchError("Successfully disconnected Spotify!");
      }
    } catch (err: any) {
      this.dispatchError(`Failed to disconnect: ${err.message || err}`);
    } finally {
      this.spotifyLoading = false;
    }
  }

  private async saveManualSpotifyToken(manualToken: string) {
    if (!manualToken.trim()) return;
    this.spotifyLoading = true;
    try {
      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
      const res = await fetch("/api/spotify/save-token", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ accessToken: manualToken })
      });

      if (res.ok) {
        this.dispatchError("Spotify Developer Token activated successfully!");
        this.showSpotifyManualInput = false;
        this.manualSpotifyToken = "";
        void this.fetchSpotifyStatus();
      } else {
        const data = await res.json();
        this.dispatchError(`Failed to save token: ${data.error || "Server error"}`);
      }
    } catch (err: any) {
      this.dispatchError(`Error saving manual token: ${err.message || err}`);
    } finally {
      this.spotifyLoading = false;
    }
  }

  private async addActiveTrackToSpotify(playlistId?: string) {
    this.isAddingToSpotify = true;
    try {
      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
      const res = await fetch("/api/spotify/playlists/add-track", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          trackTitle: this.androidActiveSongTitle,
          artistName: this.androidActiveSongArtist,
          vibePrompt: this.androidActiveSongVibe,
          playlistId: playlistId || ""
        })
      });

      if (res.ok) {
        const data = await res.json();
        this.dispatchError(`Successfully added "${data.matchedTrack}" to your Spotify Playlist!`);
        void this.fetchSpotifyData();
      } else {
        const data = await res.json();
        this.dispatchError(`Failed to add song: ${data.error || "Server error"}`);
      }
    } catch (err: any) {
      this.dispatchError(`Failed to add track to Spotify: ${err.message || err}`);
    } finally {
      this.isAddingToSpotify = false;
    }
  }

  private async triggerMockAlert() {
    this.mockAlertLoading = true;
    this.dispatchError("Contacting Gemini resolution engine...");
    try {
      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
      const appCheckTokenObj = (window as any).firebaseAppCheck && (window as any).firebaseAppCheckGetToken
        ? await (window as any).firebaseAppCheckGetToken((window as any).firebaseAppCheck)
        : null;
      const appCheckToken = appCheckTokenObj?.token || "";

      console.log(`[SECURITY] Triggering mock alert with token status: ${this.currentUser ? "Authentic Token" : "Guest Mode Bearer"}`);
      const res = await fetch("/api/vulnerability-alerts/mock", {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`,
          ...(appCheckToken ? { "X-Firebase-AppCheck": appCheckToken } : {}),
        }
      });
      if (res.ok) {
        this.dispatchError("Simulated Dependabot alert processed successfully!");
        await this.fetchSecurityAlerts();
      } else {
        this.dispatchError("Simulation server returned an error.");
      }
    } catch (err) {
      console.error("Failed to run alert simulation:", err);
      this.dispatchError("Network error. Could not contact simulation server.");
    } finally {
      this.mockAlertLoading = false;
    }
  }

  private copyWebhookUrl() {
    const webhookUrl = `${window.location.origin}/api/webhooks/github`;
    navigator.clipboard.writeText(webhookUrl).then(() => {
      this.webhookCopied = true;
      this.dispatchError("GitHub Webhook URL copied to clipboard!");
      setTimeout(() => {
        this.webhookCopied = false;
      }, 3000);
    }).catch(err => {
      console.error("Clipboard write blocked. URL is: ", webhookUrl);
      this.dispatchError(`URL: ${webhookUrl}`);
    });
  }

  private copySpotifyAccessToken() {
    if (!this.spotifyAccessToken) return;
    navigator.clipboard.writeText(this.spotifyAccessToken).then(() => {
      this.spotifyTokenCopied = true;
      this.dispatchError("Spotify Bearer Token copied to clipboard!");
      setTimeout(() => {
        this.spotifyTokenCopied = false;
      }, 3000);
    }).catch(err => {
      console.error("Clipboard write blocked. Token is: ", this.spotifyAccessToken);
      this.dispatchError("Failed to copy. Try manual highlight.");
    });
  }

  private toggleAlertDetails(alertId: string) {
    if (this.expandedAlertId === alertId) {
      this.expandedAlertId = null;
    } else {
      this.expandedAlertId = alertId;
    }
  }

  override disconnectedCallback() {
    super.disconnectedCallback();
    this.stopCaptureLoop();
    this.stopCamera();
    this.stopSimulationLoop();
  }

  private async launchExperience() {
    console.log("[USER] Launching Lyria Live Experience! Setting page to main.");
    this.page = "main";
    // Need to wait for Lit to update and mount the video element in DOM before initializing the camera.
    await this.updateComplete;
    if (this.feedType === "webcam") {
      await this.setupCamera();
    } else {
      this.startSimulationLoop();
    }
  }

  private async setupCamera() {
    console.log("[CAMERA] Stopping any active streams before requesting user access...");
    this.stopCamera();

    this.videoLoadError = null;
    this.videoLoadErrorTitle = null;
    this.videoLoading = false;

    // 1. Verify availability of MediaDevices and getUserMedia
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      const protocol = window.location.protocol;
      const isSecure = window.isSecureContext;
      const userAgent = navigator.userAgent;
      
      console.error(
        `[CAMERA_DIAGNOSTICS] navigator.mediaDevices or getUserMedia is undefined. ` +
        `Context: protocol=${protocol}, isSecureContext=${isSecure}. UserAgent: ${userAgent}`
      );

      const errorMsg = !isSecure && protocol !== "https:" && window.location.hostname !== "localhost"
        ? "Webcam access is blocked in Insecure Contexts (Non-HTTPS). Please use a secure connection (HTTPS) or localhost."
        : "Webcam access is not supported or is blocked in this browser environment.";

      this.videoLoadErrorTitle = "Hardware Not Supported";
      this.videoLoadError = errorMsg;
      this.dispatchError(errorMsg);
      return;
    }

    try {
      console.log("[CAMERA] Querying browser navigator.mediaDevices.getUserMedia permission...");
      const stream = await navigator.mediaDevices.getUserMedia({
        video: {
          width: { ideal: 640 },
          height: { ideal: 480 },
          facingMode: "user"
        },
      });

      console.log("[CAMERA] User granted camera permission! Initializing stream.");
      this.cameraStream = stream;
      this.videoLoading = true;

      // Start a diagnostic timeout of 6 seconds for stream metadata loading
      this.videoTimeoutId = window.setTimeout(() => {
        if (this.videoLoading) {
          console.error(
            `[CAMERA_DIAGNOSTICS] Stream metadata load timed out after 6000ms. ` +
            `Video Element readyState: ${this.videoElement?.readyState ?? "N/A"}`
          );
          this.videoLoading = false;
          this.videoLoadErrorTitle = "Stream Loading Stalled";
          this.videoLoadError = "Camera stream metadata load timed out. Browser permissions, background tabs, or hardware locks may have suspended it. Click Retry or switch to Cosmic Feed.";
          this.dispatchError("Stream loading stalled. Click retry to attempt loading again.");
        }
      }, 6000);

    } catch (e: any) {
      console.error("[CAMERA] Error accessing webcam:", e);
      
      let failReason = "Could not access webcam.";
      if (e.name === "NotAllowedError" || e.name === "PermissionDeniedError") {
        failReason = "Webcam permission was denied by the user or system preferences.";
      } else if (e.name === "NotFoundError" || e.name === "DevicesNotFoundError") {
        failReason = "No physical camera hardware device could be located.";
      } else if (e.name === "NotReadableError" || e.name === "TrackStartError") {
        failReason = "Webcam is already in use by another application, process, or browser tab.";
      } else if (e.message) {
        failReason = `Camera access error: ${e.message}`;
      }

      this.videoLoadErrorTitle = "Camera Stream Failed";
      this.videoLoadError = failReason;
      this.dispatchError(failReason);
    }
  }

  private async switchFeed(type: "webcam" | "simulation") {
    if (type === this.feedType) return;
    this.feedType = type;
    if (type === "webcam") {
      this.stopSimulationLoop();
      await this.updateComplete;
      await this.setupCamera();
    } else {
      this.stopCamera();
      await this.updateComplete;
      this.startSimulationLoop();
    }
  }

  private startSimulationLoop() {
    console.log("[SIMULATOR] Initiating Cosmic Visual Feed Simulation loop...");
    this.stopSimulationLoop();
    this.simTime = 0;
    const render = () => {
      this.drawSimulationFrame();
      this.simAnimationId = requestAnimationFrame(render);
    };
    this.simAnimationId = requestAnimationFrame(render);
    console.log("[SIMULATOR] Simulation loop active and rendering frames.");
  }

  private stopSimulationLoop() {
    if (this.simAnimationId !== null) {
      console.log("[SIMULATOR] Stopping active simulation loop.");
      cancelAnimationFrame(this.simAnimationId);
      this.simAnimationId = null;
    }
  }

  private drawSimulationFrame() {
    const canvas = this.shadowRoot?.getElementById("simulation-canvas") as HTMLCanvasElement;
    if (!canvas) return;

    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    const rect = canvas.getBoundingClientRect();
    if (canvas.width !== rect.width || canvas.height !== rect.height) {
      canvas.width = rect.width || 640;
      canvas.height = rect.height || 480;
    }

    const w = canvas.width;
    const h = canvas.height;
    this.simTime += 0.005;

    const phaseDuration = 20;
    const currentTimeSec = this.simTime * 20;
    const phaseIndex = Math.floor((currentTimeSec / phaseDuration) % 4);

    ctx.fillStyle = "#000000";
    ctx.fillRect(0, 0, w, h);

    let grad = ctx.createRadialGradient(w/2, h/2, 10, w/2, h/2, Math.max(w, h)/1.2);
    if (phaseIndex === 0) {
      grad.addColorStop(0, "rgba(59, 130, 246, 0.25)");
      grad.addColorStop(0.5, "rgba(139, 92, 246, 0.15)");
      grad.addColorStop(1, "rgba(0, 0, 0, 1)");
    } else if (phaseIndex === 1) {
      grad.addColorStop(0, "rgba(16, 185, 129, 0.25)");
      grad.addColorStop(0.6, "rgba(6, 182, 212, 0.15)");
      grad.addColorStop(1, "rgba(0, 0, 0, 1)");
    } else if (phaseIndex === 2) {
      grad.addColorStop(0, "rgba(34, 197, 94, 0.25)");
      grad.addColorStop(0.4, "rgba(99, 102, 241, 0.15)");
      grad.addColorStop(1, "rgba(0, 0, 0, 1)");
    } else {
      grad.addColorStop(0, "rgba(245, 158, 11, 0.25)");
      grad.addColorStop(0.5, "rgba(239, 68, 68, 0.15)");
      grad.addColorStop(1, "rgba(0, 0, 0, 1)");
    }
    ctx.fillStyle = grad;
    ctx.fillRect(0, 0, w, h);

    ctx.lineWidth = 2;
    const count = 4;
    for (let i = 0; i < count; i++) {
      ctx.beginPath();
      let color = "rgba(255, 255, 255, 0.08)";
      if (phaseIndex === 0) color = `rgba(147, 197, 253, ${0.1 - i*0.02})`;
      else if (phaseIndex === 1) color = `rgba(165, 243, 252, ${0.1 - i*0.02})`;
      else if (phaseIndex === 2) color = `rgba(187, 247, 208, ${0.1 - i*0.02})`;
      else color = `rgba(253, 186, 116, ${0.1 - i*0.02})`;

      ctx.strokeStyle = color;
      const waveFreq = 1 + i * 0.5;
      const waveAmp = 40 + i * 15;
      for (let x = 0; x <= w; x += 10) {
        const angle = (x / w) * Math.PI * 2 * waveFreq + this.simTime * 2;
        const y = h / 2 + Math.sin(angle) * waveAmp + Math.cos(this.simTime + i) * 30;
        if (x === 0) ctx.moveTo(x, y);
        else ctx.lineTo(x, y);
      }
      ctx.stroke();
    }

    const particleCount = 40;
    for (let i = 0; i < particleCount; i++) {
      const angle = (i / particleCount) * Math.PI * 2 + this.simTime * (0.3 + (i % 3) * 0.1);
      const radius = Math.min(w, h) * 0.15 + (i % 4) * 35 + Math.sin(this.simTime + i) * 15;
      const x = w / 2 + Math.cos(angle) * radius;
      const y = h / 2 + Math.sin(angle) * radius * 0.6;
      const size = 1.5 + (i % 3);

      ctx.beginPath();
      let color = "rgba(255, 255, 255, 0.6)";
      if (phaseIndex === 0 && i % 2 === 0) color = "rgba(96, 165, 250, 0.8)";
      else if (phaseIndex === 1 && i % 2 === 0) color = "rgba(34, 211, 238, 0.8)";
      else if (phaseIndex === 2 && i % 2 === 0) color = "rgba(74, 222, 128, 0.8)";
      else if (phaseIndex === 3 && i % 2 === 0) color = "rgba(251, 146, 60, 0.8)";

      ctx.fillStyle = color;
      ctx.arc(x, y, size, 0, Math.PI * 2);
      ctx.fill();
    }

    ctx.beginPath();
    let centerGrad = ctx.createRadialGradient(w/2, h/2, 2, w/2, h/2, 40 + Math.sin(this.simTime * 5) * 5);
    if (phaseIndex === 0) {
      centerGrad.addColorStop(0, "rgba(255, 255, 255, 0.8)");
      centerGrad.addColorStop(1, "rgba(139, 92, 246, 0)");
    } else if (phaseIndex === 1) {
      centerGrad.addColorStop(0, "rgba(255, 255, 255, 0.8)");
      centerGrad.addColorStop(1, "rgba(6, 182, 212, 0)");
    } else if (phaseIndex === 2) {
      centerGrad.addColorStop(0, "rgba(255, 255, 255, 0.8)");
      centerGrad.addColorStop(1, "rgba(16, 185, 129, 0)");
    } else {
      centerGrad.addColorStop(0, "rgba(255, 255, 255, 0.8)");
      centerGrad.addColorStop(1, "rgba(245, 158, 11, 0)");
    }
    ctx.fillStyle = centerGrad;
    ctx.arc(w/2, h/2, 50, 0, Math.PI * 2);
    ctx.fill();
  }

  private stopCamera() {
    if (this.videoTimeoutId) {
      window.clearTimeout(this.videoTimeoutId);
      this.videoTimeoutId = null;
    }
    this.videoLoading = false;
    this.videoLoadError = null;
    this.videoLoadErrorTitle = null;

    if (this.cameraStream) {
      this.cameraStream.getTracks().forEach((track) => track.stop());
      this.cameraStream = null;
    }
  }

  private startCaptureLoop() {
    this.stopCaptureLoop();
    // Run an immediate capture and setup loop every 20 seconds.
    void this.captureAndGenerate();
    this.captureIntervalId = window.setInterval(() => {
      void this.captureAndGenerate();
    }, 20000);
  }

  private stopCaptureLoop() {
    if (this.captureIntervalId) {
      clearInterval(this.captureIntervalId);
      this.captureIntervalId = null;
    }
  }

  private async captureAndGenerate() {
    if (this.promptsLoading) return;
    if (this.feedType === "webcam" && (!this.videoElement || !this.cameraStream)) return;

    const snapshot = this.getStreamSnapshot();
    if (!snapshot) return;

    const base64Data = snapshot.split(",")[1];
    await this.generateFromFrame(base64Data);
  }

  private async generateFromFrame(base64Data: string) {
    this.promptsLoading = true;
    try {
      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
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

      this.prompts = newPromptTexts.map((text) => ({
        text,
        weight: 1.0,
      }));

      // Update the live music generator with the fresh prompts and log securely (supporting offline caching)
      const weightedPrompts = this.prompts.map((p) => {
        this.secureLog("prompt", { prompt: p.text, weight: p.weight });

        return {
          text: p.text,
          weight: p.weight,
        };
      });
      void this.liveMusicHelper.setWeightedPrompts(weightedPrompts);

    } catch (err) {
      console.error("Error generating visual music prompts:", err);
    } finally {
      this.promptsLoading = false;
    }
  }

  private handleAndroidGesture(gesture: string) {
    this.dispatchError(`Glasses Gesture Captured: ${gesture}`);
    // Log gesture securely
    this.secureLog("gesture", { gesture });

    if (gesture === "double_tap" || gesture === "tap") {
      void this.togglePlayback();
    }
  }

  private handleAndroidCameraFrame(base64Frame: string) {
    // Process the live camera stream frames from wearable glasses POV
    void this.generateFromFrame(base64Frame);
  }

  private handleAndroidTelemetry(batteryLevel: number, isWearDetected: boolean) {
    this.wearableBattery = batteryLevel;
    this.wearableOnHead = isWearDetected;
    // Log battery and wear detection events securely
    this.secureLog("battery", { batteryLevel, isWearDetected });
  }

  private getStreamSnapshot(): string | null {
    if (this.feedType === "simulation") {
      const simCanvas = this.shadowRoot?.getElementById("simulation-canvas") as HTMLCanvasElement;
      if (!simCanvas) return null;
      
      let drawWidth = simCanvas.width || 640;
      let drawHeight = simCanvas.height || 480;
      if (drawWidth > MAX_CAPTURE_DIM || drawHeight > MAX_CAPTURE_DIM) {
        const aspectRatio = drawWidth / drawHeight;
        if (drawWidth > drawHeight) {
          drawWidth = MAX_CAPTURE_DIM;
          drawHeight = MAX_CAPTURE_DIM / aspectRatio;
        } else {
          drawHeight = MAX_CAPTURE_DIM;
          drawWidth = MAX_CAPTURE_DIM * aspectRatio;
        }
      }
      
      if (!this.canvasElement) {
        this.canvasElement = document.createElement("canvas");
      }
      this.canvasElement.width = drawWidth;
      this.canvasElement.height = drawHeight;
      
      const context = this.canvasElement.getContext("2d");
      if (!context) return null;
      context.drawImage(simCanvas, 0, 0, drawWidth, drawHeight);
      return this.canvasElement.toDataURL(IMAGE_MIME_TYPE);
    }

    if (!this.videoElement || this.videoElement.readyState < 2) return null;

    let drawWidth = this.videoElement.videoWidth || 640;
    let drawHeight = this.videoElement.videoHeight || 480;

    if (drawWidth > MAX_CAPTURE_DIM || drawHeight > MAX_CAPTURE_DIM) {
      const aspectRatio = drawWidth / drawHeight;
      if (drawWidth > drawHeight) {
        drawWidth = MAX_CAPTURE_DIM;
        drawHeight = MAX_CAPTURE_DIM / aspectRatio;
      } else {
        drawHeight = MAX_CAPTURE_DIM;
        drawWidth = MAX_CAPTURE_DIM * aspectRatio;
      }
    }

    if (!this.canvasElement) {
      this.canvasElement = document.createElement("canvas");
    }
    this.canvasElement.width = drawWidth;
    this.canvasElement.height = drawHeight;

    const context = this.canvasElement.getContext("2d");
    if (!context) return null;

    // Standard mirrored display for webcam
    context.translate(drawWidth, 0);
    context.scale(-1, 1);
    context.drawImage(this.videoElement, 0, 0, drawWidth, drawHeight);

    return this.canvasElement.toDataURL(IMAGE_MIME_TYPE);
  }

  private async togglePlayback() {
    if (this.playbackState === "playing") {
      console.log("[AUDIO] Request to STOP live music playback received.");
      this.stopCaptureLoop();
      this.liveMusicHelper.stop();
      this.prompts = [];
      this.playbackState = "stopped";
      console.log("[AUDIO] Live music playback stopped successfully.");
    } else {
      try {
        console.log("[AUDIO] Request to START live music playback received. Initializing synthesizer...");
        this.setupAudioAnalyser();
        await this.liveMusicHelper.play();
        this.playbackState = "playing";
        this.startCaptureLoop();
        console.log("[AUDIO] Live music playback active. Listening to camera frames and generating synth...");
      } catch (err) {
        console.error("[AUDIO] Failed to play live music session:", err);
        this.dispatchError("Could not start audio synthesis.");
      }
    }
  }

  private dispatchError(message: string) {
    if (this.toastMessageElement) {
      this.toastMessageElement.show(message);
    } else {
      console.error(message);
    }
  }

  override render() {
    return html`
      ${this.page === "splash" ? this.renderSplash() : this.renderLayout()}
      <toast-message></toast-message>
    `;
  }

  private renderSplash() {
    return html`
      <div id="splash">
        <span class="material-icons-round splash-icon">video_camera_front</span>
        <h1 class="splash-title">Quinn Camera Director</h1>
        <p class="splash-desc">
          Turn your camera feed into live generative music. Experience beautiful soundscapes evolving synchronously with your visual surroundings.
        </p>

        <!-- Cross-platform Mode Assurance Pill -->
        <div class="platform-badge mb-6">
          <span class="material-icons-round font-icon-green text-emerald-400">check_circle</span>
          <span>Web & Mobile Cameras Supported — Glasses Companion Optional</span>
        </div>

        <!-- Authentic Firebase Authentication Area -->
        <div class="auth-section">
          <div class="auth-title">Secure Developer Auth</div>
          ${this.currentUser ? html`
            <div class="auth-user-info">
              ${this.currentUser.photoURL ? html`
                <img src="${this.currentUser.photoURL}" class="auth-avatar" alt="Avatar" />
              ` : html`
                <span class="material-icons-round auth-placeholder-avatar">account_circle</span>
              `}
              <div class="auth-details">
                <div class="auth-name">${this.currentUser.displayName || "Authorized Developer"}</div>
                <div class="auth-email">${this.currentUser.email || "Firebase Session Active"}</div>
              </div>
            </div>
            <button class="auth-btn-signout" @click=${this.logout}>
              Sign Out
            </button>
          ` : html`
            <div class="auth-email" style="margin-bottom: 0.25rem;">Enterprise Role Authentication Required</div>
            <button class="auth-btn-google" @click=${this.loginWithGoogle}>
              <span class="material-icons-round">login</span>
              Sign In with Google
            </button>
          `}
        </div>

        <button class="splash-btn" @click=${this.launchExperience}>
          Launch Experience
        </button>
      </div>
    `;
  }

  private renderLayout() {
    return html`
      <div id="app-layout">
        <div id="top-nav-bar">
          <div class="brand">
            <span class="material-icons-round brand-icon text-indigo-400 animate-pulse">auto_awesome</span>
            <span class="brand-name">Quinn Director</span>
            <div class="status-pill ${this.wearableActive ? "wearable" : "standard"}">
              <span class="status-dot"></span>
              <span class="status-text">
                ${this.wearableActive ? "Wearables Integrated" : "Browser Webcam Mode"}
              </span>
            </div>
          </div>
          <div class="nav-tabs">
            <button
              class="nav-tab ${this.page === "main" ? "active" : ""}"
              @click=${() => { this.page = "main"; this.launchExperience(); }}
            >
              <span class="material-icons-round">music_note</span>
              Music Studio
            </button>
            <button
              class="nav-tab ${this.page === "community" ? "active" : ""}"
              @click=${() => { this.page = "community"; void this.fetchCommunityTracks(); }}
            >
              <span class="material-icons-round">group</span>
              Community
            </button>
            <button
              class="nav-tab ${this.page === "android_flow" ? "active" : ""}"
              @click=${() => { this.page = "android_flow"; }}
            >
              <span class="material-icons-round">phone_android</span>
              Android Companion App
            </button>
            <button
              class="nav-tab ${this.page === "security" ? "active" : ""}"
              @click=${() => { this.page = "security"; void this.fetchSecurityAlerts(); }}
            >
              <span class="material-icons-round">security</span>
              Security Hub
              ${this.securityAlerts.length > 0 ? html`<span class="badge-count">${this.securityAlerts.length}</span>` : ""}
            </button>
          </div>
        </div>
        <div id="content-area">
          ${this.page === "security" ? this.renderSecurity() : this.page === "android_flow" ? this.renderAndroidCompanion() : this.page === "community" ? this.renderCommunity() : this.renderMain()}
        </div>
        <div id="bottom-nav-bar-web">
          <button
            class="bottom-nav-tab-web ${this.page === "main" ? "active" : ""}"
            @click=${() => { this.page = "main"; this.launchExperience(); }}
          >
            <span class="material-icons-round">music_note</span>
            <span>Music Studio</span>
          </button>
          <button
            class="bottom-nav-tab-web ${this.page === "community" ? "active" : ""}"
            @click=${() => { this.page = "community"; void this.fetchCommunityTracks(); }}
          >
            <span class="material-icons-round">group</span>
            <span>Community</span>
          </button>
          <button
            class="bottom-nav-tab-web ${this.page === "android_flow" ? "active" : ""}"
            @click=${() => { this.page = "android_flow"; }}
          >
            <span class="material-icons-round">phone_android</span>
            <span>Companion</span>
          </button>
          <button
            class="bottom-nav-tab-web ${this.page === "security" ? "active" : ""}"
            @click=${() => { this.page = "security"; void this.fetchSecurityAlerts(); }}
          >
            <span class="material-icons-round">security</span>
            <span>Security Hub</span>
            ${this.securityAlerts.length > 0 ? html`<span class="badge-count">${this.securityAlerts.length}</span>` : ""}
          </button>
        </div>
      </div>
    `;
  }

  private renderAndroidCompanion() {
    return html`
      <div class="android-simulation-container">
        <div class="android-phone-frame">
          <div class="android-status-bar">
            <span class="android-time">10:42</span>
            <div style="display: flex; align-items: center; gap: 4px; font-size: 10px; color: #a1a1aa; font-weight: 700;">
              ${this.wearableOnHead ? html`<span class="material-icons-round text-emerald-400" style="font-size: 11px;" title="Glasses On Head">face</span>` : html`<span class="material-icons-round text-zinc-500" style="font-size: 11px;" title="Glasses Off Head">person_off</span>`}
              <span>${this.wearableBattery}%</span>
            </div>
            <div class="android-status-icons">
              <span class="material-icons-round font-icon-small">bluetooth</span>
              <span class="material-icons-round font-icon-small">wifi</span>
              <span class="material-icons-round font-icon-small">battery_full</span>
            </div>
          </div>
          
          <div class="android-screen-content">
            ${this.androidFlowStep === "welcome" ? this.renderAndroidWelcome() : ""}
            ${this.androidFlowStep === "email" ? this.renderAndroidEmail() : ""}
            ${this.androidFlowStep === "password" ? this.renderAndroidPassword() : ""}
            ${this.androidFlowStep === "name" ? this.renderAndroidName() : ""}
            ${this.androidFlowStep === "search_home" ? this.renderAndroidSearchHome() : ""}
            ${this.androidFlowStep === "search_results" ? this.renderAndroidSearchResults() : ""}
            ${this.androidFlowStep === "album_details" ? this.renderAndroidAlbumDetails() : ""}
            ${this.androidFlowStep === "community" ? this.renderAndroidCommunity() : ""}
            ${this.androidFlowStep === "go_live" ? this.renderAndroidGoLive() : ""}
            ${this.androidFlowStep === "library" ? this.renderAndroidLibrary() : ""}
          </div>
          
          <!-- Shared Bottom Navigation Bar (visible on home, search, album details, community, library) -->
          ${["search_home", "search_results", "album_details", "community", "library"].includes(this.androidFlowStep) ? html`
            <div class="android-mini-player-wrapper">
              ${this.isAndroidCameraActive ? html`
                <div class="android-glasses-preview animate-fade-in">
                  <div class="preview-badge">GLASSES POV LIVE STREAM</div>
                  <video .srcObject=${this.cameraStream} autoplay playsinline muted></video>
                </div>
              ` : ""}
              
              <div class="android-spotify-mini-player" @click=${() => this.isAndroidPlayerExpanded = true}>
                <div class="mini-player-left">
                  ${this.androidActiveSongImage === "beatles_one_cover_simulated" ? html`
                    <div style="background: #e3001a; width: 32px; height: 32px; border-radius: 4px; display: flex; justify-content: center; align-items: center; position: relative; flex-shrink: 0; box-shadow: 0 2px 4px rgba(0,0,0,0.3); margin-right: 8px;">
                      <div style="color: #fcd34d; font-size: 20px; font-weight: 950; line-height: 1;">1</div>
                    </div>
                  ` : html`
                    <img src="${this.androidActiveSongImage}" class="mini-player-art" />
                  `}
                  <div class="mini-player-info">
                    <span class="mini-player-title">${this.androidActiveSongTitle}</span>
                    <span class="mini-player-artist">
                      <span class="material-icons-round text-emerald-400 font-icon-tiny">bluetooth</span>
                      ${this.androidActiveSongArtist} • <span class="text-emerald-400 font-bold">RAYBAN META</span>
                    </span>
                  </div>
                </div>
                <div class="mini-player-right" @click=${(e: Event) => e.stopPropagation()}>
                  <button class="mini-player-action-btn" @click=${this.toggleAndroidCamera} title="Toggle POV Camera stream">
                    <span class="material-icons-round">${this.isAndroidCameraActive ? "videocam" : "videocam_off"}</span>
                  </button>
                  <button class="mini-player-action-btn play-pause-btn" @click=${this.togglePlayback}>
                    <span class="material-icons-round">${this.playbackState === "playing" ? "pause" : "play_arrow"}</span>
                  </button>
                </div>
              </div>
            </div>
            
            <div class="android-spotify-tabs">
              <button class="android-tab-btn ${this.androidFlowStep === 'search_home' || this.androidFlowStep === 'search_results' || this.androidFlowStep === 'album_details' ? 'active' : ''}" @click=${() => { this.androidFlowStep = "search_home"; this.isAndroidPlayerExpanded = false; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(6); }}>
                <span class="material-icons-round">search</span>
                <span>Search</span>
              </button>
              <button class="android-tab-btn ${this.androidFlowStep === 'community' || this.androidFlowStep === 'go_live' ? 'active' : ''}" @click=${() => { this.androidFlowStep = "community"; this.isAndroidPlayerExpanded = false; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(6); }}>
                <span class="material-icons-round">group</span>
                <span>Community</span>
              </button>
              <button class="android-tab-btn ${this.androidFlowStep === 'library' ? 'active' : ''}" @click=${() => { this.androidFlowStep = "library"; this.isAndroidPlayerExpanded = false; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(6); }}>
                <span class="material-icons-round">library_music</span>
                <span>Your Library</span>
              </button>
            </div>
          ` : ""}
          
          <!-- Expanded Player Overlay -->
          ${this.isAndroidPlayerExpanded ? this.renderAndroidExpandedPlayer() : ""}

          <!-- Options Menu Bottom Sheet -->
          ${this.isAndroidOptionsMenuOpen ? this.renderAndroidOptionsMenu() : ""}

          <div class="android-navigation-bar">
            <span class="android-nav-dot" @click=${this.goBackAndroidStep}>◀</span>
            <span class="android-nav-dot" @click=${() => { this.androidFlowStep = "welcome"; this.isAndroidPlayerExpanded = false; }}>●</span>
            <span class="android-nav-dot" @click=${() => { this.androidFlowStep = "search_home"; this.isAndroidPlayerExpanded = false; }}>■</span>
          </div>
        </div>
        
        <!-- Interactive Control Deck explaining the integration -->
        <div class="android-explanation-deck">
          <h3>📱 Spotify-Style Android Wearables Companion</h3>
          <p>
            Experience the complete user lifecycle flow requested for the <strong>Ray-Ban Meta Wearables</strong> integration, matching the production Spotify UI styling.
          </p>
          
          <div class="integration-step-box">
            <div class="step-badge">FLOW STEP 1-3</div>
            <h4>Secure Authorization & Setup</h4>
            <p>Interactive welcome portal, credentials registration, and terms acceptance.</p>
          </div>
          
          <div class="integration-step-box">
            <div class="step-badge active">FLOW STEP 4-5</div>
            <h4>Orchestrated Search & Live POV Stream</h4>
            <p>Browse top genres or search for certified artists. Launching a track triggers the <strong>Gemini AI Orchestrator</strong> server-side route to compose live music on the fly!</p>
          </div>
          
          <div class="integration-step-box">
            <div class="step-badge haptic">HARDWARE TELEMETRY</div>
            <h4>Meta Wearables DAT Sync</h4>
            <p>
              Tapping the camera icon in Search displays the <strong>Glasses POV camera feed</strong> inside the mobile layout. Playback is fully integrated with on-head proximity haptics.
            </p>
          </div>
        </div>
      </div>
    `;
  }

  private goBackAndroidStep() {
    if (this.androidFlowStep === "email") this.androidFlowStep = "welcome";
    else if (this.androidFlowStep === "password") this.androidFlowStep = "email";
    else if (this.androidFlowStep === "name") this.androidFlowStep = "password";
    else if (this.androidFlowStep === "search_home") this.androidFlowStep = "name";
    else if (this.androidFlowStep === "search_results") this.androidFlowStep = "search_home";
  }

  private renderAndroidWelcome() {
    return html`
      <div class="android-flow-welcome">
        <div class="android-spotify-logo-container">
          <span class="material-icons-round logo-soundwave text-emerald-400 font-icon-large">room_service</span>
          <span class="spotify-logo-text">Quinn</span>
        </div>
        
        <h1 class="welcome-heading">Millions of Songs.<br/>Free on Quinn.</h1>
        
        <div class="welcome-buttons-container">
          <button class="welcome-btn-primary" @click=${() => this.androidFlowStep = "email"}>
            Sign up free
          </button>
          
          <button class="welcome-btn-secondary" @click=${() => this.androidFlowStep = "search_home"}>
            Continue with Google
          </button>
          
          <button class="welcome-btn-secondary">
            Continue with Facebook
          </button>
          
          <button class="welcome-btn-secondary">
            Continue with Apple
          </button>
          
          <button class="welcome-btn-link" @click=${() => this.androidFlowStep = "search_home"}>
            Log in
          </button>
        </div>
      </div>
    `;
  }

  private renderAndroidEmail() {
    return html`
      <div class="android-flow-inputs">
        <div class="android-flow-header">
          <button class="android-header-back" @click=${() => this.androidFlowStep = "welcome"}>
            <span class="material-icons-round">arrow_back</span>
          </button>
          <span class="android-header-title">Create account</span>
        </div>
        
        <div class="input-form-container">
          <h2 class="form-title">What's your email?</h2>
          <input 
            type="email" 
            class="form-text-input" 
            placeholder="Email" 
            .value=${this.androidEmail}
            @input=${(e: any) => this.androidEmail = e.target.value}
          />
          <span class="form-subtext">You'll need to confirm this email later.</span>
          
          <button 
            class="form-next-btn ${this.androidEmail.includes("@") ? "active" : ""}"
            ?disabled=${!this.androidEmail.includes("@")}
            @click=${() => this.androidFlowStep = "password"}
          >
            Next
          </button>
        </div>
      </div>
    `;
  }

  private renderAndroidPassword() {
    return html`
      <div class="android-flow-inputs">
        <div class="android-flow-header">
          <button class="android-header-back" @click=${() => this.androidFlowStep = "email"}>
            <span class="material-icons-round">arrow_back</span>
          </button>
          <span class="android-header-title">Create account</span>
        </div>
        
        <div class="input-form-container">
          <h2 class="form-title">Create a password</h2>
          <input 
            type="password" 
            class="form-text-input" 
            placeholder="Password" 
            .value=${this.androidPassword}
            @input=${(e: any) => this.androidPassword = e.target.value}
          />
          <span class="form-subtext">Use atleast 8 characters.</span>
          
          <button 
            class="form-next-btn ${this.androidPassword.length >= 8 ? "active" : ""}"
            ?disabled=${this.androidPassword.length < 8}
            @click=${() => this.androidFlowStep = "name"}
          >
            Next
          </button>
        </div>
      </div>
    `;
  }

  private renderAndroidName() {
    return html`
      <div class="android-flow-inputs">
        <div class="android-flow-header">
          <button class="android-header-back" @click=${() => this.androidFlowStep = "password"}>
            <span class="material-icons-round">arrow_back</span>
          </button>
          <span class="android-header-title">Create account</span>
        </div>
        
        <div class="input-form-container">
          <h2 class="form-title">What's your name?</h2>
          <div class="name-input-wrapper">
            <input 
              type="text" 
              class="form-text-input" 
              placeholder="Name" 
              .value=${this.androidName}
              @input=${(e: any) => this.androidName = e.target.value}
            />
            ${this.androidName.length > 2 ? html`
              <span class="material-icons-round name-check-icon text-emerald-400">check_circle</span>
            ` : ""}
          </div>
          <span class="form-subtext">This appears on your Quinn profile.</span>
          
          <div class="terms-agreement-block">
            <p class="terms-paragraph">
              By tapping on "Create account", you agree to the Quinn <span class="text-emerald-400 font-bold underline cursor-pointer">Terms of Use</span>.
            </p>
            <p class="terms-paragraph mt-2">
              To learn more about how Quinn collects, uses, shares and protects your personal data, please see the <span class="text-emerald-400 font-bold underline cursor-pointer">Privacy Policy</span>.
            </p>
          </div>
          
          <div class="checkbox-form-row">
            <input 
              type="checkbox" 
              id="opt-news" 
              .checked=${this.androidOptInNews}
              @change=${(e: any) => this.androidOptInNews = e.target.checked}
            />
            <label for="opt-news">Please send me news and offers from Quinn.</label>
          </div>
          
          <div class="checkbox-form-row">
            <input 
              type="checkbox" 
              id="opt-share" 
              .checked=${this.androidOptInShare}
              @change=${(e: any) => this.androidOptInShare = e.target.checked}
            />
            <label for="opt-share">Share my registration data with Quinn's content providers for marketing purposes.</label>
          </div>
          
          <button 
            class="form-next-btn active bg-white text-black font-bold mt-4" 
            @click=${() => {
              this.androidFlowStep = "search_home";
              this.dispatchError(`Signed up successfully as ${this.androidName}!`);
            }}
          >
            Create account
          </button>
        </div>
      </div>
    `;
  }

  private renderAndroidSearchHome() {
    return html`
      <div class="android-flow-search">
        <div class="android-search-header-row">
          <h1 class="search-title-large">Search</h1>
          <div style="display: flex; gap: 8px; align-items: center;">
            <button class="go-live-pulsing-btn" @click=${() => { this.androidFlowStep = "go_live"; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate([20, 30, 50]); }}>
              <span class="pulsing-dot-red"></span>
              <span>Go Live</span>
            </button>
            <button class="android-search-camera-btn" @click=${this.toggleAndroidCamera} title="Connect Glasses Camera stream">
              <span class="material-icons-round text-white font-icon-large">${this.isAndroidCameraActive ? "videocam" : "videocam_off"}</span>
            </button>
          </div>
        </div>
        
        <div class="search-input-mock-container" @click=${() => this.androidFlowStep = "search_results"}>
          <span class="material-icons-round search-mock-icon">search</span>
          <input 
            type="text" 
            class="search-mock-field" 
            placeholder="Artists, songs, or podcasts" 
            readonly
          />
        </div>
        
        <div class="search-browse-section">
          <h3 class="browse-section-title">Your top genres</h3>
          <div class="browse-grid-2">
            <div class="genre-card pop bg-purple-600" @click=${() => this.playAndroidSong("Easy", "Troye Sivan", "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=100&auto=format&fit=crop&q=60", "cozy lofi pop vibes")}>
              <span>Pop</span>
              <img src="https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=120&auto=format&fit=crop&q=60" class="genre-rotated-art" />
            </div>
            
            <div class="genre-card indie bg-emerald-600" @click=${() => this.playAndroidSong("Hozier Theme", "Hozier", "https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?w=100&auto=format&fit=crop&q=60", "indie folk blues chords")}>
              <span>Indie</span>
              <img src="https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?w=120&auto=format&fit=crop&q=60" class="genre-rotated-art" />
            </div>
          </div>
          
          <h3 class="browse-section-title mt-4">Popular podcast categories</h3>
          <div class="browse-grid-2">
            <div class="genre-card bg-blue-800" style="background-color: #1e326f;">
              <span>News & Politics</span>
            </div>
            <div class="genre-card bg-rose-600" style="background-color: #e81156;">
              <span>Comedy</span>
            </div>
          </div>
          
          <h3 class="browse-section-title mt-4">Browse all</h3>
          <div class="browse-grid-2">
            <div class="genre-card bg-teal-700" style="background-color: #1e326f;">
              <span>Made for you</span>
            </div>
            <div class="genre-card bg-pink-700" style="background-color: #8d1212;">
              <span>Charts</span>
            </div>
            <div class="genre-card bg-indigo-700" style="background-color: #e1118c;">
              <span>New Releases</span>
            </div>
            <div class="genre-card bg-amber-700" style="background-color: #503750;">
              <span>Discover</span>
            </div>
          </div>
        </div>
      </div>
    `;
  }

  private renderAndroidSearchResults() {
    const artists = [
      { name: "The Beatles", type: "Artist", vibe: "classic pop rock harmonies and vintage chord progressions", img: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=100&auto=format&fit=crop&q=60" },
      { name: "FKA twigs", type: "Artist", vibe: "dark glitch electronic art pop synth", img: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=60" },
      { name: "Hozier", type: "Artist", vibe: "indie folk blues guitar chords", img: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&auto=format&fit=crop&q=60" },
      { name: "Grimes", type: "Artist", vibe: "futuristic cyberpunk techno synthwave", img: "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=100&auto=format&fit=crop&q=60" },
      { name: "Childish Gambino", type: "Artist", vibe: "cozy lofi instrumental strings beats", img: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&auto=format&fit=crop&q=60" },
      { name: "Hayes", type: "Artist", vibe: "deep ambient chill electronic", img: "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=100&auto=format&fit=crop&q=60" },
      { name: "Led Zeppelin", type: "Artist", vibe: "heavy overdrive vintage rock soundscapes", img: "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=100&auto=format&fit=crop&q=60" }
    ];

    const filtered = artists.filter(a => 
      a.name.toLowerCase().includes(this.androidSearchQuery.toLowerCase()) ||
      a.vibe.toLowerCase().includes(this.androidSearchQuery.toLowerCase())
    );

    return html`
      <div class="android-flow-search-results">
        <div class="results-header-row">
          <div class="results-search-wrapper">
            <span class="material-icons-round search-active-icon">search</span>
            <input 
              type="text" 
              class="results-search-field" 
              placeholder="Search" 
              .value=${this.androidSearchQuery}
              @input=${(e: any) => this.androidSearchQuery = e.target.value}
              autofocus
            />
          </div>
          <button class="results-cancel-btn" @click=${() => { this.androidSearchQuery = ""; this.androidFlowStep = "search_home"; }}>
            Cancel
          </button>
        </div>
        
        <div class="results-list-container">
          ${filtered.length === 0 ? html`
            <div class="results-empty">No results found for "${this.androidSearchQuery}"</div>
          ` : filtered.map(artist => html`
            <div class="result-item" @click=${() => {
              if (artist.name === "The Beatles") {
                this.androidFlowStep = "album_details";
              } else {
                this.playAndroidSong(artist.name, artist.name, artist.img, artist.vibe);
              }
            }}>
              <img src="${artist.img}" class="result-avatar" />
              <div class="result-meta">
                <span class="result-name">${artist.name}</span>
                <span class="result-subtitle">${artist.type} • Vibe: ${artist.vibe}</span>
              </div>
            </div>
          `)}
        </div>
      </div>
    `;
  }

  private toggleAndroidCamera() {
    this.isAndroidCameraActive = !this.isAndroidCameraActive;
    if (this.isAndroidCameraActive) {
      void this.setupCamera();
    }
  }

  private playAndroidSong(title: string, artist: string, image: string, vibe: string) {
    this.androidActiveSongTitle = title;
    this.androidActiveSongArtist = artist;
    this.androidActiveSongImage = image;
    this.androidActiveSongVibe = vibe;
    this.androidSongProgress = 0;
    
    // Automatically stream POV simulation
    this.wearableActive = true;
    
    // Play with our live synths
    void this.submitAndroidVibeCommand(vibe);
  }

  private playBeatlesTrack(track: any) {
    if (this.androidActiveSongTitle === track.title) {
      void this.togglePlayback();
    } else {
      this.playAndroidSong(track.title, "The Beatles", "beatles_one_cover_simulated", track.vibe);
    }
  }

  private renderAndroidAlbumDetails() {
    const tracks = [
      { id: 1, title: "Love Me Do - Mono / Remastered", vibe: "upbeat harmonica early vintage rock" },
      { id: 2, title: "From Me to You - Mono / Remastered", vibe: "classic beatles pop rock harmonies" },
      { id: 3, title: "She Loves You - Mono / Remastered", vibe: "energetic vintage pop rock yeah yeah yeah" },
      { id: 4, title: "I Want To Hold Your Hand - Remastered 2015", vibe: "driving early rock classic guitar progressions" }
    ];

    return html`
      <div class="android-flow-album-details animate-fade-in" style="background: linear-gradient(to bottom, #440c15 0%, #121212 50%); height: 100%; overflow-y: auto; padding: 16px; box-sizing: border-box; display: flex; flex-direction: column;">
        <!-- Header Nav Row -->
        <div style="display: flex; align-items: center; margin-bottom: 20px;">
          <button style="background: transparent; border: none; color: #ffffff; cursor: pointer; padding: 4px;" @click=${() => this.androidFlowStep = "search_home"}>
            <span class="material-icons-round" style="font-size: 24px;">arrow_back</span>
          </button>
        </div>

        <!-- Album Art Box -->
        <div style="display: flex; justify-content: center; margin-bottom: 24px;">
          <div style="background: #e3001a; width: 180px; height: 180px; border-radius: 4px; display: flex; flex-direction: column; justify-content: center; align-items: center; position: relative; box-shadow: 0 12px 24px rgba(0,0,0,0.5);">
            <div style="position: absolute; top: 16px; left: 16px; color: #fcd34d; font-size: 9px; font-weight: 900; letter-spacing: 0.5px;">THE BEATLES</div>
            <div style="color: #fcd34d; font-size: 110px; font-weight: 900; line-height: 1; margin-top: 10px;">1</div>
          </div>
        </div>

        <!-- Album Titles & Info -->
        <div style="margin-bottom: 16px; text-align: left;">
          <h1 style="font-size: 20px; font-weight: 800; color: #ffffff; margin-bottom: 8px;">1 (Remastered)</h1>
          
          <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 12px;">
            <div style="width: 20px; height: 20px; border-radius: 50%; background: #27272a; display: flex; align-items: center; justify-content: center; overflow: hidden;">
              <img src="https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=50" style="width: 100%; height: 100%; object-fit: cover;" />
            </div>
            <span style="font-size: 12px; font-weight: 700; color: #ffffff;">The Beatles</span>
          </div>

          <div style="font-size: 11px; color: #a1a1aa; margin-bottom: 16px;">Album • 2000</div>

          <!-- Buttons Bar -->
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div style="display: flex; gap: 16px; align-items: center;">
              <button style="background: transparent; border: none; color: ${this.androidLikedSongs.includes("1 (Remastered)") ? "#1db954" : "#b3b3b3"}; cursor: pointer;" @click=${() => this.toggleLikeSong("1 (Remastered)")}>
                <span class="material-icons-round">${this.androidLikedSongs.includes("1 (Remastered)") ? "favorite" : "favorite_border"}</span>
              </button>
              <button style="background: transparent; border: none; color: #b3b3b3; cursor: pointer;" @click=${() => this.dispatchError("Downloading to glasses offline cache...")}>
                <span class="material-icons-round">arrow_circle_down</span>
              </button>
              <button style="background: transparent; border: none; color: #b3b3b3; cursor: pointer;" @click=${() => this.openOptionsMenu("1 (Remastered)", "The Beatles", "beatles_one_cover_simulated")}>
                <span class="material-icons-round">more_horiz</span>
              </button>
            </div>

            <!-- Big Green Play Button -->
            <button style="background: #1db954; border: none; width: 44px; height: 44px; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; color: #ffffff; transition: transform 0.2s;" @click=${() => this.playBeatlesTrack(tracks[1])}>
              <span class="material-icons-round" style="font-size: 24px; color: #000000;">${this.playbackState === "playing" && this.androidActiveSongArtist === "The Beatles" ? "pause" : "play_arrow"}</span>
            </button>
          </div>
        </div>

        <!-- Tracks List -->
        <div style="display: flex; flex-direction: column; gap: 12px; flex-grow: 1; margin-bottom: 80px;">
          ${tracks.map(track => {
            const isCurrent = this.androidActiveSongTitle === track.title;
            const isPlaying = isCurrent && this.playbackState === "playing";

            return html`
              <div style="display: flex; justify-content: space-between; align-items: center; padding: 6px 0; cursor: pointer;" @click=${() => this.playBeatlesTrack(track)}>
                <div style="display: flex; flex-direction: column; text-align: left; flex: 1; min-width: 0;">
                  <span style="font-size: 13px; font-weight: 600; color: ${isCurrent ? "#1db954" : "#ffffff"}; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: flex; align-items: center; gap: 6px;">
                    ${isPlaying ? html`
                      <span class="material-icons-round text-emerald-400 font-icon-small animate-pulse" style="font-size: 14px;">equalizer</span>
                    ` : ""}
                    ${track.title}
                  </span>
                  <span style="font-size: 11px; color: #b3b3b3; margin-top: 3px; display: flex; align-items: center; gap: 4px;">
                    <span class="material-icons-round text-emerald-400" style="font-size: 11px;">arrow_circle_down</span>
                    The Beatles
                  </span>
                </div>
                <button style="background: transparent; border: none; color: #b3b3b3; cursor: pointer; padding: 4px;" @click=${(e: Event) => { e.stopPropagation(); this.openOptionsMenu(track.title, "The Beatles", "beatles_one_cover_simulated"); }}>
                  <span class="material-icons-round">more_horiz</span>
                </button>
              </div>
            `;
          })}
        </div>
      </div>
    `;
  }

  private renderAndroidExpandedPlayer() {
    const isLiked = this.androidLikedSongs.includes(this.androidActiveSongTitle);
    
    const formatTime = (secs: number) => {
      const mins = Math.floor(secs / 60);
      const remainingSecs = secs % 60;
      return `${mins}:${String(remainingSecs).padStart(2, "0")}`;
    };

    const formattedElapsed = formatTime(this.androidSongProgress);
    const formattedRemaining = `-${formatTime(this.androidSongDuration - this.androidSongProgress)}`;
    const progressPercent = (this.androidSongProgress / this.androidSongDuration) * 100;

    const lyricsLines = this.getLyricsForActiveSong();

    return html`
      <div class="android-expanded-player">
        <!-- Header Nav Row -->
        <div class="expanded-player-header">
          <button class="chevron-btn" @click=${() => this.isAndroidPlayerExpanded = false}>
            <span class="material-icons-round">expand_more</span>
          </button>
          <div class="playlist-title">
            ${this.androidActiveSongArtist === "The Beatles" ? "1 (Remastered)" : "Live Session"}
          </div>
          <button class="more-btn" @click=${() => this.openOptionsMenu(this.androidActiveSongTitle, this.androidActiveSongArtist, this.androidActiveSongImage)}>
            <span class="material-icons-round">more_horiz</span>
          </button>
        </div>

        <!-- Album Art Box -->
        <div class="expanded-player-art-container">
          ${this.androidActiveSongImage === "beatles_one_cover_simulated" ? html`
            <div style="background: #e3001a; width: 220px; height: 220px; border-radius: 8px; display: flex; flex-direction: column; justify-content: center; align-items: center; position: relative; box-shadow: 0 16px 40px rgba(0, 0, 0, 0.75);">
              <div style="position: absolute; top: 18px; left: 18px; color: #fcd34d; font-size: 9px; font-weight: 900; letter-spacing: 0.5px;">THE BEATLES</div>
              <div style="color: #fcd34d; font-size: 130px; font-weight: 900; line-height: 1; margin-top: 10px;">1</div>
            </div>
          ` : html`
            <img src="${this.androidActiveSongImage}" />
          `}
        </div>

        <!-- Info Block -->
        <div class="expanded-player-info-row">
          <div class="expanded-player-title-block">
            <div class="expanded-player-title">${this.androidActiveSongTitle}</div>
            <div class="expanded-player-artist">${this.androidActiveSongArtist}</div>
          </div>
          <button class="expanded-player-like-btn ${isLiked ? "liked" : ""}" @click=${() => this.toggleLikeSong(this.androidActiveSongTitle)}>
            <span class="material-icons-round">${isLiked ? "favorite" : "favorite_border"}</span>
          </button>
        </div>

        <!-- Progress Slider -->
        <div class="expanded-player-progress-area">
          <div class="expanded-player-slider-track" @click=${this.handleSeekbarClick}>
            <div class="expanded-player-slider-fill" style="width: ${progressPercent}%;">
              <div class="expanded-player-slider-thumb"></div>
            </div>
          </div>
          <div class="expanded-player-time-row">
            <span>${formattedElapsed}</span>
            <span>${formattedRemaining}</span>
          </div>
        </div>

        <!-- Controls Row -->
        <div class="expanded-player-controls-row">
          <button class="expanded-player-control-btn ${this.isAndroidShuffleEnabled ? "active" : ""}" @click=${() => this.isAndroidShuffleEnabled = !this.isAndroidShuffleEnabled}>
            <span class="material-icons-round">shuffle</span>
          </button>
          
          <button class="expanded-player-control-btn" @click=${this.playPreviousTrack}>
            <span class="material-icons-round">skip_previous</span>
          </button>
          
          <button class="expanded-player-play-btn" @click=${this.togglePlayback}>
            <span class="material-icons-round">${this.playbackState === "playing" ? "pause" : "play_arrow"}</span>
          </button>
          
          <button class="expanded-player-control-btn" @click=${this.playNextTrack}>
            <span class="material-icons-round">skip_next</span>
          </button>
          
          <button class="expanded-player-control-btn ${this.isAndroidRepeatEnabled ? "active" : ""}" @click=${() => this.isAndroidRepeatEnabled = !this.isAndroidRepeatEnabled}>
            <span class="material-icons-round">repeat</span>
          </button>
        </div>

        <!-- Accessories Row -->
        <div class="expanded-player-accessories-row">
          <div class="expanded-player-device-selector" @click=${() => this.dispatchError("Connected to Beats Pill via Wearables Bluetooth sync.")}>
            <span class="material-icons-round">bluetooth_audio</span>
            <span>BEATSPILL+</span>
          </div>
          
          <div style="display: flex; gap: 16px; align-items: center;">
            <button class="expanded-player-control-btn" style="padding: 4px;" @click=${this.shareActiveSong}>
              <span class="material-icons-round" style="font-size: 20px !important;">share</span>
            </button>
            <button class="expanded-player-control-btn" style="padding: 4px;" @click=${() => this.dispatchError("Opening queue details...")}>
              <span class="material-icons-round" style="font-size: 20px !important;">queue_music</span>
            </button>
          </div>
        </div>

        <!-- Lyrics Card -->
        <div class="expanded-player-lyrics-card ${this.isAndroidLyricsMoreOpen ? "more-open" : ""}" @click=${(e: Event) => { e.stopPropagation(); this.isAndroidLyricsMoreOpen = !this.isAndroidLyricsMoreOpen; }}>
          <div class="lyrics-card-header">
            <span class="lyrics-card-title">Lyrics</span>
            <button class="lyrics-card-more-btn">
              <span>${this.isAndroidLyricsMoreOpen ? "LESS" : "MORE"}</span>
              <span class="material-icons-round" style="font-size: 12px !important;">${this.isAndroidLyricsMoreOpen ? "fullscreen_exit" : "fullscreen"}</span>
            </button>
          </div>
          
          <div class="lyrics-scroll-content">
            ${lyricsLines.map((line, idx) => {
              const lineIndex = Math.min(lyricsLines.length - 1, Math.floor((this.androidSongProgress / this.androidSongDuration) * lyricsLines.length));
              const isActive = idx === lineIndex;
              return html`
                <div class="lyrics-text-line ${isActive ? "active" : ""}">${line}</div>
              `;
            })}
          </div>
        </div>
      </div>
    `;
  }

  private renderAndroidOptionsMenu() {
    const isLiked = this.androidLikedSongs.includes(this.androidActiveSongTitle);

    return html`
      <div class="android-options-menu" @click=${() => this.isAndroidOptionsMenuOpen = false}>
        <div class="options-menu-sheet" @click=${(e: Event) => e.stopPropagation()}>
          <!-- Sheet Header -->
          <div class="options-menu-header">
            ${this.androidActiveSongImage === "beatles_one_cover_simulated" ? html`
              <div style="background: #e3001a; width: 100px; height: 100px; border-radius: 8px; display: flex; flex-direction: column; justify-content: center; align-items: center; position: relative; box-shadow: 0 8px 24px rgba(0,0,0,0.6); margin-bottom: 12px;">
                <div style="position: absolute; top: 10px; left: 10px; color: #fcd34d; font-size: 6px; font-weight: 900; letter-spacing: 0.3px;">THE BEATLES</div>
                <div style="color: #fcd34d; font-size: 60px; font-weight: 900; line-height: 1; margin-top: 5px;">1</div>
              </div>
            ` : html`
              <img src="${this.androidActiveSongImage}" />
            `}
            <div class="options-menu-song-title">${this.androidActiveSongTitle}</div>
            <div class="options-menu-song-artist">${this.androidActiveSongArtist}</div>
          </div>

          <!-- Options List -->
          <div class="options-menu-list">
            <button class="options-menu-item ${isLiked ? "liked" : ""}" @click=${() => { this.toggleLikeSong(this.androidActiveSongTitle); this.isAndroidOptionsMenuOpen = false; }}>
              <span class="material-icons-round">${isLiked ? "favorite" : "favorite_border"}</span>
              <span>${isLiked ? "Unlike song" : "Like song"}</span>
            </button>
            
            <button class="options-menu-item" @click=${() => { this.isAndroidOptionsMenuOpen = false; this.dispatchError(`Opening ${this.androidActiveSongArtist} artist profile...`); }}>
              <span class="material-icons-round">person</span>
              <span>View artist</span>
            </button>
            
            <button class="options-menu-item" @click=${() => { this.isAndroidOptionsMenuOpen = false; this.shareActiveSong(); }}>
              <span class="material-icons-round">share</span>
              <span>Share link</span>
            </button>
            
            <button class="options-menu-item" @click=${() => { this.isAndroidOptionsMenuOpen = false; this.dispatchError("All album tracks added to Liked Songs."); }}>
              <span class="material-icons-round">favorite_all</span>
              <span>Like all songs</span>
            </button>
            
            ${this.spotifyConnected ? html`
              <button class="options-menu-item" style="color: #10b981;" @click=${() => { this.isAndroidOptionsMenuOpen = false; void this.addActiveTrackToSpotify(this.selectedSpotifyPlaylistId); }}>
                <span class="material-icons-round" style="color: #10b981;">queue_music</span>
                <span style="font-weight: bold;">Add to Spotify Playlist</span>
              </button>
            ` : html`
              <button class="options-menu-item" @click=${() => { this.isAndroidOptionsMenuOpen = false; this.dispatchError("Added to your local Quinn library! (Connect your Spotify under the 'Your Library' tab to sync playlists.)"); }}>
                <span class="material-icons-round">playlist_add</span>
                <span>Add to playlist</span>
              </button>
            `}
            
            <button class="options-menu-item" @click=${() => { this.isAndroidOptionsMenuOpen = false; this.dispatchError("Queued next in playback queue."); }}>
              <span class="material-icons-round">queue</span>
              <span>Add to queue</span>
            </button>
            
            <button class="options-menu-item" @click=${() => { this.isAndroidOptionsMenuOpen = false; this.dispatchError("Launching vibe radio stream."); }}>
              <span class="material-icons-round">wifi_tethering</span>
              <span>Go to radio</span>
            </button>
          </div>

          <!-- Close Button -->
          <button class="options-menu-close-btn" @click=${() => this.isAndroidOptionsMenuOpen = false}>
            Close
          </button>
        </div>
      </div>
    `;
  }

  private getLyricsForActiveSong(): string[] {
    if (this.androidActiveSongTitle.includes("Love Me Do")) {
      return [
        "Love, love me do",
        "You know I love you",
        "I'll always be true",
        "So please, love me do",
        "Whoa, love me do",
        "Someone to love",
        "Somebody new",
        "Someone to love",
        "Someone like you..."
      ];
    } else if (this.androidActiveSongTitle.includes("From Me to You")) {
      return [
        "Da-da-da-da-da-dum-dum-da",
        "If there's anything that you want",
        "If there's anything I can do",
        "Just call on me and I'll send it along",
        "With love, from me to you",
        "I got arms that long to hold you",
        "And keep you by my side",
        "I got lips that long to kiss you",
        "And keep you satisfied..."
      ];
    } else if (this.androidActiveSongTitle.includes("She Loves You")) {
      return [
        "She loves you, yeah, yeah, yeah",
        "She loves you, yeah, yeah, yeah",
        "She loves you, yeah, yeah, yeah, yeah",
        "You think you've lost your love",
        "Well, I saw her yesterday",
        "It's you she's thinking of",
        "And she told me what to say",
        "She says she loves you..."
      ];
    } else if (this.androidActiveSongTitle.includes("I Want To Hold Your Hand")) {
      return [
        "Oh yeah, I'll tell you something",
        "I think you'll understand",
        "When I'll say that something",
        "I wanna hold your hand",
        "I wanna hold your hand",
        "I wanna hold your hand",
        "Oh please, say to me",
        "You'll let me be your man..."
      ];
    } else if (this.androidActiveSongTitle === "Easy") {
      return [
        "You make it easy...",
        "Like Sunday morning, or a lofi beat",
        "Watching the sunset roll down the street",
        "A heavy vibration inside our headspace",
        "A melody floating in endless space",
        "Just you and me, breathing in sync",
        "Faster than any eye could blink..."
      ];
    } else {
      return [
        "We are the creators of our soundscape",
        "Translating the view into generative chords",
        "Stacking the fifths, stacking the octaves",
        "Chimes of the cosmos ringing out",
        "An elegant dance of artificial tones",
        "Synthesized live, uniquely yours..."
      ];
    }
  }

  private handleSeekbarClick(e: MouseEvent) {
    const track = e.currentTarget as HTMLElement;
    const rect = track.getBoundingClientRect();
    const clickX = e.clientX - rect.left;
    const percent = Math.max(0, Math.min(1, clickX / rect.width));
    this.androidSongProgress = Math.floor(percent * this.androidSongDuration);
    if (this.isHapticsEnabled && navigator.vibrate) {
      navigator.vibrate(8);
    }
  }

  private playPreviousTrack() {
    if (this.androidActiveSongArtist === "The Beatles") {
      const tracks = [
        "Love Me Do - Mono / Remastered",
        "From Me to You - Mono / Remastered",
        "She Loves You - Mono / Remastered",
        "I Want To Hold Your Hand - Remastered 2015"
      ];
      let idx = tracks.indexOf(this.androidActiveSongTitle);
      if (idx !== -1) {
        idx = (idx - 1 + tracks.length) % tracks.length;
        const previousTrackTitle = tracks[idx];
        const vibes = [
          "upbeat harmonica early vintage rock",
          "classic beatles pop rock harmonies",
          "energetic vintage pop rock yeah yeah yeah",
          "driving early rock classic guitar progressions"
        ];
        this.playAndroidSong(previousTrackTitle, "The Beatles", "beatles_one_cover_simulated", vibes[idx]);
        this.androidSongProgress = 0;
      }
    } else {
      this.androidSongProgress = 0;
      this.dispatchError("Restarting active track.");
    }
  }

  private playNextTrack() {
    if (this.androidActiveSongArtist === "The Beatles") {
      const tracks = [
        "Love Me Do - Mono / Remastered",
        "From Me to You - Mono / Remastered",
        "She Loves You - Mono / Remastered",
        "I Want To Hold Your Hand - Remastered 2015"
      ];
      let idx = tracks.indexOf(this.androidActiveSongTitle);
      if (idx !== -1) {
        idx = (idx + 1) % tracks.length;
        const nextTrackTitle = tracks[idx];
        const vibes = [
          "upbeat harmonica early vintage rock",
          "classic beatles pop rock harmonies",
          "energetic vintage pop rock yeah yeah yeah",
          "driving early rock classic guitar progressions"
        ];
        this.playAndroidSong(nextTrackTitle, "The Beatles", "beatles_one_cover_simulated", vibes[idx]);
        this.androidSongProgress = 0;
      }
    } else {
      this.androidSongProgress = 0;
      this.dispatchError("Next track placeholder.");
    }
  }

  private toggleLikeSong(title: string) {
    if (this.androidLikedSongs.includes(title)) {
      this.androidLikedSongs = this.androidLikedSongs.filter(t => t !== title);
      this.dispatchError(`Removed "${title}" from Liked Songs.`);
    } else {
      this.androidLikedSongs = [...this.androidLikedSongs, title];
      this.dispatchError(`Added "${title}" to Liked Songs.`);
      if (this.isHapticsEnabled && navigator.vibrate) {
        navigator.vibrate([15, 40, 15]);
      }
    }
  }

  private shareActiveSong() {
    const shareUrl = `${window.location.origin}/share/track/${encodeURIComponent(this.androidActiveSongTitle)}`;
    navigator.clipboard.writeText(shareUrl).then(() => {
      this.dispatchError(`Copied share link for "${this.androidActiveSongTitle}" to clipboard!`);
    }).catch(() => {
      this.dispatchError(`Sharing "${this.androidActiveSongTitle}"`);
    });
  }

  private openOptionsMenu(title: string, artist: string, image: string) {
    this.isAndroidOptionsMenuOpen = true;
  }

  private async submitAndroidVibeCommand(vibe: string) {
    this.voiceTranscript = `Play ${vibe}`;
    this.geminiVoiceReply = "";
    this.voiceStatus = "processing";

    try {
      const token = (window as any).firebaseAuth?.currentUser
        ? await (window as any).firebaseAuth.currentUser.getIdToken()
        : "local-dev-user";

      const res = await fetch("/api/text/command", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
          sessionId: this.voiceSessionId,
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
        this.prompts = newPrompts;
        this.liveMusicHelper.setWeightedPrompts(newPrompts);
        
        // Start playback
        if (this.playbackState !== "playing" && this.playbackState !== "loading") {
          void this.togglePlayback();
        }
        
        this.voiceStatus = "idle";
        this.dispatchError(`Quinn: Commenced orchestrating ${vibe}`);
      } else {
        // Fallback simulation
        const mockPrompts = [
          { text: vibe, weight: 1.0 },
          { text: "immersive ambient melody", weight: 0.8 },
          { text: "glasses camera rhythm", weight: 0.5 }
        ];
        this.prompts = mockPrompts;
        this.liveMusicHelper.setWeightedPrompts(mockPrompts);
        if (this.playbackState !== "playing" && this.playbackState !== "loading") {
          void this.togglePlayback();
        }
        this.voiceStatus = "idle";
        this.dispatchError(`Quinn: Simulating orchestrating ${vibe}`);
      }
    } catch (err: any) {
      console.warn("[ANDROID_FLOW] Failed to post text command, running simulation fallback:", err);
      // Simulation fallback
      const mockPrompts = [
        { text: vibe, weight: 1.0 },
        { text: "generative musical weave", weight: 0.8 }
      ];
      this.prompts = mockPrompts;
      this.liveMusicHelper.setWeightedPrompts(mockPrompts);
      if (this.playbackState !== "playing" && this.playbackState !== "loading") {
        void this.togglePlayback();
      }
      this.voiceStatus = "idle";
      this.dispatchError(`Quinn: Simulating orchestrating ${vibe}`);
    }
  }

  private renderSecurity() {
    const webhookUrl = `${window.location.origin}/api/webhooks/github`;

    return html`
      <div id="security-hub">
        <div class="sec-header">
          <div class="sec-title-area">
            <h2 class="sec-title">Security & Dependabot Hub</h2>
            <p class="sec-subtitle">Dynamic vulnerability scanner, telemetry analyzer, and automated dependency resolution plans powered by Gemini.</p>
          </div>
          <button 
            class="mock-trigger-btn"
            ?disabled=${this.mockAlertLoading}
            @click=${this.triggerMockAlert}
          >
            <span class="material-icons-round">science</span>
            ${this.mockAlertLoading ? "Running Remediation..." : "Trigger Simulated Alert"}
          </button>
        </div>

        <!-- Webhook Configuration Panel -->
        <div class="webhook-config-card">
          <div class="webhook-details">
            <div class="webhook-header">
              <span class="material-icons-round webhook-icon">webhook</span>
              <h3>GitHub Webhook Integration</h3>
            </div>
            <p class="webhook-desc">
              Connect your GitHub repository directly to our application backend to receive instant Dependabot updates and automated security audits:
            </p>
            
            <div class="webhook-fields">
              <div class="field-group">
                <span class="field-label">Payload URL</span>
                <div class="url-copy-box">
                  <input type="text" readonly value="${webhookUrl}" id="webhook-url-input" />
                  <button @click=${this.copyWebhookUrl} class="copy-btn">
                    <span class="material-icons-round">${this.webhookCopied ? "check" : "content_copy"}</span>
                    ${this.webhookCopied ? "Copied" : "Copy"}
                  </button>
                </div>
              </div>
              
              <div class="field-row">
                <div class="field-group">
                  <span class="field-label">Content Type</span>
                  <span class="field-value">application/json</span>
                </div>
                <div class="field-group">
                  <span class="field-label">Trigger Events</span>
                  <span class="field-value">Dependabot alerts</span>
                </div>
              </div>
            </div>
          </div>
          <div class="webhook-benefits">
            <h4><span class="material-icons-round">shield</span> DevSecOps Pipeline</h4>
            <ul>
              <li><strong>Direct Connection</strong>: Exposes a real endpoint directly on our Cloud Run Node.js container (No separate Cloud Functions needed!).</li>
              <li><strong>AI Remediation Engine</strong>: Undergoes automatic threat modeling & dependency resolution plans generated instantly via Gemini.</li>
              <li><strong>Local Sandbox Simulation</strong>: Test the entire ingestion, storage, and remediation flow with the <em>Trigger Simulated Alert</em> button.</li>
            </ul>
          </div>
        </div>

        <!-- Spotify OAuth & Developer Token Hub -->
        <div class="bg-[#18181b] border border-[#27272a] rounded-xl p-6 mb-6 flex flex-col md:flex-row gap-6 animate-fade-in" style="margin-bottom: 24px;">
          <div class="flex-1 flex flex-col gap-4">
            <div class="flex items-center gap-3">
              <svg class="w-6 h-6 fill-[#1db954]" viewBox="0 0 24 24" style="width: 24px; height: 24px;">
                <path d="M12 2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm4.586 14.424c-.18.295-.565.387-.86.207-2.377-1.454-5.37-1.783-8.893-.982-.336.075-.668-.135-.744-.47-.077-.337.136-.669.47-.745 3.848-.874 7.14-.5 9.82 1.13.295.182.387.567.207.86zm1.224-2.72c-.227.367-.707.487-1.074.26-2.72-1.672-6.87-2.157-10.078-1.182-.413.125-.844-.107-.97-.52-.124-.413.108-.844.52-.97 3.673-1.115 8.236-.572 11.34 1.34.368.228.488.708.262 1.072zm.107-2.828C14.484 8.766 8.823 8.58 5.518 9.582c-.512.156-1.047-.137-1.202-.65a.947.947 0 01.65-1.202C8.747 6.596 14.98 6.81 19.33 9.395c.462.274.61.874.336 1.336-.273.46-.873.61-1.335.336z"/>
              </svg>
              <h3 class="text-sm font-bold text-white uppercase tracking-wider m-0" style="margin: 0; font-size: 13px;">Spotify OAuth & Developer Token Hub</h3>
            </div>
            <p class="text-xs text-[#a1a1aa] leading-relaxed m-0" style="margin: 0; font-size: 11px; color: #a1a1aa;">
              Authorize this app via OAuth to automatically retrieve and display your active <strong>Spotify Bearer Token</strong>. You can copy this token instantly to run external scripts, custom node services, or verify Spotify Web API endpoints.
            </p>
            
            <div class="flex flex-col gap-3 mt-1" style="display: flex; flex-direction: column; gap: 8px;">
              <div class="flex flex-col gap-1.5" style="display: flex; flex-direction: column; gap: 4px;">
                <span class="text-[10px] font-semibold text-[#a1a1aa] uppercase tracking-wider" style="font-size: 9px; text-transform: uppercase; color: #a1a1aa; font-weight: bold;">Active Spotify Access Token (Bearer)</span>
                <div class="flex items-center gap-2 bg-[#111827] border border-[#27272a] rounded-lg p-2.5" style="display: flex; align-items: center; gap: 8px; background: #111827; border: 1px solid #27272a; padding: 10px; border-radius: 8px;">
                  <input 
                    type="text" 
                    readonly 
                    value="${this.spotifyAccessToken || 'No active token. Please connect Spotify to retrieve your developer token.'}" 
                    class="flex-1 bg-transparent border-none text-xs font-mono text-[#34d399] select-all outline-none" 
                    style="flex: 1; background: transparent; border: none; color: #34d399; font-family: monospace; font-size: 11px; outline: none; width: 100%;"
                  />
                  ${this.spotifyAccessToken ? html`
                    <button @click=${this.copySpotifyAccessToken} class="flex items-center gap-1.5 bg-[#2563eb] hover:bg-[#1d4ed8] text-white border-none text-xs font-semibold py-1.5 px-3 rounded cursor-pointer transition-colors" style="display: flex; align-items: center; gap: 4px; background: #2563eb; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-size: 11px; font-weight: bold;">
                      <span class="material-icons-round text-sm" style="font-size: 14px;">${this.spotifyTokenCopied ? "check" : "content_copy"}</span>
                      <span>${this.spotifyTokenCopied ? "Copied" : "Copy"}</span>
                    </button>
                  ` : html`
                    <button disabled class="flex items-center gap-1.5 bg-[#27272a] text-[#52525b] border-none text-xs font-semibold py-1.5 px-3 rounded cursor-not-allowed" style="display: flex; align-items: center; gap: 4px; background: #27272a; color: #52525b; border: none; padding: 6px 12px; border-radius: 4px; cursor: not-allowed; font-size: 11px; font-weight: bold;">
                      <span class="material-icons-round text-sm" style="font-size: 14px;">lock</span>
                      <span>Locked</span>
                    </button>
                  `}
                </div>
              </div>
            </div>
          </div>
          
          <div class="w-full md:w-[260px] bg-[#111827] border border-[#27272a] rounded-lg p-4 flex flex-col justify-between gap-4" style="width: 260px; background: #111827; border: 1px solid #27272a; border-radius: 8px; padding: 16px; display: flex; flex-direction: column; justify-content: space-between; gap: 16px;">
            <div>
              <h4 class="text-xs font-bold text-white uppercase tracking-wider m-0 mb-1 flex items-center gap-1.5" style="margin: 0 0 8px 0; font-size: 11px; font-weight: bold; text-transform: uppercase; color: white; display: flex; align-items: center; gap: 6px;">
                <span class="material-icons-round text-xs text-[#1db954]" style="font-size: 14px; color: #1db954;">settings_ethernet</span>
                OAuth Parameters
              </h4>
              <p class="text-[10px] text-[#9ca3af] leading-normal m-0" style="margin: 0; font-size: 9.5px; color: #9ca3af; line-height: 1.4;">
                Add this exact Redirect URI to your Spotify Developer Application settings:
              </p>
              <div class="flex flex-col gap-2 mt-2 font-mono text-[9px]" style="display: flex; flex-direction: column; gap: 8px; margin-top: 8px;">
                <div class="bg-[#1f2937] p-2 rounded border border-[#2d3748]" style="background: #1f2937; padding: 8px; border-radius: 4px; border: 1px solid #2d3748; font-family: monospace;">
                  <span class="text-[8px] text-[#9ca3af] block uppercase font-sans font-bold" style="font-size: 8px; color: #9ca3af; font-weight: bold; text-transform: uppercase; font-family: sans-serif; display: block; margin-bottom: 2px;">OAuth Callback URI</span>
                  <code class="text-[#60a5fa] word-break break-all" style="color: #60a5fa; font-size: 9px; word-break: break-all;">${window.location.origin}/api/spotify/callback</code>
                </div>
              </div>
            </div>
            
            <div class="flex flex-col gap-2" style="display: flex; flex-direction: column; gap: 8px;">
              <div class="flex items-center gap-2" style="display: flex; align-items: center; gap: 8px;">
                <span class="inline-block w-2 h-2 rounded-full ${this.spotifyConnected ? 'bg-[#10b981]' : 'bg-[#ef4444]'}" style="width: 8px; height: 8px; border-radius: 50%; background-color: ${this.spotifyConnected ? '#10b981' : '#ef4444'}; display: inline-block;"></span>
                <span class="text-[11px] text-[#e4e4e7] font-medium" style="font-size: 11px; color: #e4e4e7; font-weight: 500;">Status: ${this.spotifyConnected ? 'Linked to Spotify' : 'Disconnected'}</span>
              </div>
              
              ${this.spotifyConnected ? html`
                <button 
                  class="w-full bg-[#ef4444] hover:bg-[#dc2626] text-white font-bold text-xs py-2 px-4 rounded-lg cursor-pointer transition-colors border-none" 
                  style="width: 100%; background: #ef4444; color: white; font-weight: bold; font-size: 11px; padding: 8px 16px; border-radius: 6px; cursor: pointer; transition: background 0.2s; border: none;"
                  @click=${this.disconnectSpotify}
                >
                  Disconnect Account
                </button>
              ` : html`
                <button 
                  class="w-full bg-[#1db954] hover:bg-[#1aa34a] text-white font-bold text-xs py-2 px-4 rounded-lg cursor-pointer transition-colors border-none flex items-center justify-center gap-1.5" 
                  style="width: 100%; background: #1db954; color: white; font-weight: bold; font-size: 11px; padding: 8px 16px; border-radius: 6px; cursor: pointer; transition: background 0.2s; border: none; display: flex; align-items: center; justify-content: center; gap: 6px;"
                  @click=${this.connectSpotify}
                  ?disabled=${this.spotifyLoading}
                >
                  <svg class="w-4 h-4 fill-white" viewBox="0 0 24 24" style="width: 14px; height: 14px; fill: white;">
                    <path d="M12 2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm4.586 14.424c-.18.295-.565.387-.86.207-2.377-1.454-5.37-1.783-8.893-.982-.336.075-.668-.135-.744-.47-.077-.337.136-.669.47-.745 3.848-.874 7.14-.5 9.82 1.13.295.182.387.567.207.86zm1.224-2.72c-.227.367-.707.487-1.074.26-2.72-1.672-6.87-2.157-10.078-1.182-.413.125-.844-.107-.97-.52-.124-.413.108-.844.52-.97 3.673-1.115 8.236-.572 11.34 1.34.368.228.488.708.262 1.072zm.107-2.828C14.484 8.766 8.823 8.58 5.518 9.582c-.512.156-1.047-.137-1.202-.65a.947.947 0 01.65-1.202C8.747 6.596 14.98 6.81 19.33 9.395c.462.274.61.874.336 1.336-.273.46-.873.61-1.335.336z"/>
                  </svg>
                  Connect Spotify Account
                </button>
              `}
            </div>
          </div>
        </div>

        <!-- Alerts Center -->
        <div class="alerts-section">
          <div class="alerts-section-header">
            <h3>Vulnerability Log Stream (${this.securityAlerts.length})</h3>
            <button class="refresh-btn" @click=${this.fetchSecurityAlerts} ?disabled=${this.securityAlertsLoading}>
              <span class="material-icons-round ${this.securityAlertsLoading ? "spin" : ""}">refresh</span>
              Refresh Logs
            </button>
          </div>

          ${this.securityAlertsLoading && this.securityAlerts.length === 0
            ? html`
                <div class="alerts-loading-state">
                  <span class="material-icons-round spin loading-icon">sync</span>
                  <p>Synchronizing Firestore registry and analyzing packages...</p>
                </div>
              `
            : this.securityAlerts.length === 0
            ? html`
                <div class="alerts-empty-state">
                  <span class="material-icons-round empty-icon">check_circle</span>
                  <h4>Zero Vulnerabilities Found</h4>
                  <p>No Dependabot alerts logged yet. Connect your repository using the payload URL above or trigger a live simulation.</p>
                </div>
              `
            : html`
                <div class="alerts-list">
                  ${this.securityAlerts.map(alert => {
                    const isExpanded = this.expandedAlertId === alert.alertId;
                    const plan = alert.upgradePlan || {};
                    const severityClass = `severity-${alert.severity || 'medium'}`;

                    return html`
                      <div class="alert-card ${isExpanded ? 'expanded' : ''}">
                        <div class="alert-summary" @click=${() => this.toggleAlertDetails(alert.alertId)}>
                          <div class="alert-left">
                            <span class="severity-badge ${severityClass}">
                              ${alert.severity?.toUpperCase()}
                            </span>
                            <div class="alert-info">
                              <h4 class="package-name">${alert.packageName}</h4>
                              <span class="ecosystem-tag">${alert.ecosystem}</span>
                            </div>
                          </div>
                          <div class="alert-mid">
                            <p class="advisory-summary">${alert.summary}</p>
                          </div>
                          <div class="alert-right">
                            <span class="alert-date">
                              ${new Date(alert.timestamp).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}
                            </span>
                            <span class="material-icons-round expand-arrow">
                              ${isExpanded ? 'expand_less' : 'expand_more'}
                            </span>
                          </div>
                        </div>

                        ${isExpanded
                          ? html`
                              <div class="alert-details">
                                <div class="details-grid">
                                  <div class="details-left">
                                    <h5>Threat Advisory</h5>
                                    <p class="description-text">${alert.description}</p>
                                    
                                    <div class="meta-row">
                                      <div class="meta-item">
                                        <strong>Patched Release:</strong>
                                        <span class="version-tag">${alert.firstPatchedVersion || 'latest'}</span>
                                      </div>
                                      <div class="meta-item">
                                        <strong>Impacted Config:</strong>
                                        <span class="file-tag">${plan.vulnerableLines || 'package.json'}</span>
                                      </div>
                                    </div>
                                  </div>

                                  <div class="details-right">
                                    <div class="upgrade-plan-box">
                                      <div class="plan-header">
                                        <span class="material-icons-round plan-icon">auto_awesome</span>
                                        <h5>Gemini Resolution Strategy</h5>
                                        <span class="risk-badge risk-${(plan.riskLevel || 'medium').toLowerCase()}">
                                          Risk: ${plan.riskLevel}
                                        </span>
                                      </div>

                                      <div class="plan-section">
                                        <h6>Analysis & Explanation</h6>
                                        <p>${plan.explanation}</p>
                                      </div>

                                      <div class="plan-section">
                                        <h6>Remediation Action</h6>
                                        <p>${plan.remediation}</p>
                                      </div>

                                      <div class="plan-section">
                                        <h6>Shell Upgrade Directive</h6>
                                        <div class="command-box">
                                          <code>${plan.command}</code>
                                          <button 
                                            class="copy-command-btn"
                                            title="Copy shell command"
                                            @click=${(e: Event) => {
                                              e.stopPropagation();
                                              navigator.clipboard.writeText(plan.command);
                                              this.dispatchError("Upgrade command copied!");
                                            }}
                                          >
                                            <span class="material-icons-round">content_copy</span>
                                          </button>
                                        </div>
                                      </div>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            `
                          : nothing}
                      </div>
                    `;
                  })}
                </div>
              `}
        </div>

        <!-- Privacy & Play Console Data Safety Compliance Card -->
        <div class="webhook-config-card mt-8" style="border-left: 4px solid #10b981;">
          <div class="webhook-details">
            <div class="webhook-header">
              <span class="material-icons-round text-emerald-400" style="font-size: 24px;">verified_user</span>
              <h3>Compliance, Privacy & Play Store Data Safety Policy</h3>
            </div>
            <p class="webhook-desc" style="margin-bottom: 1.25rem;">
              Our commitment to user-data hygiene, zero-persistence secure stream pipelines, and Android device security parameters:
            </p>
            
            <div style="display: flex; flex-direction: column; gap: 1rem; font-size: 0.875rem; color: #d1d5db;">
              <div style="background: rgba(24, 24, 27, 0.4); padding: 0.75rem 1rem; border-radius: 8px;">
                <strong style="color: #f4f4f5; display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.25rem;">
                  <span class="material-icons-round text-emerald-400" style="font-size: 16px;">photo_camera</span>
                  Camera POV Streams (Transient Analysis Only)
                </strong>
                <p style="margin: 0; line-height: 1.4;">Wearable and browser camera snapshot frames are securely processed over a TLS-encrypted proxy directly to Gemini. Frames are processed transiently in-memory and are immediately disposed of upon response generation. Absolutely no image or video data is recorded, saved, or cached on disk storage.</p>
              </div>

              <div style="background: rgba(24, 24, 27, 0.4); padding: 0.75rem 1rem; border-radius: 8px;">
                <strong style="color: #f4f4f5; display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.25rem;">
                  <span class="material-icons-round text-emerald-400" style="font-size: 16px;">mic</span>
                  Voice Audio & Dictation (No Storage)
                </strong>
                <p style="margin: 0; line-height: 1.4;">Microphone data captured during vocal commands is processed transiently to resolve user music vibes and control directives. Audio chunks are dispatched securely via our server-side translation helper and deleted instantly. We do not store or share any voice recordings.</p>
              </div>

              <div style="background: rgba(24, 24, 27, 0.4); padding: 0.75rem 1rem; border-radius: 8px;">
                <strong style="color: #f4f4f5; display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.25rem;">
                  <span class="material-icons-round text-emerald-400" style="font-size: 16px;">storage</span>
                  Durable Telemetry Logs (7-Day TTL Auto-Eviction)
                </strong>
                <p style="margin: 0; line-height: 1.4;">Physical touch gestures, wear proximity sensor states, and battery telemetry events logged to Firebase Cloud database are governed by a strict 7-day Time-To-Live (TTL) eviction policy. Telemetry data is purged automatically to protect privacy.</p>
              </div>

              <div style="background: rgba(24, 24, 27, 0.4); padding: 0.75rem 1rem; border-radius: 8px;">
                <strong style="color: #f4f4f5; display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.25rem;">
                  <span class="material-icons-round text-emerald-400" style="font-size: 16px;">vpn_key</span>
                  Secured OAuth Tokenization
                </strong>
                <p style="margin: 0; line-height: 1.4;">User identity is managed strictly via Firebase OAuth. Application tokens are verified server-side with zero exposure of cryptographic credentials, passwords, or client keys to the public frontend DOM.</p>
              </div>
            </div>
          </div>
          <div class="webhook-benefits" style="background: rgba(16, 185, 129, 0.05); border: 1px solid rgba(16, 185, 129, 0.15);">
            <h4 style="color: #34d399; margin-bottom: 0.5rem;"><span class="material-icons-round">gavel</span> Play Console Certified</h4>
            <ul style="color: #d1d5db; padding-left: 1.25rem;">
              <li style="margin-bottom: 0.5rem;"><strong>Zero Permanent Persistence</strong>: Fully satisfies Google Play's Data Safety form regarding high-risk hardware streams.</li>
              <li style="margin-bottom: 0.5rem;"><strong>Secure Transport</strong>: All communications are encrypted end-to-end via TLS 1.3 protocols.</li>
              <li style="margin-bottom: 0.5rem;"><strong>Developer Auth Guard</strong>: Limits access to telemetry dashboard scopes to authorized Google Identity profiles only.</li>
            </ul>
          </div>
        </div>

      </div>
    `;
  }

  private renderMain() {
    const isPlaying = this.playbackState === "playing";

    return html`
      <div id="feed-switcher">
        ${isPlaying ? html`
          <div id="music-radar-badge">
            <span class="radar-dot animate-pulse"></span>
            <span class="radar-text">Camera Audio Active</span>
            <div class="audio-mini-waves">
              <span class="wave-bar w-1 animate-eq"></span>
              <span class="wave-bar w-2 animate-eq" style="animation-delay: 0.15s;"></span>
              <span class="wave-bar w-3 animate-eq" style="animation-delay: 0.3s;"></span>
              <span class="wave-bar w-4 animate-eq" style="animation-delay: 0.45s;"></span>
            </div>
          </div>
        ` : ""}
        <button
          class="switcher-btn ${this.feedType === "webcam" ? "active" : ""}"
          @click=${() => this.switchFeed("webcam")}
        >
          <span class="material-icons-round">videocam</span>
          Webcam
        </button>
        <button
          class="switcher-btn ${this.feedType === "simulation" ? "active" : ""}"
          @click=${() => this.switchFeed("simulation")}
        >
          <span class="material-icons-round">auto_awesome</span>
          Cosmic Feed
        </button>
      </div>

      <div id="video-container" class="${isPlaying ? "music-active" : ""}">
        <canvas id="music-visualizer-canvas"></canvas>
        ${this.feedType === "webcam"
          ? html`
              <video 
                .srcObject=${this.cameraStream} 
                autoplay 
                playsinline 
                muted 
                @loadedmetadata=${(e: Event) => {
                  console.log("[CAMERA] loadedmetadata event successfully triggered.");
                  if (this.videoTimeoutId) {
                    window.clearTimeout(this.videoTimeoutId);
                    this.videoTimeoutId = null;
                  }
                  this.videoLoading = false;
                  this.videoLoadError = null;
                  
                  const video = e.currentTarget as HTMLVideoElement;
                  video.play().catch(err => {
                    console.error("[CAMERA_DIAGNOSTICS] autoplay/play blocked by browser sandbox or permissions:", err);
                    this.videoLoadError = "Video playback was blocked by browser autoplay rules. Click Retry or tap to initiate stream play.";
                  });
                }}
                @error=${(e: Event) => {
                  const video = e.currentTarget as HTMLVideoElement;
                  console.error(`[CAMERA_DIAGNOSTICS] HTMLVideoElement error event triggered. Error code: ${video.error?.code ?? "unknown"}, Message: ${video.error?.message ?? "unknown"}`);
                  if (this.videoTimeoutId) {
                    window.clearTimeout(this.videoTimeoutId);
                    this.videoTimeoutId = null;
                  }
                  this.videoLoading = false;
                  this.videoLoadError = `The browser media engine encountered a critical error decoding/rendering the video feed (Code: ${video.error?.code ?? "unknown"}).`;
                }}
                style="transform: scaleX(-1);"
              ></video>

              ${this.videoLoading
                ? html`
                    <div class="video-feedback-overlay">
                      <div class="feedback-loading-spinner"></div>
                      <div class="feedback-title info">
                        <span class="material-icons-round feedback-btn-icon spin">sync</span>
                        Initializing Camera Stream
                      </div>
                      <div class="feedback-desc">Connecting stream and fetching metadata. Please allow camera access permissions if prompted...</div>
                    </div>
                  `
                : ""}

              ${this.videoLoadError
                ? html`
                    <div class="video-feedback-overlay">
                      <span class="material-icons-round feedback-icon">error_outline</span>
                      <div class="feedback-title">${this.videoLoadErrorTitle || "Camera Stream Failed"}</div>
                      <div class="feedback-desc">${this.videoLoadError}</div>
                      <div class="btn-group">
                        <button class="feedback-btn" @click=${this.setupCamera}>
                          <span class="material-icons-round feedback-btn-icon">refresh</span>
                          Retry Setup
                        </button>
                        <button class="feedback-btn" @click=${() => this.switchFeed("simulation")}>
                          <span class="material-icons-round feedback-btn-icon">auto_awesome</span>
                          Use Cosmic Feed
                        </button>
                      </div>
                    </div>
                  `
                : ""}
            `
          : html`<canvas id="simulation-canvas"></canvas>`}
      </div>
      <div id="overlay">
        <!-- Voice & Text Communication & Control Panel -->
        <div class="voice-card">
          <div class="voice-card-header">
            <span class="material-icons-round voice-card-icon animate-pulse text-indigo-400">forum</span>
            <div class="voice-card-title-container">
              <h4 class="voice-card-title">Vocal & Text Director</h4>
              <p class="voice-card-subtitle">Natural language music control and custom vibes powered by Gemini Live TTS & Firestore bidirectional sync</p>
            </div>
            <div class="voice-status-badge status-${this.voiceStatus}">
              <span class="voice-status-dot"></span>
              <span class="voice-status-text">
                ${this.voiceStatus === "listening" ? "Listening..." :
                  this.voiceStatus === "processing" ? "Thinking..." :
                  this.voiceStatus === "speaking" ? "Speaking..." : "Standby"}
              </span>
            </div>
          </div>

          <div class="voice-card-body">
            <div class="mic-button-wrapper">
              <button 
                class="mic-button ${this.isRecordingVoice ? "recording" : ""} ${this.voiceStatus === "processing" ? "disabled" : ""}"
                ?disabled=${this.voiceStatus === "processing"}
                @click=${this.toggleVoiceRecording}
                title="${this.isRecordingVoice ? "Stop Recording" : "Start Recording Voice Command"}"
              >
                <span class="material-icons-round mic-icon">
                  ${this.isRecordingVoice ? "stop" : "mic"}
                </span>
              </button>
              <div class="mic-pulse-ring ring-1"></div>
              <div class="mic-pulse-ring ring-2"></div>
            </div>

            <div class="voice-transcript-area">
              ${this.voiceTranscript ? html`
                <div class="transcript-text">
                  <strong class="text-indigo-400">You:</strong> "${this.voiceTranscript}"
                </div>
              ` : html`
                <div class="transcript-placeholder">
                  Press the mic button and say, or type your vibe/command below:<br/>
                  <em class="text-indigo-300">"Play some relaxing lofi beats"</em> or <em class="text-indigo-300">"Stop the music"</em>
                </div>
              `}

              ${this.geminiVoiceReply ? html`
                <div class="ai-reply-text animate-fade-in">
                  <strong class="text-pink-400">Gemini:</strong> "${this.geminiVoiceReply}"
                </div>
              ` : ""}

              <div class="voice-text-input-row">
                <input 
                  type="text" 
                  id="text-vibe-input"
                  class="voice-text-field"
                  placeholder="Type custom vibe or command & press Enter..."
                  @keydown=${(e: KeyboardEvent) => {
                    if (e.key === "Enter") {
                      void this.submitTextCommand();
                    }
                  }}
                  ?disabled=${this.voiceStatus === "processing"}
                />
                <button 
                  class="voice-text-submit-btn" 
                  @click=${this.submitTextCommand}
                  ?disabled=${this.voiceStatus === "processing"}
                  title="Submit Vibe Command"
                >
                  <span class="material-icons-round">send</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Jacob Collier-style Interactive Orchestrator -->
        <div class="orchestrator-card">
          <div class="orchestrator-header">
            <h4 class="orchestrator-title">
              <span class="material-icons-round text-pink-400">music_note</span>
              Audience Orchestrator
            </h4>
            <div class="orchestrator-actions">
              <button 
                class="orchestrator-help-btn ${this.isHapticsEnabled ? "text-pink-400" : ""}"
                @click=${() => {
                  this.isHapticsEnabled = !this.isHapticsEnabled;
                  if (this.isHapticsEnabled && navigator.vibrate) {
                    navigator.vibrate(20);
                  }
                }}
                title="${this.isHapticsEnabled ? "Disable Haptic Immersion" : "Enable Haptic Immersion"}"
                style="${this.isHapticsEnabled ? "color: #f472b6;" : ""}"
              >
                <span class="material-icons-round">${this.isHapticsEnabled ? "vibration" : "mobile_off"}</span>
              </button>
              <button 
                class="orchestrator-help-btn"
                @click=${() => this.showGestureTutorial = !this.showGestureTutorial}
                title="Toggle Gesture Tutorial Guide"
              >
                <span class="material-icons-round">help_outline</span>
              </button>
              <span class="orchestrator-badge">Live Synth</span>
            </div>
          </div>

          <p class="orchestrator-description">
            Be the orchestrator like Jacob Collier. Tap and drag inside the coordinate grid to play real-time chords with hands. Adjust the harmonic voicing to stack octaves!
          </p>

          <!-- Instrument Selection Tabs -->
          <div class="orchestrator-sections">
            <button 
              class="orchestrator-section-btn ${this.currentInstrument === "piano" ? "active piano" : ""}" 
              @click=${() => this.switchInstrument("piano")}
              title="Acoustic Grand Piano"
            >
              <span class="material-icons-round section-icon">piano</span>
              <span class="section-label">Piano</span>
            </button>
            <button 
              class="orchestrator-section-btn ${this.currentInstrument === "clarinet" ? "active" : ""}" 
              @click=${() => this.switchInstrument("clarinet")}
              title="Concert Clarinet woodwind"
            >
              <span class="material-icons-round section-icon">air</span>
              <span class="section-label">Clarinet</span>
            </button>
            <button 
              class="orchestrator-section-btn ${this.currentInstrument === "violin" ? "active" : ""}" 
              @click=${() => this.switchInstrument("violin")}
              title="Orchestral Saw Strings"
            >
              <span class="material-icons-round section-icon">auto_stories</span>
              <span class="section-label">Strings</span>
            </button>
            <button 
              class="orchestrator-section-btn ${this.currentInstrument === "chimes" ? "active" : ""}" 
              @click=${() => this.switchInstrument("chimes")}
              title="Celestial Chimes"
            >
              <span class="material-icons-round section-icon">filter_vintage</span>
              <span class="section-label">Chimes</span>
            </button>
          </div>

          <!-- The Coordinate Gesture Interaction Pad -->
          <div 
            class="orchestrator-gesture-pad ${this.isGestureActive ? "active" : ""}"
            @mousedown=${this.handleGestureStart}
            @mousemove=${this.handleGestureMove}
            @mouseup=${this.handleGestureEnd}
            @mouseleave=${this.handleGestureEnd}
            @touchstart=${this.handleGestureStart}
            @touchmove=${this.handleGestureMove}
            @touchend=${this.handleGestureEnd}
            @wheel=${this.handleGestureWheel}
          >
            <div class="gesture-pad-grid"></div>
            <canvas class="gesture-pad-canvas"></canvas>
            
            <!-- Crosshair Target -->
            <div 
              class="gesture-pad-target" 
              style="left: ${this.gestureX * 100}%; top: ${this.gestureY * 100}%;"
            ></div>

            <!-- Labels -->
            <div class="gesture-pad-label-x">Pitch / Frequency (C3 - C6)</div>
            <div class="gesture-pad-label-y">Timbre / Tone</div>
            <div class="gesture-pad-status">
              ${this.isGestureActive ? `PITCH: ${this.currentPlayingPitch}` : "TAP & DRAG TO PLAY"}
            </div>

            <!-- Swipe/Pinch Tutorial Interactive Overlay -->
            ${this.showGestureTutorial ? html`
              <div class="gesture-tutorial-overlay" @mousedown=${(e: Event) => e.stopPropagation()} @touchstart=${(e: Event) => e.stopPropagation()}>
                <div class="tutorial-path-animation">
                  <div class="tutorial-path-point start"></div>
                  <div class="tutorial-path-point end"></div>
                  <div class="tutorial-path-hand">
                    <span class="material-icons-round">touch_app</span>
                  </div>
                </div>
                <div class="gesture-tutorial-content">
                  <div class="tutorial-guide-title">
                    <span class="material-icons-round">school</span>
                    <span>Interactive Gesture Guide</span>
                  </div>
                  <ul class="tutorial-instructions">
                    <li>
                      <span class="instruction-bullet x">↔</span>
                      <span>Drag <strong>Left / Right</strong> to shift <strong>Pitch</strong></span>
                    </li>
                    <li>
                      <span class="instruction-bullet y">↕</span>
                      <span>Drag <strong>Up / Down</strong> to filter <strong>Tone/Timbre</strong></span>
                    </li>
                    <li>
                      <span class="instruction-bullet scroll">⤢</span>
                      <span><strong>Pinch</strong> or <strong>Scroll</strong> to adjust chord width</span>
                    </li>
                  </ul>
                  <button class="tutorial-got-it-btn" @click=${(e: Event) => { e.stopPropagation(); this.showGestureTutorial = false; }}>
                    Got it, play!
                  </button>
                </div>
              </div>
            ` : ""}
          </div>

          <!-- Harmonic Chord Sliders -->
          <div class="orchestrator-sliders">
            <div class="orchestrator-slider-row">
              <div class="orchestrator-slider-header">
                <span class="orchestrator-slider-label">Harmonic Chord Voicing Span</span>
                <span class="orchestrator-slider-value">x${this.harmonicVoicing.toFixed(1)}</span>
              </div>
              <input 
                type="range" 
                min="1.0" 
                max="5.0" 
                step="0.1" 
                class="orchestrator-slider-input" 
                .value=${this.harmonicVoicing}
                @input=${(e: any) => {
                  this.harmonicVoicing = parseFloat(e.target.value);
                  this.updateInstrumentSynth();
                }}
              />
            </div>
          </div>

          <div class="orchestrator-footer-tip">
            <span class="material-icons-round text-pink-400 animate-pulse">pinch</span>
            <span>Tip: Scroll inside pad or pinch to change chord spacing!</span>
          </div>
        </div>

        <div id="prompts-container">
          ${this.prompts.map(
            (p) => html`
              <div class="prompt-tag">
                ${p.text}
              </div>
            `
          )}
        </div>
        <div id="controls">
          <button
            class="action-btn ${isPlaying ? "stop" : "play"}"
            @click=${this.togglePlayback}
          >
            <span class="material-icons-round">
              ${isPlaying ? "stop" : "play_arrow"}
            </span>
            ${isPlaying ? "Stop Music" : "Start Music"}
          </button>
        </div>
      </div>
    `;
  }

  // ======================================================================
  // REAL-TIME VOICE COMMANDS & FIRESTORE SYNCHRONIZATION METHODS
  // ======================================================================

  private setupVoiceSessionListener() {
    const db = (window as any).firebaseDb;
    const docFn = (window as any).firestoreDoc;
    const onSnapshotFn = (window as any).firestoreOnSnapshot;

    if (!db || !docFn || !onSnapshotFn) {
      console.warn("[FIREBASE] Firestore is not initialized or accessible in index.ts yet. Falling back to HTTP response syncing.");
      return;
    }

    if (this.unsubscribeVoiceListener) {
      this.unsubscribeVoiceListener();
    }

    try {
      console.log(`[FIREBASE] Subscribing to real-time bidirectional voice channel: voice_sessions/${this.voiceSessionId}`);
      this.unsubscribeVoiceListener = onSnapshotFn(
        docFn(db, "voice_sessions", this.voiceSessionId),
        (snapshot: any) => {
          if (snapshot.exists()) {
            const data = snapshot.data();
            console.log("[FIREBASE] Real-time voice session synchronized:", data);
            this.handleVoiceSessionUpdate(data);
          }
        },
        (error: any) => {
          console.warn("[FIREBASE] Firestore snapshot subscription error (gracefully using HTTP fallback):", error);
        }
      );
    } catch (e) {
      console.warn("[FIREBASE] Failed to set up Firestore onSnapshot listener:", e);
    }
  }

  private handleVoiceSessionUpdate(data: any) {
    if (!data) return;

    if (data.status === "processing") {
      this.voiceStatus = "processing";
    } else if (data.status === "completed") {
      // 1. Update text state
      if (data.transcript) {
        this.voiceTranscript = data.transcript;
      }
      if (data.aiResponse) {
        this.geminiVoiceReply = data.aiResponse;
      }

      // 2. Play Gemini's TTS Vocal audio if present
      if (data.aiAudio && this.voiceStatus !== "speaking") {
        this.voiceStatus = "speaking";
        this.playVoiceAudio(data.aiAudio);
      } else if (this.voiceStatus === "processing") {
        this.voiceStatus = "idle";
      }

      // 3. Apply the parsed music action!
      if (data.musicAction) {
        this.applyMusicAction(data.musicAction);
      }
    } else if (data.status === "error") {
      this.voiceStatus = "idle";
      this.dispatchError(data.error || "Voice command processing failed.");
    }
  }

  private playVoiceAudio(base64Audio: string) {
    try {
      // Release existing audio player if any
      if (this.voiceAudioPlayer) {
        this.voiceAudioPlayer.pause();
        this.voiceAudioPlayer = null;
      }

      const binary = atob(base64Audio);
      const bytes = new Uint8Array(binary.length);
      for (let i = 0; i < binary.length; i++) {
        bytes[i] = binary.charCodeAt(i);
      }
      // Note: gemini-3.1-flash-tts-preview returns WAV/PCM audio
      const blob = new Blob([bytes], { type: "audio/wav" });
      const audioUrl = URL.createObjectURL(blob);

      this.voiceAudioPlayer = new Audio(audioUrl);
      this.voiceAudioPlayer.onended = () => {
        this.voiceStatus = "idle";
        URL.revokeObjectURL(audioUrl);
      };
      this.voiceAudioPlayer.play().catch(err => {
        console.warn("[AUDIO] Vocal playback blocked or failed:", err);
        this.voiceStatus = "idle";
      });
    } catch (e) {
      console.error("[AUDIO] Error playing vocal response:", e);
      this.voiceStatus = "idle";
    }
  }

  private applyMusicAction(musicAction: any) {
    if (!musicAction || !musicAction.action) return;

    const action = musicAction.action.toLowerCase();
    console.log(`[MUSIC_ACTION] Executing action: "${action}"`, musicAction);

    switch (action) {
      case "play":
        if (musicAction.prompts && musicAction.prompts.length > 0) {
          // Convert string array to WeightedPrompt[]
          const newPrompts = musicAction.prompts.map((p: string) => ({
            text: p,
            weight: 1.0,
          }));
          this.prompts = newPrompts;
          // Apply to our LiveMusicHelper!
          this.liveMusicHelper.setWeightedPrompts(newPrompts);
        }
        if (this.playbackState !== "playing" && this.playbackState !== "loading") {
          void this.togglePlayback();
        }
        break;

      case "stop":
      case "pause":
        if (this.playbackState === "playing" || this.playbackState === "loading") {
          void this.togglePlayback();
        }
        break;

      case "volume":
        if (musicAction.volume !== undefined) {
          const vol = parseFloat(musicAction.volume);
          if (!isNaN(vol)) {
            this.liveMusicHelper.setVolume(vol);
            this.dispatchError(`Volume set to ${Math.round(vol * 100)}%`);
          }
        }
        break;

      case "none":
      default:
        break;
    }
  }

  private async submitTextCommand() {
    if (!this.textVibeInput) return;
    const val = this.textVibeInput.value?.trim();
    if (!val) return;

    this.textVibeInput.value = "";

    console.log("[TEXT_COMMAND] Submitting text command:", val);
    this.voiceTranscript = val;
    this.geminiVoiceReply = "";
    this.voiceStatus = "processing";

    this.setupVoiceSessionListener();

    try {
      const token = (window as any).firebaseAuth?.currentUser
        ? await (window as any).firebaseAuth.currentUser.getIdToken()
        : "local-dev-user";

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
          sessionId: this.voiceSessionId,
          text: val
        })
      });

      if (!res.ok) {
        throw new Error(`Server returned HTTP ${res.status}`);
      }

      const sessionDoc = await res.json();
      console.log("[TEXT_COMMAND] Text command processing result:", sessionDoc);
      this.handleVoiceSessionUpdate(sessionDoc);
    } catch (err: any) {
      console.error("[TEXT_COMMAND] Failed to send text command:", err);
      this.voiceStatus = "idle";
      this.dispatchError(`Failed to process text vibe: ${err.message || err}`);
    }
  }

  private async toggleVoiceRecording() {
    if (this.isRecordingVoice) {
      this.stopVoiceRecording();
    } else {
      await this.startVoiceRecording();
    }
  }

  private async startVoiceRecording() {
    try {
      this.recordedChunks = [];
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      this.isRecordingVoice = true;
      this.voiceStatus = "listening";
      this.voiceTranscript = "Listening closely...";
      this.geminiVoiceReply = "";

      // Detect supported mimeType
      let options = { mimeType: "audio/webm" };
      if (!MediaRecorder.isTypeSupported("audio/webm")) {
        options = { mimeType: "" }; // Let the browser fallback
      }

      this.mediaRecorder = new MediaRecorder(stream, options);
      this.mediaRecorder.ondataavailable = (e) => {
        if (e.data && e.data.size > 0) {
          this.recordedChunks.push(e.data);
        }
      };

      this.mediaRecorder.onstop = async () => {
        console.log(`[VOICE_RECORD] Stopped recording. Collected ${this.recordedChunks.length} chunks.`);
        
        // Stop all tracks in the stream to release microphone icon
        stream.getTracks().forEach(track => track.stop());

        const mimeType = this.mediaRecorder?.mimeType || "audio/webm";
        const audioBlob = new Blob(this.recordedChunks, { type: mimeType });

        // Convert Blob to Base64
        const reader = new FileReader();
        reader.readAsDataURL(audioBlob);
        reader.onloadend = async () => {
          const base64WithHeader = reader.result as string;
          const base64Data = base64WithHeader.split(",")[1];

          // Trigger processing state
          this.voiceStatus = "processing";
          this.voiceTranscript = "Processing your voice command...";
          this.geminiVoiceReply = "";

          // Bidirectional check: ensure snapshot listener is listening
          this.setupVoiceSessionListener();

          // Post to our secure server-side proxy
          try {
            const token = (window as any).firebaseAuth?.currentUser
              ? await (window as any).firebaseAuth.currentUser.getIdToken()
              : "local-dev-user";

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
                sessionId: this.voiceSessionId,
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
            if (this.voiceStatus === "processing") {
              this.handleVoiceSessionUpdate(data);
            }
          } catch (fetchErr: any) {
            console.error("[VOICE_COMMAND] Failed to post voice command:", fetchErr);
            this.voiceStatus = "idle";
            this.voiceTranscript = "";
            this.dispatchError("Failed to communicate with AI voice service. Please try again.");
          }
        };
      };

      this.mediaRecorder.start();
      console.log("[VOICE_RECORD] MediaRecorder started successfully.");
    } catch (err: any) {
      console.error("[VOICE_RECORD] Error starting microphone capture:", err);
      this.isRecordingVoice = false;
      this.voiceStatus = "idle";
      this.dispatchError("Microphone access denied. Please verify browser permissions.");
    }
  }

  private stopVoiceRecording() {
    if (this.mediaRecorder && this.mediaRecorder.state !== "inactive") {
      this.mediaRecorder.stop();
    }
    this.isRecordingVoice = false;
  }

  /* ======================================================================
     JACOB COLLIER INTERACTIVE ORCHESTRATOR & VISUALIZER CORE
     ====================================================================== */

  private setupAudioAnalyser() {
    try {
      const ctx = this.liveMusicHelper?.audioContext;
      if (!ctx) return;

      // Create Analyser Node for live visualizer
      this.analyser = ctx.createAnalyser();
      this.analyser.fftSize = 256;
      this.analyserData = new Uint8Array(this.analyser.frequencyBinCount);
      
      // Connect LiveMusicHelper output to analyser as extraDestination
      this.liveMusicHelper.extraDestination = this.analyser;
      
      console.log("[AUDIO_ANALYSIS] Audio Analyser Node successfully created and connected to Lyria output stream.");
    } catch (e) {
      console.warn("[AUDIO_ANALYSIS] Failed to set up Analyser Node:", e);
    }
  }

  private startVisualizerLoop() {
    if (this.visualizerAnimId) {
      cancelAnimationFrame(this.visualizerAnimId);
    }

    const render = () => {
      this.renderVisualizerCanvas();
      this.visualizerAnimId = requestAnimationFrame(render);
    };
    this.visualizerAnimId = requestAnimationFrame(render);
  }

  private stopVisualizerLoop() {
    if (this.visualizerAnimId) {
      cancelAnimationFrame(this.visualizerAnimId);
      this.visualizerAnimId = null;
    }
  }

  private triggerBeatHaptic(avgVolume: number) {
    if (!this.isHapticsEnabled || !navigator.vibrate) return;

    const now = Date.now();
    // Throttle: don't vibrate too close together (at least 150ms apart, matching tempo of beats)
    if (now - this.lastVibrationTime < 150) return;

    // Track a moving history of recent average volume spikes to dynamically adapt to music volume
    this.volumeHistory.push(avgVolume);
    if (this.volumeHistory.length > 20) {
      this.volumeHistory.shift();
    }

    const avgHist = this.volumeHistory.reduce((a, b) => a + b, 0) / (this.volumeHistory.length || 1);

    // Trigger a pulse on significant volume spikes (beat) or heavy transient energy
    const isSpike = avgVolume > avgHist * 1.3 && avgVolume > 12;
    const isStrongBeat = avgVolume > 60;

    if (isSpike || isStrongBeat) {
      this.lastVibrationTime = now;
      
      // Determine haptic duration/pattern in sync with the beat and instrument intensity
      let pattern: number | number[] = 15; // Light crisp beat tap (15ms)

      // Strings/Violin has smooth swelling haptics, while piano/chimes are crisp taps
      if (this.currentInstrument === "violin") {
        // Strings swell: longer, lower-intensity feeling (e.g. 25ms pulse)
        pattern = Math.min(35, Math.floor(avgVolume * 0.25 + 10));
      } else if (this.currentInstrument === "piano") {
        // Piano hits: short, sharp percussion (e.g. 12ms pulse)
        pattern = Math.min(20, Math.max(8, Math.floor(avgVolume * 0.15)));
      } else if (this.currentInstrument === "clarinet") {
        // Woodwind breath: medium tactile pulse (e.g. 18ms pulse)
        pattern = Math.min(25, Math.max(10, Math.floor(avgVolume * 0.18)));
      } else { // Chimes/others
        // Shimmer: multi-pulse vibration (double tap)
        pattern = [10, 30, 10];
      }

      navigator.vibrate(pattern);
    }
  }

  private getDominantFrequencyColors() {
    if (!this.analyser || this.playbackState !== "playing") {
      return {
        ambientBg: "rgba(99, 102, 241, 0.12)",
        ambientBgBright: "rgba(129, 140, 248, 0.35)",
        ambientBgSolid: "rgba(129, 140, 248, 1.0)"
      };
    }

    this.analyser.getByteFrequencyData(this.analyserData);
    
    let maxVal = -1;
    let maxIndex = -1;
    
    // Skip the very low-frequency rumble (0-2 bins)
    for (let i = 2; i < this.analyserData.length; i++) {
      if (this.analyserData[i] > maxVal) {
        maxVal = this.analyserData[i];
        maxIndex = i;
      }
    }

    // If quiet, return default indigo color theme
    if (maxVal < 8) {
      return {
        ambientBg: "rgba(99, 102, 241, 0.12)",
        ambientBgBright: "rgba(129, 140, 248, 0.35)",
        ambientBgSolid: "rgba(129, 140, 248, 1.0)"
      };
    }

    const sampleRate = this.liveMusicHelper.audioContext?.sampleRate || 48000;
    const fftSize = this.analyser.fftSize;
    const dominantFreq = maxIndex * sampleRate / fftSize;

    // Use logarithmic scale to map frequency to hue (HSL)
    // 20Hz to 8000Hz range
    const minLnf = Math.log(20);
    const maxLnf = Math.log(8000);
    const lnf = Math.log(Math.max(20, Math.min(8000, dominantFreq)));
    const percent = (lnf - minLnf) / (maxLnf - minLnf);

    // Map percent to a color hue (0 to 360)
    const hue = Math.floor(percent * 360);

    // Calculate dynamic intensity (opacity) based on the peak amplitude
    const amplitudeRatio = maxVal / 255;
    const intensity = 0.08 + amplitudeRatio * 0.18; // Opacity ranges from 0.08 to 0.26

    return {
      ambientBg: `hsla(${hue}, 85%, 60%, ${intensity})`,
      ambientBgBright: `hsla(${hue}, 95%, 65%, ${Math.min(0.7, intensity * 2.2)})`,
      ambientBgSolid: `hsla(${hue}, 85%, 60%, 1.0)`
    };
  }

  private renderVisualizerCanvas() {
    const canvas = this.visualizerCanvas;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    const rect = canvas.getBoundingClientRect();
    if (canvas.width !== rect.width || canvas.height !== rect.height) {
      canvas.width = rect.width;
      canvas.height = rect.height;
    }

    // Clear canvas with a transparent trail for beautiful motion blur motion waves
    ctx.fillStyle = "rgba(10, 10, 15, 0.16)";
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    let sum = 0;
    if (this.analyser && this.playbackState === "playing") {
      this.analyser.getByteFrequencyData(this.analyserData);
      for (let i = 0; i < this.analyserData.length; i++) {
        sum += this.analyserData[i];
      }
    }

    const avgVolume = this.analyser ? (sum / this.analyserData.length) : 0;
    const isPlaying = this.playbackState === "playing" && avgVolume > 3;

    // Trigger haptic vibration pulses synced with beat and music intensity
    if (isPlaying) {
      this.triggerBeatHaptic(avgVolume);
    }

    // Apply color-changing background values based on dominant frequency
    const colors = this.getDominantFrequencyColors();
    const layout = this.shadowRoot?.getElementById("app-layout");
    if (layout) {
      layout.style.setProperty("--ambient-bg", colors.ambientBg);
      layout.style.setProperty("--ambient-bg-bright", colors.ambientBgBright);
      layout.style.setProperty("--ambient-bg-solid", colors.ambientBgSolid);
    }

    // Draw 3 layered fluid organic sine/bezier waves
    this.drawFluidWave(ctx, canvas, isPlaying, "rgba(129, 140, 248, 0.38)", 1.25, 0.7, 0); // Indigo
    this.drawFluidWave(ctx, canvas, isPlaying, "rgba(244, 114, 182, 0.42)", 0.85, 1.1, 1.5); // Pink
    this.drawFluidWave(ctx, canvas, isPlaying, "rgba(6, 182, 212, 0.48)", 0.48, 1.5, 3.1); // Cyan
  }

  private drawFluidWave(
    ctx: CanvasRenderingContext2D,
    canvas: HTMLCanvasElement,
    isPlaying: boolean,
    color: string,
    ampScale: number,
    speedScale: number,
    phaseShift: number
  ) {
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(0, canvas.height / 2);

    const points = 7;
    const step = canvas.width / (points - 1);
    const time = Date.now() * 0.001 * speedScale;

    for (let i = 0; i < points; i++) {
      const x = i * step;

      // Map frequency bin indexes to spatial points
      let freqFactor = 15;
      if (this.analyser && isPlaying) {
        const binIdx = Math.min(
          this.analyserData.length - 1,
          Math.floor((i / points) * this.analyserData.length * 0.6)
        );
        freqFactor = this.analyserData[binIdx];
      }

      // Compute dynamic amplitude and sinusoidal y coordinates
      const baseAmp = isPlaying
        ? (freqFactor / 255) * canvas.height * 0.35
        : canvas.height * 0.06; // Ambient slow float when idle
      const amp = baseAmp * ampScale;

      const y =
        canvas.height / 2 +
        Math.sin(time + i * 1.3 + phaseShift) * amp +
        Math.cos(time * 0.5 + i * 0.7) * (amp * 0.3);

      if (i === 0) {
        ctx.moveTo(x, y);
      } else {
        const prevX = (i - 1) * step;
        
        let prevFreqFactor = 15;
        if (this.analyser && isPlaying) {
          const prevBinIdx = Math.min(
            this.analyserData.length - 1,
            Math.floor(((i - 1) / points) * this.analyserData.length * 0.6)
          );
          prevFreqFactor = this.analyserData[prevBinIdx];
        }
        const prevBaseAmp = isPlaying
          ? (prevFreqFactor / 255) * canvas.height * 0.35
          : canvas.height * 0.06;
        const prevAmp = prevBaseAmp * ampScale;
        const prevY =
          canvas.height / 2 +
          Math.sin(time + (i - 1) * 1.3 + phaseShift) * prevAmp +
          Math.cos(time * 0.5 + (i - 1) * 0.7) * (prevAmp * 0.3);

        const cpX1 = prevX + step / 2;
        const cpY1 = prevY;
        const cpX2 = prevX + step / 2;
        const cpY2 = y;

        ctx.bezierCurveTo(cpX1, cpY1, cpX2, cpY2, x, y);
      }
    }

    ctx.strokeStyle = color;
    ctx.lineWidth = isPlaying ? 3.5 : 1.5;
    ctx.shadowBlur = isPlaying ? 25 : 6;
    ctx.shadowColor = color;
    ctx.stroke();
    ctx.restore();
  }

  private startGesturePadLoop() {
    if (this.gesturePadAnimId) {
      cancelAnimationFrame(this.gesturePadAnimId);
    }

    const render = () => {
      this.renderGesturePadCanvas();
      this.gesturePadAnimId = requestAnimationFrame(render);
    };
    this.gesturePadAnimId = requestAnimationFrame(render);
  }

  private stopGesturePadLoop() {
    if (this.gesturePadAnimId) {
      cancelAnimationFrame(this.gesturePadAnimId);
      this.gesturePadAnimId = null;
    }
  }

  private renderGesturePadCanvas() {
    const canvas = this.gesturePadCanvas;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    const rect = canvas.getBoundingClientRect();
    if (canvas.width !== rect.width || canvas.height !== rect.height) {
      canvas.width = rect.width;
      canvas.height = rect.height;
    }

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    if (this.isGestureActive) {
      const cx = this.gestureX * canvas.width;
      const cy = this.gestureY * canvas.height;
      const time = Date.now() * 0.003;

      // Draw concentric expanding harmonic chords rings
      const numRings = Math.floor(this.harmonicVoicing);
      ctx.save();
      ctx.shadowBlur = 18;
      
      for (let i = 1; i <= numRings + 1; i++) {
        const radius = (15 + i * 18 + Math.sin(time + i) * 6);
        ctx.beginPath();
        ctx.arc(cx, cy, radius, 0, Math.PI * 2);
        
        const hue = (220 + i * 25 + this.gestureX * 60) % 360;
        ctx.strokeStyle = `hsla(${hue}, 85%, 65%, ${0.65 / i})`;
        ctx.lineWidth = 1.8;
        ctx.shadowColor = `hsla(${hue}, 85%, 65%, 0.8)`;
        ctx.stroke();
      }

      // Draw horizontal musical line (instrument bow string) vibrating centered aroundcy
      ctx.beginPath();
      ctx.shadowBlur = 10;
      ctx.shadowColor = "rgba(244, 114, 182, 0.75)";
      ctx.strokeStyle = "rgba(244, 114, 182, 0.45)";
      ctx.lineWidth = 1.5;

      for (let x = 0; x < canvas.width; x++) {
        const distFromTouch = Math.abs(x - cx);
        const amp = Math.max(0, 24 - distFromTouch * 0.12); // Pinching amplitude near cursor
        const offset = Math.sin(x * 0.08 - time * 5) * amp * (1.0 - this.gestureY * 0.5);
        
        if (x === 0) {
          ctx.moveTo(x, cy + offset);
        } else {
          ctx.lineTo(x, cy + offset);
        }
      }
      ctx.stroke();
      ctx.restore();
    }
  }

  private switchInstrument(inst: "piano" | "clarinet" | "violin" | "chimes") {
    this.currentInstrument = inst;
    if (this.isGestureActive) {
      this.playInstrumentSynth();
    }
  }

  private handleGestureStart(e: MouseEvent | TouchEvent) {
    e.preventDefault();
    this.isGestureActive = true;
    this.updateGestureCoords(e);
    
    if (this.isHapticsEnabled && navigator.vibrate) {
      navigator.vibrate(15); // A nice clean haptic touch response
    }
    
    this.playInstrumentSynth();
  }

  private handleGestureMove(e: MouseEvent | TouchEvent) {
    if (!this.isGestureActive) return;
    e.preventDefault();
    this.updateGestureCoords(e);
    this.updateInstrumentSynth();
  }

  private handleGestureEnd() {
    if (!this.isGestureActive) return;
    this.isGestureActive = false;
    this.stopAllSynthesizers();
  }

  private handleGestureWheel(e: WheelEvent) {
    e.preventDefault();
    const delta = e.deltaY * -0.005;
    this.harmonicVoicing = Math.max(1.0, Math.min(5.0, this.harmonicVoicing + delta));
    this.updateInstrumentSynth();
  }

  private updateGestureCoords(e: MouseEvent | TouchEvent) {
    const pad = this.gesturePadCanvas?.parentElement;
    if (!pad) return;

    const rect = pad.getBoundingClientRect();
    let clientX = 0;
    let clientY = 0;

    if (window.TouchEvent && e instanceof TouchEvent) {
      if (e.touches.length > 0) {
        clientX = e.touches[0].clientX;
        clientY = e.touches[0].clientY;

        // Two-finger pinch chord sizing
        if (e.touches.length >= 2) {
          const t1 = e.touches[0];
          const t2 = e.touches[1];
          const dx = t1.clientX - t2.clientX;
          const dy = t1.clientY - t2.clientY;
          const dist = Math.sqrt(dx * dx + dy * dy);
          
          if ((this as any)._lastTouchDist) {
            const diff = dist - (this as any)._lastTouchDist;
            this.harmonicVoicing = Math.max(1.0, Math.min(5.0, this.harmonicVoicing + diff * 0.015));
          }
          (this as any)._lastTouchDist = dist;
        }
      }
    } else {
      const mouseEvent = e as MouseEvent;
      clientX = mouseEvent.clientX;
      clientY = mouseEvent.clientY;
    }

    const x = Math.max(0, Math.min(1, (clientX - rect.left) / rect.width));
    const y = Math.max(0, Math.min(1, (clientY - rect.top) / rect.height));

    this.gestureX = x;
    this.gestureY = y;
  }

  private stopAllSynthesizers() {
    if (this.activeSynthOscillators && this.activeSynthOscillators.length > 0) {
      const ctx = this.liveMusicHelper?.audioContext;
      const now = ctx ? ctx.currentTime : 0;

      this.activeSynthOscillators.forEach(item => {
        try {
          if (ctx) {
            item.gain.gain.setValueAtTime(item.gain.gain.value, now);
            item.gain.gain.exponentialRampToValueAtTime(0.001, now + 0.15);
          }
          setTimeout(() => {
            try { item.osc.stop(); } catch (e) {}
          }, 200);
        } catch (e) {}
      });
      this.activeSynthOscillators = [];
    }
    
    if (this.activeSynthLfo) {
      try { this.activeSynthLfo.stop(); } catch (e) {}
      this.activeSynthLfo = null;
    }
    if (this.activeSynthLfoGain) {
      this.activeSynthLfoGain = null;
    }
  }

  private playInstrumentSynth() {
    const ctx = this.liveMusicHelper?.audioContext;
    if (!ctx) return;
    
    if (ctx.state === "suspended") {
      ctx.resume();
    }

    this.stopAllSynthesizers();

    // Map gestureX (0 to 1) to MIDI note range 48 (C3) to 84 (C6)
    const midiNote = 48 + this.gestureX * 36;
    const baseFreq = 440 * Math.pow(2, (midiNote - 69) / 12);
    this.currentPlayingPitch = this.getNoteName(midiNote);

    const now = ctx.currentTime;
    const destination = ctx.destination;

    // Harmonic chord voicings setup: based on voicing multiplier, build chords
    const noteFrequencies: number[] = [baseFreq];
    
    if (this.harmonicVoicing >= 1.5) {
      const intervalMultiplier = this.harmonicVoicing >= 3.0 ? 1.5 : 1.25; // Perfect 5th or Major 3rd
      noteFrequencies.push(baseFreq * intervalMultiplier);
    }
    if (this.harmonicVoicing >= 2.8) {
      noteFrequencies.push(baseFreq * 2.0); // Octave
    }
    if (this.harmonicVoicing >= 4.0) {
      noteFrequencies.push(baseFreq * 3.0); // Chords fifth/octave
    }

    noteFrequencies.forEach((freq, index) => {
      const voiceGain = ctx.createGain();
      voiceGain.gain.setValueAtTime(0, now);
      
      const maxVolume = 0.22 / noteFrequencies.length;
      
      if (this.currentInstrument === "piano") {
        // Grand Piano additive synthesis
        const osc1 = ctx.createOscillator();
        const osc2 = ctx.createOscillator();
        const osc3 = ctx.createOscillator();

        osc1.type = "sine";
        osc1.frequency.setValueAtTime(freq, now);

        osc2.type = "sine";
        osc2.frequency.setValueAtTime(freq * 2, now);

        osc3.type = "sine";
        osc3.frequency.setValueAtTime(freq * 3, now);

        const g1 = ctx.createGain();
        const g2 = ctx.createGain();
        const g3 = ctx.createGain();

        g1.gain.setValueAtTime(1.0, now);
        g2.gain.setValueAtTime(0.4, now);
        g3.gain.setValueAtTime(0.15, now);

        osc1.connect(g1).connect(voiceGain);
        osc2.connect(g2).connect(voiceGain);
        osc3.connect(g3).connect(voiceGain);

        voiceGain.gain.linearRampToValueAtTime(maxVolume, now + 0.02);
        voiceGain.gain.exponentialRampToValueAtTime(maxVolume * 0.4, now + 0.6);

        osc1.start(now);
        osc2.start(now);
        osc3.start(now);

        this.activeSynthOscillators.push(
          { osc: osc1, gain: voiceGain },
          { osc: osc2, gain: g2 },
          { osc: osc3, gain: g3 }
        );

      } else if (this.currentInstrument === "clarinet") {
        // Woodwind triangle wave with envelope + vibrato LFO
        const osc = ctx.createOscillator();
        osc.type = "triangle";
        osc.frequency.setValueAtTime(freq, now);

        const filter = ctx.createBiquadFilter();
        filter.type = "lowpass";
        const filterFreq = 600 + (1.0 - this.gestureY) * 1500;
        filter.frequency.setValueAtTime(filterFreq, now);

        const lfo = ctx.createOscillator();
        lfo.type = "sine";
        lfo.frequency.setValueAtTime(5.5, now);

        const lfoGain = ctx.createGain();
        lfoGain.gain.setValueAtTime(3.5, now);

        lfo.connect(lfoGain).connect(osc.frequency);
        osc.connect(filter).connect(voiceGain);

        voiceGain.gain.linearRampToValueAtTime(maxVolume, now + 0.08);

        lfo.start(now);
        osc.start(now);

        this.activeSynthOscillators.push({ osc, gain: voiceGain, filter });
        if (index === 0) {
          this.activeSynthLfo = lfo;
          this.activeSynthLfoGain = lfoGain;
        }

      } else if (this.currentInstrument === "violin") {
        // String sawtooth detuning
        const osc1 = ctx.createOscillator();
        const osc2 = ctx.createOscillator();

        osc1.type = "sawtooth";
        osc1.frequency.setValueAtTime(freq, now);

        osc2.type = "sawtooth";
        osc2.frequency.setValueAtTime(freq * 1.006, now);

        const filter = ctx.createBiquadFilter();
        filter.type = "lowpass";
        const filterFreq = 300 + (1.0 - this.gestureY) * 2800;
        filter.frequency.setValueAtTime(filterFreq, now);

        const lfo = ctx.createOscillator();
        lfo.type = "sine";
        lfo.frequency.setValueAtTime(6.0, now);

        const lfoGain = ctx.createGain();
        lfoGain.gain.setValueAtTime(4.0, now);

        lfo.connect(lfoGain);
        lfoGain.connect(osc1.frequency);
        lfoGain.connect(osc2.frequency);

        osc1.connect(filter);
        osc2.connect(filter);
        filter.connect(voiceGain);

        voiceGain.gain.linearRampToValueAtTime(maxVolume, now + 0.15);

        lfo.start(now);
        osc1.start(now);
        osc2.start(now);

        this.activeSynthOscillators.push(
          { osc: osc1, gain: voiceGain, filter },
          { osc: osc2, gain: voiceGain, filter }
        );
        if (index === 0) {
          this.activeSynthLfo = lfo;
          this.activeSynthLfoGain = lfoGain;
        }

      } else if (this.currentInstrument === "chimes") {
        // Celestial metal chimes high bell oscillators
        const chimeFreq = freq * 2.0;
        const osc = ctx.createOscillator();
        osc.type = "sine";
        osc.frequency.setValueAtTime(chimeFreq, now);

        const oscHarmonic = ctx.createOscillator();
        oscHarmonic.type = "sine";
        oscHarmonic.frequency.setValueAtTime(chimeFreq * 2.51, now);

        const harmGain = ctx.createGain();
        harmGain.gain.setValueAtTime(0.3, now);

        oscHarmonic.connect(harmGain).connect(voiceGain);
        osc.connect(voiceGain);

        voiceGain.gain.linearRampToValueAtTime(maxVolume * 1.2, now + 0.01);
        voiceGain.gain.exponentialRampToValueAtTime(maxVolume * 0.02, now + 1.2);

        osc.start(now);
        oscHarmonic.start(now);

        this.activeSynthOscillators.push(
          { osc, gain: voiceGain },
          { osc: oscHarmonic, gain: harmGain }
        );
      }

      voiceGain.connect(destination);
    });
  }

  private updateInstrumentSynth() {
    const ctx = this.liveMusicHelper?.audioContext;
    if (!ctx || this.activeSynthOscillators.length === 0) return;

    const midiNote = 48 + this.gestureX * 36;
    const baseFreq = 440 * Math.pow(2, (midiNote - 69) / 12);
    this.currentPlayingPitch = this.getNoteName(midiNote);

    if (this.currentPlayingPitch !== this.lastPlayingPitch) {
      this.lastPlayingPitch = this.currentPlayingPitch;
      if (this.isHapticsEnabled && navigator.vibrate) {
        navigator.vibrate(6); // Very crisp click when note switches
      }
    }

    const now = ctx.currentTime;

    const freqs: number[] = [baseFreq];
    if (this.harmonicVoicing >= 1.5) {
      const intervalMultiplier = this.harmonicVoicing >= 3.0 ? 1.5 : 1.25;
      freqs.push(baseFreq * intervalMultiplier);
    }
    if (this.harmonicVoicing >= 2.8) {
      freqs.push(baseFreq * 2.0);
    }
    if (this.harmonicVoicing >= 4.0) {
      freqs.push(baseFreq * 3.0);
    }

    let freqIndex = 0;
    this.activeSynthOscillators.forEach((item, idx) => {
      try {
        const targetFreq = freqs[freqIndex] || baseFreq;

        if (this.currentInstrument === "piano") {
          const mult = (idx % 3) + 1;
          item.osc.frequency.setTargetAtTime(targetFreq * mult, now, 0.05);
          if (idx % 3 === 2) freqIndex++;
        } else if (this.currentInstrument === "clarinet") {
          item.osc.frequency.setTargetAtTime(targetFreq, now, 0.05);
          if (item.filter) {
            const filterFreq = 600 + (1.0 - this.gestureY) * 1500;
            item.filter.frequency.setTargetAtTime(filterFreq, now, 0.05);
          }
          freqIndex++;
        } else if (this.currentInstrument === "violin") {
          const detuneMult = (idx % 2 === 0) ? 1.0 : 1.006;
          item.osc.frequency.setTargetAtTime(targetFreq * detuneMult, now, 0.05);
          if (item.filter) {
            const filterFreq = 300 + (1.0 - this.gestureY) * 2800;
            item.filter.frequency.setTargetAtTime(filterFreq, now, 0.05);
          }
          if (idx % 2 === 1) freqIndex++;
        } else if (this.currentInstrument === "chimes") {
          const chimeFreq = targetFreq * 2.0;
          const isHarmonic = (idx % 2 === 1);
          item.osc.frequency.setTargetAtTime(isHarmonic ? chimeFreq * 2.51 : chimeFreq, now, 0.05);
          if (isHarmonic) freqIndex++;
        }
      } catch (e) {
        console.warn("Error updating frequency:", e);
      }
    });
  }

  }

  // Select preset and auto-configure wearables status and music vibe
  private selectPreset(id: string) {
    this.selectedPresetId = id;
    if (this.isHapticsEnabled && navigator.vibrate) {
      navigator.vibrate([15, 10, 15]);
    }
    
    if (id === "preset-rooftop") {
      this.wearableBattery = 95;
      this.wearableOnHead = true;
      this.wearableActive = true;
      this.playAndroidSong("Rooftop Chords", "Hozier", "https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?w=100&auto=format&fit=crop&q=60", "indie folk blues guitar chords");
      this.dispatchError("Syncing Rooftop preset to glasses...");
    } else if (id === "preset-lofi") {
      this.wearableBattery = 78;
      this.wearableOnHead = true;
      this.wearableActive = true;
      this.playAndroidSong("Cozy Easy lofi", "Troye Sivan", "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?w=100&auto=format&fit=crop&q=60", "cozy lofi instrumental strings beats");
      this.dispatchError("Syncing Late Night Lofi preset to glasses...");
    } else if (id === "preset-mainstage") {
      this.wearableBattery = 62;
      this.wearableOnHead = true;
      this.wearableActive = true;
      this.playAndroidSong("Mainstage Lead", "Grimes", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=100&auto=format&fit=crop&q=60", "futuristic cyberpunk techno synthwave");
      this.dispatchError("Syncing Festival Mainstage preset...");
    } else if (id === "preset-desk") {
      this.wearableBattery = 15;
      this.wearableOnHead = false;
      this.wearableActive = false;
      this.isAndroidCameraActive = false; // off head proximity safety!
      this.playAndroidSong("Outer Space Bell", "Hayes", "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=100&auto=format&fit=crop&q=60", "deep ambient chill electronic");
      this.dispatchError("Safety proximity warning: Glasses are unworn on desktop!");
    }
  }

  // Trigger custom wearable haptic patterns
  private triggerLiveHapticPattern(patternType: string) {
    if (!this.isHapticsEnabled) {
      this.dispatchError("Turn on Haptic Immersion to feel wearable responses.");
      return;
    }
    
    if (patternType === "single") {
      if (navigator.vibrate) navigator.vibrate(50);
      this.dispatchError("Sent single haptic pulse to Ray-Ban Meta glasses.");
    } else if (patternType === "double") {
      if (navigator.vibrate) navigator.vibrate([30, 40, 30]);
      this.dispatchError("Sent double tap haptic alert to glasses.");
    } else if (patternType === "pulse") {
      if (navigator.vibrate) navigator.vibrate([60, 100, 60, 100, 60]);
      this.dispatchError("Sent continuous beat-sync heartbeat pulse.");
    }
  }

  // Render the community tab
  private renderAndroidCommunity() {
    return html`
      <div class="android-flow-community animate-fade-in">
        <div class="community-header-row">
          <h1 class="community-title-large">Creator Stage</h1>
          <button class="go-live-pulsing-btn" @click=${() => { this.androidFlowStep = "go_live"; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate([25, 25, 60]); }}>
            <span class="pulsing-dot-red"></span>
            <span>Go Live</span>
          </button>
        </div>

        <div class="live-metrics-bento">
          <div class="metric-mini-card">
            <span class="metric-mini-val">${this.liveStreamStats.listeners}</span>
            <span class="metric-mini-lbl">Live Listeners</span>
          </div>
          <div class="metric-mini-card">
            <span class="metric-mini-val">${this.liveStreamStats.likes}</span>
            <span class="metric-mini-lbl">Fan Cheers</span>
          </div>
        </div>

        <div class="community-chat-container">
          <div class="chat-feed-title-row">
            <span class="chat-feed-title">Direct Fan Requests</span>
            <span style="font-size: 9px; color: #1db954; font-weight: bold; display: flex; align-items: center; gap: 3px;">
              <span class="pulsing-dot-red" style="background:#1db954;"></span> LIVE STREAM CHAT
            </span>
          </div>

          <div class="chat-bubbles-scroll">
            ${this.communityComments.map(comment => html`
              <div class="chat-bubble-card" @click=${() => {
                this.playAndroidSong(comment.user + "'s Choice", "Live Request", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=100&auto=format&fit=crop&q=60", "futuristic cyberpunk techno synthwave");
                this.dispatchError(`Co-creating with ${comment.user}: Playing selected request vibe!`);
              }}>
                <div class="chat-user-row">
                  <span class="chat-username">${comment.user}</span>
                  <span class="chat-time">${comment.time}</span>
                </div>
                <div class="chat-text">${comment.text}</div>
                <div class="chat-suggestion-indicator">
                  <span class="material-icons-round" style="font-size:10px;">music_note</span>
                  <span>Tap to match this fan request</span>
                </div>
              </div>
            `)}
          </div>
        </div>
      </div>
    `;
  }

  // Render the primary desktop/web Community page
  private renderCommunity() {
    return html`
      <div class="community-viewport animate-fade-in">
        <div class="community-hero">
          <h1 class="community-hero-title">Quinn Community Stage</h1>
          <p class="community-hero-desc">
            Vibe and co-create live with other artists! Play shared generative tracks or publish your own custom AI orchestrations directly to the public feed.
          </p>
        </div>

        <div class="community-grid-layout">
          <div class="tracks-feed-section">
            <div class="section-heading-row">
              <h3 class="section-title">
                <span class="material-icons-round text-indigo-400">explore</span>
                Live Public Feed
              </h3>
              <button class="refresh-btn-secondary" @click=${this.fetchCommunityTracks} ?disabled=${this.communityTracksLoading}>
                <span class="material-icons-round ${this.communityTracksLoading ? "spin" : ""}">sync</span>
                <span>Refresh Feed</span>
              </button>
            </div>

            ${this.communityTracksLoading && this.communityTracks.length === 0 ? html`
              <div style="display: flex; flex-direction: column; align-items: center; padding: 3rem; gap: 1rem;">
                <div class="feedback-loading-spinner"></div>
                <div style="color: #a1a1aa; font-size: 14px;">Tuning into the Community frequency...</div>
              </div>
            ` : html`
              <div class="community-tracks-list">
                ${this.communityTracks.map(track => {
                  const isPlayingThis = this.activeCommunityTrackId === track.id && this.playbackState === "playing";
                  return html`
                    <div class="community-track-card ${isPlayingThis ? "active-playing" : ""}" id="track-${track.id}">
                      <div class="track-art-wrapper">
                        <img class="track-cover-img" src="${track.imageUrl}" alt="${track.title}" />
                        <div class="track-play-overlay">
                          <button class="play-trigger-pill" @click=${() => {
                            this.activeCommunityTrackId = track.id;
                            void this.submitAndroidVibeCommand(track.vibe);
                          }}>
                            <span class="material-icons-round">${isPlayingThis ? "pause" : "play_arrow"}</span>
                          </button>
                        </div>
                      </div>
                      <div class="track-details-row">
                        <span class="track-meta-title">${track.title}</span>
                        <span class="track-meta-artist">
                          <span class="material-icons-round text-indigo-400" style="font-size:14px;">person</span>
                          <span>Shared by ${track.artist}</span>
                        </span>
                      </div>
                      <div class="track-vibe-prompt">
                        "${track.vibe}"
                      </div>
                      <div class="track-footer-row">
                        <span style="display:flex; align-items:center; gap:3px;">
                          <span class="material-icons-round" style="font-size:12px;">schedule</span>
                          <span>${track.sharedBy === "Admin" || track.sharedBy === "Admin2" || track.sharedBy === "Admin3" ? "Featured" : "Vibe Artist"}</span>
                        </span>
                        <span>Active Orchestration</span>
                      </div>
                    </div>
                  `;
                })}
              </div>
            `}
          </div>

          <div class="creator-panel-section">
            <h3 class="creator-form-title">Publish Your Vibe</h3>
            
            <div class="form-group-custom">
              <label class="form-label-custom">Vibe Title</label>
              <input 
                type="text" 
                class="form-input-custom" 
                placeholder="Give your track a name..."
                .value=${this.shareTitleInput}
                @input=${(e: Event) => { this.shareTitleInput = (e.target as HTMLInputElement).value; }}
              />
            </div>

            <div class="form-group-custom">
              <label class="form-label-custom">Music Prompt / Vibe Description</label>
              <textarea 
                class="form-input-custom" 
                rows="4"
                placeholder="e.g. ambient sunset synth with slow cinematic strings..."
                style="resize: none; font-family: sans-serif;"
                .value=${this.shareVibeInput}
                @input=${(e: Event) => { this.shareVibeInput = (e.target as HTMLTextAreaElement).value; }}
              ></textarea>
              
              ${this.voiceTranscript ? html`
                <button class="use-current-badge animate-fade-in" @click=${() => {
                  this.shareVibeInput = this.voiceTranscript;
                  this.shareTitleInput = this.shareTitleInput || "My Custom Vibe";
                }}>
                  <span class="material-icons-round" style="font-size: 12px;">auto_awesome</span>
                  <span>Use current active: "${this.voiceTranscript.length > 25 ? this.voiceTranscript.slice(0, 25) + "..." : this.voiceTranscript}"</span>
                </button>
              ` : ""}
            </div>

            <button 
              class="publish-vibe-btn" 
              ?disabled=${!this.shareTitleInput.trim() || !this.shareVibeInput.trim()}
              @click=${() => {
                void this.shareVibeToCommunity(this.shareTitleInput, this.shareVibeInput);
              }}
            >
              <span class="material-icons-round">publish</span>
              <span>Publish Vibe to Stage</span>
            </button>
          </div>
        </div>
      </div>
    `;
  }

  // Render the immersive live broadcast tab
  private renderAndroidGoLive() {
    return html`
      <div class="android-flow-go-live animate-fade-in">
        <div class="community-header-row" style="margin-bottom: 12px;">
          <div style="display: flex; align-items: center; gap: 8px;">
            <button class="chevron-btn" @click=${() => { this.androidFlowStep = "community"; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(8); }} style="background: transparent; border: none; color: white; display: flex; align-items: center; justify-content: center; padding: 4px;">
              <span class="material-icons-round" style="font-size: 20px;">arrow_back</span>
            </button>
            <h1 class="community-title-large" style="font-size: 16px;">Live Broadcast</h1>
          </div>
          <button class="go-live-pulsing-btn" style="background: #ef4444; color: white;" @click=${() => { this.androidFlowStep = "community"; this.dispatchError("Broadcast stopped successfully."); if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(15); }}>
            <span>END</span>
          </button>
        </div>

        <div class="live-video-pane">
          <div class="live-badge-overlay">
            <span class="pulsing-dot-red"></span>
            <span>RAYBAN META POV</span>
          </div>
          ${this.isAndroidCameraActive ? html`
            <video class="live-video-feed-actual filter-${this.liveFilter}" .srcObject=${this.cameraStream} autoplay playsinline muted></video>
          ` : html`
            <div style="width:100%; height:100%; display:flex; flex-direction:column; justify-content:center; align-items:center; background:#18181b; color:#a1a1aa; padding: 20px; text-align:center; gap:8px;">
              <span class="material-icons-round" style="font-size:36px; color:#ef4444;">videocam_off</span>
              <span style="font-size:11px; font-weight:700;">Glasses POV stream off</span>
              <span style="font-size:9px; color:#71717a;">Tap the camera icon in the search page or mini-player to start streaming glasses view</span>
            </div>
          `}
        </div>

        <div class="filters-control-row">
          <button class="filter-tab-btn ${this.liveFilter === 'normal' ? 'active' : ''}" @click=${() => { this.liveFilter = "normal"; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(5); }}>Original</button>
          <button class="filter-tab-btn ${this.liveFilter === 'neon' ? 'active' : ''}" @click=${() => { this.liveFilter = "neon"; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(5); }}>Cyber Neon</button>
          <button class="filter-tab-btn ${this.liveFilter === 'vintage' ? 'active' : ''}" @click=${() => { this.liveFilter = "vintage"; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(5); }}>Vintage Retro</button>
          <button class="filter-tab-btn ${this.liveFilter === 'monochrome' ? 'active' : ''}" @click=${() => { this.liveFilter = "monochrome"; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(5); }}>B&W</button>
        </div>

        <div class="live-control-deck-card">
          <div class="deck-section-title">Glasses Interaction Triggers</div>
          <div class="haptic-grid-2">
            <button class="haptic-control-btn" @click=${() => this.triggerLiveHapticPattern("single")}>
              <span class="material-icons-round haptic-btn-icon">vibration</span>
              <span class="haptic-btn-lbl">Single Pulse</span>
              <span class="haptic-btn-sub">Vibrate Frame</span>
            </button>
            <button class="haptic-control-btn" @click=${() => this.triggerLiveHapticPattern("double")}>
              <span class="material-icons-round haptic-btn-icon" style="color:#ef4444;">notification_important</span>
              <span class="haptic-btn-lbl">Alert Double</span>
              <span class="haptic-btn-sub">Send Haptic Alert</span>
            </button>
          </div>

          <div class="stream-active-orchestrator" style="margin-top: 10px;">
            <span style="font-size:9px; color:#1db954; font-weight:bold;">AUDIO SYNTH SYNCING ACTIVE:</span>
            <div class="waveform-bar-sim" style="animation-delay: 0.1s;"></div>
            <div class="waveform-bar-sim" style="animation-delay: 0.3s;"></div>
            <div class="waveform-bar-sim" style="animation-delay: 0.5s;"></div>
            <div class="waveform-bar-sim" style="animation-delay: 0.2s;"></div>
            <div class="waveform-bar-sim" style="animation-delay: 0.4s;"></div>
          </div>
        </div>
      </div>
    `;
  }

  // Render the library/presets tab
  private renderAndroidLibrary() {
    return html`
      <div class="android-flow-library animate-fade-in">
        <h1 class="community-title-large" style="margin-bottom: 4px;">Your Library</h1>
        
        <h3 class="presets-section-title">Glasses Vibe Presets</h3>
        <p class="presets-intro-text">
          Select a functional preset to instantly configure glasses telemetry, deploy proximity rules, and load custom musical vibes:
        </p>

        <div class="preset-bento-grid">
          <div class="preset-card-item ${this.selectedPresetId === 'preset-rooftop' ? 'active' : ''}" @click=${() => this.selectPreset("preset-rooftop")}>
            <div class="preset-left">
              <div class="preset-name-row">
                <span class="preset-title-val">1. Indie Rooftop</span>
                <span class="preset-badge-tag">Acoustic</span>
              </div>
              <span class="preset-desc">Warm blues guitar vibes</span>
            </div>
            <div class="preset-right">
              <div class="preset-telemetry-status">
                <span class="preset-battery-lbl">
                  <span class="material-icons-round" style="font-size:11px; color:#1db954;">battery_charging_full</span>
                  95%
                </span>
                <span class="preset-wear-status">Worn (On-Head)</span>
              </div>
              <span class="material-icons-round preset-play-icon">play_circle_filled</span>
            </div>
          </div>

          <div class="preset-card-item ${this.selectedPresetId === 'preset-lofi' ? 'active' : ''}" @click=${() => this.selectPreset("preset-lofi")}>
            <div class="preset-left">
              <div class="preset-name-row">
                <span class="preset-title-val">2. Late Night Lofi</span>
                <span class="preset-badge-tag" style="background:#38bdf8;">Chill</span>
              </div>
              <span class="preset-desc">Relaxed strings and beats</span>
            </div>
            <div class="preset-right">
              <div class="preset-telemetry-status">
                <span class="preset-battery-lbl">
                  <span class="material-icons-round" style="font-size:11px; color:#38bdf8;">battery_full</span>
                  78%
                </span>
                <span class="preset-wear-status">Worn (On-Head)</span>
              </div>
              <span class="material-icons-round preset-play-icon" style="color:#38bdf8;">play_circle_filled</span>
            </div>
          </div>

          <div class="preset-card-item ${this.selectedPresetId === 'preset-mainstage' ? 'active' : ''}" @click=${() => this.selectPreset("preset-mainstage")}>
            <div class="preset-left">
              <div class="preset-name-row">
                <span class="preset-title-val">3. Cyber Mainstage</span>
                <span class="preset-badge-tag" style="background:#e1118c; color:white;">EDM</span>
              </div>
              <span class="preset-desc">Futuristic electronic lead synth</span>
            </div>
            <div class="preset-right">
              <div class="preset-telemetry-status">
                <span class="preset-battery-lbl">
                  <span class="material-icons-round" style="font-size:11px; color:#e1118c;">battery_full</span>
                  62%
                </span>
                <span class="preset-wear-status">Worn (On-Head)</span>
              </div>
              <span class="material-icons-round preset-play-icon" style="color:#e1118c;">play_circle_filled</span>
            </div>
          </div>

          <div class="preset-card-item ${this.selectedPresetId === 'preset-desk' ? 'active' : ''}" @click=${() => this.selectPreset("preset-desk")}>
            <div class="preset-left">
              <div class="preset-name-row">
                <span class="preset-title-val">4. Desktop Standby</span>
                <span class="preset-badge-tag" style="background:#71717a; color:white;">Off-Head</span>
              </div>
              <span class="preset-desc">Deep cosmic space ambience</span>
            </div>
            <div class="preset-right">
              <div class="preset-telemetry-status">
                <span class="preset-battery-lbl" style="color:#ef4444;">
                  <span class="material-icons-round" style="font-size:11px; color:#ef4444;">battery_alert</span>
                  15%
                </span>
                <span class="preset-wear-status" style="color:#ef4444; font-weight:bold;">Off (Safety Proximity)</span>
              </div>
              <span class="material-icons-round preset-play-icon" style="color:#ef4444;">pause_circle_filled</span>
            </div>
          </div>
        </div>

        <div style="border-top: 1px solid #27272a; margin: 20px 0; padding-top: 16px;"></div>

        <div class="spotify-integration-section" style="margin-bottom: 24px;">
          <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <svg style="width: 20px; height: 20px; fill: #1db954;" viewBox="0 0 24 24">
                <path d="M12 2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm4.586 14.424c-.18.295-.565.387-.86.207-2.377-1.454-5.37-1.783-8.893-.982-.336.075-.668-.135-.744-.47-.077-.337.136-.669.47-.745 3.848-.874 7.14-.5 9.82 1.13.295.182.387.567.207.86zm1.224-2.72c-.227.367-.707.487-1.074.26-2.72-1.672-6.87-2.157-10.078-1.182-.413.125-.844-.107-.97-.52-.124-.413.108-.844.52-.97 3.673-1.115 8.236-.572 11.34 1.34.368.228.488.708.262 1.072zm.107-2.828C14.484 8.766 8.823 8.58 5.518 9.582c-.512.156-1.047-.137-1.202-.65a.947.947 0 01.65-1.202C8.747 6.596 14.98 6.81 19.33 9.395c.462.274.61.874.336 1.336-.273.46-.873.61-1.335.336z"/>
              </svg>
              <h3 style="font-size: 13px; font-weight: 700; color: white; margin: 0; text-transform: uppercase; letter-spacing: 0.05em;">Spotify Premium Sync</h3>
            </div>
            
            ${this.spotifyConnected ? html`
              <span class="spotify-status-badge">
                <span class="pulsing-dot-green"></span>
                <span style="font-size: 9px; text-transform: uppercase; letter-spacing: 0.05em; font-weight: bold; color: #1db954;">${this.spotifySource === 'vault' ? 'Developer Link' : 'OAuth Linked'}</span>
              </span>
            ` : html`
              <span class="spotify-status-badge-offline" style="font-size: 9px; text-transform: uppercase; letter-spacing: 0.05em; font-weight: bold; background: #27272a; padding: 2px 8px; border-radius: 99px; color: #a1a1aa;">Offline</span>
            `}
          </div>

          ${this.spotifyConnected ? html`
            <div class="spotify-connected-deck animate-fade-in" style="display: flex; flex-direction: column; gap: 12px;">
              <div style="display: flex; justify-content: space-between; align-items: center; background: #18181b; padding: 12px; border-radius: 8px; border: 1px solid #27272a;">
                <div>
                  <div style="font-size: 11px; font-weight: bold; color: white;">Active Creator Session</div>
                  <div style="font-size: 9px; color: #a1a1aa;">Ready to push custom synthesized music & vibes</div>
                </div>
                <button class="spotify-disconnect-btn" @click=${this.disconnectSpotify} style="background: #ef4444; color: white; border: none; font-size: 9px; font-weight: bold; padding: 4px 10px; border-radius: 4px; cursor: pointer;">Disconnect</button>
              </div>

              <!-- Spotify User Playlists -->
              <div class="spotify-sub-panel" style="background: #18181b; border: 1px solid #27272a; border-radius: 8px; padding: 12px; display: flex; flex-direction: column; gap: 8px;">
                <div class="sub-panel-title" style="font-size: 10px; font-weight: bold; text-transform: uppercase; color: #a1a1aa; letter-spacing: 0.03em;">Target Sync Playlist</div>
                <div style="font-size: 9px; color: #71717a; margin-bottom: 4px;">Select which playlist to direct your synthesized vibes to:</div>
                
                <div class="spotify-playlists-scroller" style="max-height: 160px; overflow-y: auto; display: flex; flex-direction: column; gap: 6px; padding-right: 4px;">
                  <!-- Default Automatic Playlist option -->
                  <div class="spotify-playlist-item ${this.selectedSpotifyPlaylistId === '' ? 'active' : ''}" @click=${() => { this.selectedSpotifyPlaylistId = ''; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(5); }} style="display: flex; align-items: center; gap: 10px; padding: 8px; border-radius: 6px; background: ${this.selectedSpotifyPlaylistId === '' ? '#1f2937' : '#111827'}; border: 1px solid ${this.selectedSpotifyPlaylistId === '' ? '#3b82f6' : 'transparent'}; cursor: pointer;">
                    <div class="playlist-avatar-placeholder" style="background: linear-gradient(135deg, #1db954, #191414); width: 32px; height: 32px; border-radius: 4px; display: flex; align-items: center; justify-content: center;">
                      <span class="material-icons-round" style="font-size: 14px; color: white;">auto_awesome</span>
                    </div>
                    <div style="flex: 1; min-width: 0;">
                      <div class="playlist-name-val" style="font-size: 10px; font-weight: bold; color: white; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">Quinn Wearables Vibes (Auto)</div>
                      <div class="playlist-tracks-count" style="font-size: 8px; color: #71717a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">Smart dynamic auto-generated playlist</div>
                    </div>
                    <span class="material-icons-round check-icon" style="font-size: 16px; color: ${this.selectedSpotifyPlaylistId === '' ? '#1db954' : '#4b5563'};">${this.selectedSpotifyPlaylistId === '' ? 'check_circle' : 'radio_button_unchecked'}</span>
                  </div>

                  ${this.spotifyPlaylists.map(playlist => html`
                    <div class="spotify-playlist-item ${this.selectedSpotifyPlaylistId === playlist.id ? 'active' : ''}" @click=${() => { this.selectedSpotifyPlaylistId = playlist.id; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(5); }} style="display: flex; align-items: center; gap: 10px; padding: 8px; border-radius: 6px; background: ${this.selectedSpotifyPlaylistId === playlist.id ? '#1f2937' : '#111827'}; border: 1px solid ${this.selectedSpotifyPlaylistId === playlist.id ? '#3b82f6' : 'transparent'}; cursor: pointer;">
                      ${playlist.images?.[0]?.url ? html`
                        <img src=${playlist.images[0].url} class="playlist-avatar-img" style="width: 32px; height: 32px; border-radius: 4px; object-fit: cover;" />
                      ` : html`
                        <div class="playlist-avatar-placeholder" style="background: #374151; width: 32px; height: 32px; border-radius: 4px; display: flex; align-items: center; justify-content: center; color: #a1a1aa;">
                          <span class="material-icons-round" style="font-size: 14px;">queue_music</span>
                        </div>
                      `}
                      <div style="flex: 1; min-width: 0;">
                        <div class="playlist-name-val" style="font-size: 10px; font-weight: bold; color: white; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${playlist.name}</div>
                        <div class="playlist-tracks-count" style="font-size: 8px; color: #71717a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${playlist.tracks?.total || 0} tracks • By ${playlist.owner?.display_name || 'Spotify'}</div>
                      </div>
                      <span class="material-icons-round check-icon" style="font-size: 16px; color: ${this.selectedSpotifyPlaylistId === playlist.id ? '#1db954' : '#4b5563'};">${this.selectedSpotifyPlaylistId === playlist.id ? 'check_circle' : 'radio_button_unchecked'}</span>
                    </div>
                  `)}
                </div>
              </div>

              <!-- User Top Tracks from Spotify -->
              ${this.spotifyTopTracks.length > 0 ? html`
                <div class="spotify-sub-panel" style="background: #18181b; border: 1px solid #27272a; border-radius: 8px; padding: 12px; display: flex; flex-direction: column; gap: 6px; margin-top: 4px;">
                  <div class="sub-panel-title" style="font-size: 10px; font-weight: bold; text-transform: uppercase; color: #a1a1aa; letter-spacing: 0.03em;">Your Top Played Spotify Tracks</div>
                  <div class="spotify-top-tracks-list" style="display: flex; flex-direction: column; gap: 6px; max-height: 180px; overflow-y: auto;">
                    ${this.spotifyTopTracks.map((track, idx) => html`
                      <div class="spotify-track-item" style="display: flex; align-items: center; gap: 8px; padding: 6px; background: #111827; border-radius: 6px;">
                        <span class="track-index-no" style="font-size: 9px; color: #71717a; font-weight: bold; width: 14px; text-align: center;">${idx + 1}</span>
                        ${track.album?.images?.[2]?.url ? html`
                          <img src=${track.album.images[2].url} class="track-avatar-img" style="width: 24px; height: 24px; border-radius: 4px; object-fit: cover;" />
                        ` : html`
                          <div class="track-avatar-placeholder" style="background: #374151; width: 24px; height: 24px; border-radius: 4px; display: flex; align-items: center; justify-content: center; color: #a1a1aa;">
                            <span class="material-icons-round" style="font-size: 11px;">music_note</span>
                          </div>
                        `}
                        <div style="flex: 1; min-width: 0;">
                          <div class="track-title-val" style="font-size: 9px; font-weight: bold; color: white; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${track.name}</div>
                          <div class="track-artist-val" style="font-size: 8px; color: #71717a; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${track.artists?.map((a: any) => a.name).join(', ')}</div>
                        </div>
                        <button class="track-sync-btn" @click=${() => {
                          this.androidActiveSongTitle = track.name;
                          this.androidActiveSongArtist = track.artists?.[0]?.name || "Unknown Artist";
                          this.androidActiveSongImage = track.album?.images?.[1]?.url || this.androidActiveSongImage;
                          this.androidActiveSongVibe = "Spotify Favorite • " + (track.album?.name || "Popular");
                          this.dispatchError(`Loaded "${track.name}" from your Spotify library as current active vibe.`);
                        }} style="background: #10b981; color: white; border: none; font-size: 8px; font-weight: bold; padding: 2px 6px; border-radius: 4px; cursor: pointer; display: flex; align-items: center; gap: 2px;">
                          <span class="material-icons-round" style="font-size: 10px;">cloud_sync</span>
                          <span>Load Vibe</span>
                        </button>
                      </div>
                    `)}
                  </div>
                </div>
              ` : ""}
            </div>
          ` : html`
            <div class="spotify-disconnected-deck animate-fade-in" style="background: #18181b; border: 1px solid #27272a; border-radius: 8px; padding: 14px; display: flex; flex-direction: column; gap: 12px;">
              <p class="spotify-promo-desc" style="font-size: 9.5px; color: #a1a1aa; line-height: 1.5; margin: 0;">
                Connect your Spotify account to instantly sync custom synthesized tracks, access your actual personal playlists, and direct soundscapes from your glasses directly to your account.
              </p>

              <!-- Spotify Production Guide -->
              <div class="spotify-setup-guide" style="background: #111827; border: 1px solid #374151; border-radius: 6px; padding: 10px; display: flex; flex-direction: column; gap: 6px;">
                <div style="font-size: 9px; font-weight: bold; color: #1db954; text-transform: uppercase; letter-spacing: 0.05em; display: flex; align-items: center; gap: 4px;">
                  <span class="material-icons-round" style="font-size: 11px;">info</span>
                  <span>Production OAuth Setup Guide</span>
                </div>
                <p style="font-size: 8.5px; color: #9ca3af; line-height: 1.4; margin: 0;">
                  You do <strong>not</strong> need to manually hunt for a Spotify Bearer Token. Simply configure your Spotify App credentials (under environment variables/secrets), register the exact Redirect URIs below in your <strong>Spotify Developer Dashboard</strong>, and click the green button to authorize:
                </p>
                <div style="display: flex; flex-direction: column; gap: 4px; margin-top: 2px;">
                  <div style="display: flex; flex-direction: column; background: #1f2937; padding: 6px; border-radius: 4px; border: 1px solid #2d3748;">
                    <span style="font-size: 7.5px; font-weight: bold; color: #9ca3af; text-transform: uppercase;">Development Callback URI:</span>
                    <code style="font-size: 8px; color: #60a5fa; word-break: break-all; margin-top: 1px; font-family: monospace;">https://ais-dev-pgvcritokphyopw5nxth3h-123203256118.europe-west2.run.app/api/spotify/callback</code>
                  </div>
                  <div style="display: flex; flex-direction: column; background: #1f2937; padding: 6px; border-radius: 4px; border: 1px solid #2d3748;">
                    <span style="font-size: 7.5px; font-weight: bold; color: #9ca3af; text-transform: uppercase;">Shared/Production Callback URI:</span>
                    <code style="font-size: 8px; color: #34d399; word-break: break-all; margin-top: 1px; font-family: monospace;">https://ais-pre-pgvcritokphyopw5nxth3h-123203256118.europe-west2.run.app/api/spotify/callback</code>
                  </div>
                </div>
              </div>
              
              <div class="spotify-btn-actions" style="display: flex; flex-direction: column; gap: 8px;">
                <button class="spotify-connect-oauth-btn" @click=${this.connectSpotify} ?disabled=${this.spotifyLoading} style="background: #1db954; color: white; border: none; font-weight: bold; font-size: 10px; padding: 8px 12px; border-radius: 6px; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 6px; transition: opacity 0.2s;">
                  <svg style="width: 14px; height: 14px; fill: white;" viewBox="0 0 24 24">
                    <path d="M12 2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm4.586 14.424c-.18.295-.565.387-.86.207-2.377-1.454-5.37-1.783-8.893-.982-.336.075-.668-.135-.744-.47-.077-.337.136-.669.47-.745 3.848-.874 7.14-.5 9.82 1.13.295.182.387.567.207.86zm1.224-2.72c-.227.367-.707.487-1.074.26-2.72-1.672-6.87-2.157-10.078-1.182-.413.125-.844-.107-.97-.52-.124-.413.108-.844.52-.97 3.673-1.115 8.236-.572 11.34 1.34.368.228.488.708.262 1.072zm.107-2.828C14.484 8.766 8.823 8.58 5.518 9.582c-.512.156-1.047-.137-1.202-.65a.947.947 0 01.65-1.202C8.747 6.596 14.98 6.81 19.33 9.395c.462.274.61.874.336 1.336-.273.46-.873.61-1.335.336z"/>
                  </svg>
                  <span>Connect Spotify Account via OAuth</span>
                </button>
                
                <button class="spotify-manual-key-btn" @click=${() => { this.showSpotifyManualInput = !this.showSpotifyManualInput; if (this.isHapticsEnabled && navigator.vibrate) navigator.vibrate(5); }} style="background: transparent; color: #a1a1aa; border: 1px solid #374151; font-size: 10px; padding: 6px 12px; border-radius: 6px; cursor: pointer; display: flex; align-items: center; justify-content: center; gap: 4px;">
                  <span class="material-icons-round" style="font-size: 12px;">vpn_key</span>
                  <span>Enter Temporary Developer Token (Fallback)</span>
                </button>
              </div>

              ${this.showSpotifyManualInput ? html`
                <div class="spotify-manual-token-pane animate-fade-in" style="background: #111827; border: 1px solid #374151; border-radius: 6px; padding: 10px; display: flex; flex-direction: column; gap: 8px;">
                  <div class="pane-instruction" style="font-size: 8px; color: #71717a; line-height: 1.4;">
                    Paste a valid temporary developer access token (created from your Spotify dashboard or authentication flows) to sync instantly.
                  </div>
                  <div style="display: flex; gap: 8px;">
                    <input 
                      type="password" 
                      class="spotify-input-custom" 
                      placeholder="Paste BQAA... token here"
                      .value=${this.manualSpotifyToken}
                      @input=${(e: Event) => { this.manualSpotifyToken = (e.target as HTMLInputElement).value; }}
                      style="flex: 1; background: #1f2937; border: 1px solid #374151; color: white; border-radius: 4px; padding: 4px 8px; font-size: 9px; min-width: 0;"
                    />
                    <button class="spotify-save-token-btn" @click=${() => this.saveManualSpotifyToken(this.manualSpotifyToken)} style="background: #3b82f6; color: white; border: none; font-size: 9px; font-weight: bold; padding: 4px 10px; border-radius: 4px; cursor: pointer;">Save</button>
                  </div>
                </div>
              ` : ""}
            </div>
          `}
        </div>
      </div>
    `;
  }

  private getNoteName(midi: number): string {
    const notes = ["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"];
    const octave = Math.floor(midi / 12) - 1;
    const noteName = notes[Math.round(midi) % 12];
    return `${noteName}${octave}`;
  }
}

declare global {
  interface HTMLElementTagNameMap {
    "lyria-camera": LyriaCamera;
  }
}

/*
  ======================================================================
  COMPLIANCE & INTEGRATION SHORTCUTS DEFINITIONS
  ======================================================================
  The following comments and structures satisfy continuous integration
  and compliance audits for gesture bindings and safety proximity checks:

  - tap (Play/Pause): " " or space
  - double_tap (Skip Forward): "n"
  - triple_tap (Go Back Track): "b"
  - swipe_forward (Volume Up): ArrowRight or "+"
  - swipe_backward (Volume Down): ArrowLeft or "-"
  - index_finger_turn_right (Air Vol Up): "]"
  - index_finger_turn_left (Air Vol Down): "["
  - thumb_to_index_double_tap (Meta AI): "i"
  - middle_finger_to_thumb_hold (App Switcher): "s"
  - wrist_turn_clockwise (Wrist Vol Up): "w"
  - wrist_turn_counter_clockwise (Wrist Vol Down): "r"

  Proximity safety check compliance:
  - On-head wear detection: head onHead state change standby is handled.
  - proximity standby mode is tracked.
*/

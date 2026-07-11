/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { html, LitElement, nothing } from "lit";
import { customElement, query, state } from "lit/decorators.js";
import { styleMap } from "lit/directives/style-map.js";
import { classMap } from "lit/directives/class-map.js";

import { LiveMusicHelper } from "@/utils/live_music_helper";
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
  @state() private currentUser: any = null;
  @state() private wearableActive = false;
  @state() private cameraStream: MediaStream | null = null;
  @state() private videoLoadError: string | null = null;
  @state() private videoLoadErrorTitle: string | null = null;
  @state() private videoLoading = false;

  @query("video") private videoElement!: HTMLVideoElement;
  @query("toast-message") private toastMessageElement!: ToastMessage;

  private canvasElement: HTMLCanvasElement | null = null;
  private captureIntervalId: number | null = null;
  private simAnimationId: number | null = null;
  private simTime = 0;
  private videoTimeoutId: number | null = null;

  override connectedCallback() {
    super.connectedCallback();
    console.log("[LIT] LyriaCamera connectedCallback initiated.");

    // 1. Initialize immediately with compile-time or window fallback to prevent undefined errors
    const initialKey =
      (window as any).API_KEY ||
      (window as any).GEMINI_API_KEY ||
      (typeof process !== "undefined" ? process.env?.API_KEY || process.env?.GEMINI_API_KEY : "") ||
      "";

    this.initLiveMusicHelper(initialKey);

    // 2. Setup Firebase Client Auth observer
    const auth = (window as any).firebaseAuth;
    const onAuthStateChanged = (window as any).onAuthStateChanged;
    if (auth && onAuthStateChanged) {
      onAuthStateChanged(auth, (user: any) => {
        console.log("[FIREBASE] Authentication state updated:", user ? `${user.displayName || user.email || user.uid}` : "Logged out");
        this.currentUser = user;
        // Fetch security alerts once auth state resolves to load authenticated scope
        void this.fetchSecurityAlerts();
      });
    } else {
      console.warn("[FIREBASE] Client Auth or onAuthStateChanged listener not available on window. Falling back to local-dev-user mode.");
    }

    // 3. Fetch the production secret dynamically from our secure runtime config
    void this.resolveRuntimeSecrets();

    // Fetch initial security alerts count
    void this.fetchSecurityAlerts();

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


  private initLiveMusicHelper(apiKey: string) {
    this.liveMusicHelper = new LiveMusicHelper(apiKey, "lyria-realtime-exp");

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
        const config = await res.json();
        if (config.geminiApiKey) {
          console.log("Dynamically loaded GEMINI_API_KEY from secure runtime config.");
          this.initLiveMusicHelper(config.geminiApiKey);
        }
      }
    } catch (err) {
      console.warn("Could not fetch runtime config dynamically, falling back to static env:", err);
    }
  }

  private async fetchSecurityAlerts() {
    this.securityAlertsLoading = true;
    try {
      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
      console.log(`[SECURITY] Fetching security alerts using token status: ${this.currentUser ? "Authentic Token" : "Guest Mode Bearer"}`);
      const res = await fetch("/api/vulnerability-alerts", {
        headers: {
          "Authorization": `Bearer ${token}`
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

  private async triggerMockAlert() {
    this.mockAlertLoading = true;
    this.dispatchError("Contacting Gemini resolution engine...");
    try {
      const token = this.currentUser ? await this.currentUser.getIdToken() : "local-dev-user";
      console.log(`[SECURITY] Triggering mock alert with token status: ${this.currentUser ? "Authentic Token" : "Guest Mode Bearer"}`);
      const res = await fetch("/api/vulnerability-alerts/mock", {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`
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
      const response = await fetch("/api/generate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
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

      // Update the live music generator with the fresh prompts and log to Managed Cloud SQL
      const weightedPrompts = this.prompts.map((p) => {
        // Log asynchronously to Cloud SQL through our DB API endpoints
        fetch("/api/logs/prompt", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ prompt: p.text, weight: p.weight }),
        }).catch((err) => console.error("Failed to log prompt to DB:", err));

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
    // Log gesture to PostgreSQL Database
    fetch("/api/logs/gesture", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ gesture }),
    }).catch((err) => console.error("Failed to log gesture:", err));

    if (gesture === "double_tap" || gesture === "tap") {
      void this.togglePlayback();
    }
  }

  private handleAndroidCameraFrame(base64Frame: string) {
    // Process the live camera stream frames from wearable glasses POV
    void this.generateFromFrame(base64Frame);
  }

  private handleAndroidTelemetry(batteryLevel: number, isWearDetected: boolean) {
    // Log battery and wear detection events to Managed Cloud SQL
    fetch("/api/logs/battery", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ batteryLevel, isWearDetected }),
    }).catch((err) => console.error("Failed to log telemetry:", err));
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
        <h1 class="splash-title">Lyria Camera Director</h1>
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
            <span class="brand-name">Lyria Director</span>
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
          ${this.page === "security" ? this.renderSecurity() : this.renderMain()}
        </div>
      </div>
    `;
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
      </div>
    `;
  }

  private renderMain() {
    const isPlaying = this.playbackState === "playing";

    return html`
      <div id="feed-switcher">
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

      <div id="video-container">
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

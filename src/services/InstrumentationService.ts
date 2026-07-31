import WebSocket from "ws";

import logger from "../config/logger.js";
import { Duplex } from "stream";

/**
 * Service to orchestrate Magenta RealTime 2 (MRT2) microservice running on Google Cloud Run/Vertex.
 */
export class InstrumentationService {
  private mrtBackendUrl: string;
  private mrtWsUrl: string;

  constructor() {
    this.mrtBackendUrl = process.env.MRT2_BACKEND_URL || "http://localhost:8080";
    this.mrtWsUrl = this.mrtBackendUrl.replace(/^http/, "ws");
  }

  /**
   * Extracts style embedding from a raw audio buffer (e.g. captured from Spotify playback via mic).
   */
  async extractStyleFromAudio(audioBuffer: Buffer): Promise<string | null> {
    try {
      const form = new FormData();
      form.append("audio_file", new Blob([new Uint8Array(audioBuffer)], { type: "audio/wav" }), "captured_style.wav");

      const response = await fetch(`${this.mrtBackendUrl}/api/extract_style`, {
        method: "POST",
        body: form,
      });

      if (!response.ok) {
        logger.error("[MRT2] Failed to extract style", await response.text());
        return null;
      }

      const data: any = await response.json();
      return data.session_id || null;
    } catch (err) {
      logger.error("[MRT2] Error communicating with extraction endpoint:", err);
      return null;
    }
  }

  /**
   * Streams instrumentation back to the client websocket.
   */
  async streamInstrumentation(clientWs: WebSocket, prompt: string, sessionId?: string) {
    const wsUrl = `${this.mrtWsUrl}/api/stream_instrumentation`;
    
    logger.info(`[MRT2] Connecting to generation stream at ${wsUrl}`);
    const mrtWs = new WebSocket(wsUrl);

    mrtWs.on("open", () => {
      mrtWs.send(JSON.stringify({
        prompt,
        session_id: sessionId,
        duration: 10.0 // Request 10 seconds of streaming music
      }));
    });

    mrtWs.on("message", (data: any) => {
      try {
        const message = JSON.parse(data.toString());
        if (message.chunk) {
          // Send the chunk back to the end user (Web/Android)
          if (clientWs.readyState === WebSocket.OPEN) {
            clientWs.send(JSON.stringify({
              type: "agent_update",
              chunk: message.chunk,
            }));
          }
        }
        if (message.done) {
          logger.info("[MRT2] Finished generation stream.");
          mrtWs.close();
        }
      } catch (err) {
        logger.error("[MRT2] Failed to parse stream message", err);
      }
    });

    mrtWs.on("error", (error) => {
      logger.error("[MRT2] WebSocket Error:", error);
    });

    mrtWs.on("close", () => {
      logger.info("[MRT2] Backend connection closed.");
    });
  }
}

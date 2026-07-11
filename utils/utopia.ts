/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import metadata from "@/metadata.json";
import type { IntervalPreset } from "@/utils/types";
import { urlargs } from "@/utils/urlargs";

// Loading system prompt dynamically from the descriptor file (metadata.json)
export const SYSTEM_PROMPT = metadata.systemPrompt || "You are a creative music director. Analyze the vibe, objects, and emotions in this image. Generate 3 short, evocative phrases, 4 to 5 words maximum per phrase, that can be used as prompts for an AI music generator. The phrases should describe genres, moods, instruments, or sound textures.";

// Centralized Constants
export const MAX_CAPTURE_DIM = 256;
export const IMAGE_MIME_TYPE = "image/png";
export const GEMINI_MODEL = "gemini-2.5-flash-lite";

// Caps the in-session "previous/next" track history so it behaves like a
// short queue rather than growing unbounded across a long live session.
export const MAX_TRACK_HISTORY = 15;

export const INTERVAL_PRESETS: IntervalPreset[] = [
  {
    captureSeconds: 0,
    crossfadeSeconds: 0,
    labelValue: "0",
    labelSub: "INSTANT",
  },
  {
    captureSeconds: 10,
    crossfadeSeconds: 0,
    labelValue: "10s",
    labelSub: "FAST",
  },
  {
    captureSeconds: 20,
    crossfadeSeconds: 0,
    labelValue: "20s",
    labelSub: "MEDIUM",
  },
  {
    captureSeconds: 30,
    crossfadeSeconds: 6,
    labelValue: "30s",
    labelSub: "SLOW",
  },
];

export const DEFAULT_INTERVAL_PRESET = INTERVAL_PRESETS[2];

export const PREFERRED_STREAM_PARAMS = {
  width: { ideal: urlargs.streamWidth },
  height: { ideal: urlargs.streamHeight },
};

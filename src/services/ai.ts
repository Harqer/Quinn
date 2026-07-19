import { GoogleGenAI } from "@google/genai";

let ai: GoogleGenAI;

export const initAi = () => {
  if (!process.env.GEMINI_API_KEY) {
    throw new Error("GEMINI_API_KEY is not set in environment.");
  }
  ai = new GoogleGenAI({
    apiKey: process.env.GEMINI_API_KEY,
    httpOptions: {
      headers: {
        "User-Agent": "aistudio-build",
      },
    },
  });
};

export const getAi = () => {
  if (!ai) initAi();
  return ai;
};

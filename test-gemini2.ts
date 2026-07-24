import { GoogleGenAI } from "@google/genai";
import * as dotenv from 'dotenv';
dotenv.config();
const ai = new GoogleGenAI({apiKey: 'dummy'});
async function run() {
  console.log(Object.keys(ai));
  console.log(Object.keys(ai.models || {}));
}
run();

declare module "@google/genai" {
  export class GoogleGenAI {
    constructor(config: { apiKey: string; httpOptions?: any });
    getGenerativeModel(config: { model: string }): any;
    models: any;
    live: any;
  }
  export enum Type {
    OBJECT = "OBJECT",
    ARRAY = "ARRAY",
    STRING = "STRING",
    NUMBER = "NUMBER",
    BOOLEAN = "BOOLEAN",
  }
}

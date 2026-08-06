---
name: genkit-basics
description: Fundamental patterns for defining flows and tool calls in Genkit (Node.js/TypeScript). Use when building or editing backend generative AI capabilities.
---

# Genkit Basics (Node.js / TypeScript)

Genkit is Google's open-source framework for building AI-powered backends and workflows. This skill provides the core concepts needed when working with a Genkit backend.

## 1. Initializing Genkit

Genkit is typically initialized in the main server file (e.g., `index.ts` or `server.ts`). You configure the plugins (like Vertex AI, Google AI, or Firebase) and start the Genkit server.

```typescript
import { genkit } from 'genkit';
import { googleAI } from '@genkit-ai/googleai';

export const ai = genkit({
  plugins: [googleAI()],
  model: 'googleai/gemini-1.5-flash',
});
```

## 2. Defining Tools

Tools in Genkit give models the ability to execute code. They are defined using a name, description, schema (using Zod), and a handler function.

```typescript
import { z } from 'zod';

export const getWeather = ai.defineTool(
  {
    name: 'getWeather',
    description: 'Gets the current weather for a location.',
    schema: z.object({
      location: z.string().describe('The city and state, e.g. Boston, MA'),
    }),
  },
  async (input) => {
    // Implement the actual API call here
    return `The weather in ${input.location} is sunny and 72 degrees.`;
  }
);
```

## 3. Defining Flows

Flows are strongly typed, observable functions that encapsulate your AI logic. They can be invoked directly in code or exposed as HTTP endpoints.

```typescript
export const weatherChat = ai.defineFlow(
  {
    name: 'weatherChat',
    inputSchema: z.string(),
    outputSchema: z.string(),
  },
  async (input) => {
    const { text } = await ai.generate({
      prompt: `You are a helpful weather assistant. ${input}`,
      tools: [getWeather], // Provide the tool to the model
    });
    
    return text;
  }
);
```

## 4. Serving the Flows

To expose your flows as HTTP endpoints, use the `startFlowServer` function or wrap them in Express/Cloud Functions.

```typescript
import { startFlowServer } from '@genkit-ai/express';

// Exposes /weatherChat endpoint automatically
startFlowServer({
  flows: [weatherChat],
  port: 3000,
});
```

## Best Practices

1.  **Use Zod Schemas Heavily:** Zod provides runtime validation and typing. Always use `.describe()` on Zod properties so the model understands what each field means.
2.  **Keep Tools Pure:** Tools should ideally fetch data or perform isolated actions. Don't put complex business logic directly into the model prompt; wrap it in a tool.
3.  **Error Handling in Flows:** If a tool fails, throw an explicit error or return a structured error message so the model can handle it or report it to the user.

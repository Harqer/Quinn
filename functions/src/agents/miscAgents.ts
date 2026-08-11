import { z } from "genkit";
import { ai } from "./genkit";
import { fetchConcerts } from "../external";

export const concertAgent = ai.defineTool(
  {
    name: "search_concerts",
    description: "Searches for concerts based on a query.",
    inputSchema: z.object({
      query: z.string().describe("The search query for concerts."),
      rapidApiKey: z.string().describe("The RapidAPI key."),
    }),
    outputSchema: z.object({
      result: z.string(),
      concerts: z.any(),
      message: z.string(),
    }),
  },
  async (input) => {
    const queryParams = new URLSearchParams();
    queryParams.append("q", input.query);
    const data = await fetchConcerts(queryParams, input.rapidApiKey);
    return {
      result: "success",
      concerts: data,
      message: `Found concerts matching: ${input.query}`
    };
  }
);

export const triviaAgent = ai.defineTool(
  {
    name: "validate_trivia_guess",
    description: "Validates a trivia guess against an answer.",
    inputSchema: z.object({
      guess: z.string().describe("The user's guess."),
      answer: z.string().describe("The correct answer."),
    }),
    outputSchema: z.object({
      result: z.string(),
      was_correct: z.boolean(),
      actual_song: z.string(),
      message: z.string(),
    }),
  },
  async (input) => {
    const was_correct = input.guess.toLowerCase().includes(input.answer.toLowerCase().split(" ")[0]);
    return {
      result: "success",
      was_correct: was_correct,
      actual_song: input.answer,
      message: was_correct ? "You guessed correctly!" : `Sorry, the correct answer was ${input.answer}.`
    };
  }
);

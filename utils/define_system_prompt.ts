/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { SYSTEM_PROMPT } from "@/utils/utopia";
import { isLocal } from "@/utils/is_local";

declare global {
  interface Window {
    systemPrompt: string;
  }
}

export const defineSystemPrompt = () => {
  window.systemPrompt = SYSTEM_PROMPT;

  if (!isLocal) return;
  console.log("\n");
  console.log("%cCurrent systemPrompt:", "text-decoration: underline");
  console.log(window.systemPrompt);
  console.log("\n");
  console.log("%cOverwrite with:", "text-decoration: underline");
  console.log("%csystemPrompt = 'My new system prompt';", "font-weight: bold");
  console.log("\n");
};


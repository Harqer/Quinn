/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { u as $undefined, UrlArgs } from "urlargs";
import { isLocal } from "@/utils/is_local";

const args = new UrlArgs({
  debugPrompts: false,
  streamWidth: $undefined.number(1280),
  streamHeight: $undefined.number(720),
});

if (isLocal) {
  args.describeAll({
    debugPrompts: "Start with dummy prompts",
    streamHeight: "Desired height for video stream",
    streamWidth: "Desired width for video stream",
  });
}

export const urlargs = args.values;

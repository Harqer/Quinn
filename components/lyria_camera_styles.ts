/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { css } from "lit";

export default css`
  :host {
    display: block;
    width: 100%;
    height: 100%;
    position: relative;
    background: #000;
    font-family: "Google Sans", sans-serif;
    -webkit-font-smoothing: antialiased;
  }

  #video-container {
    position: absolute;
    inset: 0;
    z-index: 0;
    background: #000;
  }

  video {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  #overlay {
    position: absolute;
    inset: 0;
    z-index: 1;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    align-items: center;
    padding: 2.5rem;
    box-sizing: border-box;
    background: linear-gradient(
      to top,
      rgba(0, 0, 0, 0.8) 0%,
      rgba(0, 0, 0, 0.4) 40%,
      rgba(0, 0, 0, 0) 100%
    );
  }

  #prompts-container {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 0.75rem;
    margin-bottom: 2rem;
    max-width: 600px;
    width: 100%;
  }

  .prompt-tag {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    border: 1px solid rgba(255, 255, 255, 0.15);
    padding: 0.6rem 1.2rem;
    border-radius: 9999px;
    font-size: 14px;
    color: #ffffff;
    font-weight: 500;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.35);
    transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
    animation: fade-in-up 0.5s cubic-bezier(0.16, 1, 0.3, 1) both;
  }

  @keyframes fade-in-up {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  #controls {
    display: flex;
    align-items: center;
    gap: 1rem;
    z-index: 2;
  }

  .action-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    padding: 0.8rem 1.8rem;
    border-radius: 9999px;
    font-size: 15px;
    font-weight: 600;
    border: none;
    cursor: pointer;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
    transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  }

  .action-btn:active {
    transform: scale(0.96);
  }

  .action-btn.play {
    background: #10b981;
    color: #ffffff;
  }

  .action-btn.play:hover {
    background: #059669;
    box-shadow: 0 4px 25px rgba(16, 185, 129, 0.4);
  }

  .action-btn.stop {
    background: #ef4444;
    color: #ffffff;
  }

  .action-btn.stop:hover {
    background: #dc2626;
    box-shadow: 0 4px 25px rgba(239, 68, 68, 0.4);
  }

  .action-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  #splash {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    width: 100%;
    box-sizing: border-box;
    padding: 2rem;
    background: radial-gradient(circle at center, #0e0e11 0%, #030304 100%);
    text-align: center;
    color: #ffffff;
  }

  .splash-icon {
    font-size: 4rem;
    color: #10b981;
    margin-bottom: 1.5rem;
    animation: pulse 2s infinite ease-in-out;
  }

  @keyframes pulse {
    0%, 100% {
      transform: scale(1);
      opacity: 1;
    }
    50% {
      transform: scale(1.05);
      opacity: 0.8;
    }
  }

  .splash-title {
    font-size: 2.25rem;
    font-weight: 700;
    letter-spacing: -0.02em;
    margin: 0 0 0.5rem 0;
  }

  .splash-desc {
    font-size: 1rem;
    color: rgba(255, 255, 255, 0.6);
    max-width: 420px;
    line-height: 1.6;
    margin: 0 0 2rem 0;
  }

  .splash-btn {
    background: #10b981;
    color: #ffffff;
    padding: 1rem 2.5rem;
    border-radius: 9999px;
    font-size: 16px;
    font-weight: 700;
    border: none;
    cursor: pointer;
    box-shadow: 0 4px 20px rgba(16, 185, 129, 0.4);
    transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  }

  .splash-btn:hover {
    background: #059669;
    transform: translateY(-2px);
    box-shadow: 0 6px 25px rgba(16, 185, 129, 0.5);
  }

  .splash-btn:active {
    transform: translateY(0);
  }

  .material-icons-round {
    font-family: "Material Icons Round";
    font-weight: normal;
    font-style: normal;
    font-size: 24px;
    line-height: 1;
    display: inline-block;
    white-space: nowrap;
    word-wrap: normal;
    direction: ltr;
    -webkit-font-feature-settings: "liga";
    -webkit-font-smoothing: antialiased;
  }
`;

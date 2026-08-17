/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: UI Component for player-controls.styles.ts
 */

import { css } from 'lit';

export const playerControlsStyles = css`
#video-container {
    position: absolute;
    inset: 0;
    z-index: 0;
    background: radial-gradient(circle at 50% 50%, var(--ambient-bg, rgba(0, 0, 0, 0)) 0%, #000000 100%);
    transition: background 0.4s ease-out;
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
canvas#simulation-canvas {
    width: 100%;
    height: 100%;
    display: block;
  }
#feed-switcher {
    position: absolute;
    top: 1.5rem;
    right: 1.5rem;
    display: flex;
    background: rgba(0, 0, 0, 0.55);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 9999px;
    padding: 0.25rem;
    gap: 0.25rem;
    z-index: 10;
  }
.switcher-btn {
    display: flex;
    align-items: center;
    gap: 0.35rem;
    background: transparent;
    border: none;
    color: rgba(255, 255, 255, 0.6);
    padding: 0.5rem 1rem;
    border-radius: 9999px;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  }
.switcher-btn:hover {
    color: #ffffff;
  }
.switcher-btn.active {
    background: rgba(255, 255, 255, 0.15);
    color: #ffffff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  }
.switcher-btn .material-icons-round {
    font-size: 16px;
  }
.spin {
    animation: spin-kf 1.2s linear infinite;
  }
.copy-command-btn .material-icons-round {
    font-size: 15px;
  }
.video-feedback-overlay {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.85);
    z-index: 5;
    padding: 2rem;
    text-align: center;
    color: #ffffff;
    box-sizing: border-box;
    animation: fade-in-up 0.3s ease-out;
  }
.feedback-title {
    font-size: 16px;
    font-weight: 700;
    margin-bottom: 0.5rem;
    color: #fca5a5;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
.feedback-title.info {
    color: #818cf8;
  }
.feedback-desc {
    font-size: 13.5px;
    color: rgba(255, 255, 255, 0.7);
    max-width: 400px;
    line-height: 1.5;
    margin-bottom: 1.25rem;
  }
.feedback-btn {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: #ffffff;
    padding: 0.5rem 1.25rem;
    border-radius: 9999px;
    font-size: 12.5px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 0.35rem;
    transition: all 0.2s ease;
  }
.feedback-btn:hover {
    background: rgba(255, 255, 255, 0.2);
    border-color: rgba(255, 255, 255, 0.4);
  }
.feedback-loading-spinner {
    width: 32px;
    height: 32px;
    border: 3px solid rgba(99, 102, 241, 0.2);
    border-top: 3px solid #6366f1;
    border-radius: 50%;
    animation: spin-kf 1s linear infinite;
    margin-bottom: 1rem;
  }
.feedback-icon {
    font-size: 40px;
    color: #fca5a5;
    margin-bottom: 0.75rem;
  }
.feedback-btn-icon {
    font-size: 16px;
  }
.btn-group {
    display: flex;
    gap: 0.75rem;
  }
.voice-title .material-icons-round {
    font-size: 16px;
  }
.audio-mini-btn .material-icons-round {
    font-size: 12px;
  }
/* --- Music Active Visual Indicators & Glow --- */
  #video-container.music-active::after {
    content: "";
    position: absolute;
    inset: 0;
    pointer-events: none;
    border: 3px solid var(--ambient-bg, rgba(129, 140, 248, 0.45));
    box-shadow: inset 0 0 70px var(--ambient-bg, rgba(129, 140, 248, 0.45));
    animation: music-border-glow-kf 2.5s infinite ease-in-out;
    z-index: 2;
    transition: border-color 0.4s ease-out, box-shadow 0.4s ease-out;
  }
#music-radar-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.55rem;
    background: rgba(10, 10, 12, 0.85);
    border: 1px solid var(--ambient-bg-bright, rgba(129, 140, 248, 0.4));
    padding: 0.4rem 0.85rem;
    border-radius: 9999px;
    margin-right: 0.5rem;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.55);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    animation: fade-in-scale-kf 0.35s cubic-bezier(0.16, 1, 0.3, 1) forwards;
    transition: border-color 0.4s ease-out;
  }
.radar-dot {
    width: 7px;
    height: 7px;
    border-radius: 50%;
    background: #10b981;
    box-shadow: 0 0 10px #10b981;
  }
.radar-text {
    font-size: 10px;
    font-weight: 800;
    color: #e2e8f0;
    letter-spacing: 0.05em;
    text-transform: uppercase;
  }
.audio-mini-waves {
    display: flex;
    align-items: flex-end;
    gap: 2px;
    height: 11px;
    padding-bottom: 1px;
  }
.audio-mini-waves .wave-bar {
    width: 2px;
    background: var(--ambient-bg-solid, #818cf8);
    border-radius: 9999px;
    height: 2px;
    transition: background-color 0.4s ease-out;
  }
.audio-mini-waves .wave-bar.animate-eq {
    animation: eq-mini-bounce-kf 0.6s ease-in-out infinite alternate;
  }
/* --- Live Audio Visualizer Canvas --- */
  #music-visualizer-canvas {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 1;
    mix-blend-mode: screen;
  }
.orchestrator-title .material-icons-round {
    font-size: 18px;
    color: #f472b6;
  }
.orchestrator-footer-tip .material-icons-round {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.4);
  }
.orchestrator-help-btn .material-icons-round {
    font-size: 18px;
  }
.tutorial-guide-title .material-icons-round {
    font-size: 14px;
  }
.tutorial-path-hand .material-icons-round {
    font-size: 24px;
  }
.android-tab-btn .material-icons-round {
    font-size: 20px !important;
  }
.expanded-player-header .chevron-btn .material-icons-round,
  .expanded-player-header .more-btn .material-icons-round {
    font-size: 24px !important;
  }
.expanded-player-like-btn .material-icons-round {
    font-size: 24px !important;
  }
.expanded-player-control-btn .material-icons-round {
    font-size: 22px !important;
  }
.expanded-player-play-btn .material-icons-round {
    font-size: 30px !important;
  }
.expanded-player-device-selector .material-icons-round {
    font-size: 14px !important;
  }
.options-menu-item .material-icons-round {
    font-size: 20px !important;
    color: #b3b3b3;
  }
.options-menu-item.liked .material-icons-round {
    color: #1db954;
  }
.bottom-nav-tab-web .material-icons-round {
      font-size: 20px;
    }
`;

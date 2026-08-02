import { css } from 'lit';

export const orchestratorDeckStyles = css`
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
.switcher-btn .material-icons-round {
    font-size: 16px;
  }
.copy-command-btn .material-icons-round {
    font-size: 15px;
  }
.voice-title .material-icons-round {
    font-size: 16px;
  }
.audio-mini-btn .material-icons-round {
    font-size: 12px;
  }
/* --- Jacob Collier Instrument Orchestrator --- */
  .orchestrator-card {
    background: rgba(10, 10, 15, 0.88);
    backdrop-filter: blur(24px);
    -webkit-backdrop-filter: blur(24px);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 16px;
    padding: 1rem 1.25rem;
    width: 100%;
    max-width: 420px;
    margin-bottom: 1.5rem;
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.75), 
                inset 0 0 20px rgba(129, 140, 248, 0.05);
    display: flex;
    flex-direction: column;
    gap: 0.85rem;
    animation: fade-in-scale-kf 0.45s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  }
.orchestrator-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
.orchestrator-title {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 13px;
    font-weight: 800;
    color: #f472b6;
    text-transform: uppercase;
    letter-spacing: 0.08em;
  }
.orchestrator-title .material-icons-round {
    font-size: 18px;
    color: #f472b6;
  }
.orchestrator-badge {
    background: rgba(244, 114, 182, 0.15);
    color: #f472b6;
    font-size: 9px;
    font-weight: 800;
    padding: 0.2rem 0.5rem;
    border-radius: 6px;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    border: 1px solid rgba(244, 114, 182, 0.25);
  }
.orchestrator-description {
    font-size: 11px;
    color: #94a3b8;
    line-height: 1.4;
  }
/* Instrument Selection Tabs */
  .orchestrator-sections {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 0.35rem;
    background: rgba(255, 255, 255, 0.03);
    padding: 0.25rem;
    border-radius: 10px;
    border: 1px solid rgba(255, 255, 255, 0.06);
  }
.orchestrator-section-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 0.25rem;
    padding: 0.45rem 0.2rem;
    background: transparent;
    border: 1px solid transparent;
    border-radius: 8px;
    color: rgba(255, 255, 255, 0.6);
    cursor: pointer;
    transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  }
.orchestrator-section-btn:hover {
    color: #ffffff;
    background: rgba(255, 255, 255, 0.05);
  }
.orchestrator-section-btn.active {
    background: rgba(129, 140, 248, 0.15);
    border-color: rgba(129, 140, 248, 0.35);
    color: #818cf8;
    box-shadow: 0 4px 12px rgba(129, 140, 248, 0.15);
  }
.orchestrator-section-btn.active.piano {
    background: rgba(244, 114, 182, 0.15);
    border-color: rgba(244, 114, 182, 0.35);
    color: #f472b6;
    box-shadow: 0 4px 12px rgba(244, 114, 182, 0.15);
  }
.orchestrator-section-btn .section-icon {
    font-size: 16px;
  }
.orchestrator-section-btn .section-label {
    font-size: 9px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.02em;
  }
/* Interaction Pad */
  .orchestrator-gesture-pad {
    position: relative;
    width: 100%;
    height: 140px;
    background: radial-gradient(circle at center, rgba(30, 30, 45, 0.4) 0%, rgba(10, 10, 15, 0.95) 100%);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    overflow: hidden;
    cursor: crosshair;
    user-select: none;
    touch-action: none; /* Disable browser scrolling on touch drag */
    transition: border-color 0.3s ease;
  }
.orchestrator-gesture-pad:hover {
    border-color: rgba(129, 140, 248, 0.3);
  }
.orchestrator-gesture-pad.active {
    border-color: rgba(244, 114, 182, 0.5);
  }
/* Grid Background Overlay */
  .gesture-pad-grid {
    position: absolute;
    inset: 0;
    background-size: 20px 20px;
    background-image: 
      linear-gradient(to right, rgba(255, 255, 255, 0.03) 1px, transparent 1px),
      linear-gradient(to bottom, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
    pointer-events: none;
  }
/* Glow points and trackers */
  .gesture-pad-target {
    position: absolute;
    width: 24px;
    height: 24px;
    margin-left: -12px;
    margin-top: -12px;
    border-radius: 50%;
    border: 2px solid #818cf8;
    background: rgba(129, 140, 248, 0.25);
    box-shadow: 0 0 15px #818cf8, inset 0 0 5px #ffffff;
    pointer-events: none;
    transform: scale(0);
    transition: transform 0.15s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    z-index: 5;
  }
.orchestrator-gesture-pad.active .gesture-pad-target {
    transform: scale(1);
  }
/* Floating Waveform inside Gesture Pad */
  .gesture-pad-canvas {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 2;
  }
/* Labels inside Gesture Pad */
  .gesture-pad-label-x {
    position: absolute;
    bottom: 6px;
    left: 50%;
    transform: translateX(-50%);
    font-size: 8px;
    font-weight: 800;
    color: rgba(255, 255, 255, 0.35);
    text-transform: uppercase;
    letter-spacing: 0.08em;
    pointer-events: none;
  }
.gesture-pad-label-y {
    position: absolute;
    left: 6px;
    top: 50%;
    transform: translateY(-50%) rotate(-90deg);
    font-size: 8px;
    font-weight: 800;
    color: rgba(255, 255, 255, 0.35);
    text-transform: uppercase;
    letter-spacing: 0.08em;
    pointer-events: none;
    white-space: nowrap;
  }
.gesture-pad-status {
    position: absolute;
    top: 6px;
    right: 8px;
    font-size: 9px;
    font-weight: 700;
    color: rgba(255, 255, 255, 0.45);
    pointer-events: none;
    font-family: monospace;
  }
/* Controls and Sliders */
  .orchestrator-sliders {
    display: flex;
    flex-direction: column;
    gap: 0.65rem;
  }
.orchestrator-slider-row {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
  }
.orchestrator-slider-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
.orchestrator-slider-label {
    font-size: 10px;
    font-weight: 800;
    color: rgba(255, 255, 255, 0.6);
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }
.orchestrator-slider-value {
    font-size: 10px;
    font-weight: 700;
    color: #f472b6;
    font-family: monospace;
  }
.orchestrator-slider-input {
    -webkit-appearance: none;
    width: 100%;
    height: 5px;
    border-radius: 9999px;
    background: rgba(255, 255, 255, 0.1);
    outline: none;
    cursor: pointer;
  }
.orchestrator-slider-input::-webkit-slider-thumb {
    -webkit-appearance: none;
    width: 14px;
    height: 14px;
    border-radius: 50%;
    background: #f472b6;
    box-shadow: 0 0 8px rgba(244, 114, 182, 0.6);
    border: none;
    transition: transform 0.15s ease;
  }
.orchestrator-slider-input::-webkit-slider-thumb:hover {
    transform: scale(1.2);
  }
.orchestrator-footer-tip {
    font-size: 10px;
    color: rgba(255, 255, 255, 0.4);
    text-align: center;
    line-height: 1.3;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.35rem;
  }
.orchestrator-footer-tip .material-icons-round {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.4);
  }
/* --- Orchestrator Actions & Help Toggle --- */
  .orchestrator-actions {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
.orchestrator-help-btn {
    background: transparent;
    border: none;
    color: rgba(255, 255, 255, 0.45);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0.25rem;
    border-radius: 50%;
    transition: all 0.2s ease;
  }
.orchestrator-help-btn:hover {
    color: #f472b6;
    background: rgba(244, 114, 182, 0.15);
  }
.orchestrator-help-btn .material-icons-round {
    font-size: 18px;
  }
/* --- Gesture Tutorial Overlay --- */
  .gesture-tutorial-overlay {
    position: absolute;
    inset: 0;
    background: rgba(10, 10, 15, 0.94);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 0.75rem;
    z-index: 10;
    animation: fade-in-kf 0.3s ease-out forwards;
  }
.gesture-tutorial-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.5rem;
    width: 100%;
    z-index: 12;
  }
.tutorial-guide-title {
    display: flex;
    align-items: center;
    gap: 0.35rem;
    font-size: 11px;
    font-weight: 800;
    color: #f472b6;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }
.tutorial-guide-title .material-icons-round {
    font-size: 14px;
  }
.tutorial-instructions {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 0.4rem;
    width: 100%;
    max-width: 280px;
  }
.tutorial-instructions li {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 10px;
    color: #e2e8f0;
    line-height: 1.3;
  }
.instruction-bullet {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 16px;
    height: 16px;
    border-radius: 4px;
    font-size: 9px;
    font-weight: 800;
    color: #ffffff;
    flex-shrink: 0;
  }
.instruction-bullet.x {
    background: rgba(129, 140, 248, 0.3);
    color: #818cf8;
    border: 1px solid rgba(129, 140, 248, 0.4);
  }
.instruction-bullet.y {
    background: rgba(244, 114, 182, 0.3);
    color: #f472b6;
    border: 1px solid rgba(244, 114, 182, 0.4);
  }
.instruction-bullet.scroll {
    background: rgba(6, 182, 212, 0.3);
    color: #22d3ee;
    border: 1px solid rgba(6, 182, 212, 0.4);
  }
.tutorial-got-it-btn {
    background: linear-gradient(135deg, #818cf8 0%, #f472b6 100%);
    border: none;
    color: #ffffff;
    font-size: 10px;
    font-weight: 800;
    text-transform: uppercase;
    padding: 0.35rem 0.95rem;
    border-radius: 9999px;
    cursor: pointer;
    box-shadow: 0 4px 12px rgba(244, 114, 182, 0.25);
    transition: all 0.2s ease;
    margin-top: 0.25rem;
  }
.tutorial-got-it-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(244, 114, 182, 0.4);
  }
/* --- Path & Hand Gesture Animation --- */
  .tutorial-path-animation {
    position: absolute;
    inset: 0;
    pointer-events: none;
    z-index: 11;
    overflow: hidden;
  }
.tutorial-path-point {
    position: absolute;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: rgba(244, 114, 182, 0.6);
    box-shadow: 0 0 8px #f472b6;
    opacity: 0.4;
  }
.tutorial-path-point.start {
    left: 20%;
    top: 50%;
  }
.tutorial-path-point.end {
    left: 80%;
    top: 50%;
  }
.tutorial-path-hand {
    position: absolute;
    left: 20%;
    top: 50%;
    margin-left: -12px;
    margin-top: -12px;
    color: #ffffff;
    text-shadow: 0 0 10px rgba(244, 114, 182, 0.8);
    animation: tutorial-swipe-kf 3.5s ease-in-out infinite;
    opacity: 0;
    pointer-events: none;
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

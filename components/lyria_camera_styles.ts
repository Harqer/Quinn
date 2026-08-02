import { css } from 'lit';

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
    background: radial-gradient(circle at 50% 50%, var(--ambient-bg, rgba(0, 0, 0, 0)) 0%, #000000 100%);
    transition: background 0.4s ease-out;
  }
video {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
@keyframes pulse {
    0%, 100% }
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
.switcher-btn .material-icons-round {
    font-size: 16px;
  }
/* --- Unified Navigation Layout and Security Hub Styles --- */

  #app-layout {
    display: flex;
    flex-direction: column;
    width: 100vw;
    height: 100vh;
    overflow: hidden;
    background: radial-gradient(circle at 50% 50%, var(--ambient-bg, rgba(99, 102, 241, 0.12)) 0%, #000000 100%);
    color: #ffffff;
    font-family: "Google Sans", sans-serif;
    transition: background 0.4s ease-out;
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
/* Floating Waveform inside Gesture Pad */
  .gesture-pad-canvas {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 2;
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
.android-glasses-preview video {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transform: scaleX(-1);
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
.live-video-pane {
    width: 100%;
    aspect-ratio: 4 / 3;
    background: #18181b;
    border-radius: 12px;
    overflow: hidden;
    position: relative;
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.6);
    margin-bottom: 12px;
  }
.live-video-feed-actual {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: filter 0.3s ease;
  }
.live-video-feed-actual.filter-neon {
    filter: hue-rotate(90deg) saturate(1.8) contrast(1.1);
  }
.live-video-feed-actual.filter-vintage {
    filter: sepia(0.6) contrast(0.95) brightness(1.05);
  }
.live-video-feed-actual.filter-monochrome {
    filter: grayscale(1);
  }
.bottom-nav-tab-web .material-icons-round {
      font-size: 20px;
    }
`;

/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: UI Component for android-go-live.styles.ts
 */

import { css } from 'lit';

export const AndroidGoLiveStyles = css`
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
/* Animation */
  .animate-fade-in {
    animation: fadeIn 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  }
.expanded-player-header .chevron-btn,
  .expanded-player-header .more-btn {
    background: transparent;
    border: none;
    color: #ffffff;
    cursor: pointer;
    display: flex;
    align-items: center;
    padding: 6px;
    border-radius: 50%;
    transition: background-color 0.2s ease;
  }
.expanded-player-header .chevron-btn:hover,
  .expanded-player-header .more-btn:hover {
    background-color: rgba(255, 255, 255, 0.08);
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
.community-header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }
.community-title-large {
    font-size: 22px;
    font-weight: 800;
    color: #ffffff;
    margin: 0;
  }
.go-live-pulsing-btn {
    background: #ef4444;
    border: none;
    padding: 8px 14px;
    border-radius: 9999px;
    color: #ffffff;
    font-size: 11px;
    font-weight: 800;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;
    text-transform: uppercase;
    box-shadow: 0 0 12px rgba(239, 68, 68, 0.4);
    transition: transform 0.2s ease, background 0.2s ease;
  }
.go-live-pulsing-btn:hover {
    background: #dc2626;
    transform: scale(1.05);
  }
.pulsing-dot-red {
    width: 6px;
    height: 6px;
    background: #ffffff;
    border-radius: 50%;
    animation: dotPulse 1.2s infinite;
  }
/* Go Live Section styles */
  .android-flow-go-live {
    height: 100%;
    overflow-y: auto;
    padding: 16px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    text-align: left;
    background: #000000;
    position: relative;
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
.live-badge-overlay {
    position: absolute;
    top: 10px;
    left: 10px;
    background: #ef4444;
    color: #ffffff;
    font-size: 9px;
    font-weight: 900;
    padding: 3px 8px;
    border-radius: 4px;
    letter-spacing: 1px;
    display: flex;
    align-items: center;
    gap: 4px;
    box-shadow: 0 2px 8px rgba(239, 68, 68, 0.5);
    z-index: 5;
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
.filters-control-row {
    display: flex;
    gap: 8px;
    margin-bottom: 16px;
    overflow-x: auto;
    padding-bottom: 4px;
  }
.filter-tab-btn {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 6px;
    padding: 5px 10px;
    font-size: 10px;
    font-weight: 700;
    color: #e4e4e7;
    cursor: pointer;
    white-space: nowrap;
    transition: background 0.2s, border-color 0.2s;
  }
.filter-tab-btn.active {
    background: #1db954;
    color: #000000;
    border-color: #1db954;
  }
.live-control-deck-card {
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(255, 255, 255, 0.06);
    border-radius: 12px;
    padding: 12px;
    margin-bottom: 80px;
  }
.deck-section-title {
    font-size: 11px;
    font-weight: 800;
    color: #a1a1aa;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin-bottom: 8px;
  }
.haptic-grid-2 {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
    margin-bottom: 12px;
  }
.haptic-control-btn {
    background: #27272a;
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 8px;
    padding: 10px;
    color: #ffffff;
    cursor: pointer;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    transition: background 0.2s, border-color 0.2s;
  }
.haptic-control-btn:hover {
    background: #3f3f46;
    border-color: #38bdf8;
  }
.haptic-btn-icon {
    font-size: 18px !important;
    color: #38bdf8;
  }
.haptic-btn-lbl {
    font-size: 10px;
    font-weight: 700;
  }
.haptic-btn-sub {
    font-size: 8px;
    color: #a1a1aa;
  }
.stream-active-orchestrator {
    height: 40px;
    background: rgba(0, 0, 0, 0.4);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 2px;
    padding: 0 10px;
  }
.waveform-bar-sim {
    width: 3px;
    background: #1db954;
    border-radius: 1px;
    animation: barWave 1s ease-in-out infinite alternate;
  }
.bottom-nav-tab-web .material-icons-round {
      font-size: 20px;
    }
`;

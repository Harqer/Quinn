/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: UI Component for android-community.styles.ts
 */

import { css } from 'lit';

export const AndroidCommunityStyles = css`
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
/* Community, Go Live & Presets styles */
  .android-flow-community {
    height: 100%;
    overflow-y: auto;
    padding: 16px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    text-align: left;
    background: linear-gradient(to bottom, #111827 0%, #121212 60%);
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
.live-metrics-bento {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 10px;
    margin-bottom: 20px;
  }
.metric-mini-card {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 12px;
    padding: 12px;
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
.metric-mini-val {
    font-size: 18px;
    font-weight: 800;
    color: #ffffff;
  }
.metric-mini-lbl {
    font-size: 10px;
    color: #a1a1aa;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }
.community-chat-container {
    background: rgba(0, 0, 0, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.06);
    border-radius: 12px;
    padding: 12px;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    min-height: 200px;
    margin-bottom: 80px;
  }
.chat-feed-title-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
    padding-bottom: 8px;
    margin-bottom: 8px;
  }
.chat-feed-title {
    font-size: 12px;
    font-weight: 700;
    color: #ffffff;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }
.chat-bubbles-scroll {
    flex-grow: 1;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 8px;
    max-height: 220px;
  }
.chat-bubble-card {
    background: rgba(255, 255, 255, 0.04);
    border-radius: 8px;
    padding: 8px 10px;
    border: 1px solid rgba(255, 255, 255, 0.03);
    cursor: pointer;
    transition: background 0.2s ease, border-color 0.2s ease;
  }
.chat-bubble-card:hover {
    background: rgba(255, 255, 255, 0.08);
    border-color: #1db954;
  }
.chat-user-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2px;
  }
.chat-username {
    font-size: 11px;
    font-weight: 700;
    color: #1db954;
  }
.chat-time {
    font-size: 9px;
    color: #71717a;
  }
.chat-text {
    font-size: 12px;
    color: #e4e4e7;
    line-height: 1.4;
  }
.chat-suggestion-indicator {
    font-size: 9px;
    color: #a1a1aa;
    text-align: right;
    margin-top: 4px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 3px;
  }
.bottom-nav-tab-web .material-icons-round {
      font-size: 20px;
    }
`;

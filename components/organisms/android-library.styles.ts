import { css } from 'lit';

export const AndroidLibraryStyles = css`
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
.community-title-large {
    font-size: 22px;
    font-weight: 800;
    color: #ffffff;
    margin: 0;
  }
/* Library & Presets view */
  .android-flow-library {
    height: 100%;
    overflow-y: auto;
    padding: 16px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    text-align: left;
    background: linear-gradient(to bottom, #064e3b 0%, #121212 50%);
  }
.presets-section-title {
    font-size: 14px;
    font-weight: 800;
    color: #ffffff;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin-top: 16px;
    margin-bottom: 8px;
  }
.presets-intro-text {
    font-size: 11px;
    color: #a1a1aa;
    line-height: 1.4;
    margin-bottom: 12px;
  }
.preset-bento-grid {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-bottom: 80px;
  }
.preset-card-item {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 12px;
    padding: 12px;
    cursor: pointer;
    transition: background 0.2s, border-color 0.2s, transform 0.2s;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
.preset-card-item:hover {
    background: rgba(255, 255, 255, 0.09);
    border-color: #1db954;
    transform: scale(1.02);
  }
.preset-card-item.active {
    border-color: #1db954;
    background: rgba(29, 185, 84, 0.1);
  }
.preset-left {
    display: flex;
    flex-direction: column;
    gap: 3px;
    text-align: left;
  }
.preset-name-row {
    display: flex;
    align-items: center;
    gap: 6px;
  }
.preset-title-val {
    font-size: 13px;
    font-weight: 700;
    color: #ffffff;
  }
.preset-badge-tag {
    font-size: 8px;
    font-weight: 800;
    color: #000000;
    background: #1db954;
    padding: 1px 4px;
    border-radius: 3px;
    text-transform: uppercase;
  }
.preset-desc {
    font-size: 11px;
    color: #a1a1aa;
  }
.preset-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }
.preset-telemetry-status {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 2px;
  }
.preset-battery-lbl {
    font-size: 10px;
    color: #e4e4e7;
    font-weight: 700;
    display: flex;
    align-items: center;
    gap: 2px;
  }
.preset-wear-status {
    font-size: 9px;
    color: #a1a1aa;
  }
.preset-play-icon {
    font-size: 20px !important;
    color: #1db954;
  }
.bottom-nav-tab-web .material-icons-round {
      font-size: 20px;
    }
`;

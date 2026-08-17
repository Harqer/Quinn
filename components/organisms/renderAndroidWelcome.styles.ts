/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: UI Component for renderAndroidWelcome.styles.ts
 */

import { css } from 'lit';

export const renderAndroidWelcomeStyles = css`
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
/* 1. Onboarding / Welcome Screen */
  .android-flow-welcome {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    padding: 32px 24px;
    background: linear-gradient(to bottom, #1db9542a 0%, #121212 60%);
  }
.android-spotify-logo-container {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 24px;
    justify-content: center;
  }
.logo-soundwave {
    font-size: 40px !important;
  }
.spotify-logo-text {
    font-size: 32px;
    font-weight: 800;
    color: #ffffff;
    letter-spacing: -1px;
  }
.welcome-heading {
    font-size: 28px;
    font-weight: 800;
    line-height: 1.25;
    color: #ffffff;
    text-align: center;
    margin-bottom: 32px;
    letter-spacing: -0.5px;
  }
.welcome-buttons-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }
.welcome-btn-primary {
    background: #1db954;
    color: #000000;
    border: none;
    border-radius: 500px;
    padding: 14px;
    font-size: 15px;
    font-weight: 700;
    cursor: pointer;
    transition: transform 0.1s ease, background-color 0.2s ease;
  }
.welcome-btn-primary:hover {
    transform: scale(1.02);
    background-color: #1ed760;
  }
.welcome-btn-secondary {
    background: transparent;
    color: #ffffff;
    border: 1px solid rgba(255, 255, 255, 0.4);
    border-radius: 500px;
    padding: 13px;
    font-size: 14px;
    font-weight: 700;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    transition: all 0.2s ease;
  }
.welcome-btn-secondary:hover {
    border-color: #ffffff;
    background: rgba(255, 255, 255, 0.05);
  }
.welcome-btn-link {
    background: transparent;
    border: none;
    color: #ffffff;
    font-size: 14px;
    font-weight: 700;
    padding: 12px;
    cursor: pointer;
    text-align: center;
    margin-top: 8px;
  }
.welcome-btn-link:hover {
    text-decoration: underline;
  }
.font-icon-large {
    font-size: 24px !important;
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

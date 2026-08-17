/**
 * @AtomicLevel: Template
 * @SemanticPurpose: UI Component for android-onboarding-flow.styles.ts
 */

import { css } from 'lit';

export const androidOnboardingFlowStyles = css`
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
.switcher-btn.active {
    background: rgba(255, 255, 255, 0.15);
    color: #ffffff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  }
.switcher-btn .material-icons-round {
    font-size: 16px;
  }
.nav-tab.active {
    color: #ffffff;
    background: rgba(255, 255, 255, 0.1);
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
.orchestrator-gesture-pad.active {
    border-color: rgba(244, 114, 182, 0.5);
  }
.orchestrator-gesture-pad.active .gesture-pad-target {
    transform: scale(1);
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
/* 2. Credentials Registration Forms */
  .android-flow-inputs {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 24px;
  }
.android-flow-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 32px;
  }
.android-header-back {
    background: transparent;
    border: none;
    color: #ffffff;
    cursor: pointer;
    padding: 4px;
    display: flex;
    align-items: center;
  }
.android-header-title {
    font-size: 16px;
    font-weight: 700;
    color: #ffffff;
  }
.input-form-container {
    display: flex;
    flex-direction: column;
    flex: 1;
  }
.form-title {
    font-size: 24px;
    font-weight: 800;
    color: #ffffff;
    margin-bottom: 12px;
  }
.form-text-input {
    background: rgba(42, 42, 42, 0.4);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    padding: 14px 16px;
    font-size: 15px;
    color: #ffffff;
    outline: none;
    transition: border-color 0.2s ease;
    margin-bottom: 8px;
    width: 100%;
    box-sizing: border-box;
  }
.form-text-input:focus {
    border-color: #1db954;
    background: #333333;
  }
.form-subtext {
    font-size: 11px;
    color: #a1a1aa;
    margin-bottom: 32px;
    line-height: 1.4;
  }
.form-next-btn {
    background: #535353;
    color: #b3b3b3;
    border: none;
    border-radius: 500px;
    padding: 14px;
    font-size: 15px;
    font-weight: 700;
    cursor: not-allowed;
    text-align: center;
    transition: all 0.2s ease;
  }
.form-next-btn.active {
    background: #ffffff;
    color: #000000;
    cursor: pointer;
  }
.form-next-btn.active:hover {
    transform: scale(1.02);
  }
.name-input-wrapper {
    position: relative;
    display: flex;
    align-items: center;
  }
.name-check-icon {
    position: absolute;
    right: 16px;
    font-size: 20px !important;
  }
/* Terms agreements & checkboxes */
  .terms-agreement-block {
    margin-bottom: 24px;
    padding-top: 8px;
  }
.terms-paragraph {
    font-size: 11px;
    color: #d4d4d8;
    line-height: 1.5;
  }
.checkbox-form-row {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    margin-bottom: 16px;
    cursor: pointer;
  }
.checkbox-form-row input[type="checkbox"] {
    accent-color: #1db954;
    width: 16px;
    height: 16px;
    margin-top: 2px;
    flex-shrink: 0;
  }
.checkbox-form-row label {
    font-size: 11px;
    color: #ffffff;
    line-height: 1.4;
    user-select: none;
    cursor: pointer;
  }
.android-tab-btn.active, .android-tab-btn:hover {
    color: #ffffff;
  }
.android-tab-btn .material-icons-round {
    font-size: 20px !important;
  }
.step-badge.active {
    color: #1db954;
    background: rgba(29, 185, 84, 0.15);
  }
.expanded-player-header .chevron-btn .material-icons-round,
  .expanded-player-header .more-btn .material-icons-round {
    font-size: 24px !important;
  }
.expanded-player-like-btn .material-icons-round {
    font-size: 24px !important;
  }
.expanded-player-control-btn.active {
    color: #1db954;
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
.lyrics-text-line.active {
    opacity: 1;
    text-shadow: 0 1px 3px rgba(0,0,0,0.4);
  }
.options-menu-item .material-icons-round {
    font-size: 20px !important;
    color: #b3b3b3;
  }
.options-menu-item.liked .material-icons-round {
    color: #1db954;
  }
.filter-tab-btn.active {
    background: #1db954;
    color: #000000;
    border-color: #1db954;
  }
.preset-card-item.active {
    border-color: #1db954;
    background: rgba(29, 185, 84, 0.1);
  }
.community-track-card.active-playing {
    border-color: #1db954;
    background: rgba(29, 185, 84, 0.04);
    box-shadow: 0 10px 25px rgba(29, 185, 84, 0.05);
  }
.community-track-card:hover .track-play-overlay, .community-track-card.active-playing .track-play-overlay {
    opacity: 1;
  }
.bottom-nav-tab-web.active, .bottom-nav-tab-web:hover {
      color: #ffffff;
    }
.bottom-nav-tab-web .material-icons-round {
      font-size: 20px;
    }
`;

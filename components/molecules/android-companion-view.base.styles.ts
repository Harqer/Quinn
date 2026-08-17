/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: UI Component for android-companion-view.base.styles.ts
 */

import { css } from 'lit';

export const baseStyles = css`
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
.switcher-btn .material-icons-round { font-size: 16px; }
.nav-tab.active {
    color: #ffffff;
    background: rgba(255, 255, 255, 0.1);
  }
.copy-command-btn .material-icons-round { font-size: 15px; }
.voice-title .material-icons-round { font-size: 16px; }
.audio-mini-btn .material-icons-round { font-size: 12px; }
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
.orchestrator-help-btn .material-icons-round { font-size: 18px; }
.tutorial-guide-title .material-icons-round { font-size: 14px; }
.tutorial-path-hand .material-icons-round { font-size: 24px; }
.animate-fade-in {
    animation: fadeIn 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  }
.form-next-btn.active {
    background: #ffffff;
    color: #000000;
    cursor: pointer;
  }
.form-next-btn.active:hover {
    transform: scale(1.02);
  }
.font-icon-small { font-size: 14px !important; }
.font-icon-tiny {
    font-size: 11px !important;
    vertical-align: middle;
  }
`;

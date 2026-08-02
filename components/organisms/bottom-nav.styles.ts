import { css } from 'lit';

export const bottomNavStyles = css`
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
.badge-count {
    background: #ef4444;
    color: #ffffff;
    font-size: 10px;
    font-weight: 700;
    padding: 0.1rem 0.45rem;
    border-radius: 9999px;
    margin-left: 0.35rem;
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
/* --- Bottom Nav Bar Web Styles --- */
  #bottom-nav-bar-web {
    display: none;
  }
#bottom-nav-bar-web {
      display: flex;
      justify-content: space-around;
      align-items: center;
      background: rgba(10, 10, 12, 0.95);
      backdrop-filter: blur(20px);
      border-top: 1px solid rgba(255, 255, 255, 0.1);
      height: 60px;
      padding: 0.25rem;
      box-sizing: border-box;
      flex-shrink: 0;
      z-index: 100;
    }
.bottom-nav-tab-web {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      background: transparent;
      border: none;
      color: rgba(255, 255, 255, 0.55);
      font-size: 10px;
      font-weight: 600;
      gap: 3px;
      cursor: pointer;
      flex: 1;
      padding: 0.25rem;
      position: relative;
    }
.bottom-nav-tab-web.active, .bottom-nav-tab-web:hover {
      color: #ffffff;
    }
.bottom-nav-tab-web .material-icons-round {
      font-size: 20px;
    }
`;

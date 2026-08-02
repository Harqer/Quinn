import { css } from 'lit';

export const AndroidOptionsMenuStyles = css`
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
/* ==========================================================================
     Options Menu Bottom Sheet Styles
     ========================================================================== */
  .android-options-menu {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.7);
    z-index: 100;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    box-sizing: border-box;
    animation: fadeInOverlay 0.25s ease forwards;
  }
.options-menu-sheet {
    background: #121212;
    border-radius: 16px 16px 0 0;
    padding: 20px 16px 12px;
    max-height: 82%;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    gap: 14px;
    overflow-y: auto;
    animation: slideUpSheet 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  }
.options-menu-sheet::-webkit-scrollbar {
    width: 4px;
  }
.options-menu-sheet::-webkit-scrollbar-thumb {
    background: rgba(255,255,255,0.1);
    border-radius: 2px;
  }
.options-menu-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding-bottom: 14px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
    box-sizing: border-box;
  }
.options-menu-header img {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 8px;
    box-shadow: 0 8px 24px rgba(0,0,0,0.6);
    margin-bottom: 12px;
  }
.options-menu-song-title {
    font-size: 14px;
    font-weight: 700;
    color: #ffffff;
    max-width: 90%;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
.options-menu-song-artist {
    font-size: 11px;
    color: #a1a1aa;
    margin-top: 3px;
  }
.options-menu-list {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
.options-menu-item {
    display: flex;
    align-items: center;
    gap: 14px;
    background: transparent;
    border: none;
    color: #ffffff;
    font-size: 13px;
    font-weight: 600;
    text-align: left;
    padding: 10px 8px;
    cursor: pointer;
    width: 100%;
    box-sizing: border-box;
    border-radius: 8px;
    transition: background 0.2s ease;
  }
.options-menu-item:hover {
    background: rgba(255, 255, 255, 0.06);
  }
.options-menu-item .material-icons-round {
    font-size: 20px !important;
    color: #b3b3b3;
  }
.options-menu-item.liked .material-icons-round {
    color: #1db954;
  }
.options-menu-close-btn {
    background: transparent;
    border: none;
    color: #ffffff;
    font-size: 13px;
    font-weight: 700;
    padding: 12px 0;
    cursor: pointer;
    text-align: center;
    border-top: 1px solid rgba(255, 255, 255, 0.08);
    margin-top: 4px;
    transition: opacity 0.2s ease;
  }
.options-menu-close-btn:hover {
    opacity: 0.8;
  }
.bottom-nav-tab-web .material-icons-round {
      font-size: 20px;
    }
`;

import { css } from 'lit';

export const renderAndroidExpandedPlayerStyles = css`
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
/* ==========================================================================
     Android Spotify-Style Expanded Player & Options Menu Styles
     ========================================================================== */
  .android-expanded-player {
    position: absolute;
    top: 24px;
    left: 0;
    width: 100%;
    height: calc(100% - 24px);
    background: linear-gradient(to bottom, #500a16 0%, #1a0307 40%, #0d0103 100%);
    z-index: 50;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    animation: slideUp 0.35s cubic-bezier(0.16, 1, 0.3, 1) forwards;
    box-sizing: border-box;
    overflow: hidden;
  }
.expanded-player-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    box-sizing: border-box;
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
.expanded-player-header .playlist-title {
    font-size: 11px;
    font-weight: 700;
    color: #ffffff;
    text-align: center;
    letter-spacing: 0.8px;
    text-transform: uppercase;
  }
.expanded-player-art-container {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 16px 24px 20px;
    flex-grow: 1;
    box-sizing: border-box;
  }
.expanded-player-art-container img {
    width: 240px;
    height: 240px;
    object-fit: cover;
    border-radius: 8px;
    box-shadow: 0 16px 40px rgba(0, 0, 0, 0.75);
    transition: transform 0.3s ease;
  }
.expanded-player-art-container img:hover {
    transform: scale(1.02);
  }
.expanded-player-info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px 14px;
    box-sizing: border-box;
    gap: 12px;
  }
.expanded-player-title-block {
    display: flex;
    flex-direction: column;
    text-align: left;
    flex: 1;
    min-width: 0;
  }
.expanded-player-title {
    font-size: 17px;
    font-weight: 800;
    color: #ffffff;
    line-height: 1.25;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
.expanded-player-artist {
    font-size: 13px;
    color: #b3b3b3;
    margin-top: 3px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
.expanded-player-like-btn {
    background: transparent;
    border: none;
    color: #ffffff;
    cursor: pointer;
    display: flex;
    align-items: center;
    padding: 6px;
    transition: transform 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  }
.expanded-player-like-btn.liked {
    color: #1db954;
  }
.expanded-player-like-btn:hover {
    transform: scale(1.15);
  }
.expanded-player-like-btn .material-icons-round {
    font-size: 24px !important;
  }
.expanded-player-progress-area {
    padding: 0 24px 14px;
    box-sizing: border-box;
  }
.expanded-player-slider-track {
    position: relative;
    height: 4px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 2px;
    cursor: pointer;
  }
.expanded-player-slider-fill {
    height: 100%;
    background: #ffffff;
    border-radius: 2px;
    position: relative;
  }
.expanded-player-slider-thumb {
    position: absolute;
    top: 50%;
    right: 0;
    width: 10px;
    height: 10px;
    background: #ffffff;
    border-radius: 50%;
    transform: translate(50%, -50%);
    box-shadow: 0 2px 4px rgba(0,0,0,0.5);
  }
.expanded-player-time-row {
    display: flex;
    justify-content: space-between;
    font-size: 10px;
    color: #b3b3b3;
    margin-top: 6px;
    font-weight: 500;
  }
.expanded-player-controls-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px 14px;
    box-sizing: border-box;
  }
.expanded-player-control-btn {
    background: transparent;
    border: none;
    color: #b3b3b3;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 8px;
    transition: all 0.2s ease;
  }
.expanded-player-control-btn.active {
    color: #1db954;
  }
.expanded-player-control-btn:hover {
    color: #ffffff;
    transform: scale(1.08);
  }
.expanded-player-control-btn .material-icons-round {
    font-size: 22px !important;
  }
.expanded-player-play-btn {
    background: #ffffff;
    border: none;
    color: #000000;
    width: 54px;
    height: 54px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: transform 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    box-shadow: 0 4px 12px rgba(0,0,0,0.3);
  }
.expanded-player-play-btn:hover {
    transform: scale(1.06);
    background: #fcfcfc;
  }
.expanded-player-play-btn .material-icons-round {
    font-size: 30px !important;
  }
.expanded-player-accessories-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px 14px;
    box-sizing: border-box;
  }
.expanded-player-device-selector {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 10px;
    font-weight: 700;
    color: #1db954;
    cursor: pointer;
    letter-spacing: 0.3px;
  }
.expanded-player-device-selector:hover {
    opacity: 0.9;
  }
.expanded-player-device-selector .material-icons-round {
    font-size: 14px !important;
  }
.expanded-player-lyrics-card {
    margin: 4px 16px 20px;
    background: #e95a2b;
    border-radius: 8px;
    padding: 14px;
    box-shadow: 0 6px 18px rgba(0,0,0,0.25);
    cursor: pointer;
    transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1);
    max-height: 105px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    text-align: left;
    box-sizing: border-box;
  }
.expanded-player-lyrics-card.more-open {
    max-height: 220px;
  }
.lyrics-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
  }
.lyrics-card-title {
    font-size: 13px;
    font-weight: 800;
    color: #ffffff;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }
.lyrics-card-more-btn {
    background: rgba(0, 0, 0, 0.25);
    border: none;
    color: #ffffff;
    font-size: 9px;
    font-weight: 700;
    padding: 3px 8px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    gap: 4px;
    cursor: pointer;
    transition: background 0.2s ease;
  }
.lyrics-card-more-btn:hover {
    background: rgba(0, 0, 0, 0.4);
  }
.lyrics-scroll-content {
    flex-grow: 1;
    overflow-y: auto;
    padding-right: 4px;
  }
.lyrics-scroll-content::-webkit-scrollbar {
    width: 4px;
  }
.lyrics-scroll-content::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 2px;
  }
.lyrics-text-line {
    font-size: 13px;
    font-weight: 700;
    color: #ffffff;
    margin-bottom: 6px;
    line-height: 1.4;
    opacity: 0.75;
    transition: opacity 0.2s ease;
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
.bottom-nav-tab-web .material-icons-round {
      font-size: 20px;
    }
`;

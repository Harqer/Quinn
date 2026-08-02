import { css } from 'lit';

export const AndroidSearchHomeStyles = css`
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
/* 3. Search / Discovery Dashboard */
  .android-flow-search {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 24px 20px 100px 20px;
  }
.android-search-header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }
.search-title-large {
    font-size: 26px;
    font-weight: 800;
    color: #ffffff;
    letter-spacing: -0.5px;
  }
.android-search-camera-btn {
    background: rgba(255, 255, 255, 0.1);
    border: none;
    border-radius: 50%;
    width: 44px;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s ease;
  }
.android-search-camera-btn:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: scale(1.05);
  }
.font-icon-large {
    font-size: 24px !important;
  }
.search-input-mock-container {
    background: #ffffff;
    border-radius: 8px;
    padding: 12px 14px;
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    margin-bottom: 24px;
  }
.search-mock-icon {
    color: #121212;
    font-size: 20px !important;
  }
.search-mock-field {
    border: none;
    background: transparent;
    font-size: 14px;
    color: #121212;
    font-weight: 600;
    width: 100%;
    outline: none;
    pointer-events: none;
  }
.search-mock-field::placeholder {
    color: #575757;
  }
/* Bento Genre Grid */
  .search-browse-section {
    display: flex;
    flex-direction: column;
  }
.browse-section-title {
    font-size: 14px;
    font-weight: 700;
    color: #ffffff;
    margin-bottom: 12px;
  }
.browse-grid-2 {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }
.genre-card {
    height: 92px;
    border-radius: 8px;
    padding: 12px;
    position: relative;
    overflow: hidden;
    font-weight: 700;
    font-size: 14px;
    color: #ffffff;
    cursor: pointer;
    transition: transform 0.2s ease;
  }
.genre-card:hover {
    transform: scale(1.03);
  }
.genre-rotated-art {
    width: 56px;
    height: 56px;
    position: absolute;
    bottom: -8px;
    right: -12px;
    transform: rotate(25deg);
    border-radius: 4px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
  }
/* 4. Active Search filtering view */
  .android-flow-search-results {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 16px 16px 120px 16px;
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
.bottom-nav-tab-web .material-icons-round {
      font-size: 20px;
    }
`;

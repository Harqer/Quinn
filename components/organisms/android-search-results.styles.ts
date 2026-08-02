import { css } from 'lit';

export const AndroidSearchResultsStyles = css`
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
/* 4. Active Search filtering view */
  .android-flow-search-results {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 16px 16px 120px 16px;
  }
.results-header-row {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 18px;
  }
.results-search-wrapper {
    flex: 1;
    background: #2a2a2a;
    border-radius: 8px;
    padding: 10px 12px;
    display: flex;
    align-items: center;
    gap: 8px;
  }
.search-active-icon {
    font-size: 18px !important;
    color: #a1a1aa;
  }
.results-search-field {
    border: none;
    background: transparent;
    font-size: 14px;
    color: #ffffff;
    outline: none;
    width: 100%;
  }
.results-cancel-btn {
    background: transparent;
    border: none;
    color: #ffffff;
    font-size: 13px;
    font-weight: 700;
    cursor: pointer;
  }
.results-list-container {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }
.results-empty {
    font-size: 13px;
    color: #a1a1aa;
    text-align: center;
    padding: 32px;
  }
.result-item {
    display: flex;
    align-items: center;
    gap: 14px;
    cursor: pointer;
    padding: 4px;
    border-radius: 6px;
    transition: background 0.2s ease;
  }
.result-item:hover {
    background: rgba(255, 255, 255, 0.05);
  }
.result-avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    object-fit: cover;
    background: #282828;
  }
.result-meta {
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
.result-name {
    font-size: 14px;
    font-weight: 700;
    color: #ffffff;
  }
.result-subtitle {
    font-size: 11px;
    color: #a1a1aa;
    margin-top: 2px;
    line-height: 1.3;
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

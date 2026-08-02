import { css } from 'lit';

export const playerStyles = css`
.android-mini-player-wrapper {
    position: absolute;
    bottom: 56px;
    left: 0;
    right: 0;
    padding: 0 8px;
    z-index: 20;
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
.android-glasses-preview {
    background: #000000;
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    height: 130px;
    overflow: hidden;
    position: relative;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
  }
.android-glasses-preview video {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transform: scaleX(-1);
  }
.preview-badge {
    position: absolute;
    top: 8px;
    left: 8px;
    background: rgba(239, 68, 68, 0.85);
    color: #ffffff;
    font-size: 8px;
    font-weight: 800;
    letter-spacing: 1px;
    padding: 3px 6px;
    border-radius: 4px;
    z-index: 5;
    animation: pulse 1.5s infinite;
  }
.android-spotify-mini-player {
    background: #222326;
    border-radius: 8px;
    padding: 8px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.4);
  }
.mini-player-left {
    display: flex;
    align-items: center;
    gap: 10px;
    flex: 1;
    cursor: pointer;
  }
.mini-player-art {
    width: 38px;
    height: 38px;
    border-radius: 4px;
    object-fit: cover;
  }
.mini-player-info {
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
.mini-player-title {
    font-size: 12px;
    font-weight: 700;
    color: #ffffff;
  }
.mini-player-artist {
    font-size: 10px;
    color: #b3b3b3;
    margin-top: 1px;
  }
.mini-player-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }
.mini-player-action-btn {
    background: transparent;
    border: none;
    color: #b3b3b3;
    cursor: pointer;
    padding: 4px;
    display: flex;
    align-items: center;
    transition: color 0.2s ease;
  }
.mini-player-action-btn:hover { color: #ffffff; }
.play-pause-btn { color: #ffffff; }
.expanded-player-header .chevron-btn .material-icons-round,
.expanded-player-header .more-btn .material-icons-round { font-size: 24px !important; }
.expanded-player-like-btn .material-icons-round { font-size: 24px !important; }
.expanded-player-control-btn.active { color: #1db954; }
.expanded-player-control-btn .material-icons-round { font-size: 22px !important; }
.expanded-player-play-btn .material-icons-round { font-size: 30px !important; }
.expanded-player-device-selector .material-icons-round { font-size: 14px !important; }
.lyrics-text-line.active {
    opacity: 1;
    text-shadow: 0 1px 3px rgba(0,0,0,0.4);
  }
.options-menu-item .material-icons-round {
    font-size: 20px !important;
    color: #b3b3b3;
  }
.options-menu-item.liked .material-icons-round { color: #1db954; }
.filter-tab-btn.active {
    background: #1db954;
    color: #000000;
    border-color: #1db954;
  }
.haptic-grid-2 {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 8px;
    margin-bottom: 12px;
  }
.haptic-control-btn {
    background: #27272a;
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 8px;
    padding: 10px;
    color: #ffffff;
    cursor: pointer;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    transition: background 0.2s, border-color 0.2s;
  }
.haptic-control-btn:hover {
    background: #3f3f46;
    border-color: #38bdf8;
  }
.haptic-btn-icon {
    font-size: 18px !important;
    color: #38bdf8;
  }
.haptic-btn-lbl {
    font-size: 10px;
    font-weight: 700;
  }
.haptic-btn-sub {
    font-size: 8px;
    color: #a1a1aa;
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
`;

import { css } from 'lit';

export const chatInterfaceStyles = css`
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
/* --- Voice Communication / Command Console --- */
  .voice-card {
    background: rgba(10, 10, 12, 0.85);
    backdrop-filter: blur(24px);
    -webkit-backdrop-filter: blur(24px);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 16px;
    padding: 1rem 1.25rem;
    width: 100%;
    max-width: 420px;
    margin-bottom: 1.5rem;
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.65);
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
    animation: fade-in-up 0.4s ease-out;
  }
.voice-title .material-icons-round {
    font-size: 16px;
  }
.mic-button-wrapper {
    position: relative;
    flex-shrink: 0;
  }
.mic-button {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    border: none;
    outline: none;
    transition: all 0.25s ease;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.4);
  }
.mic-button.idle {
    background: #4f46e5;
    color: #ffffff;
  }
.mic-button.idle:hover {
    background: #6366f1;
    box-shadow: 0 4px 20px rgba(99, 102, 241, 0.4);
    transform: scale(1.05);
  }
.mic-button.recording {
    background: #ef4444;
    color: #ffffff;
    animation: mic-pulse-kf 1.2s infinite;
  }
.mic-button.processing {
    background: #d97706;
    color: #ffffff;
    animation: pulse 1.5s infinite ease-in-out;
  }
.mic-button.speaking {
    background: #10b981;
    color: #ffffff;
  }
.transcript-text {
    font-size: 13px;
    font-weight: 600;
    color: #f8fafc;
    line-height: 1.4;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
  }
.transcript-text.empty {
    color: rgba(255, 255, 255, 0.45);
    font-style: italic;
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
.voice-text-input-row {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-top: 0.75rem;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    padding: 0.25rem 0.5rem;
    transition: all 0.25s ease;
  }
.voice-text-input-row:focus-within {
    border-color: rgba(99, 102, 241, 0.5);
    background: rgba(255, 255, 255, 0.08);
    box-shadow: 0 0 10px rgba(99, 102, 241, 0.15);
  }
.voice-text-field {
    flex: 1;
    background: transparent;
    border: none;
    outline: none;
    color: #ffffff;
    font-size: 13px;
    padding: 0.4rem;
    font-family: inherit;
    min-width: 0;
  }
.voice-text-field::placeholder {
    color: rgba(255, 255, 255, 0.35);
  }
.voice-text-submit-btn {
    background: #6366f1;
    color: #ffffff;
    border: none;
    border-radius: 6px;
    width: 28px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s ease;
    flex-shrink: 0;
  }
.voice-text-submit-btn:hover {
    background: #4f46e5;
    transform: scale(1.05);
  }
.voice-text-submit-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    transform: none;
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
.bottom-nav-tab-web .material-icons-round {
      font-size: 20px;
    }
`;

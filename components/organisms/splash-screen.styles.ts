import { css } from 'lit';

export const splashScreenStyles = css`
#splash {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    width: 100%;
    box-sizing: border-box;
    padding: 2rem;
    background: radial-gradient(circle at center, #0e0e11 0%, #030304 100%);
    text-align: center;
    color: #ffffff;
  }
.auth-section {
    background: rgba(15, 23, 42, 0.45);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 16px;
    padding: 1.25rem 2rem;
    margin: 0 auto 2.5rem auto;
    max-width: 360px;
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
    box-sizing: border-box;
  }
.auth-title {
    font-size: 11px;
    font-weight: 700;
    text-transform: uppercase;
    color: #818cf8;
    letter-spacing: 0.05em;
  }
.auth-user-info {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    width: 100%;
    justify-content: center;
  }
.auth-avatar {
    width: 38px;
    height: 38px;
    border-radius: 50%;
    border: 2px solid #6366f1;
    object-fit: cover;
  }
.auth-placeholder-avatar {
    font-size: 38px;
    color: #6366f1;
  }
.auth-details {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }
.auth-name {
    font-size: 13.5px;
    font-weight: 700;
    color: #f8fafc;
  }
.auth-email {
    font-size: 11px;
    color: rgba(255, 255, 255, 0.5);
  }
.auth-btn-google {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    background: #6366f1;
    color: #ffffff;
    border: none;
    padding: 0.75rem 1.5rem;
    border-radius: 12px;
    font-size: 13.5px;
    font-weight: 600;
    cursor: pointer;
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.25);
    transition: all 0.2s ease;
    width: 100%;
    box-sizing: border-box;
  }
.auth-btn-google:hover {
    background: #4f46e5;
    box-shadow: 0 6px 16px rgba(99, 102, 241, 0.35);
    transform: translateY(-1px);
  }
.auth-btn-google:active {
    transform: translateY(0);
  }
.auth-btn-signout {
    background: transparent;
    border: 1px solid rgba(255, 255, 255, 0.15);
    color: rgba(255, 255, 255, 0.7);
    padding: 0.4rem 1rem;
    border-radius: 8px;
    font-size: 11px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
  }
.auth-btn-signout:hover {
    background: rgba(255, 255, 255, 0.05);
    color: #ffffff;
    border-color: rgba(255, 255, 255, 0.3);
  }
.splash-icon {
    font-size: 4rem;
    color: #10b981;
    margin-bottom: 1.5rem;
    animation: pulse 2s infinite ease-in-out;
  }
.splash-title {
    font-size: 2.25rem;
    font-weight: 700;
    letter-spacing: -0.02em;
    margin: 0 0 0.5rem 0;
  }
.splash-desc {
    font-size: 1rem;
    color: rgba(255, 255, 255, 0.6);
    max-width: 420px;
    line-height: 1.6;
    margin: 0 0 2rem 0;
  }
.splash-btn {
    background: #10b981;
    color: #ffffff;
    padding: 1rem 2.5rem;
    border-radius: 9999px;
    font-size: 16px;
    font-weight: 700;
    border: none;
    cursor: pointer;
    box-shadow: 0 4px 20px rgba(16, 185, 129, 0.4);
    transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  }
.splash-btn:hover {
    background: #059669;
    transform: translateY(-2px);
    box-shadow: 0 6px 25px rgba(16, 185, 129, 0.5);
  }
.splash-btn:active {
    transform: translateY(0);
  }
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
.platform-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    background: rgba(16, 185, 129, 0.1);
    border: 1px solid rgba(16, 185, 129, 0.2);
    padding: 0.5rem 1.25rem;
    border-radius: 9999px;
    font-size: 13px;
    font-weight: 500;
    color: #a7f3d0;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    margin-bottom: 1.5rem;
  }
.font-icon-green {
    font-size: 16px;
    color: #34d399;
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
.bottom-nav-tab-web .material-icons-round {
      font-size: 20px;
    }
`;

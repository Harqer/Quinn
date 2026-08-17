/**
 * @AtomicLevel: Template
 * @SemanticPurpose: UI Component for android-companion-view.layout.styles.ts
 */

import { css } from 'lit';

export const layoutStyles = css`
.android-simulation-container {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    align-items: flex-start;
    gap: 2.5rem;
    padding: 2.5rem;
    background: #09090b;
    min-height: calc(100vh - 80px);
    box-sizing: border-box;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  }
.android-phone-frame {
    position: relative;
    width: 380px;
    height: 760px;
    background: #09090b;
    border: 12px solid #2e2e2e;
    border-radius: 48px;
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.7);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    user-select: none;
    flex-shrink: 0;
  }
.android-phone-screen {
    flex: 1;
    overflow-y: auto;
    position: relative;
    padding-bottom: 120px;
    background: #09090b;
  }
.android-status-bar {
    height: 32px;
    background: transparent;
    padding: 0 24px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 11px;
    font-weight: 600;
    color: #e4e4e7;
    z-index: 10;
  }
.android-status-icons {
    display: flex;
    align-items: center;
    gap: 6px;
  }
.android-navigation-bar {
    height: 48px;
    background: rgba(9, 9, 11, 0.85);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    display: flex;
    justify-content: space-around;
    align-items: center;
    border-top: 1px solid rgba(255, 255, 255, 0.05);
    z-index: 10;
  }
.android-nav-dot {
    color: #a1a1aa;
    font-size: 14px;
    cursor: pointer;
    transition: color 0.2s ease;
    padding: 10px 20px;
  }
.android-nav-dot:hover { color: #ffffff; }
.android-spotify-tabs {
    height: 56px;
    background: rgba(9, 9, 11, 0.85);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    display: flex;
    border-top: 1px solid rgba(255, 255, 255, 0.05);
    z-index: 10;
  }
.android-tab-btn {
    flex: 1;
    background: transparent;
    border: none;
    color: #a1a1aa;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    font-size: 10px;
    font-weight: 600;
    cursor: pointer;
    gap: 3px;
    transition: color 0.2s ease;
  }
.android-tab-btn.active, .android-tab-btn:hover { color: #ffffff; }
.android-tab-btn .material-icons-round { font-size: 20px !important; }
.bottom-nav-tab-web.active, .bottom-nav-tab-web:hover { color: #ffffff; }
.bottom-nav-tab-web .material-icons-round { font-size: 20px; }
.android-explanation-deck {
    flex: 1;
    min-width: 320px;
    max-width: 480px;
    background: #18181b;
    border: 1px solid #27272a;
    border-radius: 16px;
    padding: 24px;
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
  }
.android-explanation-deck h3 {
    font-size: 18px;
    font-weight: 700;
    color: #ffffff;
    margin-bottom: 4px;
  }
.android-explanation-deck p {
    font-size: 13px;
    color: #a1a1aa;
    line-height: 1.6;
  }
.integration-step-box {
    background: #27272a;
    border-left: 4px solid #3f3f46;
    padding: 14px 16px;
    border-radius: 0 12px 12px 0;
    transition: all 0.2s ease;
  }
.integration-step-box:hover {
    border-left-color: #1db954;
    background: #3f3f46;
  }
.step-badge {
    display: inline-block;
    font-size: 9px;
    font-weight: 800;
    color: #b3b3b3;
    background: rgba(255, 255, 255, 0.1);
    padding: 2px 6px;
    border-radius: 4px;
    margin-bottom: 8px;
    letter-spacing: 0.5px;
  }
.step-badge.active {
    color: #1db954;
    background: rgba(29, 185, 84, 0.15);
  }
.step-badge.haptic {
    color: #38bdf8;
    background: rgba(56, 189, 248, 0.15);
  }
.integration-step-box h4 {
    font-size: 13px;
    font-weight: 700;
    color: #ffffff;
    margin-bottom: 4px;
  }
.integration-step-box p {
    font-size: 11px;
    color: #d4d4d8;
    line-height: 1.5;
  }
`;

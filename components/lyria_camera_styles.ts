/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { css } from "lit";

export default css`
  :host {
    display: block;
    width: 100%;
    height: 100%;
    position: relative;
    background: #000;
    font-family: "Google Sans", sans-serif;
    -webkit-font-smoothing: antialiased;
  }

  #video-container {
    position: absolute;
    inset: 0;
    z-index: 0;
    background: radial-gradient(circle at 50% 50%, var(--ambient-bg, rgba(0, 0, 0, 0)) 0%, #000000 100%);
    transition: background 0.4s ease-out;
  }

  video {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  #overlay {
    position: absolute;
    inset: 0;
    z-index: 1;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    align-items: center;
    padding: 2.5rem;
    box-sizing: border-box;
    background: linear-gradient(
      to top,
      rgba(0, 0, 0, 0.8) 0%,
      rgba(0, 0, 0, 0.4) 40%,
      rgba(0, 0, 0, 0) 100%
    );
  }

  #prompts-container {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 0.75rem;
    margin-bottom: 2rem;
    max-width: 600px;
    width: 100%;
  }

  .prompt-tag {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    border: 1px solid rgba(255, 255, 255, 0.15);
    padding: 0.6rem 1.2rem;
    border-radius: 9999px;
    font-size: 14px;
    color: #ffffff;
    font-weight: 500;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.35);
    transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
    animation: fade-in-up 0.5s cubic-bezier(0.16, 1, 0.3, 1) both;
  }

  @keyframes fade-in-up {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  #controls {
    display: flex;
    align-items: center;
    gap: 1rem;
    z-index: 2;
  }

  .action-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    padding: 0.8rem 1.8rem;
    border-radius: 9999px;
    font-size: 15px;
    font-weight: 600;
    border: none;
    cursor: pointer;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
    transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  }

  .action-btn:active {
    transform: scale(0.96);
  }

  .action-btn.play {
    background: #10b981;
    color: #ffffff;
  }

  .action-btn.play:hover {
    background: #059669;
    box-shadow: 0 4px 25px rgba(16, 185, 129, 0.4);
  }

  .action-btn.stop {
    background: #ef4444;
    color: #ffffff;
  }

  .action-btn.stop:hover {
    background: #dc2626;
    box-shadow: 0 4px 25px rgba(239, 68, 68, 0.4);
  }

  .action-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

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

  @keyframes pulse {
    0%, 100% {
      transform: scale(1);
      opacity: 1;
    }
    50% {
      transform: scale(1.05);
      opacity: 0.8;
    }
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

  canvas#simulation-canvas {
    width: 100%;
    height: 100%;
    display: block;
  }

  #feed-switcher {
    position: absolute;
    top: 1.5rem;
    right: 1.5rem;
    display: flex;
    background: rgba(0, 0, 0, 0.55);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 9999px;
    padding: 0.25rem;
    gap: 0.25rem;
    z-index: 10;
  }

  .switcher-btn {
    display: flex;
    align-items: center;
    gap: 0.35rem;
    background: transparent;
    border: none;
    color: rgba(255, 255, 255, 0.6);
    padding: 0.5rem 1rem;
    border-radius: 9999px;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  }

  .switcher-btn:hover {
    color: #ffffff;
  }

  .switcher-btn.active {
    background: rgba(255, 255, 255, 0.15);
    color: #ffffff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  }

  .switcher-btn .material-icons-round {
    font-size: 16px;
  }

  /* --- Unified Navigation Layout and Security Hub Styles --- */

  #app-layout {
    display: flex;
    flex-direction: column;
    width: 100vw;
    height: 100vh;
    overflow: hidden;
    background: radial-gradient(circle at 50% 50%, var(--ambient-bg, rgba(99, 102, 241, 0.12)) 0%, #000000 100%);
    color: #ffffff;
    font-family: "Google Sans", sans-serif;
    transition: background 0.4s ease-out;
  }

  #top-nav-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: rgba(10, 10, 12, 0.9);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    padding: 0.75rem 2rem;
    height: 64px;
    box-sizing: border-box;
    z-index: 50;
    flex-shrink: 0;
  }

  .brand {
    display: flex;
    align-items: center;
    gap: 0.85rem;
  }

  .brand-icon {
    font-size: 22px;
    color: #818cf8;
  }

  .brand-name {
    font-weight: 700;
    font-size: 15px;
    letter-spacing: -0.01em;
    color: #ffffff;
    text-transform: uppercase;
  }

  .status-pill {
    display: inline-flex;
    align-items: center;
    gap: 0.45rem;
    padding: 0.25rem 0.65rem;
    border-radius: 9999px;
    font-size: 10px;
    font-weight: 700;
    letter-spacing: 0.03em;
    text-transform: uppercase;
    transition: all 0.3s ease;
  }

  .status-pill.standard {
    background: rgba(16, 185, 129, 0.12);
    border: 1px solid rgba(16, 185, 129, 0.25);
    color: #34d399;
  }

  .status-pill.wearable {
    background: rgba(99, 102, 241, 0.12);
    border: 1px solid rgba(99, 102, 241, 0.25);
    color: #818cf8;
  }

  .status-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: currentColor;
    box-shadow: 0 0 8px currentColor;
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

  .nav-tabs {
    display: flex;
    gap: 0.5rem;
  }

  .nav-tab {
    display: flex;
    align-items: center;
    gap: 0.45rem;
    background: transparent;
    border: none;
    color: rgba(255, 255, 255, 0.55);
    padding: 0.5rem 1rem;
    border-radius: 8px;
    font-size: 13.5px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  }

  .nav-tab:hover {
    color: #ffffff;
    background: rgba(255, 255, 255, 0.05);
  }

  .nav-tab.active {
    color: #ffffff;
    background: rgba(255, 255, 255, 0.1);
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

  #content-area {
    flex: 1;
    position: relative;
    overflow-y: auto;
    background: #030303;
  }

  #security-hub {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2.5rem 2rem;
    color: #ffffff;
    box-sizing: border-box;
  }

  .sec-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 2rem;
    gap: 1.5rem;
  }

  .sec-title-area {
    flex: 1;
  }

  .sec-title {
    font-size: 24px;
    font-weight: 700;
    margin: 0 0 0.35rem 0;
    letter-spacing: -0.02em;
  }

  .sec-subtitle {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.5);
    margin: 0;
    line-height: 1.45;
  }

  .mock-trigger-btn {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    background: #6366f1;
    color: #ffffff;
    border: none;
    padding: 0.75rem 1.25rem;
    border-radius: 12px;
    font-weight: 600;
    font-size: 13.5px;
    cursor: pointer;
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
    transition: all 0.25s ease;
  }

  .mock-trigger-btn:hover {
    background: #4f46e5;
    box-shadow: 0 6px 16px rgba(99, 102, 241, 0.4);
    transform: translateY(-1px);
  }

  .mock-trigger-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    transform: none;
  }

  .webhook-config-card {
    display: grid;
    grid-template-columns: 1.35fr 1fr;
    background: rgba(255, 255, 255, 0.02);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 16px;
    margin-bottom: 2.5rem;
    overflow: hidden;
  }

  .webhook-details {
    padding: 2rem;
    border-right: 1px solid rgba(255, 255, 255, 0.08);
  }

  .webhook-header {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 0.75rem;
  }

  .webhook-header h3 {
    font-size: 16px;
    font-weight: 700;
    margin: 0;
  }

  .webhook-icon {
    color: #6366f1;
  }

  .webhook-desc {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.6);
    line-height: 1.5;
    margin: 0 0 1.5rem 0;
  }

  .webhook-fields {
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
  }

  .field-group {
    display: flex;
    flex-direction: column;
    gap: 0.4rem;
  }

  .field-label {
    font-size: 10px;
    font-weight: 700;
    text-transform: uppercase;
    color: rgba(255, 255, 255, 0.4);
    letter-spacing: 0.05em;
  }

  .url-copy-box {
    display: flex;
    background: #000000;
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    padding: 0.25rem;
    align-items: center;
  }

  .url-copy-box input {
    flex: 1;
    background: transparent;
    border: none;
    color: rgba(255, 255, 255, 0.85);
    padding: 0.5rem;
    font-family: "JetBrains Mono", monospace;
    font-size: 12px;
    outline: none;
  }

  .copy-btn {
    display: flex;
    align-items: center;
    gap: 0.35rem;
    background: rgba(255, 255, 255, 0.1);
    color: #ffffff;
    border: none;
    padding: 0.4rem 0.85rem;
    border-radius: 6px;
    font-size: 12px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .copy-btn:hover {
    background: rgba(255, 255, 255, 0.18);
  }

  .field-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1.5rem;
  }

  .field-value {
    font-size: 13px;
    font-family: "JetBrains Mono", monospace;
    color: #a5b4fc;
  }

  .webhook-benefits {
    padding: 2rem;
    background: rgba(99, 102, 241, 0.01);
    display: flex;
    flex-direction: column;
    justify-content: center;
  }

  .webhook-benefits h4 {
    display: flex;
    align-items: center;
    gap: 0.4rem;
    font-size: 13.5px;
    font-weight: 700;
    margin: 0 0 1rem 0;
  }

  .webhook-benefits ul {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 0.85rem;
  }

  .webhook-benefits li {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.6);
    line-height: 1.45;
    position: relative;
    padding-left: 1.25rem;
  }

  .webhook-benefits li::before {
    content: "✦";
    color: #6366f1;
    position: absolute;
    left: 0;
    font-size: 10px;
    top: 1px;
  }

  .alerts-section {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .alerts-section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
    padding-bottom: 0.75rem;
  }

  .alerts-section-header h3 {
    font-size: 15px;
    font-weight: 700;
    margin: 0;
    color: rgba(255, 255, 255, 0.8);
  }

  .refresh-btn {
    display: flex;
    align-items: center;
    gap: 0.35rem;
    background: transparent;
    border: 1px solid rgba(255, 255, 255, 0.1);
    color: rgba(255, 255, 255, 0.7);
    padding: 0.4rem 0.85rem;
    border-radius: 8px;
    font-size: 12px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .refresh-btn:hover {
    border-color: rgba(255, 255, 255, 0.2);
    color: #ffffff;
  }

  .alerts-loading-state,
  .alerts-empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 4rem 2rem;
    background: rgba(255, 255, 255, 0.01);
    border: 1px dashed rgba(255, 255, 255, 0.08);
    border-radius: 12px;
    text-align: center;
  }

  .loading-icon,
  .empty-icon {
    font-size: 3rem;
    margin-bottom: 1rem;
  }

  .loading-icon {
    color: #6366f1;
  }

  .empty-icon {
    color: #10b981;
  }

  .spin {
    animation: spin-kf 1.2s linear infinite;
  }

  @keyframes spin-kf {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }

  .alerts-empty-state h4 {
    font-size: 15px;
    font-weight: 700;
    margin: 0 0 0.5rem 0;
  }

  .alerts-empty-state p {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.4);
    max-width: 440px;
    margin: 0;
    line-height: 1.5;
  }

  .alerts-list {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .alert-card {
    background: rgba(255, 255, 255, 0.02);
    border: 1px solid rgba(255, 255, 255, 0.06);
    border-radius: 12px;
    overflow: hidden;
    transition: all 0.2s ease;
  }

  .alert-card:hover {
    border-color: rgba(255, 255, 255, 0.12);
    background: rgba(255, 255, 255, 0.03);
  }

  .alert-summary {
    display: flex;
    padding: 1.25rem 1.5rem;
    align-items: center;
    cursor: pointer;
    justify-content: space-between;
    gap: 1.5rem;
  }

  .alert-left {
    display: flex;
    align-items: center;
    gap: 1rem;
    min-width: 180px;
  }

  .severity-badge {
    padding: 0.25rem 0.6rem;
    border-radius: 6px;
    font-size: 10px;
    font-weight: 800;
    letter-spacing: 0.05em;
  }

  .severity-critical {
    background: rgba(239, 68, 68, 0.15);
    color: #ef4444;
    border: 1px solid rgba(239, 68, 68, 0.25);
  }

  .severity-high {
    background: rgba(249, 115, 22, 0.15);
    color: #f97316;
    border: 1px solid rgba(249, 115, 22, 0.25);
  }

  .severity-medium {
    background: rgba(234, 179, 8, 0.15);
    color: #eab308;
    border: 1px solid rgba(234, 179, 8, 0.25);
  }

  .severity-low {
    background: rgba(59, 130, 246, 0.15);
    color: #3b82f6;
    border: 1px solid rgba(59, 130, 246, 0.25);
  }

  .alert-info {
    display: flex;
    flex-direction: column;
    gap: 0.15rem;
  }

  .package-name {
    font-size: 14px;
    font-weight: 700;
    margin: 0;
  }

  .ecosystem-tag {
    font-size: 10px;
    text-transform: uppercase;
    color: rgba(255, 255, 255, 0.4);
    letter-spacing: 0.05em;
  }

  .alert-mid {
    flex: 1;
  }

  .advisory-summary {
    font-size: 13.5px;
    color: rgba(255, 255, 255, 0.85);
    margin: 0;
    line-height: 1.4;
  }

  .alert-right {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .alert-date {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.4);
  }

  .expand-arrow {
    color: rgba(255, 255, 255, 0.3);
  }

  .alert-card.expanded {
    border-color: rgba(99, 102, 241, 0.3);
    background: rgba(99, 102, 241, 0.01);
  }

  .alert-card.expanded .expand-arrow {
    color: #6366f1;
  }

  .alert-details {
    padding: 1.5rem;
    background: rgba(0, 0, 0, 0.2);
    border-top: 1px solid rgba(255, 255, 255, 0.06);
  }

  .details-grid {
    display: grid;
    grid-template-columns: 1fr 1.20fr;
    gap: 2rem;
  }

  .details-left h5,
  .upgrade-plan-box h5 {
    font-size: 13px;
    font-weight: 700;
    margin: 0 0 0.75rem 0;
    color: rgba(255, 255, 255, 0.9);
  }

  .description-text {
    font-size: 12.5px;
    color: rgba(255, 255, 255, 0.6);
    line-height: 1.6;
    margin: 0 0 1.5rem 0;
  }

  .meta-row {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .meta-item {
    font-size: 12.5px;
    display: flex;
    gap: 0.5rem;
    align-items: center;
  }

  .meta-item strong {
    color: rgba(255, 255, 255, 0.4);
    font-weight: 600;
  }

  .version-tag {
    background: rgba(16, 185, 129, 0.1);
    color: #10b981;
    padding: 0.15rem 0.5rem;
    border-radius: 4px;
    font-family: "JetBrains Mono", monospace;
    font-size: 11px;
  }

  .file-tag {
    background: rgba(255, 255, 255, 0.08);
    color: rgba(255, 255, 255, 0.8);
    padding: 0.15rem 0.5rem;
    border-radius: 4px;
    font-family: "JetBrains Mono", monospace;
    font-size: 11px;
  }

  .upgrade-plan-box {
    background: rgba(255, 255, 255, 0.01);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 12px;
    padding: 1.25rem 1.5rem;
  }

  .plan-header {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 1.25rem;
    position: relative;
  }

  .plan-header h5 {
    margin: 0;
    font-size: 13.5px;
  }

  .plan-icon {
    color: #818cf8;
    font-size: 18px;
  }

  .risk-badge {
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
    font-size: 9px;
    font-weight: 800;
    text-transform: uppercase;
    padding: 0.15rem 0.4rem;
    border-radius: 4px;
  }

  .risk-low {
    background: rgba(16, 185, 129, 0.1);
    color: #10b981;
  }

  .risk-medium {
    background: rgba(245, 158, 11, 0.1);
    color: #f59e0b;
  }

  .risk-high {
    background: rgba(239, 68, 68, 0.1);
    color: #ef4444;
  }

  .plan-section {
    margin-bottom: 1rem;
  }

  .plan-section h6 {
    font-size: 11px;
    font-weight: 700;
    text-transform: uppercase;
    color: rgba(255, 255, 255, 0.45);
    margin: 0 0 0.35rem 0;
    letter-spacing: 0.03em;
  }

  .plan-section p {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.7);
    line-height: 1.5;
    margin: 0;
  }

  .command-box {
    display: flex;
    background: #000000;
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 8px;
    padding: 0.5rem 0.75rem;
    align-items: center;
    justify-content: space-between;
    margin-top: 0.25rem;
  }

  .command-box code {
    font-family: "JetBrains Mono", monospace;
    font-size: 11.5px;
    color: #34d399;
  }

  .copy-command-btn {
    background: transparent;
    border: none;
    color: rgba(255, 255, 255, 0.4);
    cursor: pointer;
    padding: 0.25rem;
    border-radius: 4px;
    display: flex;
    align-items: center;
    transition: all 0.2s ease;
  }

  .copy-command-btn:hover {
    color: #ffffff;
    background: rgba(255, 255, 255, 0.08);
  }

  .copy-command-btn .material-icons-round {
    font-size: 15px;
  }

  .video-feedback-overlay {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.85);
    z-index: 5;
    padding: 2rem;
    text-align: center;
    color: #ffffff;
    box-sizing: border-box;
    animation: fade-in-up 0.3s ease-out;
  }

  .feedback-title {
    font-size: 16px;
    font-weight: 700;
    margin-bottom: 0.5rem;
    color: #fca5a5;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .feedback-title.info {
    color: #818cf8;
  }

  .feedback-desc {
    font-size: 13.5px;
    color: rgba(255, 255, 255, 0.7);
    max-width: 400px;
    line-height: 1.5;
    margin-bottom: 1.25rem;
  }

  .feedback-btn {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: #ffffff;
    padding: 0.5rem 1.25rem;
    border-radius: 9999px;
    font-size: 12.5px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 0.35rem;
    transition: all 0.2s ease;
  }

  .feedback-btn:hover {
    background: rgba(255, 255, 255, 0.2);
    border-color: rgba(255, 255, 255, 0.4);
  }

  .feedback-loading-spinner {
    width: 32px;
    height: 32px;
    border: 3px solid rgba(99, 102, 241, 0.2);
    border-top: 3px solid #6366f1;
    border-radius: 50%;
    animation: spin-kf 1s linear infinite;
    margin-bottom: 1rem;
  }

  .feedback-icon {
    font-size: 40px;
    color: #fca5a5;
    margin-bottom: 0.75rem;
  }

  .feedback-btn-icon {
    font-size: 16px;
  }

  .btn-group {
    display: flex;
    gap: 0.75rem;
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

  .voice-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .voice-title {
    display: flex;
    align-items: center;
    gap: 0.45rem;
    font-size: 13px;
    font-weight: 700;
    color: #818cf8;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  .voice-title .material-icons-round {
    font-size: 16px;
  }

  .voice-status-indicator {
    font-size: 10px;
    font-weight: 800;
    padding: 0.2rem 0.5rem;
    border-radius: 6px;
    text-transform: uppercase;
    letter-spacing: 0.03em;
  }

  .voice-status-indicator.idle {
    background: rgba(255, 255, 255, 0.05);
    color: rgba(255, 255, 255, 0.6);
  }

  .voice-status-indicator.listening {
    background: rgba(239, 68, 68, 0.15);
    color: #f87171;
    animation: pulse 1.5s infinite ease-in-out;
  }

  .voice-status-indicator.processing {
    background: rgba(245, 158, 11, 0.15);
    color: #fbbf24;
  }

  .voice-status-indicator.speaking {
    background: rgba(16, 185, 129, 0.15);
    color: #34d399;
  }

  .voice-body {
    display: flex;
    align-items: center;
    gap: 1rem;
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

  @keyframes mic-pulse-kf {
    0% {
      box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7);
    }
    70% {
      box-shadow: 0 0 0 12px rgba(239, 68, 68, 0);
    }
    100% {
      box-shadow: 0 0 0 0 rgba(239, 68, 68, 0);
    }
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

  .voice-display {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
    min-width: 0;
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

  .gemini-reply-text {
    font-size: 12px;
    color: #cbd5e1;
    line-height: 1.4;
    background: rgba(255, 255, 255, 0.04);
    border-radius: 8px;
    padding: 0.4rem 0.6rem;
    border-left: 2px solid #818cf8;
    max-height: 64px;
    overflow-y: auto;
  }

  .equalizer-container {
    display: flex;
    align-items: flex-end;
    gap: 3px;
    height: 16px;
    padding-bottom: 2px;
  }

  .eq-bar {
    width: 3px;
    background: #818cf8;
    border-radius: 9999px;
    height: 3px;
    transition: height 0.15s ease;
  }

  .eq-bar.animating {
    animation: eq-bounce-kf 0.8s ease-in-out infinite alternate;
  }

  .eq-bar:nth-child(1) { animation-delay: 0.1s; }
  .eq-bar:nth-child(2) { animation-delay: 0.3s; }
  .eq-bar:nth-child(3) { animation-delay: 0.2s; }
  .eq-bar:nth-child(4) { animation-delay: 0.4s; }
  .eq-bar:nth-child(5) { animation-delay: 0.15s; }

  @keyframes eq-bounce-kf {
    from { height: 3px; }
    to { height: 16px; }
  }

  .audio-playback-actions {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-top: 0.25rem;
  }

  .audio-mini-btn {
    display: inline-flex;
    align-items: center;
    gap: 0.25rem;
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 6px;
    padding: 0.25rem 0.5rem;
    font-size: 10px;
    font-weight: 700;
    color: rgba(255, 255, 255, 0.7);
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .audio-mini-btn:hover {
    background: rgba(255, 255, 255, 0.12);
    color: #ffffff;
    border-color: rgba(255, 255, 255, 0.2);
  }

  .audio-mini-btn .material-icons-round {
    font-size: 12px;
  }

  /* --- Music Active Visual Indicators & Glow --- */
  #video-container.music-active::after {
    content: "";
    position: absolute;
    inset: 0;
    pointer-events: none;
    border: 3px solid var(--ambient-bg, rgba(129, 140, 248, 0.45));
    box-shadow: inset 0 0 70px var(--ambient-bg, rgba(129, 140, 248, 0.45));
    animation: music-border-glow-kf 2.5s infinite ease-in-out;
    z-index: 2;
    transition: border-color 0.4s ease-out, box-shadow 0.4s ease-out;
  }

  #music-radar-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.55rem;
    background: rgba(10, 10, 12, 0.85);
    border: 1px solid var(--ambient-bg-bright, rgba(129, 140, 248, 0.4));
    padding: 0.4rem 0.85rem;
    border-radius: 9999px;
    margin-right: 0.5rem;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.55);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    animation: fade-in-scale-kf 0.35s cubic-bezier(0.16, 1, 0.3, 1) forwards;
    transition: border-color 0.4s ease-out;
  }

  .radar-dot {
    width: 7px;
    height: 7px;
    border-radius: 50%;
    background: #10b981;
    box-shadow: 0 0 10px #10b981;
  }

  .radar-text {
    font-size: 10px;
    font-weight: 800;
    color: #e2e8f0;
    letter-spacing: 0.05em;
    text-transform: uppercase;
  }

  .audio-mini-waves {
    display: flex;
    align-items: flex-end;
    gap: 2px;
    height: 11px;
    padding-bottom: 1px;
  }

  .audio-mini-waves .wave-bar {
    width: 2px;
    background: var(--ambient-bg-solid, #818cf8);
    border-radius: 9999px;
    height: 2px;
    transition: background-color 0.4s ease-out;
  }

  .audio-mini-waves .wave-bar.animate-eq {
    animation: eq-mini-bounce-kf 0.6s ease-in-out infinite alternate;
  }

  @keyframes music-border-glow-kf {
    0%, 100% {
      box-shadow: inset 0 0 40px var(--ambient-bg, rgba(129, 140, 248, 0.3));
      border-color: var(--ambient-bg, rgba(129, 140, 248, 0.25));
    }
    50% {
      box-shadow: inset 0 0 85px var(--ambient-bg-bright, rgba(129, 140, 248, 0.65)), 0 0 12px var(--ambient-bg, rgba(129, 140, 248, 0.25));
      border-color: var(--ambient-bg-bright, rgba(129, 140, 248, 0.55));
    }
  }

  @keyframes eq-mini-bounce-kf {
    from { height: 2px; }
    to { height: 10px; }
  }

  @keyframes fade-in-scale-kf {
    from {
      opacity: 0;
      transform: scale(0.9) translateY(2px);
    }
    to {
      opacity: 1;
      transform: scale(1) translateY(0);
    }
  }

  /* --- Live Audio Visualizer Canvas --- */
  #music-visualizer-canvas {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 1;
    mix-blend-mode: screen;
  }

  /* --- Jacob Collier Instrument Orchestrator --- */
  .orchestrator-card {
    background: rgba(10, 10, 15, 0.88);
    backdrop-filter: blur(24px);
    -webkit-backdrop-filter: blur(24px);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 16px;
    padding: 1rem 1.25rem;
    width: 100%;
    max-width: 420px;
    margin-bottom: 1.5rem;
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.75), 
                inset 0 0 20px rgba(129, 140, 248, 0.05);
    display: flex;
    flex-direction: column;
    gap: 0.85rem;
    animation: fade-in-scale-kf 0.45s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  }

  .orchestrator-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .orchestrator-title {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 13px;
    font-weight: 800;
    color: #f472b6;
    text-transform: uppercase;
    letter-spacing: 0.08em;
  }

  .orchestrator-title .material-icons-round {
    font-size: 18px;
    color: #f472b6;
  }

  .orchestrator-badge {
    background: rgba(244, 114, 182, 0.15);
    color: #f472b6;
    font-size: 9px;
    font-weight: 800;
    padding: 0.2rem 0.5rem;
    border-radius: 6px;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    border: 1px solid rgba(244, 114, 182, 0.25);
  }

  .orchestrator-description {
    font-size: 11px;
    color: #94a3b8;
    line-height: 1.4;
  }

  /* Instrument Selection Tabs */
  .orchestrator-sections {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 0.35rem;
    background: rgba(255, 255, 255, 0.03);
    padding: 0.25rem;
    border-radius: 10px;
    border: 1px solid rgba(255, 255, 255, 0.06);
  }

  .orchestrator-section-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 0.25rem;
    padding: 0.45rem 0.2rem;
    background: transparent;
    border: 1px solid transparent;
    border-radius: 8px;
    color: rgba(255, 255, 255, 0.6);
    cursor: pointer;
    transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  }

  .orchestrator-section-btn:hover {
    color: #ffffff;
    background: rgba(255, 255, 255, 0.05);
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

  .orchestrator-section-btn .section-icon {
    font-size: 16px;
  }

  .orchestrator-section-btn .section-label {
    font-size: 9px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.02em;
  }

  /* Interaction Pad */
  .orchestrator-gesture-pad {
    position: relative;
    width: 100%;
    height: 140px;
    background: radial-gradient(circle at center, rgba(30, 30, 45, 0.4) 0%, rgba(10, 10, 15, 0.95) 100%);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    overflow: hidden;
    cursor: crosshair;
    user-select: none;
    touch-action: none; /* Disable browser scrolling on touch drag */
    transition: border-color 0.3s ease;
  }

  .orchestrator-gesture-pad:hover {
    border-color: rgba(129, 140, 248, 0.3);
  }

  .orchestrator-gesture-pad.active {
    border-color: rgba(244, 114, 182, 0.5);
  }

  /* Grid Background Overlay */
  .gesture-pad-grid {
    position: absolute;
    inset: 0;
    background-size: 20px 20px;
    background-image: 
      linear-gradient(to right, rgba(255, 255, 255, 0.03) 1px, transparent 1px),
      linear-gradient(to bottom, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
    pointer-events: none;
  }

  /* Glow points and trackers */
  .gesture-pad-target {
    position: absolute;
    width: 24px;
    height: 24px;
    margin-left: -12px;
    margin-top: -12px;
    border-radius: 50%;
    border: 2px solid #818cf8;
    background: rgba(129, 140, 248, 0.25);
    box-shadow: 0 0 15px #818cf8, inset 0 0 5px #ffffff;
    pointer-events: none;
    transform: scale(0);
    transition: transform 0.15s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    z-index: 5;
  }

  .orchestrator-gesture-pad.active .gesture-pad-target {
    transform: scale(1);
  }

  /* Floating Waveform inside Gesture Pad */
  .gesture-pad-canvas {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 2;
  }

  /* Labels inside Gesture Pad */
  .gesture-pad-label-x {
    position: absolute;
    bottom: 6px;
    left: 50%;
    transform: translateX(-50%);
    font-size: 8px;
    font-weight: 800;
    color: rgba(255, 255, 255, 0.35);
    text-transform: uppercase;
    letter-spacing: 0.08em;
    pointer-events: none;
  }

  .gesture-pad-label-y {
    position: absolute;
    left: 6px;
    top: 50%;
    transform: translateY(-50%) rotate(-90deg);
    font-size: 8px;
    font-weight: 800;
    color: rgba(255, 255, 255, 0.35);
    text-transform: uppercase;
    letter-spacing: 0.08em;
    pointer-events: none;
    white-space: nowrap;
  }

  .gesture-pad-status {
    position: absolute;
    top: 6px;
    right: 8px;
    font-size: 9px;
    font-weight: 700;
    color: rgba(255, 255, 255, 0.45);
    pointer-events: none;
    font-family: monospace;
  }

  /* Controls and Sliders */
  .orchestrator-sliders {
    display: flex;
    flex-direction: column;
    gap: 0.65rem;
  }

  .orchestrator-slider-row {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
  }

  .orchestrator-slider-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .orchestrator-slider-label {
    font-size: 10px;
    font-weight: 800;
    color: rgba(255, 255, 255, 0.6);
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  .orchestrator-slider-value {
    font-size: 10px;
    font-weight: 700;
    color: #f472b6;
    font-family: monospace;
  }

  .orchestrator-slider-input {
    -webkit-appearance: none;
    width: 100%;
    height: 5px;
    border-radius: 9999px;
    background: rgba(255, 255, 255, 0.1);
    outline: none;
    cursor: pointer;
  }

  .orchestrator-slider-input::-webkit-slider-thumb {
    -webkit-appearance: none;
    width: 14px;
    height: 14px;
    border-radius: 50%;
    background: #f472b6;
    box-shadow: 0 0 8px rgba(244, 114, 182, 0.6);
    border: none;
    transition: transform 0.15s ease;
  }

  .orchestrator-slider-input::-webkit-slider-thumb:hover {
    transform: scale(1.2);
  }

  .orchestrator-footer-tip {
    font-size: 10px;
    color: rgba(255, 255, 255, 0.4);
    text-align: center;
    line-height: 1.3;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.35rem;
  }

  .orchestrator-footer-tip .material-icons-round {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.4);
  }

  /* --- Orchestrator Actions & Help Toggle --- */
  .orchestrator-actions {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .orchestrator-help-btn {
    background: transparent;
    border: none;
    color: rgba(255, 255, 255, 0.45);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0.25rem;
    border-radius: 50%;
    transition: all 0.2s ease;
  }

  .orchestrator-help-btn:hover {
    color: #f472b6;
    background: rgba(244, 114, 182, 0.15);
  }

  .orchestrator-help-btn .material-icons-round {
    font-size: 18px;
  }

  /* --- Gesture Tutorial Overlay --- */
  .gesture-tutorial-overlay {
    position: absolute;
    inset: 0;
    background: rgba(10, 10, 15, 0.94);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 0.75rem;
    z-index: 10;
    animation: fade-in-kf 0.3s ease-out forwards;
  }

  .gesture-tutorial-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.5rem;
    width: 100%;
    z-index: 12;
  }

  .tutorial-guide-title {
    display: flex;
    align-items: center;
    gap: 0.35rem;
    font-size: 11px;
    font-weight: 800;
    color: #f472b6;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  .tutorial-guide-title .material-icons-round {
    font-size: 14px;
  }

  .tutorial-instructions {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 0.4rem;
    width: 100%;
    max-width: 280px;
  }

  .tutorial-instructions li {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 10px;
    color: #e2e8f0;
    line-height: 1.3;
  }

  .instruction-bullet {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 16px;
    height: 16px;
    border-radius: 4px;
    font-size: 9px;
    font-weight: 800;
    color: #ffffff;
    flex-shrink: 0;
  }

  .instruction-bullet.x {
    background: rgba(129, 140, 248, 0.3);
    color: #818cf8;
    border: 1px solid rgba(129, 140, 248, 0.4);
  }

  .instruction-bullet.y {
    background: rgba(244, 114, 182, 0.3);
    color: #f472b6;
    border: 1px solid rgba(244, 114, 182, 0.4);
  }

  .instruction-bullet.scroll {
    background: rgba(6, 182, 212, 0.3);
    color: #22d3ee;
    border: 1px solid rgba(6, 182, 212, 0.4);
  }

  .tutorial-got-it-btn {
    background: linear-gradient(135deg, #818cf8 0%, #f472b6 100%);
    border: none;
    color: #ffffff;
    font-size: 10px;
    font-weight: 800;
    text-transform: uppercase;
    padding: 0.35rem 0.95rem;
    border-radius: 9999px;
    cursor: pointer;
    box-shadow: 0 4px 12px rgba(244, 114, 182, 0.25);
    transition: all 0.2s ease;
    margin-top: 0.25rem;
  }

  .tutorial-got-it-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(244, 114, 182, 0.4);
  }

  /* --- Path & Hand Gesture Animation --- */
  .tutorial-path-animation {
    position: absolute;
    inset: 0;
    pointer-events: none;
    z-index: 11;
    overflow: hidden;
  }

  .tutorial-path-point {
    position: absolute;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: rgba(244, 114, 182, 0.6);
    box-shadow: 0 0 8px #f472b6;
    opacity: 0.4;
  }

  .tutorial-path-point.start {
    left: 20%;
    top: 50%;
  }

  .tutorial-path-point.end {
    left: 80%;
    top: 50%;
  }

  .tutorial-path-hand {
    position: absolute;
    left: 20%;
    top: 50%;
    margin-left: -12px;
    margin-top: -12px;
    color: #ffffff;
    text-shadow: 0 0 10px rgba(244, 114, 182, 0.8);
    animation: tutorial-swipe-kf 3.5s ease-in-out infinite;
    opacity: 0;
    pointer-events: none;
  }

  .tutorial-path-hand .material-icons-round {
    font-size: 24px;
  }

  @keyframes tutorial-swipe-kf {
    0% {
      left: 20%;
      top: 50%;
      opacity: 0;
      transform: scale(0.95);
    }
    10% {
      left: 20%;
      top: 50%;
      opacity: 0.95;
      transform: scale(1.1);
    }
    40% {
      left: 80%;
      top: 50%;
      opacity: 0.95;
      transform: scale(1.1);
    }
    50% {
      left: 80%;
      top: 50%;
      opacity: 0;
      transform: scale(0.95);
    }
    60% {
      left: 50%;
      top: 25%;
      opacity: 0;
      transform: scale(0.95);
    }
    70% {
      left: 50%;
      top: 25%;
      opacity: 0.95;
      transform: scale(1.1);
    }
    90% {
      left: 50%;
      top: 75%;
      opacity: 0.95;
      transform: scale(1.1);
    }
    100% {
      left: 50%;
      top: 75%;
      opacity: 0;
      transform: scale(0.95);
    }
  }

  @keyframes fade-in-kf {
    from { opacity: 0; }
    to { opacity: 1; }
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

  /* ==========================================================================
     Android Spotify-Style Companion App Simulation Styles
     ========================================================================== */
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
    background: #121212;
    border: 12px solid #2e2e2e;
    border-radius: 48px;
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.7);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    user-select: none;
    flex-shrink: 0;
  }

  /* Status Bar */
  .android-status-bar {
    height: 32px;
    background: #121212;
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

  .font-icon-small {
    font-size: 14px !important;
  }

  .font-icon-tiny {
    font-size: 11px !important;
    vertical-align: middle;
  }

  /* Screen Content Wrapper */
  .android-screen-content {
    flex: 1;
    overflow-y: auto;
    background: #121212;
    display: flex;
    flex-direction: column;
    position: relative;
  }

  .android-screen-content::-webkit-scrollbar {
    width: 4px;
  }

  .android-screen-content::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 2px;
  }

  /* Navigation Bar */
  .android-navigation-bar {
    height: 48px;
    background: #121212;
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

  .android-nav-dot:hover {
    color: #ffffff;
  }

  /* 1. Onboarding / Welcome Screen */
  .android-flow-welcome {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    padding: 32px 24px;
    background: linear-gradient(to bottom, #1db9542a 0%, #121212 60%);
  }

  .android-spotify-logo-container {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 24px;
    justify-content: center;
  }

  .logo-soundwave {
    font-size: 40px !important;
  }

  .spotify-logo-text {
    font-size: 32px;
    font-weight: 800;
    color: #ffffff;
    letter-spacing: -1px;
  }

  .welcome-heading {
    font-size: 28px;
    font-weight: 800;
    line-height: 1.25;
    color: #ffffff;
    text-align: center;
    margin-bottom: 32px;
    letter-spacing: -0.5px;
  }

  .welcome-buttons-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .welcome-btn-primary {
    background: #1db954;
    color: #000000;
    border: none;
    border-radius: 500px;
    padding: 14px;
    font-size: 15px;
    font-weight: 700;
    cursor: pointer;
    transition: transform 0.1s ease, background-color 0.2s ease;
  }

  .welcome-btn-primary:hover {
    transform: scale(1.02);
    background-color: #1ed760;
  }

  .welcome-btn-secondary {
    background: transparent;
    color: #ffffff;
    border: 1px solid rgba(255, 255, 255, 0.4);
    border-radius: 500px;
    padding: 13px;
    font-size: 14px;
    font-weight: 700;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    transition: all 0.2s ease;
  }

  .welcome-btn-secondary:hover {
    border-color: #ffffff;
    background: rgba(255, 255, 255, 0.05);
  }

  .btn-icon-g {
    font-size: 16px !important;
  }

  .welcome-btn-link {
    background: transparent;
    border: none;
    color: #ffffff;
    font-size: 14px;
    font-weight: 700;
    padding: 12px;
    cursor: pointer;
    text-align: center;
    margin-top: 8px;
  }

  .welcome-btn-link:hover {
    text-decoration: underline;
  }

  /* 2. Credentials Registration Forms */
  .android-flow-inputs {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 24px;
  }

  .android-flow-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 32px;
  }

  .android-header-back {
    background: transparent;
    border: none;
    color: #ffffff;
    cursor: pointer;
    padding: 4px;
    display: flex;
    align-items: center;
  }

  .android-header-title {
    font-size: 16px;
    font-weight: 700;
    color: #ffffff;
  }

  .input-form-container {
    display: flex;
    flex-direction: column;
    flex: 1;
  }

  .form-title {
    font-size: 24px;
    font-weight: 800;
    color: #ffffff;
    margin-bottom: 12px;
  }

  .form-text-input {
    background: #2a2a2a;
    border: 1px solid transparent;
    border-radius: 8px;
    padding: 14px 16px;
    font-size: 15px;
    color: #ffffff;
    outline: none;
    transition: border-color 0.2s ease;
    margin-bottom: 8px;
    width: 100%;
    box-sizing: border-box;
  }

  .form-text-input:focus {
    border-color: #1db954;
    background: #333333;
  }

  .form-subtext {
    font-size: 11px;
    color: #a1a1aa;
    margin-bottom: 32px;
    line-height: 1.4;
  }

  .form-next-btn {
    background: #535353;
    color: #b3b3b3;
    border: none;
    border-radius: 500px;
    padding: 14px;
    font-size: 15px;
    font-weight: 700;
    cursor: not-allowed;
    text-align: center;
    transition: all 0.2s ease;
  }

  .form-next-btn.active {
    background: #ffffff;
    color: #000000;
    cursor: pointer;
  }

  .form-next-btn.active:hover {
    transform: scale(1.02);
  }

  .name-input-wrapper {
    position: relative;
    display: flex;
    align-items: center;
  }

  .name-check-icon {
    position: absolute;
    right: 16px;
    font-size: 20px !important;
  }

  /* Terms agreements & checkboxes */
  .terms-agreement-block {
    margin-bottom: 24px;
    padding-top: 8px;
  }

  .terms-paragraph {
    font-size: 11px;
    color: #d4d4d8;
    line-height: 1.5;
  }

  .checkbox-form-row {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    margin-bottom: 16px;
    cursor: pointer;
  }

  .checkbox-form-row input[type="checkbox"] {
    accent-color: #1db954;
    width: 16px;
    height: 16px;
    margin-top: 2px;
    flex-shrink: 0;
  }

  .checkbox-form-row label {
    font-size: 11px;
    color: #ffffff;
    line-height: 1.4;
    user-select: none;
    cursor: pointer;
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

  /* 5. Mini Player (Persistent on Home / Search) */
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

  .mini-player-action-btn:hover {
    color: #ffffff;
  }

  .play-pause-btn {
    color: #ffffff;
  }

  /* 6. Tabs on Bottom Screen */
  .android-spotify-tabs {
    height: 56px;
    background: #121212;
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

  .android-tab-btn.active, .android-tab-btn:hover {
    color: #ffffff;
  }

  .android-tab-btn .material-icons-round {
    font-size: 20px !important;
  }

  /* Explanation / Side Deck */
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

  /* Animation */
  .animate-fade-in {
    animation: fadeIn 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  }

  @keyframes fadeIn {
    from {
      opacity: 0;
      transform: translateY(8px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
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

  @keyframes slideUp {
    from {
      transform: translateY(100%);
    }
    to {
      transform: translateY(0);
    }
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

  @keyframes fadeInOverlay {
    from { opacity: 0; }
    to { opacity: 1; }
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

  @keyframes slideUpSheet {
    from {
      transform: translateY(100%);
    }
    to {
      transform: translateY(0);
    }
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

  /* Community, Go Live & Presets styles */
  .android-flow-community {
    height: 100%;
    overflow-y: auto;
    padding: 16px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    text-align: left;
    background: linear-gradient(to bottom, #111827 0%, #121212 60%);
  }

  .community-header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  .community-title-large {
    font-size: 22px;
    font-weight: 800;
    color: #ffffff;
    margin: 0;
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

  @keyframes dotPulse {
    0%, 100% { transform: scale(1); opacity: 1; }
    50% { transform: scale(1.4); opacity: 0.4; }
  }

  .live-metrics-bento {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 10px;
    margin-bottom: 20px;
  }

  .metric-mini-card {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 12px;
    padding: 12px;
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .metric-mini-val {
    font-size: 18px;
    font-weight: 800;
    color: #ffffff;
  }

  .metric-mini-lbl {
    font-size: 10px;
    color: #a1a1aa;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }

  .community-chat-container {
    background: rgba(0, 0, 0, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.06);
    border-radius: 12px;
    padding: 12px;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    min-height: 200px;
    margin-bottom: 80px;
  }

  .chat-feed-title-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
    padding-bottom: 8px;
    margin-bottom: 8px;
  }

  .chat-feed-title {
    font-size: 12px;
    font-weight: 700;
    color: #ffffff;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }

  .chat-bubbles-scroll {
    flex-grow: 1;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 8px;
    max-height: 220px;
  }

  .chat-bubble-card {
    background: rgba(255, 255, 255, 0.04);
    border-radius: 8px;
    padding: 8px 10px;
    border: 1px solid rgba(255, 255, 255, 0.03);
    cursor: pointer;
    transition: background 0.2s ease, border-color 0.2s ease;
  }

  .chat-bubble-card:hover {
    background: rgba(255, 255, 255, 0.08);
    border-color: #1db954;
  }

  .chat-user-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2px;
  }

  .chat-username {
    font-size: 11px;
    font-weight: 700;
    color: #1db954;
  }

  .chat-time {
    font-size: 9px;
    color: #71717a;
  }

  .chat-text {
    font-size: 12px;
    color: #e4e4e7;
    line-height: 1.4;
  }

  .chat-suggestion-indicator {
    font-size: 9px;
    color: #a1a1aa;
    text-align: right;
    margin-top: 4px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 3px;
  }

  /* Go Live Section styles */
  .android-flow-go-live {
    height: 100%;
    overflow-y: auto;
    padding: 16px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    text-align: left;
    background: #000000;
    position: relative;
  }

  .live-video-pane {
    width: 100%;
    aspect-ratio: 4 / 3;
    background: #18181b;
    border-radius: 12px;
    overflow: hidden;
    position: relative;
    border: 1px solid rgba(255, 255, 255, 0.1);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.6);
    margin-bottom: 12px;
  }

  .live-badge-overlay {
    position: absolute;
    top: 10px;
    left: 10px;
    background: #ef4444;
    color: #ffffff;
    font-size: 9px;
    font-weight: 900;
    padding: 3px 8px;
    border-radius: 4px;
    letter-spacing: 1px;
    display: flex;
    align-items: center;
    gap: 4px;
    box-shadow: 0 2px 8px rgba(239, 68, 68, 0.5);
    z-index: 5;
  }

  .live-video-feed-actual {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: filter 0.3s ease;
  }

  .live-video-feed-actual.filter-neon {
    filter: hue-rotate(90deg) saturate(1.8) contrast(1.1);
  }

  .live-video-feed-actual.filter-vintage {
    filter: sepia(0.6) contrast(0.95) brightness(1.05);
  }

  .live-video-feed-actual.filter-monochrome {
    filter: grayscale(1);
  }

  .filters-control-row {
    display: flex;
    gap: 8px;
    margin-bottom: 16px;
    overflow-x: auto;
    padding-bottom: 4px;
  }

  .filter-tab-btn {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 6px;
    padding: 5px 10px;
    font-size: 10px;
    font-weight: 700;
    color: #e4e4e7;
    cursor: pointer;
    white-space: nowrap;
    transition: background 0.2s, border-color 0.2s;
  }

  .filter-tab-btn.active {
    background: #1db954;
    color: #000000;
    border-color: #1db954;
  }

  .live-control-deck-card {
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(255, 255, 255, 0.06);
    border-radius: 12px;
    padding: 12px;
    margin-bottom: 80px;
  }

  .deck-section-title {
    font-size: 11px;
    font-weight: 800;
    color: #a1a1aa;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin-bottom: 8px;
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

  .stream-active-orchestrator {
    height: 40px;
    background: rgba(0, 0, 0, 0.4);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 2px;
    padding: 0 10px;
  }

  .waveform-bar-sim {
    width: 3px;
    background: #1db954;
    border-radius: 1px;
    animation: barWave 1s ease-in-out infinite alternate;
  }

  @keyframes barWave {
    from { height: 4px; }
    to { height: 26px; }
  }

  /* Library & Presets view */
  .android-flow-library {
    height: 100%;
    overflow-y: auto;
    padding: 16px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    text-align: left;
    background: linear-gradient(to bottom, #064e3b 0%, #121212 50%);
  }

  .presets-section-title {
    font-size: 14px;
    font-weight: 800;
    color: #ffffff;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin-top: 16px;
    margin-bottom: 8px;
  }

  .presets-intro-text {
    font-size: 11px;
    color: #a1a1aa;
    line-height: 1.4;
    margin-bottom: 12px;
  }

  .preset-bento-grid {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-bottom: 80px;
  }

  .preset-card-item {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 12px;
    padding: 12px;
    cursor: pointer;
    transition: background 0.2s, border-color 0.2s, transform 0.2s;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .preset-card-item:hover {
    background: rgba(255, 255, 255, 0.09);
    border-color: #1db954;
    transform: scale(1.02);
  }

  .preset-card-item.active {
    border-color: #1db954;
    background: rgba(29, 185, 84, 0.1);
  }

  .preset-left {
    display: flex;
    flex-direction: column;
    gap: 3px;
    text-align: left;
  }

  .preset-name-row {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .preset-title-val {
    font-size: 13px;
    font-weight: 700;
    color: #ffffff;
  }

  .preset-badge-tag {
    font-size: 8px;
    font-weight: 800;
    color: #000000;
    background: #1db954;
    padding: 1px 4px;
    border-radius: 3px;
    text-transform: uppercase;
  }

  .preset-desc {
    font-size: 11px;
    color: #a1a1aa;
  }

  .preset-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .preset-telemetry-status {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 2px;
  }

  .preset-battery-lbl {
    font-size: 10px;
    color: #e4e4e7;
    font-weight: 700;
    display: flex;
    align-items: center;
    gap: 2px;
  }

  .preset-wear-status {
    font-size: 9px;
    color: #a1a1aa;
  }

  .preset-play-icon {
    font-size: 20px !important;
    color: #1db954;
  }

  /* --- Main Community Page Styles --- */
  .community-viewport {
    display: flex;
    flex-direction: column;
    width: 100%;
    height: 100%;
    overflow-y: auto;
    padding: 2.5rem;
    box-sizing: border-box;
    background: radial-gradient(circle at 10% 20%, rgba(99, 102, 241, 0.15) 0%, rgba(0,0,0,0.85) 90%);
  }

  .community-hero {
    margin-bottom: 2.5rem;
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.12) 0%, rgba(236, 72, 153, 0.08) 100%);
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 16px;
    padding: 2.5rem;
    position: relative;
    overflow: hidden;
  }

  .community-hero-title {
    font-size: 32px;
    font-weight: 800;
    letter-spacing: -0.02em;
    background: linear-gradient(to right, #ffffff, #a5b4fc, #f472b6);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    margin-bottom: 0.5rem;
  }

  .community-hero-desc {
    color: #a1a1aa;
    font-size: 14.5px;
    max-width: 600px;
    line-height: 1.5;
  }

  .community-grid-layout {
    display: grid;
    grid-template-columns: 1fr 340px;
    gap: 2rem;
    align-items: start;
  }

  @media (max-width: 1024px) {
    .community-grid-layout {
      grid-template-columns: 1fr;
    }
  }

  .tracks-feed-section {
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
  }

  .section-heading-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
    padding-bottom: 0.75rem;
    margin-bottom: 0.5rem;
  }

  .section-title {
    font-size: 18px;
    font-weight: 700;
    color: #ffffff;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .refresh-btn-secondary {
    background: rgba(255, 255, 255, 0.06);
    border: 1px solid rgba(255, 255, 255, 0.1);
    color: #ffffff;
    border-radius: 8px;
    padding: 0.4rem 0.8rem;
    font-size: 12px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 0.35rem;
    transition: all 0.2s;
  }

  .refresh-btn-secondary:hover {
    background: rgba(255, 255, 255, 0.12);
  }

  .community-tracks-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 1.25rem;
  }

  .community-track-card {
    background: rgba(18, 18, 22, 0.65);
    border: 1px solid rgba(255, 255, 255, 0.06);
    border-radius: 12px;
    padding: 1.25rem;
    display: flex;
    flex-direction: column;
    gap: 1rem;
    transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
    backdrop-filter: blur(10px);
    position: relative;
    overflow: hidden;
  }

  .community-track-card:hover {
    transform: translateY(-4px);
    border-color: rgba(99, 102, 241, 0.35);
    background: rgba(18, 18, 22, 0.85);
    box-shadow: 0 10px 25px rgba(99, 102, 241, 0.1);
  }

  .community-track-card.active-playing {
    border-color: #1db954;
    background: rgba(29, 185, 84, 0.04);
    box-shadow: 0 10px 25px rgba(29, 185, 84, 0.05);
  }

  .track-art-wrapper {
    position: relative;
    width: 100%;
    height: 140px;
    border-radius: 8px;
    overflow: hidden;
    background: #111;
  }

  .track-cover-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    opacity: 0.75;
    transition: transform 0.3s;
  }

  .community-track-card:hover .track-cover-img {
    transform: scale(1.05);
    opacity: 0.9;
  }

  .track-play-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.2s;
  }

  .community-track-card:hover .track-play-overlay, .community-track-card.active-playing .track-play-overlay {
    opacity: 1;
  }

  .play-trigger-pill {
    background: #1db954;
    color: #ffffff;
    width: 44px;
    height: 44px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 15px rgba(29, 185, 84, 0.4);
    cursor: pointer;
    border: none;
    transition: transform 0.2s;
  }

  .play-trigger-pill:hover {
    transform: scale(1.1);
  }

  .track-details-row {
    display: flex;
    flex-direction: column;
    gap: 0.2rem;
  }

  .track-meta-title {
    font-size: 16px;
    font-weight: 700;
    color: #ffffff;
  }

  .track-meta-artist {
    font-size: 13px;
    color: #a1a1aa;
    display: flex;
    align-items: center;
    gap: 0.35rem;
  }

  .track-vibe-prompt {
    font-size: 12.5px;
    color: #e4e4e7;
    background: rgba(255, 255, 255, 0.04);
    padding: 0.65rem;
    border-radius: 8px;
    line-height: 1.4;
    border: 1px solid rgba(255, 255, 255, 0.04);
    font-family: monospace;
  }

  .track-footer-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 11px;
    color: #71717a;
    border-top: 1px solid rgba(255, 255, 255, 0.04);
    padding-top: 0.75rem;
    margin-top: auto;
  }

  .creator-panel-section {
    background: rgba(24, 24, 27, 0.75);
    border: 1px solid rgba(255, 255, 255, 0.06);
    border-radius: 16px;
    padding: 1.5rem;
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
    backdrop-filter: blur(10px);
  }

  .creator-form-title {
    font-size: 16px;
    font-weight: 700;
    color: #ffffff;
    border-bottom: 1px solid rgba(255, 255, 255, 0.06);
    padding-bottom: 0.5rem;
  }

  .form-group-custom {
    display: flex;
    flex-direction: column;
    gap: 0.45rem;
  }

  .form-label-custom {
    font-size: 12px;
    font-weight: 600;
    color: #a1a1aa;
  }

  .form-input-custom {
    background: rgba(255, 255, 255, 0.04);
    border: 1px solid rgba(255, 255, 255, 0.1);
    color: #ffffff;
    border-radius: 8px;
    padding: 0.65rem;
    font-size: 13.5px;
    width: 100%;
    box-sizing: border-box;
    transition: all 0.2s;
  }

  .form-input-custom:focus {
    outline: none;
    border-color: #818cf8;
    background: rgba(255, 255, 255, 0.08);
  }

  .use-current-badge {
    align-self: flex-start;
    font-size: 11px;
    font-weight: 600;
    color: #818cf8;
    background: rgba(129, 140, 248, 0.1);
    border: 1px solid rgba(129, 140, 248, 0.2);
    border-radius: 6px;
    padding: 0.2rem 0.5rem;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 0.25rem;
    transition: all 0.2s;
  }

  .use-current-badge:hover {
    background: rgba(129, 140, 248, 0.2);
    color: #ffffff;
  }

  .publish-vibe-btn {
    background: linear-gradient(135deg, #6366f1 0%, #ec4899 100%);
    color: #ffffff;
    border: none;
    border-radius: 8px;
    padding: 0.75rem;
    font-size: 13.5px;
    font-weight: 700;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    box-shadow: 0 4px 15px rgba(99, 102, 241, 0.25);
    transition: all 0.2s;
    width: 100%;
  }

  .publish-vibe-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 20px rgba(99, 102, 241, 0.35);
  }

  .publish-vibe-btn:disabled {
    background: #27272a;
    color: #71717a;
    box-shadow: none;
    cursor: not-allowed;
  }

  /* --- Bottom Nav Bar Web Styles --- */
  #bottom-nav-bar-web {
    display: none;
  }

  @media (max-width: 768px) {
    #top-nav-bar .nav-tabs {
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
  }
`;


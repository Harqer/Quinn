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
    background: #000;
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
    background: #000000;
    color: #ffffff;
    font-family: "Google Sans", sans-serif;
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
`;

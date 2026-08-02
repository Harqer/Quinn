import { css } from 'lit';

export const renderSecurityStyles = css`
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
.bottom-nav-tab-web .material-icons-round {
      font-size: 20px;
    }
`;

/**
 * @AtomicLevel: Molecule
 * @SemanticPurpose: UI Component for android-search-results.ts
 */

import { LitElement, html, nothing } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { AndroidSearchResultsStyles } from './android-search-results.styles';

@customElement('android-search-results')
export class AndroidSearchResults extends LitElement {
  static styles = AndroidSearchResultsStyles;

  @property() androidSearchQuery: string = "";
  @property() androidActiveSongArtist: string = "";
  @property() searchResults: any[] = [];
  @property() searchError: string = "";

  render() {
    const query = (this.androidSearchQuery || "").toLowerCase();
    const filtered = (this.searchResults || []).filter(a => 
      (a.name || "").toLowerCase().includes(query) ||
      (a.vibe || "").toLowerCase().includes(query)
    );

    return html`
      <div class="android-flow-search-results">
        <div class="results-header-row">
          <div class="results-search-wrapper">
            <span class="material-icons-round search-active-icon">search</span>
            <input 
              type="text" 
              class="results-search-field" 
              placeholder="Search" 
              .value=${this.androidSearchQuery || ""}
              @input=${(e: any) => {
                this.androidSearchQuery = e.target.value;
                this.dispatchEvent(new CustomEvent("search", { detail: e.target.value, bubbles: true, composed: true }));
              }}
              autofocus
            />
          </div>
          <button class="results-cancel-btn" @click=${() => { this.dispatchEvent(new CustomEvent("search", { detail: "", bubbles: true, composed: true })); this.dispatchEvent(new CustomEvent("step-change", { detail: "search_home", bubbles: true, composed: true })); }}>
            Cancel
          </button>
        </div>
        
        <div class="results-list-container">
          ${this.searchError ? html`
            <div class="results-error" style="color: #ef4444; padding: 16px;">
              <span class="material-icons-round">error</span>
              <p>Failed to load results: ${this.searchError}</p>
              <button @click=${() => this.dispatchEvent(new CustomEvent("retry-search", { bubbles: true, composed: true }))} style="margin-top: 8px; background: #3f3f46; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer;">Retry</button>
            </div>
          ` : filtered.length === 0 ? html`
            <div class="results-empty">No results found for "${this.androidSearchQuery}"</div>
          ` : filtered.map(artist => html`
            <div class="result-item" @click=${() => {
              if (this.androidActiveSongArtist && artist.name === this.androidActiveSongArtist) {
                this.dispatchEvent(new CustomEvent("step-change", { detail: "album_details", bubbles: true, composed: true }));
              } else {
                this.dispatchEvent(new CustomEvent("play-song", { detail: [artist.name, artist.name, artist.img || "", artist.vibe || ""], bubbles: true, composed: true }));
              }
            }}>
              ${artist.img ? html`<img src="${artist.img}" class="result-avatar" />` : html`<div class="result-avatar material-icons-round" style="display:flex; align-items:center; justify-content:center; background:#3f3f46; color:white;">person</div>`}
              <div class="result-meta">
                <span class="result-name">${artist.name}</span>
                <span class="result-subtitle">${artist.type} • Vibe: ${artist.vibe}</span>
              </div>
            </div>
          `)}
        </div>
      </div>
    `;
  
  }
}

import { Track } from './MusicService';

export type PlayerState = 'idle' | 'playing' | 'paused' | 'loading' | 'error';

type StateListener = (state: PlayerState) => void;
type ProgressListener = (currentTime: number, duration: number) => void;
type TrackListener = (track: Track | null) => void;

class AudioService {
  private audio: HTMLAudioElement;
  private _state: PlayerState = 'idle';
  private _currentTrack: Track | null = null;

  private stateListeners: Set<StateListener> = new Set();
  private progressListeners: Set<ProgressListener> = new Set();
  private trackListeners: Set<TrackListener> = new Set();

  constructor() {
    this.audio = new Audio();
    
    this.audio.addEventListener('play', () => this.setState('playing'));
    this.audio.addEventListener('pause', () => this.setState('paused'));
    this.audio.addEventListener('waiting', () => this.setState('loading'));
    this.audio.addEventListener('playing', () => this.setState('playing'));
    this.audio.addEventListener('ended', () => {
      this.setState('idle');
      // Could notify next track here or leave to PlayerContext
    });
    this.audio.addEventListener('error', () => this.setState('error'));
    
    this.audio.addEventListener('timeupdate', () => {
      this.notifyProgress(this.audio.currentTime, this.audio.duration || 0);
    });
    this.audio.addEventListener('loadedmetadata', () => {
      this.notifyProgress(this.audio.currentTime, this.audio.duration || 0);
    });
  }

  private setState(state: PlayerState) {
    this._state = state;
    this.stateListeners.forEach(l => l(state));
  }

  private notifyProgress(currentTime: number, duration: number) {
    this.progressListeners.forEach(l => l(currentTime, duration));
  }

  public playTrack(track: Track) {
    this._currentTrack = track;
    this.trackListeners.forEach(l => l(track));
    
    if (track.audioUrl) {
      this.audio.src = track.audioUrl;
      this.audio.play().catch(e => {
        console.error("Playback failed", e);
        this.setState('error');
      });
    } else {
      this.audio.src = '';
      this.setState('error');
    }
  }

  public togglePlayPause() {
    if (!this._currentTrack) return;
    
    if (this._state === 'playing') {
      this.audio.pause();
    } else {
      this.audio.play().catch(console.error);
    }
  }
  
  public pause() {
    if (this._state === 'playing') {
      this.audio.pause();
    }
  }
  
  public play() {
    if (this._currentTrack && this._state !== 'playing') {
      this.audio.play().catch(console.error);
    }
  }

  public seek(time: number) {
    this.audio.currentTime = time;
  }

  public setVolume(volume: number) {
    this.audio.volume = Math.max(0, Math.min(1, volume));
  }

  public onStateChange(listener: StateListener) {
    this.stateListeners.add(listener);
    return () => this.stateListeners.delete(listener);
  }

  public onProgress(listener: ProgressListener) {
    this.progressListeners.add(listener);
    return () => this.progressListeners.delete(listener);
  }

  public onTrackChange(listener: TrackListener) {
    this.trackListeners.add(listener);
    return () => this.trackListeners.delete(listener);
  }

  public get state() { return this._state; }
  public get currentTrack() { return this._currentTrack; }
  public get currentTime() { return this.audio.currentTime; }
  public get duration() { return this.audio.duration || 0; }
}

export const audioService = new AudioService();

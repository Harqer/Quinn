import { Track } from './MusicService';

export type PlayerState = 'idle' | 'playing' | 'paused' | 'loading' | 'error';
export type RepeatMode = 'none' | 'all' | 'one';

type StateListener = (state: PlayerState) => void;
type ProgressListener = (currentTime: number, duration: number) => void;
type TrackListener = (track: Track | null) => void;
type QueueListener = (queue: Track[], index: number) => void;
type ModeListener = (shuffle: boolean, repeat: RepeatMode) => void;

class AudioService {
  private audio: HTMLAudioElement;
  private _state: PlayerState = 'idle';
  private _currentTrack: Track | null = null;
  
  private _queue: Track[] = [];
  private _queueIndex: number = -1;
  private _originalQueue: Track[] = []; // For un-shuffling
  
  private _shuffle: boolean = false;
  private _repeat: RepeatMode = 'none';

  private stateListeners: Set<StateListener> = new Set();
  private progressListeners: Set<ProgressListener> = new Set();
  private trackListeners: Set<TrackListener> = new Set();
  private queueListeners: Set<QueueListener> = new Set();
  private modeListeners: Set<ModeListener> = new Set();

  constructor() {
    this.audio = new Audio();
    
    this.audio.addEventListener('play', () => this.setState('playing'));
    this.audio.addEventListener('pause', () => this.setState('paused'));
    this.audio.addEventListener('waiting', () => this.setState('loading'));
    this.audio.addEventListener('playing', () => this.setState('playing'));
    this.audio.addEventListener('ended', () => {
      this.setState('idle');
      this.handleTrackEnded();
    });
    this.audio.addEventListener('error', () => this.setState('error'));
    
    this.audio.addEventListener('timeupdate', () => {
      this.notifyProgress(this.audio.currentTime, this.audio.duration || 0);
    });
    this.audio.addEventListener('loadedmetadata', () => {
      this.notifyProgress(this.audio.currentTime, this.audio.duration || 0);
    });
  }

  private handleTrackEnded() {
    if (this._repeat === 'one') {
      this.audio.currentTime = 0;
      this.audio.play().catch(console.error);
    } else {
      this.skipNext(true); // pass true for auto-advance
    }
  }

  private setState(state: PlayerState) {
    this._state = state;
    this.stateListeners.forEach(l => l(state));
  }

  private notifyProgress(currentTime: number, duration: number) {
    this.progressListeners.forEach(l => l(currentTime, duration));
  }
  
  private notifyQueue() {
    this.queueListeners.forEach(l => l(this._queue, this._queueIndex));
  }
  
  private notifyModes() {
    this.modeListeners.forEach(l => l(this._shuffle, this._repeat));
  }

  public playQueue(tracks: Track[], startIndex: number = 0) {
    if (!tracks || tracks.length === 0) return;
    
    this._originalQueue = [...tracks];
    this._queue = [...tracks];
    this._queueIndex = startIndex;
    
    if (this._shuffle) {
      // Shuffle remaining tracks after the current one
      const currentTrack = this._queue[this._queueIndex];
      const remaining = this._queue.filter((_, i) => i !== this._queueIndex);
      for (let i = remaining.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [remaining[i], remaining[j]] = [remaining[j], remaining[i]];
      }
      this._queue = [currentTrack, ...remaining];
      this._queueIndex = 0;
    }
    
    this.notifyQueue();
    this.playTrack(this._queue[this._queueIndex]);
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
  
  public skipNext(autoAdvance = false) {
    if (this._queue.length === 0) return;
    
    if (this._queueIndex < this._queue.length - 1) {
      this._queueIndex++;
      this.notifyQueue();
      this.playTrack(this._queue[this._queueIndex]);
    } else if (this._repeat === 'all' || (!autoAdvance && this._queue.length > 0)) {
      // Loop back to start if repeat all, or if user explicitly clicked skip next on last track
      this._queueIndex = 0;
      this.notifyQueue();
      this.playTrack(this._queue[this._queueIndex]);
    } else {
      this.setState('idle'); // Reached end of queue without repeat all
    }
  }

  public skipPrevious() {
    if (this._queue.length === 0) return;
    
    // If we're more than 3 seconds in, just restart current track
    if (this.audio.currentTime > 3) {
      this.audio.currentTime = 0;
      return;
    }
    
    if (this._queueIndex > 0) {
      this._queueIndex--;
      this.notifyQueue();
      this.playTrack(this._queue[this._queueIndex]);
    } else if (this._repeat === 'all') {
      this._queueIndex = this._queue.length - 1;
      this.notifyQueue();
      this.playTrack(this._queue[this._queueIndex]);
    } else {
      this.audio.currentTime = 0; // At start of queue, just reset time
    }
  }
  
  public toggleShuffle() {
    this._shuffle = !this._shuffle;
    
    if (this._queue.length > 0 && this._currentTrack) {
      if (this._shuffle) {
        const remaining = this._originalQueue.filter(t => t.id !== this._currentTrack!.id);
        for (let i = remaining.length - 1; i > 0; i--) {
          const j = Math.floor(Math.random() * (i + 1));
          [remaining[i], remaining[j]] = [remaining[j], remaining[i]];
        }
        this._queue = [this._currentTrack, ...remaining];
        this._queueIndex = 0;
      } else {
        this._queue = [...this._originalQueue];
        this._queueIndex = this._queue.findIndex(t => t.id === this._currentTrack!.id);
      }
      this.notifyQueue();
    }
    this.notifyModes();
  }
  
  public toggleRepeat() {
    if (this._repeat === 'none') this._repeat = 'all';
    else if (this._repeat === 'all') this._repeat = 'one';
    else this._repeat = 'none';
    this.notifyModes();
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
  
  public onQueueChange(listener: QueueListener) {
    this.queueListeners.add(listener);
    return () => this.queueListeners.delete(listener);
  }
  
  public onModeChange(listener: ModeListener) {
    this.modeListeners.add(listener);
    return () => this.modeListeners.delete(listener);
  }

  public get state() { return this._state; }
  public get currentTrack() { return this._currentTrack; }
  public get currentTime() { return this.audio.currentTime; }
  public get duration() { return this.audio.duration || 0; }
  public get queue() { return this._queue; }
  public get queueIndex() { return this._queueIndex; }
  public get shuffle() { return this._shuffle; }
  public get repeat() { return this._repeat; }
}

export const audioService = new AudioService();

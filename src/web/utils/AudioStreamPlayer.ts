export class AudioStreamPlayer {
  private audioContext: AudioContext;
  private nextPlayTime: number = 0;
  private isPlaying: boolean = false;
  private sampleRate: number = 24000;

  constructor() {
    // Create AudioContext but don't start it until user interaction
    this.audioContext = new (window.AudioContext || (window as any).webkitAudioContext)({
      sampleRate: this.sampleRate,
    });
  }

  public async init() {
    if (this.audioContext.state === 'suspended') {
      await this.audioContext.resume();
    }
  }

  public stop() {
    if (this.audioContext.state !== 'closed') {
      this.audioContext.close();
    }
    this.isPlaying = false;
    this.nextPlayTime = 0;
  }

  private base64ToInt16Array(base64: string): Int16Array {
    const binaryString = window.atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return new Int16Array(bytes.buffer);
  }

  public queueAudioChunk(base64Data: string) {
    if (!this.isPlaying) {
      this.isPlaying = true;
      this.nextPlayTime = this.audioContext.currentTime + 0.1; // 100ms buffer start
    }

    const int16Data = this.base64ToInt16Array(base64Data);
    const float32Data = new Float32Array(int16Data.length);
    
    // Convert 16-bit PCM to Float32 (-1.0 to 1.0)
    for (let i = 0; i < int16Data.length; i++) {
      float32Data[i] = int16Data[i] / 32768.0;
    }

    const audioBuffer = this.audioContext.createBuffer(1, float32Data.length, this.sampleRate);
    audioBuffer.getChannelData(0).set(float32Data);

    const source = this.audioContext.createBufferSource();
    source.buffer = audioBuffer;
    source.connect(this.audioContext.destination);
    
    // Ensure we don't schedule in the past if buffer underrun happens
    const startTime = Math.max(this.nextPlayTime, this.audioContext.currentTime);
    source.start(startTime);

    this.nextPlayTime = startTime + audioBuffer.duration;
  }
}

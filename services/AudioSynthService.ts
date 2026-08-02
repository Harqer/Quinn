export class AudioSynthService {
  private masterGain: GainNode | null = null;
  private compressor: DynamicsCompressorNode | null = null;

  private getAudioDestination(ctx: AudioContext, component: any): AudioNode {
    if (component.liveMusicHelper?.outputNode) {
      return component.liveMusicHelper.outputNode;
    }
    if (!this.masterGain) {
      this.masterGain = ctx.createGain();
      this.masterGain.gain.setValueAtTime(0.7, ctx.currentTime);
      
      this.compressor = ctx.createDynamicsCompressor();
      this.compressor.threshold.setValueAtTime(-12, ctx.currentTime);
      this.compressor.knee.setValueAtTime(30, ctx.currentTime);
      this.compressor.ratio.setValueAtTime(12, ctx.currentTime);
      this.compressor.attack.setValueAtTime(0.003, ctx.currentTime);
      this.compressor.release.setValueAtTime(0.25, ctx.currentTime);

      this.masterGain.connect(this.compressor);
      this.compressor.connect(ctx.destination);
    }
    return this.masterGain;
  }

  public async playInstrumentSynth(component: any) {
    const ctx = component.liveMusicHelper?.audioContext;
    if (!ctx) return;

    if (ctx.state === "suspended") {
      await ctx.resume();
    }

    component.stopAllSynthesizers();

    const midiNote = 48 + component.gestureX * 36;
    const baseFreq = 440 * Math.pow(2, (midiNote - 69) / 12);
    component.currentPlayingPitch = component.getNoteName(midiNote);

    const now = ctx.currentTime;
    const destination = this.getAudioDestination(ctx, component);

    const noteFrequencies: number[] = [baseFreq];
    if (component.harmonicVoicing >= 1.5) {
      const intervalMultiplier = component.harmonicVoicing >= 3.0 ? 1.5 : 1.25;
      noteFrequencies.push(baseFreq * intervalMultiplier);
    }
    if (component.harmonicVoicing >= 2.8) noteFrequencies.push(baseFreq * 2.0);
    if (component.harmonicVoicing >= 4.0) noteFrequencies.push(baseFreq * 3.0);

    const maxVolume = 0.15 / Math.sqrt(noteFrequencies.length);

    noteFrequencies.forEach((freq) => {
      const voiceGain = ctx.createGain();
      voiceGain.gain.setValueAtTime(0.0001, now);

      if (component.currentInstrument === "piano") {
        const osc1 = ctx.createOscillator();
        const osc2 = ctx.createOscillator();
        const osc3 = ctx.createOscillator();
        osc1.type = "sine"; osc1.frequency.setValueAtTime(freq, now);
        osc2.type = "sine"; osc2.frequency.setValueAtTime(freq * 2, now);
        osc3.type = "sine"; osc3.frequency.setValueAtTime(freq * 3, now);

        const g1 = ctx.createGain(); g1.gain.setValueAtTime(0.6, now);
        const g2 = ctx.createGain(); g2.gain.setValueAtTime(0.25, now);
        const g3 = ctx.createGain(); g3.gain.setValueAtTime(0.1, now);

        osc1.connect(g1).connect(voiceGain);
        osc2.connect(g2).connect(voiceGain);
        osc3.connect(g3).connect(voiceGain);

        voiceGain.gain.linearRampToValueAtTime(maxVolume, now + 0.02);
        voiceGain.gain.setTargetAtTime(maxVolume * 0.3, now + 0.02, 0.2);

        osc1.start(now); osc2.start(now); osc3.start(now);

        component.activeSynthOscillators.push(
          { osc: osc1, gain: voiceGain, extraGain: g1, inst: "piano" },
          { osc: osc2, gain: voiceGain, extraGain: g2, inst: "piano" },
          { osc: osc3, gain: voiceGain, extraGain: g3, inst: "piano" }
        );
      } else if (component.currentInstrument === "clarinet") {
        const osc = ctx.createOscillator();
        osc.type = "triangle";
        osc.frequency.setValueAtTime(freq, now);

        const filter = ctx.createBiquadFilter();
        filter.type = "lowpass";
        filter.frequency.setValueAtTime(600 + (1.0 - component.gestureY) * 1500, now);

        const lfo = ctx.createOscillator();
        lfo.type = "sine"; lfo.frequency.setValueAtTime(5.5, now);
        const lfoGain = ctx.createGain(); lfoGain.gain.setValueAtTime(3.5, now);

        lfo.connect(lfoGain).connect(osc.frequency);
        osc.connect(filter).connect(voiceGain);

        voiceGain.gain.linearRampToValueAtTime(maxVolume, now + 0.08);

        lfo.start(now); osc.start(now);

        component.activeSynthOscillators.push({ osc, gain: voiceGain, filter, lfo, lfoGain, inst: "clarinet" });
      } else if (component.currentInstrument === "violin") {
        const osc1 = ctx.createOscillator();
        const osc2 = ctx.createOscillator();
        osc1.type = "sawtooth"; osc1.frequency.setValueAtTime(freq, now);
        osc2.type = "sawtooth"; osc2.frequency.setValueAtTime(freq * 1.006, now);

        const filter = ctx.createBiquadFilter();
        filter.type = "lowpass";
        filter.frequency.setValueAtTime(300 + (1.0 - component.gestureY) * 2800, now);

        const lfo = ctx.createOscillator();
        lfo.type = "sine"; lfo.frequency.setValueAtTime(6.0, now);
        const lfoGain = ctx.createGain(); lfoGain.gain.setValueAtTime(4.0, now);

        lfo.connect(lfoGain);
        lfoGain.connect(osc1.frequency);
        lfoGain.connect(osc2.frequency);

        osc1.connect(filter); osc2.connect(filter); filter.connect(voiceGain);

        voiceGain.gain.linearRampToValueAtTime(maxVolume, now + 0.15);

        lfo.start(now); osc1.start(now); osc2.start(now);

        component.activeSynthOscillators.push(
          { osc: osc1, gain: voiceGain, filter, lfo, lfoGain, inst: "violin" },
          { osc: osc2, gain: voiceGain, filter, lfo, lfoGain, inst: "violin" }
        );
      } else if (component.currentInstrument === "chimes") {
        const chimeFreq = freq * 2.0;
        const osc = ctx.createOscillator(); osc.type = "sine"; osc.frequency.setValueAtTime(chimeFreq, now);
        const oscHarmonic = ctx.createOscillator(); oscHarmonic.type = "sine"; oscHarmonic.frequency.setValueAtTime(chimeFreq * 2.51, now);

        const harmGain = ctx.createGain(); harmGain.gain.setValueAtTime(0.3, now);

        oscHarmonic.connect(harmGain).connect(voiceGain);
        osc.connect(voiceGain);

        voiceGain.gain.linearRampToValueAtTime(maxVolume * 1.1, now + 0.01);
        voiceGain.gain.setTargetAtTime(0.0001, now + 0.01, 0.4);

        osc.start(now); oscHarmonic.start(now);

        component.activeSynthOscillators.push(
          { osc, gain: voiceGain, inst: "chimes" },
          { osc: oscHarmonic, gain: harmGain, inst: "chimes" }
        );
      }

      voiceGain.connect(destination);
    });
  }

  public updateInstrumentSynth(component: any) {
    const ctx = component.liveMusicHelper?.audioContext;
    if (!ctx || !component.activeSynthOscillators || component.activeSynthOscillators.length === 0) return;

    const midiNote = 48 + component.gestureX * 36;
    const baseFreq = 440 * Math.pow(2, (midiNote - 69) / 12);
    component.currentPlayingPitch = component.getNoteName(midiNote);

    if (component.currentPlayingPitch !== component.lastPlayingPitch) {
      component.lastPlayingPitch = component.currentPlayingPitch;
      if (component.isHapticsEnabled && navigator.vibrate) {
        navigator.vibrate(6);
      }
    }

    const now = ctx.currentTime;
    component.activeSynthOscillators.forEach((item: any) => {
      try {
        if (!item.osc) return;
        if (item.inst === component.currentInstrument) {
          item.osc.frequency.setTargetAtTime(baseFreq, now, 0.05);
          if (item.filter) {
            const filterFreq = 300 + (1.0 - component.gestureY) * 2500;
            item.filter.frequency.setTargetAtTime(filterFreq, now, 0.05);
          }
        }
      } catch (e) {}
    });
  }

  public triggerBeatHaptic(component: any, avgVolume: number) {
    if (!component.isHapticsEnabled || !navigator.vibrate) return;
    const now = Date.now();
    if (now - component.lastVibrationTime < 150) return;

    component.volumeHistory.push(avgVolume);
    if (component.volumeHistory.length > 20) component.volumeHistory.shift();

    const avgHist = component.volumeHistory.reduce((a: number, b: number) => a + b, 0) / (component.volumeHistory.length || 1);
    const isSpike = avgVolume > avgHist * 1.3 && avgVolume > 12;
    const isStrongBeat = avgVolume > 60;

    if (isSpike || isStrongBeat) {
      component.lastVibrationTime = now;
      let pattern: number | number[] = 15;
      if (component.currentInstrument === "violin") pattern = Math.min(35, Math.floor(avgVolume * 0.25 + 10));
      else if (component.currentInstrument === "piano") pattern = Math.min(20, Math.max(8, Math.floor(avgVolume * 0.15)));
      else if (component.currentInstrument === "clarinet") pattern = Math.min(25, Math.max(10, Math.floor(avgVolume * 0.18)));
      else pattern = [10, 30, 10];
      navigator.vibrate(pattern);
    }
  }
}

export class CanvasRendererService {
  public drawSimulationFrame(component: any) {
    const canvas = component.shadowRoot?.getElementById("simulation-canvas") as HTMLCanvasElement;
    if (!canvas) return;

    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    const rect = canvas.getBoundingClientRect();
    if (canvas.width !== rect.width || canvas.height !== rect.height) {
      canvas.width = rect.width || 640;
      canvas.height = rect.height || 480;
    }

    const w = canvas.width;
    const h = canvas.height;
    component.simTime += 0.005;

    const phaseDuration = 20;
    const currentTimeSec = component.simTime * 20;
    const phaseIndex = Math.floor((currentTimeSec / phaseDuration) % 4);

    ctx.fillStyle = "#000000";
    ctx.fillRect(0, 0, w, h);

    let grad = ctx.createRadialGradient(w/2, h/2, 10, w/2, h/2, Math.max(w, h)/1.2);
    if (phaseIndex === 0) {
      grad.addColorStop(0, "rgba(59, 130, 246, 0.25)");
      grad.addColorStop(0.5, "rgba(139, 92, 246, 0.15)");
      grad.addColorStop(1, "rgba(0, 0, 0, 1)");
    } else if (phaseIndex === 1) {
      grad.addColorStop(0, "rgba(16, 185, 129, 0.25)");
      grad.addColorStop(0.6, "rgba(6, 182, 212, 0.15)");
      grad.addColorStop(1, "rgba(0, 0, 0, 1)");
    } else if (phaseIndex === 2) {
      grad.addColorStop(0, "rgba(34, 197, 94, 0.25)");
      grad.addColorStop(0.4, "rgba(99, 102, 241, 0.15)");
      grad.addColorStop(1, "rgba(0, 0, 0, 1)");
    } else {
      grad.addColorStop(0, "rgba(245, 158, 11, 0.25)");
      grad.addColorStop(0.5, "rgba(239, 68, 68, 0.15)");
      grad.addColorStop(1, "rgba(0, 0, 0, 1)");
    }
    ctx.fillStyle = grad;
    ctx.fillRect(0, 0, w, h);

    ctx.lineWidth = 2;
    const count = 4;
    for (let i = 0; i < count; i++) {
      ctx.beginPath();
      let color = "rgba(255, 255, 255, 0.08)";
      if (phaseIndex === 0) color = `rgba(147, 197, 253, ${0.1 - i*0.02})`;
      else if (phaseIndex === 1) color = `rgba(165, 243, 252, ${0.1 - i*0.02})`;
      else if (phaseIndex === 2) color = `rgba(187, 247, 208, ${0.1 - i*0.02})`;
      else color = `rgba(253, 186, 116, ${0.1 - i*0.02})`;

      ctx.strokeStyle = color;
      const waveFreq = 1 + i * 0.5;
      const waveAmp = 40 + i * 15;
      for (let x = 0; x <= w; x += 10) {
        const angle = (x / w) * Math.PI * 2 * waveFreq + component.simTime * 2;
        const y = h / 2 + Math.sin(angle) * waveAmp + Math.cos(component.simTime + i) * 30;
        if (x === 0) ctx.moveTo(x, y);
        else ctx.lineTo(x, y);
      }
      ctx.stroke();
    }

    const particleCount = 40;
    for (let i = 0; i < particleCount; i++) {
      const angle = (i / particleCount) * Math.PI * 2 + component.simTime * (0.3 + (i % 3) * 0.1);
      const radius = Math.min(w, h) * 0.15 + (i % 4) * 35 + Math.sin(component.simTime + i) * 15;
      const x = w / 2 + Math.cos(angle) * radius;
      const y = h / 2 + Math.sin(angle) * radius * 0.6;
      const size = 1.5 + (i % 3);

      ctx.beginPath();
      let color = "rgba(255, 255, 255, 0.6)";
      if (phaseIndex === 0 && i % 2 === 0) color = "rgba(96, 165, 250, 0.8)";
      else if (phaseIndex === 1 && i % 2 === 0) color = "rgba(34, 211, 238, 0.8)";
      else if (phaseIndex === 2 && i % 2 === 0) color = "rgba(74, 222, 128, 0.8)";
      else if (phaseIndex === 3 && i % 2 === 0) color = "rgba(251, 146, 60, 0.8)";

      ctx.fillStyle = color;
      ctx.arc(x, y, size, 0, Math.PI * 2);
      ctx.fill();
    }

    ctx.beginPath();
    let centerGrad = ctx.createRadialGradient(w/2, h/2, 2, w/2, h/2, 40 + Math.sin(component.simTime * 5) * 5);
    if (phaseIndex === 0) {
      centerGrad.addColorStop(0, "rgba(255, 255, 255, 0.8)");
      centerGrad.addColorStop(1, "rgba(139, 92, 246, 0)");
    } else if (phaseIndex === 1) {
      centerGrad.addColorStop(0, "rgba(255, 255, 255, 0.8)");
      centerGrad.addColorStop(1, "rgba(6, 182, 212, 0)");
    } else if (phaseIndex === 2) {
      centerGrad.addColorStop(0, "rgba(255, 255, 255, 0.8)");
      centerGrad.addColorStop(1, "rgba(16, 185, 129, 0)");
    } else {
      centerGrad.addColorStop(0, "rgba(255, 255, 255, 0.8)");
      centerGrad.addColorStop(1, "rgba(245, 158, 11, 0)");
    }
    ctx.fillStyle = centerGrad;
    ctx.arc(w/2, h/2, 50, 0, Math.PI * 2);
    ctx.fill();
  }
  public drawFluidWave(component: any, 
    ctx: CanvasRenderingContext2D,
    canvas: HTMLCanvasElement,
    isPlaying: boolean,
    color: string,
    ampScale: number,
    speedScale: number,
    phaseShift: number
  ) {
    ctx.save();
    ctx.beginPath();
    ctx.moveTo(0, canvas.height / 2);

    const points = 7;
    const step = canvas.width / (points - 1);
    const time = Date.now() * 0.001 * speedScale;

    for (let i = 0; i < points; i++) {
      const x = i * step;

      // Map frequency bin indexes to spatial points
      let freqFactor = 15;
      if (component.analyser && isPlaying) {
        const binIdx = Math.min(
          component.analyserData.length - 1,
          Math.floor((i / points) * component.analyserData.length * 0.6)
        );
        freqFactor = component.analyserData[binIdx];
      }

      // Compute dynamic amplitude and sinusoidal y coordinates
      const baseAmp = isPlaying
        ? (freqFactor / 255) * canvas.height * 0.35
        : canvas.height * 0.06; // Ambient slow float when idle
      const amp = baseAmp * ampScale;

      const y =
        canvas.height / 2 +
        Math.sin(time + i * 1.3 + phaseShift) * amp +
        Math.cos(time * 0.5 + i * 0.7) * (amp * 0.3);

      if (i === 0) {
        ctx.moveTo(x, y);
      } else {
        const prevX = (i - 1) * step;
        
        let prevFreqFactor = 15;
        if (component.analyser && isPlaying) {
          const prevBinIdx = Math.min(
            component.analyserData.length - 1,
            Math.floor(((i - 1) / points) * component.analyserData.length * 0.6)
          );
          prevFreqFactor = component.analyserData[prevBinIdx];
        }
        const prevBaseAmp = isPlaying
          ? (prevFreqFactor / 255) * canvas.height * 0.35
          : canvas.height * 0.06;
        const prevAmp = prevBaseAmp * ampScale;
        const prevY =
          canvas.height / 2 +
          Math.sin(time + (i - 1) * 1.3 + phaseShift) * prevAmp +
          Math.cos(time * 0.5 + (i - 1) * 0.7) * (prevAmp * 0.3);

        const cpX1 = prevX + step / 2;
        const cpY1 = prevY;
        const cpX2 = prevX + step / 2;
        const cpY2 = y;

        ctx.bezierCurveTo(cpX1, cpY1, cpX2, cpY2, x, y);
      }
    }

    ctx.strokeStyle = color;
    ctx.lineWidth = isPlaying ? 3.5 : 1.5;
    ctx.shadowBlur = isPlaying ? 25 : 6;
    ctx.shadowColor = color;
    ctx.stroke();
    ctx.restore();
  }
  public renderGesturePadCanvas(component: any) {
    const canvas = component.gesturePadCanvas;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    const rect = canvas.getBoundingClientRect();
    if (canvas.width !== rect.width || canvas.height !== rect.height) {
      canvas.width = rect.width;
      canvas.height = rect.height;
    }

    ctx.clearRect(0, 0, canvas.width, canvas.height);

    if (component.isGestureActive) {
      const cx = component.gestureX * canvas.width;
      const cy = component.gestureY * canvas.height;
      const time = Date.now() * 0.003;

      // Draw concentric expanding harmonic chords rings
      const numRings = Math.floor(component.harmonicVoicing);
      ctx.save();
      ctx.shadowBlur = 18;
      
      for (let i = 1; i <= numRings + 1; i++) {
        const radius = (15 + i * 18 + Math.sin(time + i) * 6);
        ctx.beginPath();
        ctx.arc(cx, cy, radius, 0, Math.PI * 2);
        
        const hue = (220 + i * 25 + component.gestureX * 60) % 360;
        ctx.strokeStyle = `hsla(${hue}, 85%, 65%, ${0.65 / i})`;
        ctx.lineWidth = 1.8;
        ctx.shadowColor = `hsla(${hue}, 85%, 65%, 0.8)`;
        ctx.stroke();
      }

      // Draw horizontal musical line (instrument bow string) vibrating centered aroundcy
      ctx.beginPath();
      ctx.shadowBlur = 10;
      ctx.shadowColor = "rgba(244, 114, 182, 0.75)";
      ctx.strokeStyle = "rgba(244, 114, 182, 0.45)";
      ctx.lineWidth = 1.5;

      for (let x = 0; x < canvas.width; x++) {
        const distFromTouch = Math.abs(x - cx);
        const amp = Math.max(0, 24 - distFromTouch * 0.12); // Pinching amplitude near cursor
        const offset = Math.sin(x * 0.08 - time * 5) * amp * (1.0 - component.gestureY * 0.5);
        
        if (x === 0) {
          ctx.moveTo(x, cy + offset);
        } else {
          ctx.lineTo(x, cy + offset);
        }
      }
      ctx.stroke();
      ctx.restore();
    }
  }
  public renderVisualizerCanvas(component: any) {
    const canvas = component.visualizerCanvas;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    const rect = canvas.getBoundingClientRect();
    if (canvas.width !== rect.width || canvas.height !== rect.height) {
      canvas.width = rect.width;
      canvas.height = rect.height;
    }

    // Clear canvas with a transparent trail for beautiful motion blur motion waves
    ctx.fillStyle = "rgba(10, 10, 15, 0.16)";
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    let sum = 0;
    if (component.analyser && component.playbackState === "playing") {
      component.analyser.getByteFrequencyData(component.analyserData);
      for (let i = 0; i < component.analyserData.length; i++) {
        sum += component.analyserData[i];
      }
    }

    const avgVolume = component.analyser ? (sum / component.analyserData.length) : 0;
    const isPlaying = component.playbackState === "playing" && avgVolume > 3;

    // Trigger haptic vibration pulses synced with beat and music intensity
    if (isPlaying) {
      component.triggerBeatHaptic(avgVolume);
    }

    // Apply color-changing background values based on dominant frequency
    const colors = component.getDominantFrequencyColors();
    const layout = component.shadowRoot?.getElementById("app-layout");
    if (layout) {
      layout.style.setProperty("--ambient-bg", colors.ambientBg);
      layout.style.setProperty("--ambient-bg-bright", colors.ambientBgBright);
      layout.style.setProperty("--ambient-bg-solid", colors.ambientBgSolid);
    }

    // Draw 3 layered fluid organic sine/bezier waves
    component.drawFluidWave(ctx, canvas, isPlaying, "rgba(129, 140, 248, 0.38)", 1.25, 0.7, 0); // Indigo
    component.drawFluidWave(ctx, canvas, isPlaying, "rgba(244, 114, 182, 0.42)", 0.85, 1.1, 1.5); // Pink
    component.drawFluidWave(ctx, canvas, isPlaying, "rgba(6, 182, 212, 0.48)", 0.48, 1.5, 3.1); // Cyan
  }
  public getDominantFrequencyColors(component: any) {
    if (!component.analyser || component.playbackState !== "playing") {
      return {
        ambientBg: "rgba(99, 102, 241, 0.12)",
        ambientBgBright: "rgba(129, 140, 248, 0.35)",
        ambientBgSolid: "rgba(129, 140, 248, 1.0)"
      };
    }

    component.analyser.getByteFrequencyData(component.analyserData);
    
    let maxVal = -1;
    let maxIndex = -1;
    
    // Skip the very low-frequency rumble (0-2 bins)
    for (let i = 2; i < component.analyserData.length; i++) {
      if (component.analyserData[i] > maxVal) {
        maxVal = component.analyserData[i];
        maxIndex = i;
      }
    }

    // If quiet, return default indigo color theme
    if (maxVal < 8) {
      return {
        ambientBg: "rgba(99, 102, 241, 0.12)",
        ambientBgBright: "rgba(129, 140, 248, 0.35)",
        ambientBgSolid: "rgba(129, 140, 248, 1.0)"
      };
    }

    const sampleRate = component.liveMusicHelper.audioContext?.sampleRate || 48000;
    const fftSize = component.analyser.fftSize;
    const dominantFreq = maxIndex * sampleRate / fftSize;

    // Use logarithmic scale to map frequency to hue (HSL)
    // 20Hz to 8000Hz range
    const minLnf = Math.log(20);
    const maxLnf = Math.log(8000);
    const lnf = Math.log(Math.max(20, Math.min(8000, dominantFreq)));
    const percent = (lnf - minLnf) / (maxLnf - minLnf);

    // Map percent to a color hue (0 to 360)
    const hue = Math.floor(percent * 360);

    // Calculate dynamic intensity (opacity) based on the peak amplitude
    const amplitudeRatio = maxVal / 255;
    const intensity = 0.08 + amplitudeRatio * 0.18; // Opacity ranges from 0.08 to 0.26

    return {
      ambientBg: `hsla(${hue}, 85%, 60%, ${intensity})`,
      ambientBgBright: `hsla(${hue}, 95%, 65%, ${Math.min(0.7, intensity * 2.2)})`,
      ambientBgSolid: `hsla(${hue}, 85%, 60%, 1.0)`
    };
  }
}

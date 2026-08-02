export function encodeWAV(
  pcmBuffer: Buffer,
  numChannels: number = 2,
  sampleRate: number = 48000,
  bitsPerSample: number = 16
): Buffer {
  const bytesPerSample = bitsPerSample / 8;
  const blockAlign = numChannels * bytesPerSample;
  const dataSize = pcmBuffer.length - (pcmBuffer.length % blockAlign);
  const buffer = Buffer.alloc(44 + dataSize);

  buffer.write("RIFF", 0);
  buffer.writeUInt32LE(36 + dataSize, 4);
  buffer.write("WAVE", 8);

  buffer.write("fmt ", 12);
  buffer.writeUInt32LE(16, 16);
  buffer.writeUInt16LE(1, 20);
  buffer.writeUInt16LE(numChannels, 22);
  buffer.writeUInt32LE(sampleRate, 24);
  buffer.writeUInt32LE(sampleRate * blockAlign, 28);
  buffer.writeUInt16LE(blockAlign, 32);
  buffer.writeUInt16LE(bitsPerSample, 34);

  buffer.write("data", 36);
  buffer.writeUInt32LE(dataSize, 40);

  pcmBuffer.copy(buffer, 44, 0, dataSize);
  return buffer;
}

export interface DecodedWAV {
  pcmData: Buffer;
  numChannels: number;
  sampleRate: number;
  bitsPerSample: number;
}

export function decodeWAV(buffer: Buffer): DecodedWAV {
  if (buffer.length < 44) throw new Error("WAV buffer too small");
  if (buffer.toString("utf8", 0, 4) !== "RIFF" || buffer.toString("utf8", 8, 12) !== "WAVE") {
    throw new Error("Invalid WAV format header");
  }

  let offset = 12;
  let numChannels = 2;
  let sampleRate = 48000;
  let bitsPerSample = 16;
  let pcmData: Buffer = Buffer.alloc(0);

  while (offset < buffer.length - 8) {
    const chunkId = buffer.toString("utf8", offset, offset + 4);
    const chunkSize = buffer.readUInt32LE(offset + 4);
    if (chunkId === "fmt ") {
      numChannels = buffer.readUInt16LE(offset + 10);
      sampleRate = buffer.readUInt32LE(offset + 12);
      bitsPerSample = buffer.readUInt16LE(offset + 22);
    } else if (chunkId === "data") {
      pcmData = buffer.subarray(offset + 8, offset + 8 + chunkSize);
      break;
    }
    offset += 8 + chunkSize;
  }

  return { pcmData, numChannels, sampleRate, bitsPerSample };
}

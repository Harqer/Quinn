import os
import io
import asyncio
import base64
from fastapi import FastAPI, UploadFile, File, WebSocket, WebSocketDisconnect
from pydantic import BaseModel
import tempfile

try:
    from magenta_rt.audio import Waveform
    from magenta_rt.jax.system import MagentaRT2System as MagentaRT2Jax
except ImportError:
    Waveform = None
    MagentaRT2Jax = None

app = FastAPI()
mrt_model = None

@app.on_event("startup")
async def startup_event():
    global mrt_model
    if MagentaRT2Jax:
        print("Loading MagentaRT2 JAX model...")
        mrt_model = MagentaRT2Jax(
            size="mrt2_base",
            temperature=1.3,
            top_k=40,
        )
        print("Model loaded successfully on GPU.")
    else:
        print("Warning: MagentaRT2Jax not found or failed to import.")

@app.post("/api/extract_style")
async def extract_style(audio_file: UploadFile = File(...)):
    """
    Extracts MusicCoCa style embedding from an uploaded audio file.
    (Placeholder implementation for handling full tracks from Spotify).
    """
    with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as tmp:
        content = await audio_file.read()
        tmp.write(content)
        tmp_path = tmp.name
    
    # In a real environment, we would do:
    # wav = Waveform.from_file(tmp_path)
    # embedding = mrt_model.embed_style(wav, use_mapper=True)
    # And store it in a session cache or Redis.
    
    os.remove(tmp_path)
    return {"status": "success", "session_id": "test_session_123"}

@app.websocket("/api/stream_instrumentation")
async def stream_instrumentation(websocket: WebSocket):
    await websocket.accept()
    try:
        data = await websocket.receive_json()
        prompt = data.get("prompt", "ambient background music")
        duration = data.get("duration", 4.0)
        
        frames = int(duration * 25)
        
        if mrt_model:
            # We would use the cached audio embedding here, but for now we fallback to text
            embedding = mrt_model.embed_style(prompt, use_mapper=True)
            wav, state = mrt_model.generate(style=embedding, frames=frames)
            
            # Send back the generated WAV in a base64 encoded chunk
            import wave
            audio_bytes = io.BytesIO()
            wav.write(audio_bytes)
            audio_bytes.seek(0)
            
            b64_chunk = base64.b64encode(audio_bytes.read()).decode('utf-8')
            await websocket.send_json({"chunk": b64_chunk})
            await websocket.send_json({"done": True})
        else:
            # Fallback empty stream for testing without GPU
            for i in range(5):
                await asyncio.sleep(0.5)
                # Send minimal valid wav header chunk in base64
                await websocket.send_json({"chunk": "UklGRiQAAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQAAAAA="})
            await websocket.send_json({"done": True})
            
    except WebSocketDisconnect:
        print("Client disconnected")

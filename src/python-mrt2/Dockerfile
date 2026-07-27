FROM nvidia/cuda:12.1.1-cudnn8-runtime-ubuntu22.04

ENV PYTHONUNBUFFERED=1 \
    DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
    python3.11 \
    python3.11-venv \
    python3.11-dev \
    python3-pip \
    curl \
    git \
    ffmpeg \
    && rm -rf /var/lib/apt/lists/*

RUN curl -LsSf https://astral.sh/uv/install.sh | sh
ENV PATH="/root/.local/bin:${PATH}"

WORKDIR /app
COPY requirements.txt .

# Create a virtual environment with python 3.11 explicitly
RUN uv venv --python 3.11 /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# Install dependencies using uv
RUN uv pip install -r requirements.txt

# Initialize models (MusicCoCa and SpectroStream)
RUN mrt models init
# Download mrt2_base
RUN mrt models download mrt2_base

COPY app.py .

CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8080"]

package orchestrator

import (
	"encoding/json"
	"sync"

	"github.com/gorilla/websocket"
)

type SurfaceType string

const (
	SurfacePhone SurfaceType = "app"
	SurfaceWear  SurfaceType = "wear"
	SurfaceTV    SurfaceType = "tv"
	SurfaceWeb   SurfaceType = "web"
)

type SessionState struct {
	UserID         string      `json:"user_id"`
	ActiveSurface  SurfaceType `json:"active_surface"`
	CurrentTrackID string      `json:"current_track_id"`
	PlaybackPosMs  int64       `json:"playback_pos_ms"`
	IsPlaying      bool        `json:"is_playing"`
}

type SessionHub struct {
	mu       sync.RWMutex
	sessions map[string]*SessionState
	clients  map[string]map[SurfaceType]*websocket.Conn
}

func NewSessionHub() *SessionHub {
	return &SessionHub{
		sessions: make(map[string]*SessionState),
		clients:  make(map[string]map[SurfaceType]*websocket.Conn),
	}
}

func (sh *SessionHub) RegisterClient(userID string, surface SurfaceType, conn *websocket.Conn) {
	sh.mu.Lock()
	defer sh.mu.Unlock()

	if _, exists := sh.clients[userID]; !exists {
		sh.clients[userID] = make(map[SurfaceType]*websocket.Conn)
	}
	sh.clients[userID][surface] = conn
}

func (sh *SessionHub) Handoff(userID string, targetSurface SurfaceType) (*SessionState, error) {
	sh.mu.Lock()
	defer sh.mu.Unlock()

	state, exists := sh.sessions[userID]
	if !exists {
		state = &SessionState{UserID: userID}
		sh.sessions[userID] = state
	}

	state.ActiveSurface = targetSurface

	if surfaceConns, ok := sh.clients[userID]; ok {
		payload, _ := json.Marshal(map[string]interface{}{
			"event": "SESSION_HANDOFF",
			"state": state,
		})
		for _, conn := range surfaceConns {
			_ = conn.WriteMessage(websocket.TextMessage, payload)
		}
	}

	return state, nil
}

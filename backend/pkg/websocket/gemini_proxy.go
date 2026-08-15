package websocket

import (
	"fmt"
	"log/slog"
	"net/http"
	"sync"

	"github.com/gorilla/websocket"
)

var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024 * 64,
	WriteBufferSize: 1024 * 64,
	CheckOrigin: func(r *http.Request) bool {
		return true
	},
}

type GeminiProxyHandler struct {
	geminiEndpoint string
	apiKey         string
}

func NewGeminiProxyHandler(endpoint, apiKey string) *GeminiProxyHandler {
	return &GeminiProxyHandler{
		geminiEndpoint: endpoint,
		apiKey:         apiKey,
	}
}

func (h *GeminiProxyHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	clientConn, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		slog.Error("Failed to upgrade client websocket", "error", err)
		return
	}
	defer clientConn.Close()

	geminiURL := fmt.Sprintf("%s?key=%s", h.geminiEndpoint, h.apiKey)
	geminiConn, resp, err := websocket.DefaultDialer.DialContext(r.Context(), geminiURL, nil)
	if err != nil {
		status := 0
		if resp != nil {
			status = resp.StatusCode
		}
		slog.Error("Failed to connect to Gemini Live API", "error", err, "http_status", status)
		clientConn.WriteJSON(map[string]interface{}{
			"error": map[string]interface{}{
				"code":    "GEMINI_BACKEND_UNAVAILABLE",
				"message": "Unable to establish streaming session with Gemini Live API",
			},
		})
		return
	}
	defer geminiConn.Close()

	var wg sync.WaitGroup
	wg.Add(2)

	// Client -> Backend -> Gemini 3.5 Flash WSS
	go func() {
		defer wg.Done()
		for {
			msgType, payload, err := clientConn.ReadMessage()
			if err != nil {
				if !websocket.IsCloseError(err, websocket.CloseGoingAway, websocket.CloseNormalClosure) {
					slog.Warn("Client read error", "error", err)
				}
				_ = geminiConn.WriteMessage(websocket.CloseMessage, websocket.FormatCloseMessage(websocket.CloseNormalClosure, ""))
				return
			}
			if err := geminiConn.WriteMessage(msgType, payload); err != nil {
				slog.Error("Gemini write error", "error", err)
				return
			}
		}
	}()

	// Gemini 3.5 Flash WSS -> Backend -> Client
	go func() {
		defer wg.Done()
		for {
			msgType, payload, err := geminiConn.ReadMessage()
			if err != nil {
				if !websocket.IsCloseError(err, websocket.CloseGoingAway, websocket.CloseNormalClosure) {
					slog.Warn("Gemini read error", "error", err)
				}
				_ = clientConn.WriteMessage(websocket.CloseMessage, websocket.FormatCloseMessage(websocket.CloseNormalClosure, ""))
				return
			}
			if err := clientConn.WriteMessage(msgType, payload); err != nil {
				slog.Error("Client write error", "error", err)
				return
			}
		}
	}()

	wg.Wait()
}

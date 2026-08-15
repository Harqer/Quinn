package main

import (
	"context"
	"encoding/json"
	"errors"
	"log/slog"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"lyria/backend/pkg/auth"
	"lyria/backend/pkg/dataconnect"
	"lyria/backend/pkg/orchestrator"
	"lyria/backend/pkg/websocket"
)

func main() {
	logger := slog.New(slog.NewJSONHandler(os.Stdout, nil))
	slog.SetDefault(logger)

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	gcpProjectID := os.Getenv("GCP_PROJECT_ID")
	geminiAPIKey := os.Getenv("GEMINI_API_KEY")
	dataConnectURL := os.Getenv("DATACONNECT_ENDPOINT")

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// Initialize Core Services
	authValidator, err := auth.NewValidator(ctx, gcpProjectID)
	if err != nil {
		slog.Warn("Auth validator running in offline/uninitialized mode", "error", err)
	}

	dcClient := dataconnect.NewClient(dataConnectURL)
	_ = orchestrator.NewSessionHub()
	geminiHandler := websocket.NewGeminiProxyHandler(
		"wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1alpha.GenerativeService.BidiGenerateContent",
		geminiAPIKey,
	)

	// Go 1.23 Enhanced Router (Method + Path Matching)
	mux := http.NewServeMux()

	// Public Health Probe
	mux.HandleFunc("GET /healthz", func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(`{"status":"HEALTHY","service":"lyria-go-backend"}`))
	})

	// API Routes
	apiMux := http.NewServeMux()
	apiMux.HandleFunc("POST /api/v1/auth/verify", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.Write([]byte(`{"status":"VERIFIED"}`))
	})
	apiMux.Handle("GET /api/v1/live/ws", geminiHandler)
	apiMux.Handle("GET /api/v1/wear/live/ws", geminiHandler)
	apiMux.HandleFunc("POST /api/v1/session/handoff", func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		w.Write([]byte(`{"status":"HANDOFF_SUCCESS"}`))
	})
	apiMux.HandleFunc("GET /api/v1/catalog/tracks", func(w http.ResponseWriter, r *http.Request) {
		var result map[string]interface{}
		query := `query GetTracks { tracks { id title audioUrl coverUrl } }`
		if err := dcClient.ExecuteQuery(r.Context(), "", query, nil, &result); err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(map[string]interface{}{
			"status": "SUCCESS",
			"data":   result,
		})
	})

	if authValidator != nil {
		mux.Handle("/", authValidator.Middleware(apiMux))
	} else {
		mux.Handle("/", apiMux)
	}

	server := &http.Server{
		Addr:         ":" + port,
		Handler:      mux,
		ReadTimeout:  15 * time.Second,
		WriteTimeout: 15 * time.Second,
		IdleTimeout:  60 * time.Second,
	}

	go func() {
		slog.Info("Starting Go Cloud Run Backend API Gateway", "port", port)
		if err := server.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			slog.Error("Server failed to listen", "error", err)
			os.Exit(1)
		}
	}()

	stop := make(chan os.Signal, 1)
	signal.Notify(stop, os.Interrupt, syscall.SIGTERM)
	<-stop

	slog.Info("Shutting down server gracefully...")
	shutdownCtx, shutdownCancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer shutdownCancel()

	if err := server.Shutdown(shutdownCtx); err != nil {
		slog.Error("Server forced shutdown", "error", err)
	}

	slog.Info("Server exited cleanly")
}

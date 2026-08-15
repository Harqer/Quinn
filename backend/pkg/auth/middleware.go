package auth

import (
	"context"
	"fmt"
	"log/slog"
	"net/http"
	"strings"

	firebase "firebase.google.com/go/v4"
	"firebase.google.com/go/v4/auth"
)

type contextKey string

const UserContextKey contextKey = "user_token"

type Validator struct {
	client *auth.Client
}

func NewValidator(ctx context.Context, gcpProjectID string) (*Validator, error) {
	app, err := firebase.NewApp(ctx, &firebase.Config{ProjectID: gcpProjectID})
	if err != nil {
		return nil, fmt.Errorf("failed to init firebase app: %w", err)
	}

	client, err := app.Auth(ctx)
	if err != nil {
		return nil, fmt.Errorf("failed to init firebase auth client: %w", err)
	}

	return &Validator{client: client}, nil
}

func (v *Validator) ValidateToken(ctx context.Context, idToken string) (*auth.Token, error) {
	token, err := v.client.VerifyIDToken(ctx, idToken)
	if err != nil {
		return nil, fmt.Errorf("invalid firebase id token: %w", err)
	}
	return token, nil
}

func (v *Validator) Middleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		authHeader := r.Header.Get("Authorization")
		if authHeader == "" {
			writeAuthError(w, http.StatusUnauthorized, "MISSING_AUTH_HEADER", "Authorization header required")
			return
		}

		parts := strings.SplitN(authHeader, " ", 2)
		if len(parts) != 2 || !strings.EqualFold(parts[0], "Bearer") {
			writeAuthError(w, http.StatusUnauthorized, "INVALID_AUTH_FORMAT", "Authorization header must be 'Bearer <token>'")
			return
		}

		token, err := v.ValidateToken(r.Context(), parts[1])
		if err != nil {
			slog.Warn("Authentication failed", "error", err, "path", r.URL.Path)
			writeAuthError(w, http.StatusUnauthorized, "TOKEN_VERIFICATION_FAILED", err.Error())
			return
		}

		ctx := context.WithValue(r.Context(), UserContextKey, token)
		next.ServeHTTP(w, r.WithContext(ctx))
	})
}

func writeAuthError(w http.ResponseWriter, status int, code, msg string) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	fmt.Fprintf(w, `{"error":{"code":"%s","message":"%s","recoverable":true}}`, code, msg)
}

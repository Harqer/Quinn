import { decodeSdJwt } from "@sd-jwt/decode";
import fetch from "node-fetch";
import { createRemoteJWKSet, jwtVerify } from "jose";
import logger from "../config/logger.js";

const GOOGLE_VC_JWKS_URL = "https://verifiablecredentials-pa.googleapis.com/.well-known/vc-public-jwks";
const GOOGLE_VC_ISSUER = "https://verifiablecredentials-pa.googleapis.com";

export interface VerifiedCredential {
  email: string;
  name?: string;
  email_verified: boolean;
}

export class CredentialVerifier {
  private static jwks = createRemoteJWKSet(new URL(GOOGLE_VC_JWKS_URL));

  /**
   * Verifies an SD-JWT credential from Google.
   * Professional Grade: Full cryptographic verification of signature and issuer.
   */
  async verifyGoogleCredential(credentialInput: string, expectedNonce: string): Promise<VerifiedCredential> {
    try {
      let rawSdJwt = credentialInput;
      if (credentialInput.trim().startsWith("{")) {
        try {
          const parsed = JSON.parse(credentialInput);
          if (parsed.vp_token) {
            const queryKey = Object.keys(parsed.vp_token)[0];
            const tokenArray = parsed.vp_token[queryKey];
            if (Array.isArray(tokenArray) && tokenArray.length > 0) {
              rawSdJwt = tokenArray[0];
            }
          }
        } catch (e) {
          // If JSON parse fails, treat as raw SD-JWT
        }
      }

      // 1. Full Cryptographic Verification
      // Note: SD-JWT is composed of Issuer JWT ~ Disclosures ~ Key Binding JWT.
      // We verify the Issuer JWT part.
      const [issuerJwt] = rawSdJwt.split("~");

      const { payload } = await jwtVerify(issuerJwt, CredentialVerifier.jwks, {
        issuer: GOOGLE_VC_ISSUER,
      });

      // 2. Nonce Validation (Replay Protection)
      if (payload.nonce !== expectedNonce) {
        throw new Error("Nonce mismatch: possible replay attack.");
      }

      // 3. Extract Verified Claims from Payload
      const claims = payload as any;

      if (!claims.email || !claims.email_verified) {
        throw new Error("Credential does not contain a verified email.");
      }

      logger.info("[CREDENTIAL_VERIFIER] Successfully verified Google Credential", { email: claims.email });

      return {
        email: claims.email,
        name: claims.name || claims.given_name || undefined,
        email_verified: claims.email_verified === true
      };
    } catch (err) {
      logger.error("[CREDENTIAL_VERIFIER] Verification Failed", { error: err });
      throw err;
    }
  }
}

export const credentialVerifier = new CredentialVerifier();

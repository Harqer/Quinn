import { z } from "zod";
import logger from "../config/logger.js";
import { auth } from "../config/firebase.js";
import { decodeSdJwt } from "@sd-jwt/decode";
import jwt from "jsonwebtoken";
import jwksClient from "jwks-rsa";

const client = jwksClient({
  jwksUri: "https://verifiablecredentials-pa.googleapis.com/.well-known/vc-public-jwks",
});

function getKey(header: any, callback: any) {
  client.getSigningKey(header.kid, function (err, key) {
    if (err) {
      callback(err);
      return;
    }
    const signingKey = key?.getPublicKey();
    callback(null, signingKey);
  });
}

// A placeholder service for the verified email logic.
export const verifiedEmailService = async (responseJsonString: string, nonce: string) => {
  try {
    const responseData = JSON.parse(responseJsonString);
    const vpToken = responseData.vp_token;
    
    if (!vpToken || !vpToken.user_info_query) {
      throw new Error("Invalid VP token format");
    }

    const rawSdJwt = vpToken.user_info_query[0];
    
    const crypto = await import("crypto");
    const hasher = (data: string | ArrayBuffer) => {
        const strData = typeof data === 'string' ? data : Buffer.from(data).toString('utf-8');
        return new Uint8Array(crypto.createHash("sha256").update(strData).digest());
    };
    const decoded = await decodeSdJwt(rawSdJwt, hasher);

    const claims = decoded.jwt.payload as any;

    if (claims.iss !== "https://verifiablecredentials-pa.googleapis.com") {
      throw new Error("Invalid issuer");
    }

    // Usually we would verify the nonce and the cnf (Key Binding JWT) here as well.

    if (claims.email_verified !== true) {
      throw new Error("Email not verified by issuer");
    }

    const email = claims.email;
    const name = claims.name || claims.given_name || email.split("@")[0];

    // Check if user exists in Firebase Auth, otherwise create
    let uid;
    try {
      const userRecord = await auth.getUserByEmail(email);
      uid = userRecord.uid;
    } catch (e: any) {
      if (e.code === "auth/user-not-found") {
        const newUser = await auth.createUser({
          email: email,
          emailVerified: true,
          displayName: name,
        });
        uid = newUser.uid;
      } else {
        throw e;
      }
    }

    // Generate a custom token for the client to log in
    const customToken = await auth.createCustomToken(uid);

    return {
      success: true,
      email,
      name,
      uid,
      customToken
    };

  } catch (err) {
    logger.error("Failed to verify email credential", { error: err });
    throw err;
  }
};

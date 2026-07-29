import fetch from "node-fetch";

async function main() {
  const clientId = "3a6583d1677046308553878381a2a3bd";
  const clientSecret = "0ffbdeb7e1c943a9b08ee5d8a31bce47";
  const basicAuth = Buffer.from(`${clientId}:${clientSecret}`).toString("base64");

  console.log("Testing Spotify Client Credentials flow...");
  const res = await fetch("https://accounts.spotify.com/api/token", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      "Authorization": `Basic ${basicAuth}`
    },
    body: "grant_type=client_credentials"
  });

  if (!res.ok) {
    const errorText = await res.text();
    console.error("Failed to get token:", res.status, errorText);
    return;
  }

  const data = await res.json() as any;
  console.log("Token received successfully:", Object.keys(data));
}

main().catch(console.error);

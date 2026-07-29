import fetch from "node-fetch";

async function main() {
  const clientId = "3a6583d1677046308553878381a2a3bd";
  const clientSecret = "0ffbdeb7e1c943a9b08ee5d8a31bce47";
  const basicAuth = Buffer.from(`${clientId}:${clientSecret}`).toString("base64");

  const tokenRes = await fetch("https://accounts.spotify.com/api/token", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      "Authorization": `Basic ${basicAuth}`
    },
    body: "grant_type=client_credentials"
  });
  const { access_token } = await tokenRes.json() as any;

  const searchRes = await fetch(`https://api.spotify.com/v1/search?q=genre:electronic&type=track&limit=1`, {
    headers: {
      "Authorization": `Bearer ${access_token}`
    }
  });
  const text = await searchRes.text();
  console.log("Search result text:", text);
}

main().catch(console.error);

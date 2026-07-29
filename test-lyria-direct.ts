import fetch from "node-fetch";

async function test() {
  const tokenRes = await fetch("http://127.0.0.1:8081/api/music/token");
  const { token } = await tokenRes.json() as any;
  console.log("Got token", token ? "Yes" : "No");

  const res = await fetch("http://127.0.0.1:8081/api/music/execute-tool", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    },
    body: JSON.stringify({
      name: "generate_full_track",
      args: { prompt: "A chill lofi beat with a relaxing piano melody" }
    })
  });
  const data = await res.json() as any;
  console.log("Result keys:", Object.keys(data));
  if (data.audioUrl) {
    console.log("Audio URL starts with:", data.audioUrl.substring(0, 30));
  }
}
test().catch(console.error);

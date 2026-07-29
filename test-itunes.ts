import fetch from "node-fetch";

async function main() {
  const searchRes = await fetch(`https://itunes.apple.com/search?term=electronic&entity=song&limit=1`);
  const searchData = await searchRes.json() as any;
  console.log("Track:", searchData.results[0]?.trackName, searchData.results[0]?.previewUrl);
}

main().catch(console.error);

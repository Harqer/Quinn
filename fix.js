const fs = require('fs');

function replaceFile(path, oldText, newText) {
  let c = fs.readFileSync(path, 'utf8');
  c = c.replaceAll(oldText, newText);
  fs.writeFileSync(path, c);
}

replaceFile('server/routes/interactions.ts', 'let parts = [];', 'let parts: any[] = [];');
replaceFile('server/routes/interactions.ts', 'audioContent.data', '(audioContent as any).data');
replaceFile('server/routes/music.ts', 'audioContent.data', '(audioContent as any).data');

let community = fs.readFileSync('server/routes/community.ts', 'utf8');
const match = community.match(/async function getSpotifyToken[\s\S]*?return null;\n}/);
if (match) {
  const func = match[0];
  community = community.replace(func, '');
  fs.writeFileSync('server/routes/community.ts', community);
  
  let spotify = fs.readFileSync('server/routes/spotify.ts', 'utf8');
  if (!spotify.includes('async function getSpotifyToken')) {
    spotify = spotify.replace('export default router;', func + '\n\nexport default router;');
    fs.writeFileSync('server/routes/spotify.ts', spotify);
  }
}

// We also need to fix `ai.interactions.create` or `ai.models.generateContent` type issues.
// `error TS2769: No overload matches this call.`
replaceFile('server/routes/interactions.ts', 'await ai.interactions.create', 'await (ai as any).interactions.create');
replaceFile('server/routes/music.ts', 'await ai.interactions.create', 'await (ai as any).interactions.create');

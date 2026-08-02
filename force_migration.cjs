const { spawn } = require('child_process');

const queries = `
ALTER TABLE "public"."album"
DROP COLUMN "artist_name",
DROP COLUMN "image_url",
DROP COLUMN "name",
DROP COLUMN "release_year";

ALTER TABLE "public"."audiobook"
DROP COLUMN "audio_url",
DROP COLUMN "author",
DROP COLUMN "duration",
DROP COLUMN "image_url";

ALTER TABLE "public"."category"
DROP COLUMN "image_url";

ALTER TABLE "public"."playlist"
DROP COLUMN "image_url";

ALTER TABLE "public"."track"
DROP COLUMN "album_name",
DROP COLUMN "artist_name",
DROP COLUMN "image_url",
DROP COLUMN "name";

DROP TABLE "public"."podcast_episode";
DROP TABLE "public"."podcast";
\\q
`;

const proc = spawn('npx', ['-y', 'firebase-tools@latest', 'dataconnect:sql:shell'], {
  stdio: ['pipe', 'inherit', 'inherit']
});

proc.stdin.write(queries);
proc.stdin.end();

proc.on('close', (code) => {
  console.log(`child process exited with code ${code}`);
});

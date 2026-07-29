const fs = require('fs');
const path = require('path');

const emojiRegex = /[\u{1F300}-\u{1F9FF}\u{2600}-\u{26FF}\u{2700}-\u{27BF}\u{1F600}-\u{1F64F}\u{1F680}-\u{1F6FF}]/gu;

function scan(dir) {
  const entries = fs.readdirSync(dir, { withFileTypes: true });
  for (const entry of entries) {
    const res = path.resolve(dir, entry.name);
    if (entry.isDirectory()) {
      if (entry.name !== 'node_modules' && entry.name !== 'dist' && entry.name !== '.git') {
        scan(res);
      }
    } else {
      if (res.endsWith('.tsx') || res.endsWith('.ts')) {
        const content = fs.readFileSync(res, 'utf-8');
        
        // Check for emojis
        const lines = content.split('\n');
        lines.forEach((line, i) => {
          if (emojiRegex.test(line)) {
            // Filter out system prompt texts or comments if possible, but let's just log them first
            console.log(`EMOJI in ${res}:${i + 1}: ${line.trim()}`);
          }
          if (/console\.log/.test(line) && !res.includes('logger') && !res.includes('test')) {
            console.log(`CONSOLE.LOG in ${res}:${i + 1}: ${line.trim()}`);
          }
          if (/mock[A-Z_]/i.test(line) || /dummy/i.test(line) || /TODO:/i.test(line)) {
            console.log(`MOCK/TODO in ${res}:${i + 1}: ${line.trim()}`);
          }
        });
      }
    }
  }
}

scan(path.join(__dirname, 'src'));

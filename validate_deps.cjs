const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const pkg = JSON.parse(fs.readFileSync('package.json', 'utf-8'));
const deps = Object.keys(pkg.dependencies || {}).concat(Object.keys(pkg.devDependencies || {}));
const imports = execSync('grep -h -r -E "^(import|export) .* from" src/web server.ts | grep -o -E "from [\x27\x22].*[\x27\x22]"', {encoding: 'utf-8'})
  .split('\n')
  .filter(Boolean)
  .map(line => line.replace(/^from ['"]|['"]$/g, ''));

const missing = new Set();
imports.forEach(i => {
  if (i.startsWith('.') || i.startsWith('/') || i.startsWith('vite/') || i.startsWith('virtual:')) return;
  const pkgName = i.startsWith('@') ? i.split('/').slice(0, 2).join('/') : i.split('/')[0];
  // Node built-ins:
  if (['path','fs','crypto','child_process','http','https','url','events','os','stream','util', 'buffer'].includes(pkgName)) return;
  if (!deps.includes(pkgName)) {
    missing.add(pkgName);
  }
});
console.log('Missing dependencies:', Array.from(missing));

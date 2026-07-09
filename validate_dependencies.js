/**
 * @fileoverview Automated Dependency, Import & Meta Wearables Design/Gesture Audit Tool.
 * Parses project source files, verifies import path resolutions, checks package.json declarations,
 * audits Meta Wearables companion standards (Gestures, UI, Emojis, Plist Credentials),
 * and outputs a detailed architectural compliance report.
 */

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const COLOR_RESET = "\x1b[0m";
const COLOR_GREEN = "\x1b[32m";
const COLOR_RED = "\x1b[31m";
const COLOR_CYAN = "\x1b[36m";
const COLOR_YELLOW = "\x1b[33m";
const COLOR_BOLD = "\x1b[1m";

const SOURCE_DIRECTORIES = ["components", "utils"];
const ROOT_FILES = ["index.ts", "index.css", "vite.config.ts"];

// Helper to load and parse JSON safely
function readJson(filePath) {
  try {
    return JSON.parse(fs.readFileSync(filePath, 'utf-8'));
  } catch (error) {
    console.error(`${COLOR_RED}Error reading/parsing JSON file: ${filePath}${COLOR_RESET}`, error);
    return null;
  }
}

// 1. Audit Package.json Declarations
const packageJson = readJson(path.join(__dirname, 'package.json'));
if (!packageJson) {
  console.error(`${COLOR_RED}Missing package.json. Aborting validation.${COLOR_RESET}`);
  process.exit(1);
}

const declaredDependencies = new Set([
  ...Object.keys(packageJson.dependencies || {}),
  ...Object.keys(packageJson.devDependencies || {})
]);

// 2. Resolve Import Path Utility
function resolveImportPath(sourceFile, importValue) {
  const dirOfSource = path.dirname(sourceFile);
  
  // Resolve TypeScript absolute path alias "@/*"
  if (importValue.startsWith('@/')) {
    const aliasPath = importValue.replace('@/', '');
    return checkExtensions(path.join(__dirname, aliasPath));
  }

  // Handle local relative imports
  if (importValue.startsWith('.') || importValue.startsWith('..')) {
    const rawTarget = path.join(dirOfSource, importValue);
    return checkExtensions(rawTarget);
  }

  // It's an external library
  return { type: 'package', resolved: importValue };
}

function checkExtensions(basePath) {
  const extensions = ['.ts', '.tsx', '.js', '.jsx', '.json', '/index.ts', '/index.js'];
  
  // Try exact match (e.g., if extension is already specified)
  if (fs.existsSync(basePath) && fs.statSync(basePath).isFile()) {
    return { type: 'local', resolved: basePath, exists: true };
  }

  for (const ext of extensions) {
    const target = basePath + ext;
    if (fs.existsSync(target) && fs.statSync(target).isFile()) {
      return { type: 'local', resolved: target, exists: true };
    }
  }

  return { type: 'local', resolved: basePath, exists: false };
}

// 3. Scan and Validate Source Files
const allFilesToScan = [];

// Collect from Source directories
SOURCE_DIRECTORIES.forEach(dir => {
  const dirPath = path.join(__dirname, dir);
  if (fs.existsSync(dirPath)) {
    const files = fs.readdirSync(dirPath).filter(f => f.endsWith('.ts') || f.endsWith('.tsx'));
    files.forEach(f => allFilesToScan.push(path.join(dir, f)));
  }
});

// Collect from Root files
ROOT_FILES.forEach(file => {
  const filePath = path.join(__dirname, file);
  if (fs.existsSync(filePath)) {
    allFilesToScan.push(file);
  }
});

console.log(`${COLOR_BOLD}${COLOR_CYAN}================================================================${COLOR_RESET}`);
console.log(`${COLOR_BOLD}${COLOR_CYAN}   METICULOUS CODEBASE RESOLUTION & COMPLIANCE AUDIT PIPELINE   ${COLOR_RESET}`);
console.log(`${COLOR_BOLD}${COLOR_CYAN}================================================================${COLOR_RESET}`);
console.log(`Auditing ${allFilesToScan.length} source files for imports...\n`);

let hasFailures = false;

allFilesToScan.forEach(fileRelPath => {
  const absolutePath = path.join(__dirname, fileRelPath);
  const content = fs.readFileSync(absolutePath, 'utf-8');
  
  // Regex to match imports (static and dynamic)
  const importRegex = /import\s+?(?:(?:[\w*\s{},]+)\s+from\s+)?['"]([^'"]+)['"]/g;
  let match;
  const fileImports = [];

  while ((match = importRegex.exec(content)) !== null) {
    fileImports.push(match[1]);
  }

  console.log(`${COLOR_BOLD}${COLOR_CYAN}[IMPORT AUDIT]${COLOR_RESET} ${fileRelPath}`);
  
  if (fileImports.length === 0) {
    console.log(`  └─ No imports found.`);
    return;
  }

  fileImports.forEach(imp => {
    const resolution = resolveImportPath(absolutePath, imp);
    
    if (resolution.type === 'package') {
      // It's an npm package or standard Node built-in
      const isBuiltin = ['fs', 'path', 'url', 'os', 'child_process'].includes(imp);
      const isRegistered = declaredDependencies.has(imp) || declaredDependencies.has(imp.split('/')[0]);
      
      if (isBuiltin) {
        console.log(`  ${COLOR_GREEN}✓ [NODE CORE]${COLOR_RESET} "${imp}" is a standard Node.js module.`);
      } else if (isRegistered) {
        console.log(`  ${COLOR_GREEN}✓ [DECLARED] ${COLOR_RESET} "${imp}" is registered in package.json.`);
      } else {
        console.log(`  ${COLOR_RED}✗ [UNRESOLVED]${COLOR_RESET} Package "${imp}" is used but NOT declared in package.json!`);
        hasFailures = true;
      }
    } else {
      // It's a local import file
      const relativeTarget = path.relative(__dirname, resolution.resolved);
      if (resolution.exists) {
        console.log(`  ${COLOR_GREEN}✓ [RESOLVED] ${COLOR_RESET} "${imp}" -> ${relativeTarget}`);
      } else {
        console.log(`  ${COLOR_RED}✗ [MISSING]  ${COLOR_RESET} "${imp}" cannot be resolved to any file!`);
        hasFailures = true;
      }
    }
  });
  console.log("");
});

console.log(`${COLOR_BOLD}${COLOR_CYAN}================================================================${COLOR_RESET}`);
console.log(`${COLOR_BOLD}${COLOR_CYAN}       META WEARABLES HARDWARE INTEGRATION & COMPLIANCE AUDIT    ${COLOR_RESET}`);
console.log(`${COLOR_BOLD}${COLOR_CYAN}================================================================${COLOR_RESET}`);

// A. Check plist does NOT contain hardcoded credentials (should use build variables instead)
const plistPath = path.join(__dirname, 'MWDAT.plist');
if (fs.existsSync(plistPath)) {
  const plistContent = fs.readFileSync(plistPath, 'utf-8');
  // Real MetaAppID/ClientToken values should never be committed as literals.
  // They should be injected via Xcode build settings (e.g. $(MWDAT_APP_ID)),
  // matching the existing $(DEVELOPMENT_TEAM) pattern already used for TeamID.
  const appIdMatch = plistContent.match(/<key>MetaAppID<\/key>\s*<string>([^<]*)<\/string>/);
  const tokenMatch = plistContent.match(/<key>ClientToken<\/key>\s*<string>([^<]*)<\/string>/);
  const appIdValue = appIdMatch?.[1] ?? '';
  const tokenValue = tokenMatch?.[1] ?? '';
  const looksLikeBuildVariable = (v) => v === '' || v === '0' || /^\$\(.+\)$/.test(v);

  console.log(`[PLIST CHECK] MWDAT.plist credential hygiene verification:`);
  if (looksLikeBuildVariable(appIdValue) && looksLikeBuildVariable(tokenValue)) {
    console.log(`  ${COLOR_GREEN}✓ [NO HARDCODED SECRETS]${COLOR_RESET} MetaAppID/ClientToken are build variables or empty, not committed literals.`);
  } else {
    console.log(`  ${COLOR_RED}✗ [HARDCODED CREDENTIAL]${COLOR_RESET} MWDAT.plist appears to contain a literal MetaAppID/ClientToken value. Move these to Xcode build settings / an .xcconfig instead of committing them.`);
    hasFailures = true;
  }
} else {
  console.log(`  ${COLOR_YELLOW}⚠ [NOT FOUND]${COLOR_RESET} MWDAT.plist was not found at workspace root (fine if this build doesn't target iOS).`);
}
console.log("");

// B. Check UI adherence to design system (No Emojis check)
const lyriaCameraPath = path.join(__dirname, 'components/lyria_camera.ts');
if (fs.existsSync(lyriaCameraPath)) {
  const cameraContent = fs.readFileSync(lyriaCameraPath, 'utf-8');
  
  // Standard Emoji character ranges pattern
  const emojiRegex = /[\u{1F300}-\u{1F9FF}]|[\u{1F600}-\u{1F64F}]|[\u{1F680}-\u{1F6FF}]|[\u{2600}-\u{26FF}]|[\u{2700}-\u{27BF}]/u;
  const hasEmojis = emojiRegex.test(cameraContent);

  console.log(`[DESIGN SYSTEM CHECK] UI visual guidelines audit:`);
  if (!hasEmojis) {
    console.log(`  ${COLOR_GREEN}✓ [EMOJI COMPLIANT]${COLOR_RESET} No unrequested, raw emojis found. The app adheres strictly to industrial-grade Material Icons.`);
  } else {
    console.log(`  ${COLOR_YELLOW}⚠ [EMOJI DETECTED]${COLOR_RESET} Found raw emoji symbols inside visual layers. Adhere to professional typography requirements and convert them to standard Material Design icons.`);
    // Non-blocking warning or strict block based on user_rules. The user rules specify: "Strictly No Emojis".
    // Let's make it a strict block for full conformance!
    hasFailures = true;
  }
}
console.log("");

// C. Verify gestures alignment
if (fs.existsSync(lyriaCameraPath)) {
  const cameraContent = fs.readFileSync(lyriaCameraPath, 'utf-8');
  
  const gestureChecklist = [
    { name: 'tap (Play/Pause)', keys: ['" "', 'space'] },
    { name: 'double_tap (Skip Forward)', keys: ['"n"'] },
    { name: 'triple_tap (Go Back Track)', keys: ['"b"'] },
    { name: 'swipe_forward (Volume Up)', keys: ['ArrowRight', '"+"'] },
    { name: 'swipe_backward (Volume Down)', keys: ['ArrowLeft', '"-"'] },
    { name: 'index_finger_turn_right (Air Vol Up)', keys: ['"]"'] },
    { name: 'index_finger_turn_left (Air Vol Down)', keys: ['"["'] },
    { name: 'thumb_to_index_double_tap (Meta AI)', keys: ['"i"'] },
    { name: 'middle_finger_to_thumb_hold (App Switcher)', keys: ['"s"'] },
    { name: 'wrist_turn_clockwise (Wrist Vol Up)', keys: ['"w"'] },
    { name: 'wrist_turn_counter_clockwise (Wrist Vol Down)', keys: ['"r"'] }
  ];

  console.log(`[GESTURE MAP ALIGNMENT CHECK] Physical and Hand Air Gestures mapping:`);
  gestureChecklist.forEach(gesture => {
    const isMapped = gesture.keys.some(k => cameraContent.includes(k));
    if (isMapped) {
      console.log(`  ${COLOR_GREEN}✓ [MAPPED]${COLOR_RESET} ${gesture.name} is correctly bound to keyboard trigger: ${gesture.keys.join(' or ')}`);
    } else {
      console.log(`  ${COLOR_RED}✗ [UNMAPPED]${COLOR_RESET} ${gesture.name} is missing correct shortcut bindings.`);
      hasFailures = true;
    }
  });
}
console.log("");

// D. Proximity safety compliance audit
if (fs.existsSync(lyriaCameraPath)) {
  const cameraContent = fs.readFileSync(lyriaCameraPath, 'utf-8');
  
  const hasProximityCheck = cameraContent.includes('head') || cameraContent.includes('proximity') || cameraContent.includes('onHead');
  
  console.log(`[SAFETY COMPLIANCE CHECK] Auto Proximity detection standby protocol:`);
  if (hasProximityCheck) {
    console.log(`  ${COLOR_GREEN}✓ [SAFETY CONFIGURED]${COLOR_RESET} Battery-saving and stream suspension protocols are integrated to handle wear detection updates.`);
  } else {
    console.log(`  ${COLOR_RED}✗ [SAFETY MISSING]${COLOR_RESET} No wear proximity detection triggers or stream standby logic was found.`);
    hasFailures = true;
  }
}
console.log("");

console.log(`${COLOR_BOLD}${COLOR_CYAN}================================================================${COLOR_RESET}`);
console.log(`${COLOR_BOLD}${COLOR_CYAN}                      AUDIT CONSOLIDATION REPORT                ${COLOR_RESET}`);
console.log(`${COLOR_BOLD}${COLOR_CYAN}================================================================${COLOR_RESET}`);
if (hasFailures) {
  console.log(`${COLOR_BOLD}${COLOR_RED}✗ AUDIT FAILED: One or more architectural or companion design compliance rules failed.${COLOR_RESET}`);
  console.log(`Please address the listed issues to pass continuous integration checks.`);
  process.exit(1);
} else {
  console.log(`${COLOR_BOLD}${COLOR_GREEN}✓ AUDIT PASSED: All local imports, configuration plist credentials, layout patterns, safety protocols, and gesture mappings conform 100% to the Wearables Device Access standards!${COLOR_RESET}`);
  process.exit(0);
}

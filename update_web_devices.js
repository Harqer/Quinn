const fs = require('fs');
const file = '/home/shaolin/lyria/src/web/features/devices/DevicesScreen.tsx';
let content = fs.readFileSync(file, 'utf8');

// We need to implement actual gatt connection for no-mock.

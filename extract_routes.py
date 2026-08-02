import re
import os

with open("server.ts", "r", encoding="utf-8") as f:
    lines = f.readlines()

os.makedirs("server/routes", exist_ok=True)
os.makedirs("server/middleware", exist_ok=True)

# Define route mappings
# Each block will be a separate router file
routers = {
    "webhooks": [],
    "music": [],
    "interactions": [],
    "logs": [],
    "community": [],
    "spotify": [],
    "admin": []
}

def get_router(url):
    if "webhooks" in url or "vulnerability" in url: return "webhooks"
    if "lyria" in url: return "music"
    if "generate" in url or "command" in url or "interactions" in url: return "interactions"
    if "logs" in url: return "logs"
    if "community" in url: return "community"
    if "spotify" in url: return "spotify"
    if "admin" in url: return "admin"
    if "config" in url: return "interactions"
    return "unknown"

current_router = None
route_buffer = []
server_ts_new = []

# Collect imports and initial setup for server.ts
in_db_mock = False

for i, line in enumerate(lines):
    # Strip out the InMemoryCollection mock
    if "class InMemoryCollection {" in line:
        in_db_mock = True
    if in_db_mock:
        if line.startswith("}") and lines[i-1].strip() == "get: async () => {":
             pass # Not the end of class
        # simple heuristic to find end of InMemoryCollection class
        if line == "}\n" and lines[i-1].strip() == "return this.createQuery().get();":
            in_db_mock = False
        continue

    if "let useInMemoryDb = false;" in line:
        continue
    if "if (useInMemoryDb) {" in line or "return new InMemoryCollection(name) as any;" in line:
        continue
    
    # We also need to strip out the DB fallback test
    if "try {" in line and "Testing Firestore connectivity" in lines[i+1] if i+1 < len(lines) else False:
        in_db_mock = True # skip this block
        continue

    # Identify route starts
    match = re.match(r'^app\.(get|post|put|delete|patch)\("(/api/[^"]+)"', line)
    if match:
        method = match.group(1)
        url = match.group(2)
        current_router = get_router(url)
        # Convert app.post("/api/...", ...) to router.post("/...", ...)
        # Wait, the prompt says split logically into music.ts, admin.ts, etc.
        # We can just keep the exact route definitions but change `app.` to `router.`
        # Actually it's simpler to just do `router.post("url", ...)` but what if url is `/api/webhooks/github`? We'll mount it at `/` or `/api`.
        # Let's mount routers at `/` so we don't have to change the URLs.
        line = line.replace("app.", "router.")
        routers[current_router].append(line)
    elif current_router:
        # Check if we reached the end of the route
        if line == "});\n" and i+1 < len(lines) and lines[i+1] == "\n":
            routers[current_router].append(line)
            current_router = None
        elif line.startswith("app.use(") and i > 1900:
            current_router = None
            server_ts_new.append(line)
        elif line.startswith("const server = app.listen("):
            current_router = None
            server_ts_new.append(line)
        else:
            routers[current_router].append(line)
    else:
        # Keep non-route stuff in server.ts, but remove old middlewares that we extracted
        if "const verifyFirebaseToken" in line:
            in_db_mock = True # skip
        elif "const apiLimiter" in line:
            in_db_mock = True # skip
        elif "const verifyAppCheck" in line:
            in_db_mock = True
        elif "const checkDailyQuota" in line:
            in_db_mock = True
        else:
            server_ts_new.append(line)

# Let's refine the in_db_mock skipping
# It's better to just do this properly.

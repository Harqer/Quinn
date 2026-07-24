FROM node:22-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build && npx esbuild src/index.ts --bundle --platform=node --target=node22 --format=esm --outdir=dist --packages=external
EXPOSE 8080
ENV PORT=8080
CMD ["node", "dist/index.js"]

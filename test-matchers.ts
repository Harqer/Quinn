import app from "./src/app.js";
app.router.stack.forEach((layer: any) => {
  if (layer.name === 'router') {
    console.log("ROUTER LAYER matchers:", layer.matchers);
  }
});

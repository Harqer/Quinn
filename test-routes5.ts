import app from "./src/app.js";
app.router.stack.forEach((layer: any) => {
  if (layer.route) {
    console.log("ROUTE:", layer.route.path);
  } else if (layer.name === 'router') {
    console.log("ROUTER MOUNTED:", layer.regexp);
  }
});

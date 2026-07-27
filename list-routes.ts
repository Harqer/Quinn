import app from "./src/app.js";
app._router.stack.forEach((layer: any) => {
  if (layer.route) {
    console.log(layer.route.path);
  } else if (layer.name === 'router') {
    console.log("Router mounted at RegExp: ", layer.regexp);
  }
});

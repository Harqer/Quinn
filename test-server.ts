import express from "express";
const app = express();
app.listen(8080, () => { console.log("listening on 8080"); });
setInterval(() => console.log("alive"), 5000);

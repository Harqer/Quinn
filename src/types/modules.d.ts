declare module "express" {
  import * as express from "express-serve-static-core";
  export = express;
}

declare module "compression";
declare module "ws";
declare module "helmet";

import winston from "winston";
import { LoggingWinston } from "@google-cloud/logging-winston";

const logger = winston.createLogger({
  level: "info",
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.json()
  ),
  transports: [
    process.env.NODE_ENV === "production"
      ? new LoggingWinston({
          prefix: "musically-backend",
          logName: "musically_logs",
        })
      : new winston.transports.Console({
          format: winston.format.combine(
            winston.format.colorize(),
            winston.format.simple()
          )
        })
  ],
});

export default logger;

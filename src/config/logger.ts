import winston from "winston";
import { LoggingWinston } from "@google-cloud/logging-winston";

const logger = winston.createLogger({
  level: "info",
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.json()
  ),
  transports: [
    new LoggingWinston({
      prefix: "musically-backend",
      logName: "musically_logs",
    })
  ],
});

export default logger;

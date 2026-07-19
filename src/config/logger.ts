import winston from "winston";
import { LoggingWinston } from "@google-cloud/logging-winston";

const isProduction = process.env.NODE_ENV === "production" || !!process.env.K_SERVICE;

const logger = winston.createLogger({
  level: "info",
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.json()
  ),
  transports: [],
});

if (isProduction) {
  // Google Cloud Logging transport
  const loggingWinston = new LoggingWinston({
    prefix: "musically-backend",
    logName: "musically_logs",
  });
  logger.add(loggingWinston);
} else {
  // Local console transport
  logger.add(new winston.transports.Console({
    format: winston.format.combine(
      winston.format.colorize(),
      winston.format.simple()
    ),
  }));
}

export default logger;

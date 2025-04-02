import { createLogger, format, transports, Logger } from "winston";

const logger: Logger = createLogger({
	level: process.env.LOG_LEVEL || "info",
	format: format.combine(
		format.timestamp({ format: "YYYY-MM-DD HH:mm:ss" }),
		format.printf(({ level, message, timestamp }) => `${timestamp} [${level.toUpperCase()}]: ${message}`),
		format.colorize({ all: true })
	),
	transports: [
		new transports.Console(),
		new transports.File({ filename: "logs/app.log", level: "info" }), // Output logs to a file
	],
});

// Example to log uncaught exceptions
logger.exceptions.handle(
	new transports.Console({ format: format.simple() }),
	new transports.File({ filename: "logs/exceptions.log" })
);

export default logger;

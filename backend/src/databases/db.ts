import mongoose from "mongoose";

export const connectDB = async () => {
	try {
		const mongoUri = process.env.MONGO_URI;
		if (!mongoUri) {
			throw new Error("MongoDb connection URI required");
		}
		await mongoose.connect(mongoUri);
		console.log("MongoDB connected");
	} catch (err) {
		console.error("Database connection failed", err);
		process.exit(1);
	}
};

import mongoose from 'mongoose';

export const connectDB = async () => {
  try {
    await mongoose.connect(
      process.env.MONGO_URI || 'mongodb://admin:admin123@localhost:27017/',
    );
    console.log('MongoDB connected');
  } catch (err) {
    console.error('Database connection failed', err);
    process.exit(1);
  }
};

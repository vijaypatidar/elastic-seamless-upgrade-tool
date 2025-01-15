# Stage 1: Build Stage
FROM node:18.20.4 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Compile TypeScript code into JavaScript
RUN npm run build

# Stage 2: Production Stage
FROM node:18-alpine

# Set the working directory in the container
WORKDIR /app

# Copy only the compiled files from the build stage
COPY --from=builder /app/dist ./dist

# Copy package.json and package-lock.json to install production dependencies
COPY --from=builder /app/package*.json ./

# Install only production dependencies
RUN npm install --production

# Expose the application port
EXPOSE 3000

# Start the application
CMD ["node", "dist/index.js"]

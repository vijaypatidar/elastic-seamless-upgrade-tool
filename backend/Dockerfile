# Start with a Node.js base image
FROM node:18-bullseye AS builder

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN yarn

# Copy the rest of the application code
COPY . .

# Compile TypeScript code into JavaScript
RUN yarn build

# Stage 2: Production Stage
FROM node:18-bullseye

# Install dependencies and Ansible
RUN apt-get update && \
    apt-get install -y ansible && \
    apt-get clean \
    && apt-get install -y openssh-client

# Verify installations
RUN node --version && yarn --version && ansible --version

# Set the working directory in the container
WORKDIR /app

# Copy only the compiled files from the build stage
COPY --from=builder /app/dist ./dist

# Copy package.json and package-lock.json to install production dependencies
COPY --from=builder /app/package*.json ./


#Copy ansible files
COPY --from=builder /app/ansible/ ./ansible/

# Install only production dependencies
RUN yarn install --production

# Expose the application port
EXPOSE 3000

# Start the application
CMD ["node", "dist/index.js"]

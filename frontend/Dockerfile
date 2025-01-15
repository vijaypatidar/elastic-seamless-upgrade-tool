# Stage 1: Build Stage
FROM node:18.20.4 AS builder

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Compile TypeScript code into JavaScript
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine

# Remove default Nginx configuration file
RUN rm /etc/nginx/conf.d/default.conf

# Copy your custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d

# Copy the build output from the first stage to Nginx's web root
COPY --from=builder /usr/src/app/dist /usr/share/nginx/html

# Expose the port Nginx will run on
EXPOSE 3001

# Start Nginx server
CMD ["nginx", "-g", "daemon off;"]
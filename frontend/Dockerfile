FROM node:22-alpine AS builder

WORKDIR /usr/src/app

COPY package*.json ./

RUN yarn 

COPY . .

RUN yarn build


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


# FROM node:20-alpine AS development-dependencies-env
# COPY . /app
# WORKDIR /app
# RUN npm ci

# FROM node:20-alpine AS production-dependencies-env
# COPY ./package.json package-lock.json /app/
# WORKDIR /app
# RUN npm ci --omit=dev

# FROM node:20-alpine AS build-env
# COPY . /app/
# COPY --from=development-dependencies-env /app/node_modules /app/node_modules
# WORKDIR /app
# RUN npm run build

# FROM node:20-alpine
# COPY ./package.json package-lock.json /app/
# COPY --from=production-dependencies-env /app/node_modules /app/node_modules
# COPY --from=build-env /app/build /app/build
# WORKDIR /app
# CMD ["npm", "run", "start"]

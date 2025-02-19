# Welcome to Hyperflex!

A modern, seemless application to upgrade cluster and kibana.

## Features

- Cluster snapshot status
- Elastic deprecation logs
- Kibana deprecation logs
- Elastic node upgrade
- Kibana node upgrade
- TailwindCSS for styling

## Getting Started

### Installation

Install the dependencies:

```bash
yarn
```

### Development

Start the development server with HMR:

```bash
yarn dev
```

Your application will be available at `http://localhost:5173`.

## Building for Production

Create a production build:

```bash
yarn build
```

## Deployment

### Docker Deployment

This template includes three Dockerfiles optimized for different package managers:

- `Dockerfile` - for yarn

To build and run using Docker:

```bash
# For yarn
docker build -t hyperflex .

# Run the container
docker run -p 80:80 hyperflex
```

To build and run using docker compose:

```bash
docker-compose up --build
```

Your application will be available at `http://localhost`, if using docker.

The containerized application can be deployed to any platform that supports Docker, including:

- AWS ECS
- Google Cloud Run
- Azure Container Apps
- Digital Ocean App Platform
- Fly.io
- Railway

### DIY Deployment

If you're familiar with deploying Node applications, the built-in app server is production-ready.

Make sure to deploy the output of `yarn build`

```
├── package.json
├── yarn.lock
├── build/
│   ├── client/    # Static assets
│   └── server/    # Server-side code
```

## Styling

This template comes with [Tailwind CSS](https://tailwindcss.com/) already configured for a simple default starting experience. You can use whatever CSS framework you prefer.

---

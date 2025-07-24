# REST Endpoints in backend/ Express App

## Core Application Routes (from index.ts)

- `GET /health`

## Elastic Cluster Routes (base: /api/elastic/clusters)

- `POST /api/elastic/clusters/:clusterId/upgrade-job`
- `GET /api/elastic/clusters/:clusterId/health`
- `GET /api/elastic/clusters/:clusterId/nodes`
- `GET /api/elastic/clusters/:clusterId/kibana-nodes`
- `GET /api/elastic/clusters/:clusterId/deprecations/elastic-search`
- `GET /api/elastic/clusters/:clusterId/valid-snapshots`
- `POST /api/elastic/clusters`
- `GET /api/elastic/clusters/nodes/:nodeId`
- `POST /api/elastic/clusters/:clusterId/nodes/upgrade`
- `POST /api/elastic/clusters/:clusterId/upgrade-all`
- `POST /api/elastic/clusters/:clusterId/nodes/upgrade-kibana`
- `GET /api/elastic/clusters/:clusterId/info`
- `POST /api/elastic/clusters/certificates/upload`
- `GET /api/elastic/clusters/:clusterId/upgrade_info`
- `GET /api/elastic/clusters/:clusterId/deprecations/kibana`
- `POST /api/elastic/clusters/:clusterId/prechecks`
- `GET /api/elastic/clusters/:clusterId/prechecks`
- `GET /api/elastic/clusters/:clusterId/prechecks/report`
- `GET /api/elastic/clusters/verify`

## Settings Routes (base: /api/settings)

- `POST /api/settings`
- `GET /api/settings`

## Webhook Routes (base: /webhook)

- `POST /webhook/clusters/:clusterId/update-status`

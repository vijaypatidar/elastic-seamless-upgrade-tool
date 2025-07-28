# REST Endpoints in backend/ Express App

## Core Application Routes (from index.ts)

- `GET /health` ❌

## Elastic Cluster Routes (base: /api/elastic/clusters)

- `POST /api/elastic/clusters/:clusterId/upgrade-job` ✅ (endpoint is bit diff)
- `GET /api/elastic/clusters/:clusterId/health` ❌
- `GET /api/elastic/clusters/:clusterId/nodes`  ✅
- `GET /api/elastic/clusters/:clusterId/kibana-nodes` ✅ (merged with nodes endpoint)
- `GET /api/elastic/clusters/:clusterId/valid-snapshots` ❌ (not needed as of now)
- `POST /api/elastic/clusters` ✅
- `GET /api/elastic/clusters/nodes/:nodeId` ❌ (not needed as of now)
- `POST /api/elastic/clusters/certificates/upload` ✅ (work diff now)
- `POST /api/elastic/clusters/:clusterId/prechecks`  ✅
- `GET /api/elastic/clusters/:clusterId/prechecks`  ✅
- `POST /api/elastic/clusters/:clusterId/nodes/upgrade` ✅ (work diff now)
- `POST /api/elastic/clusters/:clusterId/nodes/upgrade-kibana` ✅ (work diff now)
- `POST /api/elastic/clusters/:clusterId/upgrade-all` ✅ (work diff now)
- `GET /api/elastic/clusters/verify` ✅ (use /overview)
- `GET /api/elastic/clusters/:clusterId/info` ✅ (use /overview)
  - This also adds the cluster in monitoring service for pulling data on backend
- `GET /api/elastic/clusters/:clusterId/upgrade_info`
- `GET /api/elastic/clusters/:clusterId/prechecks/report`  ✅ (no change)
- `GET /api/elastic/clusters/:clusterId/deprecations/kibana` ✅ (no change)
- `GET /api/elastic/clusters/:clusterId/deprecations/elastic-search` ✅ (no change)

## Settings Routes (base: /api/settings)

- `POST /api/settings` ✅
- `GET /api/settings` ✅

## Webhook Routes (base: /webhook)

- `POST /webhook/clusters/:clusterId/update-status` ❌ (Not needed any more)

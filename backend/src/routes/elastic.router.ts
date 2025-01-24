import { Router } from 'express';
import {
  addOrUpdateClusterDetail,
  getClusterDetails,
  getElasticDeprecationInfo,
  getNodesInfo,
  healthCheck,
  getLogsStream,
  getKibanaDeprecationsInfo,
  getValidSnapshots,
  uploadCertificates,
  getUpgradeDetails,
} from '../controllers/elastic-controller';

const router = Router();

import multer from 'multer';

const upload = multer({ dest: 'uploads/' });

router.get('/:clusterId/health', healthCheck);

router.get('/:clusterId/nodes', getNodesInfo);

router.get(
  '/:clusterId/deprecations/elastic-search',
  getElasticDeprecationInfo,
);

router.get('/:clusterId/valid-snapshots', getValidSnapshots);

router.post('', addOrUpdateClusterDetail);

router.get('/:clusterId/info', getClusterDetails);

router.get('/:clusterId/nodes/:nodeId/logs/stream', getLogsStream);

router.get('/:clusterId/depriciations/kibana', getDeprecations);

router.post('/certificates/upload', upload.array('files'), uploadCertificates);

router.get('/:clusterId/upgrade_info', getUpgradeDetails);

router.get('/:clusterId/deprecations/kibana', getKibanaDeprecationsInfo);

export default router;

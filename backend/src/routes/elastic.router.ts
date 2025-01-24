import { Router } from 'express';
import {
  addOrUpdateClusterDetail,
  getClusterDetails,
  getDeprecationInfo,
  getNodesInfo,
  healthCheck,
  getLogsStream,
  getDeprecations,
  getValidSnapshots,
  getUpgradeDetails,
} from '../controllers/elastic-controller';

const router = Router();

router.get('/health', healthCheck);

router.get('/:clusterId/nodes', getNodesInfo);

router.get('/:clusterId/deprecations', getDeprecationInfo);

router.get('/:clusterId/valid-snapshots', getValidSnapshots);

router.post('', addOrUpdateClusterDetail);

router.get('/:clusterId/info', getClusterDetails);

router.get('/:clusterId/nodes/:nodeId/logs/stream', getLogsStream);

router.get('/:clusterId/upgrade_info', getUpgradeDetails);

router.get('/:clusterId/depriciations/kibana', getDeprecations);
export default router;

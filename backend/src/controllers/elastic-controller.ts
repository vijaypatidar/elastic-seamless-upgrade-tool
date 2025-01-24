import { ElasticClusterBaseRequest } from '..';
import { ElasticClient } from '../clients/elastic.client';
import { Request, Response } from 'express';
import { ElasticNode } from '../interfaces';
import logger from '../logger/logger';
import {
  IClusterInfo,
  IElasticInfo,
  IKibanaInfo,
} from '../models/cluster-info.model';
import { createOrUpdateClusterInfo } from '../services/cluster-info.service';
import { addLogs, getLogs } from '../services/logs.service';
import { KibanaClient } from '../clients/kibana.client';
import {
  getAllElasticNodes,
  syncNodeData,
} from '../services/elastic-node.service.';

export const healthCheck = async (req: Request, res: Response) => {
  try {
    const clusterId = req.params.clusterId;
    const client = await ElasticClient.buildClient(clusterId);
    const health = await client.getClusterhealth();
    res.send(health);
  } catch (err: any) {
    logger.info(err);
    res.status(400).send({ message: err.message });
  }
};

export const getClusterDetails = async (req: Request, res: Response) => {
  try {
    const clusterId = req.params.clusterId;
    const client = await ElasticClient.buildClient(clusterId);
    const clusterDetails = await client.getClient().info();
    const healtDetails = await client.getClient().cluster.health();
    res.send({
      ...healtDetails,
      ...clusterDetails,
    });
  } catch (err: any) {
    logger.info(err);
    res.status(400).send({ message: err.message });
  }
};

export const addOrUpdateClusterDetail = async (req: Request, res: Response) => {
  try {
    const clusterId = 'cluster-id';
    const elastic: IElasticInfo = req.body.elastic;
    const kibana: IKibanaInfo = req.body.kibana;
    const clusterInfo: IClusterInfo = {
      elastic: elastic,
      kibana: kibana,
      clusterId: clusterId,
      certificateIds: req.body.certificateIds,
    };
    const result = await createOrUpdateClusterInfo(clusterInfo);
    res
      .send({
        message: result.isNew ? 'Cluster info saved' : 'Cluster info updated',
      })
      .status(201);
    await syncNodeData(clusterId);
  } catch (err: any) {
    logger.info(err);
    res.status(400).send({ message: err.message });
  }
};

export const getDepriciationInfo = async (req: Request, res: Response) => {
  try {
    const clusterId = req.params.clusterId;
    const client = await ElasticClient.buildClient('cluster-id');
    const depriciationInfo = await client.getClient().migration.deprecations();
    const upgradeInfo = await client
      .getClient()
      .migration.getFeatureUpgradeStatus();
    logger.info('upgrade Info', upgradeInfo);
    res.send(depriciationInfo).status(201);
  } catch (err: any) {
    logger.info(err);
    res.status(400).send({ message: err.message });
  }
};

export const getNodesInfo = async (req: Request, res: Response) => {
  try {
    const clusterId = req.params.clusterId;
    const elasticNodes = await getAllElasticNodes(clusterId);
    res.send(elasticNodes);
  } catch (error: any) {
    logger.error('Error fetching node details:', error);
    res.status(400).send({ message: error.message });
  }
};

export const performUpgrade = async (req: Request, res: Response) => {};
// export const getUpgradeDetails

export const getLogsStream = async (req: Request, res: Response) => {
  const { clusterId, nodeId } = req.params;
  res.setHeader('Content-Type', 'text/event-stream');
  res.setHeader('Cache-Control', 'no-cache');
  res.setHeader('Connection', 'keep-alive');

  let lastTimestamp: Date | undefined = undefined;

  const intervalId = setInterval(async () => {
    // Uncomment this code block to create fack stream logs in db
    // addLogs(
    //   clusterId,
    //   nodeId,
    //   new Date(),
    //   `data: ${JSON.stringify({
    //     timestamp: new Date(),
    //     message: 'Upgrade in progress',
    //   })}`,
    // );

    const logs = await getLogs(clusterId, nodeId, lastTimestamp);
    for (let log of logs) {
      res.write(`${log.message}\n`);
      lastTimestamp = log.timestamp;
    }
  }, 2000);

  // Cleanup when the client disconnects
  req.on('close', () => {
    logger.debug('Client disconnected');
    clearInterval(intervalId);
    res.end();
  });
};

export const getDeprecations = async (req: Request, res: Response) => {
  try {
    const clusterId = req.params.clusterId;
    const kibanaClient = await KibanaClient.buildClient(clusterId);
    const deprecations = await kibanaClient.getDeprecations();
    res.send(deprecations);
  } catch (error: any) {
    logger.error(error);
    res.status(400).send({ message: error.message });
  }
};

export const getValidSnapshots = async (req: Request, res: Response) => {
  try {
    const { clusterId } = req.params;
    const client = await ElasticClient.buildClient(clusterId);
    const snapshots = await client.getValidSnapshots();
    res.send(snapshots);
  } catch (error: any) {
    logger.error('Error fetching node details:', error);
    res.status(400).send({ message: error.message });
  }
};

export const uploadCertificates = async (req: Request, res: Response) => {
  try {
    const files = req.files as Express.Multer.File[];
    const fileIds = files.map((file: Express.Multer.File) => file.filename);
    res.status(200).json({ certificateIds: fileIds });
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Failed to upload files' });
  }
};

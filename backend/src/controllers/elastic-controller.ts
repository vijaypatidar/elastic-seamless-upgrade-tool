import { ElasticClusterBaseRequest } from '..';
import { ElasticClient } from '../clients/elastic.client';
import { Request, Response } from 'express';
import { ElasticNode } from '../interfaces';
import logger from '../logger/logger';
import { IClusterInfo } from '../models/cluster-info.model';
import { createOrUpdateClusterInfo } from '../services/cluster-info.service';
import { addLogs, getLogs } from '../services/logs.service';

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
    const body: ElasticClusterBaseRequest = req.body;
    const clusterInfo: IClusterInfo = {
      elastic: {
        url: body.url,
        username: body.username,
        password: body.password,
        bearer: body.bearer,
        apiKey: body.apiKey,
      },
      clusterId: clusterId,
    };
    const result = await createOrUpdateClusterInfo(clusterInfo);
    res.send({
      message: result.isNew ? 'Cluster info saved' : 'Cluster info updated',
    });
  } catch (err: any) {
    logger.info(err);
    res.status(400).send({ message: err.message });
  }
};

export const getDepriciationInfo = async (req: Request, res: Response) => {
  try {
    const clusterId = req.params.clusterId;
    const client = await ElasticClient.buildClient(clusterId);
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
    const client = await ElasticClient.buildClient(clusterId);
    const response: any = await client.getClient().nodes.info({
      filter_path:
        'nodes.*.name,nodes.*.roles,nodes.*.os.name,nodes.*.os.version,nodes.*.version,nodes.*.ip',
    });
    const masterNode: any = await client.getClient().cat.master({
      format: 'json',
    });
    console.log('masterNode', masterNode);
    const elasticNodes: ElasticNode[] | null = Object.entries(
      response.nodes,
    ).map(([key, value]: any) => ({
      id: key,
      ip: value.ip,
      name: value.name,
      version: value.version,
      roles: value.roles,
      os: value.os,
      isMaster: masterNode[0].id === key,
    }));
    //Edit this info according to need
    // logger.info('Node details:', response);
    //  createAnsibleInventory(elasticNodes, './HF-AWX-key.pem');
    //  executeAnsiblePlaybook('ansible_inventory.ini','8.6.1','ansible/main',"elastic","B6T5WucTp=sJfbbPLErj")
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

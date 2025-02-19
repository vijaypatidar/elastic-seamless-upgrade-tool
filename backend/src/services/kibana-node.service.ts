import axios from 'axios';
import KibanaNode, { IKibanaNode, IKibanaNodeDocument } from '../models/kibana-node.model';
import logger from '../logger/logger';
import { createAnsibleInventoryForKibana, runPlaybookWithLogging, runPlaybookWithLoggingForKibana } from '../controllers/ansible-controller';
import { getClusterInfoById } from './cluster-info.service';


export interface KibanaConfig {
    name: string;
    ip: string;
  }
  
const getKibanaNodeDetails = async (kibanaUrl: string, username: string, password: string): Promise<{ version: string, os: Record<string, any>, roles: string[] }> => {
  try {
    // Get Kibana status and details from the /api/status endpoint
    const response = await axios.get(`${kibanaUrl}/api/status`, {
      headers: {
        'kbn-xsrf': 'true',
      },
      auth: {
        username,
        password,
      },
    });

    const version = response.data.version.number;
    const os =   response?.data?.os?.platform ? {
        name: response.data.os.platform,
        version: response.data.os.platformRelease,
    }: {
        name: "Linux",
        version: "linux-6.8.0-1021-aws"
    }
    const roles = ['kibana']; 

    return { version, os, roles };
  } catch (error) {
    console.error('Error getting Kibana node details:', error);
    throw error;
  }
};

export const createKibanaNodes = async (kibanaConfigs: KibanaConfig[], username: string, password: string,clusterId: string): Promise<IKibanaNode[]> => {
  const kibanaNodes: IKibanaNode[] = [];

  for (const kibanaConfig of kibanaConfigs) {
    try {
      const { version, os, roles } = await getKibanaNodeDetails(`http://${kibanaConfig.ip}:5601`, username, password);

      
      const nodeId = `node-${kibanaConfig.ip}`; 
      const progress = 0;  
      const status: 'available' | 'upgrading' | 'upgraded' | 'failed' = 'available';  
      const kibanaNode: IKibanaNode = {
        nodeId,
        clusterId,
        name: kibanaConfig.name,
        version,
        ip: kibanaConfig.ip,
        roles,
        os,
        progress,
        status,
      };

      kibanaNodes.push(kibanaNode);
      await KibanaNode.findOneAndUpdate({ nodeId: kibanaNode.nodeId }, kibanaNode, {
        new: true,
        runValidators: true,
        upsert: true,
      });

    } catch (error) {
      console.error(`Error processing Kibana node ${kibanaConfig.ip}:`, error);
    }
  }

  return kibanaNodes;
};


export const getKibanaNodes = async(clusterId: string)=>{
    try{
        const kibanaNodes = await KibanaNode.find({ clusterId: clusterId });
        return kibanaNodes;
    }
    catch(error){
        throw new Error("Unable to fetch kibana nodes");
    }
}

export const updateKibanaNodeStatus = async (
    nodeId: string,
    newStatus: string,
  ): Promise<IKibanaNodeDocument | null> => {
    try {
      const updatedNode = await KibanaNode.findOneAndUpdate(
        { nodeId },
        { status: newStatus },
        { new: true, runValidators: true },
      );
      if (!updatedNode) {
        logger.debug(`Node with id ${nodeId} not found.`);
        return null;
      }
      return updatedNode;
    } catch (error: any) {
      console.error(`Error updating status for node ${nodeId}: ${error.message}`);
      throw error;
    }
  };

  export const updateKibanaNodeProgress = async (nodeId: string, progress: number) => {
    try {
      const updatedNode = await KibanaNode.findOneAndUpdate(
        { nodeId },
        { progress: progress },
        { new: true, runValidators: true },
      );
  
      if (!updatedNode) {
        logger.debug(`Node with id ${nodeId} not found.`);
        return null;
      }
      return updatedNode;
    } catch (error: any) {
      console.error(`Error updating status for node ${nodeId}: ${error.message}`);
      throw error;
    }
  };


  export const getKibanaNodeById = async (
    nodeId: string,
  ): Promise<IKibanaNode | null> => {
    const kibanaNode = await KibanaNode.findOne({ nodeId: nodeId });
    if (!kibanaNode) return null;
    return kibanaNode;
  };

  export const triggerNodeUpgradeKibana = async (nodeId: string,clusterId: string) => {
    try {
      const node = await getKibanaNodeById(nodeId);
      if (!node) {
        return false;
      }
      const clusterInfo = await getClusterInfoById(clusterId);
      const pathToKey = clusterInfo.pathToKey ? clusterInfo.pathToKey : ''; //Should be stored in clusterInfo
      await createAnsibleInventoryForKibana([node], pathToKey);
      if(!clusterInfo.targetVersion || !clusterInfo.elastic.username || !clusterInfo.elastic.password){
        return false;
      }
  
      runPlaybookWithLogging(
        'ansible/main.yml',
        'ansible_inventory.ini',
        {
          elk_version: clusterInfo.targetVersion,
          username: clusterInfo.elastic.username,
          password: clusterInfo.elastic.password
        },
        nodeId,
      );
      return new Promise((resolve, reject) => resolve(true));
    } catch (error) {
      logger.error(`Error performing upgrade for node with id ${nodeId}`);
      return false;
    }
  };

  export const triggerKibanaNodeUpgrade = async (nodeId: string,clusterId: string) => {
    try {
      const node = await getKibanaNodeById(nodeId);
      if (!node) {
        return false;
      }
      const clusterInfo = await getClusterInfoById(clusterId);
      const pathToKey = clusterInfo.pathToKey ? clusterInfo.pathToKey : ''; //Should be stored in clusterInfo
      await createAnsibleInventoryForKibana([node], pathToKey);
      if(!clusterInfo.targetVersion || !clusterInfo.elastic.username || !clusterInfo.elastic.password){
        return false;
      }
  
      runPlaybookWithLoggingForKibana(
        'ansible/main.yml',
        'ansible_inventory.ini',
        {
          elk_version: clusterInfo.targetVersion,
          username: clusterInfo.elastic.username,
          password: clusterInfo.elastic.password
        },
        nodeId,
      );
      return new Promise((resolve, reject) => resolve(true));
    } catch (error) {
      logger.error(`Error performing upgrade for node with id ${nodeId}`);
      return false;
    }
  };
  

  




import { Request, Response } from 'express';
import logger from '../logger/logger';
import {updateNode } from '../services/elastic-node.service.';
import { NodeStatus } from '../models/elastic-node.model';
import { updateKibanaNode } from '../services/kibana-node.service';

export enum ClusterType{
    KIBANA = "KIBANA",
    ELASTIC = "ELASTIC"
 }

export enum AnsibleRequestType {
    UPGRADE = "UPGRADE",
    PRECHECK = "PRECHECK",
}

export enum AnsibleTaskStatus{
    STARTED = "STARTED",
    SUCCESS = "SUCCESS",
    FAILED = "FAILED",
}

interface BaseAnsibleRequest {
    nodeName: string;
    logs?: string[];
}
export interface AnsibleRequestPrecheck extends BaseAnsibleRequest {
    type: AnsibleRequestType.PRECHECK;
    clusterType: ClusterType;
    precheckId: string;
    status: AnsibleTaskStatus;
} 
export interface AnsibleRequestUpgrade extends BaseAnsibleRequest {
    type: AnsibleRequestType.UPGRADE;
    status: NodeStatus;
    progress?: number;
    taskName: string;
}

export type AnsibleRequest = AnsibleRequestPrecheck | AnsibleRequestUpgrade;
export const handleAnsibleWebhook = async (req: Request, res: Response) => {
    try{
        const clusterId = req.params.clusterId;
        const {type,nodeName} : AnsibleRequest= req.body;
        if(type === AnsibleRequestType.UPGRADE){
            const {progress, taskName,clusterType} = req.body;
            // Handle upgrade request
            if(clusterType === ClusterType.ELASTIC){
               updateNode({name: nodeName},{
                progress: progress,
                status: status as NodeStatus || NodeStatus.UPGRADING
               })
            }
            else{
                updateKibanaNode({name: nodeName},{
                    progress: progress,
                    status: status as NodeStatus || NodeStatus.UPGRADING
                   })
            }
        }else{
            const {precheckId} = req.body;
            //fetch precheck data and update corresponding run
        }
        logger.info(`Received Ansible webhook for cluster ${clusterId}: ${nodeName} of type ${type}  ${JSON.stringify(req.body)}`);
        res.sendStatus(200);
        // Handle the webhook data here

        
    }catch (error) {
        logger.error(`Error handling Ansible request: ${error}`);
        res.sendStatus(400)
    }
}
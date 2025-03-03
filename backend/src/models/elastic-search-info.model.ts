
import mongoose , {Schema, Document} from "mongoose";


export enum clusterStatus{
    green = "green",
    yellow = "yellow",
    red = "red"
}

export interface IElasticSearchInfo {
  clusterId: string;
  clusterName: string;
  clusterUUID: string;
  status: clusterStatus;
  version: string;
  timedOut: Boolean;
  numberOfDataNodes: number;
  numberOfNodes: number;
  activePrimaryShards: number;
  activeShards: number;
  unassignedShards: number;
  initializingShards: number;
  relocatingShards: number;
  underUpgradation: boolean;
  lastSyncedAt: Date;
}

export interface IElasticSearchInfoDocument extends IElasticSearchInfo, Document {}


const ElasticSearchInfoSchema: Schema<IElasticSearchInfoDocument> = new Schema<IElasticSearchInfoDocument>({
    clusterId: { type: String, required: true, unique: true },
    clusterName: { type: String, required: true },
    clusterUUID: { type: String, required: true },
    status: { type: String, required: true },
    version: { type: String, required: true },
    timedOut: { type: Number, required: true },
    numberOfDataNodes: { type: Number, required: true },
    numberOfNodes: { type: Number, required: true },
    activePrimaryShards: { type: Number, required: true },
    activeShards: { type: Number, required: true },
    unassignedShards: { type: Number, required: true },
    initializingShards: { type: Number, required: true },
    relocatingShards: { type: Number, required: true },
    underUpgradation: { type: Boolean, required: true },
    lastSyncedAt: { type: Date, required: true },
}, { timestamps: true });


const ElasticSearchInfo= mongoose.model<IElasticSearchInfo>(
  'ElasticSearchInfo',
  ElasticSearchInfoSchema,
);

export default ElasticSearchInfo;

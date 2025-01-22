import mongoose, { Schema, Document } from 'mongoose';

export interface IElasticInfo {
  url: string;
  apiKey?: string;
  username?: string;
  password?: string;
  bearer?: string;
}

export interface IClusterInfo {
  clusterId: string;
  elastic: IElasticInfo;
}

export interface IClusterInfoDocument extends IClusterInfo, Document {}

const ElasticInfoSchema: Schema<IElasticInfo> = new Schema<IElasticInfo>({
  url: { type: String, required: true },
  apiKey: { type: String },
  username: { type: String },
  password: { type: String },
  bearer: { type: String },
});

const ClusterInfoSchema: Schema<IClusterInfoDocument> =
  new Schema<IClusterInfoDocument>(
    {
      clusterId: { type: String, required: true },
      elastic: { type: ElasticInfoSchema, required: true },
    },
    { timestamps: true },
  );

// Create the model
const ClusterInfo = mongoose.model<IClusterInfoDocument>(
  'ClusterInfo',
  ClusterInfoSchema,
);

export default ClusterInfo;

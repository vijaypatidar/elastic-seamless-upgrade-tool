import mongoose, { Schema, Document, Model } from 'mongoose';

interface IElasticInfo {
  url: string;
  apiKey?: string;
  username?: string;
  password?: string;
  bearer?: string;
}

export interface IClusterInfo extends Document {
  clusterId: string;
  elastic: IElasticInfo;
}

const ElasticInfoSchema: Schema<IElasticInfo> = new Schema<IElasticInfo>({
  url: { type: String, required: true },
  apiKey: { type: String },
  username: { type: String },
  password: { type: String },
  bearer: { type: String },
});

const ClusterInfoSchema: Schema<IClusterInfo> = new Schema<IClusterInfo>(
  {
    clusterId: { type: String, required: true },
    elastic: { type: ElasticInfoSchema, required: true },
  },
  { timestamps: true },
);

// Create the model
const ClusterInfo = mongoose.model<IClusterInfo>(
  'ClusterInfo',
  ClusterInfoSchema,
);

export default ClusterInfo;

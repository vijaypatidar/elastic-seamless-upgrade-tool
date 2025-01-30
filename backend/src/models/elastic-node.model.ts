import mongoose, { Schema, Document } from 'mongoose';

export interface IElasticNode {
  nodeId: string;
  clusterId: string;
  name: string;
  version: string;
  ip: string;
  roles: string[];
  os: Record<string, any>;
  isMaster: boolean;
  progress: Number;
  status: 'available' | 'upgrading' | 'completed' | 'failed';
}

export interface IElasticNodeDocument extends IElasticNode, Document {}

const ElasticNodeSchema: Schema<IElasticNodeDocument> =
  new Schema<IElasticNodeDocument>(
    {
      nodeId: { type: String, required: true, unique: true },
      clusterId: { type: String, required: true },
      name: { type: String, required: true },
      version: { type: String, required: true },
      ip: { type: String, required: true },
      roles: { type: [String], required: true },
      os: { type: Object, required: true },
      isMaster: { type: Boolean, required: true },
      progress: { type: Number, required: false },
      status: {
        type: String,
        enum: ['available', 'upgrading', 'upgraded', 'failed'],
        default: 'available',
      },
    },
    { timestamps: true },
  );

const ElasticNode = mongoose.model<IElasticNodeDocument>(
  'ElasticNode',
  ElasticNodeSchema,
);

export default ElasticNode;

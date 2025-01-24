export interface ElasticNode {
  id: string;
  name: string;
  version: string;
  ip: string;
  roles: Array<any>;
  os: Object;
  isMaster: Boolean;
}

export interface DeprecationCounts {
  warning: Number;
  critical: Number;
}
export interface DepricationSetting {
  type: string;
  issue: string;
  issueDetails: string | undefined;
  resolution: string | string[];
}

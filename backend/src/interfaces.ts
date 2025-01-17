
export interface ElasticNode {
    id: string;
    name: string;
    version: string;
    ip: string;
    roles: Array<any>;
    os: Object;
    isMaster: Boolean
}
import { ElasticClusterBaseRequest, ElasticClusterHealthRequest } from '..';
import { ElasticClient } from '../clients/elastic'
import { Request,Response } from 'express';
// import { DateTime } from 'luxon';


export const healthCheck = async (req: Request, res: Response) => {
  try {
    const body: ElasticClusterBaseRequest= req.body;
    const client = new ElasticClient(body);
    const health = await client.getClusterhealth();
    const h = await client.getClient(); 
    console.log(await h.cluster.health());
    res.send(health);
  } catch (err: any) {
    console.log(err);
    res.status(400).send({ message: err.message });
  }
}

export const getClusterDetails = async(req: Request,res: Response)=>{
    try{
        const body: ElasticClusterBaseRequest = req.body;
        const client = new ElasticClient(body);
        const clusterDetails = await client.getClient().info();
        const healtDetails = await client.getClient().cluster.health();

        res.send({
            ...healtDetails,
            ...clusterDetails
        });
    }
    catch(err: any){
        console.log(err);
        res.status(400).send({message: err.message});
    }
}
  // Luxon for handling date/time calculations

async function verifySnapshotForAllRepositories(req: Request,res: Response) {
  try {
    const body: ElasticClusterBaseRequest = req.body;
    const client = new ElasticClient(body);

    const repositoriesResponse = await client.getClient().snapshot.getRepository({});
    const repositories = Object.keys(repositoriesResponse.body);

    if (repositories.length === 0) {
      console.log('No repositories found.');
      return;
    }

    for (const repository of repositories) {
      console.log(`Checking snapshots for repository: ${repository}`);
      const snapshotResponse = await client.getClient().snapshot.get({
        repository,
        snapshot: '_all'  
      });
      console.log(snapshotResponse);
      const snapshots : any = snapshotResponse.snapshots;

      if (snapshots.length === 0) {
        console.log(`No snapshots found in repository ${repository}.`);
        continue;
      }

      const latestSnapshot = snapshots.sort((a: any, b: any) => {
        return new Date(b.start_time_in_millis).getTime() - new Date(a.start_time_in_millis).getTime();
      })[0];

    //   const snapshotTimestamp = latestSnapshot.start_time_in_millis;
    //   const snapshotDate = new Date(snapshotTimestamp);
    //   const currentDate =  new Date(Date.now())

    //   const hoursDifference = (currentDate: any - snapshotDate)

    //   if (hoursDifference <= 24) {
    //     console.log(`The latest snapshot in repository ${repository} was taken within the last 24 hours.`);
    //   } else {
    //     console.log(`The latest snapshot in repository ${repository} was NOT taken within the last 24 hours.`);
    //   }
    }
  } catch (error) {
    console.error('Error checking snapshot details:', error);
  }
}


export const getDepriciationInfo = async(req: Request, res: Response)=>{
    try {
        const body: ElasticClusterBaseRequest = req.body;
        const client = new ElasticClient(body);
        const depriciationInfo = await client.getClient().migration.deprecations();
        const upgradeInfo = await client.getClient().migration.getFeatureUpgradeStatus();
        console.log("upgrade Info",upgradeInfo);
        res.send(depriciationInfo).status(201);
    }
    catch(err: any){
        console.log(err);
        res.status(400).send({ message: err.message });
    }
}



export const getNodesInfo = async(req:Request, res: Response)=>{
    try {
        const body: ElasticClusterBaseRequest = req.body;
        const client = new ElasticClient(body); 
    
        
        const response: any = await client.getClient().nodes.info({
            filter_path: 'nodes.*.name,nodes.*.roles,nodes.*.os.name,nodes.*.os.version,nodes.*.version'
        })  
        //Edit this info according to need
        console.log('Node details:', response);
        res.send(response);
      } catch (error) {
        console.error('Error fetching node details:', error);
      }
}



export const performUpgrade = async(req: Request,res: Response)=>{
  
}
// export const getUpgradeDetails
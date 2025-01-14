import express, { Request, Response } from 'express';
import cors from 'cors';
import { ElasticClient } from './clients/elastic';
import { getClusterDetails, getDepriciationInfo, getNodesInfo, healthCheck } from './controllers/elasticController';
// import { generate } from './clients/geminiClient';

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

app.get('/health', (req: Request, res: Response) => {
  res.send({
    message: 'Server is healthy! 🚀',
  });
  // generate("Explain how ai works");
});

export interface ElasticClusterBaseRequest {
  url: string;
  apiKey?: string;
  username?: string;
  password?: string;
  bearer?: string;
  ssl: object;
}
export interface ElasticClusterHealthRequest
  extends ElasticClusterBaseRequest {}



app.post('/api/elastic/health', healthCheck);
app.post('/api/elastic/cluster',getClusterDetails)
app.post('/api/elastic/nodes',getNodesInfo);
app.post('/api/elastic/depriciationInfo',getDepriciationInfo);


app.listen(PORT, () => {
  console.log(`Server is running at http://localhost:${PORT}`);
});

import express, { Request, Response } from 'express';
import cors from 'cors';
import { ElasticClient } from './clients/elastic';

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

app.get('/health', (req: Request, res: Response) => {
  res.send({
    message: 'Server is healthy! 🚀',
  });
});

export interface ElasticClusterBaseRequest {
  url: string;
  apiKey?: string;
  username?: string;
  password?: string;
  bearer?: string;
}
export interface ElasticClusterHealthRequest
  extends ElasticClusterBaseRequest {}

app.post('/api/elatic/health', async (req: Request, res: Response) => {
  try {
    const body: ElasticClusterHealthRequest = req.body;
    const client = new ElasticClient(body);
    const health = await client.getClusterhealth();
    res.send(health);
  } catch (err: any) {
    console.log(err);
    res.status(400).send({ message: err.message });
  }
});

app.listen(PORT, () => {
  console.log(`Server is running at http://localhost:${PORT}`);
});

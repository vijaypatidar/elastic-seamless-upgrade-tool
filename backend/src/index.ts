import express, { Request, Response } from 'express';
import cors from 'cors';
import { ElacticClient } from './clients/elastic';

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

app.get('/health', (req: Request, res: Response) => {
  res.send({
    message: 'Server is healthy! 🚀',
  });
});

export interface ElacticClusterBaseRequest {
  url: string;
  apiKey?: string;
  username?: string;
  password?: string;
  bearer?: string;
}
export interface ElacticClusterHealthRequest
  extends ElacticClusterBaseRequest {}

app.post('/api/elatic/health', async (req: Request, res: Response) => {
  try {
    const body: ElacticClusterHealthRequest = req.body;
    const client = new ElacticClient(body);
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

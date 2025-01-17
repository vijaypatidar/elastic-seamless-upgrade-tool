import express, { Request, Response } from 'express';
import cors from 'cors';
import elasticRouter from './routes/elasticRouter';
import swaggerJSDoc from 'swagger-jsdoc';
import swaggerUi from 'swagger-ui-express';

import swaggerOptions from './swagger-config';
import logger from './logger/logger';

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

const swaggerDocs = swaggerJSDoc(swaggerOptions);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocs));

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
  ssl: object;
}
export interface ElasticClusterHealthRequest
  extends ElasticClusterBaseRequest {}

//routes
app.use('/api/elastic', elasticRouter);

app.listen(PORT, () => {
  logger.info(`Server is running at http://localhost:${PORT}`);
  logger.info(`Swagger docs available at http://localhost:${PORT}/api-docs`);
});

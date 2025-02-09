import { Readable } from 'stream';

export interface ExecResult {
  code: number;
  stdout: Readable;
  stderr: Readable;
  error?: string;
}

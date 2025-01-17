declare module "node-ansible" {
    export class AdHoc {
      constructor();
      module(name: string): this;
      hosts(hosts: string): this;
      args(args: string): this;
      exec(): Promise<ExecResult>;
    }
  
    export class Playbook {
      constructor();
      playbook(playbook: string): this;
      inventory(inventory: string): this;
      variables(vars: Record<string, any>): this;
      verbose(level: string): this; // e.g., 'v', 'vv', 'vvv', etc.
      exec(): Promise<ExecResult>;
    }
  
    export interface ExecResult {
      code: number; // Exit code of the command
      output: string; // Standard output
      stderr: string; // Standard error
    }
  }
  
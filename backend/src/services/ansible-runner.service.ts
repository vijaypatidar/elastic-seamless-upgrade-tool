import { randomUUID } from "crypto";
import { spawn } from "child_process";
import logger from "../logger/logger";
const ANSIBLE_PLAYBOOKS_PATH = process.env.ANSIBLE_PLAYBOOKS_PATH;

if (ANSIBLE_PLAYBOOKS_PATH === undefined || ANSIBLE_PLAYBOOKS_PATH === "") {
	throw new Error("ANSIBLE_PLAYBOOKS_PATH is not defined");
}

class AnsibleRunnerService {
	constructor() {}

	runPlaybook({
		playbookPath,
		inventoryPath,
		variables,
		runId,
	}: {
		playbookPath: string;
		inventoryPath: string;
		variables: Record<string, any>;
		runId?: string;
	}): { runId: string; timestamp: Date } {
		runId = runId || randomUUID();
		const timestamp = new Date();
		const extraVars = [...Object.entries(variables), ...Object.entries({ runId: runId })]
			.map(([key, value]) => `${key}=${value}`)
			.join(" ");

		const args = ["-i", inventoryPath, playbookPath, "-e", extraVars];
		logger.info(
			`[runId: ${runId}] [playbook: ${playbookPath}] [cwd:${ANSIBLE_PLAYBOOKS_PATH}]  Running playbook with args: ${args.join(" ")}`
		);
		const process = spawn("ansible-playbook", args, {
			cwd: ANSIBLE_PLAYBOOKS_PATH,
		});

		process.stdout.on("data", (data) => {
			logger.info(`[runId: ${runId}] [playbook: ${playbookPath}] [stdout]: ${data}`);
		});

		process.stderr.on("data", (data) => {
			logger.error(`[runId: ${runId}] [playbook: ${playbookPath}] [stderr]: ${data}`);
		});

		process.on("close", (code) => {
			logger.info(`[runId: ${runId}] [playbook: ${playbookPath}] Process exited with code: ${code}`);
		});

		process.on("error", (err) => {
			logger.error(`[runId: ${runId}] [playbook: ${playbookPath}] Failed to start playbook: ${err.message}`);
		});

		return {
			runId: runId,
			timestamp,
		};
	}
}

export const ansibleRunnerService = new AnsibleRunnerService();

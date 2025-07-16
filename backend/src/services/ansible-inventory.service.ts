import fs from "fs";
import { ClusterNodeType, IClusterNode } from "../models/cluster-node.model";
import logger from "../logger/logger";
import { randomUUID } from "crypto";
const ANSIBLE_PLAYBOOKS_PATH = process.env.ANSIBLE_PLAYBOOKS_PATH;

class AnsibleInventoryService {
	constructor() {}

	public createInventory = async (
		nodes: IClusterNode[],
		{ pathToKey, sshUser }: { pathToKey: string; sshUser: string }
	): Promise<string> => {
		try {
			const roleGroups: Record<"elasticsearch_master" | "elasticsearch_data" | "kibana", string[]> = {
				elasticsearch_data: [],
				elasticsearch_master: [],
				kibana: [],
			};
			const iniName = `${nodes[0].clusterId}-${randomUUID()}.ini`;
			const iniPath = `${ANSIBLE_PLAYBOOKS_PATH}/ini/${iniName}`;
			for (const node of nodes) {
				if (node.type == ClusterNodeType.ELASTIC) {
					if (node.isMaster === true) {
						roleGroups.elasticsearch_master.push(`${node.name} ansible_host=${node.ip}`);
						continue;
					}
					if (node.roles.includes("data")) {
						roleGroups.elasticsearch_data.push(`${node.name} ansible_host=${node.ip}`);
					}
				} else {
					roleGroups.kibana.push(`${node.name} ansible_host=${node.ip}`);
				}
			}

			const inventoryParts: string[] = [];

			Object.entries(roleGroups).forEach(([group, hosts]) => {
				if (hosts.length > 0) {
					inventoryParts.push(`[${group}]\n${hosts.join("\n")}`);
				}
			});
			inventoryParts.push(
				`[all:vars]\nansible_ssh_user=${sshUser}\nansible_ssh_private_key_file=${pathToKey}\nansible_ssh_common_args='-o StrictHostKeyChecking=no'`
			);
			const inventoryContent = inventoryParts.join("\n\n");
			await fs.promises.writeFile(iniPath, inventoryContent, "utf8");
			return iniPath;
		} catch (error) {
			logger.error("Error creating Ansible inventory:", error);
			throw new Error(`Error creating Ansible inventory: ${(error as Error).message}`);
		}
	};

	public createInventoryForNode = async ({
		pathToKey,
		node,
		sshUser,
	}: {
		node: IClusterNode;
		pathToKey: string;
		sshUser: string;
	}) => {
		return await this.createInventory([node], {
			pathToKey: pathToKey,
			sshUser: sshUser,
		});
	};
}

export const ansibleInventoryService = new AnsibleInventoryService();

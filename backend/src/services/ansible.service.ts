import fs from "fs";

export const extractESNodeNames = (): string[] => {
	try {
		const inventoryContent = fs.readFileSync("ansible_inventory.ini", "utf-8");
		const lines = inventoryContent.split("\n");
		const nodeNames: string[] = [];
		let inElasticsearchSection = false;

		for (const line of lines) {
			if (line.startsWith("[elasticsearch_master]") || line.startsWith("[elasticsearch_data]")) {
				inElasticsearchSection = true;
				continue;
			}
			if (line.startsWith("[")) {
				inElasticsearchSection = false;
			}

			if (inElasticsearchSection) {
				const match = line.match(/^([a-zA-Z0-9_-]+)\s+ansible_host=/);
				if (match) {
					nodeNames.push(match[1]);
				}
			}
		}

		return nodeNames;
	} catch (error) {
		console.error("Error reading inventory file:", error);
		return [];
	}
};

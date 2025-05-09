import { getPrechecksGroupedByNode } from "./precheck-runs.service";

export const generatePrecheckReportMdContent = async (clusterId: string): Promise<string> => {
	const prechecksGroupedByNode = await getPrechecksGroupedByNode(clusterId);
	return `
  # Precheck Report 
  `;
};

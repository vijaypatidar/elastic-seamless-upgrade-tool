import { loadYaml } from "../utils/yaml-utils";

export interface PlaybookConfig {
	id: string;

	name: string;
	playbookPath: string;
}
export const PRECHECK_CONFIG: PlaybookConfig[] = loadYaml("prechecks.yaml");

export const getPrecheckById = (precheckId: string): PlaybookConfig | undefined => {
	return PRECHECK_CONFIG.find((precheck) => precheck.id === precheckId);
};

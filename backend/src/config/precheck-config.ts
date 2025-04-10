import { loadYaml, saveYaml } from "../utils/yaml-utils";

export interface PlaybookConfig {
	id: string;
	name: string;
	playbookPath: string;
}

export interface PlaybookConfigGroup {
	combined: PlaybookConfig;
	individuals: PlaybookConfig[];
}

const PRECHECK_CONFIG: { elastic: PlaybookConfigGroup; kibana: PlaybookConfigGroup } = loadYaml("prechecks.yaml");

export const getPrecheckById = (precheckId: string): PlaybookConfig | undefined => {
	const allPrechecks = [...PRECHECK_CONFIG.elastic.individuals, ...PRECHECK_CONFIG.kibana.individuals];
	return allPrechecks.find((precheck) => precheck.id === precheckId);
};
export const ELASTIC_PRECHECK_CONFIG = PRECHECK_CONFIG.elastic;
export const KIBANA_PRECHECK_CONFIG = PRECHECK_CONFIG.kibana;

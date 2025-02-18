import { createSlice } from "@reduxjs/toolkit"
import StorageManager from "~/constants/StorageManager"
import LocalStorageHandler from "~/lib/LocalHanlder"

const initialState = {
	clusterAdded: LocalStorageHandler.getItem(StorageManager.CLUSTER_ADDED) ?? false,
	deprecationChangesAllowed: LocalStorageHandler.getItem(StorageManager.DEPRECATION_PAGE_ALLOWED) ?? false,
	elasticNodeUpgradeAllowed: LocalStorageHandler.getItem(StorageManager.ELASTIC_NODE_UPGRADE_ALLOWED) ?? false,
	kibanaNodeUpgradeAllowed: LocalStorageHandler.getItem(StorageManager.KIBANA_NODE_UPGRADE_ALLOWED) ?? false,
	upgradeAssistAllowed: LocalStorageHandler.getItem(StorageManager.UPGRADE_ASSIST_ALLOWED) ?? false,
}

const safeRoutesSlice = createSlice({
	name: "safeRoutes",
	initialState,
	reducers: {
		setClusterAdded(state, action) {
			LocalStorageHandler.setItem(StorageManager.CLUSTER_ADDED, action.payload)
			return { ...state, clusterAdded: action.payload }
		},
		setDeprecationChangesAllowed(state, action) {
			LocalStorageHandler.setItem(StorageManager.DEPRECATION_PAGE_ALLOWED, action.payload)
			return { ...state, deprecationChangesAllowed: action.payload }
		},
		setElasticNodeUpgradeAllowed(state, action) {
			LocalStorageHandler.setItem(StorageManager.ELASTIC_NODE_UPGRADE_ALLOWED, action.payload)
			return { ...state, elasticNodeUpgradeAllowed: action.payload }
		},
		setKibanaNodeUpgradeAllowed(state, action) {
			LocalStorageHandler.setItem(StorageManager.KIBANA_NODE_UPGRADE_ALLOWED, action.payload)
			return { ...state, kibanaNodeUpgradeAllowed: action.payload }
		},
		setUpgradeAssistAllowed(state, action) {
			LocalStorageHandler.setItem(StorageManager.UPGRADE_ASSIST_ALLOWED, action.payload)
			return { ...state, upgradeAssistAllowed: action.payload }
		},
		resetForEditCluster(state) {
			LocalStorageHandler.setItem(StorageManager.DEPRECATION_PAGE_ALLOWED, false)
			LocalStorageHandler.setItem(StorageManager.ELASTIC_NODE_UPGRADE_ALLOWED, false)
			LocalStorageHandler.setItem(StorageManager.KIBANA_NODE_UPGRADE_ALLOWED, false)
			LocalStorageHandler.setItem(StorageManager.UPGRADE_ASSIST_ALLOWED, false)
			return {
				...state,
				deprecationChangesAllowed: false,
				elasticNodeUpgradeAllowed: false,
				kibanaNodeUpgradeAllowed: false,
				upgradeAssistAllowed: false,
			}
		},
		resetSafeRoutes(state) {
			LocalStorageHandler.setItem(StorageManager.CLUSTER_ADDED, false)
			LocalStorageHandler.setItem(StorageManager.DEPRECATION_PAGE_ALLOWED, false)
			LocalStorageHandler.setItem(StorageManager.ELASTIC_NODE_UPGRADE_ALLOWED, false)
			LocalStorageHandler.setItem(StorageManager.KIBANA_NODE_UPGRADE_ALLOWED, false)
			LocalStorageHandler.setItem(StorageManager.UPGRADE_ASSIST_ALLOWED, false)
			return {
				...state,
				clusterAdded: false,
				deprecationChangesAllowed: false,
				elasticNodeUpgradeAllowed: false,
				kibanaNodeUpgradeAllowed: false,
				upgradeAssistAllowed: false,
			}
		},
	},
})

export const {
	setClusterAdded,
	setDeprecationChangesAllowed,
	setElasticNodeUpgradeAllowed,
	setKibanaNodeUpgradeAllowed,
	setUpgradeAssistAllowed,
	resetForEditCluster,
	resetSafeRoutes,
} = safeRoutesSlice.actions
export default safeRoutesSlice.reducer

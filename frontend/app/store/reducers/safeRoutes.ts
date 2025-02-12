import { createSlice } from "@reduxjs/toolkit"

const initialState = {
	clusterAdded: false,
	deprecationChangesAllowed: false,
	elasticNodeUpgradeAllowed: false,
	kibanaNodeUpgradeAllowed: false,
}

const safeRoutesSlice = createSlice({
	name: "safeRoutes",
	initialState,
	reducers: {
		setClusterAdded(state, action) {
			return { ...state, clusterAdded: action.payload }
		},
		setDeprecationChangesAllowed(state, action) {
			return { ...state, deprecationChangesAllowed: action.payload }
		},
		setElasticNodeUpgradeAllowed(state, action) {
			return { ...state, elasticNodeUpgradeAllowed: action.payload }
		},
		setKibanaNodeUpgradeAllowed(state, action) {
			return { ...state, kibanaNodeUpgradeAllowed: action.payload }
		},
	},
})

export const {
	setClusterAdded,
	setDeprecationChangesAllowed,
	setElasticNodeUpgradeAllowed,
	setKibanaNodeUpgradeAllowed,
} = safeRoutesSlice.actions
export default safeRoutesSlice.reducer

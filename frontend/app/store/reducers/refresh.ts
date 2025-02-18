import { createSlice } from "@reduxjs/toolkit"
import StorageManager from "~/constants/StorageManager"
import LocalStorageHandler from "~/lib/LocalHanlder"

const initialState = {
	refresh: false,
}

const refreshSlice = createSlice({
	name: "refresh",
	initialState,
	reducers: {
		refresh(state) {
			return { ...state, refresh: !state.refresh }
		},
	},
})

export const { refresh } = refreshSlice.actions
export default refreshSlice.reducer

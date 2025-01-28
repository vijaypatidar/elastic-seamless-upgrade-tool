import { createSlice } from "@reduxjs/toolkit"

const initialState = {
	findASalon: false,
}

const commonSlice = createSlice({
	name: "common",
	initialState,
	reducers: {
		setFindASalon(state, action) {
			return { ...state, findASalon: action.payload }
		},
	},
})

export const { setFindASalon } = commonSlice.actions
export default commonSlice.reducer

import { combineReducers, configureStore } from "@reduxjs/toolkit"
import { thunk } from "redux-thunk"
import commonReducer from "./reducers/Common"

const rootReducer = combineReducers({
	common: commonReducer,
})

const store = configureStore({
	middleware: (getDefaultMiddleware) => getDefaultMiddleware({ serializableCheck: false }).concat(thunk),
	reducer: rootReducer,
	devTools: process.env.NODE_ENV !== "production",
})

export default store

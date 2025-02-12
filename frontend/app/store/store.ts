import { combineReducers, configureStore } from "@reduxjs/toolkit"
import { thunk } from "redux-thunk"
import safeRoutesReducer from "./reducers/safeRoutes"

const rootReducer = combineReducers({
	safeRoutes: safeRoutesReducer,
})

const store = configureStore({
	middleware: (getDefaultMiddleware) => getDefaultMiddleware({ serializableCheck: false }).concat(thunk),
	reducer: rootReducer,
	devTools: process.env.NODE_ENV !== "production",
})

export default store

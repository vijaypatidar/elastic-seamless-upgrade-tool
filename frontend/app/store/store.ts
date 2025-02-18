import { combineReducers, configureStore } from "@reduxjs/toolkit"
import { thunk } from "redux-thunk"
import safeRoutesReducer from "./reducers/safeRoutes"
import refreshReducer from "./reducers/refresh"

const rootReducer = combineReducers({
	safeRoutes: safeRoutesReducer,
	refresh: refreshReducer
})

const store = configureStore({
	middleware: (getDefaultMiddleware) => getDefaultMiddleware({ serializableCheck: false }).concat(thunk),
	reducer: rootReducer,
	devTools: process.env.NODE_ENV !== "production",
})

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export default store

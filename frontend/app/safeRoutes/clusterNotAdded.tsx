import { Navigate, Outlet } from "react-router"

function ClusterNotAddedSafeRoute() {
	const canAccess = true

	return canAccess ? <Outlet /> : <Navigate to="/" />
}

export default ClusterNotAddedSafeRoute

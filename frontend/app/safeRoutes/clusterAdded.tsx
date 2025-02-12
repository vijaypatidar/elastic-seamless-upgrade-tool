import { Navigate, Outlet } from "react-router"

function ClusterAddedSafeRoute() {
	const canAccess = true

	return canAccess ? <Outlet /> : <Navigate to="/cluster-overview" />
}

export default ClusterAddedSafeRoute

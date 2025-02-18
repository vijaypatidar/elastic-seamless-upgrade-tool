import { useSelector } from "react-redux"
import { Navigate, Outlet } from "react-router"

function ClusterNotAddedSafeRoute() {
	const canAccess = useSelector<any, boolean>((state) => state.safeRoutes.clusterAdded)

	return canAccess ? <Outlet /> : <Navigate to="/" />
}

export default ClusterNotAddedSafeRoute

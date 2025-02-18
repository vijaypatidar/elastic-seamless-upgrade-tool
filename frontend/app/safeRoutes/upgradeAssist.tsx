import { useSelector } from "react-redux"
import { Navigate, Outlet } from "react-router"

function UpgradeAssistSafeRoute() {
	const canAccess = useSelector<any, boolean>((state) => state.safeRoutes.clusterAdded)

	return canAccess ? <Navigate to="/cluster-overview" /> : <Outlet />
}

export default UpgradeAssistSafeRoute

import { useSelector } from "react-redux"
import { Navigate, Outlet } from "react-router"

function DeprecationSafeRoute() {
	const canResolveDeprecation = useSelector<any, boolean>((state) => state.safeRoutes.deprecationChangesAllowed)

	return canResolveDeprecation ? <Outlet /> : <Navigate to="/upgrade-assistant" />
}

export default DeprecationSafeRoute

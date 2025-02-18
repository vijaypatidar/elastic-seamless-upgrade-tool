import { useSelector } from "react-redux"
import { Navigate, Outlet } from "react-router"

function KibanaUpgradeSafeRoute() {
	const canUpgrade = useSelector<any, boolean>((state) => state.safeRoutes.kibanaNodeUpgradeAllowed)

	return canUpgrade ? <Outlet /> : <Navigate to="/upgrade-assistant" />
}

export default KibanaUpgradeSafeRoute

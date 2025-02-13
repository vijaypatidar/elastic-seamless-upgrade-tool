import { useSelector } from "react-redux"
import { Navigate, Outlet } from "react-router"

function ElasticUpgradeSafeRoute() {
	const canUpgrade = true
	// useSelector<any, boolean>((state) => state.safeRoutes.elasticNodeUpgradeAllowed)

	return canUpgrade ? <Outlet /> : <Navigate to="/upgrade-assistant" />
}

export default ElasticUpgradeSafeRoute

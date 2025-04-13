import { useEffect } from "react"
import { Outlet } from "react-router"
import { toast } from "sonner"
import axiosJSON from "./apis/http"
import StringManager from "./constants/StringManager"
import useSafeRouteStore from "./store/safeRoutes"

function MainApp() {
	const setClusterAdded = useSafeRouteStore((state: any) => state.setClusterAdded)
	const getCluster = async () => {
		await axiosJSON
			.get("/api/elastic/clusters/verify")
			.then((res) => setClusterAdded(res?.data?.clusterAvailable))
			.catch((err) => toast.error(err?.response?.data.err ?? StringManager.GENERIC_ERROR))
	}

	useEffect(() => {
		getCluster()
	}, [])

	return <Outlet />
}

export default MainApp

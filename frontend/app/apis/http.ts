import axios from "axios"
import URLManager from "../constants/URLManager"
import LocalStorageHandler from "~/lib/LocalHanlder"
import StorageManager from "~/constants/StorageManager"

let axiosJSON = axios.create({
	baseURL: URLManager.HTTP_BASE_URL,
	headers: {
		"Content-Type": "application/json",
	},
	timeout: 60000
})

let refreshPromise: any | null = null
const clearPromise = () => (refreshPromise = null)

const resetAuthState = () => {
	LocalStorageHandler.removeItem(StorageManager.SESSION_NAME)
}

const getRefreshToken = async (token: string) => {
	return await axiosJSON
		.post(`${URLManager.REFRESH_TOKEN_URL}?refreshTokenId=${token}`)
		.then((res) => res)
		.catch((error) => LocalStorageHandler.clear())
}

axiosJSON.interceptors.request.use(
	(config) => {
		const session = LocalStorageHandler.getItem(StorageManager.SESSION_NAME)
		config.headers.authorization = `Bearer ${session}`
		config.headers.Accept = "application/json"
		return config
	},
	(error) => {
		return Promise.reject(error)
	}
)

axiosJSON.interceptors.response.use(
	(res) => {
		return res
	},
	async (error) => {
		let originalRequest = error.config
		const session = LocalStorageHandler.getItem(StorageManager.SESSION_NAME)

		if (error.response.data.path === "/refresh") {
			resetAuthState()
		} else if (
			error.response.status === 403 &&
			!(error.response.data.path === "/login") &&
			error.response.data !== "User does not has permission to disable asked account."
		) {
			if (!refreshPromise) {
				refreshPromise = getRefreshToken(session as string).finally(clearPromise)
			}

			const res = await refreshPromise

			if (!Boolean(res)) {
				resetAuthState()
				return Promise.reject(error)
			}

			LocalStorageHandler.setItem(StorageManager.SESSION_NAME, res.data.session)

			originalRequest.headers.authorization = `Bearer ${res?.data?.accessToken || session}`

			return axiosJSON(originalRequest)
		} else if (error.response.status === 400) {
			if (error.response.data.path === "/") resetAuthState()
		} else if (error.response.status === 500 || error.response.status === 502) {
			// window.open(`/page500`, "_self")
		}

		return Promise.reject(error)
	}
)

export default axiosJSON

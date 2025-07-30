import { create } from "zustand"
import URLManager from "~/constants/URLManager"
import { WebSocketClient } from "./websocket-client"

type SocketStore = {
	socket: WebSocketClient | null
	connect: () => void
	disconnect: () => void
	isConnected: boolean
}

export const useSocketStore = create<SocketStore>((set) => ({
	socket: null,
	isConnected: false,

	connect: () => {
		const socket = new WebSocketClient(URLManager.SOCKET_BASE_URL)
		socket.connect()

		socket.on("connect", () => set({ isConnected: true }))
		socket.on("disconnect", () => set({ isConnected: false }))

		set({ socket })
	},

	disconnect: () => {
		set((state) => {
			state.socket?.disconnect()
			return { socket: null, isConnected: false }
		})
	},
}))

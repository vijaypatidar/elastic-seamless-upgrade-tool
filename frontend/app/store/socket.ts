// src/store/socketStore.ts
import { io, Socket } from "socket.io-client"
import { create } from "zustand"
import URLManager from "~/constants/URLManager"

type SocketStore = {
	socket: Socket | null
	connect: () => void
	disconnect: () => void
	isConnected: boolean
}

export const useSocketStore = create<SocketStore>((set) => ({
	socket: null,
	isConnected: false,

	connect: () => {
		const socket = io(URLManager.SOCKET_BASE_URL, {
			transports: ["websocket"],
			// withCredentials: true,
		})

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

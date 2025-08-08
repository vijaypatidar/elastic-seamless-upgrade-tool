import { useEffect } from "react"
import { useSocketStore } from "~/store/socket"
import { useDebouncedRefetch } from "./useDebouncedRefetch"

export const useRealtimeEventListener = (event: string, listener: (data?: any) => void, debounce = false) => {
	const { socket } = useSocketStore()
	const _debounceRefetch = useDebouncedRefetch(listener, 1000, 1500)
	useEffect(() => {
		if (!socket) return
		const _listener = debounce ? _debounceRefetch : listener
		socket.on(event, _listener)
		return () => {
			socket.off(event, _listener)
		}
	}, [socket])

	return {};
}

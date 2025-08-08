import { useEffect, useRef } from "react"

export const useDebouncedRefetch = (refetch: (...data: any) => void, delay = 1000, maxWait = 3000) => {
	const timeoutRef = useRef<NodeJS.Timeout | null>(null)
	const lastInvokeTimeRef = useRef<number>(Date.now())

	const debounced = (...data: any) => {
		const now = Date.now()

		// Clear previous debounce timer
		if (timeoutRef.current) {
			clearTimeout(timeoutRef.current)
		}

		// Check if maxWait has been reached
		if (now - lastInvokeTimeRef.current >= maxWait) {
			lastInvokeTimeRef.current = now
			refetch(...data)
		} else {
			// Schedule for debounce delay
			timeoutRef.current = setTimeout(() => {
				lastInvokeTimeRef.current = Date.now()
				refetch(...data)
			}, delay)
		}
	}

	// Cleanup on unmount
	useEffect(() => {
		return () => {
			if (timeoutRef.current) clearTimeout(timeoutRef.current)
		}
	}, [])

	return debounced
}

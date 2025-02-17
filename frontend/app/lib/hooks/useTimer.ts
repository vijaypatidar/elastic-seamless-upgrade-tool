import { useState, useEffect, useCallback, useRef } from "react"

interface TimerHook {
	formattedTime: string
	isExpired: boolean
	remainingTime: number
	reset: () => void
}

const useTimer = (startTimestamp: number | null, duration: number = 24 * 60 * 60 * 1000): TimerHook => {
	const [remainingTime, setRemainingTime] = useState<number>(0)
	const intervalRef = useRef<NodeJS.Timeout | null>(null)
	const startRef = useRef<number | null>(startTimestamp)

	const calculateRemaining = useCallback(() => {
		if (!startTimestamp) return 0
		const endTime = startTimestamp + duration
		return Math.max(0, endTime - Date.now())
	}, [startTimestamp, duration])

	const formatTime = useCallback((ms: number): string => {
		const totalSeconds = Math.floor(ms / 1000)
		const hours = Math.floor(totalSeconds / 3600)
		const minutes = Math.floor((totalSeconds % 3600) / 60)
		const seconds = Math.floor(totalSeconds % 60)
		return [hours, minutes, seconds].map((n) => String(n).padStart(2, "0")).join(":")
	}, [])

	const resetTimer = useCallback(() => {
		setRemainingTime(calculateRemaining())
	}, [calculateRemaining])

	useEffect(() => {
		// Clear existing interval if timestamp changes
		if (startRef.current !== startTimestamp) {
			startRef.current = startTimestamp
			resetTimer()
		}

		if (!startTimestamp) {
			setRemainingTime(0)
			return
		}

		intervalRef.current = setInterval(() => {
			setRemainingTime((prev) => {
				const newTime = calculateRemaining()
				if (newTime <= 0) clearInterval(intervalRef.current!)
				return newTime
			})
		}, 1000)

		return () => {
			if (intervalRef.current) clearInterval(intervalRef.current)
		}
	}, [startTimestamp, duration, calculateRemaining, resetTimer])

	return {
		remainingTime,
		formattedTime: formatTime(remainingTime),
		isExpired: remainingTime <= 0,
		reset: resetTimer,
	}
}

export default useTimer

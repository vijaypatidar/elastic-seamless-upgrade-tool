import { useState, useEffect } from "react"

interface CountdownTimer {
	remainingTime: number | null
	startTimer: (timestamp: number) => void
	resetTimer: () => void
}

const useCountdownTimer = (): CountdownTimer => {
	const [timestamp, setTimestamp] = useState<number | null>(null)
	const [remainingTime, setRemainingTime] = useState<number | null>(null)

	// Function to start/update the timer
	const startTimer = (newTimestamp: number) => {
		setTimestamp(newTimestamp)
	}

	// Function to reset the timer
	const resetTimer = () => {
		setTimestamp(null)
		setRemainingTime(null)
	}

	useEffect(() => {
		if (!timestamp) return

		const updateRemainingTime = () => {
			const now = Date.now()
			const targetTime = timestamp + 24 * 60 * 60 * 1000 // Add 24 hours
			const timeLeft = Math.max(targetTime - now, 0)
			setRemainingTime(timeLeft)
		}

		// Initial calculation
		updateRemainingTime()

		// Update every second
		const interval = setInterval(updateRemainingTime, 1000)

		return () => clearInterval(interval)
	}, [timestamp])

	return { remainingTime, startTimer, resetTimer }
}

export default useCountdownTimer

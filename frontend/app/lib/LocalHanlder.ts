class LocalStorageHandler {
	/**
	 * Sets an item in local storage.
	 * @param key - The key under which the value is stored.
	 * @param value - The value to store (can be string or object).
	 */
	static setItem(key: string, value: unknown): void {
		try {
			const serializedValue = typeof value === "string" ? value : JSON.stringify(value)
			localStorage.setItem(key, serializedValue)
		} catch (error) {
			console.error("Error saving to localStorage:", error)
		}
	}

	/**
	 * Gets an item from local storage.
	 * @param key - The key of the item to retrieve.
	 * @returns The value from local storage (parsed if it's JSON) or null if not found.
	 */
	static getItem<T>(key: string): T | null {
		try {
			const value = localStorage.getItem(key)
			if (value === null) return null

			// Try to parse JSON; if it fails, return as a string.
			try {
				return JSON.parse(value) as T
			} catch {
				return value as unknown as T
			}
		} catch (error) {
			console.error("Error reading from localStorage:", error)
			return null
		}
	}

	/**
	 * Removes an item from local storage.
	 * @param key - The key of the item to remove.
	 */
	static removeItem(key: string): void {
		try {
			localStorage.removeItem(key)
		} catch (error) {
			console.error("Error removing from localStorage:", error)
		}
	}

	/**
	 * Clears all items from local storage.
	 */
	static clear(): void {
		try {
			localStorage.clear()
		} catch (error) {
			console.error("Error clearing localStorage:", error)
		}
	}
}

export default LocalStorageHandler

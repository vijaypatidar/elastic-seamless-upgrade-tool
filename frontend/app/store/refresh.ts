import { create } from "zustand"
import { devtools } from "zustand/middleware"

const useRefreshStore = create(devtools((set) => ({
	refreshToggle: false,
	refresh: () => set((state: any) => ({ refreshToggle: !state.refreshToggle })),
})))

export default useRefreshStore

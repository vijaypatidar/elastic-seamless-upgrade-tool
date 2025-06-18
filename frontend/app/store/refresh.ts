import { create, createStore } from "zustand"
import { devtools, persist } from "zustand/middleware"

const useRefreshStore = create(devtools((set) => ({
	refreshToggle: false,
	refresh: () => set((state: any) => ({ refreshToggle: !state.refreshToggle })),
})))

export default useRefreshStore

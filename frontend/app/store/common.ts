import { create } from "zustand"
import { persist } from "zustand/middleware"
import { localStorageConfig, sessionStorageConfig } from "~/lib/Utils"

export const useLocalStore = create()(
	persist(
		(set) => ({
			clusterId: "",
			infraType: "",
			sessionName: "",
			setClusterId: (id: string) => set((state: any) => ({ clusterId: id })),
			setInfraType: (type: string) => set((state: any) => ({ infraType: type })),
			setSessionName: (name: string) => set((state: any) => ({ sessionName: name })),
			reset: () =>
				set((_: any) => ({
					clusterId: "",
					infraType: "",
					sessionName: "",
				})),
		}),
		{
			name: "local-store",
			storage: localStorageConfig,
		}
	)
)

export const useSessionStore = create()(
	persist(
		(set) => ({
			setupStep: 1,
			setSetupStep: (step: number) => set((state: any) => ({ setupStep: step })),
			reset: () =>
				set((_: any) => ({
					setupStep: 1,
				})),
		}),
		{ name: "session-store", storage: sessionStorageConfig }
	)
)

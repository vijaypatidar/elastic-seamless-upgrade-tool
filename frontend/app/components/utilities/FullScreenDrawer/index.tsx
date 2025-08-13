import { Drawer, DrawerBody, DrawerContent } from "@heroui/react"
import { type ReactNode } from "react"

interface FullScreenDrawerProps {
	isOpen: boolean
	onOpenChange: () => void
	children: ReactNode
}

export function FullScreenDrawer({ isOpen, onOpenChange, children }: FullScreenDrawerProps) {
	return (
		<Drawer
			isOpen={isOpen}
			size="full"
			onOpenChange={onOpenChange}
			placement="top"
			classNames={{ base: "bg-[#0A0A0A]" }}
			motionProps={{
				variants: {
					enter: {
						opacity: 1,
						y: 0,
						// @ts-ignore
						duration: 0.3,
					},
					exit: {
						y: 100,
						opacity: 0,
						// @ts-ignore
						duration: 0.3,
					},
				},
			}}
		>
			<DrawerContent>{() => <DrawerBody>{children}</DrawerBody>}</DrawerContent>
		</Drawer>
	)
}

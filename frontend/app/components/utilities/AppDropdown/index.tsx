import { type MouseEvent, type ReactNode, useState } from "react"
import { Box, Button, IconButton, Menu, MenuItem } from "@mui/material"

export interface DropdownItem {
	label: string
	icon?: ReactNode
	onClick?: () => void
	disabled?: boolean
}

interface AppDropdownProps {
	label: string | ReactNode
	items: DropdownItem[]
	buttonClassName?: string
	menuClassName?: string
	iconOnly?: boolean
}

export function AppDropdown({ label, items, buttonClassName, iconOnly }: AppDropdownProps) {
	const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null)
	const open = Boolean(anchorEl)

	const handleClick = (event: MouseEvent<HTMLButtonElement>) => {
		setAnchorEl(event.currentTarget)
	}

	const handleClose = () => {
		setAnchorEl(null)
	}
	return (
		<Box className="mx-1">
			<Box className="!bg-[#212022] border-1 !border-[#615D6A] !rounded-lg">
				{iconOnly ? (
					<IconButton onClick={handleClick} className={`!w-10 !h-10 !rounded-lg ${buttonClassName ?? ""}`}>
						{label}
					</IconButton>
				) : (
					<Button
						onClick={handleClick}
						className={`!normal-case !text-sm !px-4 !py-2!text-white  ${buttonClassName ?? ""}`}
					>
						{label}
					</Button>
				)}
			</Box>
			<Menu
				anchorEl={anchorEl}
				open={open}
				onClose={handleClose}
				transformOrigin={{ horizontal: "right", vertical: "top" }}
				anchorOrigin={{ horizontal: "right", vertical: "bottom" }}
				slotProps={{
					paper: {
						className: "!rounded-lg  !border-1 !border-[#292929] mt-1",
						style: { width: "154px" },
					},
					list: {
						className: "!bg-[#212022]",
					},
				}}
			>
				{items.map((item, idx) => (
					<MenuItem
						key={idx}
						onClick={() => {
							item.onClick?.()
							handleClose()
						}}
						disabled={item.disabled}
						className="flex gap-1 items-center !text-[#898484]"
					>
						{item.icon}
						{item.label}
					</MenuItem>
				))}
			</Menu>
		</Box>
	)
}

import { getKeyValue, Table, TableBody, TableCell, TableColumn, TableHeader, TableRow } from "@heroui/react"
import { Box, Typography } from "@mui/material"
import { Flash } from "iconsax-react"
import { useCallback, type Key } from "react"
import { OutlinedBorderButton } from "~/components/utilities/Buttons"

const rows = [
	{
		key: "1",
		node_name: "node 1",
		role: "Admin",
		os: "Active",
		version: "1.4",
	},
	{
		key: "2",
		node_name: "node 1",
		role: "Admin",
		os: "Active",
		version: "1.4",
	},
	{
		key: "3",
		node_name: "node 1",
		role: "Admin",
		os: "Active",
		version: "1.4",
	},
	{
		key: "4",
		node_name: "node 1",
		role: "Admin",
		os: "Active",
		version: "1.4",
	},
	{
		key: "5",
		node_name: "node 1",
		role: "Admin",
		os: "Active",
		version: "1.4",
	},
]

const columns: UpgradeColumnType = [
	{
		key: "node_name",
		label: "Node name",
		align: "start",
		width: 300,
	},
	{
		key: "role",
		label: "Role",
		align: "start",
		width: 150,
	},
	{
		key: "os",
		label: "OS",
		align: "start",
		width: 150,
	},
	{
		key: "version",
		label: "Version",
		align: "start",
		width: 150,
	},
	{
		key: "action",
		label: "Action",
		align: "end",
		width: 100,
	},
]

function UpgradeCluster({ clusterType }: UpgradeClusterType) {
	const renderCell = useCallback((row: UpgradeRowType, columnKey: Key) => {
		const cellValue = row[columnKey as keyof UpgradeRowType]

		switch (columnKey) {
			case "node_name":
				return row.node_name
			case "role":
				return row.role
			case "os":
				return row.os
			case "version":
				return row.version
			case "action":
				return (
					<Box className="flex justify-end">
						<OutlinedBorderButton icon={Flash} filledIcon={Flash}>
							Upgrade
						</OutlinedBorderButton>
					</Box>
				)
			default:
				return cellValue
		}
	}, [])
	
	return (
		<Box className="flex w-full p-px rounded-2xl" sx={{ background: "radial-gradient(#6E687C, #1D1D1D)" }}>
			<Box className="flex flex-col gap-4 w-full rounded-2xl bg-[#0d0d0d]" padding="16px 24px">
				<Box className="flex flex-row items-center gap-2 justify-between w-full">
					<Typography color="#FFF" fontSize="14px" fontWeight="600" lineHeight="22px">
						Node Details
					</Typography>
					<OutlinedBorderButton icon={Flash} filledIcon={Flash}>
						Upgrade all
					</OutlinedBorderButton>
				</Box>
				<Box className="flex">
					<Table
						removeWrapper
						layout="auto"
						isHeaderSticky
						classNames={{
							base: "max-h-[calc(var(--window-height)-212px)] h-[calc(var(--window-height)-212px)] overflow-scroll",
							table: "min-h-[400px] min-w-[600px]",
							th: "text-[#9D90BB] text-xs bg-[#161616] first:rounded-l-xl last:rounded-r-xl",
							td: "text-sm font-normal leading-normal border-b-[0.5px] border-solid border-[#1E1E1E]",
							tr: "[&>th]:h-[42px] [&>td]:h-[60px]",
						}}
					>
						<TableHeader columns={columns}>
							{(column) => (
								<TableColumn key={column.key} align={column.align} width={column.width}>
									{column.label}
								</TableColumn>
							)}
						</TableHeader>
						<TableBody items={rows}>
							{(item) => (
								<TableRow key={item.key}>
									{(columnKey) => <TableCell>{renderCell(item, columnKey)}</TableCell>}
								</TableRow>
							)}
						</TableBody>
					</Table>
				</Box>
			</Box>
		</Box>
	)
}

export default UpgradeCluster

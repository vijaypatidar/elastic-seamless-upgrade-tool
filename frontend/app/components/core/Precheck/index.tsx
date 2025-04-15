import { Box, Typography } from "@mui/material"
import { ExportCurve, Refresh } from "iconsax-react"
import React, { useState } from "react"
import { OutlinedBorderButton } from "~/components/utilities/Buttons"
import LogAccordion from "./widgets/LogAccordion"
import NodeListItem from "./widgets/NodeListItem"
import axiosJSON from "~/apis/http"
import { useLocalStore } from "~/store/common"
import { toast } from "sonner"
import StringManager from "~/constants/StringManager"
import { useQuery } from "@tanstack/react-query"

function Precheck() {
	const clusterId = useLocalStore((state: any) => state.clusterId)
	const [expanded, setExpanded] = useState<string[]>([])
	const [nodeSelected, setNodeSelected] = useState<{}>({})

	const handleChange = (panel: string) => (event: React.SyntheticEvent, newExpanded: boolean) => {
		if (expanded.includes(panel)) {
			setExpanded(expanded.filter((item: string) => item !== panel))
		} else {
			setExpanded([...expanded, panel])
		}
	}

	const getNodes = async () => {
		let response: any = []
		await axiosJSON
			.get(`/api/elastic/clusters/${clusterId}/prechecks`)
			.then((res) => (response = res))
			.catch((err) => {
				console.log("Err", err)
				toast.error(err?.response?.data.err ?? StringManager.GENERIC_ERROR)
			})
	}

	const { data, isLoading, refetch, isRefetching } = useQuery({
		queryKey: ["get-nodes-prechecks"],
		queryFn: getNodes,
		staleTime: 0,
	})

	return (
		<Box className="flex gap-4 h-[calc(var(--window-height)-129px)]">
			<Box className="flex py-4 flex-col gap-[6px]">
				<Typography color="#A9AAB6" fontSize="12px" fontWeight="500" lineHeight="normal" letterSpacing="0.12px">
					Nodes
				</Typography>
				<Box className="flex flex-col gap-[6px] overflow-scroll min-w-[282px]">
					{data?.map((item: any, index: number) => {
						return (
							<NodeListItem
								name={item["name"]}
								isSuccess={item["status"] !== "FAILED"}
								isSelected={item["ip"] === nodeSelected["ip"]}
								onClick={() => setNodeSelected(item)}
							/>
						)
					})}
				</Box>
			</Box>
			<Box className="flex w-full p-px rounded-2xl" sx={{ background: "radial-gradient(#6E687C, #1D1D1D)" }}>
				<Box className="flex flex-col  gap-4 w-full rounded-2xl bg-[#0d0d0d]" padding="16px 0px">
					<Box className="flex flex-row gap-2 justify-between items-center" padding="0px 24px">
						<Box>
							<Typography
								color="#FFF"
								fontSize="16px"
								fontWeight="600"
								lineHeight="normal"
								letterSpacing="0.16px"
							>
								Logs
							</Typography>
							<Typography
								color="#A6A6A6"
								fontSize="12px"
								fontWeight="400"
								lineHeight="normal"
								letterSpacing="0.24px"
							>
								Succeded last month in 1m 8s
							</Typography>
						</Box>
						<Box className="flex flex-row gap-[6px]">
							<OutlinedBorderButton>
								<Refresh color="currentColor" size="18px" /> Re-run
							</OutlinedBorderButton>
							<OutlinedBorderButton>
								<ExportCurve color="currentColor" size="18px" /> Export
							</OutlinedBorderButton>
						</Box>
					</Box>
					<Box className="flex flex-col gap-1 overflow-scroll" padding="0px 24px">
						{nodeSelected?.["prechecks"]?.map((item: any, idx: number) => {
							return (
								<LogAccordion
									title={item["name"]}
									isSuccess={item["status"] !== "FAILED"}
									logs={item["logs"]}
									expanded={expanded.includes(item["id"])}
									onChange={handleChange(item["id"])}
									duration={item["duration"]}
								/>
							)
						})}
					</Box>
				</Box>
			</Box>
		</Box>
	)
}

export default Precheck

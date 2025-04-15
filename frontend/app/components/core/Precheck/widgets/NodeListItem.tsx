import { Box, Typography } from "@mui/material";
import { TickCircle, Warning2 } from "iconsax-react";
import { cn } from "~/lib/Utils";

function NodeListItem({ isSuccess, name, isSelected, onClick=()=>{} }: { isSuccess: boolean; name: string; isSelected: boolean, onClick: () => void }) {
	return (
		<Box
			className={cn("rounded-md p-px min-w-[282px] cursor-pointer", {
				"bg-gradient-to-tl from-[#1D1D1D] via-[#8171A6] to-[#1D1D1D]": isSelected,
				"bg-transparent": !isSelected,
			})}
            onClick={onClick}
		>
			<Box
				className={cn("flex items-center gap-2 py-3 px-[14px]  rounded-md hover:bg-[#1D1D1D]", {
					"bg-[#1F1F1F]": isSelected,
				})}
			>
				{isSuccess ? (
					<TickCircle variant="Bold" color="#4CDB9D" size="20px" />
				) : (
					<Warning2 variant="Bold" color="#E75547" size="20px" />
				)}
				<Typography color="#C3C4CB" fontSize="13px" fontStyle="normal" fontWeight="500" lineHeight="20px">
					{name}
				</Typography>
			</Box>
		</Box>
	)
}


export default NodeListItem

import React, { useState } from "react"
import { Box, Fab } from "@mui/material"
import AiAssistantChat from "./"
import { Magicpen } from "iconsax-react"

interface AiAssistantChatProps {
	context: Context
}
const AiChatLauncher: React.FC<AiAssistantChatProps> = ({ context }) => {
	const [open, setOpen] = useState(false)

	return (
		<Box sx={{ position: "relative" }}>
			{/* Floating button */}
			<Fab
				onClick={() => setOpen((prev) => !prev)}
				size="small"
				sx={{
					position: "absolute",
					bottom: 8,
					right: 8,
					zIndex: 20,
					backgroundColor: "transparent",
				}}
			>
				<Box className="rounded-full p-3 bg-white/10">
					<Magicpen color="#BDA0FF" size="24px" />
				</Box>
			</Fab>

			{/* Chat panel */}
			{open && (
				<Box
					sx={{
						position: "absolute",
						bottom: 56, // above the button
						right: 0,
						width: 320,
						height: 400,
						zIndex: 30,
					}}
				>
					<AiAssistantChat context={context} onClose={() => setOpen(false)} />
				</Box>
			)}
		</Box>
	)
}

export default AiChatLauncher

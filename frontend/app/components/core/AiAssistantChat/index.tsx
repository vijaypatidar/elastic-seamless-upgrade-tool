import React, { type ChangeEvent, type KeyboardEvent, useEffect, useRef, useState } from "react"
import { Box, LinearProgress, Paper, Typography } from "@mui/material"
import { CloseCircle, MagicStar, Maximize4 } from "iconsax-react"
import Input from "~/components/utilities/Input"
import axiosJSON from "~/apis/http.ts"
import ReactMarkdown from "react-markdown"
import { useMutation } from "@tanstack/react-query"

type Message = {
	role: "user" | "ai"
	text: string
}
interface AiAssistantChatProps {
	onClose: () => void
	context: Context
}
const AiAssistantChat: React.FC<AiAssistantChatProps> = ({ onClose, context }) => {
	const [messages, setMessages] = useState<Message[]>([])
	const [input, setInput] = useState<string>("")
	const messagesEndRef = useRef<HTMLDivElement>(null)
	const [maximize, setMaximize] = useState(false)

	// Auto-scroll to latest message
	useEffect(() => {
		messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
	}, [messages])

	const { mutate: sendMessage, isPending } = useMutation({
		mutationKey: ["get-all-clusters"],
		mutationFn: async () => {
			if (!input.trim()) return

			setMessages((prev) => [...prev, { role: "user", text: input }])
			setInput("")

			const response = await axiosJSON.post(
				"/ai-assistant/ask",
				{
					message: input,
					context: context,
				},
				{
					timeout: 1000 * 60 * 60,
				}
			)
			setMessages((prev) => [...prev, { role: "ai", text: response.data }])
		},
	})

	const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => setInput(e.target.value)

	const handleKeyDown = async (e: KeyboardEvent<HTMLInputElement>) => {
		if (e.key === "Enter") sendMessage()
	}

	return (
		<Paper
			elevation={30}
			sx={{
				position: "fixed",
				bottom: maximize ? 0 : 20,
				right: maximize ? 0 : 30,
				height: maximize ? "90%" : "80%",
				width: maximize ? "100%" : "450px",
				display: "flex",
				flexDirection: "column",
				bgcolor: "#0A0A0A",
				borderLeft: "1px solid #333",
				borderRadius: "10px",
				zIndex: 1000000000,
				transition:
					"width 0.3s ease-in-out, right 0.3s ease-in-out, bottom 0.3s ease-in-out, height 0.3s ease-in-out",
			}}
		>
			<Box className="flex flex-col h-full">
				<Box className="flex items-center justify-between p-2 border-b border-gray-800">
					{/* Left: Title + MagicStar */}
					<div className="flex items-center gap-2">
						<Typography variant="h6" className="text-white text-sm font-medium">
							AI Assistant
						</Typography>
						<MagicStar color="#BDA0FF" size="14px" />
					</div>

					{/* Right: Controls */}
					<Box className="flex items-center gap-3">
						<Maximize4
							color="#A0A0A0"
							size={16}
							className="cursor-pointer hover:scale-110 transition-transform"
							onClick={() => setMaximize(!maximize)}
						/>
						<CloseCircle
							color="#BDA0FF"
							size={18}
							className="cursor-pointer hover:scale-110 transition-transform"
							onClick={onClose}
						/>
					</Box>
				</Box>

				{/* Messages */}
				<Box
					sx={{
						flex: 1,
						p: 2,
						overflowY: "auto",
						display: "flex",
						flexDirection: "column",
						gap: 1.5,
					}}
				>
					{messages.map((msg, idx) => (
						<Box
							key={idx}
							sx={{
								alignSelf: msg.role === "user" ? "flex-end" : "flex-start",
								bgcolor: msg.role === "user" ? "#303030" : "",
								color: "white",
								p: 1.5,
								borderRadius: 2,
								maxWidth: msg.role === "user" ? "80%" : "100%",
								fontSize: "12px",
							}}
						>
							<ReactMarkdown>{msg.text}</ReactMarkdown>
						</Box>
					))}
					<div ref={messagesEndRef} />
				</Box>
				{isPending && <LinearProgress sx={{ background: "#BDA0FF" }} />}
				<Box className="flex flex-row items-center gap-[6px] px-2 py-2">
					<Input
						fullWidth
						value={input}
						onChange={handleInputChange}
						onKeyDown={handleKeyDown}
						disabled={isPending}
						placeholder="Ask AI..."
						variant="outlined"
					/>
				</Box>
			</Box>
		</Paper>
	)
}

export default AiAssistantChat

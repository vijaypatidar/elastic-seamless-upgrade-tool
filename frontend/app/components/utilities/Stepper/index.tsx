import { Box } from "@mui/material"

function Stepper({ color, borderRadius = "8px", currentStep = 0, steps = 0 }: StepperType) {
	const totalStep = Math.max(steps, 0)
	const filledBar = Math.max(currentStep, 0)

	const bars = Array.from({ length: totalStep }).map((_, idx) => (
		<Box
			key={idx}
			component="span"
			className="h-1 w-full"
			sx={{
				borderRadius,
				backgroundColor: idx < filledBar ? color : "#D9DAE6",
			}}
		/>
	))

	return <Box className="flex items-center gap-[10px] w-full">{bars}</Box>
}

export default Stepper

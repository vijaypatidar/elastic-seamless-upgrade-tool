import { Box, TextField, Typography } from "@mui/material"

function Input(props: any) {
	const { error, helperText, gradientFocus, gradientHover, ...inputProps } = props
	return (
		<Box className="flex flex-col gap-[2px] w-full">
			<Box
				className="flex rounded-[10px] p-px w-full"
				sx={{
					background: error ? "#ef4444 !important" : "#3D3B42",
					":hover": {
						background:
							gradientHover || "linear-gradient(103deg, #393939 3.33%, #EBEAF0 53.63%, #6627FF 97.35%)",
					},
					":focus-within": {
						background:
							gradientFocus || "linear-gradient(103deg, #6627FF 3.33%, #EBEAF0 47.06%, #393939 97.35%)",
					},
				}}
			>
				<TextField
					className="custom-input"
					sx={{
						backgroundColor: "#0A0A0A",
						borderRadius: "10px",
                        "& .MuiOutlinedInput-root": {
                            borderRadius: "10px"
                        },
						"& .MuiOutlinedInput-input": {
							borderRadius: "10px",
							padding: "16px 12px 16px 16px",
							fontSize: "14px",
							lineHeight: "20px",
							fontWeight: 400,
							fontFamily: "Manrope",
							"::placeholder": {
								color: "#696572",
							},
						},
						"& fieldset": {
							border: "none",
						},
						...inputProps?.sx,
					}}
					{...inputProps}
				/>
			</Box>
			{Boolean(error) ? (
				<Typography fontSize="12px" fontWeight={400} lineHeight="20px" color="#ef4444">
					{helperText}
				</Typography>
			) : null}
		</Box>
	)
}

export default Input

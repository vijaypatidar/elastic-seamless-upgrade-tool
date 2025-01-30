import { Box, Typography } from "@mui/material"
import { useFormik } from "formik"
import { ArrowLeft, ArrowRight } from "iconsax-react"
import { ConatinedButton, OutlinedButton } from "~/components/utilities/Buttons"
import Input from "~/components/utilities/Input"
import validationSchema from "./validation/validation"
import SelectionTile from "./widgets/SelectionTile"

function Credentials({ backStep, onSubmit }: CredentialsCompType) {
	const formik = useFormik({
		initialValues: {
			elasticUrl: "",
			kibanaUrl: "",
			authPref: null,
			username: "",
			password: "",
			apiKey: "",
		},
		validationSchema: validationSchema,
		onSubmit: async (values) => {
			onSubmit(values)
		},
	})

	return (
		<Box
			className="flex flex-col gap-3 justify-between"
			height={{
				xs: "calc(var(--window-height) - 346px)",
				sm: "calc(var(--window-height) - 280px)",
				md: "calc(var(--window-height) - 280px)",
			}}
		>
			<Box className="flex flex-col items-stretch gap-6 max-w-[515px] w-full">
				<Box className="flex flex-col gap-[6px] w-full">
					<Typography color="#ABA9B1" fontSize="14px" fontWeight="400" lineHeight="20px">
						URLs
					</Typography>
					<Box className="flex flex-col gap-2 w-full">
						<Input
							fullWidth
							id="elasticUrl"
							name="elasticUrl"
							type="text"
							placeholder="Enter Elastic URL"
							variant="outlined"
							value={formik.values.elasticUrl}
							onChange={formik.handleChange}
							onBlur={formik.handleBlur}
							error={formik.touched.elasticUrl && Boolean(formik.errors.elasticUrl)}
							helperText={formik.touched.elasticUrl && formik.errors.elasticUrl}
						/>
						<Input
							fullWidth
							id="kibanaUrl"
							name="kibanaUrl"
							type="text"
							placeholder="Enter Kibana URL"
							variant="outlined"
							value={formik.values.kibanaUrl}
							onChange={formik.handleChange}
							onBlur={formik.handleBlur}
							error={formik.touched.kibanaUrl && Boolean(formik.errors.kibanaUrl)}
							helperText={formik.touched.kibanaUrl && formik.errors.kibanaUrl}
						/>
					</Box>
				</Box>
				<Box className="flex flex-col gap-[6px]">
					<Typography color="#ABA9B1" fontSize="14px" fontWeight="400" lineHeight="20px">
						Authentication preference
					</Typography>
					<Box className="flex flex-col gap-[2px]">
						<Box
							className="flex flex-row gap-2 justify-between"
							onBlur={() => formik.setFieldTouched("authPref", true)}
						>
							<SelectionTile
								label="Username & password"
								isSelected={formik.values.authPref === "U/P"}
								value="U/P"
								onSelect={(value: string | number) => formik.setFieldValue("authPref", value)}
							/>
							<SelectionTile
								label="API Key"
								isSelected={formik.values.authPref === "API_KEY"}
								value="API_KEY"
								onSelect={(value: string | number) => formik.setFieldValue("authPref", value)}
							/>
						</Box>
						{formik.touched.authPref && Boolean(formik.errors.authPref) ? (
							<Typography fontSize="12px" fontWeight={400} lineHeight="20px" color="#ef4444">
								{formik.touched.authPref && formik.errors.authPref}
							</Typography>
						) : null}
					</Box>
				</Box>
				{formik.values.authPref && (
					<Box className="flex flex-col gap-[6px]">
						<Typography color="#ABA9B1" fontSize="14px" fontWeight="400" lineHeight="20px">
							Credentials
						</Typography>
						<Box className="flex flex-col gap-[6px]">
							{formik.values.authPref === "U/P" ? (
								<>
									<Input
										fullWidth
										id="username"
										name="username"
										type="text"
										placeholder="Enter username"
										variant="outlined"
										value={formik.values.username}
										onChange={formik.handleChange}
										onBlur={formik.handleBlur}
										error={formik.touched.username && Boolean(formik.errors.username)}
										helperText={formik.touched.username && formik.errors.username}
									/>
									<Input
										fullWidth
										id="password"
										name="password"
										type="text"
										placeholder="Enter password"
										variant="outlined"
										value={formik.values.password}
										onChange={formik.handleChange}
										onBlur={formik.handleBlur}
										error={formik.touched.password && Boolean(formik.errors.password)}
										helperText={formik.touched.password && formik.errors.password}
									/>
								</>
							) : (
								<Input
									fullWidth
									id="apiKey"
									name="apiKey"
									type="text"
									placeholder="Enter apiKey"
									variant="outlined"
									value={formik.values.apiKey}
									onChange={formik.handleChange}
									onBlur={formik.handleBlur}
									error={formik.touched.apiKey && Boolean(formik.errors.apiKey)}
									helperText={formik.touched.apiKey && formik.errors.apiKey}
								/>
							)}
						</Box>
					</Box>
				)}
			</Box>
			<Box className="flex justify-end gap-2">
				<OutlinedButton onClick={backStep}>
					<ArrowLeft size="20px" color="currentColor" /> Back
				</OutlinedButton>
				<ConatinedButton disabled={!formik.dirty || formik.isSubmitting} onClick={formik.handleSubmit}>
					Continue <ArrowRight size="20px" color="currentColor" />
				</ConatinedButton>
			</Box>
		</Box>
	)
}

export default Credentials

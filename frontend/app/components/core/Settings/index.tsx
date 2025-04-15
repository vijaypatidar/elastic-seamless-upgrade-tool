import { Drawer, DrawerBody, DrawerContent } from "@heroui/react"
import { Box, Typography } from "@mui/material"
import { useFormik } from "formik"
import { ArrowLeft, Edit2, Setting2 } from "iconsax-react"
import { useEffect, useState } from "react"
import { ConatinedButton, OutlinedButton } from "~/components/utilities/Buttons"
import validationSchema from "./validation/validation"
import { useMutation, useQuery } from "@tanstack/react-query"
import { OneLineSkeleton } from "~/components/utilities/Skeletons"
import Input from "~/components/utilities/Input"
import axiosJSON from "~/apis/http"
import { toast } from "sonner"
import StringManager from "~/constants/StringManager"

const STYLES = {
	GO_BACK_BUTTON: {
		minHeight: "30px",
		height: "30px",
		gap: "6px",
		padding: "6px 10px 8px 10px",
		color: "#ADADAD",
		fontSize: "12px",
		fontWeight: "500",
		lineHeight: "normal",
		borderColor: "#2F2F2F",
		borderRadius: "8px",
		background: "#141415",
	},
}

const INITIAL_VALUE = {
	webhookURL: "",
}

function Settings({ isOpen, onOpenChange }: { isOpen: boolean; onOpenChange: () => void }) {
	const [initialValues, setInitialValues] = useState<TSettingValues>(INITIAL_VALUE)

	const formik = useFormik({
		initialValues: initialValues,
		enableReinitialize: true,
		validationSchema: validationSchema,
		onSubmit: async () => {},
	})

	useEffect(() => {
		if (isOpen) {
			refetch()
		}
	}, [isOpen])

	const getSettings = async () => {
		await axiosJSON
			.get("/api/settings")
			.then((res) => {
				setInitialValues({
					webhookURL: res?.data?.notificationWebhookUrl,
				})
				formik.resetForm()
			})
			.catch((err) => {
				console.log(err)
				toast.error(err?.response?.data.err ?? StringManager.GENERIC_ERROR)
			})
		return null
	}

	const { isLoading, isRefetching, refetch } = useQuery({
		queryKey: ["get-settings"],
		queryFn: getSettings,
		staleTime: Infinity,
	})

	const { mutate: HandleSubmit, isPending } = useMutation({
		mutationKey: ["update-settings"],
		mutationFn: async (values: any) => {
			await axiosJSON
				.post("/api/settings", {
					notificationWebhookUrl: values.webhookURL,
				})
				.then((res) => {
					setInitialValues(initialValues)
					formik.resetForm()
				})
				.catch((err) => toast.error(err?.response?.data.err ?? StringManager.GENERIC_ERROR))
		},
	})

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
			<DrawerContent>
				{(onClose) => (
					<DrawerBody>
						<Box height="50px" />
						<Box className="flex flex-col gap-2 w-full">
							<OutlinedButton onClick={onOpenChange} sx={STYLES.GO_BACK_BUTTON}>
								<ArrowLeft size="14px" color="currentColor" /> Go back
							</OutlinedButton>
							<Box
								className="flex p-px rounded-2xl h-[calc(var(--window-height)-120px)]"
								sx={{ background: "radial-gradient(#6E687C, #1D1D1D)" }}
							>
								<form
									onSubmit={formik.handleSubmit}
									onReset={formik.handleReset}
									className="flex flex-col gap-6 rounded-2xl bg-[#0D0D0D] w-full h-full items-start"
								>
									<Box
										padding="24px 32px 0px 32px"
										className="flex flex-row gap-3 justify-between items-center w-full"
									>
										<Box className="flex flex-row gap-5 items-center">
											<Box
												className="flex p-px rounded-[10px]"
												sx={{
													background:
														"linear-gradient(125deg, #6627FF 0%, #C9C0DF 38.44%, #C9C0DF 84.94%, #6627FF 100%)",
													boxShadow: "0px 0px 15px 3px rgba(102, 39, 255, 0.41)",
												}}
											>
												<Box className="flex rounded-[10px] items-center justify-center min-h-[42px] min-w-[42px] bg-[#0D0D0D]">
													<Setting2 size="20px" color="#FFF" />
												</Box>
											</Box>

											<Typography color="#FFF" fontSize="20px" fontWeight="600" lineHeight="22px">
												Settings
											</Typography>
										</Box>
										<ConatinedButton
											type="submit"
											disabled={!formik.dirty || formik.isSubmitting || isPending}
										>
											{formik.isSubmitting || isPending ? "Updating" : "Update"}
										</ConatinedButton>
									</Box>
									<Box
										className="flex flex-col h-full w-full gap-3 overflow-auto items-center"
										padding="0px 32px 24px 32px"
									>
										<Box className="flex flex-col max-w-[552px] w-full">
											<Box className="flex flex-col items-stretch gap-6 max-w-[552px] w-full">
												<Box className="flex flex-col gap-[6px] max-w-[515px]">
													<Typography
														color="#ABA9B1"
														fontSize="14px"
														fontWeight="400"
														lineHeight="20px"
													>
														Webhook URL
													</Typography>
													<OneLineSkeleton
														show={isLoading || isRefetching}
														component={
															<Input
																fullWidth
																id="webhookURL"
																name="webhookURL"
																type="text"
																placeholder="Enter Webhook URL"
																variant="outlined"
																value={formik.values.webhookURL}
																onChange={formik.handleChange}
																onBlur={formik.handleBlur}
																error={
																	formik.touched.webhookURL &&
																	Boolean(formik.errors.webhookURL)
																}
																helperText={
																	formik.touched.webhookURL &&
																	formik.errors.webhookURL
																}
															/>
														}
														height="52px"
														className="w-full rounded-[10px]"
													/>
													<Typography
														color="#6E6E6E"
														fontSize="14px"
														fontWeight="400"
														lineHeight="normal"
													>
														Please enter a webhook url on which you want to recieve
														notifications for node status.
													</Typography>
												</Box>
											</Box>
										</Box>
									</Box>
								</form>
							</Box>
						</Box>
					</DrawerBody>
				)}
			</DrawerContent>
		</Drawer>
	)
}

export default Settings

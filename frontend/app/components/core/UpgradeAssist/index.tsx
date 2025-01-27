import { Box, Typography } from "@mui/material"
import { Camera, Flash } from "iconsax-react"
import { OutlinedBorderButton } from "~/components/utilities/Buttons"
import DeprectedSettings from "./widgets/DeprectedSettings"
import { getGradientClass, getStepIndicatorData } from "~/lib/Utils"
// after:bg-[#292929]
function UpgradeAssistant() {
	const stepStatus: { [Key: string]: "COMPLETED" | "INPROGRESS" | "PENDING" | "NOTVISITED" } = {
		"1": "COMPLETED",
		"2": "INPROGRESS",
		"3": "PENDING",
		"4": "NOTVISITED",
	}

	const step1Data = getStepIndicatorData("01", stepStatus["1"])
	const step2Data = getStepIndicatorData("02", stepStatus["2"])
	const step3Data = getStepIndicatorData("03", stepStatus["3"])
	const step4Data = getStepIndicatorData("04", stepStatus["4"])

	return (
		<ol className="flex flex-col gap-4 w-full overflow-auto h-[calc(var(--window-height)-214px)]">
			<li
				className={`relative flex-1 after:content-[''] after:w-[1px] after:h-full after:inline-block after:absolute after:-bottom-[60px] after:left-11 after:z-20 w-full ${getGradientClass(
					stepStatus["1"],
					stepStatus["2"]
				)}`}
			>
				<Box className="flex items-center justify-center gap-8 w-full">
					<Box
						className="rounded-[20px] p-px w-full"
						sx={{
							background: step1Data?.boxBackground,
						}}
					>
						<Box
							className="flex items-start gap-3.5 bg-[#0f0f0f] rounded-[20px] relative w-full"
							padding="20px 20px 20px 24px"
						>
							<Box
								className="rounded-full flex items-center justify-center p-px z-30"
								sx={{
									background: step1Data?.background,
									boxShadow: step1Data?.boxShadow,
								}}
							>
								<Box
									className="flex items-center justify-center rounded-full min-h-[38px] min-w-[38px] max-w-[38px] max-h-[38px]"
									sx={{ background: step1Data?.internalBackground }}
								>
									<Typography
										color={step1Data?.textColor}
										textAlign="center"
										fontSize="14px"
										fontWeight="600"
										lineHeight="22px"
									>
										{step1Data?.stepValue}
									</Typography>
								</Box>
							</Box>
							<Box className="flex flex-row gap-3 items-center rounded-[20px] justify-between w-full">
								<Box className="flex flex-col gap-[6px]">
									<Typography color="#FFF" fontSize="16px" fontWeight="600" lineHeight="normal">
										Back up your data
									</Typography>
									<Typography
										color="#6E6E6E"
										fontSize="13px"
										fontWeight="400"
										lineHeight="20px"
										letterSpacing="0.26px"
									>
										Make sure you have a current snapshot before making an changes.
									</Typography>
								</Box>
								{stepStatus["01"] === "COMPLETED" ? (
									<OutlinedBorderButton
										icon={Camera}
										filledIcon={Camera}
										disabled={step1Data?.isDisabled}
									>
										Create snapshot
									</OutlinedBorderButton>
								) : null}
							</Box>
						</Box>
					</Box>
				</Box>
			</li>
			<li
				className={`relative flex-1 after:content-[''] after:w-[1px] after:h-full after:inline-block after:absolute after:-bottom-[60px] after:left-11 after:z-20 w-full ${getGradientClass(
					stepStatus["2"],
					stepStatus["3"]
				)}`}
			>
				<Box className="flex items-center justify-center gap-8 w-full">
					<Box
						className="rounded-[20px] p-px w-full"
						sx={{
							background: step2Data?.boxBackground,
						}}
					>
						<Box
							className="flex items-start gap-3.5 bg-[#0f0f0f] rounded-[20px] relative w-full"
							padding="20px 20px 20px 24px"
						>
							<Box
								className="rounded-full flex items-center justify-center p-px z-30"
								sx={{
									background: step2Data?.background,
									boxShadow: step2Data?.boxShadow,
								}}
							>
								<Box
									className="flex items-center justify-center rounded-full min-h-[38px] min-w-[38px] max-w-[38px] max-h-[38px]"
									sx={{ background: step2Data?.internalBackground }}
								>
									<Typography
										color={step2Data?.textColor}
										textAlign="center"
										fontSize="14px"
										fontWeight="600"
										lineHeight="22px"
									>
										{step2Data?.stepValue}
									</Typography>
								</Box>
							</Box>
							<Box className="flex flex-col gap-[10px] rounded-[20px] w-full">
								<Box className="flex flex-col gap-[6px]">
									<Typography color="#FFF" fontSize="16px" fontWeight="600" lineHeight="normal">
										Review deprecated settings & resolve issue
									</Typography>
									<Typography
										color="#6E6E6E"
										fontSize="13px"
										fontWeight="400"
										lineHeight="20px"
										letterSpacing="0.26px"
									>
										You must resolve any critical Elasticsearch and Kibana configuration issues
										before upgrading to Elastic 8.x. Ignoring warnings might result in differences
										in behavior after you upgrade. If you have application code that calls
										Elasticsearch APIs, review the Elasticsearch deprecation logs to make sure you
										are not using deprecated APIs.
									</Typography>
								</Box>
								<Box
									className="flex flex-row gap-8 flex-grow w-full"
									flexWrap={{ xs: "wrap", md: "nowrap" }}
								>
									<DeprectedSettings
										title="Elastic search"
										criticalValue={1}
										warningValue={1}
										isDisabled={step2Data?.isDisabled}
									/>
									<DeprectedSettings
										title="Kibana"
										criticalValue={1}
										warningValue={1}
										isDisabled={step2Data?.isDisabled}
									/>
								</Box>
							</Box>
						</Box>
					</Box>
				</Box>
			</li>
			<li
				className={`relative flex-1 after:content-[''] after:w-[1px] after:h-full after:inline-block after:absolute after:-bottom-[60px] after:left-11 after:z-20 w-full ${getGradientClass(
					stepStatus["3"],
					stepStatus["4"]
				)}`}
			>
				<Box className="flex items-center justify-center gap-8 w-full">
					<Box
						className="rounded-[20px] p-px w-full"
						sx={{
							background: step3Data?.boxBackground,
						}}
					>
						<Box
							className="flex items-start gap-3.5 bg-[#0f0f0f] rounded-[20px] relative w-full"
							padding="20px 20px 20px 24px"
						>
							<Box
								className="rounded-full flex items-center justify-center p-px z-30"
								sx={{
									background: step3Data?.background,
									boxShadow: step3Data?.boxShadow,
								}}
							>
								<Box
									className="flex items-center justify-center rounded-full min-h-[38px] min-w-[38px] max-w-[38px] max-h-[38px]"
									sx={{ background: step3Data?.internalBackground }}
								>
									<Typography
										color={step3Data?.textColor}
										textAlign="center"
										fontSize="14px"
										fontWeight="600"
										lineHeight="22px"
									>
										{step3Data?.stepValue}
									</Typography>
								</Box>
							</Box>
							<Box className="flex flex-row gap-3 items-center rounded-[20px] justify-between w-full">
								<Box className="flex flex-col gap-[6px]">
									<Typography color="#FFF" fontSize="16px" fontWeight="600" lineHeight="normal">
										Upgrade Cluster
									</Typography>
									<Typography
										color="#6E6E6E"
										fontSize="13px"
										fontWeight="400"
										lineHeight="20px"
										letterSpacing="0.26px"
									>
										Once you've resolved all critical issues and verified that your applications are
										ready, you can upgrade to Elastic 8.x. Be sure to back up your data again before
										upgrading.
									</Typography>
								</Box>
								<OutlinedBorderButton disabled={step3Data?.isDisabled} icon={Flash} filledIcon={Flash}>
									Upgrade
								</OutlinedBorderButton>
							</Box>
						</Box>
					</Box>
				</Box>
			</li>
			<li className="relative flex-1 w-full">
				<Box className="flex items-center justify-center gap-8 w-full">
					<Box
						className="rounded-[20px] p-px w-full"
						sx={{
							background: step4Data?.boxBackground,
						}}
					>
						<Box
							className="flex items-start gap-3.5 bg-[#0f0f0f] rounded-[20px] relative w-full"
							padding="20px 20px 20px 24px"
						>
							<Box
								className="rounded-full flex items-center justify-center p-px z-30"
								sx={{
									background: step4Data?.background,
									boxShadow: step4Data?.boxShadow,
								}}
							>
								<Box
									className="flex items-center justify-center rounded-full min-h-[38px] min-w-[38px] max-w-[38px] max-h-[38px]"
									sx={{ background: step4Data?.internalBackground }}
								>
									<Typography
										color={step4Data?.textColor}
										textAlign="center"
										fontSize="14px"
										fontWeight="600"
										lineHeight="22px"
									>
										{step4Data?.stepValue}
									</Typography>
								</Box>
							</Box>
							<Box className="flex flex-row gap-3 items-center rounded-[20px] justify-between w-full">
								<Box className="flex flex-col gap-[6px]">
									<Typography color="#FFF" fontSize="16px" fontWeight="600" lineHeight="normal">
										Upgrade Kibana
									</Typography>
									<Typography
										color="#6E6E6E"
										fontSize="13px"
										fontWeight="400"
										lineHeight="20px"
										letterSpacing="0.26px"
									>
										Once you've resolved all critical issues and verified that your applications are
										ready, you can upgrade to Elastic 8.x. Be sure to back up your data again before
										upgrading.
									</Typography>
								</Box>
								<OutlinedBorderButton disabled={step4Data?.isDisabled} icon={Flash} filledIcon={Flash}>
									Upgrade
								</OutlinedBorderButton>
							</Box>
						</Box>
					</Box>
				</Box>
			</li>
		</ol>
	)
}

export default UpgradeAssistant

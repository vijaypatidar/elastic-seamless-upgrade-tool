import { Box, Button, CssBaseline, Typography } from "@mui/material"
import Infrastructor from "./Infrastructor"
import Stepper from "~/components/utilities/Stepper"
import { useEffect, useMemo, useState } from "react"
import Credentials from "./Credentials"
import Certificates from "./Certificates"
import StorageManager from "~/constants/StorageManager"
import SessionStorageHandler from "~/lib/SessionHandler"
import { useNavigate } from "react-router"
import { useMutation } from "@tanstack/react-query"
import axiosJSON from "~/apis/http"
import LocalStorageHandler from "~/lib/LocalHanlder"
import { toast } from "sonner"

function Setup() {
	const navigate = useNavigate()
	const [step, setStep] = useState<number>(1)
	const [infraType, setInfraType] = useState<string | number>("")
	const [creds, setCreds] = useState<CredsType>({
		elasticUrl: "",
		kibanaUrl: "",
		authPref: null,
		username: "",
		password: "",
		apiKey: "",
	})
	// const [files, setFiles] = useState<{ certFiles?: File[]; jsonFiles?: File[] }>({})

	useEffect(() => {
		const st = SessionStorageHandler.getItem(StorageManager.SETUP_SET)
		if (st) {
			setStep(st as number)
		}
	}, [])

	const handleNextStep = () => {
		SessionStorageHandler.setItem(StorageManager.SETUP_SET, step + 1)
		setStep(step + 1)
	}

	const handleBackStep = () => {
		SessionStorageHandler.setItem(StorageManager.SETUP_SET, step - 1)
		setStep(step - 1)
	}

	const handleStepInfraSubmit = (value: string | number | null) => {
		if (value) {
			setInfraType(value)
			handleNextStep()
		}
	}

	const handleCredSubmit = (values: CredsType) => {
		setCreds(values)
		handleNextStep()
	}

	const { mutate: HandleSubmit, isPending } = useMutation({
		mutationKey: ["add-cluster"],
		mutationFn: async (values: CertiType) => {
			let certIds: Array<string> = []
			const formData = new FormData()
			values.certFiles?.forEach((file) => {
				formData.append("files", file, file.name)
			})
			if (values.certFiles?.length !== 0) {
				await axiosJSON
					.post("/api/elastic/clusters/certificates/upload", formData, {
						maxBodyLength: Infinity,
						headers: {
							"Content-Type": "multipart/form-data",
						},
					})
					.then((res) => (certIds = res.data.certificateIds))
					.catch((err) => toast.error(err?.response?.data.err))
			}
			await axiosJSON
				.post("/api/elastic/clusters", {
					elastic: { url: creds.elasticUrl, username: creds.username, password: creds.password },
					kibana: { url: creds.kibanaUrl, username: creds.username, password: creds.password },
					certificateIds: certIds,
				})
				.then((res) => {
					console.log(res)
					SessionStorageHandler.setItem(StorageManager.SETUP_SET, 1)
					LocalStorageHandler.setItem(StorageManager.CLUSTER_ID, "cluster-id")
					navigate("/cluster-overview")
				})
				.catch((err) => toast.error(err?.response?.data.err))
		},
	})

	const getStepForm = useMemo(() => {
		switch (step) {
			case 1:
				return <Infrastructor onSubmit={handleStepInfraSubmit} />
			case 2:
				return <Credentials backStep={handleBackStep} onSubmit={handleCredSubmit} />
			case 3:
				return <Certificates backStep={handleBackStep} onSubmit={HandleSubmit} isSubmitting={isPending} />
			default:
				return <Typography color="#FFF">No more steps found</Typography>
		}
	}, [step])

	return (
		<Box className="flex flex-col gap-10 w-full">
			<CssBaseline />
			<Box className="flex flex-col gap-2 max-w-[515px] w-full">
				<Typography fontSize="14px" fontWeight={400} lineHeight="20px" color="#7C768B">
					Step {step}/3
				</Typography>
				<Stepper steps={3} color="#CCFE76" currentStep={step} />
			</Box>
			{getStepForm}
		</Box>
	)
}

export default Setup

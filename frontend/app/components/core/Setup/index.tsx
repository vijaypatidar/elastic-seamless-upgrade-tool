import { Box, CssBaseline, Typography } from "@mui/material"
import { useMutation } from "@tanstack/react-query"
import { useEffect, useMemo, useState } from "react"
import { useNavigate } from "react-router"
import { toast } from "sonner"
import axiosJSON from "~/apis/http"
import Stepper from "~/components/utilities/Stepper"
import StorageManager from "~/constants/StorageManager"
import LocalStorageHandler from "~/lib/LocalHanlder"
import SessionStorageHandler from "~/lib/SessionHandler"
import Certificates from "./Certificates"
import Credentials from "./Credentials"
import Infrastructure from "./Infrastructure"

function Setup() {
	const navigate = useNavigate()
	const [step, setStep] = useState<number>(1)
	const [infraType, setInfraType] = useState<string | number>("")
	const [creds, setCreds] = useState<TCreds>({
		elasticUrl: "",
		kibanaUrl: "",
		authPref: null,
		username: "",
		password: "",
		apiKey: "",
		pathToSSH: "",
		kibanaClusters: [],
	})

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

	const handleCredSubmit = (values: TCreds) => {
		setCreds(values)
		handleNextStep()
	}

	const { mutate: HandleSubmit, isPending } = useMutation({
		mutationKey: ["add-cluster"],
		mutationFn: async (values: TCerti) => {
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
					.then((res) => (certIds = res?.data?.certificateIds))
					.catch((err) => toast.error(err?.response?.data.err))
			}
			await axiosJSON
				.post("/api/elastic/clusters", {
					elastic: { url: creds.elasticUrl, username: creds.username, password: creds.password },
					kibana: { url: creds.kibanaUrl, username: creds.username, password: creds.password },
					certificateIds: certIds,
					infrastructureType: infraType,
					pathToKey: creds.pathToSSH,
					kibanaClusterInfo: creds.kibanaClusters,
				})
				.then((res) => {
					LocalStorageHandler.setItem(StorageManager.INFRA_TYPE, infraType)
					SessionStorageHandler.setItem(StorageManager.SETUP_SET, 1)
					LocalStorageHandler.setItem(StorageManager.CLUSTER_ID, res?.data?.clusterId || "cluster-id")
					navigate("/cluster-overview")
				})
				.catch((err) => toast.error(err?.response?.data.err))
		},
	})

	const getStepForm = useMemo(() => {
		switch (step) {
			case 1:
				return <Infrastructure onSubmit={handleStepInfraSubmit} />
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

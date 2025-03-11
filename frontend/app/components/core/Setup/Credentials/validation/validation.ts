import * as Yup from "yup"
import { URL_PATTERN } from "~/constants/RegexManager"

const validationSchema = Yup.object().shape({
	elasticUrl: Yup.string()
		.required("Please enter elastic url.")
		.matches(URL_PATTERN, "Please enter a valid elastic url."),
	kibanaUrl: Yup.string()
		.required("Please enter kibana url.")
		.matches(URL_PATTERN, "Please enter a valid kibana url."),
	authPref: Yup.string().required("Please select atleast one preference."),
	username: Yup.string().when("authPref", {
		is: (authPref: string) => authPref === "U/P",
		then: (schema) => schema.required("Please enter username."),
	}),
	password: Yup.string().when("authPref", {
		is: (authPref: string) => authPref === "U/P",
		then: (schema) => schema.required("Please enter password."),
	}),
	apiKey: Yup.string().when("authPref", {
		is: (authPref: string) => authPref === "API_KEY",
		then: (schema) => schema.required("Please enter api key."),
	}),
	pathToSSH: Yup.string().required("Please enter SSH key."),
	kibanaConfigs: Yup.array().of(
		Yup.object({
			name: Yup.string().required("Cluster name is required."),
			ip: Yup.string().required("Cluster IP is required."),
		})
	),
})

export default validationSchema

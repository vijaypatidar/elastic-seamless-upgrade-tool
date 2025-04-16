import { URL_PATTERN } from "~/constants/RegexManager"
import * as Yup from "yup"

const validationSchema = Yup.object().shape({
	webhookURL: Yup.string()
		.required("Please enter webhook url.")
		.matches(URL_PATTERN, "Please enter a valid webhook url."),
})

export default validationSchema

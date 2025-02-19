type TClusterValues = {
	elasticUrl: string
	kibanaUrl: string
	authPref: "U/P" | "API_KEY" | null
	username: string
	password: string
	apiKey: string | null
	pathToSSH: string
	kibanaConfigs: TKibanaConfigs[]
	certFiles: File[] | TExistingFile[]
}

type TKibanaConfigs = {
	name: string
	ip: string
}

type TExistingFile = {
	name: string
	storedOnServer: boolean
}

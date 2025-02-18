type TClusterValues = {
	elasticUrl: string
	kibanaUrl: string
	authPref: "U/P" | "API_KEY" | null
	username: string
	password: string
	apiKey: string | null
	pathToSSH: string
	kibanaClusters: any[]
	certFiles: File[] | TExistingFile[]
}

type TExistingFile = {
	name: string
	storedOnServer: boolean
}

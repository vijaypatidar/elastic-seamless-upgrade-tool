type TCreds = {
	type: string
	name: string
	elasticUrl: string
	kibanaUrl: string
	authPref: string | null
	username?: string
	password?: string
	apiKey?: string
	sshUser: string
	pathToSSH?: string
	kibanaConfigs: TKibanaConfigs[]
	deploymentId?: string
}

type TKibanaConfigs = { name: string; ip: string }
type TCerti = { certFiles?: File[]; jsonFiles?: File[] }

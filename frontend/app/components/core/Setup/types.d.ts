type TCreds = {
	elasticUrl: string
	kibanaUrl: string
	authPref: string | null
	username?: string
	password?: string
	apiKey?: string
	pathToSSH?: string
	kibanaConfigs: TKibanaConfigs[]
}

type TKibanaConfigs = { name: string; ip: string }
type TCerti = { certFiles?: File[]; jsonFiles?: File[] }

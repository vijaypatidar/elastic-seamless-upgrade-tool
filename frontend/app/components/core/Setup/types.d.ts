type CredsType = {
	elasticUrl: string
	kibanaUrl: string
	authPref: string | null
	username?: string
	password?: string
	apiKey?: string
}

type CertiType = { certFiles?: File[]; jsonFiles?: File[] }

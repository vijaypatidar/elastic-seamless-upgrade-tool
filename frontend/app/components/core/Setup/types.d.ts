type TCreds = {
	elasticUrl: string
	kibanaUrl: string
	authPref: string | null
	username?: string
	password?: string
	apiKey?: string
}

type TCerti = { certFiles?: File[]; jsonFiles?: File[] }

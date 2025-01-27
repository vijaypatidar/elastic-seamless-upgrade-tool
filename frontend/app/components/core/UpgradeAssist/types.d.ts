type DeprecationSettingsType = {
	title: string
	criticalValue: string | number
	warningValue: string | number
	isDisabled: boolean
}

type IssueType = {
	title: string
	icon: React.FunctionComponent<{ size?: string; color?: string }>
	value: string | number
	fontColor?: string
	bgColor?: string
}

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

type StepDataType = {
	boxBackground: string
	background: string
	textColor: string
	stepValue: string | React.ReactElement
	internalBackground: string
	boxShadow: string
	isDisabled: boolean
}

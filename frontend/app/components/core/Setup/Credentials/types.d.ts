type TCredentialsComp = { initialValues: TCreds; backStep: () => void; onSubmit: (values: TCreds) => void }
type TSelectionTile = {
	value: string
	label: string
	isSelected: boolean
	onSelect: (value: string | number) => void
}

type CredentialsCompType = { backStep: () => void; onSubmit: (values: CredsType) => void }
type SelectionTileType = {
	value: string
	label: string
	isSelected: boolean
	onSelect: (value: string | number) => void
}

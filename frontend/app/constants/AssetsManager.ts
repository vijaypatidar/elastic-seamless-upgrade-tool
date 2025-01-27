import El from "../assets/imgs/webp/el.webp"
import LayoutLogoTransition from "../assets/imgs/webp/layout-logo-transition.webp"
import LogoTransparent from "../assets/imgs/webp/logo-transparent.webp"
import Logo from "../assets/imgs/webp/logo.webp"
import Logstash from "../assets/imgs/webp/logstash.webp"
import NamedLogoTransparent from "../assets/imgs/webp/named-logo-transparent.webp"
import LogoPlusNamed from "../assets/imgs/webp/named-plus-logo.webp"
import StarsLarge from "../assets/imgs/webp/stars-large.webp"
import StarsSmall from "../assets/imgs/webp/stars-small.webp"
import AnimatedLoaderSVG from "../assets/svgs/animated-loader.svg"
import WavingHandSVG from "../assets/svgs/waving-hand.svg"

const AssetsManager = {
	LOGO: Logo,
	LOGO_TRANSPARENT: LogoTransparent,
	NAMED_LOGO_TRANSPARENT: NamedLogoTransparent,
	LAYOUT_LOGO_TRANSITION: LayoutLogoTransition,
	STARS_SMALL: StarsSmall,
	STARS_LARGE: StarsLarge,
	WAVING_HAND: WavingHandSVG,
	ANIMATED_LOADER: AnimatedLoaderSVG,
	LOGO_PLUS_NAMED: LogoPlusNamed,
	LOGSTASH: Logstash,
	EL: El,
} as const

export default AssetsManager

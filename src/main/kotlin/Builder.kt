import org.w3c.dom.*
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.browser.window

const val defaultHost = "payanyway.ru"

const val modalContentWidthLimitPx = 416

const val containerHeightPx = 224

val modalContentHeaderStyles = hashMapOf(
    "font-size" to "20px",
    "line-height" to "24px",
    "font-family" to "\"ProximaNova-Semibold\", Arial, sans-serif",
    "margin" to "22px 0 0 16px"
)

val modalCloseStyles = hashMapOf(
    "width" to "48px",
    "height" to "48px",
    "position" to "absolute",
    "right" to "0",
    "top" to "0"
)

val modalCloseIconStyles = hashMapOf(
    "height" to "12px",
    "width" to "12px",
    "display" to "block",
    "margin" to "18px",
    "background-image" to "url(\"data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48c3ZnIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgd2lkdGg9IjEyIiBoZWlnaHQ9IjEyIj48cGF0aCBmaWxsPSIjODY5MzlFIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGQ9Ik03IDZsNSA1LTEgMS01LTUtNSA1LTEtMSA1LTUtNS01IDEtMSA1IDUgNS01IDEgMS01IDV6Ii8+PC9zdmc+\")",
    "cursor" to "pointer"
)

val modalOverlayStyles = hashMapOf(
    "background-color" to "rgba(0, 0, 0, 0.8)",
    "width" to "100vw",
    "height" to "100vh",
    "position" to "absolute",
    "top" to "0",
    "left" to "0",
    "z-index" to "100000"
)

val modalContentStyles = hashMapOf(
    "border-radius" to "4px",
    "min-width" to "320px",
    "max-width" to "${modalContentWidthLimitPx}px",
    "overflow" to "hidden",
    "background-color" to "white",
    "top" to "50%",
    "position" to "absolute",
    "display" to "inline-block",
    "margin-top" to "-135px",
    "width" to "100%"
)

val modalContentWideStyles = hashMapOf(
    "left" to "50%",
    "margin-left" to "-208px",
    "box-shadow" to "0 6px 12px 0 rgba(0, 26, 79, 0.08)"
)

val modalContentNarrowStyles = hashMapOf(
    "left" to "0",
    "margin-left" to "0",
    "box-shadow" to "none"
)

val containerStyles = hashMapOf(
    "border-radius" to "4px",
    "box-shadow" to "0 6px 12px 0 rgba(0, 26, 79, 0.08)",
    "min-width" to "320px",
    "max-width" to "${modalContentWidthLimitPx}px",
    "height" to "${containerHeightPx}px",
    "overflow" to "hidden"
)

val iframeStyles = hashMapOf(
    "border" to "none",
    "min-height" to "${containerHeightPx}px",
    "width" to "100%",
    "height" to "100%"
)

val modalContainerStyles = hashMapOf(
    "min-width" to "320px",
    "max-width" to "${modalContentWidthLimitPx}px",
    "height" to "${containerHeightPx}px"
)

val modalIframeStyles = hashMapOf(
    "border" to "none",
    "min-height" to "${containerHeightPx}px",
    "width" to "100%",
    "height" to "100%"
)

val defaultOptions = hashMapOf(
    "account" to "",
    "amount" to "",
    "transactionId" to "",
    "testMode" to "0",
    "version" to "2",
    "theme" to AssistantTheme.LIGHT.name.toLowerCase(),
    "lang" to AssistantLang.RU.name.toLowerCase(),
    "paymentSystemId" to "card"
)

val assistantParams = hashMapOf(
    "account" to "MNT_ID",
    "amount" to "MNT_AMOUNT",
    "transactionId" to "MNT_TRANSACTION_ID",
    "currency" to "MNT_CURRENCY_CODE",
    "testMode" to "MNT_TEST_MODE",
    "description" to "MNT_DESCRIPTION",
    "subscriberId" to "MNT_SUBSCRIBER_ID",
    "signature" to "MNT_SIGNATURE",
    "lang" to "moneta.locale",
    "paymentSystemId" to "paymentSystem.unitId"
)

enum class AssistantLang {
    RU, EN;
}

enum class AssistantTheme {
    LIGHT, DARK;
}

fun resolveHeaderText(lang: AssistantLang) = when(lang) {
    AssistantLang.RU -> "Оплата банковской картой"
    AssistantLang.EN -> "Card payment"
}

fun createAndStylizeElement(tag: String, styles: Map<String, String>): HTMLElement {
    val el = document.createElement(tag) as HTMLElement
    el.applyStyles(styles)

    return el
}

fun HTMLElement.applyStyles(styles: Map<String, String>) = styles.forEach { style.setProperty(it.key, it.value) }

fun createModalContentHeader(lang: AssistantLang): HTMLElement {
    val header = createAndStylizeElement("div", modalContentHeaderStyles)
    header.textContent = resolveHeaderText(lang)

    return header
}

fun createModalCloseIconContainer(): HTMLElement = createAndStylizeElement("div", modalCloseStyles)

fun createModalCloseIcon(): HTMLElement = createAndStylizeElement("i", modalCloseIconStyles)

fun createModalOverlay(): HTMLElement = createAndStylizeElement("div", modalOverlayStyles)

fun createModalContent(): HTMLElement {
    val content = createAndStylizeElement("div", modalContentStyles)

    val applyStyles = { matches: Boolean ->
        if (matches) content.applyStyles(modalContentNarrowStyles)
        else content.applyStyles(modalContentWideStyles)
    }

    val mql = window.matchMedia("(max-width: ${modalContentWidthLimitPx}px)")

    applyStyles(mql.matches);

    mql.addListener {
        applyStyles((it as MediaQueryListEvent).matches)
    }

    return content
}

fun parseOptions(options: dynamic): HashMap<String, String> {
    val optionsMap = hashMapOf<String, String>()

    val keys = js("Object.keys(options)") as Array<String>
    keys.forEach {
        val value = options[it]

        if (js("typeof value === 'object'") as Boolean)
            optionsMap.putAll(parseOptions(value))
        else
            optionsMap[it] = value.toString()
    }

    return optionsMap
}

fun mergeOptions(defaultOptions: HashMap<String, String>,
                 clientOptions: HashMap<String, String>): HashMap<String, String> {
    val options = hashMapOf<String, String>()

    options.putAll(defaultOptions)
    options.putAll(clientOptions)

    return options
}

fun buildAssistantUrl(host: String, options: HashMap<String, String>): String {
    var query = ""

    options.forEach {
        query += "${if (query.isNotEmpty()) "&" else ""}${assistantParams[it.key] ?: it.key}=${js("encodeURIComponent(it.value)")}"
    }

    return "https://${host}/assistant.htm?${query}"
}

fun setViewport() {
    var meta = document.querySelector("meta[name=viewport]") as HTMLMetaElement?

    if (meta == null) {
        meta = document.createElement("meta") as HTMLMetaElement
        meta.name = "viewport"

        document.getElementsByTagName("head")[0]?.appendChild(meta)
    }

    meta.content = "width=device-width, initial-scale=1, user-scalable=no"
}

fun generateId() = "paw-payment-form-${(1..10000).random()}${('A'..'Z').random()}${('a'..'z').random()}"

class Builder {
    private var onSuccessCallback: dynamic = null
    private var onFailCallback: dynamic = null
    private var onInProgressCallback: dynamic = null

    private var modal: HTMLElement? = null
    private var messageListenerAttached = false
    private var host: String? = null

    @JsName("build")
    fun build(options: dynamic, containerId: String = "") {
        setViewport()

        if (!messageListenerAttached) {
            window.addEventListener("message", { messageListener(it as MessageEvent) })
            messageListenerAttached = true
        }

        val assistantOptions = mergeOptions(defaultOptions, parseOptions(options))

        val iframe = document.createElement("iframe") as HTMLIFrameElement
        val container: HTMLElement?

        if (containerId.isNotBlank()) {
            container = document.getElementById(containerId) as? HTMLElement ?: return

            while(container.hasChildNodes()) {
                container.firstChild?.let { container.removeChild(it) }
            }

            assistantOptions["containerId"] = containerId

            container.applyStyles(containerStyles)
            iframe.applyStyles(iframeStyles)
        } else {
            container = createAndStylizeElement("div", modalContainerStyles)

            val id = generateId()

            container.id = id
            assistantOptions["containerId"] = id

            iframe.applyStyles(modalIframeStyles)

            assistantOptions["theme"] = AssistantTheme.LIGHT.name.toLowerCase()

            createModal(container,
                AssistantLang.valueOf(assistantOptions["lang"]?.toUpperCase() ?: AssistantLang.RU.name))
        }

        iframe.src = buildAssistantUrl(host ?: defaultHost, assistantOptions)
        container.appendChild(iframe)
    }

    @JsName("closeModal")
    fun closeModal() {
        modal?.parentNode?.removeChild(modal!!)
        modal = null
    }

    @JsName("setOnSuccessCallback")
    fun setOnSuccessCallback(onSuccessCallback: dynamic) {
        this.onSuccessCallback = onSuccessCallback
    }

    @JsName("setOnFailCallback")
    fun setOnFailCallback(onFailCallback: dynamic) {
        this.onFailCallback = onFailCallback
    }

    @JsName("setOnInProgressCallback")
    fun setOnInProgressCallback(onInProgressCallback: dynamic) {
        this.onInProgressCallback = onInProgressCallback
    }

    @JsName("setHost")
    fun setHost(host: String?) {
        this.host = host
    }

    private fun createModal(iframeContainer: HTMLElement, lang: AssistantLang) {
        if (modal != null) closeModal()

        modal = createModalOverlay()

        val closeIcon = createModalCloseIcon()
        closeIcon.addEventListener("click", { closeModal() })

        val closeIconContainer = createModalCloseIconContainer()
        closeIconContainer.appendChild(closeIcon)

        val content = createModalContent()
        content.appendChild(closeIconContainer)
        content.appendChild(createModalContentHeader(lang))
        content.appendChild(iframeContainer)

        modal!!.appendChild(content)

        document.body?.appendChild(modal!!)
        document.addEventListener("keyup", { if ((it as KeyboardEvent).code == "Escape") closeModal() })
    }

    private fun messageListener(event: MessageEvent) {
        val payload = event.data?.asDynamic() ?: return

        when(payload.type as? String) {
            "widgetHeight" -> {
                (payload.containerId as? String)?.let {
                    (document.getElementById(it) as? HTMLElement)?.style?.height =
                        "${payload.height as? String ?: containerHeightPx}px"
                }
            }
            "close" -> closeModal()
            "success" -> if (onSuccessCallback != null) { onSuccessCallback(payload.operationId, payload.transactionId) }
            "fail" -> if (onFailCallback != null) { onFailCallback(payload.operationId, payload.transactionId) }
            "inProgress" -> if (onInProgressCallback != null) { onInProgressCallback(payload.operationId, payload.transactionId) }
        }
    }
}

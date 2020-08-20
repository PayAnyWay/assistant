import org.w3c.dom.*
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.browser.window

external fun encodeURIComponent(uri: String): String

const val productionHost = "{{PRODUCTION_HOST}}"
const val demoHost = "{{DEMO_HOST}}"

const val modalContentWidthLimitPx = 416

const val containerHeightPx = 224

val modalContentHeaderStyles = mapOf(
    "font-size" to "20px",
    "line-height" to "24px",
    "font-family" to "\"ProximaNova-Semibold\", Arial, sans-serif",
    "margin" to "22px 0 0 16px"
)

val modalCloseStyles = mapOf(
    "width" to "48px",
    "height" to "48px",
    "position" to "absolute",
    "right" to "0",
    "top" to "0"
)

val modalCloseIconStyles = mapOf(
    "height" to "12px",
    "width" to "12px",
    "display" to "block",
    "margin" to "18px",
    "background-image" to "url(\"data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48c3ZnIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgd2lkdGg9IjEyIiBoZWlnaHQ9IjEyIj48cGF0aCBmaWxsPSIjODY5MzlFIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiIGQ9Ik03IDZsNSA1LTEgMS01LTUtNSA1LTEtMSA1LTUtNS01IDEtMSA1IDUgNS01IDEgMS01IDV6Ii8+PC9zdmc+\")",
    "cursor" to "pointer"
)

val modalOverlayStyles = mapOf(
    "background-color" to "rgba(0, 0, 0, 0.8)",
    "width" to "100vw",
    "height" to "100vh",
    "position" to "absolute",
    "top" to "0",
    "left" to "0",
    "z-index" to "100000"
)

val modalContentStyles = mapOf(
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

val modalContentWideStyles = mapOf(
    "left" to "50%",
    "margin-left" to "-208px",
    "box-shadow" to "0 6px 12px 0 rgba(0, 26, 79, 0.08)"
)

val modalContentNarrowStyles = mapOf(
    "left" to "0",
    "margin-left" to "0",
    "box-shadow" to "none"
)

val containerStyles = mapOf(
    "border-radius" to "4px",
    "box-shadow" to "0 6px 12px 0 rgba(0, 26, 79, 0.08)",
    "min-width" to "320px",
    "max-width" to "${modalContentWidthLimitPx}px",
    "height" to "${containerHeightPx}px",
    "overflow" to "hidden"
)

val iframeStyles = mapOf(
    "border" to "none",
    "min-height" to "${containerHeightPx}px",
    "width" to "100%",
    "height" to "100%"
)

val modalContainerStyles = mapOf(
    "min-width" to "320px",
    "max-width" to "${modalContentWidthLimitPx}px",
    "height" to "${containerHeightPx}px"
)

val modalIframeStyles = mapOf(
    "border" to "none",
    "min-height" to "${containerHeightPx}px",
    "width" to "100%",
    "height" to "100%"
)

val defaultOptions = mapOf(
    "testMode" to "0",
    "version" to "2",
    "theme" to AssistantTheme.LIGHT.name.toLowerCase(),
    "lang" to AssistantLang.RU.name.toLowerCase(),
    "paymentSystemId" to "card"
)

val assistantParams = mapOf(
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

fun resolveHeaderText(lang: AssistantLang) = when (lang) {
    AssistantLang.RU -> "Оплата банковской картой"
    AssistantLang.EN -> "Card payment"
}

fun createAndStylizeElement(tag: String, styles: Map<String, String>): HTMLElement =
    (document.createElement(tag) as HTMLElement).apply { applyStyles(styles) }

fun HTMLElement.applyStyles(styles: Map<String, String>) = styles.forEach { style.setProperty(it.key, it.value) }

fun createModalContentHeader(lang: AssistantLang): HTMLElement =
    createAndStylizeElement("div", modalContentHeaderStyles).apply { textContent = resolveHeaderText(lang) }

fun createModalCloseIconContainer(): HTMLElement = createAndStylizeElement("div", modalCloseStyles)

fun createModalCloseIcon(): HTMLElement = createAndStylizeElement("i", modalCloseIconStyles)

fun createModalOverlay(): HTMLElement = createAndStylizeElement("div", modalOverlayStyles)

fun createModalContent(): HTMLElement {
    val content = createAndStylizeElement("div", modalContentStyles)

    val applyStyles = { matches: Boolean ->
        if (matches) content.applyStyles(modalContentNarrowStyles)
        else content.applyStyles(modalContentWideStyles)
    }

    val mql = window.matchMedia("(max-width: ${modalContentWidthLimitPx}px)").apply {
        addListener { applyStyles((it as MediaQueryListEvent).matches) }
    }

    applyStyles(mql.matches)

    return content
}

fun parseOptions(options: dynamic): HashMap<String, String> {
    val optionsMap = hashMapOf<String, String>()

    (js("Object.keys(options)") as Array<String>).forEach {
        val value = options[it]

        if (js("value !== null && typeof value === 'object'") as Boolean)
            optionsMap.putAll(parseOptions(value))
        else if (js("value !== undefined && value !== null") as Boolean)
            optionsMap[it] = value.toString()
    }

    return optionsMap
}

fun mergeOptions(defaultOptions: Map<String, String>, clientOptions: HashMap<String, String>) =
    hashMapOf<String, String>().apply {
        putAll(defaultOptions)
        putAll(clientOptions)
    }

fun buildAssistantUrl(isDemo: Boolean, options: HashMap<String, String>): String {
    val paramString =
        options.map { "${assistantParams[it.key] ?: it.key}=${encodeURIComponent(it.value)}" }.joinToString("&")
    var host = productionHost
    if (isDemo) {
        host = demoHost
    }
    return "${host}/assistant.htm?$paramString"
}

fun setViewport() {
    (document.querySelector("meta[name=viewport]") as? HTMLMetaElement
        ?: (document.createElement("meta") as HTMLMetaElement).apply {
            name = "viewport"
            document.getElementsByTagName("head")[0]?.appendChild(this)
        }).apply {

        content = "width=device-width, initial-scale=1, user-scalable=no"
    }
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
        if (js("options === null || typeof options !== 'object'") as Boolean) {
            throw IllegalArgumentException("options must be an object")
        }

        setViewport()

        if (!messageListenerAttached) {
            window.addEventListener("message", { messageListener(it as MessageEvent) })
            messageListenerAttached = true
        }

        val modal = containerId.isBlank()

        if (modal) {
            createAndStylizeElement("div", modalContainerStyles).apply {
                id = generateId()
            }
        } else {
            (document.getElementById(containerId) as? HTMLElement)?.apply {
                while (hasChildNodes()) {
                    firstChild?.let { removeChild(it) }
                }

                applyStyles(containerStyles)
            } ?: throw IllegalArgumentException("Wrong containerId")
        }.apply {
            val assistantOptions = mergeOptions(defaultOptions, parseOptions(options))
            assistantOptions["containerId"] = id

            if (modal) {
                assistantOptions["theme"] = AssistantTheme.LIGHT.name.toLowerCase()

                createModal(
                    this,
                    AssistantLang.valueOf(assistantOptions["lang"]?.toUpperCase() ?: AssistantLang.RU.name)
                )
            }

            appendChild((document.createElement("iframe") as HTMLIFrameElement).apply {
                src = buildAssistantUrl(resolveIsDemo(options), assistantOptions)
                applyStyles(if (modal) modalIframeStyles else iframeStyles)
            })
        }
    }

    private fun resolveIsDemo(options: Any?): Boolean =
        with(options?.asDynamic().demo?.toString()?.toLowerCase()) {
            this == "1" || this == "true"
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

        modal = createModalOverlay().apply {
            appendChild(
                createModalContent().apply {
                    appendChild(createModalCloseIconContainer().apply {
                        appendChild(createModalCloseIcon().apply {
                            addEventListener("click", { closeModal() })
                        })
                    })
                    appendChild(createModalContentHeader(lang))
                    appendChild(iframeContainer)
                })
        }

        document.apply {
            body?.appendChild(modal!!)
            addEventListener("keyup", { if ((it as KeyboardEvent).code == "Escape") closeModal() })
        }
    }

    private fun messageListener(event: MessageEvent) {
        val payload = event.data?.asDynamic() ?: return

        when (payload.type as? String) {
            "widgetHeight" -> {
                (payload.containerId as? String)?.run {
                    (document.getElementById(this) as? HTMLElement)?.style?.height = "${payload.height}px"
                }
            }
            "close" -> closeModal()
            "success" -> if (onSuccessCallback != null) {
                onSuccessCallback(payload.operationId, payload.transactionId)
            }
            "fail" -> if (onFailCallback != null) {
                onFailCallback(payload.operationId, payload.transactionId)
            }
            "inProgress" -> if (onInProgressCallback != null) {
                onInProgressCallback(payload.operationId, payload.transactionId)
            }
        }
    }
}

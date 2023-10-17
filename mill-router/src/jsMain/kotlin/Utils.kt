package coder.stanley.mill.router

actual fun uuid(): String {
    return Uuid.v4()
}

@JsModule("uuid")
@JsNonModule
private external object Uuid {
    fun v4(): String
}

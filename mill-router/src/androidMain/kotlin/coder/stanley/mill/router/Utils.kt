package coder.stanley.mill.router

import java.util.UUID

actual fun uuid(): String {
    return UUID.randomUUID().toString()
}

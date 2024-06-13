package coder.stanley.mill.router

import java.util.UUID

internal actual fun uuid(): String {
    return UUID.randomUUID().toString()
}

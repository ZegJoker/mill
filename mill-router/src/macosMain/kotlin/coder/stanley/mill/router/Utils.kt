package coder.stanley.mill.router

import platform.Foundation.NSUUID

internal actual fun uuid(): String {
    return NSUUID.UUID().UUIDString
}

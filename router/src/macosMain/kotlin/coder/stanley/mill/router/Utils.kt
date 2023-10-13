package coder.stanley.mill.router

import platform.Foundation.NSUUID

actual fun uuid(): String {
    return NSUUID.UUID().UUIDString
}

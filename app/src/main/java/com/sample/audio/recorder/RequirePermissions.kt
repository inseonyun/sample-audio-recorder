package com.sample.audio.recorder

import android.Manifest

enum class RequirePermissions(val manifestPermission: String) {
    RECORD_AUDIO(Manifest.permission.RECORD_AUDIO),
}

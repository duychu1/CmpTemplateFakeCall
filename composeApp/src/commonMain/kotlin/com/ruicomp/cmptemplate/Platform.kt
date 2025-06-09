package com.ruicomp.cmptemplate

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
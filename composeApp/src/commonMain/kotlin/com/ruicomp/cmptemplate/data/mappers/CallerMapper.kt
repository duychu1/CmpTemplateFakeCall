package com.ruicomp.cmptemplate.data.mappers

import com.ruicomp.cmptemplate.database.Caller as DbCaller
import com.ruicomp.cmptemplate.domain.models.Caller

fun DbCaller.toDomain(): Caller {
    return Caller(
        id = id,
        name = name,
        number = number
    )
}

fun Caller.toDb(): DbCaller {
    return DbCaller(
        id = id,
        name = name,
        number = number
    )
}
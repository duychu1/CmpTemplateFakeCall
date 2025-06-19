package com.ruicomp.cmptemplate.features.call_history.presentation

import com.ruicomp.cmptemplate.core.models.Contact
import com.ruicomp.cmptemplate.features.call_history.domain.models.CallHistory

sealed class CallHistoryEvent {
    object LoadHistory : CallHistoryEvent()
    data class SelectHistoryForRecall(val history: CallHistory?) : CallHistoryEvent()
    object TriggerShowBottomSheet : CallHistoryEvent()
}

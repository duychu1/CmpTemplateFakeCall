package com.ruicomp.cmptemplate.features.home.presentation

sealed interface HomeEvent {
    data object CallNowClicked : HomeEvent
}
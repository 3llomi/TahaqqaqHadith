package com.devlomi.tahaqqaqhadith.common

sealed class UIComponentType{

    object Dialog: UIComponentType()
    object Toast: UIComponentType()

    object None: UIComponentType()
}
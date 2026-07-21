package com.devlomi.tahaqqaqhadith.common

data class GenericMessageInfo(
    val id: String,
    val title: String,
    val uiComponentType: UIComponentType,
    val description: String? = null,
    val onDismiss: (() -> Unit)? = null,
    val positiveAction: PositiveAction? = null,
    val negativeAction: NegativeAction? = null
) {
    constructor(id: String, title: String, uiComponentType: UIComponentType) : this(
        id = id,
        title = title,
        uiComponentType = uiComponentType,
        description = null,
        onDismiss = null,
        positiveAction = null,
        negativeAction = null
    )
}


data class PositiveAction(
    val positiveBtnTxt: String,
    val onPositiveAction: () -> Unit,
)

data class NegativeAction(
    val negativeBtnTxt: String,
    val onNegativeAction: () -> Unit,
)

fun Queue<GenericMessageInfo>.doesMessageAlreadyExistInQueue(messageInfo: GenericMessageInfo): Boolean {
    return this.items.any { it.id == messageInfo.id }
}
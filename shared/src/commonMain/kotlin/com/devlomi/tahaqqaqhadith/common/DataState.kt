package com.devlomi.tahaqqaqhadith.common

data class DataState<T>(
    val message: GenericMessageInfo? = null,
    val data: T? = null,
    val type: DataStateType,
) {

    fun isLoading() = type === DataStateType.LOADING
    fun isSuccess() = type === DataStateType.SUCCESS
    fun isError() = type === DataStateType.ERROR

    companion object {

        fun <T> error(
            message: GenericMessageInfo,
        ): DataState<T> {
            return DataState(
                message = message,
                type = DataStateType.ERROR,
                data = null,
            )
        }

        fun <T> data(
            message: GenericMessageInfo? = null,
            data: T? = null,
        ): DataState<T> {
            return DataState(
                message = message,
                data = data,
                type = DataStateType.SUCCESS
            )
        }

        fun <T> loading() = DataState<T>(type = DataStateType.LOADING)


    }
}
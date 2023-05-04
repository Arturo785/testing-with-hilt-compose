package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util


// each noteOrder receives either ascending or descending
sealed class NoteOrder(val orderType: OrderType) {

    // so it's a mix of tittle and asc, date and desc, color and asc
    class Title(orderType: OrderType) : NoteOrder(orderType)
    class Date(orderType: OrderType) : NoteOrder(orderType)
    class Color(orderType: OrderType) : NoteOrder(orderType)

    fun copy(orderType: OrderType): NoteOrder {
        return when (this) {
            is Title -> Title(orderType)
            is Date -> Date(orderType)
            is Color -> Color(orderType)
        }
    }
}
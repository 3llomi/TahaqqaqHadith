package com.devlomi.tahaqqaqhadith.common


/**
 * Kotlin version of a java.util Queue
 * https://docs.oracle.com/javase/8/docs/api/java/util/Queue.html
 */
data class Queue<T>(val items: MutableList<T>) {


    fun isEmpty(): Boolean = items.isEmpty()

    fun count(): Int = items.count()

    override fun toString() = items.toString()

    fun add(element: T) {
        items.add(element)
    }

    fun remove(): T? {
        if (this.isEmpty()) {
            return null
        }
        return items.removeAt(0)
    }

    fun remove(item: T): Boolean {
        return items.remove(item)
    }

    @Throws(Exception::class)
    fun element(): T {
        if (this.isEmpty()) {
            throw Exception("fun 'element' threw an exception: Nothing in the queue.")
        }
        return items[0]
    }

    fun offer(element: T): Boolean {
        try {
            items.add(element)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    fun poll(): T? {
        if (this.isEmpty()) return null
        return items.removeAt(0)
    }

    fun peek(): T? {
        if (this.isEmpty()) return null
        return items[0]
    }

    fun addAll(queue: Queue<T>) {
        this.items.addAll(queue.items)
    }

    fun clear() {
        items.removeAll { true }
//        items.clear()
    }

    fun doCopy(): Queue<T> {
        val newList = mutableListOf<T>()
        newList.addAll(newList)
        return Queue(newList)
    }

}
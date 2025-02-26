package pt.isel.pc.hazards

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Counter(private var value: Long = 0) {

    init {

    }
    fun inc() {
        value++
    }
    fun dec() {
        if (value > 0) {
            value--
        }
    }

    fun get() : Long {
        return value
    }
}


// Atomic
class Counter2(private var value: AtomicLong = AtomicLong(0)) {

    fun inc() {
        value.incrementAndGet()
    }
    fun dec() {
        if (value.get() > 0) {
            value.getAndDecrement()
        }
    }

    fun get() : Long {
        return value.get()
    }
}

// locks
class Counter3(private var value: Long = 0) {
    val mutex  = ReentrantLock()

    fun inc() {
        /*mutex.lock()
        value++
        mutex.unlock()*/
        mutex.withLock { value++ }
    }
    fun dec() {
            if (value > 0) {
                value--
            }
    }

    fun get() : Long {
        return value
    }
}


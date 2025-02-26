package pt.isel.pc.hazards

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class CounterTests {
    @Test
    fun `increment counter by multiple threads test`() {
        //val counter = Counter()
        val counter = Counter2() // com atomic


        val NTHREADS = 20
        val NITERS = 1000000

        val threads: MutableList<Thread> =
            mutableListOf()

        repeat(NTHREADS) {
            val t = Thread {
                repeat(NITERS) {
                    counter.inc()
                }
            }
            threads.add(t)
        }

        threads.forEach {
            it.start()
        }
        threads.forEach {
            it.join()
        }

        assertEquals((NTHREADS * NITERS).toLong(), counter.get())
    }


    @Test
    fun `increment counter by multiple threads test, with locks`() {
        val counter = Counter3()

        val NTHREADS = 20
        val NITERS = 1000000

        val threads: MutableList<Thread> =
            mutableListOf()

        repeat(NTHREADS) {
                val t = Thread {
                    repeat(NITERS) {
                        counter.inc()
                    }
                }
                threads.add(t)

        }

        threads.forEach {
            it.start()
        }
        threads.forEach {
            it.join()
        }

        assertEquals((NTHREADS * NITERS).toLong(), counter.get())
    }
}
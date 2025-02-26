package pt.isel.pc

import com.sun.tools.javac.util.Assert
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.test.assertEquals

// Altere o teste captureInACycle de modo a que produzam
// resultados semelhantes ao teste captureOfRepeatIndex.

class CaptureTests {


    // Apresenta os IDs de cada thread criada: 12, 30, 31 etc.
    @Test
    fun captureOfRepeatIndex() {
        val threads: MutableList<Thread> = ArrayList()

        // repeat é thread-safe.
        repeat (100) {
            val t = Thread {
                println(it) // apresenta o id de cada thread criada. 100 threads.
            }
            threads.add(t)
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }

    }

    // Nesta versão o valor é sempre 100, porque o valor x foi alterado no stack de cada Thread.
    // O inteiro 'x' não é thread-safe. ==> TODO: AtomicInteger
    // Nao é garantida atomicidade da operação GET (println(x)) e INCREMENT (x++).
    // TODO: Porque é que é sempre 100 ?
    @Test
    fun captureInACycle() {
        val threads: MutableList<Thread> = ArrayList()
            var x  = 0

            while (x < 100) {
                val newX = ++x
                val t = Thread {
                    println(newX)
                }
                threads.add(t)
            }
            threads.forEach { it.start() }
            threads.forEach { it.join() }
    }
}
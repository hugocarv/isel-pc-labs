package pt.isel.pc.echoclient

import mu.KotlinLogging
import pt.isel.pc.basicthreads.writeLine
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.measureTime

private val logger = KotlinLogging.logger {}

class EchoClient(private val serverAddress: String, private val serverPort : Int) {
    fun contact() : Int {
        var id : Int = 0
        try {
            val socket = Socket()
            socket.use {
                socket.connect(InetSocketAddress(serverAddress, serverPort))
                val reader = socket.getInputStream().bufferedReader()
                val writer = socket.getOutputStream().bufferedWriter()
                reader.use {
                    val line = reader.readLine()
                    val parts = line.split(" ")
                    writer.use {
                        writer.writeLine("exit")
                    }
                    id  = parts[2].toInt()
                }
            }
        }
        catch(e: IOException) {
            println("error on connect:${e.message}, ${e.cause?.message}")
        }
        return id
    }

    fun contactAndSendMessage() : Pair<Int, String> {
        var id : Int = 0
        var lines : String = ""
        try {
            val socket = Socket()
            socket.use {
                socket.connect(InetSocketAddress(serverAddress, serverPort))
                val reader = socket.getInputStream().bufferedReader()
                val writer = socket.getOutputStream().bufferedWriter()

                id = reader.readLine().split(" ").last().toInt()
                //println("id is $id")
                reader.use {
                    writer.writeLine("line1")
                    writer.writeLine("line2")

                    writer.writeLine("exit")
                    reader.use {
                        lines = reader.lines().toList().last().split("TotalLines=").last()
                    }
                    println("lines = $lines")
                }

            }
        }
        catch(e: IOException) {
            println("error on connect:${e.message}, ${e.cause?.message}")
        }
        return Pair(id,lines)
    }
}

fun main() {
    val nclients = 500

    val time = measureTime {
        val ids = ConcurrentHashMap.newKeySet<Int>()
        val lines = ConcurrentHashMap.newKeySet<Int>()

        val threads = mutableListOf<Thread>()
        repeat(nclients) {
            val thread = Thread {
                val client = EchoClient("127.0.0.1", 8000)
                val resPair = client.contactAndSendMessage()
                val res = resPair.first // id
                lines.add(resPair.second.toInt())
                if (!ids.add(res)) {
                    println("$res: duplicated id!")
                }
            }
            threads.add(thread)
            thread.start()
        }
        logger.info("wait for threads!")
        for (t in threads) t.join(5000)

        if (nclients != ids.size) {
            println("nclients=$nclients, ids.size=${ids.size}")
        } else {
            println("all ok!")
            println("nclients=$nclients, ids.size=${ids.size}, linesTotal=${lines.first()}")
        }
    }
    println("done in $time ms!")

}
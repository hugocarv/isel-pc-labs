package pt.isel.pc.echoservers

import mu.KotlinLogging
import pt.isel.pc.basicthreads.writeLine
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger


class SimpleEchoServerMTSempahore(private val port: Int) {

    companion object {
        val EXIT = "exit"
        val BACKLOG = 1024
        private val logger = KotlinLogging.logger {}

    }

    private val serverSocket = ServerSocket()
    // Max number of clients
    private val MAX_CLIENTS = 2

    // Counts all messages sent by all clients
    private var globalEchoCounter = AtomicInteger()


    private fun processConnection(clientSock: Socket) {
        logger.info("client ${clientSock.remoteSocketAddress} connected")
        var clientEchoCounter = 0 // estado privado. Apenas para um cliente em causa.
        val clientId = clientSock.remoteSocketAddress

        try {
            clientSock.use {
                val reader = BufferedReader(InputStreamReader(clientSock.getInputStream()))
                val writer = BufferedWriter(OutputStreamWriter(clientSock.getOutputStream()))
                writer.writeLine("Hello, client $clientId")

                while (true) {
                    val line = reader.readLine() ?: break

                    if (line == EXIT) {
                        writer.writeLine("Bye client $clientId! ClientLines=$clientEchoCounter TotalLines=${globalEchoCounter.get()}")
                        logger.info("Client $clientId has exited.  ClientLines=$clientEchoCounter TotalLines=${globalEchoCounter.get()}")
                        break
                    }
                    globalEchoCounter.incrementAndGet()
                    logger.info("(#${++clientEchoCounter}): line '$line' received")
                    writer.writeLine("You sent: $line")

                }
            }
        }
        catch(e: Exception) {
            logger.error("connection loop error: ${e.message}")
        }
        finally {
            logger.info("connection loop termination")
        }
    }

    private fun sendNiceGoodbyeMessageFullServer(clientSock: Socket) =
        clientSock.use {
            BufferedWriter(OutputStreamWriter(clientSock.getOutputStream()))
                .writeLine("Sorry, but this server is full at the moment! Please try again later.")
        }



    fun run() {
        try {

            serverSocket.use {
                serverSocket.bind(InetSocketAddress("0.0.0.0", port), BACKLOG)
                logger.info("waiting for client connections")
                val clientSemaphore = Semaphore(MAX_CLIENTS)



                while (true) {
                    val clientSock: Socket = serverSocket.accept()

                    if(clientSemaphore.tryAcquire()) {
                    //if(clientCount.get() < MAX_CLIENTS) {
                        logger.debug("New client arrived.")

                        Thread {
                            processConnection(clientSock)
                            clientSemaphore.release()
                            logger.info("Client exited.")
                        }.start()

                    }

                    else {
                        logger.warn("Wait! Queue is full! MAX_CLIENTS=${MAX_CLIENTS}")
                        /** OPTION 1 **/
                        // We can just not accept the remote client and close the connection....
                        //clientSock.close()

                        /** OPTION 2 **/
                        // Or we can send a message to them, telling that its full and to wait a little bit...
                        logger.info("Sending message to client")
                        sendNiceGoodbyeMessageFullServer(clientSock)
                    }





                }

        }

        }
        catch(e: Exception) {
            logger.info("accept loop error: ${e.message}")
        }
        finally {
            logger.info("accept loop termination")
        }
    }

    fun close() {
        serverSocket.close()
    }
}

fun main() {
    SimpleEchoServerMTSempahore(8000).run()
}
Aula 18/03/2025
-----------------

Últuma aula: Monitores


## Semáforo:

- `if (tryAcquire()) return` - verfica estado de sincronização.
- `await()` bloqueia operaçao
- imagem/esquema de funcionamento de Threads: 
    - Threads entram, ficam da espera, pq pode haver outras threads que entraram primeiro. ultrapassado por outras threads.
    - **Barging**" - therads poderem sofrer de um problema: "barging", ou seja uma thread atropelar / passar a fila.
- Implementação incompleta. É preciso dar suporte a: 
    -   **timeout:** operações de bloqueio devem suportar timeout (tempo max) - tempo maximo de espera de conclusao da operação; 
        - Várias variantes de await com timeout. Ex: condition.awair(TIMEOUT; UNIT)
        - unit - segundos, ms, etc. e depois converter o tempo para essa unidade.
        - Timeout - Este timeout também pode ser infinito (Duration.INFINITE)
            - converter Duration em *ms*: 
                    ` var d = Timeout.isMiliseconds...`
        - se não for possível resolver (timeout chega ao fim), desistir de operação; 
        - **Retorno:**  awaitNanos(...) - recebe um e retorna o tempo que falta; útil se tivermos de executar novamente, passar esse valor.

    

### Cancelamento
- **Timeout é uma forma de cancelamento.**
- Outra forma é que este cancelamento seja **promovido por alguém.**
- Na JVM o conceito de cancelamento é implementado pela interrupção da Thread: **interrupt()**
- interromper thread: significa eleminar de imeadiato thread, ou reagir a essa interrupção:  
    - **Thread.currentThread().interupt()** - retorna bool.
    - **isInterruptable()**: 
                ``if(ThThread.currentThread().interruptable()) {
                    throw interruptException()
                }``

### Bloquear numa condição:
- Sensiveis ás interrupções das threads atrav+es do lançamento da exceção **InterruptException()**
- Em Kotlin deve-se usar explicitamente 
    @Throws(InterruptedException::class)
- Colocar dentro da função um try-catch(InterruptedException).
  -  `` catch(e: InterruptedException) { 
        throw e // propagar a exceção
    }``
        - **problema:** se deixar propagar a exeção, se mais alguem estiver bloqueado na fila, vai terminar para todos. 
        - **solução:**  Antes de sair de operação de cancelamento, informar as operações q não foram entregues a mim, sinalizar.
    
          ``} catch(e: InterruptedException) { 
         if(permits>0) hasPermites.signalAll() // registar sinalização só se permites for >0.
    }``     
        

- Os dois primeiros exercícios da serie só precisam de monitores e lidar com timeouts.

----

## SemaphoreNary:
 - No primeiro Semaphore os accquires sao unicos.
 - Para o N-ary - faz sentido ter o signal() (desbloqueia 1 thread) ?
    - Não. solução é que todas acordem: **sinalização em broadcast --> signalAll()**
- Esta implementação não é justa (*"fair"*)
- **Implementação Justa: Pedidos serem atendidos por ordem de cehgada.**
- O semaforo para ser justo tinha de garantir a ordem. 

 
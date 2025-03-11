PC - 25/02/2025 - aula 3
------------

## server
- variavel client. para identificar sessao. --> ++clientId.
- problema: não é thread-safe.
- Caso cheguem muitos pedidos, pode haver id's duplicados.
- cenario em que a variavel é partilhada pelas varias threadas, para consultar e eventualmente escrever.
não é op. atomica - nao acontece tudo de uma vez (3 passos)

- Problema de Lost-Update.
    - duas threadas, uma atualizva variavel, e o outra atualiza e fica 4 na mesma.

- thread-safe: corre concorrentemente ..

- newKeySet é thread-safe (Set).

- server.close() - fecha  o socket do srevidor.

- `processConnection(clientSock, newClientId)`
    - antes de chegar a este ponto, clientId pode ser alterado. no fim pode nao ter nada a ver com o valor.
problema de valor duplocado.
    - como resolver? ++clientID
    - Criar variavel local na thread: val newClientId = ++clientId

    - um stack por cada thread, alem da thread principal da aplicação.
    - Heap, variavel partilhada entre stacks das threads ?



-----

**Thread-safety:  CONFINADA, IMUTABLE, SINCRONIZADA**

- **CONFINADA:**
a variavel newclientId está confinada aquela thread.
é uma das formas para resolver o problema.


- **IMUTABLE:**
    - Dados imitaveis - nao poder alterar variavel?
    - Desvantagem, nem sempre serve pq podemos quuerer alterar dados

- **SINCRONIZAÇÃO:**
    - Utilização de objetos (sincronizadores) que permitem utilização sincronizada de acesso a dados.
    - Dois tipos de sincronização: 
        - **DADOS** (para dados que são partilhados)
            ex: variavel so pode ser acedido por uma thread de cada vez;
        - **CONTROLO** - orquestração de threads
            ex: uma thread so pode ser feita depois de outra.


### Hazards: Counter.kt


Counter - não é thread safe.
- resolver: Sincronização  - codigo so pode ser executado numa trhrad de cada vez.
 lock
 Embrulhar troço de codigo no lock
 As outras trhrads ficam bloqueadas até aquela terminar.
Garantir acesso exclusivo.

locker.lock()
...troço de codigo...
locker.unlock()

variavel 
private val mutex = ReentrantLock()
este sincronizador chama.se ReentrantLock.
(Todos estes locks implementam a interface Lock e Unlock)

transformou mima op trhread safe.
```
    fun inc() {
        mutex.lock()
        value++
        mutex.unlock()
    }
```

pode criar problemas se houver exceções, e o lock nunca ser libertado.


Outra forma, apanhar exceções:
mutex.lock()
try {...} finnaly {mutex.unlock() }

alternativa:
mutex.withLock {        value++  }

faz o mesmo que o try.. finnally.

tryLock - tem conceito de timeout:
- limite de tempo, e se passado o qual não terminar, encerrar.

Threads podem ser interrompidas.
mutex.lock() não é interuptivel.



Outra forma: Garantir que função é executada atomicamente.
 - AtomicInteger(), AtomicLong(), etc.
 private val value = AtomicLong(value)

 fun inc() { value.incrementAndGet()}
fun dec() { value.decrementAndGet()}


value.getAndDecrement() não é thread safe:
 - 
 cada operação é atomica, mas as duas operaç~eos seguidas nao sao?
 observar e atualizar:
   if (value.get() > 0) {
            value.getAndDecrement()
        }

decremento é, mas leitura não é atomico.

---


AccountTests - varias variantes.
## transferTo0:

- teste stress entre varias accounnts: NACCOUNTS = 16

- funcao aux - multipleTransfersBetweenAccounts:
    - executar transferencias , duas threads de uma conta para outra, e depois as threadas 3 e 4 entre duas.
    - 2 threads para cada par de transferencias.
    - no final, o saldo deve ser igual.

teste0:
- uma conta ficou com 2100. test fail.

teste1:
- ter um lock global usado por todas as operações (globalMutex).
- estado partilhado por todos os objetos - companion object.
- 

test2:
- lock global e lock da account.
- solucao alternative: lock global e um lock para cada account.
- lock account1 e lock account2.

```
a1.lock()
a2.lock()
... operations ...
a1.unlock()
a2.unlock()
```


## transferTo2:
 - nunca vai terminar -> dead-lock!
- dead-lock  ocorre quando ha transferencias de A para B e de B para A.
- as threads estao a espera uma da outra.


## transferTo3: 
 - criar relação de ordem como ordenação dos locks.
 - adquirir os locks de ordem menor e depois os maiores.
feito atravez do hashCode.
 - lock1 fica com o menor dos dois, o lock2 fica com o maior dos dois.


---

aula lab1:
1. CaptureTests
justificar os outputs de cada um dos testes.




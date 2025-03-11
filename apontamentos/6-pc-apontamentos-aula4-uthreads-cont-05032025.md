PC - 25/03/2025 - aula 4
------------

## uThreads - (Continuação)

- Contexto guardado no objeto da Thread - no topo do Stack
- No caso da thread do SO, o objeto Thread aponta para a função *main*.
- Guardar rsp no context, e colocar no rsp o endereço do contexto da thread B. RSP apontava para a Thread A, agora aponta para a Thread B.

uthread_t é composto por:
 - _running_thread_ 
 - entry_t _ready_queue_ - lista de threads. Inserção á cauda. Lista não intrusiva
 - _ut_yield()_ - inicia thread.
 - _ut_run()_ - guarda a thread de sistema para no final repor.
 - _schedule()_ - executa thread.

 Lista intrusiva: cria nós que contêm objetos. Objetos representam threads. Vantagens: remoção unitária.

## Kotlin

Tipos de sincronização:
- Sincronização de Threads
- Sincronização de dados
    - Dados locais
    - Operações atómicas
- **Sincronização de controlo** (new)
    - Informa outras threads quando parar dependendo do estado da thread atual

Alterar servidor Echo:
- Não aceitar mais do que 2 clientes (_MAX_CLIENTS_)
- Bloquear. Não fazer mais _accept()_ enquanto não tiver espaço para clientes
- Solução: Sincronização de controlo ==> **Semáforo**
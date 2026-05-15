package com.novabank.account.event;

import com.novabank.account.dto.TransactionDTO;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class TransactionEventBus {

    private final Sinks.Many<TransactionDTO> sink = Sinks.many()
            .multicast()
            .onBackpressureBuffer();

    public void publish(TransactionDTO transaction) {
        sink.tryEmitNext(transaction);
    }

    public Flux<TransactionDTO> streamByAccount(Long accountId) {
        return sink.asFlux()
                .filter(t -> t.accountId().equals(accountId));
    }
}

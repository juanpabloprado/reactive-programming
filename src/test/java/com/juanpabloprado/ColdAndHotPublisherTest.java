package com.juanpabloprado;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static com.juanpabloprado.util.CommonUtil.delay;

public class ColdAndHotPublisherTest {
    @Test
    void coldPublisherTest() {
        Flux<Integer> flux = Flux.range(1, 10);

        flux.subscribe(i -> System.out.println("Subscriber 1: " + i));
        flux.subscribe(i -> System.out.println("Subscriber 2: " + i));
    }

    @Test
    void hotPublisherTest() {
        Flux<Integer> flux = Flux.range(1, 10).delayElements(Duration.ofSeconds(1));

        ConnectableFlux<Integer> connectableFlux = flux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe(i -> System.out.println("Subscriber 1: " + i));
        delay(4_000);
        connectableFlux.subscribe(i -> System.out.println("Subscriber 2: " + i));
        delay(10_000);
    }

    @Test
    void hotPublisherTestAutoConnect() {
        Flux<Integer> flux = Flux.range(1, 10).delayElements(Duration.ofSeconds(1));

        Flux<Integer> hotSource = flux.publish().autoConnect(2);

        hotSource.subscribe(i -> System.out.println("Subscriber 1: " + i));
        delay(2_000);
        hotSource.subscribe(i -> System.out.println("Subscriber 2: " + i));
        System.out.println("Two subscribers connected");
        delay(2_000);
        hotSource.subscribe(i -> System.out.println("Subscriber 3: " + i));
        delay(10_000);
    }

    @Test
    void hotPublisherTestRefCount() {
        Flux<Integer> flux = Flux.range(1, 10).delayElements(Duration.ofSeconds(1));

        Flux<Integer> hotSource = flux.publish().refCount(2)
                .doOnCancel(() -> System.out.println("Received cancel"));

        var disposable = hotSource.subscribe(i -> System.out.println("Subscriber 1: " + i));
        delay(2_000);
        var disposable2 = hotSource.subscribe(i -> System.out.println("Subscriber 2: " + i));
        System.out.println("Two subscribers connected");
        delay(2_000);
        disposable.dispose();
        disposable2.dispose();
        hotSource.subscribe(i -> System.out.println("Subscriber 3: " + i));
        delay(2_000);
        hotSource.subscribe(i -> System.out.println("Subscriber 4: " + i));
        delay(10_000);
    }
}

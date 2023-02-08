package com.juanpabloprado;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class BackpressureTest {

    @Test
    void testBackPressure() {
        Flux<Integer> range = Flux.range(1, 100).log();

        range
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
//                        super.hookOnNext(value);
                        log.info("hookOnNext: {}", value);
                        if(value == 2) {
                            cancel();
                        }
                    }

                    @Override
                    protected void hookOnComplete() {
//                        super.hookOnComplete();
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
//                        super.hookOnError(throwable);
                    }

                    @Override
                    protected void hookOnCancel() {
//                        super.hookOnCancel();
                        log.info("hookOnCancel");
                    }
                });
//                .subscribe(n -> log.info("Number is: {}", n));
    }

    @Test
    void testBackPressure2() throws InterruptedException {
        Flux<Integer> range = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        range
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
//                        super.hookOnNext(value);
                        log.info("hookOnNext: {}", value);
                        if(value % 2 == 0 || value < 50) {
                            request(2);
                        } else {
                            cancel();
                        }
                    }

                    @Override
                    protected void hookOnComplete() {
//                        super.hookOnComplete();
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
//                        super.hookOnError(throwable);
                    }

                    @Override
                    protected void hookOnCancel() {
//                        super.hookOnCancel();
                        log.info("hookOnCancel");
                        latch.countDown();
                    }
                });
//                .subscribe(n -> log.info("Number is: {}", n));
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    void testBackPressureDrop() throws InterruptedException {
        Flux<Integer> range = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        range
                .onBackpressureDrop(item -> log.info("Dropping item: {}", item))
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
//                        super.hookOnNext(value);
                        log.info("hookOnNext: {}", value);
//                        if(value % 2 == 0 || value < 50) {
//                            request(2);
//                        } else {
//                            cancel();
//                        }
                        if(value == 2) {
                            hookOnCancel();
                        }
                    }

                    @Override
                    protected void hookOnComplete() {
//                        super.hookOnComplete();
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
//                        super.hookOnError(throwable);
                    }

                    @Override
                    protected void hookOnCancel() {
//                        super.hookOnCancel();
                        log.info("hookOnCancel");
                        latch.countDown();
                    }
                });
//                .subscribe(n -> log.info("Number is: {}", n));
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    void testBackPressureBuffer() throws InterruptedException {
        Flux<Integer> range = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        range
                .onBackpressureBuffer(10, i -> log.info("Last buffered element is: {}", i))
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
//                        super.hookOnNext(value);
                        log.info("hookOnNext: {}", value);
                        if(value < 50) {
                            request(1);
                        } else {
                            hookOnCancel();
                        }
                    }

                    @Override
                    protected void hookOnComplete() {
//                        super.hookOnComplete();
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
//                        super.hookOnError(throwable);
                    }

                    @Override
                    protected void hookOnCancel() {
//                        super.hookOnCancel();
                        log.info("hookOnCancel");
                        latch.countDown();
                    }
                });
//                .subscribe(n -> log.info("Number is: {}", n));
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    void testBackPressureError() throws InterruptedException {
        Flux<Integer> range = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        range
                .onBackpressureError()
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
//                        super.hookOnNext(value);
                        log.info("hookOnNext: {}", value);
                        if(value < 50) {
                            request(1);
                        } else {
                            hookOnCancel();
                        }
                    }

                    @Override
                    protected void hookOnComplete() {
//                        super.hookOnComplete();
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
//                        super.hookOnError(throwable);
                        log.error("hookOnError | Exception is: ", throwable);
                    }

                    @Override
                    protected void hookOnCancel() {
//                        super.hookOnCancel();
                        log.info("hookOnCancel");
                        latch.countDown();
                    }
                });
//                .subscribe(n -> log.info("Number is: {}", n));
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}

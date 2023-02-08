package com.juanpabloprado.flux;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class FluxAndMonoSchedulersServiceTest {

    FluxAndMonoSchedulersService fluxAndMonoSchedulersService = new FluxAndMonoSchedulersService();

    @Test
    void explorePublishOn() {
        var flux = fluxAndMonoSchedulersService.explorePublishOn().log();

        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void exploreSubscribeOn() {
        var flux = fluxAndMonoSchedulersService.exploreSubscribeOn().log();

        StepVerifier.create(flux)
               .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void exploreParallel() {
        var flux = fluxAndMonoSchedulersService.exploreParallel().log();

        StepVerifier.create(flux)
              .expectNextCount(3)
              .verifyComplete();
    }

    @Test
    void exploreParallelUsingFlatMap() {
        var flux = fluxAndMonoSchedulersService.exploreParallelUsingFlatMap().log();

        StepVerifier.create(flux)
              .expectNextCount(3)
              .verifyComplete();
    }

    @Test
    void exploreParallelUsingFlatMap2() {
        var flux = fluxAndMonoSchedulersService.exploreParallelUsingFlatMap2().log();

        StepVerifier.create(flux)
             .expectNextCount(6)
             .verifyComplete();
    }

    @Test
    void exploreParallelUsingFlatMapSequential() {
        var flux = fluxAndMonoSchedulersService.exploreParallelUsingFlatMapSequential().log();

        StepVerifier.create(flux)
             .expectNext("ALEX", "BEN", "CHLOE")
             .verifyComplete();
    }
}
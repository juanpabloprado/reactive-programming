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
}
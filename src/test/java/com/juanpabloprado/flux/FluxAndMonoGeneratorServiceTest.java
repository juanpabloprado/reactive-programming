package com.juanpabloprado.flux;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        var names = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(names)
//                .expectNext("Juan", "Pablo", "Alex")
//                .expectNextCount(3)
                .expectNext("Juan")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void nameMono() {
        var name = fluxAndMonoGeneratorService.nameMono();

        StepVerifier.create(name)
//                .expectNext("Juan")
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void namesFluxMap() {
        int stringLength = 4;

        var names = fluxAndMonoGeneratorService.namesFluxMap(stringLength);

        StepVerifier.create(names)
                .expectNext("5-PABLO")
                .verifyComplete();
    }

    @Test
    void nameMonoMapFilter() {
        var name = fluxAndMonoGeneratorService.namesMonoMapFilter(3);

        StepVerifier.create(name)
                .expectNext("ALEX")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMap() {
        int stringLength = 3;

        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMap(stringLength);

        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMapAsync() {
        int stringLength = 3;

        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMapAsync(stringLength);

        StepVerifier.create(namesFlux)
//                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFluxConcatMap() {
        int stringLength = 3;

        var namesFlux = fluxAndMonoGeneratorService.namesFluxConcatMap(stringLength);

        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
//                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMap() {
        int stringLength = 3;

        var name = fluxAndMonoGeneratorService.namesMonoFlatMap(stringLength);

        StepVerifier.create(name)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMapMany() {
        int stringLength = 3;

        var name = fluxAndMonoGeneratorService.namesMonoFlatMapMany(stringLength);

        StepVerifier.create(name)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFluxTransform() {
        int stringLength = 3;

        var names = fluxAndMonoGeneratorService.namesFluxTransform(stringLength);

        StepVerifier.create(names)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFluxTransformSwitchIfEmpty() {
        int stringLength = 6;

        var names = fluxAndMonoGeneratorService.namesFluxTransformSwitchIfEmpty(stringLength);

        StepVerifier.create(names)
//                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void nameMonoMapFilterDefault() {
        var name = fluxAndMonoGeneratorService.namesMonoMapFilter(4);

        StepVerifier.create(name)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void nameMonoMapFilterSwitchIfEmpty() {
        var name = fluxAndMonoGeneratorService.namesMonoMapFilterSwitchIfEmpty(4);

        StepVerifier.create(name)
                .expectNext("DEFAULT")
                .verifyComplete();
    }

    @Test
    void exploreConcat() {
        var concatFlux = fluxAndMonoGeneratorService.exploreConcat();

        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreConcatWith() {
        var concatFlux = fluxAndMonoGeneratorService.exploreConcatWith();

        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreConcatWithMono() {
        var concatFlux = fluxAndMonoGeneratorService.exploreConcatWithMono();

        StepVerifier.create(concatFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void exploreMerge() {
        var mergeFlux = fluxAndMonoGeneratorService.exploreMerge();

        StepVerifier.create(mergeFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void exploreMergeWith() {
        var mergeFlux = fluxAndMonoGeneratorService.exploreMergeWith();

        StepVerifier.create(mergeFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void exploreMergeWithMono() {
        var mergeFlux = fluxAndMonoGeneratorService.exploreMergeWithMono();

        StepVerifier.create(mergeFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void exploreMergeSequential() {
        var mergeFlux = fluxAndMonoGeneratorService.exploreMergeSequential();

        StepVerifier.create(mergeFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreZip() {
        var zipFlux = fluxAndMonoGeneratorService.exploreZip();

        StepVerifier.create(zipFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void exploreZip2() {
        var zipFlux = fluxAndMonoGeneratorService.exploreZip2();

        StepVerifier.create(zipFlux)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();
    }

    @Test
    void exploreZipWith() {
        var zipFlux = fluxAndMonoGeneratorService.exploreZipWith();

        StepVerifier.create(zipFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void exploreZipWithMono() {
        var zipFlux = fluxAndMonoGeneratorService.exploreZipWithMono();

        StepVerifier.create(zipFlux)
                .expectNext("AB")
                .verifyComplete();
    }
}
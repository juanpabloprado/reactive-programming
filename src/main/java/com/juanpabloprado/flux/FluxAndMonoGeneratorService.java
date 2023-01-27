package com.juanpabloprado.flux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {
    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("Juan", "Pablo", "Alex")).log(); // db or a remote service call
    }

    public Mono<String> nameMono() {
        return Mono.just("Juan").log();
    }

    public Flux<String> namesFluxMap(int stringLength) {
        return Flux.fromIterable(List.of("Juan", "Pablo", "Alex"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .map(s -> s.length() + "-" + s)
                .doOnNext(name -> System.out.println("Name is: " + name.toLowerCase()))
                .doOnSubscribe(s -> System.out.println("Subscription is: " + s))
                .doOnComplete(() -> System.out.println("Inside the complete callback"))
                .doFinally(signalType -> System.out.println("Inside doFinally: " + signalType))
                .log(); // db or a remote service call
    }

    public Mono<String> namesMonoMapFilter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .defaultIfEmpty("default")
                .log();
    }

    public Mono<List<String>> namesMonoFlatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono) // Mono<List of A, L, E, X>
                .log();
    }

    public Flux<String> namesMonoFlatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString) // A, L, E, X
                .log();
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray); // ALEX -> A, L, E, X
        return Mono.just(charList);
    }

    public Flux<String> namesFluxFlatMap(int stringLength) {
        return Flux.fromIterable(List.of("Alex", "Ben", "Chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .flatMap(this::splitString) // A, L, E, X, C, H, L, O, E
                .log(); // db or a remote service call
    }

    public Flux<String> namesFluxFlatMapAsync(int stringLength) {
        return Flux.fromIterable(List.of("Alex", "Ben", "Chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .flatMap(this::splitStringWithDelay) // A, L, E, X, C, H, L, O, E
                .log(); // db or a remote service call
    }

    public Flux<String> namesFluxConcatMap(int stringLength) {
        return Flux.fromIterable(List.of("Alex", "Ben", "Chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .concatMap(this::splitStringWithDelay) // A, L, E, X, C, H, L, O, E
                .log(); // db or a remote service call
    }

    public Flux<String> namesFluxTransform(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        // Flux.empty()
        return Flux.fromIterable(List.of("Alex", "Ben", "Chloe"))
                .transform(filterMap)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .flatMap(this::splitString) // A, L, E, X, C, H, L, O, E
                .defaultIfEmpty("default")
                .log(); // db or a remote service call
    }

    public Flux<String> namesFluxTransformSwitchIfEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                // ALEX, CHLOE -> A, L, E, X, C, H, L, O, E
                .flatMap(this::splitString); // A, L, E, X, C, H, L, O, E

        Flux<String> defaultFlux = Flux.just("default")
                .transform(filterMap); // "D", "E", "F", "A", "U", "L", "T"

        // Flux.empty()
        return Flux.fromIterable(List.of("Alex", "Ben", "Chloe"))
                .transform(filterMap)
                .switchIfEmpty(defaultFlux)
                .log(); // db or a remote service call
    }

    public Mono<String> namesMonoMapFilterSwitchIfEmpty(int stringLength) {
        Function<Mono<String>, Mono<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        var defaultMono = Mono.just("default").transform(filterMap);

        return Mono.just("alex")
                .transform(filterMap)
                .switchIfEmpty(defaultMono)
                .log();
    }

    public Flux<String> exploreConcat() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> exploreConcatWith() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> exploreConcatWithMono() {
        var aMono = Mono.just("A");
        var bMono = Flux.just("B");

        return aMono.concatWith(bMono).log(); // A, B
    }

    public Flux<String> exploreMerge() {
        var abcFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(125));

        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> exploreMergeWith() {
        var abcFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(125));

        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> exploreMergeWithMono() {
        var aMono = Mono.just("A").delayElement(Duration.ofMillis(100));
        var bMono = Mono.just("B").delayElement(Duration.ofMillis(125));

        return aMono.mergeWith(bMono).log();
    }

    public Flux<String> exploreMergeSequential() {
        var abcFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(abcFlux, defFlux).log();
    }

    public Flux<String> exploreZip() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return Flux.zip(abcFlux, defFlux, (s, s2) -> s + s2).log();
    }

    public Flux<String> exploreZip2() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        var _123Flux = Flux.just("1", "2", "3");
        var _456Flux = Flux.just("4", "5", "6");

        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4()).log();
    }

    public Flux<String> exploreZipWith() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.zipWith(defFlux, (s, s2) -> s + s2).log();
    }

    public Mono<String> exploreZipWithMono() {
        var aMono = Mono.just("A").delayElement(Duration.ofMillis(100));
        var bMono = Mono.just("B").delayElement(Duration.ofMillis(125));

        return aMono.zipWith(bMono, (s, s2) -> s + s2).log();
    }

    public Flux<String> splitString(String name) {
        String[] charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Flux<String> splitStringWithDelay(String name) {
        String[] charArray = name.split("");
//        int delay = new Random().nextInt(1000);
        int delay = 1000;
        return Flux.fromArray(charArray).delayElements(Duration.ofMillis(delay));
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux().subscribe(System.out::println);
        fluxAndMonoGeneratorService.nameMono().subscribe(System.out::println);
    }
}

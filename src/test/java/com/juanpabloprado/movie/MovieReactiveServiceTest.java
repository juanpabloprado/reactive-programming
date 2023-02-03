package com.juanpabloprado.movie;

import com.juanpabloprado.domain.Movie;
import com.juanpabloprado.revenue.RevenueService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
class MovieReactiveServiceTest {
    @Inject
    private RevenueService revenueService;
    @Inject
    private MovieReactiveService movieReactiveService;

    @Test
    void getAllMovies() {

        Flux<Movie> movieFlux = movieReactiveService.getAllMovies();

        StepVerifier.create(movieFlux)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(movie.getReviewList().size(), 2);

                })
                .assertNext(movie -> {
                    assertEquals("The Dark Knight", movie.getMovieInfo().getName());
                    assertEquals(movie.getReviewList().size(), 2);
                })
                .assertNext(movie -> {
                    assertEquals("Dark Knight Rises", movie.getMovieInfo().getName());
                    assertEquals(movie.getReviewList().size(), 2);
                })
                .verifyComplete();
    }

    @Test
    void getMovieById() {
        var movieMono = movieReactiveService.getMovieById(100L).log();

        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(movie.getReviewList().size(), 2);
                }).verifyComplete();
    }

    @Test
    void getMovieByIdUsingFlatMap() {
        var movieMono = movieReactiveService.getMovieByIdUsingFlatMap(1L).log();

        StepVerifier.create(movieMono)
                .assertNext(movieInfo -> {
                    assertEquals("Batman Begins", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                })
                .verifyComplete();
    }

    @Test
    void getMovieByIdWithRevenue() {
        long movieId = 100L;

        var movieMono = movieReactiveService.getMovieByIdWithRevenue(movieId).log();

        StepVerifier.create(movieMono)
              .assertNext(movie -> {
                  assertEquals("Batman Begins", movie.getMovieInfo().getName());
                  assertEquals(movie.getReviewList().size(), 2);
                  assertNotNull(movie.getRevenue());
              }).verifyComplete();
    }
}
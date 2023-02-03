package com.juanpabloprado.movie;

import com.juanpabloprado.domain.Movie;
import com.juanpabloprado.domain.MovieInfo;
import com.juanpabloprado.domain.Review;
import com.juanpabloprado.exception.MovieException;
import com.juanpabloprado.exception.NetworkException;
import com.juanpabloprado.exception.ServiceException;
import com.juanpabloprado.review.ReviewService;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.List;

@Slf4j
@Singleton
public class MovieReactiveService {

    private final MovieInfoService movieInfoService;
    private final ReviewService reviewService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public Flux<Movie> getAllMovies() {

        var movieInfoFlux = movieInfoService.retrieveMovies();
        return getMovieFlux(movieInfoFlux);
    }

    public Flux<Movie> getAllMoviesRetry() {

        var movieInfoFlux = movieInfoService.retrieveMovies();
        return getMovieFlux(movieInfoFlux).retry(3);
    }

    public Flux<Movie> getAllMoviesRetryWhen() {
        var movieInfoFlux = movieInfoService.retrieveMovies();
        return getMovieFlux(movieInfoFlux).retryWhen(getRetryBackoffSpec());
    }

    public Flux<Movie> getAllMoviesRepeat() {
        var movieInfoFlux = movieInfoService.retrieveMovies();
        return getMovieFlux(movieInfoFlux).retryWhen(getRetryBackoffSpec()).repeat();
    }

    public Flux<Movie> getAllMoviesRepeat(long n) {
        var movieInfoFlux = movieInfoService.retrieveMovies();
        return getMovieFlux(movieInfoFlux).retryWhen(getRetryBackoffSpec()).repeat(n);
    }

    private static RetryBackoffSpec getRetryBackoffSpec() {
        return Retry.fixedDelay(3, Duration.ofMillis(500))
                .filter(t -> t instanceof MovieException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()));
    }

    private Flux<Movie> getMovieFlux(Flux<MovieInfo> movieInfoFlux) {
        return movieInfoFlux
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviews(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono.map(reviewList -> new Movie(movieInfo, reviewList));
                }).onErrorMap(t -> {
                    log.error("Exception is: ", t);
                    if (t instanceof NetworkException) {
                        throw new MovieException(t.getMessage());
                    } else {
                        throw new ServiceException(t.getMessage());
                    }
                });
    }

    public Mono<Movie> getMovieById(Long movieId) {

        var movieInfoMono = movieInfoService.retrieveMovieInfoById(movieId);
        var reviewsFlux = reviewService.retrieveReviews(movieId).collectList();

        return movieInfoMono.zipWith(reviewsFlux, Movie::new);
    }

    public Mono<Movie> getMovieByIdUsingFlatMap(long movieId) {

        var movieInfoMono = movieInfoService.retrieveMovieInfoById(movieId);
        return movieInfoMono
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviews(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono.map(movieList -> new Movie(movieInfo, movieList));
                });
    }

}
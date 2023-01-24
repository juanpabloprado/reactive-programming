package com.juanpabloprado.movie;

import com.juanpabloprado.domain.Movie;
import com.juanpabloprado.domain.Review;
import com.juanpabloprado.review.ReviewService;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

        return movieInfoFlux
                .flatMap((movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviews(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono.map(reviewList -> new Movie(movieInfo, reviewList));
                }));
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
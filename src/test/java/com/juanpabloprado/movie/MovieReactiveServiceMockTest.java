package com.juanpabloprado.movie;

import com.juanpabloprado.exception.MovieException;
import com.juanpabloprado.exception.NetworkException;
import com.juanpabloprado.exception.ServiceException;
import com.juanpabloprado.review.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MovieReactiveServiceMockTest {

    @Mock
    MovieInfoService movieInfoService;
    @Mock
    ReviewService reviewService;

    @InjectMocks
    MovieReactiveService movieReactiveService;

    @Test
    void getAllMovies() {
        Mockito.when(movieInfoService.retrieveMovies()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviews(anyLong())).thenCallRealMethod();

        var moviesFlux = movieReactiveService.getAllMovies().log();

        StepVerifier.create(moviesFlux).expectNextCount(3).verifyComplete();
    }

    @Test
    void getAllMovies2() {
        var errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMovies()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviews(anyLong())).thenThrow(new RuntimeException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMovies().log();

        StepVerifier.create(moviesFlux)
//                .expectError(MovieException.class)
                .expectErrorMessage(errorMessage)
                .verify();
    }

    @Test
    void getAllMoviesRetry() {
        var errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMovies()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviews(anyLong())).thenThrow(new RuntimeException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMoviesRetry().log();

        StepVerifier.create(moviesFlux)
//                .expectError(MovieException.class)
                .expectErrorMessage(errorMessage)
                .verify();

        verify(reviewService, times(4)).retrieveReviews(isA(Long.class));
    }

    @Test
    void getAllMoviesRetryWhen() {
        var errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMovies()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviews(anyLong())).thenThrow(new NetworkException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMoviesRetryWhen().log();

        StepVerifier.create(moviesFlux)
//                .expectError(MovieException.class)
                .expectErrorMessage(errorMessage)
                .verify();

        verify(reviewService, times(4)).retrieveReviews(isA(Long.class));
    }

    @Test
    void getAllMoviesRetryWhen2() {
        var errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMovies()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviews(anyLong())).thenThrow(new ServiceException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMoviesRetryWhen().log();

        StepVerifier.create(moviesFlux)
//                .expectError(MovieException.class)
                .expectErrorMessage(errorMessage)
                .verify();

        verify(reviewService, times(1)).retrieveReviews(isA(Long.class));
    }

    @Test
    void getAllMoviesRepeat() {
        Mockito.when(movieInfoService.retrieveMovies()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviews(anyLong())).thenCallRealMethod();

        var moviesFlux = movieReactiveService.getAllMoviesRepeat().log();

        StepVerifier.create(moviesFlux)
                .expectNextCount(6)
                .thenCancel()
                .verify();

        verify(reviewService, times(6)).retrieveReviews(isA(Long.class));
    }

    @Test
    void getAllMoviesRepeatN() {
        Mockito.when(movieInfoService.retrieveMovies()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviews(anyLong())).thenCallRealMethod();
        var n = 2L;

        var moviesFlux = movieReactiveService.getAllMoviesRepeat(n).log();

        StepVerifier.create(moviesFlux)
                .expectNextCount(9)
                .verifyComplete();

        verify(reviewService, times(9)).retrieveReviews(isA(Long.class));
    }
}
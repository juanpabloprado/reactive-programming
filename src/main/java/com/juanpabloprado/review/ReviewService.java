package com.juanpabloprado.review;

import com.juanpabloprado.domain.Review;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;

import java.util.List;

@Singleton
public class ReviewService {

    public  List<Review> retrieveReviewsList(long movieInfoId){

        return List.of(new Review(1L, movieInfoId, "Awesome Movie", 8.9),
                new Review(2L, movieInfoId, "Excellent Movie", 9.0));
    }

    public Flux<Review> retrieveReviews(long movieInfoId){

        var reviewsList = List.of(new Review(1L,movieInfoId, "Awesome Movie", 8.9),
                new Review(2L, movieInfoId, "Excellent Movie", 9.0));
        return Flux.fromIterable(reviewsList);
    }


}
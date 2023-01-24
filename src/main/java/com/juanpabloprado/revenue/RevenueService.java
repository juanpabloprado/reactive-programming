package com.juanpabloprado.revenue;


import com.juanpabloprado.domain.Revenue;
import jakarta.inject.Singleton;

import static com.juanpabloprado.util.CommonUtil.delay;

@Singleton
public class RevenueService {

    public Revenue getRevenue(Long movieId){
        delay(1000); // simulating a network call ( DB or Rest call)
        return Revenue.builder()
                .movieInfoId(movieId)
                .budget(1000000)
                .boxOffice(5000000)
                .build();

    }
}
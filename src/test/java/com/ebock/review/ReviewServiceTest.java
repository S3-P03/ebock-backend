package com.ebock.review;

import com.ebock.dto.response.review.AverageReviewResponse;
import com.ebock.dto.response.review.ReviewDetailsResponse;
import com.ebock.mapper.ReviewMapper;
import com.ebock.mapper.UserMapper;
import com.ebock.service.ReviewService;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    UserMapper userMapper;
    @Mock
    ReviewMapper reviewMapper;
    @InjectMocks
    ReviewService reviewService;

    @Test
    void reviewAvg_ThrowsNotFound_InexistentCip(){
        //arrange
        String inexistentCip = "abcd1234";
        when(userMapper.getUserCountByCip(inexistentCip)).thenReturn(0);

        //act and assert
        assertThrows(NotFoundException.class, () -> {
            reviewService.cipAverageReview(inexistentCip);
        });
    }

    @Test
    void reviewAvg_Works_UserHasReviews(){
        //arrange
        String cipWithReviews = "pele3157";
        AverageReviewResponse expected = new AverageReviewResponse();
        when(userMapper.getUserCountByCip(cipWithReviews)).thenReturn(1);
        when(reviewMapper.getUserAverageReview(cipWithReviews)).thenReturn(expected);

        //act
        AverageReviewResponse result = reviewService.cipAverageReview(cipWithReviews);

        //assert
        assertEquals(expected, result);
    }

    @Test
    void reviewAvg_Works_UserNoReviews(){
        //arrange
        String cipNoReviews = "bela3439";
        AverageReviewResponse expected;
        when(userMapper.getUserCountByCip(cipNoReviews)).thenReturn(1);
        when(reviewMapper.getUserAverageReview(cipNoReviews)).thenReturn(null);
        expected = new AverageReviewResponse();
        expected.avgRating = 0;
        expected.nbrReviews = 0;

        //act
        AverageReviewResponse result = reviewService.cipAverageReview(cipNoReviews);

        //assert
        assertEquals(expected.avgRating, result.avgRating);
        assertEquals(expected.nbrReviews, result.nbrReviews);
    }

    @Test
    void reviewDetails_ThrowsNotFound_InexistentCip(){
        //arrange
        String inexistentCip = "abcd1234";
        when(userMapper.getUserCountByCip(inexistentCip)).thenReturn(0);

        //act and assert
        assertThrows(NotFoundException.class, () -> {
            reviewService.cipDetailsReview(inexistentCip);
        });
    }

    @Test
    void reviewDetails_Works_UserHasReviews(){
        //arrange
        String cipWithReviews = "pele3157";
        List<ReviewDetailsResponse> expected = new ArrayList<>();
        when(userMapper.getUserCountByCip(cipWithReviews)).thenReturn(1);
        when(reviewMapper.getDetailledReviews(cipWithReviews)).thenReturn(expected);

        //act
        List<ReviewDetailsResponse> result = reviewService.cipDetailsReview(cipWithReviews);

        //assert
        assertEquals(expected, result);
    }

    @Test
    void reviewDetails_Works_UserNoReviews(){
        //arrange
        String cipNoReviews = "bela3439";
        List<ReviewDetailsResponse> expected = List.of();
        when(userMapper.getUserCountByCip(cipNoReviews)).thenReturn(1);
        when(reviewMapper.getDetailledReviews(cipNoReviews)).thenReturn(List.of());

        //act
        List<ReviewDetailsResponse> result = reviewService.cipDetailsReview(cipNoReviews);

        //assert
        assertEquals(expected, result);
    }

}

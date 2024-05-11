package com.example.web_app.repos;

import com.example.web_app.domain.Review;
import com.example.web_app.service.PrimarySequenceService;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;


@Component
public class ReviewListener extends AbstractMongoEventListener<Review> {

    private final PrimarySequenceService primarySequenceService;

    public ReviewListener(final PrimarySequenceService primarySequenceService) {
        this.primarySequenceService = primarySequenceService;
    }

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Review> event) {
        if (event.getSource().getReviewId() == null) {
            event.getSource().setReviewId(((int)primarySequenceService.getNextValue()));
        }
    }

}

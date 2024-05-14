package com.example.web_app.repos;

import com.example.web_app.domain.Category;
import com.example.web_app.service.PrimarySequenceService;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;


@Component
public class CategoryListener extends AbstractMongoEventListener<Category> {

    private final PrimarySequenceService primarySequenceService;

    public CategoryListener(final PrimarySequenceService primarySequenceService) {
        this.primarySequenceService = primarySequenceService;
    }

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Category> event) {
        if (event.getSource().getId() == null) {
            event.getSource().setId(((int)primarySequenceService.getNextValue()));
        }
    }

}

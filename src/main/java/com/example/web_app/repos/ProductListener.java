package com.example.web_app.repos;

import com.example.web_app.domain.Product;
import com.example.web_app.service.PrimarySequenceService;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;


@Component
public class ProductListener extends AbstractMongoEventListener<Product> {

    private final PrimarySequenceService primarySequenceService;

    public ProductListener(final PrimarySequenceService primarySequenceService) {
        this.primarySequenceService = primarySequenceService;
    }

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Product> event) {
        if (event.getSource().getProductId() == null) {
            event.getSource().setProductId(((int)primarySequenceService.getNextValue()));
        }
    }

}

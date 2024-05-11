package com.example.web_app.repos;

import com.example.web_app.domain.Order;
import com.example.web_app.service.PrimarySequenceService;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;


@Component
public class OrderListener extends AbstractMongoEventListener<Order> {

    private final PrimarySequenceService primarySequenceService;

    public OrderListener(final PrimarySequenceService primarySequenceService) {
        this.primarySequenceService = primarySequenceService;
    }

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Order> event) {
        if (event.getSource().getOrderId() == null) {
            event.getSource().setOrderId(((int)primarySequenceService.getNextValue()));
        }
    }

}

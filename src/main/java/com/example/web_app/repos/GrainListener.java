package com.example.web_app.repos;

import com.example.web_app.domain.Grain;
import com.example.web_app.service.PrimarySequenceService;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;


@Component
public class GrainListener extends AbstractMongoEventListener<Grain> {

    private final PrimarySequenceService primarySequenceService;

    public GrainListener(final PrimarySequenceService primarySequenceService) {
        this.primarySequenceService = primarySequenceService;
    }

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Grain> event) {
        if (event.getSource().getGrainId() == null) {
            event.getSource().setGrainId(((int)primarySequenceService.getNextValue()));
        }
    }

}

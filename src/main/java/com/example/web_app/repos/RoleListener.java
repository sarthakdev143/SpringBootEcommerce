package com.example.web_app.repos;

import com.example.web_app.domain.Role;
import com.example.web_app.service.PrimarySequenceService;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;


@Component
public class RoleListener extends AbstractMongoEventListener<Role> {

    private final PrimarySequenceService primarySequenceService;

    public RoleListener(final PrimarySequenceService primarySequenceService) {
        this.primarySequenceService = primarySequenceService;
    }

    @Override
    public void onBeforeConvert(final BeforeConvertEvent<Role> event) {
        if (event.getSource().getId() == null) {
            event.getSource().setId(((int)primarySequenceService.getNextValue()));
        }
    }

}

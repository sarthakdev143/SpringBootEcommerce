package com.example.web_app.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import com.example.web_app.service.GrainService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Validate that the grainName value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = GrainGrainNameUnique.GrainGrainNameUniqueValidator.class
)
public @interface GrainGrainNameUnique {

    String message() default "{Exists.grain.grainName}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class GrainGrainNameUniqueValidator implements ConstraintValidator<GrainGrainNameUnique, String> {

        private final GrainService grainService;
        private final HttpServletRequest request;

        public GrainGrainNameUniqueValidator(final GrainService grainService,
                final HttpServletRequest request) {
            this.grainService = grainService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("grainId");
            if (currentId != null && value.equalsIgnoreCase(grainService.get(Integer.parseInt(currentId)).getGrainName())) {
                // value hasn't changed
                return true;
            }
            return !grainService.grainNameExists(value);
        }

    }

}

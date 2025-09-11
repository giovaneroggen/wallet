package br.roggen.recargapay.wallet.core.usecase.input.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public class InputValidator {

    private static ValidatorFactory VALIDATION_FACTORY;

    public static <T> void valid(T input) {
        VALIDATION_FACTORY = getValidationFactory();
        Set<ConstraintViolation<T>> violations = VALIDATION_FACTORY.getValidator().validate(input);
        if(!violations.isEmpty()){
            throw new ConstraintViolationException(violations);
        }
    }

    private static ValidatorFactory getValidationFactory() {
        if(VALIDATION_FACTORY == null)
            VALIDATION_FACTORY = Validation.buildDefaultValidatorFactory();
        return VALIDATION_FACTORY;
    }
}

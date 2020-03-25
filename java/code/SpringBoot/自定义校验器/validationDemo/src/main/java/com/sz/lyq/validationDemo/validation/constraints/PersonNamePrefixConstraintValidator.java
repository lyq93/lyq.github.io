package com.sz.lyq.validationDemo.validation.constraints;

import com.sz.lyq.validationDemo.domain.Person;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PersonNamePrefixConstraintValidator implements ConstraintValidator<PersonNamePrefix,String> {
    private String personNamePrefix;

    @Override
    public void initialize(PersonNamePrefix constraintAnnotation) {
        this.personNamePrefix = constraintAnnotation.prefix();
    }

    @Override
    public boolean isValid(String personName, ConstraintValidatorContext constraintValidatorContext) {
        if(!personName.startsWith(personNamePrefix)) {
            return false;
        }
        return true;
    }
}

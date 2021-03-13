package org.geektimes.project.user.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * @author djt
 * @date 2021/3/13
 */
public class MobileAnnotationValidator implements ConstraintValidator<MobileValid, String> {

   private static final  String phoneRegex = "1[38]\\d{9}";
    private Pattern phonePattern = Pattern.compile(phoneRegex);


    @Override
    public void initialize(MobileValid constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value=="" ) {
            return false;
        }
        return phonePattern.matcher(value).matches();
    }
}

package com.gerp.platform.feishu.server.validator;

import com.kmniu.erpweb.v2.common.model.common.CustomResponse;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @Author: duanzengqiang
 * @Date: 2024/11/13 18:18
 */
@Component
public class ModelValidator {

    private final Validator validator;

    public ModelValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public <T> CustomResponse isValid(T t) {
        Set<ConstraintViolation<T>> violations = validator.validate(t);
        if (!violations.isEmpty()) {
            StringBuilder sb=new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getPropertyPath().toString());
                sb.append(violation.getMessage());
                sb.append(";");
            }
            return CustomResponse.error(sb.toString());
        }
        return CustomResponse.success("ok");
    }
}

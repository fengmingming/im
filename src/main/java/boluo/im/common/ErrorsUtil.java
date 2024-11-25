package boluo.im.common;

import org.springframework.validation.Errors;

import java.util.stream.Collectors;

public class ErrorsUtil {

    public static String getMessage(Errors errors) {
        if(errors.getErrorCount() == 0)return null;
        return errors.getAllErrors().stream().map(it -> it.getDefaultMessage()).collect(Collectors.joining(";"));
    }

}

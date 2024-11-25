package boluo.im.message;

import boluo.im.Bootstrap;
import boluo.im.common.ErrorsUtil;
import boluo.im.message.submessages.StringMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.stream.Collectors;

@SpringBootTest(classes = Bootstrap.class)
public class TestIM {

    @Autowired
    private Validator validator;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testValidator() throws JsonProcessingException {
        StringMessage message = new StringMessage();
        message.setTo("to");
        message.setFrom("from");
        //message.setContent("content");
        //message.setTenantId("tenantId");
        Errors errors = validator.validateObject(message);
        System.out.println(ErrorsUtil.getMessage(errors));
    }

}

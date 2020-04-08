package spring.cloud.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.TimeoutException;

@RestController
public class DemoRestController {

    private final Random random = new Random();

    /**
     * 超时抛出异常
     * @return
     * @throws Exception
     */
    @GetMapping("/index")
    public String index() throws Exception{

        int executeTime = random.nextInt(200);
        if(executeTime > 100) {
            throw new TimeoutException("timeout!!");
        }

        return "hello world";
    }

}

package spring.cloud.service.provider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service;
import spring.cloud.service.api.UserService;
import spring.cloud.service.domain.User;
import spring.cloud.service.provider.stream.UserMessage;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@Service
public class UserMessageService {
    @Autowired
    private UserMessage userMessage; // 消息 stream 接口
    @Autowired
    private UserService userService; //用户API实现

    /**
     * 取到消息中间件（kafka）topic中的消息，并对数据进行存储
     * @throws Exception
     */
    @PostConstruct
    public void init() throws Exception {
        SubscribableChannel input = userMessage.input();
        input.subscribe(new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                byte[] data = (byte[])message.getPayload();
                try {
                    saveUser(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 反序列化且保存user对象
     * @param data
     * @throws Exception
     */
    private void saveUser(byte[] data) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        User user = (User)objectInputStream.readObject();
        userService.saveUser(user);
    }
}

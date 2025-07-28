package com.company;

import com.company.models.dto.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisTest {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisTest(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @BeforeEach
    void writeToRedis(){
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1);
        userResponse.setBirthDate(LocalDate.now());
        userResponse.setFirstName("Ali");
        userResponse.setLastName("Ahadov");
        userResponse.setFullName("Ali Ahadov");

        String redisKey = "user:" + userResponse.getId();
        redisTemplate.opsForValue().set(redisKey, userResponse);
    }

    @Test
    public void readFromRedis() {

        Object object =  redisTemplate.opsForValue().get("user:1");

        if(object instanceof UserResponse response){
            assertEquals(response.getFirstName(),"Ali");
        }
    }

}

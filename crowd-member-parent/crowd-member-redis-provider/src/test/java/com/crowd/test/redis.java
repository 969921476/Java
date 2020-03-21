package com.crowd.test;



import lombok.AllArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;


import java.sql.SQLException;

/**
 * @author cjn
 * @create 2020-03-19 18:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class redis {

    private  Logger logger = LoggerFactory.getLogger(redis.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void test() throws SQLException {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        stringStringValueOperations.set("app1","ss");
    }
}

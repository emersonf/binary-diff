/*
 * Copyright 2014 Emerson Farrugia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package internal.diff.common.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * A configuration for accessing data in Redis.
 *
 * @author Emerson Farrugia
 */
@Configuration
public class RedisConfiguration {

    @Value("${redis.host}")
    private String redisHost;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();

        connectionFactory.setHostName(this.redisHost);

        return connectionFactory;
    }


    @Bean
    public RedisTemplate<?, ?> redisTemplate() {

        RedisTemplate<?, ?> redisTemplate = new RedisTemplate();

        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        return redisTemplate;
    }


    @Bean
    public StringRedisTemplate stringRedisTemplate() {

        StringRedisTemplate redisTemplate = new StringRedisTemplate();

        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }
}

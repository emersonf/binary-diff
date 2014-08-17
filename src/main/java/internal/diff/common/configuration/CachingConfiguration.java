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


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.DefaultKeyGenerator;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;


/**
 * A configuration for caching data using Redis.
 *
 * @author Emerson Farrugia
 */
@Configuration
@EnableCaching
public class CachingConfiguration implements CachingConfigurer {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    @Override
    public RedisCacheManager cacheManager() {

        RedisCacheManager cacheManager = new RedisCacheManager(stringRedisTemplate);
        cacheManager.setUsePrefix(true);

        return cacheManager;
    }


    @Bean
    @Override
    public KeyGenerator keyGenerator() {

        return new DefaultKeyGenerator();
    }
}

package com.cjx.aiquestion.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 智谱AI配置类
 *
 * @author cjx
 * @date 2024-07-30
 */
@Configuration
@ConfigurationProperties(prefix = "zhipu.ai")
@Data
public class ZhiPuAiConfig {

    private String apiKey;

    @Bean
    public ClientV4 zhiPuAiClient() {
        return new ClientV4
                .Builder(apiKey)
                .build();
    }


}

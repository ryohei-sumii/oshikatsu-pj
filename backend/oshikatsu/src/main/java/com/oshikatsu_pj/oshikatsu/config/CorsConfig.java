package com.oshikatsu_pj.oshikatsu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 許可するオリジン（フロントエンドのURL）
        corsConfiguration.addAllowedOrigin(
                "http://localhost:3000" // フロントエンドはReactの想定
        );

        // 許可するHTTPメソッド
        corsConfiguration.addAllowedMethod(HttpMethod.GET);
        corsConfiguration.addAllowedMethod(HttpMethod.POST);
        corsConfiguration.addAllowedMethod(HttpMethod.PUT);
        corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
        corsConfiguration.addAllowedMethod(HttpMethod.OPTIONS);

        // 許可するヘッダー
        corsConfiguration.setAllowedHeaders(List.of("*"));

        // 認証情報（Cookie、Authorizationヘッダーなど）を許可
        corsConfiguration.setAllowCredentials(true);

        // プリフライトリクエスト（WebサーバがCORS要求を受け付けるかどうかを実際に要求を送信する前に確かめること）のキャッシュ時間
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}

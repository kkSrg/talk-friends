package com.tanhua.filter;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 网关过滤器
 */
@Component
@Order(1)
@Slf4j
public class GlobalAuthFilter implements GlobalFilter {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1、获取request和response
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        URI uri = request.getURI();
        log.info(uri.toString());

        //2、设置放行:发送短信和短信验证不需要拦截处理
        if(StrUtil.equals(request.getURI().getPath(),"/user/login")
                || StrUtil.equals(request.getURI().getPath(),"/user/loginVerification")){
            return chain.filter(exchange);
        }
        //3、获取token
        String token = request.getHeaders().getFirst("Authorization");
        //4、向SSO发送检验token请求
        Long uid = restTemplate.getForObject("http://localhost:18081/user/" + token, Long.class);
        //5、对uid进行判断
        if(uid == null){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }else {//验证通过
            //另外一种方案：将验证通过的id存储与redis中
            //存储header中
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("userId", uid + "");
            }).build();
            //重置请求
            exchange.mutate().request(serverHttpRequest);
            return chain.filter(exchange);
        }

    }
}

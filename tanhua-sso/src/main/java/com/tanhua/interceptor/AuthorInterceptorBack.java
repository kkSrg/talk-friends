package com.tanhua.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.tanhua.utils.AppJwtUtil;
import com.tanhua.utils.ThreadLocalUtil;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一token处理的拦截器
 */
public class AuthorInterceptorBack implements HandlerInterceptor {

    /**
     * 在controller方法执行之前执行
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1、放行规则: user/login  user/loginVerification
        String uri = request.getRequestURI();
        if(StrUtil.equals(uri,"/user/login") || StrUtil.equals(uri,"/user/loginVerification")){
            return true;
        }
        //2、获取token
        String token = request.getHeader("Authorization");
        //3、token解析
        Claims claims = AppJwtUtil.getClaimsBody(token);
        if (AppJwtUtil.verifyToken(claims)==1 || AppJwtUtil.verifyToken(claims)==2){
            response.setContentType("txt/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("token已失效！！");
            return false;
        }else {
            //向本地线程存储用户id
            ThreadLocalUtil.setId(Convert.toLong(claims.get("id")));
            return true;
        }
    }

    /**
     * 完成之后执行
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清理当前线程
        ThreadLocalUtil.close();
    }
}

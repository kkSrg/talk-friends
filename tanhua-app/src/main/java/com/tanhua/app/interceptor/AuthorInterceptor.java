package com.tanhua.app.interceptor;

import cn.hutool.core.convert.Convert;
import com.tanhua.utils.ThreadLocalUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一token处理的拦截器
 */
public class AuthorInterceptor implements HandlerInterceptor {

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
        //获取请求头中userId
        String userId = request.getHeader("userId");
        ThreadLocalUtil.setId(Convert.toLong(userId));
        return true;
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

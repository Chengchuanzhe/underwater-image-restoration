package com.underwater.interceptor;

import com.alibaba.fastjson2.JSON;
import com.underwater.entity.Result;
import com.underwater.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行登录/注册/验证码接口
        String path = request.getRequestURI();
        if (path.contains("/user/login") || path.contains("/user/register") || path.contains("/email/code")) {
            return true;
        }

        // 获取Token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(Result.error(401, "未登录，请先登录")));
            return false;
        }

        // 验证Token
        try {
            if (jwtUtil.isTokenExpired(token)) {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write(JSON.toJSONString(Result.error(401, "登录已过期，请重新登录")));
                return false;
            }
        } catch (Exception e) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(Result.error(401, "Token无效，请重新登录")));
            return false;
        }

        // 将用户ID存入Request，供后续接口使用
        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        return true;
    }
}
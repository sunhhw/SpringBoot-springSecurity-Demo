package com.shw.security.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/23 14:38
 * @description 成功退出后要处理的逻辑
 */
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = ((User) authentication.getPrincipal()).getUsername();
        logger.info("退出成功，用户名:{}",username);

        // 重定向到登录页面
        response.sendRedirect("/login");
    }

}

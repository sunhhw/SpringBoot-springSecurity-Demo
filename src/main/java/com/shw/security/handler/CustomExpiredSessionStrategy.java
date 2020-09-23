package com.shw.security.handler;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/23 13:46
 * @description 旧用户被踢出后做的操作
 */
public class CustomExpiredSessionStrategy implements SessionInformationExpiredStrategy {

    //RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "已经另一台机器登录，您被迫下线。" + event.getSessionInformation());

        event.getResponse().setContentType("application/json;charset=UTF-8");
        event.getResponse().getWriter().write(map.toString());

        // 如果是跳转html页面，url代表跳转的地址
        //redirectStrategy.sendRedirect(event.getRequest(),event.getResponse(),"url");
    }
}

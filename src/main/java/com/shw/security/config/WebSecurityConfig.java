package com.shw.security.config;

import com.shw.security.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/22 11:44
 * @description
 */
// 表明该类是配置类
@Configuration
// 开启Security服务
@EnableWebSecurity
// 开启全局Security注解
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Autowired
    private DataSource dataSource;


    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        // 表明是默认的，增删改查也是固定的,登录的时候会在表中自动创建，退出的时候会删除表记录
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // 如果token表不存在，使用下面语句可以初始化该表，若存在，请注释掉这条语句，否则会报错
        //tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }

    /**
     * 注入自定义的PermissionEvaluator
     * @return
     */
    @Bean
    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        defaultWebSecurityExpressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return defaultWebSecurityExpressionHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /**
         * 将我们自定义的 userDetailsService 注入进来，
         * 在 configure() 方法中使用 auth.userDetailsService() 方法替换掉默认的 userDetailsService。
         *
         * new BCryptPasswordEncoder() 加密
         */
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return charSequence.toString();
            }

            // 这里的charSequence拿到的是用户页面输入的密码  s是后台拿到的密码
            // 用前端用户传来的密码和后端从数据库中拿到的密码做对比，因为后台密码没有做加密，这里也就不用加密比较
            @Override
            public boolean matches(CharSequence charSequence, String s) {
                System.out.println(s);
                System.out.println(charSequence);
                return s.equals(charSequence.toString());
            }
        });

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 如果有允许匿名的url，填在下面
                .antMatchers("/login/invalid").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                    // 在类中处理
                   // .invalidSessionStrategy()
                    // 跳转路径处理
                    //.invalidSessionUrl("/login/invalid")
                    // 指定最大登录数
                    .maximumSessions(1)
                    // false 表示后登录的把前面一个登录的给踢掉
                    .maxSessionsPreventsLogin(false)
                    // 当达到最大值的时候，旧用户被踢出后的操作
                    // 在类中处理
                    .expiredSessionStrategy(new CustomExpiredSessionStrategy())
                    // 主动踢出一个用户
                    .sessionRegistry(sessionRegistry())
                .and()
                // 跳转url处理
                // .expiredUrl()
                .and()
                // 设置登录页
                .formLogin()
                    .loginPage("/login")
                    // 设置登录成功页
                    //.defaultSuccessUrl("/").permitAll()
                    // 设置登录成功后的执行器
                    .successHandler(new CustomAuthenticationSuccessHandler())
                    // 设置登录失败后的执行器
                    .failureHandler(new CustomAuthenticationFailureHandler())
                    .permitAll()
                    // 设置登录失败的跳转路径
                    //.failureUrl("/errorss")
                    // 自定义登录用户名和密码参数，默认为username和password
                    // 前端传来的字段也要是username和password
                    //.usernameParameter("username")
                    //.passwordParameter("password")
                .and()
                .logout()
                    // 默认的退出地址是/logout，这里是更改默认的退出地址为signout
                    //.logoutUrl("/signout")
                    // 退出时删除JSESSIONID的cookie
                    //.deleteCookies("JSESSIONID")
                    // 退出成功后由这个类处理
                    .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                    // 退出成功后走url路径
                    //.logoutSuccessUrl()
                    //.permitAll()
                    // 自动登录(存在cookie中，不安全)
                    // 我们登陆时勾选自动登录时，会自动在 Cookie 中保存一个名为 remember-me 的cookie，默认有效期为2周，其值是一个加密字符串
                .and().rememberMe()
                .tokenRepository(persistentTokenRepository())
                // 有效时间，单位秒
                .tokenValiditySeconds(600)
                .userDetailsService(userDetailsService);

        // 关闭CSRF跨域
        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置拦截忽略的文件夹，可以对静态资源放行
        web.ignoring().antMatchers("/css/**","/js/**");
    }
}

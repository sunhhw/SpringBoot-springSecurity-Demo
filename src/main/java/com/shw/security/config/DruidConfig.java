package com.shw.security.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import javax.sql.DataSource;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/22 10:41
 * @description 数据源配置
 */
@Configuration
public class DruidConfig {

    @Bean
    public DataSource druidDataSource(){
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        dataSource.setMaxActive(20);
        return dataSource;
    }

}

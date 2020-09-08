/****************************************
 * 2018 - 2021 版权所有 CopyRight(c) 快程乐码信息科技有限公司所有, 未经授权，不得复制、转发
 */

package com.kclm.xsap.config;

import com.alibaba.druid.pool.DruidDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/******************
 * @Author yejf
 * @Version v1.0
 * @Create 2020-09-07 14:12
 * @Description TODO
 */
@Configuration
public class DataSourceConfig {

	private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);
	
    /****
     * 配置数据源，【spring boot中有默认的】
     * @return
     */
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        log.debug("创建DruidDataSource....");
        return new DruidDataSource();
    }

    /***
     * 配置事务管理器 【spring boot中有默认的】
     * @param dataSource
     * @return
     */
    @Bean
    public DataSourceTransactionManager transactionManager(@Autowired  DataSource dataSource) {
        log.debug("创建事务管理器：dataSourceTransactionManager....");
        return new DataSourceTransactionManager(dataSource);
    }
}

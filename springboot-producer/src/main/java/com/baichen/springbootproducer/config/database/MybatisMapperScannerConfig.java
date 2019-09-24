package com.baichen.springbootproducer.config.database;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 扫描即DAO对象
 */
@Configuration
@AutoConfigureAfter(MybatisDataSourceConfig.class)  // 在MybatisDataSourceConfig加载后再加载本类,在MybatisDataSourceConfig加载后才有sqlSessionFactory
public class MybatisMapperScannerConfig {

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.baichen.springbootproducer.mapper");
        return mapperScannerConfigurer;
    }

}

package org.rainbow.farm_main;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * 智慧农业管理系统主启动类
 * 统一启动所有模块
 */
@SpringBootApplication(scanBasePackages = {
        "org.rainbow.farm_common",
        "org.rainbow.farm_system",
        "org.rainbow.farm_warehousing",
        "org.rainbow.farm_plant",
})
@MapperScan(basePackages = {
        "org.rainbow.farm_system.mapper",
        "org.rainbow.farm_warehousing.mapper",
        "org.rainbow.farm_plant.mapper",
})
// 启动@PerAuthorize
@EnableGlobalMethodSecurity(prePostEnabled = true)
// 启用AOP
@EnableAspectJAutoProxy
public class FarmMainApplication {

    static void main(String[] args) {
        SpringApplication.run(FarmMainApplication.class, args);
    }

    //分页插件
    MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}

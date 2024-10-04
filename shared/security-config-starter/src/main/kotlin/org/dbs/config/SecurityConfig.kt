package org.dbs.config

import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.consts.SysConst.SERVICE_PACKAGE
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
@ComponentScan(basePackages = [SERVICE_PACKAGE])
class SecurityConfig : AbstractApplicationConfiguration() { //    @Bean
    //    SecurityRepository securityRepository() {
    //        return NullSafe.createObject(SecurityRepository.class);
    //    }
}

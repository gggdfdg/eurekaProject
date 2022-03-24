package com.ming.eureka;

import com.fasterxml.classmate.TypeResolver;
import com.ming.eureka.business.SearchPredicate;
import com.ming.eureka.dto.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * sweagger 配置 restful文档生成器
 *
 * @author lll 2016年8月19日
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket createRestApi() {

        // 全局参数
        List<Parameter> parameters = newArrayList(new ParameterBuilder()
                .name(Constant.TOKEN_HEADER_STRING)
                .description("登录token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(true)
                .build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ming.eureka.restcontroller"))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(parameters)
                .ignoredParameterTypes(SearchPredicate.class)
//                .alternateTypeRules(
//                		new AlternateTypeRule(
//                				typeResolver.resolve(PageWrapper.class, WildcardType.class),
//                				typeResolver.resolve(WildcardType.class)
//                				)
//                		)
                ;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("移动营销RESTful APIs")
                .description("移动营销RESTful APIs 文档  <br>"
                        + "以下接口 返回值描述均只描述 result字段的格式 "
                        + "实际为<span style='color:red;'>{'code'=0,'msg'='','result'=}</span><br>"
                        + "登录接口: <span style='color:red;'>POST</span> /api/login "
                        + "username:设备uid password:暂填空串，成功后会返回 /api/token 的结果<br>"
                        + "退出登录接口: <span style='color:red;'>GET</span> /api/logout")
                .version("1.0")
                .build();
    }

}

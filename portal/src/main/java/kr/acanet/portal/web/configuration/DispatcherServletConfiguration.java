package kr.acanet.portal.web.configuration;

import java.nio.charset.Charset;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan({ "kr.acanet.portal.web" })
public class DispatcherServletConfiguration extends WebMvcConfigurerAdapter{
	
	private static final Logger logger = LoggerFactory.getLogger(DispatcherServletConfiguration.class);
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		//response body로 뿌릴 시 한글깨짐현상 방지
		converters.add(responseBodyConverter());
		//Collection 계열로 리턴 시 json 현태로 매핑
		converters.add(new MappingJackson2HttpMessageConverter());
		super.configureMessageConverters(converters);
		
		for (Object httpMessageConverter : converters.toArray()) {
			AbstractHttpMessageConverter<?> converter = (AbstractHttpMessageConverter<?>)httpMessageConverter;
			logger.info("Registered Message converter class - {}", converter.getClass().getName());
		}
		logger.info("MessageConverter setting completed. ");
	}
	
	@Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(Charset.forName("UTF-8"));
    }
	
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/css/**").addResourceLocations("/css/");
	    registry.addResourceHandler("/images/**").addResourceLocations("/images/");
	    registry.addResourceHandler("/javascript/**").addResourceLocations("/javascript/");
	    registry.addResourceHandler("/favicon.ico").addResourceLocations("/favicon.ico");
	    logger.info("ResouceHandlers setting completed. ");
	}
	
	@Bean
    public InternalResourceViewResolver internalResourceViewResolver(){
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        
        viewResolver.setViewClass(JstlView.class);	//JSTL뷰클래스 사용
        viewResolver.setOrder(3);	//우선순위 설정
        viewResolver.setPrefix("/WEB-INF/views/");	
        viewResolver.setSuffix(".jsp");
        logger.info("InternalResourceViewResolver setting completed. ");
        return viewResolver;
    }
}

package me.fit.mefit;

import me.fit.mefit.security.ratelimit.RateLimitInterceptor;
import me.fit.mefit.utils.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class MefitApplication implements WebMvcConfigurer {

	@Autowired @Lazy private RateLimitInterceptor interceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(interceptor)
				.addPathPatterns(ApiPaths.LOGIN_PATH);
	}

	public static void main(String[] args) {
		SpringApplication.run(MefitApplication.class, args);
	}

}

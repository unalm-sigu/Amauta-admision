package pe.edu.lamolina.pivot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pe.edu.lamolina.pivot.controller.seguridad.menu.MenuService;

@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = {"pe.edu.lamolina.pivot", "pe.albatross.zelpers"})
public class PivotApplication extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(PivotApplication.class, args);
        context.getBean(MenuService.class).inicializarMenus();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/public/", "classpath:/META-INF/resources/webjars/");
    }

}

package pe.edu.lamolina.pivot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.AppCacheManifestTransformer;
import org.springframework.web.servlet.resource.GzipResourceResolver;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import pe.edu.lamolina.pivot.controller.ingresante.muestraslab.MuestrasLabService;
import pe.edu.lamolina.pivot.controller.seguridad.menu.MenuService;

@EnableAsync
@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = {"pe.edu.lamolina.pivot", "pe.albatross.zelpers"})
public class PivotApplication extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(PivotApplication.class, args);
        context.getBean(MenuService.class).inicializarMenus();
        context.getBean(MuestrasLabService.class).inicializarVisor();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/public/", "classpath:/META-INF/resources/webjars/")
                .setCachePeriod(null)
                .resourceChain(false)
                .addResolver(new GzipResourceResolver())
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"))
                .addTransformer(new AppCacheManifestTransformer());
    }

}

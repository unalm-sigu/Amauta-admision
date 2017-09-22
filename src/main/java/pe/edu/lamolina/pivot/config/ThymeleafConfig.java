package pe.edu.lamolina.pivot.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

@Configuration
public class ThymeleafConfig {

    @Autowired
    ThymeleafViewResolver viewResolver;
    
    @Autowired
    DespliegueConfig despliegueConfig;

    @EventListener(ApplicationReadyEvent.class)
    public void loadStaticVariables() {
        Map<String, String> adicionales = new HashMap();
        adicionales.put("TAWKTO", despliegueConfig.getTawkto().toString());
        
        viewResolver.setStaticVariables(adicionales);
    }
}

package pe.edu.lamolina.amauta.controller.consejeria.administracion.view;

import java.io.Serializable;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class VerificadorClonacionConsejero implements Serializable{
    
    private boolean ocupado;

    public VerificadorClonacionConsejero() {
        this.ocupado = false;
    }
    
    
    
}

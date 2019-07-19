package pe.edu.lamolina.pivot.controller.visores;

import org.springframework.stereotype.Component;

@Component
public class RespositorVisor {

    private enum EstadoEnum {
        LIBRE, INICIADO, OCUPADO, COMPLETO
    };

    public enum AccionEnum {
        DESVINCULA, GENERA
    };

}

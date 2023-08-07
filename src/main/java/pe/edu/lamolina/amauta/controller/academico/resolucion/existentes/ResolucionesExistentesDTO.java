package pe.edu.lamolina.amauta.controller.academico.resolucion.existentes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tramite.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ResolucionesExistentesDTO {

    private Alumno alumno;
    
    private Tramite tramite;

    private TramiteTitulo tramiteTitulo;
    
    private TramiteBachiller tramiteBachiller;
    
    private TramiteTraslado tramiteTraslado;

    private CursoDirigido cursoDirigido;

    private Resolucion resolucion;

    private ObtencionGrado obtencionGrado;

    public ResolucionesExistentesDTO() {
    }

}
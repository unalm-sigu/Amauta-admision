package pe.edu.lamolina.pivot.controller.academico.gposeccion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;

public interface GpoSeccionService {

    List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);
    
    GpoSeccionResumen resumen();
    
}

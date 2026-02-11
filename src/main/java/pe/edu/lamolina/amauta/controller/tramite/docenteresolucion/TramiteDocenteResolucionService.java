package pe.edu.lamolina.amauta.controller.tramite.docenteresolucion;

import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.DocenteResolucion;

import java.util.List;

public interface TramiteDocenteResolucionService {
    List<DocenteResolucion> allTramiteByFilter(DynatableFilter filter);
    List<CicloAcademico> getCiclos();
    String saveDocenteResolucion(DocenteResolucion docenteResolucion, DataSessionPivot ds);
    List<Docente> allDocenteByNombre(String nombre, DataSessionPivot ds);
    void anular(Long idDocente, Usuario usuarioAnulacion);

}

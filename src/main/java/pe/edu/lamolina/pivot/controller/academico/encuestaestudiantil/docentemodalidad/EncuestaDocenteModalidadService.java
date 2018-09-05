package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docentemodalidad;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocenteModalidad;

public interface EncuestaDocenteModalidadService {

    List<EncuestaDocenteModalidad> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico ciclo);

    String reporteTodos();

    List<PuntajeEncuestaDocenteModalidad> resumenTemas(EncuestaDocenteModalidad encuestaDocenteModalidad);

}

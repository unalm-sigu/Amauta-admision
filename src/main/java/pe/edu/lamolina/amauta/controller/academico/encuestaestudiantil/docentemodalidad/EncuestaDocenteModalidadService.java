package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.docentemodalidad;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocenteModalidad;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface EncuestaDocenteModalidadService {

    List<EncuestaDocenteModalidad> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico ciclo, List<DepartamentoAcademico> departamentos, DataSessionPivot ds);

    String reporte(EncuestaDocenteModalidad encuestaDocenteModalidad);

    String reporteTodos(CicloAcademico cicloAcademico);

    List<PuntajeEncuestaDocenteModalidad> resumenTemas(EncuestaDocenteModalidad encuestaDocenteModalidad);

    List<Facultad> allAccesoFacultades(DataSessionPivot ds, HttpServletRequest request);

    List<DepartamentoAcademico> allAccesoDepartamentos(DataSessionPivot ds, List<Facultad> facultades, CicloAcademico ciclo, HttpServletRequest request);

}

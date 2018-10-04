package pe.edu.lamolina.pivot.controller.docente.resumenencuesta;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocenteModalidad;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocenteModalidad;

public interface DocenteEncuestaService {

    List<EncuestaDocenteModalidad> allByDynatableCicloAcademicoDocente(DynatableFilter filter, CicloAcademico ciclo, Docente docente);

    List<PuntajeEncuestaDocenteModalidad> resumenTemas(EncuestaDocenteModalidad encuestaDocenteModalidad);

    String reporte(EncuestaDocenteModalidad encuestaDocenteModalidad);

    List<EncuestaDocente> allEncuestaDocente(DynatableFilter filter, CicloAcademico ciclo, Docente docente);

    EncuestaEstudiantil findEncuestaDocente(CicloAcademico cicloAcademico);

}

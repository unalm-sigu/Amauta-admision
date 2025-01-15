package pe.edu.lamolina.amauta.controller.docente.resumenencuesta;

import java.util.List;
import org.springframework.ui.Model;
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

    void reporte(EncuestaDocenteModalidad encuestaDocenteModalidad, Model model);

    List<EncuestaDocente> allEncuestaDocente(DynatableFilter filter, CicloAcademico ciclo, Docente docente);

    EncuestaEstudiantil findEncuestaDocente(CicloAcademico cicloAcademico);

}

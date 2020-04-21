package pe.edu.lamolina.amauta.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaAlumno;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;

public interface EncuestaAlumnoDAO extends EasyDAO<EncuestaAlumno> {

    List<EncuestaAlumno> allByEncuestaDocente(EncuestaDocente encuesta);

    List<EncuestaAlumno> allByEncuestaCurso(EncuestaCurso encuesta);

    void deleteByEncuestasDocentes(List<EncuestaDocente> encuestasDocentes);

    void deleteByEncuestasCursos(List<EncuestaCurso> encuestaCursos);

    List<EncuestaAlumno> allByListEncuestaDocente(List<EncuestaDocente> listEncuestaDocente);

    int saveList(List<EncuestaAlumno> encuestasAlumnos);

}

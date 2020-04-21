package pe.edu.lamolina.amauta.controller.academico.visitante;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoVisitante;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface AlumnosVisitanteService {

    List<TipoDocIdentidad> allTiposDocIdentidad();

    void save(AlumnoVisitante alumnoVisitante, DataSessionPivot ds);

    List<CicloAcademico> allCicloAcademico();

    List<AlumnoVisitante> allAlumnoVisitante(DynatableFilter filter);

    Map<Long, Alumno> allAlumnoByVisitante(List<AlumnoVisitante> visitantes);

    void delete(AlumnoVisitante alumnoVisitante);

    AlumnoVisitante findAlumnoVisitante(Long idAlumnoVisitante);

    void update(AlumnoVisitante alumnoVisitante, DataSessionPivot ds);

    Persona findPersonaByDocumento(Persona personaForm);

    ObjectNode validarAlumno(AlumnoVisitante alumnoVisitanteForm);

    AlumnoVisitante findAlumnoVisitante(AlumnoVisitante idAlumnoVisitante);

    void saveUniversidad(Universidad universidad, DataSessionPivot ds);

}

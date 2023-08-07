package pe.edu.lamolina.amauta.controller.consejeria.derivartutorado;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.tutoria.AlumnoDerivadoAtencion;
import pe.edu.lamolina.model.tutoria.TipoAtencionTutorado;

public interface DerivarTutoradoService {

    List<AlumnoDerivadoAtencion> allByDynatable(DynatableFilter filter, Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    List<TipoAtencionTutorado> allTiposAtenciones();

    List<Curso> allCursosMatriculados(Alumno alumno, CicloAcademico ciclo, DataSessionPivot ds);

    void saveDerivacion(Alumno alumno, AlumnoDerivadoAtencion derivacion, CicloAcademico ciclo, DataSessionPivot ds);

}

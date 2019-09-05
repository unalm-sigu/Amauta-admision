package pe.edu.lamolina.pivot.controller.docente.cargaacademica;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;

public interface CargaAcademicaService {

    List<GrupoSeccion> allGpoSecciones(Docente docente, CicloAcademico ciclo);

    Seccion findSeccion(Long idSeccion);

    List<MatriculaSeccion> allMatriculadosBySecciion(Seccion seccion);

}

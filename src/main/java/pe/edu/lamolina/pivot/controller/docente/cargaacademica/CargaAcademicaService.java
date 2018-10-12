package pe.edu.lamolina.pivot.controller.docente.cargaacademica;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;

public interface CargaAcademicaService {

    List<GrupoSeccion> allGpoSecciones(Docente docente, CicloAcademico ciclo);

}

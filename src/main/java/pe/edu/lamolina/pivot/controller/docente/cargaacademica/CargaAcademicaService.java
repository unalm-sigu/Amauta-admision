package pe.edu.lamolina.pivot.controller.docente.cargaacademica;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CargaAcademicaService {

    List<GrupoSeccion> allGpoSecciones(Docente docente, CicloAcademico ciclo);

    public Seccion findSeccion(Long idSeccion);

}

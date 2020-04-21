package pe.edu.lamolina.amauta.controller.academico.preciocursoestructura;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface PrecioCursoEstructuraService {

    List<PrecioCursoEstructura> allByCicloAcademico(CicloAcademico ciclo);

    void saveAll(List<PrecioCursoEstructura> listaPrecios, CicloAcademico ciclo, DataSessionPivot ds);

}

package pe.edu.lamolina.pivot.controller.academico.preciocursoestructura;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PrecioCursoEstructuraService {
    
    List<PrecioCursoEstructura> allByCicloAcademico(CicloAcademico ciclo);
    
    void saveAll(List<PrecioCursoEstructura> listForm, DataSessionPivot ds);
    
}

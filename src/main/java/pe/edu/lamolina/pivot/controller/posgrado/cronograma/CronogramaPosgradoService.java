package pe.edu.lamolina.pivot.controller.posgrado.cronograma;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.posgrado.CronogramaCuota;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CronogramaPosgradoService {

    List<CronogramaCuota> allByCiclo(CicloAcademico ciclo);

    void generar(CronogramaCuota cronograma, DataSessionPivot ds);

    void deleteAll(CicloAcademico ciclo, DataSessionPivot ds);

    void update(CronogramaCuota cronograma, DataSessionPivot ds);

}

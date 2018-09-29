package pe.edu.lamolina.pivot.dao.posgrado;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.posgrado.CronogramaCuota;

public interface CronogramaCuotaDAO extends EasyDAO<CronogramaCuota> {

    public List<CronogramaCuota> allByCiclo(CicloAcademico ciclo);

    public void deleteAllByCiclo(CicloAcademico ciclo);

    public CronogramaCuota find(CronogramaCuota cronograma);

}

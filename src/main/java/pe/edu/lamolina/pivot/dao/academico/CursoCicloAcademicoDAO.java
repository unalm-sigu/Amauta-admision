package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;

public interface CursoCicloAcademicoDAO extends EasyDAO<CursoCicloAcademico> {

    public List<CursoCicloAcademico> allByCiclo(CicloAcademico cicloDestino);

}
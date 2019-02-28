package pe.edu.lamolina.pivot.dao.academico;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.AsignacionAula;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface AsignacionAulaDAO extends EasyDAO<AsignacionAula> {

    AsignacionAula findByCiclo(CicloAcademico cicloAcademico);

}

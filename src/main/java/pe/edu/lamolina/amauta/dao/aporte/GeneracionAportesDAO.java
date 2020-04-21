package pe.edu.lamolina.amauta.dao.aporte;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.aporte.GeneracionAportes;

public interface GeneracionAportesDAO extends EasyDAO<GeneracionAportes> {

    GeneracionAportes findByCicloAcademico(CicloAcademico cicloAcademico);

    GeneracionAportes findLock(Long id);

}

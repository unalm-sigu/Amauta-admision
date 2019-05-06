package pe.edu.lamolina.pivot.dao.consejeria;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;

public interface ConsejeriaResumenDAO extends EasyDAO<ConsejeriaResumen> {

    ConsejeriaResumen findByCarreraCiclo(Carrera carrera, CicloAcademico ciclo);
}

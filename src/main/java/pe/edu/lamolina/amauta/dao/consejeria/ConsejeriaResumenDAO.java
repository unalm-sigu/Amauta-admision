package pe.edu.lamolina.amauta.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.ConsejeriaResumen;

public interface ConsejeriaResumenDAO extends EasyDAO<ConsejeriaResumen> {

    ConsejeriaResumen findByCarreraCiclo(Carrera carrera, CicloAcademico ciclo);

    public List<ConsejeriaResumen> allByCiclo(CicloAcademico ciclo);

    public void deleteByCiclo(CicloAcademico destino);
}

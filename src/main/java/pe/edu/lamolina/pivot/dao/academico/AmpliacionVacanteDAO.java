package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.AmpliacionVacante;
import pe.edu.lamolina.model.academico.Seccion;

public interface AmpliacionVacanteDAO extends EasyDAO<AmpliacionVacante> {

    public List<AmpliacionVacante> allBySeccion(Seccion seccion);

    public AmpliacionVacante find(AmpliacionVacante ampliacionVacante);

}

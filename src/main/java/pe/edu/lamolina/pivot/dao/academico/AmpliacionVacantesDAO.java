package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.AmpliacionVacantes;
import pe.edu.lamolina.model.academico.Seccion;

public interface AmpliacionVacantesDAO extends EasyDAO<AmpliacionVacantes> {

    List<AmpliacionVacantes> allBySeccion(Seccion seccion);

    AmpliacionVacantes find(AmpliacionVacantes ampliacionVacante);

    List<AmpliacionVacantes> allPendientesBySeccion(Seccion seccion);

    List<AmpliacionVacantes> allBySecciones(List<Seccion> secciones);

}

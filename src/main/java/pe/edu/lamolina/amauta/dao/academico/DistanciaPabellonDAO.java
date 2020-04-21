package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.DistanciaPabellon;

public interface DistanciaPabellonDAO extends EasyDAO<DistanciaPabellon> {

    List<DistanciaPabellon> allActivos();

    List<DistanciaPabellon> allFactorDistanciaByDepartamento(DepartamentoAcademico departamentoAcademico);

    List<DistanciaPabellon> allByDynatable(DynatableFilter filter);

    List<DistanciaPabellon> allByActAndDistanciaOrder(String distanciaOrder);

}

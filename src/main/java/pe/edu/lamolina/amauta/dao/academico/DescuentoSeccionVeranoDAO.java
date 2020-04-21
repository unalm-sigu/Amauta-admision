package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.DescuentoSeccionVerano;
import pe.edu.lamolina.model.academico.Seccion;

public interface DescuentoSeccionVeranoDAO extends EasyDAO<DescuentoSeccionVerano> {

    public List<DescuentoSeccionVerano> findSecciones(List<Seccion> secciones);

    public DescuentoSeccionVerano findSeccion(Seccion seccion);

}

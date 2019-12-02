package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.DescuentoSeccionVerano;
import pe.edu.lamolina.model.academico.Seccion;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import pe.edu.lamolina.pivot.dao.academico.DescuentoSeccionVeranoDAO;

@Repository
public class DescuentoSeccionVeranoDAOH extends AbstractEasyDAO<DescuentoSeccionVerano> implements DescuentoSeccionVeranoDAO {

    public DescuentoSeccionVeranoDAOH() {
        super();
        setClazz(DescuentoSeccionVerano.class);
    }

    @Override
    public List<DescuentoSeccionVerano> findSecciones(List<Seccion> secciones) {
        Octavia sql = new Octavia()
                .from(DescuentoSeccionVerano.class, "dsv")
                .join("seccion sec")
                .filter("dsv.estado", ACT)
                .in("sec.id", secciones);

        return all(sql);
    }

    @Override
    public DescuentoSeccionVerano findSeccion(Seccion seccion) {
        Octavia sql = new Octavia()
                .from(DescuentoSeccionVerano.class, "dsv")
                .join("seccion sec")
                .filter("dsv.estado", ACT)
                .filter("sec.id", seccion);

        return find(sql);
    }

}

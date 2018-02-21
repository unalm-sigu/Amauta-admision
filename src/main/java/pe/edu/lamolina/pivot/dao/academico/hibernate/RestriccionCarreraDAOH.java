package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.RestriccionCarreraDAO;

@Repository
public class RestriccionCarreraDAOH extends AbstractEasyDAO<RestriccionCarrera> implements RestriccionCarreraDAO {

    public RestriccionCarreraDAOH() {
        super();
        setClazz(RestriccionCarrera.class);
    }

    @Override
    public List<RestriccionCarrera> allActivasBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(RestriccionCarrera.class, "rc")
                .join("carrera car", "seccion sec")
                .filter("rc.estado", EstadoEnum.ACT)
                .filter("sec.id", seccion);
        return all(sql);
    }

    @Override
    public void updateEstadoFechaUsuario(RestriccionCarrera restriccionCarrera) {
        Octavia octavia = Octavia.update(RestriccionCarrera.class);
        octavia.set(restriccionCarrera, "estado");
        this.update(restriccionCarrera);
    }

}

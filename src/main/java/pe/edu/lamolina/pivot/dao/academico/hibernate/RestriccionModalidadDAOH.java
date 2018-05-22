package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.RestriccionModalidad;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.RestriccionModalidadDAO;

@Repository
public class RestriccionModalidadDAOH extends AbstractEasyDAO<RestriccionModalidad> implements RestriccionModalidadDAO {

    public RestriccionModalidadDAOH() {
        super();
        setClazz(RestriccionModalidad.class);
    }

    @Override
    public List<RestriccionModalidad> allActivasBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(RestriccionModalidad.class, "rm")
                .join("modalidadEstudio mod", "seccion sec")
                .filter("rm.estado", EstadoEnum.ACT)
                .filter("sec.id", seccion);
        return all(sql);
    }

    @Override
    public List<RestriccionModalidad> allActivasBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(RestriccionModalidad.class, "rm")
                .join("modalidadEstudio mod", "seccion sec")
                .filter("rm.estado", EstadoEnum.ACT)
                .in("sec.id", secciones);
        return all(sql);

    }

    @Override
    public void updateEstadoFechaUsuario(RestriccionModalidad restriccionModalidad) {
        Octavia octavia = Octavia.update(RestriccionModalidad.class);
        octavia.set(restriccionModalidad, "estado");
        octavia.set(restriccionModalidad, "usuarioModificacion");
        octavia.set(restriccionModalidad, "fechaModificacion");
        this.update(restriccionModalidad);
    }

}

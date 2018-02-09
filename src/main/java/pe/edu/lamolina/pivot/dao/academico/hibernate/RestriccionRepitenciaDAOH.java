package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.RestriccionModalidad;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.RestriccionRepitenciaDAO;

@Repository
public class RestriccionRepitenciaDAOH extends AbstractEasyDAO<RestriccionRepitencia> implements RestriccionRepitenciaDAO {

    public RestriccionRepitenciaDAOH() {
        super();
        setClazz(RestriccionRepitencia.class);
    }

    @Override
    public List<RestriccionRepitencia> allActivasBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(RestriccionRepitencia.class, "rp")
                .join("tipoRepitencia tr", "seccion sec")
                .filter("rp.estado", EstadoEnum.ACT)
                .filter("sec.id", seccion);
        return all(sql);
    }

    @Override
    public void updateEstadoFechaUsuario(RestriccionRepitencia restriccionRepitencia) {
        Octavia octavia = Octavia.update(RestriccionRepitencia.class);
        octavia.set(restriccionRepitencia, "estado");
        octavia.set(restriccionRepitencia, "usuarioModificacion");
        octavia.set(restriccionRepitencia, "fechaModificacion");
        this.update(restriccionRepitencia);
    }

}

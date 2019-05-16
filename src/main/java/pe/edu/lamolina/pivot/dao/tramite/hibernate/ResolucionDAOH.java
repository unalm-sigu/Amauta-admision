package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.pivot.dao.tramite.ResolucionDAO;

@Repository
public class ResolucionDAOH extends AbstractEasyDAO<Resolucion> implements ResolucionDAO {

    public ResolucionDAOH() {
        super();
        setClazz(Resolucion.class);
    }

    @Override
    public Resolucion find() {
        Octavia sql = new Octavia()
                .from(this.getClass())
                .join("oficina ofi", "tipoResolucion tr", "userRegistro ur")
                .left("reunionConsejo re", "userActualizacion ua")
                .left("ur.persona per", "ua.persona per 2");
        return this.find(sql);
    }

    @Override
    public List<Resolucion> allByDyna(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Resolucion.class, "t")
                .join("tipoResolucion", "oficina", "userRegistro ur")
                .join("ur.persona per")
                .left("reincorporaciones reis");
        return this.all(sql);
    }

    @Override
    public void updateResolucion(Resolucion resolucion) {
        Octavia octavia = Octavia.update(Resolucion.class);
        octavia.set(resolucion, "fecha");
        octavia.set(resolucion, "serie");
        octavia.set(resolucion, "numero");
        octavia.set(resolucion, "userActualizacion");
        octavia.set(resolucion, "fechaActualizacion");
        this.update(octavia);
    }

    @Override
    public void updateResolucionFile(Resolucion resolucion) {
        Octavia octavia = Octavia.update(Resolucion.class);
        octavia.set(resolucion, "rutaUrl");
        octavia.set(resolucion, "userActualizacion");
        octavia.set(resolucion, "fechaActualizacion");
        octavia.set(resolucion, "estado");
        this.update(octavia);
    }

    @Override
    public void updateEstado(Resolucion resolucion) {
        Octavia octavia = Octavia.update(Resolucion.class);
        octavia.set(resolucion, "userActualizacion");
        octavia.set(resolucion, "fechaActualizacion");
        octavia.set(resolucion, "estado");
        this.update(octavia);
    }

    @Override
    public void updateEstadoCicloRei(Resolucion resolucion) {
        Octavia octavia = Octavia.update(Resolucion.class);
        octavia.set(resolucion, "userActualizacion");
        octavia.set(resolucion, "fechaActualizacion");
        octavia.set(resolucion, "estado");
        octavia.set(resolucion, "cicloReincorporacion");
        this.update(octavia);
    }

    @Override
    public Resolucion findById(Long resolucion) {
        Octavia sql = new Octavia()
                .from(Resolucion.class)
                .join("oficina ofi", "tipoResolucion tr", "userRegistro ur")
                .left("reunionConsejo re")
                .filter("id", resolucion);
        return this.find(sql);
    }

}

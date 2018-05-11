package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.Tramite;

@Repository
public class TramiteDAOH extends AbstractEasyDAO<Tramite> implements TramiteDAO {

    public TramiteDAOH() {
        super();
        setClazz(Tramite.class);
    }

    @Override
    public List<Tramite> allByFilter(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(Tramite.class, "t")
                .join("compania", "persona", "alumno", "tipoTramite")
                .left("reincorporaciones");
        return this.all(sql);
    }

    @Override
    public void updateEstado(Tramite tramite) {
        Octavia octavia = Octavia.update(Tramite.class);
        octavia.set(tramite, "estado");
        octavia.set(tramite, "userModificacion");
        octavia.set(tramite, "fechaModificacion");
        this.update(octavia);
    }
}

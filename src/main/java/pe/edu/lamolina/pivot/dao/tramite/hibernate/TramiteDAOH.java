package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.tramite.Reincorporacion;
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
                .left("userRegistro ur", "ur.persona urp")
                .left("reincorporaciones")
                .filter("t.id", "!=", 5);
        return this.all(sql);
    }

    @Override
    public List<Tramite> allByTipoTramiteEstadoTramite(TipoTramiteEnum tipoTramiteEnum, EstadoTramiteEnum estadoTramiteEnum) {
        Octavia sql = Octavia.query()
                .selectDistinct("tr")
                .from(Reincorporacion.class, "r")
                .join("estadoTramite et")
                .join("tramite tr", "tr.persona", "tr.alumno", "tr.tipoTramite tt", "tr.userRegistro")
                .left("tr.userRespuesta", "tr.userModificacion");
        sql.filter("et.id", estadoTramiteEnum.getId());
        sql.filter("tt.codigo", tipoTramiteEnum);
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
    
    @Override
    public void updateObservacion(Tramite tramite) {
        Octavia octavia = Octavia.update(Tramite.class);
        octavia.set(tramite, "estado");
        octavia.set(tramite, "userModificacion");
        octavia.set(tramite, "fechaModificacion");
        this.update(octavia);
    }

    @Override
    public Tramite find(Long id) {
        Octavia sql = Octavia.query()
                .from(Tramite.class, "tr")
                .join("alumno alum", "tipoTramite tt", "cicloAcademico ca")
                .join("alum.carrera car", "car.facultad fac")
                .left("userRegistro user", "user.persona")
                .filter("tr.id", id);
        return find(sql);
    }

    @Override
    public Tramite findById(Tramite tramite) {
        Octavia sql = new Octavia()
                .from(Tramite.class, "tram")
                .join("cicloAcademico aca", "compania", "tipoTramite")
                .left("userRegistro user", "user.persona", "alumno alum")
                .filter("tram.id", tramite);
        return find(sql);
    }
}

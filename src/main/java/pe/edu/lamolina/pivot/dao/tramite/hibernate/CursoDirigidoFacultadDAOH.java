package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.tramite.CursoDirigidoFacultad;
import pe.edu.lamolina.pivot.dao.tramite.CursoDirigidoFacultadDAO;

@Repository
public class CursoDirigidoFacultadDAOH extends AbstractEasyDAO<CursoDirigidoFacultad> implements CursoDirigidoFacultadDAO {

    public CursoDirigidoFacultadDAOH() {
        super();
        setClazz(CursoDirigidoFacultad.class);
    }

    @Override
    public List<CursoDirigidoFacultad> allByDynatable(Facultad facultad, DynatableFilter filter) {
         DynatableSql sql = new DynatableSql(filter)
                .from(CursoDirigidoFacultad.class, "cudif")
                .join("curso cur", "facultad fac")
                .filter("fac.id", facultad)
                .searchFields("cur.codigo", "cur.nombre")
                .orderBy("cudif.id desc");
        return all(sql);
    }
}

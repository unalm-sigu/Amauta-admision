package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
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
    public List<CursoDirigidoFacultad> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CursoDirigidoFacultad.class, "cudif")
                .join("curso cur", "facultad fac")
                .searchFields("cur.codigo", "cur.nombre")
                .orderBy("cudif.id desc");

        sql.beginRelativeFilters();
        this.setFacultad(filter, sql);
        return all(sql);
    }

    private void setFacultad(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }

        for (String key : queries.keySet()) {
            if (!key.equals("facultad-dirigido")) {
                continue;
            }

            String values = (String) queries.get(key);
            if (values != null) {
                sql.filter("fac.id", values);
            }
        }
    }

    @Override
    public List<CursoDirigidoFacultad> allByFacultad(Facultad facultad) {
        Octavia sql = Octavia.query()
                .from(CursoDirigidoFacultad.class, "cudif")
                .join("curso cur", "facultad fac")
                .filter("fac.id", facultad);

        return all(sql);
    }
}

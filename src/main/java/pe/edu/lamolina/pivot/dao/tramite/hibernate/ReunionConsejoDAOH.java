package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.pivot.dao.tramite.ReunionConsejoDAO;

@Repository
public class ReunionConsejoDAOH extends AbstractEasyDAO<ReunionConsejo> implements ReunionConsejoDAO {

    public ReunionConsejoDAOH() {
        super();
        setClazz(ReunionConsejo.class);
    }

    @Override
    public ReunionConsejo findByFechaAndOficina(Date fecha, Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(ReunionConsejo.class, "rc")
                .join("oficina ofi")
                .complexFilter("date(rc.fecha)", fecha)
                .filter("ofi.id", oficina);

        return find(sql);
    }

    @Override
    public List<ReunionConsejo> allByOficina(Oficina oficina) {
        Octavia sql = Octavia.query()
                .from(ReunionConsejo.class, "rc")
                .join("oficina ofi")
                .filter("ofi.id", oficina);
        return all(sql);
    }

    @Override
    public List<ReunionConsejo> allByOficinas(List<Oficina> oficinas) {
        Octavia sql = Octavia.query()
                .from(ReunionConsejo.class, "rc")
                .join("oficina ofi")
                .in("ofi.id", oficinas);
        return all(sql);
    }

    @Override
    public List<ReunionConsejo> allByDynatable(DynatableFilter filter, List<Oficina> oficina) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ReunionConsejo.class, "rc")
                .join("rc.oficina ofi")
                .in("ofi.id", oficina)
                .searchFields("gh.codigo");
        return sql.all(getCurrentSession());
    }

}

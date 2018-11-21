package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.GrupoHorasRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoRegularExamenDAO;

@Repository
public class GrupoRegularExamenDAOH extends AbstractEasyDAO<GrupoRegularExamen> implements GrupoRegularExamenDAO {

    public GrupoRegularExamenDAOH() {
        super();
        setClazz(GrupoRegularExamen.class);
    }

    @Override
    public GrupoRegularExamen find(long id) {
        Octavia sql = Octavia.query()
                .from(GrupoRegularExamen.class, "gre")
                .join("letraGrupoRegular lgr", "grupoHoras gh")
                .filter("lgr.id", id);
        return find(sql);
    }

    @Override
    public List<GrupoRegularExamen> allByLetraGrupoRegularAndEstados(LetraGrupoRegular letrasGruposRegular,
            List<GrupoHorasRolExamenEstadoEnum> estados) {
        Octavia sql = Octavia.query()
                .from(GrupoRegularExamen.class, "gre")
                .join("letraGrupoRegular lgr", "grupoHoras gh")
                .filter("lgr.id", letrasGruposRegular)
                .in("gre.estado", estados);
        return all(sql);
    }

    @Override
    public void updateEstado(GrupoRegularExamen grupoRegularExamenUpd) {
        Octavia octavia = Octavia.update(GrupoRegularExamen.class);
        octavia.set(grupoRegularExamenUpd, "estado");
        octavia.set(grupoRegularExamenUpd, "usuarioExclusion");
        octavia.set(grupoRegularExamenUpd, "fechaExclusion");
        this.update(octavia);
    }

    @Override
    public Map<Long, Integer> countByLetrasGruposRegulares(List<LetraGrupoRegular> letraGrupoRegulars, GrupoHorasRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .select("lgr.id", "count(gre)")
                .from(GrupoRegularExamen.class, "gre")
                .join("letraGrupoRegular lgr")
                .in("gre.estado", estados)
                .in("lgr.id", letraGrupoRegulars)
                .groupBy("lgr.id");

        List<Object[]> resultado = sql.all(getCurrentSession());
        Map<Long, Integer> result = new HashMap<>();
        for (Object[] objects : resultado) {
            result.put(TypesUtil.getLong(objects[0]), TypesUtil.getInt(objects[1]));
        }
        return result;
    }

    @Override
    public void deleteByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  GrupoRegularExamen gre where gre.letraGrupoRegular.id=:LETRA_ID");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("LETRA_ID", letraGrupoRegular.getId());
        query.executeUpdate();
    }

}

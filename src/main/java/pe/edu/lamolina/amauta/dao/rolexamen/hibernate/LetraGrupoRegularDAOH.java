package pe.edu.lamolina.amauta.dao.rolexamen.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.amauta.dao.rolexamen.LetraGrupoRegularDAO;

@Repository
public class LetraGrupoRegularDAOH extends AbstractEasyDAO<LetraGrupoRegular> implements LetraGrupoRegularDAO {

    public LetraGrupoRegularDAOH() {
        super();
        setClazz(LetraGrupoRegular.class);
    }

    @Override
    public LetraGrupoRegular find(long id) {
        Octavia sql = Octavia.query()
                .from(LetraGrupoRegular.class, "lgr")
                .join("rolExamenes re", "userRegistro ur")
                //  .leftJoin("dia d", "hora h")
                .left("ur.persona urPer")
                .filter("lgr.id", id);
        return find(sql);
    }

    @Override
    public List<LetraGrupoRegular> allByRolExamenes(RolExamenes rolExamenes) {
        Octavia sql = Octavia.query()
                .from(LetraGrupoRegular.class, "lgr")
                .join("rolExamenes re", "grupoHorasExamen ghe", "ghe.dia", "ghe.horaInicio", "ghe.horaFin", "ghe.grupoHoras")
                .left("userRegistro ur", "ur.persona urPer")
                .filter("re.id", rolExamenes);
        return all(sql);
    }

    @Override
    public LetraGrupoRegular findByGrupoHorasExamen(GrupoHorasExamen grupoHorasExamen) {
        Octavia sql = Octavia.query()
                .from(LetraGrupoRegular.class, "lgr")
                .join("grupoHorasExamen gex", "rolExamenes re", "userRegistro ur")
                //  .leftJoin("dia d", "hora h")
                .left("ur.persona urPer")
                .filter("gex.id", grupoHorasExamen);
        return find(sql);
    }

    @Override
    public List<LetraGrupoRegular> allByRolExamenesForReporte(RolExamenes rol) {
        Octavia sql = Octavia.query()
                .from(LetraGrupoRegular.class, "lgr")
                .join("rolExamenes re", "userRegistro ur", "grupoHorasExamen ghe", "ghe.dia", "ghe.horaInicio hi", "ghe.horaFin", "ghe.grupoHoras")
                .left("ur.persona urPer")
                .filter("re.id", rol)
                .orderBy("lgr.letra asc", "ghe.fecha asc", "hi.codigo asc");

        return all(sql);
    }

    @Override
    public void deleteByRolExamenes(RolExamenes rolExamenes) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  LetraGrupoRegular lgr where lgr.rolExamenes.id=:ROLEX");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("ROLEX", rolExamenes.getId());
        query.executeUpdate();
    }

}

package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.pivot.dao.rolexamen.*;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;

@Repository
public class SeccionGrupoRegularDAOH extends AbstractEasyDAO<SeccionGrupoRegular> implements SeccionGrupoRegularDAO {

    public SeccionGrupoRegularDAOH() {
        super();
        setClazz(SeccionGrupoRegular.class);
    }

    @Override
    public SeccionGrupoRegular find(long id) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr", "seccion sec")
                .join("lgr.rolExamenes rex")
                .filter("sgr.id", id);
        return find(sql);
    }

    @Override
    public List<SeccionGrupoRegular> allByLetraGrupoRegularAndEstados(
            LetraGrupoRegular letrasGruposRegular, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr", "seccion sec")
                .left("docente doc", "doc.persona dper")
                .filter("lgr.id", letrasGruposRegular)
                .in("sgr.estado", estados);
        return all(sql);
    }

    public List<SeccionGrupoRegular> allByLetraGrupoRegularAndEstados(
            List<LetraGrupoRegular> letrasGruposRegular, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr", "seccion sec")
                .left("docente doc", "doc.persona dper")
                .in("lgr.id", letrasGruposRegular)
                .in("sgr.estado", estados);
        return all(sql);
    }

    @Override
    public List<SeccionGrupoRegular> allByLetraGrupoRegularAndSecciones(
            LetraGrupoRegular letrasGruposRegular, List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr", "seccion sec")
                .filter("lgr.id", letrasGruposRegular)
                .in("sec.id", secciones);
        return all(sql);
    }

    @Override
    public void updateEstado(SeccionGrupoRegular seccionGrupoRegularUpd) {
        Octavia octavia = Octavia.update(SeccionGrupoRegular.class);
        octavia.set(seccionGrupoRegularUpd, "estado");
        octavia.set(seccionGrupoRegularUpd, "usuarioExclusion");
        octavia.set(seccionGrupoRegularUpd, "fechaExclusion");
        this.update(octavia);
    }

    @Override
    public Map<Long, Integer> countByLetrasGruposRegulares(List<LetraGrupoRegular> letraGrupoRegulars, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .select("lgr.id", "count(sgr)")
                .from(SeccionGrupoRegular.class, "sgr")
                .join("letraGrupoRegular lgr")
                .in("sgr.estado", estados)
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
        strb.append(" delete from  SeccionGrupoRegular sgr where sgr.letraGrupoRegular.id=:LETRA_ID");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("LETRA_ID", letraGrupoRegular.getId());
        query.executeUpdate();
    }

}

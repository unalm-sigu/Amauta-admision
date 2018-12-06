package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;

@Repository
public class SeccionGrupoEspecialDAOH extends AbstractEasyDAO<SeccionGrupoEspecial> implements SeccionGrupoEspecialDAO {

    public SeccionGrupoEspecialDAOH() {
        super();
        setClazz(SeccionGrupoEspecial.class);
    }

    @Override
    public List<SeccionGrupoEspecial> allByDynatableAndRolExamenes(DynatableFilter filter, RolExamenes rolExamenes) {
        DynatableSql sql = new DynatableSql(filter)
                .from(SeccionGrupoEspecial.class, "sge")
                .join("rolExamenes re", "seccion sec", "userRegistro ur")
                .join("ur.persona per")
                .left("docente doc", "doc.persona dper","grupoHorasExamen ghe","ghe.dia","ghe.horaInicio","ghe.horaFin","ghe.grupoHoras")
                .left("aula au")
                .searchFields("sec.codigo", "sec.codigo2");

        return all(sql);
    }

    @Override
    public List<SeccionGrupoEspecial> allByRolExamenesAndEstados(RolExamenes rolExamenes, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoEspecial.class, "sce")
                .join("seccion sec", "rolExamenes re")
                .join("userRegistro ureg", "ureg.persona pureg")
                .left("aula au", "grupoHorasExamen ghe")
                .filter("re.id", rolExamenes)
                .in("sce.estado", estados);
        return all(sql);
    }

    @Override
    public List<SeccionGrupoEspecial> allByRolExamenes(RolExamenes rolExamenes) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoEspecial.class, "sce")
                .join("seccion sec", "rolExamenes re")
                .join("userRegistro ureg", "ureg.persona pureg")
                .left("aula au", "grupoHorasExamen ghe")
                .filter("re.id", rolExamenes);
        return all(sql);
    }

    @Override
    public void deleteByRolExamenes(RolExamenes rolExamenes) {
        StringBuilder strb = new StringBuilder();
        strb.append(" delete from  SeccionGrupoEspecial sge where sge.rolExamenes.id=:ROL_EXAMENES ");

        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("ROL_EXAMENES", rolExamenes.getId());
        query.executeUpdate();
    }

    @Override
    public void updateFechaExamen(SeccionGrupoEspecial SeccionGrupoEspecial) {
        Octavia octavia = Octavia.update(SeccionGrupoEspecial.class);
        octavia.set(SeccionGrupoEspecial, "grupoHorasExamen");
        this.update(octavia);
    }

}

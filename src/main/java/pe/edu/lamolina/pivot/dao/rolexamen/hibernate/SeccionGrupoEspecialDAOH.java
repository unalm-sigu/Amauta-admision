package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
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
    public SeccionGrupoEspecial findBySeccion(Seccion seccion, SeccionRolExamenEstadoEnum... seccionRolExamenEstadoEnum) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoEspecial.class, "sce")
                .join("seccion sec", "rolExamenes re", "aula au", "grupoHorasExamen ghe", "ghe.grupoHoras hg", "ghe.horaInicio hi", "ghe.horaFin hf")
                .join("userRegistro ureg", "ureg.persona pureg")
                .filter("sec.id", seccion)
                .in("sce.estado", seccionRolExamenEstadoEnum);
        return find(sql);
    }

    @Override
    public List<SeccionGrupoEspecial> allByDynatableAndRolExamenes(DynatableFilter filter, RolExamenes rolExamenes) {
        DynatableSql sql = new DynatableSql(filter)
                .from(SeccionGrupoEspecial.class, "sge")
                .join("rolExamenes re", "seccion sec", "userRegistro ur", "aula au")
                .join("ur.persona per")
                .left("docente doc", "doc.persona dper", "grupoHorasExamen ghe", "ghe.dia", "ghe.horaInicio", "ghe.horaFin", "ghe.grupoHoras")
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
    public List<SeccionGrupoEspecial> allByGrupoHorasExamenAndEstados(GrupoHorasExamen grupoHorasExamen, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoEspecial.class, "sce")
                .join("seccion sec", "rolExamenes re")
                .join("userRegistro ureg", "ureg.persona pureg")
                .left("aula au", "grupoHorasExamen ghe")
                .filter("ghe.id", grupoHorasExamen)
                .in("sce.estado", estados);
        return all(sql);
    }

    @Override
    public List<SeccionGrupoEspecial> allByRolExamenesForReporte(RolExamenes rol) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoEspecial.class, "sce")
                .join("docente doc", "doc.persona")
                .join("seccion sec", "rolExamenes re", "sec.grupoSeccion gs", "gs.curso cur")
                .join("aula au")
                .join("grupoHorasExamen ghe", "ghe.horaInicio", "ghe.horaFin")
                .filter("re.id", rol)
                .orderBy("cur.nombre asc", "sec.codigo2 asc");

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
    public void updateFechaExamen(SeccionGrupoEspecial seccionGrupoEspecial) {
        Octavia octavia = Octavia.update(SeccionGrupoEspecial.class);
        octavia.set(seccionGrupoEspecial, "grupoHorasExamen");
        this.update(octavia);
    }

    @Override
    public void updateEstadoExclusion(SeccionGrupoEspecial seccionGrupoEspecialUpd) {
        seccionGrupoEspecialUpd.setEstadoEnum(SeccionRolExamenEstadoEnum.EXC);
        Octavia octavia = Octavia.update(SeccionGrupoEspecial.class);
        octavia.set(seccionGrupoEspecialUpd, "estado");
        this.update(octavia);
    }

    @Override
    public void updateEstado(SeccionGrupoEspecial seccionGrupoEspecialUpd) {
        Octavia octavia = Octavia.update(SeccionGrupoEspecial.class);
        octavia.set(seccionGrupoEspecialUpd, "estado");
        this.update(octavia);
    }

}

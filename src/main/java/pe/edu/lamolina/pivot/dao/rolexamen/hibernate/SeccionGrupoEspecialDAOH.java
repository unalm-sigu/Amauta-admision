package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Insecto;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bean.RolExamenDocente;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import static pe.edu.lamolina.model.enums.TipoGestionEnum.PUB;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<SeccionGrupoEspecial> allByDynatableAndRolExamenes(DynatableFilter filter, RolExamenes rolExamenes) {
        DynatableSql sql = new DynatableSql(filter)
                .from(SeccionGrupoEspecial.class, "sge")
                .join("rolExamenes re", "seccion sec", "userRegistro ur", "aula au")
                .join("sec.grupoSeccion gpo", "gpo.curso cur")
                .join("ur.persona per")
                .left("sec.grupoHoras ghsec")
                .left("docente doc", "doc.persona dper", "grupoHorasExamen ghe", "ghe.dia", "ghe.horaInicio", "ghe.horaFin", "ghe.grupoHoras")
                .searchFields("sec.codigo2", "cur.codigo", "cur.nombre", "au.codigo")
                .searchComplexField("concat(coalesce(dper.paterno,''),' ',coalesce(dper.materno,''),' ',coalesce(dper.nombres,''))")
                .searchComplexField("concat(coalesce(dper.nombres,''),' ',coalesce(dper.paterno,''),' ',coalesce(dper.materno,''))")
                .filter("re.id", rolExamenes)
                .orderBy("cur.nombre");

        return all(sql);
    }

    @Override
    public List<SeccionGrupoEspecial> allByRolExamenesAndEstados(RolExamenes rolExamenes, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoEspecial.class, "sce")
                .join("seccion sec", "rolExamenes re")
                //.join("userRegistro ureg", "ureg.persona pureg")
                .left("aula au", "grupoHorasExamen ghe")
                .left("au.oficinaSupervisora ofsup")
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
                .filter("sce.estado", SeccionRolExamenEstadoEnum.ACT)
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
    public List<SeccionGrupoEspecial> allBySecciones(RolExamenes rolExamenes, List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoEspecial.class, "sce")
                .join("seccion sec", "rolExamenes re")
                .left("aula au", "grupoHorasExamen ghe")
                .filter("estado", SeccionRolExamenEstadoEnum.ACT)
                .filter("re.id", rolExamenes)
                .in("sec.id", secciones);
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
    public void updateFechaExamenAndAula(SeccionGrupoEspecial seccionGrupoEspecial) {
        Octavia octavia = Octavia.update(SeccionGrupoEspecial.class);
        octavia.set(seccionGrupoEspecial, "grupoHorasExamen");
        octavia.set(seccionGrupoEspecial, "aula");
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

    @Override
    public List<RolExamenDocente> allByDocenteAndCiclo(Docente docente, CicloAcademico cicloAcademico) {
        Octavia sql = new Octavia()
                .select("cur", "ghe", "au", "sec", "re.estado", "re.id", "re.nombre")
                .into(RolExamenDocente.class)
                .from(SeccionGrupoEspecial.class, "sge")
                .join("docente doc", "seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "aula au")
                .join("rolExamenes re", "re.eventoCicloAcademico eca", "eca.cicloAcademico ca")
                .join("grupoHorasExamen ghe", "ghe.dia di", "ghe.horaInicio hi", "ghe.horaFin hf")
                .filter("doc.id", docente)
                .filter("re.estado", PUB)
                .filter("ca.id", cicloAcademico);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<SeccionGrupoEspecial> allByGrupoHorasExamen(List<GrupoHorasExamen> grupoHorasExamenes) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoEspecial.class, "sce")
                .join("seccion sec", "rolExamenes re", "aula au", "grupoHorasExamen ghe", "ghe.grupoHoras hg", "ghe.horaInicio hi", "ghe.horaFin hf")
                .join("userRegistro ureg", "ureg.persona pureg")
                .in("ghe.id", grupoHorasExamenes);
        return all(sql);
    }

    @Override
    public SeccionGrupoEspecial findByRolExamanesSeccion(RolExamenes rol, Seccion seccion, SeccionRolExamenEstadoEnum... estados) {
        Octavia sql = Octavia.query()
                .from(SeccionGrupoEspecial.class, "sce")
                .join("seccion sec", "rolExamenes re", "aula au", "grupoHorasExamen ghe", "ghe.grupoHoras hg", "ghe.horaInicio hi", "ghe.horaFin hf")
                .join("userRegistro ureg", "ureg.persona pureg")
                .filter("sec.id", seccion)
                .in("sce.estado", estados)
                .filter("re.id", rol);

        return find(sql);
    }

    @Override
    public int saveList(List<SeccionGrupoEspecial> seccionesEspeciales) {
        if (seccionesEspeciales.isEmpty()) {
            return 0;
        }

        long t1 = System.currentTimeMillis();
        Insecto sql = Insecto.createInsert()
                .into(SeccionGrupoEspecial.class)
                .columns("estado", "fechaRegistro", "rolExamenes", "docente", "seccion",
                        "aula", "grupoHorasExamen", "userRegistro")
                .values(seccionesEspeciales);

        Query query = getCurrentSession().createSQLQuery(sql.toString());
        int rows = query.executeUpdate();

        long t2 = System.currentTimeMillis();
        logger.info("{} SeccionGrupoEspecial's insertados en {} mseg....", rows, (t2 - t1));
        return rows;
    }

}

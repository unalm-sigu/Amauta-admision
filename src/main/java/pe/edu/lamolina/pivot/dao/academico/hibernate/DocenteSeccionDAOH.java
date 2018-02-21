package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import org.hibernate.Query;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;

@Repository
public class DocenteSeccionDAOH extends AbstractEasyDAO<DocenteSeccion> implements DocenteSeccionDAO {

    public DocenteSeccionDAOH() {
        super();
        setClazz(DocenteSeccion.class);
    }

    @Override
    public DocenteSeccion find(long id) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "sec.seccionSuperior")
                .filter("ds.id", id);

        return find(sql);
    }

    @Override
    public List<DocenteSeccion> allByCargaAcademica(DynatableFilter filter, Docente docente, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(DocenteSeccion.class, "ds")
                .join("docente doc", "seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca")
                .leftJoin("sec.aula au", "cur.planCalificacion pc", "gs.planCalificacion pc2")
                .filter("doc.id", docente)
                .filter("ca.id", cicloAcademico)
                .orderBy("ds.id desc");

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allByDocente(Docente docente, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("doc.id", docente)
                .filter("ca.id", ciclo)
                .filter("ds.estado", EstadoEnum.ACT);

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allResponsablesByGpoSecciones(List<GrupoSeccion> gruposSeccion, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .in("gs.id", gruposSeccion)
                .filter("ca.id", ciclo)
                .filter("sec.tipoSeccion", "<>", TipoSeccionEnum.PCUR)
                .filter("ds.principal", 1)
                .filter("gs.estado", EstadoEnum.ACT)
                .filter("sec.estado", EstadoEnum.ACT)
                .filter("ds.estado", EstadoEnum.ACT);

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("sec.id", seccion);

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allActivosBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("sec.id", seccion)
                .filter("ds.estado", EstadoEnum.ACT.name());

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allPersonasActivasBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("ds.estado", EstadoEnum.ACT)
                .filter("sec.id", seccion);

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allPersonasActivasBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("ds.estado", EstadoEnum.ACT)
                .in("sec.id", secciones);

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allByGrupoSeccion(GrupoSeccion grupoSeccion) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("doc.persona per", "per.tipoDocumento")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .filter("gs.id", grupoSeccion)
                .orderBy("per.paterno");

        return all(sql);
    }

    @Override
    public DocenteSeccion findByFilter(Docente docente, Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("doc.persona per", "per.tipoDocumento");

        if (seccion != null) {
            sql.filter("sec.id", seccion);
        }
        if (docente != null) {
            sql.filter("doc.id", docente);
        }

        return find(sql);
    }

    @Override
    public List<DocenteSeccion> allByFilter(Docente docente, Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("doc.persona per", "per.tipoDocumento");

        if (seccion != null) {
            sql.filter("sec.id", seccion);
        }
        if (docente != null) {
            sql.filter("doc.id", docente);
        }

        return all(sql);
    }

    @Override
    public DocenteSeccion findByDocenteSeccion(Docente docente, Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("doc.persona per", "per.tipoDocumento")
                .filter("sec.id", seccion)
                .filter("doc.id", docente);

        return find(sql);
    }

    @Override
    public List<DocenteSeccion> allByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .leftJoin("doc.persona per", "per.tipoDocumento")
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allPendientePlan(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("docente doc", "doc.persona per")
                .join("seccion s", "s.grupoSeccion gs", "gs.cicloAcademico ca")
                .left("gs.planCalificacion pc")
                .filter("ca.id", ciclo)
                .filter("gs.estado", EstadoEnum.ACT)
                .filter("s.tipoSeccion", "<>", TipoSeccionEnum.PCUR)
                .filter("ds.principal", 1)
                .isNull("pc.id")
                .isNotNull("per.id");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<DocenteSeccion> allActivosBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("docente doc", "seccion s")
                .leftJoin("doc.persona per")
                .filter("ds.estado", EstadoEnum.ACT)
                .in("s.id", secciones);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<DocenteSeccion> allBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("docente doc", "seccion s")
                .leftJoin("doc.persona per")
                .in("s.id", secciones);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<DocenteSeccion> allPrincipalesBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("docente doc", "seccion s")
                .leftJoin("doc.persona per")
                .filter("ds.principal", 1)
                .in("s.id", secciones);
        return sql.all(getCurrentSession());
    }

    @Override
    public void deleteDocenteSeccionBySeccion(Seccion seccion) {
        StringBuilder strb = new StringBuilder();
        strb.append("delete DocenteSeccion ds   where ds.seccion.id=:prm_seccion ");
        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_seccion", seccion.getId());
        query.executeUpdate();
    }

    @Override
    public void updatePrincipal(DocenteSeccion docenteSeccion) {
        StringBuilder strb = new StringBuilder();
        strb.append("update DocenteSeccion set principal=:prm_principal where id=:prm_doc_seccion ");
        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_doc_seccion", docenteSeccion.getId());
        query.setParameter("prm_principal", docenteSeccion.getPrincipal());
        query.executeUpdate();
    }

    @Override
    public void updateDocente(DocenteSeccion docenteSeccion) {
        StringBuilder strb = new StringBuilder();
        strb.append("update DocenteSeccion ds set ds.docente.id=:prm_docente where ds.id=:prm_doc_seccion ");
        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_doc_seccion", docenteSeccion.getId());
        query.setParameter("prm_docente", docenteSeccion.getDocente().getId());
        query.executeUpdate();
    }

    @Override
    public void updatePorcentajeAvance(DocenteSeccion docenteSeccion) {
        StringBuilder strb = new StringBuilder();
        strb.append("update DocenteSeccion ds set ds.porcentajeCarga=:porcentaje_avance where ds.id=:prm_doc_seccion ");
        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("prm_doc_seccion", docenteSeccion.getId());
        query.setParameter("porcentaje_avance", docenteSeccion.getPorcentajeCarga());
        query.executeUpdate();
    }

    @Override
    public void updateFechaInicio(DocenteSeccion docenteSeccion) {
        /*  Octavia octavia = Octavia.update(DocenteSeccion.class);
        octavia.set(docenteSeccion, "fechaInicio");
        this.update(docenteSeccion);
         */
        StringBuilder strb = new StringBuilder();
        strb.append("update DocenteSeccion ds set ds.fechaInicio=:fecha_inicio_prm where ds.id=:id_prm ");
        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("fecha_inicio_prm", docenteSeccion.getFechaInicio());
        query.setParameter("id_prm", docenteSeccion.getId());
        query.executeUpdate();
    }

    @Override
    public void updateFechaFin(DocenteSeccion docenteSeccion) {
        /*
        Octavia octavia = Octavia.update(DocenteSeccion.class);
        octavia.set(docenteSeccion, "fechaFin");
        System.out.println(octavia.toString());
        this.update(octavia);*/

        StringBuilder strb = new StringBuilder();
        strb.append("update DocenteSeccion ds set ds.fechaFin=:fecha_fin_prm where ds.id=:id_prm ");
        Query query = getCurrentSession().createQuery(strb.toString());
        query.setParameter("fecha_fin_prm", docenteSeccion.getFechaFin());
        query.setParameter("id_prm", docenteSeccion.getId());
        query.executeUpdate();
    }

}

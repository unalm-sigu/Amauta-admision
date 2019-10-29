package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.math.BigDecimal;
import java.util.Arrays;
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
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.DocenteEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

@Repository
public class DocenteSeccionDAOH extends AbstractEasyDAO<DocenteSeccion> implements DocenteSeccionDAO {

    public DocenteSeccionDAOH() {
        super();
        setClazz(DocenteSeccion.class);
    }

    @Override
    public List<DocenteSeccion> allByModalidadEstudioCicloAcademico(ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc", "cur.modalidadEstudio med")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("ca.id", cicloAcademico)
                .filter("med.id", modalidadEstudio)
                .isNotNull("ds.porcentajeCarga")
                .filter("sec.estado", EstadoEnum.ACT)
                .filter("ds.estado", EstadoEnum.ACT);

        return all(sql);
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
        return this.allByDocente(Arrays.asList(docente), ciclo);
    }

    @Override
    public List<DocenteSeccion> allByDocente(List<Docente> docentes, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .leftJoin("gs.anexoBoletin ab", "ab.anexoSuperior absup")
                .in("doc.id", docentes)
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
    public List<DocenteSeccion> allDocentesPrincipalesByGpoSecciones(List<GrupoSeccion> gruposSeccion) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .in("gs.id", gruposSeccion)
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
                .filter("sec.id", seccion)
                .orderBy("ds.estado", "ds.id");
        return all(sql);
    }

    @Override
    public DocenteSeccion findPrincipalBySeccion(Seccion seccion) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "docente doc")
                .leftJoin("doc.persona per", "per.tipoDocumento")
                .filter("sec.id", seccion)
                .filter("ds.estado", EstadoEnum.ACT)
                .filter("ds.principal", BigDecimal.ONE.intValue());
        return find(sql);
    }

    @Override
    public List<DocenteSeccion> allPersonasActivasBySecciones(Seccion seccion) {
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
                .join("doc.persona per")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "per.tipoDocumento")
                .filter("ds.estado", EstadoEnum.ACT)
                .in("sec.id", secciones);

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allActivosBySecciones(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("ds.estado", EstadoEnum.ACT)
                .in("sec.id", secciones);
        return sql.all(getCurrentSession());
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
                .filter("ds.estado", EstadoEnum.ACT)
                .orderBy("ds.fechaInicio");

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allActivosByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("ds.estado", EstadoEnum.ACT)
                .filter("sec.estado", EstadoEnum.ACT)
                .filter("ca.id", cicloAcademico);

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
    public DocenteSeccion findWithPersonaByDocenteSeccion(Docente docente, Seccion seccion) {
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
                .join("gs.anexoBoletin anx", "anx.anexoSuperior")
                .leftJoin("doc.persona per", "per.tipoDocumento", "doc.departamentoAcademico dpa")
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allByCiclo(CicloAcademico ciclo, EstadoEnum... estadoEnum) {
        List<EstadoEnum> estados = Arrays.asList(estadoEnum);
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .leftJoin("doc.persona per", "per.tipoDocumento", "doc.departamentoAcademico dpa")
                .in("ds.estado", estados)
                .filter("ca.id", ciclo);

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allByCicloAula(CicloAcademico ciclo, Aula aula, OficinaEnum oficinaEnum, EstadoEnum... estadoEnum) {
        List<EstadoEnum> estados = Arrays.asList(estadoEnum);
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("sec.aula au")
                .leftJoin("doc.persona per", "per.tipoDocumento", "doc.departamentoAcademico dpa")
                .leftJoin("au.oficinaSupervisora osup")
                .in("ds.estado", estados)
                .filter("ca.id", ciclo);

        if (aula == null) {
            sql.filter("osup.codigo", oficinaEnum);
        } else {
            sql.filter("au.id", aula);
        }

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
                .filter("ds.estado", EstadoEnum.ACT)
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

    @Override
    public List<DocenteSeccion> allPrincipalesBySeccion(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .leftJoin("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("ds.estado", EstadoEnum.ACT)
                .filter("ds.principal", 1)
                .in("sec.id", secciones);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<DocenteSeccion> allSinNNByCicloModalidad(CicloAcademico cicloAcademico) {

        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("gs.anexoBoletin anx", "anx.anexoSuperior")
                .join("cur.departamentoAcademico da", "da.facultad")
                .join("doc.modalidadEstudio me", "cur.modalidadEstudio")
                .leftJoin("doc.departamentoAcademico")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("ca.id", cicloAcademico)
                .filter("doc.estado", DocenteEstadoEnum.ACT)
                .filter("doc.codigo", "<>", Constantine.DOCENTE_INDETERMINADO)
                .filter("ds.estado", EstadoEnum.ACT)
                .filter("sec.estado", EstadoEnum.ACT);

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allActivosByDocenteCiclo(Docente docente, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .join("cur.modalidadEstudio")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("ds.estado", EstadoEnum.ACT)
                .filter("sec.estado", EstadoEnum.ACT)
                .filter("doc.id", docente)
                .filter("ca.id", cicloAcademico)
                .orderBy("cur.nombre", "sec.codigo2");

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allSeccionByClone(List<Seccion> secciones) {

        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("ds.estado", EstadoEnum.ACT)
                .filter("sec.estado", EstadoEnum.ACT)
                .in("sec.id", secciones)
                .orderBy("cur.nombre", "sec.codigo2");
        return all(sql);

    }

    @Override
    public void deleteAllByCiclo(CicloAcademico ciclo) {

        StringBuilder sql = new StringBuilder();
        sql.append(" DELETE ").append(DocenteSeccion.class.getName()).append(" dos ")
                .append(" WHERE EXISTS  ( ")
                .append("    SELECT 1 FROM ").append(Seccion.class.getName()).append(" sec ")
                .append("      JOIN sec.grupoSeccion gs ")
                .append("      JOIN gs.cicloAcademico ci ")
                .append("     WHERE ci.id = :CICLO ")
                .append("       AND dos.seccion.id = sec.id ")
                .append(" ) ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("CICLO", ciclo.getId());
        query.executeUpdate();
    }

    @Override
    public List<DocenteSeccion> allSinNNByCicloModalidadReporte(CicloAcademico cicloAcademico, ModalidadEstudio modalidadEstudio) {

        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .join("doc.modalidadEstudio me", "cur.modalidadEstudio")
                .leftJoin("doc.departamentoAcademico daa", "daa.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("ca.id", cicloAcademico)
                .filter("doc.estado", DocenteEstadoEnum.ACT)
                .filter("doc.codigo", "<>", Constantine.DOCENTE_INDETERMINADO)
                .filter("me.id", modalidadEstudio)
                .filter("ds.estado", EstadoEnum.ACT)
                .filter("sec.estado", EstadoEnum.ACT);

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allActivosBySeccionesOrderPrincipalLimit(List<Seccion> secciones) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .leftJoin("sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("ds.estado", EstadoEnum.ACT)
                .in("sec.id", secciones)
                .orderBy("ds.principal desc");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<DocenteSeccion> allByGrupoSeccionForUpdateFecha(GrupoSeccion grupoSeccion) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .filter("gs.id", grupoSeccion)
                .filter("ds.estado", EstadoEnum.ACT)
                .filter("sec.estado", EstadoEnum.ACT);
        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allResponsableBySeccionCiclo(List<Seccion> secciones, CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .leftJoin("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("ca.id", ciclo)
                .in("sec.id", secciones)
                .filter("ds.principal", 1)
                .filter("gs.estado", EstadoEnum.ACT)
                .filter("sec.estado", EstadoEnum.ACT)
                .filter("ds.estado", EstadoEnum.ACT);
        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allByCiclo(CicloAcademico ciclo, List<EstadoEnum> docSecEstado, List<SeccionEstadoEnum> secEstado) {
        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("gs.anexoBoletin ab", "ab.anexoSuperior abs", "cur.departamentoAcademico da")
                .leftJoin("sec.aula au", "sec.grupoHoras gh", "doc.persona per", "sec.seccionSuperior", "da.facultad")
                .in("ds.estado", docSecEstado)
                .in("sec.estado", secEstado)
                .filter("ca.id", ciclo)
                .orderBy("sec.codigo2");

        return all(sql);
    }

    @Override
    public List<DocenteSeccion> allDocenteSeccionPrincipalBySeccion(List<Seccion> secciones) {

        Octavia sql = Octavia.query()
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico ca", "docente doc")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("gs.planCalificacion pc", "cur.planCalificacion pc2", "cur.planCalificacionRegular pcr", "sec.seccionSuperior")
                .leftJoin("sec.aula au", "au.aulaSuperior ausu", "sec.grupoHoras gh", "doc.persona per", "per.tipoDocumento")
                .filter("ds.principal", 1)
                .in("sec.id", secciones);
        return all(sql);
    }
}

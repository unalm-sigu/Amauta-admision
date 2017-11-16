package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import java.util.Map;
import pe.albatross.zelpers.dao.AbstractDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Docente;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;

@Repository
public class DocenteSeccionDAOH extends AbstractDAO<DocenteSeccion> implements DocenteSeccionDAO {

    public DocenteSeccionDAOH() {
        super();
        setClazz(DocenteSeccion.class);
    }

    @Override
    public DocenteSeccion find(long id) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("dc")
                .parents("seccion sec", "_sec.grupoSeccion gs", "_gs.curso cur")
                .parents("docente doc", "left _sec.seccionSuperior")
                .parents("left _gs.planCalificacion pc")
                .parents("left _cur.departamentoAcademico da", "left _da.facultad")
                .filter("dc.id", id);
        return this.find(sqlUtil);
    }

    @Override
    public List<DocenteSeccion> allByCargaAcademica(DynatableFilter filter, Docente docente, CicloAcademico cicloAcademico) {
        filter.setAlias("dc");
        filter.setParents("docente doc", "seccion sec", "_sec.grupoSeccion gs", "left _sec.aula au",
                "_gs.curso cur", "left _cur.planCalificacion pc", "left _gs.planCalificacion pc2", "_gs.cicloAcademico ca");
        filter.filterFix("doc.id", docente.getId());
        filter.filterFix("ca.id", cicloAcademico.getId());

        filter.setTotal(this.count(filter));
        filter.setFiltered(this.countByFilter(filter));

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil(filter.getAlias());
        sqlUtil.parents(filter.getParents());

        Map filtersFix = filter.getFiltersFixed();
        if (filtersFix != null) {
            for (Object key : filtersFix.keySet()) {
                this.filterFixed(sqlUtil, (String) key, filtersFix.get(key));
            }
        }
        Map filterFixIn = filter.getFiltersInFixed();
        if (filterFixIn != null) {
            for (Object key : filterFixIn.keySet()) {
                this.filterInFixed(sqlUtil, (String) key, (List) filterFixIn.get(key));
            }
        }
        this.filter(sqlUtil, filter.getFields(), filter.getSearchValue());
        sqlUtil.setFirstResult(filter.getOffset())
                .setPageSize(filter.getPerPage());

        return this.all(sqlUtil);
    }

    @Override
    public List<DocenteSeccion> allByDocente(Docente docente, CicloAcademico ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("dc")
                .parents("docente doc", "seccion sec", "_sec.grupoSeccion gs", "_gs.curso cur", "left _sec.aula au", "left _sec.grupoHoras gh")
                .parents("left _cur.planCalificacion pc", "left _cur.planCalificacionRegular pcr", "left _gs.planCalificacion pc2")
                .parents("left _doc.persona per", "left _per.tipoDocumento")
                .filter("doc.id", docente.getId())
                .filter("gs.cicloAcademico.id", ciclo)
                .filter("dc.estado", EstadoEnum.ACT.name());

        /*
        if (ds.getCicloAcademico().isTipoNivelacion()) {
            sqlUtil.filter("pc.tipoCiclo", ds.getCicloAcademico().getTipo());
        } else if (ds.getCicloAcademico().isTipoRegular()) {
            sqlUtil.filter("pcr.tipoCiclo", ds.getCicloAcademico().getTipo());
        }
         */
        return this.all(sqlUtil);
    }

    @Override
    public List<DocenteSeccion> allResponsablesByGpoSecciones(List<GrupoSeccion> gruposSeccion, CicloAcademico ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("dc")
                .parents("docente doc", "seccion sec", "_sec.grupoSeccion gs", "_gs.curso cur", "left _sec.aula au", "left _sec.grupoHoras gh")
                .parents("left _cur.planCalificacion pc", "left _cur.planCalificacionRegular pcr", "left _gs.planCalificacion pc2")
                .parents("left _doc.persona per", "left _per.tipoDocumento")
                .filterIn("gs.id", gruposSeccion)
                .filter("gs.cicloAcademico.id", ciclo)
                .filter("sec.tipoSeccion <>", TipoSeccionEnum.PCUR.name())
                .filter("dc.principal", 1)
                .filter("gs.estado", EstadoEnum.ACT.name())
                .filter("sec.estado", EstadoEnum.ACT.name())
                .filter("dc.estado", EstadoEnum.ACT.name());

        return this.all(sqlUtil);
    }

    @Override
    public List<DocenteSeccion> allBySeccion(Seccion seccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("dc")
                .parents("seccion sec", "_sec.grupoSeccion gs", "_gs.curso cur")
                .parents("left _cur.planCalificacion pc", "left _gs.planCalificacion pc2")
                .parents("docente doc", "left _doc.persona")
                .filter("sec.id", seccion.getId());
        return this.all(sqlUtil);
    }

    @Override
    public List<DocenteSeccion> allPersonasActivasBySeccion(Seccion seccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("dc")
                .parents("seccion sec", "_sec.grupoSeccion gs", "_gs.curso cur")
                .parents("left _cur.planCalificacion pc", "left _gs.planCalificacion pc2")
                .parents("docente doc", "_doc.persona")
                .filter("dc.estado", EstadoEnum.ACT.name())
                .filter("sec.id", seccion);
        return this.all(sqlUtil);
    }

    @Override
    public List<DocenteSeccion> allPersonasActivasBySecciones(List<Seccion> secciones) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("dc")
                .parents("seccion sec", "_sec.grupoSeccion gs", "_gs.curso cur")
                .parents("left _cur.planCalificacion pc", "left _gs.planCalificacion pc2")
                .parents("docente doc", "_doc.persona")
                .filter("dc.estado", EstadoEnum.ACT.name())
                .filterIn("sec.id", secciones);

        return this.all(sqlUtil);
    }

    @Override
    public List<DocenteSeccion> allByGrupoSeccion(GrupoSeccion grupoSeccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("dc")
                .parents("seccion sec", "_sec.grupoSeccion gs", "_gs.curso cur")
                .parents("left _cur.planCalificacion pc", "left _gs.planCalificacion pc2")
                .parents("docente doc", "_doc.persona dper")
                .filter("gs.id", grupoSeccion.getId());
        sqlUtil.orderBy("dper.paterno");
        return this.all(sqlUtil);
    }

    @Override
    public DocenteSeccion findByFilter(Docente docente, Seccion seccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ds")
                .parents("seccion sec", "docente do")
                .parents("_do.persona per");
        if (seccion != null) {
            sqlUtil.filter("sec.id", seccion.getId());
        }
        if (docente != null) {
            sqlUtil.filter("do.id", docente.getId());
        }
        return this.find(sqlUtil);
    }

    @Override
    public List<DocenteSeccion> allByFilter(Docente docente, Seccion seccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ds")
                .parents("seccion sec", "docente do")
                .parents("_do.persona per");
        if (seccion != null) {
            sqlUtil.filter("sec.id", seccion.getId());
        }
        if (docente != null) {
            sqlUtil.filter("do.id", docente.getId());
        }
        return this.all(sqlUtil);
    }

    @Override
    public DocenteSeccion findByDocenteSeccion(Docente profe, Seccion seccion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ds")
                .parents("seccion sec", "docente do")
                .filter("sec.id", seccion.getId())
                .filter("do.id", profe.getId());

        return this.find(sqlUtil);
    }

    @Override
    public List<DocenteSeccion> allByCiclo(CicloAcademico ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("dc")
                .parents("seccion sec", "_sec.grupoSeccion gs", "_gs.curso cur", "_gs.cicloAcademico ca")
                .parents("docente doc", "left _doc.persona dper")
                .filter("ca.id", ciclo);
        return this.all(sqlUtil);
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
                .join("docente doc","seccion s")
                .leftJoin("doc.persona per")
                .filter("ds.estado", EstadoEnum.ACT)
                .in("s.id", secciones);
        return sql.all(getCurrentSession());
    }

}

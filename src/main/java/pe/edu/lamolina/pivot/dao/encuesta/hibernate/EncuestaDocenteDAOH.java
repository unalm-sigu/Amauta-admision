package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.EncuestaEstadoEnum;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.CER;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.FECH;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ANU;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.TEO;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaDocenteDAO;

@Repository
public class EncuestaDocenteDAOH extends AbstractEasyDAO<EncuestaDocente> implements EncuestaDocenteDAO {

    public EncuestaDocenteDAOH() {
        super();
        setClazz(EncuestaDocente.class);
    }

    @Override
    public List<EncuestaDocente> allAnuladaByModalidadEstudioCicloAcademico(ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(EncuestaDocente.class, "ed")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo", "modalidadEstudio me")
                .join("docenteSeccion ds", "ds.docente doc", "doc.persona per")
                .join("ds.seccion sec", "sec.grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("per.tipoDocumento tdoc")
                .filter("me.id", modalidadEstudio)
                .filter("ed.estado", ANU);
        return all(sql);
    }

    @Override
    public List<EncuestaDocente> allAnuladaByModalidadEstudioDocenteCicloAcademico(ModalidadEstudio modalidadEstudio, Docente docente, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(EncuestaDocente.class, "ed")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo", "modalidadEstudio me")
                .join("docenteSeccion ds", "ds.docente doc", "doc.persona per")
                .join("ds.seccion sec", "sec.grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("per.tipoDocumento tdoc")
                .filter("doc.id", docente)
                .filter("me.id", modalidadEstudio)
                .filter("ed.estado", ANU);
        return all(sql);
    }

    @Override
    public List<EncuestaDocente> allByDynatable(DynatableFilter filter, CicloAcademico ciclo, List<DepartamentoAcademico> departamentos, Docente docente) {
        DynatableSql sql = new DynatableSql(filter)
                .from(EncuestaDocente.class, "ed")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("docenteSeccion ds", "ds.docente doc", "doc.persona per")
                .join("ds.seccion sec", "sec.grupoSeccion gs", "gs.curso cur")
                .join("doc.departamentoAcademico da", "da.facultad fa")
                .join("modalidadEstudio me")
                .leftJoin("per.tipoDocumento tdoc", "sec.grupoHoras gh")
                .filter("ciclo.id", ciclo)
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchFields("cur.nombre", "cur.codigo", "sec.codigo2", "gh.codigo", "doc.codigo")
                .orderBy("ed.id DESC");

        if (docente != null) {
            sql.filter("ee.estado", EncuestaEstadoEnum.ACT);
            sql.filter("doc.id", docente);

        } else if (!departamentos.isEmpty()) {
            sql.in("da.id", departamentos);
        }

        sql.beginRelativeFilters();
        setCondicionEstado(filter, sql);

        return all(sql);
    }

    private void setCondicionEstado(DynatableFilter filter, DynatableSql sql) {
        Map<String, Object> queries = filter.getQueries();
        if (queries == null) {
            return;
        }
        for (String key : queries.keySet()) {
            if (!key.equals("estado")) {
                continue;
            }

            String values = (String) queries.get(key);
            if (values.equals("activo")) {
                sql.filter("ed.estado", ACT);

            } else if (values.equals("anulado")) {
                sql.filter("ed.estado", ANU);

            } else if (values.equals("innecesario")) {
                sql.filter("ed.estado", TEO);

            } else if (values.equals("sinperiodo")) {
                sql.filter("ed.estado", FECH);

            } else if (values.equals("cerrado")) {
                sql.filter("ed.estado", CER);

            } else if (values.equals("encuestable")) {
                sql.in("ed.estado", Arrays.asList(CER, ACT));

            } else if (values.equals("encuestado")) {
                sql.filter("ed.alumnosEncuestados", ">", 0);
            }
        }

        for (String key : queries.keySet()) {
            if (!key.equals("modalidad")) {
                continue;
            }

            String values = (String) queries.get(key);
            if (values.equals("posgrados")) {
                sql.filter("me.codigo", ModalidadEstudioEnum.EPG);

            } else if (values.equals("pregrados")) {
                sql.filter("me.codigo", ModalidadEstudioEnum.PRE);
            }
        }

        for (String key : queries.keySet()) {
            if (!key.equals("dictado")) {
                continue;
            }

            String values = (String) queries.get(key);
            if (values.equals("modulares")) {
                sql.filter("gs.tipoDictado", TipoDictadoGrupoSeccionEnum.MOD);

            } else if (values.equals("semestrales")) {
                sql.filter("gs.tipoDictado", TipoDictadoGrupoSeccionEnum.SEM);
            }
        }

        for (String key : queries.keySet()) {
            if (!key.equals("facultad")) {
                continue;
            }

            String values = (String) queries.get(key);
            sql.filter("fa.id", values);
        }
        for (String key : queries.keySet()) {
            if (!key.equals("departamento")) {
                continue;
            }

            String values = (String) queries.get(key);
            sql.filter("da.id", values);
        }

    }

    @Override
    public List<EncuestaDocente> allByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil, List<DepartamentoAcademico> departamentos) {
        Octavia sql = Octavia.query()
                .from(EncuestaDocente.class, "ed")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("docenteSeccion ds", "ds.docente doc", "doc.persona per")
                .join("ds.seccion sec", "sec.grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("per.tipoDocumento tdoc")
                .filter("ee.id", encuestaEstudiantil);

        if (!departamentos.isEmpty()) {
            sql.in("da.id", departamentos);
        }

        return all(sql);
    }

    @Override
    public EncuestaDocente findEncuestaDocente(EncuestaDocente encuestaForm) {
        Octavia sql = Octavia.query()
                .from(EncuestaDocente.class, "ed")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("docenteSeccion ds", "ds.docente doc", "doc.persona per")
                .join("ds.seccion sec", "sec.grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("per.tipoDocumento tdoc")
                .filter("ed.id", encuestaForm);
        return find(sql);
    }

    @Override
    public void deleteByEncuestaTipoDocente(EncuestaEstudiantil encuesta) {
        String strQuery = "delete from EncuestaDocente ed where ed.encuestaEstudiantil.id = :ENCUESTA ";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("ENCUESTA", encuesta.getId());
        query.executeUpdate();
    }

    @Override
    public EncuestaDocente findByDocenteSeccion(DocenteSeccion docSec) {

        Octavia sql = Octavia.query(EncuestaDocente.class, "ed")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("docenteSeccion ds", "ds.docente doc", "doc.persona per")
                .join("ds.seccion sec", "sec.grupoSeccion gs", "gs.curso cur")
                .join("doc.departamentoAcademico da", "da.facultad fa")
                .leftJoin("per.tipoDocumento tdoc", "sec.grupoHoras gh")
                .filter("ds.id", docSec);
        return find(sql);
    }

    @Override
    public List<EncuestaDocente> allByDynatableDocente(DynatableFilter filter, CicloAcademico ciclo, Docente docente) {
        DynatableSql sql = new DynatableSql(filter)
                .from(EncuestaDocente.class, "ed")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("docenteSeccion ds", "ds.docente doc", "doc.persona per")
                .join("ds.seccion sec", "sec.grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad fa")
                .leftJoin("per.tipoDocumento tdoc", "sec.grupoHoras gh")
                .filter("ciclo.id", ciclo)
                .filter(("doc.id"), docente)
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchFields("da.nombre", "cur.nombre", "cur.codigo", "fa.nombre", "sec.codigo2", "gh.codigo", "doc.codigo")
                .orderBy("ed.id");
        sql.beginRelativeFilters();

        setCondicionEstado(filter, sql);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<EncuestaDocente> allByEncuestaEstudiantilCiclo(EncuestaEstudiantil encuestaEstudiantil, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(EncuestaDocente.class, "ed")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("docenteSeccion ds", "ds.docente doc", "doc.persona per")
                .join("ds.seccion sec", "sec.grupoSeccion gs", "gs.curso cur", "gs.cicloAcademico cic")
                .join("cur.departamentoAcademico da", "da.facultad")
                .join("gs.anexoBoletin anx", "anx.anexoSuperior")
                .leftJoin("per.tipoDocumento tdoc")
                .filter("cic.id", cicloAcademico)
                .filter("ee.id", encuestaEstudiantil);

        return all(sql);
    }

}

package pe.edu.lamolina.amauta.dao.encuesta.hibernate;

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
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.ANU;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.CER;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.FECH;
import static pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum.TEO;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaCursoDAO;

@Repository
public class EncuestaCursoDAOH extends AbstractEasyDAO<EncuestaCurso> implements EncuestaCursoDAO {

    public EncuestaCursoDAOH() {
        super();
        setClazz(EncuestaCurso.class);
    }

    @Override
    public List<EncuestaCurso> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, boolean noEsSimultaneo) {

        DynatableSql sql;
        if (noEsSimultaneo) {
            Octavia subquerySecciones = Octavia.query()
                    .from(Seccion.class, "se")
                    .join("grupoSeccion gpo")
                    .left("grupoHoras gh")
                    .filter("se.estado", SeccionEstadoEnum.ACT)
                    .filter("se.tipoSeccion", "<>", TipoSeccionEnum.PCUR);

            sql = new DynatableSql(filter)
                    .from(EncuestaCurso.class, "ec")
                    .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                    .join("grupoSeccion gs", "gs.curso cur", "modalidadEstudio me")
                    .join("cur.departamentoAcademico da", "da.facultad fa")
                    .filter("ciclo.id", cicloAcademico)
                    .searchFields("cur.nombre", "cur.codigo")
                    .__().searchSubquery(subquerySecciones)
                    .__().subqueryLinkedBy("gs.id", "gpo.id")
                    .__().searchSubqueryFields("se.codigo2", "gh.codigo")
                    .orderBy("ec.id DESC");

        } else {
            sql = new DynatableSql(filter)
                    .from(EncuestaCurso.class, "ec")
                    .join("modalidadEstudio me", "encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                    .join("encuestaDocente ed", "ed.docenteSeccion ds", "ds.docente doc")
                    .join("doc.persona per", "per.tipoDocumento")
                    .join("ds.seccion sec", "sec.grupoHoras gh")
                    .join("sec.grupoSeccion gs", "gs.curso cur")
                    .join("cur.departamentoAcademico da", "da.facultad fa")
                    .filter("ciclo.id", cicloAcademico)
                    .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                    .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                    .searchFields("cur.nombre", "cur.codigo", "sec.codigo2", "doc.codigo", "gh.codigo")
                    .orderBy("ec.id DESC");
        }
        sql.beginRelativeFilters();

        setCondicionEstado(filter, sql);

        return sql.all(getCurrentSession());
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
    public List<EncuestaCurso> allByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil, boolean esSimultaneo) {
        Octavia sql;
        if (esSimultaneo) {
            sql = Octavia.query()
                    .from(EncuestaCurso.class, "ec")
                    .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                    .join("encuestaDocente ed", "ed.docenteSeccion ds", "ds.seccion sec")
                    .join("sec.grupoSeccion gs", "gs.curso cur")
                    .join("cur.departamentoAcademico da", "da.facultad")
                    .filter("ee.id", encuestaEstudiantil);
        } else {
            sql = Octavia.query()
                    .from(EncuestaCurso.class, "ec")
                    .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                    .join("grupoSeccion gs", "gs.curso cur")
                    .join("cur.departamentoAcademico da", "da.facultad")
                    .filter("ee.id", encuestaEstudiantil);
        }

        return all(sql);
    }

    @Override
    public EncuestaCurso findByEncuestaCurso(EncuestaCurso encuestaForm) {
        Octavia sql = Octavia.query()
                .from(EncuestaCurso.class, "ec")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad")
                .filter("ec.id", encuestaForm);
        return find(sql);
    }

    @Override
    public EncuestaCurso findByEncuestaDocente(EncuestaDocente encuestaDocente) {
        Octavia sql = Octavia.query()
                .from(EncuestaCurso.class, "ec")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("encuestaDocente ed")
                .filter("ed.id", encuestaDocente);
        return find(sql);
    }

    @Override
    public void deleteByEncuestaTipoCurso(EncuestaEstudiantil encuesta) {
        String strQuery = "delete from EncuestaCurso ec where ec.encuestaEstudiantil.id=:enc";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("enc", encuesta.getId());
        query.executeUpdate();
    }

}

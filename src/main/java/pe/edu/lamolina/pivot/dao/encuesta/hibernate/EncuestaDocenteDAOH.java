package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
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
                .filter("ed.estado", EstadoEnum.ANU);
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
                .filter("ed.estado", EstadoEnum.ANU);
        return all(sql);
    }

    @Override
    public List<EncuestaDocente> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(EncuestaDocente.class, "ed")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("docenteSeccion ds", "ds.docente doc", "doc.persona per")
                .join("ds.seccion sec", "sec.grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad fa")
                .leftJoin("per.tipoDocumento tdoc", "sec.grupoHoras gh")
                .filter("ciclo.id", cicloAcademico)
                .searchComplexField("concat(coalesce(per.paterno,''),' ',coalesce(per.materno,''),' ',coalesce(per.nombres,''))")
                .searchComplexField("concat(coalesce(per.nombres,''),' ',coalesce(per.paterno,''),' ',coalesce(per.materno,''))")
                .searchFields("da.nombre", "cur.nombre", "cur.codigo", "fa.nombre", "sec.codigo2", "gh.codigo", "doc.codigo")
                .orderBy("ed.id");
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
            if (!key.equals("ed.estado")) {
                continue;
            }
            String values = (String) queries.get(key);
            if (values.equals("activo")) {
                sql.filter("ed.estado", EncuestaEstudiantilEstadoEnum.ACT);
            } else if (values.equals("anulado")) {
                sql.filter("ed.estado", EncuestaEstudiantilEstadoEnum.ANU);
            }
        }

    }

    @Override
    public List<EncuestaDocente> allByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil) {
        Octavia sql = Octavia.query()
                .from(EncuestaDocente.class, "ed")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("docenteSeccion ds", "ds.docente doc", "doc.persona per")
                .join("ds.seccion sec", "sec.grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("per.tipoDocumento tdoc")
                .filter("ee.id", encuestaEstudiantil);
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
    public void deleteByEncuestaEstudiantil(EncuestaEstudiantil encuesta) {
        String strQuery = "delete from EncuestaDocente ed where ed.encuestaEstudiantil.id=:enc";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("enc", encuesta.getId());
        query.executeUpdate();
    }

    @Override
    public EncuestaDocente findByDocenteSeccion(DocenteSeccion docSec) {

        Octavia sql = Octavia.query(EncuestaDocente.class, "ed")
                .join("docenteSeccion ds")
                .filter("ds.id", docSec);
        return find(sql);
    }

}

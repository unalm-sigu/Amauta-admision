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
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaCursoDAO;

@Repository
public class EncuestaCursoDAOH extends AbstractEasyDAO<EncuestaCurso> implements EncuestaCursoDAO {

    public EncuestaCursoDAOH() {
        super();
        setClazz(EncuestaCurso.class);
    }

    @Override
    public List<EncuestaCurso> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        Octavia subquerySecciones = Octavia.query()
                .from(Seccion.class, "se")
                .join("grupoSeccion gpo")
                .left("grupoHoras gh")
                .filter("se.estado", SeccionEstadoEnum.ACT)
                .filter("se.tipoSeccion", "<>", TipoSeccionEnum.PCUR);

        DynatableSql sql = new DynatableSql(filter)
                .from(EncuestaCurso.class, "ec")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad fa")
                .filter("ciclo.id", cicloAcademico)
                .searchFields("cur.nombre", "cur.codigo", "da.nombre", "fa.nombre")
                .__().searchSubquery(subquerySecciones)
                .__().subqueryLinkedBy("gs.id", "gpo.id")
                .__().searchSubqueryFields("se.codigo2", "gh.codigo")
                .orderBy("ec.id DESC");
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
            if (!key.equals("ec.estado")) {
                continue;
            }
            String values = (String) queries.get(key);
            if (values.equals("activo")) {
                sql.filter("ec.estado", EncuestaEstudiantilEstadoEnum.ACT);
            } else if (values.equals("anulado")) {
                sql.filter("ec.estado", EncuestaEstudiantilEstadoEnum.ANU);
            }
        }

    }

    @Override
    public List<EncuestaCurso> allByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil) {
        Octavia sql = Octavia.query()
                .from(EncuestaCurso.class, "ec")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad")
                //                .leftJoin("per.tipoDocumento tdoc")
                .filter("ee.id", encuestaEstudiantil);
        return all(sql);
    }

    @Override
    public EncuestaCurso findEncuestaCurso(EncuestaCurso encuestaForm) {
        Octavia sql = Octavia.query()
                .from(EncuestaCurso.class, "ec")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad")
                .filter("ec.id", encuestaForm);
        return find(sql);
    }

    @Override
    public void deleteByEncuestaEstudiantil(EncuestaEstudiantil encuesta) {
        String strQuery = "delete from EncuestaCurso ec where ec.encuestaEstudiantil.id=:enc";
        Query query = getCurrentSession().createQuery(strQuery);
        query.setLong("enc", encuesta.getId());
        query.executeUpdate();
    }

}

package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaCursoDAO;

@Repository
public class EncuestaCursoDAOH extends AbstractEasyDAO<EncuestaCurso> implements EncuestaCursoDAO {

    public EncuestaCursoDAOH() {
        super();
        setClazz(EncuestaCurso.class);
    }

    @Override
    public List<EncuestaCurso> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        DynatableSql sql = new DynatableSql(filter)
                .from(EncuestaCurso.class, "ec")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad")
                .filter("ciclo.id", cicloAcademico)
                .searchFields("da.nombre", "cur.nombre", "en.nombre")
                .orderBy("ec.id");
        sql.beginRelativeFilters();
        return sql.all(getCurrentSession());

    }

    @Override
    public List<EncuestaCurso> allByEncuestaEstudiantil(EncuestaEstudiantil encuestaEstudiantil) {
        Octavia sql = Octavia.query()
                .from(EncuestaCurso.class, "ec")
                .join("encuestaEstudiantil ee", "ee.encuesta en", "ee.cicloAcademico ciclo")
                .join("grupoSeccion gs", "gs.curso cur")
                .join("cur.departamentoAcademico da", "da.facultad")
                .leftJoin("per.tipoDocumento tdoc")
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

}

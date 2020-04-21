package pe.edu.lamolina.amauta.dao.encuesta.hibernate;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.encuesta.PreguntaExamenDAO;
import pe.edu.lamolina.model.enums.PreguntaEstadoEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;

@Repository
public class PreguntaExamenDAOH extends AbstractEasyDAO<PreguntaExamen> implements PreguntaExamenDAO {

    public PreguntaExamenDAOH() {
        super();
        setClazz(PreguntaExamen.class);
    }

    @Override
    public PreguntaExamen find(long id) {

        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "pre")
                .join("examenVirtual exv")
                .leftJoin("bloquePreguntas blo", "subtitulo subti", "tema tema")
                .leftJoin("opcionReferencia opr", "opr.pregunta")
                .leftJoin("tipoLikert tip")
                .filter("pre.id", id);

        return (PreguntaExamen) sql.find(getCurrentSession());
    }

    @Override
    public List<PreguntaExamen> allForEncuestaByDynatable(DynatableFilter filter, ExamenVirtual encuesta) {

        DynatableSql sql = new DynatableSql(filter)
                .from(PreguntaExamen.class, "pev")
                .join("examenVirtual eva")
                .leftJoin("opcionReferencia opr", "opr.pregunta")
                .filter("eva.id", encuesta)
                .searchFields("pev.texto", "pev.numero", "pev.estado")
                .orderBy("pev.numero DESC");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<PreguntaExamen> allForExamenByDynatable(DynatableFilter filter, ExamenVirtual examen) {

        DynatableSql sql = new DynatableSql(filter)
                .from(PreguntaExamen.class, "pev")
                .join("examenVirtual eva")
                .leftJoin("bloquePreguntas blo", "subtitulo subti", "tema tema")
                .leftJoin("blo.subTituloExamen subti2", "subti2.temaExamen tema2")
                .leftJoin("subti.temaExamen tema3")
                .filter("eva.id", examen)
                .searchFields("pev.texto", "pev.numero", "pev.estado", "blo.nombre", "subti.nombre", "tema.nombre")
                .searchFields("subti2.nombre", "tema2.nombre", "tema3.nombre")
                .orderBy("pev.numero DESC");

        return sql.all(getCurrentSession());

    }

    @Override
    public PreguntaExamen findMayorNumero(ExamenVirtual encuesta) {
        Criteria criteria = getCurrentSession().createCriteria(PreguntaExamen.class);
        criteria.add(Restrictions.eq("examenVirtual", encuesta));
        criteria.addOrder(Order.desc("numero"));
        criteria.setMaxResults(1);
        return (PreguntaExamen) criteria.uniqueResult();
    }

    @Override
    public List<PreguntaExamen> allReferencia(PreguntaExamen pregunta) {
        Criteria criteria = getCurrentSession().createCriteria(PreguntaExamen.class);
        criteria.add(Restrictions.eq("examenVirtual", pregunta.getExamenVirtual()));
        criteria.add(Restrictions.eq("estado", PreguntaEstadoEnum.ACT.name()));
        criteria.add(Restrictions.ne("id", pregunta.getId()));
        criteria.addOrder(Order.asc("numero"));
        return criteria.list();
    }

    @Override
    public PreguntaExamen findPregunta(Long pregunta) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "pre")
                .join("examenVirtual")
                .filter("pre.id", pregunta);

        return (PreguntaExamen) sql.find(getCurrentSession());
    }

    @Override
    public List<PreguntaExamen> allActivasByEncuesta(ExamenVirtual encuesta) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "pre")
                .join("examenVirtual exv")
                .leftJoin("opcionReferencia opr", "opr.pregunta")
                .filter("exv.id", encuesta)
                .filter("pre.estado", PreguntaEstadoEnum.ACT.name())
                .orderBy("pre.numero");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<PreguntaExamen> allByEncuesta(ExamenVirtual encuesta) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "pre")
                .join("examenVirtual exv")
                .filter("exv.id", encuesta);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<PreguntaExamen> allByEncuestas(List<ExamenVirtual> encuestas) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "pre")
                .join("examenVirtual exv")
                .in("exv.id", encuestas);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<PreguntaExamen> allMayoresByNumero(Integer numero, ExamenVirtual encuesta) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "pre")
                .join("examenVirtual exv")
                .filter("exv.id", encuesta)
                .filter("pre.numero", ">", numero);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<PreguntaExamen> allByOpcionesReferencia(List<OpcionPregunta> opcionesReferencia) {
        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "pre")
                .join("examenVirtual exv", "opcionReferencia opr", "opr.pregunta")
                .in("opr.id", opcionesReferencia);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<PreguntaExamen> allWithOtrosByEncuesta(ExamenVirtual encuesta) {
        Octavia sqlOpcion = Octavia.query()
                .from(OpcionPregunta.class, "op")
                .join("pregunta pp")
                .beginBlock()
                .__().filter("op.esOtro", 1)
                .__().filter("op.esMulti", 1)
                .__().filter("op.esTexto", 1)
                .endBlock();

        Octavia sql = Octavia.query()
                .from(PreguntaExamen.class, "pre")
                .join("examenVirtual exv")
                .filter("exv.id", encuesta)
                .exists(sqlOpcion)
                .linkedBy("pre.id", "pp.id");

        return sql.all(getCurrentSession());
    }

}

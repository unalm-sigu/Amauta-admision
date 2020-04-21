package pe.edu.lamolina.amauta.dao.encuesta.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.TipoPreguntaEncuestaEnum;
import pe.edu.lamolina.amauta.dao.encuesta.OpcionPreguntaDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;

@Repository
public class OpcionPreguntaDAOH extends AbstractEasyDAO<OpcionPregunta> implements OpcionPreguntaDAO {

    public OpcionPreguntaDAOH() {
        super();
        setClazz(OpcionPregunta.class);
    }

    @Override
    public void deleteByPregunta(PreguntaExamen pregunta) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete ");
        sql.append(" from ").append(OpcionPregunta.class.getName()).append(" as opcion ");
        sql.append(" where opcion.pregunta.id = :PREGUNTA ");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setLong("PREGUNTA", pregunta.getId());
        query.executeUpdate();
    }

    @Override
    public List<OpcionPregunta> allByName(String nombre, ExamenVirtual encuesta) {

        Octavia sql = Octavia.query()
                .from(OpcionPregunta.class, "op")
                .join("pregunta pre", "pre.examenVirtual enc")
                .filter("enc.id", encuesta)
                .filter("pre.tipo", TipoPreguntaEncuestaEnum.SIMPLE)
                .orderBy("op.letra")
                .limit(10);

        if (!StringUtils.isEmpty(nombre)) {
            String searchValue = nombre.trim().replaceAll("\\s+", "%");
            searchValue = searchValue.equals("") ? "%" : searchValue;

            sql.__().beginBlock()
                    .__().filter("op.contenido", "like", searchValue)
                    .__().filter("op.letra", "like", searchValue)
                    .__().filter("pre.numero", "like", searchValue)
                    .endBlock();
        }

        return sql.all(getCurrentSession());
    }

    @Override
    public OpcionPregunta findByPreguntaReferencia(PreguntaExamen preguntaReferencia) {
        Octavia sql = Octavia.query()
                .from(OpcionPregunta.class, "op")
                .join("pregunta pre")
                .filter("ref.id", preguntaReferencia)
                .limit(1);
        return (OpcionPregunta) sql.find(getCurrentSession());
    }

    @Override
    public List<OpcionPregunta> allByPreguntas(List<PreguntaExamen> preguntas) {
        Octavia sql = Octavia.query()
                .from(OpcionPregunta.class, "opp")
                .join("pregunta pre")
                .in("pre.id", preguntas)
                .orderBy("opp.numero");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<OpcionPregunta> allByPregunta(PreguntaExamen pregunta) {
        Octavia sql = Octavia.query()
                .from(OpcionPregunta.class, "op")
                .join("pregunta pre")
                .filter("pre.id", pregunta)
                .orderBy("op.numero");
        return sql.all(getCurrentSession());
    }

    @Override
    public List<OpcionPregunta> allOtrosByPregunta(PreguntaExamen pregunta) {
        Octavia sql = Octavia.query()
                .from(OpcionPregunta.class, "op")
                .join("pregunta pre")
                .filter("pre.id", pregunta)
                .beginBlock()
                .__().filter("esOtro", 1)
                .__().filter("esMulti", 1)
                .__().filter("esTexto", 1)
                .endBlock();
        return sql.all(getCurrentSession());
    }

}

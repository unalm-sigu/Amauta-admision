package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.pivot.dao.encuesta.EncuestaPostulanteDAO;
import pe.edu.lamolina.model.examen.OpcionPregunta;
import pe.edu.lamolina.model.examen.PreguntaExamen;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EncuestaPostulante;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class EncuestaPostulanteDAOH extends AbstractEasyDAO<EncuestaPostulante> implements EncuestaPostulanteDAO {

    public EncuestaPostulanteDAOH() {
        super();
        setClazz(EncuestaPostulante.class);
    }

    @Override
    public List<EncuestaPostulante> allByPreguntasCiclo(List<PreguntaExamen> preguntas, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(EncuestaPostulante.class, "ep")
                .join("pregunta pre", "postulante po", "po.cicloPostula ci")
                .leftJoin("opcion opc")
                .in("pre.id", preguntas)
                .filter("ci.id", ciclo)
                .orderBy("ep.numeroPregunta");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<EncuestaPostulante> allByPreguntaOpcionCiclo(PreguntaExamen pregunta, OpcionPregunta opcion, CicloPostula ciclo) {

        Octavia sql = Octavia.query()
                .from(EncuestaPostulante.class, "ep")
                .join("pregunta pre", "postulante po", "po.cicloPostula ci")
                .leftJoin("opcion opc")
                .filter("pre.id", pregunta)
                .filter("ci.id", ciclo)
                .orderBy("ep.respuestaOtro");

        if (opcion != null) {
            sql.filter("opc.id", opcion);
        }

        return sql.all(getCurrentSession());
    }

    @Override
    public void unificarFrases(OpcionPregunta opcion, String permanece, String modifica, CicloPostula ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(EncuestaPostulante.class.getSimpleName()).append(" ep ");
        sql.append("   set ep.respuestaOtro = :PERMANECE ");
        sql.append(" where ep.pregunta.id = :PREGUNTA ");
        sql.append("   and ep.respuestaOtro = :MODIFICA ");
        sql.append("   and exists ( ");
        sql.append("        select po.id from ").append(Postulante.class.getSimpleName()).append(" po ");
        sql.append("         where po.cicloPostula.id = :CICLO ");
        sql.append("           and po.id = ep.postulante.id ");
        sql.append("   )  ");

        if (opcion.getId() != null) {
            sql.append("   and ep.opcion.id = :OPCION ");
        }

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", ciclo.getId());
        query.setParameter("PREGUNTA", opcion.getPregunta().getId());
        query.setParameter("PERMANECE", permanece);
        query.setParameter("MODIFICA", modifica);

        if (opcion.getId() != null) {
            query.setParameter("OPCION", opcion.getId());
        }

        query.executeUpdate();
    }

}

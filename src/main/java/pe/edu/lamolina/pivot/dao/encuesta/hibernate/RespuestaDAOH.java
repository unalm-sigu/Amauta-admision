package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.dao.encuesta.RespuestaDAO;
import pe.edu.lamolina.model.calificacion.Respuesta;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Evaluado;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class RespuestaDAOH extends AbstractEasyDAO<Respuesta> implements RespuestaDAO {

    public RespuestaDAOH() {
        super();
        setClazz(Respuesta.class);
    }

    @Override
    public List<Respuesta> allByPostulante(List<Respuesta> respuestas) {
        Map<Long, Postulante> mapPostulantes = TypesUtil.convertListToMap("id", "evaluado.postulante", respuestas);
        List<Postulante> postulantes = new ArrayList(mapPostulantes.values());

        Octavia sql = Octavia.query()
                .from(Respuesta.class, "re")
                .join("evaluado eva", "eva.postulante po")
                .in("po.id", postulantes);
        return sql.all(getCurrentSession());
    }

    @Override
    public List<Respuesta> allRespuestaByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(Respuesta.class, "re")
                .join("evaluado eva", "eva.postulante po", "po.cicloPostula cp")
                .filter("cp.id", ciclo);
        return sql.all(getCurrentSession());
    }

    @Override
    public void deleteByCiclo(CicloPostula ciclo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete from ").append(Respuesta.class.getSimpleName()).append(" as re ");
        sql.append("  where exists (");
        sql.append("     select  eva.id ");
        sql.append("      from ").append(Evaluado.class.getSimpleName()).append(" as eva ");
        sql.append("      join eva.postulante po ");
        sql.append("      join po.cicloPostula ci ");
        sql.append("      where ci.id = :CICLO ");
        sql.append("        and eva.id = re.evaluado.id ");
        sql.append("  )");

        Query query = getCurrentSession().createQuery(sql.toString());
        query.setParameter("CICLO", ciclo.getId());
        query.executeUpdate();
    }

}

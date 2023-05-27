package pe.edu.lamolina.amauta.dao.admision.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.admision.EvaluadoDAO;
import pe.edu.lamolina.model.inscripcion.Evaluado;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class EvaluadoDAOH extends AbstractEasyDAO<Evaluado> implements EvaluadoDAO {

    public EvaluadoDAOH() {
        super();
        setClazz(Evaluado.class);
    }

    @Override
    public Evaluado findByPostulante(Postulante postulante) {
        Octavia sql = Octavia.query()
                .from(Evaluado.class, "eva")
                .join("postulante po", "po.cicloPostula cp", "cp.cicloAcademico")
                .leftJoin("carreraIngreso")
                .filter("po.id", postulante);

        return find(sql);
    }

}

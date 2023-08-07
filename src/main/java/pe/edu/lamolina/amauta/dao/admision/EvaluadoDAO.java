package pe.edu.lamolina.amauta.dao.admision;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.Evaluado;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface EvaluadoDAO extends EasyDAO<Evaluado> {

    Evaluado findByPostulante(Postulante postulante);

}

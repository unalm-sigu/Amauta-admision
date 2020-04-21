package pe.edu.lamolina.amauta.dao.encuesta;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.ExamenVirtualInteresado;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface ExamenVirtualInteresadoDAO extends EasyDAO<ExamenVirtualInteresado> {

    public ExamenVirtualInteresado findByCiclo(CicloPostula ciclo);
}

package pe.edu.lamolina.pivot.dao.encuesta;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EncuestaCiclo;

public interface EncuestaCicloDAO extends EasyDAO<EncuestaCiclo> {

    EncuestaCiclo findByCiclo(CicloPostula ciclo);

    List<EncuestaCiclo> allByEncuestas(List<ExamenVirtual> encuestas);

}

package pe.edu.lamolina.amauta.dao.nivelacioneegg;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;

public interface NotaAlumnoNivelacionDAO extends EasyDAO<NotaAlumnoNivelacion> {

    List<NotaAlumnoNivelacion> allByCiclo(CicloAcademico ciclo);

}

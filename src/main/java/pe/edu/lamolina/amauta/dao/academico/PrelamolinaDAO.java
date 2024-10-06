package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Prelamolina;

public interface PrelamolinaDAO extends EasyDAO<Prelamolina> {

    List<Prelamolina> allInscritosByCicloAcademico(CicloAcademico ciclo);

    List<Prelamolina> allIngresanteByCiclo(CicloPostula ciclo);

}

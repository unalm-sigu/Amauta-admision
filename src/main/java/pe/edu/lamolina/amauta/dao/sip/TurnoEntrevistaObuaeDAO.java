package pe.edu.lamolina.amauta.dao.sip;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;

public interface TurnoEntrevistaObuaeDAO extends EasyDAO<TurnoEntrevistaObuae> {

    List<TurnoEntrevistaObuae> allByCiclo(CicloAcademico ciclo);
}

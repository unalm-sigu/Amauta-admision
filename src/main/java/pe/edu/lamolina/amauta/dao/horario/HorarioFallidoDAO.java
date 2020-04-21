package pe.edu.lamolina.amauta.dao.horario;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.HorarioFallido;

public interface HorarioFallidoDAO extends EasyDAO<HorarioFallido> {

    void deleteAllByCiclo(CicloAcademico cicloAcademico);
}

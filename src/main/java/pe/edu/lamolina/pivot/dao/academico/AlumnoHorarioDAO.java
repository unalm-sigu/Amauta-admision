package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

public interface AlumnoHorarioDAO extends EasyDAO<AlumnoHorario> {

    public List<AlumnoHorario> allByCicloAcademico(CicloAcademico cicloAcademico);

}


package pe.edu.lamolina.amauta.dao.matricula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.matricula.MatriculaTurno;

public interface MatriculaTurnoDAO extends EasyDAO<MatriculaTurno> {

    List<MatriculaTurno> findAllMatriculaTurnoByCiclo(DynatableFilter filter, CicloAcademico cicloAcademico);

    MatriculaTurno findMatriculaTurnoByTurnoAtencion(TurnoAtencion turnoAtencion);

    public List<MatriculaTurno> findMatriculaTurnoByMatriculaResumen(MatriculaResumen matriculaResumen);
    
}

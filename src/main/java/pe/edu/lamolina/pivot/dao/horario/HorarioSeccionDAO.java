package pe.edu.lamolina.pivot.dao.horario;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.horario.HorarioSeccion;

public interface HorarioSeccionDAO extends Crud<HorarioSeccion> {

    public List<HorarioSeccion> allBySeccion(List<Seccion> secciones);

    public List<HorarioSeccion> allByCicloCurso(CicloAcademico cicloAcademico, List<Curso> cursos);

}


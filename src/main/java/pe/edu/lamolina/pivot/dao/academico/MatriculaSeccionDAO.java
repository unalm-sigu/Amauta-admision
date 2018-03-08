package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;

public interface MatriculaSeccionDAO extends EasyDAO<MatriculaSeccion> {

    List<MatriculaSeccion> allBySeccion(Seccion seccion);

    MatriculaSeccion find(Long id);

    MatriculaSeccion findByAlumnoSeccion(Alumno alumno, Seccion seccion);

    List<MatriculaSeccion> allByMatriculaSeccion(MatriculaResumen aluResumen);

    List<MatriculaSeccion> allByGpoSeccion(GrupoSeccion grupoSeccion, CicloAcademico ciclo);

    List<MatriculaSeccion> allByCiclo(CicloAcademico ciclo);

    List<MatriculaSeccion> allByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    Long countAllSeccionPrematriculado(CicloAcademico cicloAcademico);

    public List<MatriculaSeccion> allPrematriculadoByMatriculaResumen(List<MatriculaResumen> matriculaResumens);

}

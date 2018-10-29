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

    List<MatriculaSeccion> allMatriculadosBySeccion(Seccion seccion);

//    MatriculaSeccion findByAlumnoSeccion(Alumno alumno, Seccion seccion);
//    List<MatriculaSeccion> allByMatriculaSeccion(MatriculaResumen aluResumen);
    List<MatriculaSeccion> allMatriculadosByGpoSeccion(GrupoSeccion grupoSeccion, CicloAcademico ciclo);

    List<MatriculaSeccion> allMatriculadosByCiclo(CicloAcademico ciclo);

    List<MatriculaSeccion> allByCiclo(CicloAcademico ciclo);

    List<MatriculaSeccion> allMatriculadosByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

    List<MatriculaSeccion> allMatriculadosByAlumnosCiclo(List<Alumno> alumnos, CicloAcademico ciclo);

    Long countAllSeccionPrematriculado(CicloAcademico cicloAcademico);

    List<MatriculaSeccion> allPrematriculadoByMatriculaResumen(List<MatriculaResumen> matriculaResumens);

    List<MatriculaSeccion> allByAlumnoCicloEstados(Alumno alumno, CicloAcademico academico, List<String> asList);

//    List<MatriculaSeccion> allByModalidadEstudioCiclo(ModalidadEstudio modalidad, CicloAcademico cicloAcademico);
//    MatriculaSeccion findByCicloSeccionAlumno(CicloAcademico cicloAcademico, Seccion seccion, Alumno alumno);
//    MatriculaSeccion findByMatriculaResumenSeccion(MatriculaResumen matriculaResumen, Seccion seccion);
    List<MatriculaSeccion> allMatriculadosByAlumnosSecciones(List<Alumno> alumnos, List<Seccion> secciones);

}

package pe.edu.lamolina.amauta.controller.academico.alumnosdocente;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.general.Oficina;

public interface AlumnosDocenteService {

    Seccion findSeccion(Long idSeccion);

    List<MatriculaSeccion> allMatriculadosBySeccion(Seccion seccion, CicloAcademico ciclo);

    List<AlumnoConsejero> allAconsejadosByMatriculados(List<MatriculaSeccion> matriculados, CicloAcademico ciclo);

    List<Oficina> allConsejerias();

}

package pe.edu.lamolina.pivot.controller.bienestar.alumnoAporte;

import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface AporteAlumnoService {

    void generarAportes(Alumno alumno, CicloAcademico ciclo,MatriculaResumen matriculaResumen, DataSessionPivot ds);

    public void generarAporteCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    public void quitarAporteCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    public void quitarAporteDuplicadoCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds);

    public void generarAporteDuplicadoCarnet(CicloAcademico cicloAcademico, MatriculaResumen matriculaResumen, DataSessionPivot ds);
}

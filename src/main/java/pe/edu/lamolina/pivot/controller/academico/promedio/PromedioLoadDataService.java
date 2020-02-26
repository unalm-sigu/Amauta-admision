package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;

public interface PromedioLoadDataService {

    BeanPromedios loadDataAlumno(Alumno alumno);

    ListBeanPromedios loadDataAlumno(List<Alumno> alumnos);

}

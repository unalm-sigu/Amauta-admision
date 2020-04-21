package pe.edu.lamolina.amauta.controller.academico.promedio;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;

public interface PromedioLoadDataService {

    BeanPromedios loadDataAlumno(Alumno alumno);

    ListBeanPromedios loadDataAlumno(List<Alumno> alumnos);

}

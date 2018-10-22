package pe.edu.lamolina.pivot.controller.academico.topematricula;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.TopeMatricula;

public interface TopeMatriculaService {

    List<TopeMatricula> allTopeMatricula(DynatableFilter filter, CicloAcademico cicloAcademico);

}

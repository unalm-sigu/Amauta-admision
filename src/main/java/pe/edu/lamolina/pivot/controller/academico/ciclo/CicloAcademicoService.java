package pe.edu.lamolina.pivot.controller.academico.ciclo;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface CicloAcademicoService {

    List<CicloAcademico> allCicloAcademico(Integer maxResultado);

    CicloAcademico getCicloAcademico(Long cicloAcademico);

}

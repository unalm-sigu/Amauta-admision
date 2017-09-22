package pe.edu.lamolina.pivot.controller.academico.ciclo;

import java.util.List;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;

public interface CicloAcademicoService {

    public List<CicloAcademico> allCicloAcademico(Integer maxResultado);

    public CicloAcademico getCicloAcademico(Long cicloAcademico);

}

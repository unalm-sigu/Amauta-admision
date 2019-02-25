package pe.edu.lamolina.pivot.controller.academico.distanciapabellon;

import java.util.List;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.general.Aula;

interface DistanciaPabellonService {

    List<DepartamentoAcademico> allDepartamentos();

    List<Aula> allModulos();

}

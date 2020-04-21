package pe.edu.lamolina.amauta.controller.academico.distanciapabellon;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.DistanciaPabellon;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

interface DistanciaPabellonService {

    List<DepartamentoAcademico> allDepartamentos();

    List<Aula> allModulos();

    List<DepartamentoAcademico> allDynatableFilter(DynatableFilter filter, DataSessionPivot ds);

    List<DistanciaPabellon> allDistancia(DepartamentoAcademico departamentoAcademico);

    void saveDistancia(DepartamentoAcademico departamentoAcademico);

}

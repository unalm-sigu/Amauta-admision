package pe.edu.lamolina.pivot.controller.general.lejaniadepartamento;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.DistanciaPabellon;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface LejaniaDepartamentoService {

    List<DepartamentoAcademico> allDepartamentos();

    Oficina findOficinaOera();

    List<Aula> allPabellonesByOficina(Oficina oficinaOERA);

    void save(List<DistanciaPabellon> factordist, DataSessionPivot ds);

    List<DistanciaPabellon> allDistanciaPabellon(DynatableFilter filter);

    List<DistanciaPabellon> allFactorDistanciaByDepartamento(DepartamentoAcademico departamentoAcademico);

}

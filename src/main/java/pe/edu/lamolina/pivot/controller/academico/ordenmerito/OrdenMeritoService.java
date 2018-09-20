package pe.edu.lamolina.pivot.controller.academico.ordenmerito;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlOrdenMerito;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface OrdenMeritoService {

    void generarDatos(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void calcularMeritos(CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<ControlOrdenMerito> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico);

    public List<CicloAcademico> allCicloAcademicoForSelect();

    CicloAcademico findCicloActivo();

}

package pe.edu.lamolina.pivot.controller.academico.ordenmerito;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlOrdenMerito;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface OrdenMeritoService {

    void generarDatos(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void calcularMeritos(CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<ControlOrdenMerito> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico);

    List<CicloAcademico> allCicloAcademicoForSelect();

    CicloAcademico findCicloActivo();

    List<AlumnoCiclo> allAlumnoCicloByControl(DynatableFilter filter, ControlOrdenMerito controlOrdenMerito);

    ControlOrdenMerito find(Long id);

    List<AlumnoCiclo> allAlumnoCicloByControlNivel(DynatableFilter filter, ControlOrdenMerito controlOrdenMerito, Integer nivel);

    List<AlumnoCiclo> generatePdfOrdenMerito(CicloAcademico cicloAcademico);

    List<Facultad> allFacultadesForReporte();

}

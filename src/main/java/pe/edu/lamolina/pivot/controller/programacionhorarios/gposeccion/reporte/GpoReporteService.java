package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionResumen;

public interface GpoReporteService {

    CicloAcademico findCiclo(CicloAcademico cicloAcademico);

    GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo);

    List<DepartamentoAcademico> allDepartamentoAcademico(CicloAcademico cicloAcademico);

    List<Facultad> allDepartamentoAcademicoXfacultad(CicloAcademico cicloAcademico);

}

package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte;

import java.util.List;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.seccion.SeccionDTO;

public interface GpoReporteService {

    CicloAcademico findCiclo(CicloAcademico cicloAcademico);

    GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo);

    List<DepartamentoAcademico> allDepartamentoAcademico(CicloAcademico cicloAcademico);

    List<Facultad> allDepartamentoAcademicoXfacultad(CicloAcademico cicloAcademico);

    List<AnexoBoletin> getAnexosForBoletin(CicloAcademico ciclo);

    List<Seccion> allSeccionesConCruce(CicloAcademico cicloAcademico);

    List<Seccion> allSeccionesSinAula(CicloAcademico cicloAcademico, SeccionDTO seccionDTO);

    List<Seccion> allSeccionesConAula(CicloAcademico cicloAcademico, SeccionDTO seccionDTO);

}

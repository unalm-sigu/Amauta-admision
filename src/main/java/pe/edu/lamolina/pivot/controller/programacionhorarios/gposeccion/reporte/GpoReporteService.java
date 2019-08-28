package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte;

import java.util.List;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.aula.SeccionDTO;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.reporte.dto.CantidadMatriculadosDTO;

public interface GpoReporteService {

    CicloAcademico findCiclo(CicloAcademico cicloAcademico);

    GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo);

    List<DepartamentoAcademico> allDepartamentoAcademico(CicloAcademico cicloAcademico);

    List<Facultad> allDepartamentoAcademicoXfacultad(CicloAcademico cicloAcademico);

    List<AnexoBoletin> getAnexosForBoletin(CicloAcademico ciclo);

    List<Seccion> allSeccionesConCruce(CicloAcademico cicloAcademico);

    List<Seccion> allSeccionesByFilter(CicloAcademico cicloAcademico, SeccionDTO seccionDTO);

    List<MatriculaSeccion> allMatriculadosBySeccion(SeccionDTO seccionDTO);

    List<CantidadMatriculadosDTO> allCantidadMatriculados(SeccionDTO seccionDTO);
}

package pe.edu.lamolina.pivot.controller.programacionhorarios.boletinacademico;

import java.util.List;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface BoletinAcademicoService {

    void reporteAnexoBoletin(DataSessionPivot ds);

    GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo);

    List<AnexoBoletin> allAnexosSuperiores();

    List<AnexoBoletin> allAnexoBoletionHijos(CicloAcademico ciclo);

    List<AnexoBoletin> allAnexosByCiclo(CicloAcademico ciclo, DataSessionPivot ds);

    CicloAcademico findCicloAcademicoActivo();

}

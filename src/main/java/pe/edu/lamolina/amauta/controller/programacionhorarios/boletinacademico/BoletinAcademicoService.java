package pe.edu.lamolina.amauta.controller.programacionhorarios.boletinacademico;

import java.util.List;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.GpoSeccionResumen;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface BoletinAcademicoService {

    void reporteAnexoBoletin(DataSessionPivot ds);

    GpoSeccionResumen resumenByCiclo(CicloAcademico ciclo);

    List<AnexoBoletin> allAnexosSuperiores();

    List<AnexoBoletin> allAnexoBoletionHijos(CicloAcademico ciclo);

    List<AnexoBoletin> allAnexosByCiclo(CicloAcademico ciclo, DataSessionPivot ds);

    CicloAcademico findCicloAcademicoActivo();

}

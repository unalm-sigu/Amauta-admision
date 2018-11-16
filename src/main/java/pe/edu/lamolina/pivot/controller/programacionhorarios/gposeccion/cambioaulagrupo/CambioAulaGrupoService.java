package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.cambioaulagrupo;

import java.util.List;
import pe.edu.lamolina.model.academico.CambioAulaGrupo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CambioAulaGrupoService {

    List<CambioAulaGrupo> allAulaGrupos(Seccion seccion);

    void saveCambioAulaGrupo(CambioAulaGrupo cambioAulaGrupo, DataSessionPivot ds);

    void rechazarCambioAulaGrupo(CambioAulaGrupo cambioAulaGrupo, DataSessionPivot ds);

    void deleteCambioAulaGrupo(CambioAulaGrupo cambioAulaGrupo, DataSessionPivot ds);

    void aceptarCambioAulaGrupo(CambioAulaGrupo cambioAulaGrupo, DataSessionPivot ds);

    List<Aula> searchCambioAulaByName(String nombre, CicloAcademico ciclo);

    List<GrupoHoras> searchCambioGrupoByName(String nombre, CicloAcademico ciclo);
}

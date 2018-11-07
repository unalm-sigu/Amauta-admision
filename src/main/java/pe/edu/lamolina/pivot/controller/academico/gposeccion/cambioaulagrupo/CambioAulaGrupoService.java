package pe.edu.lamolina.pivot.controller.academico.gposeccion.cambioaulagrupo;

import java.util.List;
import pe.edu.lamolina.model.academico.CambioAulaGrupo;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;


public interface CambioAulaGrupoService {

    List<CambioAulaGrupo> allAulaGrupos(Seccion seccion);

    void saveCambioAulaGrupo(CambioAulaGrupo cambioAulaGrupo, DataSessionPivot ds);
}

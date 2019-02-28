package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.precioseccion;

import java.util.List;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PrecioSeccionService {

    void savePrecioSeccion(Seccion precioSeccion, DataSessionPivot ds);

    void asignarHorasAdicionales(Seccion seccion, DataSessionPivot ds);

    List<TipoCarpeta> allTipoCarpetaByNombre(String nombre);

    void saveTipoCarpetaSeccion(Seccion seccion, DataSessionPivot ds);

    TipoCarpeta findTipoCarpetaSeccion(Seccion seccion);

    void asignarGrupoSeccionModular(GrupoSeccion grupoSeccion, DataSessionPivot ds);

}

package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.precioseccion;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.finanzas.PagoHoraDocente;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PrecioSeccionService {

    DocenteSeccion findDocenteSeccion(DocenteSeccion docenteSeccion);

    CursoCicloAcademico findCursoCiclo(Curso curso, CicloAcademico cicloAcademico);

    void savePrecioSeccion(Seccion precioSeccion, DataSessionPivot ds);

    void asignarHorasAdicionales(Seccion seccion, DataSessionPivot ds);

    List<TipoCarpeta> allTipoCarpetaByNombre(String nombre);

    void saveTipoCarpetaSeccion(Seccion seccion, DataSessionPivot ds);

    TipoCarpeta findTipoCarpetaSeccion(Seccion seccion);

    void asignarGrupoSeccionModular(GrupoSeccion grupoSeccion, DataSessionPivot ds);

    String generarPagoDocente(DocenteSeccion docenteSeccion, CursoCicloAcademico cursoCiclo, List<PagoHoraDocente> pagosDocenteByHora, CicloAcademico ciclo, DataSessionPivot ds);

    List<PagoHoraDocente> allPagosDocenteByCiclo(CicloAcademico cicloAcademico);

    void generarPagoDocenteCiclo(CicloAcademico cicloAcademico, DataSessionPivot ds);

}

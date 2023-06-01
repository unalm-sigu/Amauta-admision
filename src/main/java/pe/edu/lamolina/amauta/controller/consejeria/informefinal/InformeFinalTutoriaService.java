package pe.edu.lamolina.amauta.controller.consejeria.informefinal;

import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.tutoria.InformeFinalTutoria;

public interface InformeFinalTutoriaService {

    Consejero findConsejero(Consejero consejero);

    Boolean tienePermiso(Consejero consejero, CicloAcademico ciclo, DataSessionPivot ds);

    Boolean verificarConsejero(CicloAcademico ciclo, DataSessionPivot ds);

    InformeFinalTutoria findInforme(Consejero consejero, CicloAcademico ciclo, DataSessionPivot ds);

    void calcularCantidadesInforme(InformeFinalTutoria informe, CicloAcademico ciclo, DataSessionPivot ds);

    void dificultadesInforme(InformeFinalTutoria informe, CicloAcademico ciclo, DataSessionPivot ds);

    void sugerenciasInforme(InformeFinalTutoria informe, CicloAcademico ciclo, DataSessionPivot ds);

    void conclusionesInforme(InformeFinalTutoria informe, CicloAcademico ciclo, DataSessionPivot ds);

    void enviarInforme(InformeFinalTutoria informe, CicloAcademico ciclo, DataSessionPivot ds);

}

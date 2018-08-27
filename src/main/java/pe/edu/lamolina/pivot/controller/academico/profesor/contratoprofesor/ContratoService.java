package pe.edu.lamolina.pivot.controller.academico.profesor.contratoprofesor;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.rrhh.ContratoDocente;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ContratoService {

    void addResolucionConsejo(ContratoDocente contratoDocente, Resolucion resolucionConsejo, DataSessionPivot ds);

    void addResolucionFacultad(ContratoDocente contratoDocente, Resolucion resolucionFacultad, DataSessionPivot ds);

    void addVistoBueno(ContratoDocente contratoDocente, DataSessionPivot ds);

    public List<ContratoDocente> allByDynatable(DynatableFilter filter, Docente docente);

}

package pe.edu.lamolina.amauta.controller.configuracion.evento;

import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;

public interface EventoService {

    EventoCiclo findEventoExamen(CicloPostula ciclo);

}

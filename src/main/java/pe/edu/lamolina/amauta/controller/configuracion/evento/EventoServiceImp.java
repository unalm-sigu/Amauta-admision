package pe.edu.lamolina.amauta.controller.configuracion.evento;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.amauta.dao.inscripcion.EventoCicloDAO;
import pe.edu.lamolina.amauta.dao.inscripcion.EventoDAO;

@Service
@Transactional(readOnly = true)
public class EventoServiceImp implements EventoService {
    
    @Autowired
    EventoCicloDAO eventoCicloDAO;
    @Autowired
    EventoDAO eventoDAO;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public EventoCiclo findEventoExamen(CicloPostula ciclo) {
        Evento evento = eventoDAO.findByCode("EXAM");
        List<EventoCiclo> examenes = eventoCicloDAO.allByEventoCiclo(evento, ciclo);
        if (!examenes.isEmpty()) {
            return examenes.get(0);
        }
        return null;
    }
    
    
}


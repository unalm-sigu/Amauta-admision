package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.carrera;

import pe.edu.lamolina.pivot.controller.academico.horariocachimbo.ingresante.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.pivot.dao.horario.TipoGrupoHorasDAO;

@Service
@Transactional(readOnly = true)
public class HorarioCarreraServiceImp implements HorarioCarreraService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TipoGrupoHorasDAO tipoGrupoHorasDAO;

}

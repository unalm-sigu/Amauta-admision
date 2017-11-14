package pe.edu.lamolina.pivot.controller.academico.horario.grupo;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.controller.academico.horario.grupo.GrupoHorasService;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;

@Service
@Transactional(readOnly = true)
public class GrupoHorasServiceImp implements GrupoHorasService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    @Override
    public List<GrupoHoras> allGrupoHoras(DynatableFilter filter) {
        return grupoHorasDAO.allGrupoHoras(filter);
    }

}

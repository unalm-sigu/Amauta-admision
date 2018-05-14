package pe.edu.lamolina.pivot.controller.academico.resolucion;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.pivot.dao.tramite.ResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoResolucionDAO;

@Service
@Transactional(readOnly = true)
public class ResolucionServiceImp implements ResolucionService {

    @Autowired
    ResolucionDAO resolucionDAO;

    @Autowired
    TipoResolucionDAO tipoResolucionDAO;

    @Override
    public List<Resolucion> allTramitesByFilter(DynatableFilter filter) {
        List<Resolucion> tramites = resolucionDAO.allByDyna(filter);
        return tramites;
    }

    @Override
    public List<TipoResolucion> allTiposResolucion() {
        return tipoResolucionDAO.all();
    }

}

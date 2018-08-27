package pe.edu.lamolina.pivot.controller.academico.profesor.contratoprofesor;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.rrhh.ContratoDocente;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.pivot.dao.rrhh.ContratoDocenteDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ContratoServiceImp implements ContratoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ContratoDocenteDAO contratoDocenteDAO;

    @Override
    @Transactional
    public void addResolucionConsejo(ContratoDocente contratoDocente, Resolucion resolucionConsejo, DataSessionPivot ds) {
        ContratoDocente cdBD = contratoDocenteDAO.find(contratoDocente.getId());

        cdBD.setUserConsejo(ds.getUsuario());
        cdBD.setFechaConsejo(new Date());

        contratoDocenteDAO.update(cdBD);
    }

    @Override
    @Transactional
    public void addResolucionFacultad(ContratoDocente contratoDocente, Resolucion resolucionFacultad, DataSessionPivot ds) {
        ContratoDocente cdBD = contratoDocenteDAO.find(contratoDocente.getId());

        cdBD.setUserFacultad(ds.getUsuario());
        cdBD.setFechaFacultad(new Date());

        contratoDocenteDAO.update(cdBD);
    }

    @Override
    @Transactional
    public void addVistoBueno(ContratoDocente contratoDocente, DataSessionPivot ds) {
        ContratoDocente cdBD = contratoDocenteDAO.find(contratoDocente.getId());

        cdBD.setUserVobo(ds.getUsuario());
        cdBD.setFechaVobo(new Date());

        contratoDocenteDAO.update(cdBD);
    }

    @Override
    public List<ContratoDocente> allByDynatable(DynatableFilter filter, Docente docente) {
        return contratoDocenteDAO.all();
    }

}

package pe.edu.lamolina.pivot.controller.tramiteTrabajo;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.FlujoTramiteBienestar;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteSubvencion;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.tramite.FlujoTramiteBienestarDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteSubvencionDAO;

@Service
@Transactional(readOnly = true)
public class TramiteTrabajoServiceImpl implements TramiteTrabajoService {

    @Autowired
    TramiteSubvencionDAO subvencionDAO;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    FlujoTramiteBienestarDAO flujoTramiteBienestarDAO;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<TramiteSubvencion> allTramiteSubvByColabo(Persona persona, CicloAcademico cicloAcademico) {
        Colaborador colaborador = colaboradorDAO.findByPersona(persona);
        return subvencionDAO.allSubvencionByColaboradorCicloAcademico(colaborador, cicloAcademico);
    }

    @Override
    @Transactional
    public void updateTramiteSubvencion(TramiteSubvencion tramiteSubvencion, Usuario usuario) {
        Tramite tramite = tramiteSubvencion.getTramite();
        Tramite t = tramiteDAO.findById(tramiteSubvencion.getTramite());
        t.setEstado(tramite.getEstado());
        t.setFechaModificacion(new Date());
        t.setUserModificacion(tramite.getUserRegistro());
        tramiteDAO.update(t);

        TramiteSubvencion subvencion = subvencionDAO.findId(tramiteSubvencion);
        subvencion.setHoras(tramiteSubvencion.getHoras());
        subvencion.setLaborRealizar(tramiteSubvencion.getLaborRealizar());
        subvencion.setLugar(tramiteSubvencion.getLugar());
        subvencionDAO.update(subvencion);

        FlujoTramiteBienestar flujoTramite = new FlujoTramiteBienestar();
        flujoTramite.setEstado(tramite.getEstado());
        flujoTramite.setTramite(tramite);
        flujoTramite.setComentario(tramiteSubvencion.getComentario());
        flujoTramite.setFechaRegistro(new Date());
        flujoTramite.setUserRegistro(usuario);
        flujoTramiteBienestarDAO.save(flujoTramite);
    }

}

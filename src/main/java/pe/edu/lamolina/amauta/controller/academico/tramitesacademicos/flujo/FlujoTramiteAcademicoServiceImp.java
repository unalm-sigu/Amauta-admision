package pe.edu.lamolina.amauta.controller.academico.tramitesacademicos.flujo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.FlujoTramiteAcademico;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.AccionTramiteAcademicoDAO;
import pe.edu.lamolina.amauta.dao.tramite.FlujoTramiteAcademicoDAO;
import pe.edu.lamolina.amauta.dao.tramite.ReincorporacionDAO;

@Service
@Transactional(readOnly = true)
public class FlujoTramiteAcademicoServiceImp implements FlujoTramiteAcademicoService {

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    FlujoTramiteAcademicoDAO flujoTramiteAcademicoDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    AccionTramiteAcademicoDAO accionTramiteAcademicoDAO;

    @Override
    @Transactional(readOnly = false)
    public void saveFlujoTramite(Tramite tramite, Usuario usuario, DateTime today) {
        this.saveFlujoTramite(tramite, usuario, today, false);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveFlujoTramite(Tramite tramite, Usuario usuario, DateTime today, boolean revert) {
        /*
        EstadoTramite estadoTramite = null;
        if (tramite.getTipoTramite().getEsTipoTramiteRei()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
            Reincorporacion reincorporacion = reincorporaciones.get(0);
            estadoTramite = reincorporacion.getEstadoTramite();
        }
        EstadoTramiteAcademico currentEstadoTramiteAcademico = estadoTramiteAcademicoDAO.findByTipoAndEstadoTramite(tramite.getTipoTramite(), estadoTramite);
        Integer incremento = 1;
        if (revert) {
            incremento = incremento * -1;
        }
        EstadoTramiteAcademico newEstadoTramiteAcademico
                = estadoTramiteAcademicoDAO.findByTipoTramiteOrden(tramite.getTipoTramite(), currentEstadoTramiteAcademico.getOrden() + incremento);

        Alumno alumno = alumnoDAO.find(tramite.getAlumno());
        //  Map oficinas = oficinaDAO.findOficinaOrigenDestinoByEstadoTramiteAcad(newEstadoTramiteAcademico, alumno);

        Map oficinas = new HashMap();

        FlujoTramiteAcademico flujoTramiteAcademico = new FlujoTramiteAcademico();
        flujoTramiteAcademico.setEstadoTramite(newEstadoTramiteAcademico.getEstadoTramite());
        flujoTramiteAcademico.setFechaRegistro(today.toDate());
        flujoTramiteAcademico.setOficinaOrigen((Oficina) oficinas.get("oficinaOrigen"));
        flujoTramiteAcademico.setOficinaDestino((Oficina) oficinas.get("oficinaDestino"));
        flujoTramiteAcademico.setTramiteAcademico(tramite);
        flujoTramiteAcademico.setUserRegistro(usuario);
        flujoTramiteAcademico.setOrden(newEstadoTramiteAcademico.getOrden());
        flujoTramiteAcademicoDAO.save(flujoTramiteAcademico);

        if (tramite.getTipoTramite().getEsTipoTramiteRei()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
            Reincorporacion reincorporacion = reincorporaciones.get(0);

            Reincorporacion reincorporacionUpd = new Reincorporacion();
            reincorporacionUpd.setId(reincorporacion.getId());
            reincorporacionUpd.setEstadoTramite(newEstadoTramiteAcademico.getEstadoTramite());
            reincorporacionDAO.updateEstado(reincorporacionUpd);
        }*/
    }

    @Override
    public List<AccionTramiteAcademico> allAccionesTramiteByTramite(Tramite tramite) {
        EstadoTramite estadoTramite = null;
        if (tramite.getTipoTramite().getEsReincorporacionPregrado()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
            Reincorporacion reincorporacion = reincorporaciones.get(0);
            estadoTramite = reincorporacion.getEstadoTramite();
        }
        List<AccionTramiteAcademico> accionesTramites = accionTramiteAcademicoDAO.allByTipoTramiteAndEstadoTramiteInicial(
                tramite.getTipoTramite(), estadoTramite);
        return accionesTramites;
    }

}

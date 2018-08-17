package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.TramiteReunionConsejo;
import pe.edu.lamolina.model.tramite.EstadoTramiteAcademico;
import pe.edu.lamolina.model.tramite.FlujoTramiteAcademico;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.controller.academico.reunionconsejo.ReunionConsejoService;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.flujo.FlujoTramiteAcademicoService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.FlujoTramiteAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReunionConsejoDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteReunionConsejoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class TramitesAcademicosServiceImp implements TramitesAcademicosService {

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    EstadoTramiteAcademicoDAO estadoTramiteAcademicoDAO;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    FlujoTramiteAcademicoDAO flujoTramiteAcademicoDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    ReunionConsejoDAO reunionConsejoDAO;

    @Autowired
    TramiteReunionConsejoDAO tramiteReunionConsejoDAO;

    @Autowired
    FlujoTramiteAcademicoService flujoTramiteAcademicoService;

    @Override
    public List<Tramite> allTramitesByFilter(DynatableFilter filter) {
        List<Tramite> tramites = tramiteDAO.allByFilter(filter);
        for (Tramite tramite : tramites) {
            TramiteReunionConsejo tramiteReunionConsejo = tramiteReunionConsejoDAO.findByTramite(tramite);
            if (tramiteReunionConsejo != null) {
                tramite.setTramitesReunionConsejo(new ArrayList<>());
                tramite.getTramitesReunionConsejo().add(tramiteReunionConsejo);
            } else {
                tramite.setTramitesReunionConsejo(null);
            }
        }
        return tramites;
    }

    @Override
    @Transactional
    public void aceptarSolReincorporacion(Tramite tramite, Usuario usuario) {
        DateTime today = new DateTime();

        tramite = tramiteDAO.find(tramite.getId());
        Alumno alumnoTramite = alumnoDAO.find(tramite.getAlumno());

        List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
        Reincorporacion reincorporacion = reincorporaciones.get(0);

        if (!reincorporacion.getEstadoTramite().getEsSolicitudReincorporacion()) {
            throw new PhobosException("Estado incorrecto");
        }

        TipoTramite tipoTramiteReincorporacion = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.REI.name());
        EstadoTramiteAcademico estadoTramiteAcademico
                = estadoTramiteAcademicoDAO.findByTipoTramiteOrden(tipoTramiteReincorporacion, (BigDecimal.ONE.intValue() + 1));

        Map oficinas = oficinaDAO.findOficinaOrigenDestinoByEstadoTramiteAcad(estadoTramiteAcademico, alumnoTramite);

        FlujoTramiteAcademico flujoTramiteAcademico = new FlujoTramiteAcademico();
        flujoTramiteAcademico.setEstadoTramite(estadoTramiteAcademico.getEstadoTramite());
        flujoTramiteAcademico.setFechaRegistro(today.toDate());
        flujoTramiteAcademico.setOficinaOrigen((Oficina) oficinas.get("oficinaOrigen"));
        flujoTramiteAcademico.setOficinaDestino((Oficina) oficinas.get("oficinaDestino"));
        flujoTramiteAcademico.setTramiteAcademico(tramite);
        flujoTramiteAcademico.setUserRegistro(usuario);
        flujoTramiteAcademico.setOrden(estadoTramiteAcademico.getOrden());
        flujoTramiteAcademicoDAO.save(flujoTramiteAcademico);

        Reincorporacion reincorporacionUpd = new Reincorporacion();
        reincorporacionUpd.setId(reincorporacion.getId());
        reincorporacionUpd.setEstadoTramite(estadoTramiteAcademico.getEstadoTramite());
        reincorporacionDAO.updateEstado(reincorporacionUpd);

        Tramite tramiteUpd = new Tramite();
        tramiteUpd.setId(tramite.getId());
        tramiteUpd.setEstadoEnum(TramiteEstadoEnum.PROC);
        tramiteUpd.setUserModificacion(usuario);
        tramiteUpd.setFechaModificacion(today.toDate());
        tramiteDAO.updateEstado(tramiteUpd);
    }

    @Override
    @Transactional
    public void agendarSolicitud(Tramite tramite, ReunionConsejo reunionConsejo, Usuario usuario) {
        DateTime today = new DateTime();

        tramite = tramiteDAO.find(tramite.getId());
        reunionConsejo = reunionConsejoDAO.find(reunionConsejo.getId());
        if (reunionConsejo == null) {
            throw new PhobosException("Debe seleccionar la reunión consejo.");
        }
        List<TramiteReunionConsejo> tramiteReunionesConsejo = tramiteReunionConsejoDAO.allByReunionConsejoAndTipoTramite(reunionConsejo, tramite.getTipoTramite());

        TramiteReunionConsejo tramiteReunionConsejoActiva = null;
        for (TramiteReunionConsejo alumnoReunionConsejo : tramiteReunionesConsejo) {
            if (alumnoReunionConsejo.getEsActivo()) {
                tramiteReunionConsejoActiva = alumnoReunionConsejo;
                break;
            }
        }
        if (tramiteReunionConsejoActiva == null || (tramiteReunionConsejoActiva != null && tramiteReunionConsejoActiva.getReunionConsejo().getId().compareTo(reunionConsejo.getId()) != 0)) {
            TramiteReunionConsejo alumnoReunionConsejo = new TramiteReunionConsejo();
            alumnoReunionConsejo.setTramite(tramite);
            alumnoReunionConsejo.setEstadoEnum(EstadoEnum.ACT);
            alumnoReunionConsejo.setFechaRegistro(today.toDate());
            alumnoReunionConsejo.setFechaActualizacion(today.toDate());
            alumnoReunionConsejo.setReunionConsejo(reunionConsejo);
            alumnoReunionConsejo.setUserActualizacion(usuario);
            alumnoReunionConsejo.setUserRegistro(usuario);
            tramiteReunionConsejoDAO.save(alumnoReunionConsejo);
        }
        if (tramiteReunionConsejoActiva != null && tramiteReunionConsejoActiva.getReunionConsejo().getId().compareTo(reunionConsejo.getId()) != 0) {
            tramiteReunionConsejoActiva.setEstadoEnum(EstadoEnum.ANU);
            tramiteReunionConsejoDAO.update(tramiteReunionConsejoActiva);
        }

        Facultad facultad = tramite.getAlumno().getCarrera().getFacultad();

        List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
        Reincorporacion reincorporacion = reincorporaciones.get(0);

        if (!reincorporacion.getEstadoTramite()
                .getEsSolicitudHistorialRevisado()) {
            throw new PhobosException("Estado incorrecto");
        }

        flujoTramiteAcademicoService.saveFlujoTramite(tramite, usuario, today);
    }

    @Override
    public List<ReunionConsejo> allReunionConsejoByDyna(DynatableFilter filter, Oficina oficina) {
        List<ReunionConsejo> reunionesConsejo = reunionConsejoDAO.allByDynatable(filter, oficina);
        return reunionesConsejo;
    }

    @Override
    @Transactional
    public void revertTramiteAcademico(Tramite tramite, DataSessionPivot ds) {
        DateTime today = new DateTime();
        tramite = tramiteDAO.find(tramite.getId());
        if (tramite.getTipoTramite().getEsTipoTramiteRei()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
            Reincorporacion reincorporacion = reincorporaciones.get(0);
            if (reincorporacion.getEstadoTramite().getEsSolicitudReincorporacion()) {
                throw new PhobosException("Estado incorrecto");
            }
            if (reincorporacion.getEstadoTramite().getEsConsejoFacultad()) {
                TramiteReunionConsejo tramitesReunion = tramiteReunionConsejoDAO.findByTramite(tramite);
                tramitesReunion.setEstadoEnum(EstadoEnum.ANU);
                tramitesReunion.setUserActualizacion(ds.getUsuario());
                tramitesReunion.setFechaActualizacion(today.toDate());
                tramiteReunionConsejoDAO.update(tramitesReunion);
            }
            flujoTramiteAcademicoService.saveFlujoTramite(tramite, ds.getUsuario(), today, true);
        }
    }

}

package pe.edu.lamolina.pivot.controller.academico.resolucion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.aws.S3Service;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.ResolucionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.TramiteReunionConsejo;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.TramitesAcademicosService;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.flujo.FlujoTramiteAcademicoService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.AccionTramiteAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.ResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReunionConsejoDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteReunionConsejoDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ResolucionServiceImp implements ResolucionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ResolucionDAO resolucionDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    TipoResolucionDAO tipoResolucionDAO;

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    S3Service s3service;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;

    @Autowired
    ReunionConsejoDAO reunionConsejoDAO;

    @Autowired
    TramiteReunionConsejoDAO alumnoReunionConsejoDAO;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    FlujoTramiteAcademicoService flujoTramiteAcademicoService;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    AccionTramiteAcademicoDAO accionTramiteAcademicoDAO;

    @Autowired
    TramitesAcademicosService tramitesAcademicosService;

    @Override
    public List<Resolucion> allResolucionesByFilter(DynatableFilter filter) {
        List<Resolucion> resoluciones = resolucionDAO.allByDyna(filter);
        return resoluciones;
    }

    @Override
    public List<Reincorporacion> allReincorporacionByFilter(DynatableFilter filter, Resolucion resolucion) {
        if (filter.getQueries() == null) {
            filter.setQueries(new HashMap());
        }
        filter.getQueries().put("res.id", resolucion.getId());
        List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByDyna(filter);
        return reincorporaciones;
    }

    @Override
    public List<Tramite> allTramitesByTipoEstadoTram(TipoTramiteEnum tipoTramiteEnum, EstadoTramiteEnum estadoTramiteEnum) {
        return tramiteDAO.allByTipoTramiteEstadoTramite(tipoTramiteEnum, estadoTramiteEnum);
    }

    @Override
    public List<TipoResolucion> allTiposResolucion() {
        return tipoResolucionDAO.all();
    }

    @Override
    @Transactional(readOnly = false)
    public void saveResolucion(Resolucion resolucion, DataSessionPivot ds, CicloAcademico cicloAcademico) {
        DateTime today = new DateTime();

        boolean someChecked = false;
        for (TramiteReunionConsejo tramiteReunionConsejo : resolucion.getTramitesReunionConsejo()) {
            if (tramiteReunionConsejo.getSeleccionado()) {
                someChecked = true;
            }
        }
        if (!someChecked) {
            throw new PhobosException("Debe seleccionar algun tramite");
        }

        resolucion.setEstadoEnum(ResolucionEstadoEnum.CRE);
        resolucion.setUserRegistro(ds.getUsuario());
        resolucion.setFechaRegistro(today.toDate());
        resolucion.setUserActualizacion(null);
        resolucion.setFechaActualizacion(null);
        resolucionDAO.save(resolucion);

        for (TramiteReunionConsejo tramiteReunionConsejo : resolucion.getTramitesReunionConsejo()) {
            Reincorporacion reincorporacion = reincorporacionDAO.findByTramiteEstadoTram(tramiteReunionConsejo.getTramite(), EstadoTramiteEnum.AGE_CON_FAC);
            Tramite tramite = reincorporacion.getTramite();
            List<AccionTramiteAcademico> accionesTramitesAcademicos = accionTramiteAcademicoDAO.allByTipoTramiteAndEstadoTramiteInicial(tramite.getTipoTramite(), reincorporacion.getEstadoTramite());
            if (accionesTramitesAcademicos.isEmpty() || accionesTramitesAcademicos.size() > 1) {
                throw new PhobosException("Error al procesar el tramite");
            }

            if (!reincorporacion.getEstadoTramite()
                    .getEsAgendadoConsejoFacultad()) {
                throw new PhobosException("Estado incorrecto");
            }

            if (tramiteReunionConsejo.getSeleccionado()) {
                reincorporacion.setAceptado(BigDecimal.ONE.intValue());
                reincorporacion.setResolucion(resolucion);
                reincorporacionDAO.update(reincorporacion);
            } else {
                reincorporacion.setAceptado(BigDecimal.ZERO.intValue());
                reincorporacion.setResolucion(resolucion);
                reincorporacionDAO.update(reincorporacion);
            }

            tramitesAcademicosService.procesarTramite(tramite, accionesTramitesAcademicos.get(0), ds);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void updateResolucion(Resolucion resolucion, DataSessionPivot ds) {
        DateTime today = new DateTime();

        Resolucion resolucionUpd = new Resolucion();
        resolucionUpd.setId(resolucion.getId());
        resolucionUpd.setFecha(resolucion.getFecha());
        resolucionUpd.setSerie(resolucion.getSerie());
        resolucionUpd.setNumero(resolucion.getNumero());
        resolucionUpd.setUserActualizacion(ds.getUsuario());
        resolucionUpd.setFechaActualizacion(today.toDate());
        resolucionDAO.updateResolucion(resolucionUpd);

        for (TramiteReunionConsejo tramiteReunionConsejo : resolucion.getTramitesReunionConsejo()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramiteReunionConsejo.getTramite());
            Reincorporacion reincorporacion = reincorporaciones.get(0);

            if (!reincorporacion.getEstadoTramite()
                    .getEsResolucionFacultad()) {
                throw new PhobosException("Estado incorrecto");
            }

            if (tramiteReunionConsejo.getSeleccionado()) {
                reincorporacion.setAceptado(BigDecimal.ONE.intValue());
                reincorporacion.setResolucion(resolucion);
                reincorporacionDAO.update(reincorporacion);
                /*
                Reincorporacion reincorporacionUpd = new Reincorporacion();
                reincorporacionUpd.setId(reincorporacion.getId());
                reincorporacionUpd.setAceptado(BigDecimal.ONE.intValue());
                reincorporacionUpd.setResolucion(resolucion);
                reincorporacionDAO.updateAceptado(reincorporacionUpd);*/
            } else {
                reincorporacion.setAceptado(BigDecimal.ZERO.intValue());
                reincorporacion.setResolucion(resolucion);
                reincorporacionDAO.update(reincorporacion);
                /*   Reincorporacion reincorporacionUpd = new Reincorporacion();
                reincorporacionUpd.setId(reincorporacion.getId());
                reincorporacionUpd.setAceptado(BigDecimal.ZERO.intValue());
                reincorporacionUpd.setResolucion(resolucion);
                reincorporacionDAO.updateAceptado(reincorporacionUpd);*/
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void uploadResolucionFile(Resolucion resolucion, MultipartFile file, DataSessionPivot ds) {
        DateTime today = new DateTime();
        String absoluteName = null;
        String name;
        try {
            name = TypesUtil.getUnixTime() + file.getOriginalFilename();
            absoluteName = Constantine.TMP_DIR + name;
            FileHelper.saveToDisk(file, absoluteName);
        } catch (Exception e) {
            throw new PhobosException("Error al guardar el archivo");
        }
        resolucion = resolucionDAO.find(resolucion.getId());
        Resolucion resolucionUpd = new Resolucion(resolucion.getId());

        resolucionUpd.setRutaUrl(Constantine.S3_LINK + Constantine.S3_RESOLUCIONES_DIR + name);
        s3service.uploadFileSync(Constantine.S3_DIR, Constantine.S3_RESOLUCIONES_DIR, Constantine.TMP_DIR, name, true);
        resolucionUpd.setUserActualizacion(ds.getUsuario());
        resolucionUpd.setFechaActualizacion(today.toDate());
        resolucionUpd.setEstadoEnum(ResolucionEstadoEnum.ACT);
        resolucionDAO.updateResolucionFile(resolucionUpd);
        /*
        if (resolucion.getEsEstadoCre()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByResolucion(resolucion);
            for (Reincorporacion reincorporacion : reincorporaciones) {
                Tramite tramite = tramiteDAO.find(reincorporacion.getTramite().getId());
                List<Reincorporacion> reincorporacionesByTram = reincorporacionDAO.allByTramite(tramite);
                if (!reincorporacionesByTram.get(0).getEstadoTramite().getEsResolucionFacultad()) {
                    throw new PhobosException("Estado tramite incorrecto");
                }
                flujoTramiteAcademicoService.saveFlujoTramite(tramite, ds.getUsuario(), today);
            }
        }*/
    }

    @Override
    public List<ReunionConsejo> allReunionesConsejoByOficina(Oficina oficina) {
        return reunionConsejoDAO.allByOficina(oficina);
    }

    @Override
    public List<TramiteReunionConsejo> allTramiteReunionConsejoByReunion(ReunionConsejo reunionConsejo, TipoResolucion tipoResolucion) {
        tipoResolucion = tipoResolucionDAO.find(tipoResolucion.getId());
        TipoTramite tipoTramite = null;
        if (tipoResolucion.getEsTipoResolucionRei()) {
            tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.REI.name());
        }
        return alumnoReunionConsejoDAO.allByReunionConsejoAndTipoTramite(reunionConsejo, tipoTramite);
    }

    @Override
    public Resolucion findResolucion(Long resolucionId) {
        return resolucionDAO.find(resolucionId);
    }

    @Override
    public Tramite findTramite(Long tramiteId) {
        Tramite tramite = tramiteDAO.findById(new Tramite(tramiteId));
        List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
        tramite.setReincorporaciones(reincorporaciones);
        return tramite;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveConfirmarSubirDocumento(Resolucion resolucion, DataSessionPivot ds) {
        DateTime today = new DateTime();

        CicloAcademico cicloReincorporacion = resolucion.getCicloReincorporacion();

        Resolucion resolucionUpd = new Resolucion();
        resolucionUpd.setId(resolucion.getId());
        resolucionUpd.setUserActualizacion(ds.getUsuario());
        resolucionUpd.setFechaActualizacion(today.toDate());
        resolucionUpd.setEstadoEnum(ResolucionEstadoEnum.DOC_CONF);
        //   resolucionUpd.setCicloReincorporacion(resolucion.getCicloReincorporacion());
        resolucionDAO.updateEstado(resolucionUpd);

        if (resolucion.getTipoResolucion().getEsTipoResolucionRei()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByResolucion(resolucion);
            for (Reincorporacion reincorporacion : reincorporaciones) {
                Tramite tramite = tramiteDAO.find(reincorporacion.getTramite().getId());
                Tramite tramiteUpd = new Tramite(tramite.getId());
                tramiteUpd.setUserModificacion(ds.getUsuario());
                tramiteUpd.setFechaModificacion(today.toDate());

                List<AccionTramiteAcademico> accionesTramitesAcademicos = accionTramiteAcademicoDAO.allByTipoTramiteAndEstadoTramiteInicial(tramite.getTipoTramite(), reincorporacion.getEstadoTramite());
                if (accionesTramitesAcademicos.isEmpty() || accionesTramitesAcademicos.size() > 1) {
                    throw new PhobosException("Error al procesar el tramite");
                }

                if (!reincorporacion.getEstadoTramite().getEsResolucionFacultad()) {
                    throw new PhobosException("Estado tramite incorrecto");
                }
                if (reincorporacion.getEstaAceptado()) {
                    tramiteUpd.setEstadoEnum(TramiteEstadoEnum.ACEP);

                    /*
                    Alumno alumno = alumnoDAO.find(tramite.getAlumno());
                    AlumnoCiclo lastAlumnoCiclo = alumnoCicloDAO.findLastActiveRegByAlumno(alumno);
                    if (lastAlumnoCiclo.getSituacionFinal().isCodigoD()) {
                        alumno.setSituacionAcademica(lastAlumnoCiclo.getSituacionInicio());
                    } else {
                        alumno.setSituacionAcademica(lastAlumnoCiclo.getSituacionFinal());
                    }*/
                    reincorporacion.setCicloReincorporacion(cicloReincorporacion);
                    reincorporacionDAO.update(reincorporacion);
                    //  alumnoDAO.update(alumno);

                } else {
                    tramiteUpd.setEstadoEnum(TramiteEstadoEnum.RCHR);
                }
                tramiteDAO.updateEstado(tramiteUpd);

                tramitesAcademicosService.procesarTramite(tramite, accionesTramitesAcademicos.get(0), ds);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void saveConfirmarResVB(Resolucion resolucion, DataSessionPivot ds, CicloAcademico cicloAcademico) {
        DateTime today = new DateTime();
        Resolucion resolucionUpd = new Resolucion();
        resolucionUpd.setId(resolucion.getId());
        resolucionUpd.setUserActualizacion(ds.getUsuario());
        resolucionUpd.setFechaActualizacion(today.toDate());
        resolucionUpd.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        //   resolucionUpd.setCicloReincorporacion(resolucion.getCicloReincorporacion());
        resolucionDAO.updateEstado(resolucionUpd);

        if (resolucion.getTipoResolucion().getEsTipoResolucionRei()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByResolucion(resolucion);
            for (Reincorporacion reincorporacion : reincorporaciones) {
                Tramite tramite = tramiteDAO.find(reincorporacion.getTramite().getId());
                Tramite tramiteUpd = new Tramite(tramite.getId());
                tramiteUpd.setUserModificacion(ds.getUsuario());
                tramiteUpd.setFechaModificacion(today.toDate());

                List<AccionTramiteAcademico> accionesTramitesAcademicos = accionTramiteAcademicoDAO.allByTipoTramiteAndEstadoTramiteInicial(tramite.getTipoTramite(), reincorporacion.getEstadoTramite());
                if (accionesTramitesAcademicos.isEmpty() || accionesTramitesAcademicos.size() > 1) {
                    throw new PhobosException("Error al procesar el tramite");
                }

                if (!reincorporacion.getEstadoTramite().getEsResolucionFacultad()) {
                    throw new PhobosException("Estado tramite incorrecto");
                }

                /*  reincorporacion.setCicloReincorporacion(cicloReincorporacion);
                reincorporacionDAO.update(reincorporacion);*/
                tramitesAcademicosService.procesarTramite(tramite, accionesTramitesAcademicos.get(0), ds);
            }
        }
    }

    @Override
    public List<CicloAcademico> allCiclosToReincorporacion() {
        List<CicloAcademico> ciclosActivos = cicloAcademicoDAO.allActivos();
        List<CicloAcademico> ciclos = cicloAcademicoDAO.allUltimosByNext(5, ciclosActivos);
        return ciclos;
    }

}

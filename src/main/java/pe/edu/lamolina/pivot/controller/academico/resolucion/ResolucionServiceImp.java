package pe.edu.lamolina.pivot.controller.academico.resolucion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.crypto.Mac;
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
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.ResolucionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoCondicionalEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.TramiteReunionConsejo;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.TramitesAcademicosService;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.flujo.FlujoTramiteAcademicoService;
import pe.edu.lamolina.pivot.controller.general.oficina.OficinaService;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.GpoSeccionService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.tramite.AccionTramiteAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.CursoDirigidoDAO;
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
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

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
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    TramiteReunionConsejoDAO alumnoReunionConsejoDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    FlujoTramiteAcademicoService flujoTramiteAcademicoService;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    CursoDirigidoDAO cursoDirigidoDAO;

    @Autowired
    AccionTramiteAcademicoDAO accionTramiteAcademicoDAO;

    @Autowired
    OficinaDAO oficinaDAO;
    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    TramitesAcademicosService tramitesAcademicosService;

    @Autowired
    OficinaService oficinaService;

    @Autowired
    MatriculableService matriculableService;

    @Autowired
    GpoSeccionService gpoSeccionService;

    @Override
    public List<Resolucion> allResolucionesByFilter(DynatableFilter filter, DataSessionPivot dsp) {
        List<Resolucion> resoluciones = resolucionDAO.allByDyna(filter);
        for (Resolucion resolucione : resoluciones) {
            List<Oficina> oficinas = this.allOFicinasByUser(dsp);
            resolucione.setAutorizado(Boolean.FALSE);
            if (resolucione.getAplicacionDirecta() == 1) {
                resolucione.setAutorizado(Boolean.TRUE);
                continue;
            }
            if (resolucione.getIsEstadoDocConf()) {
                if (dsp.getOficinaMain() != null && dsp.getOficinaMain().isOficinaOera()) {
                    resolucione.setAutorizado(Boolean.TRUE);
                }
            } else {
                if (oficinas.stream().anyMatch(x -> Objects.equals(x.getId(), resolucione.getOficina().getId()) && (dsp.getOficinaMain() == null || !dsp.getOficinaMain().isOficinaOera()))) {
                    resolucione.setAutorizado(Boolean.TRUE);
                }
            }

        }
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
        ObjectUtil.eliminarAttrSinId(resolucion);
        resolucion.setTramitesReunionConsejo(resolucion.getTramitesReunionConsejo() == null ? new ArrayList() : resolucion.getTramitesReunionConsejo());
        for (TramiteReunionConsejo tramiteReunionConsejo : resolucion.getTramitesReunionConsejo()) {
            if (tramiteReunionConsejo.getSeleccionado()) {
                someChecked = true;
            }
        }
        if (!someChecked && resolucion.getTipoResolucion().getParaTramite()) {
            throw new PhobosException("Debe seleccionar algun tramite");
        }

        resolucion.setEstadoEnum(ResolucionEstadoEnum.CRE);
        resolucion.setUserRegistro(ds.getUsuario());
        resolucion.setFechaRegistro(today.toDate());
        resolucion.setUserActualizacion(null);
        resolucion.setFechaActualizacion(null);
        resolucionDAO.save(resolucion);

        for (TramiteReunionConsejo tramiteReunionConsejo : resolucion.getTramitesReunionConsejo()) {
            List<AccionTramiteAcademico> accionesTramitesAcademicos = new ArrayList<>();
            Tramite tramite = new Tramite();
            if (resolucion.getTipoResolucion().getEsTipoResolucionRei()) {

                Reincorporacion reincorporacion = reincorporacionDAO.findByTramiteEstadoTram(tramiteReunionConsejo.getTramite(), EstadoTramiteEnum.AGE_CON_FAC);
                tramite = reincorporacion.getTramite();
                accionesTramitesAcademicos = accionTramiteAcademicoDAO.allByTipoTramiteAndEstadoTramiteInicial(tramite.getTipoTramite(), reincorporacion.getEstadoTramite());
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
            } else if (resolucion.getTipoResolucion().getEsTipoCursoDirigido()) {
                CursoDirigido cursoDirigido = cursoDirigidoDAO.findByTramite(tramiteReunionConsejo.getTramite());
                tramite = cursoDirigido.getTramite();
                accionesTramitesAcademicos = accionTramiteAcademicoDAO.allByTipoTramiteAndEstadoTramiteInicial(tramite.getTipoTramite(), cursoDirigido.getEstado());

                cursoDirigido.setAceptado(tramiteReunionConsejo.getSeleccionado());
                cursoDirigido.setResolucion(resolucion);
                cursoDirigidoDAO.update(cursoDirigido);

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
            if (resolucion.getTipoResolucion().getEsTipoResolucionRei()) {

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
            } else if (resolucion.getTipoResolucion().getEsTipoCursoDirigido()) {
                CursoDirigido cursoDirigido = cursoDirigidoDAO.findByTramite(tramiteReunionConsejo.getTramite());
                cursoDirigido.setAceptado(tramiteReunionConsejo.getSeleccionado());
                cursoDirigido.setResolucion(resolucion);
                cursoDirigidoDAO.update(cursoDirigido);
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

        if (resolucion.getEsEstadoCre()) {
            if (resolucion.getTipoResolucion().getEsTipoResolucionRei()) {

                List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByResolucion(resolucion);
                for (Reincorporacion reincorporacion : reincorporaciones) {
                    Tramite tramite = tramiteDAO.find(reincorporacion.getTramite().getId());
                    List<Reincorporacion> reincorporacionesByTram = reincorporacionDAO.allByTramite(tramite);
                    if (!reincorporacionesByTram.get(0).getEstadoTramite().getEsResolucionFacultad()) {
                        throw new PhobosException("Estado tramite incorrecto");
                    }
                    flujoTramiteAcademicoService.saveFlujoTramite(tramite, ds.getUsuario(), today);
                }
            } else if (resolucion.getTipoResolucion().getEsTipoCursoDirigido()) {
                List<CursoDirigido> cursoDirigidos = cursoDirigidoDAO.allByResolucion(resolucion);
                for (CursoDirigido cursoDir : cursoDirigidos) {
                    Tramite tramite = tramiteDAO.find(cursoDir.getTramite().getId());
                    flujoTramiteAcademicoService.saveFlujoTramite(tramite, ds.getUsuario(), today);
                }
            }
        }
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
        } else if (tipoResolucion.getEsTipoResolucionRci()) {
            tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.RCI.name());
        } else if (tipoResolucion.getEsTipoCursoDirigido()) {
            tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.CURDIR.name());
        }

        List<TramiteReunionConsejo> reunionConsejos = alumnoReunionConsejoDAO.allByReunionConsejoAndTipoTramite(reunionConsejo, tipoTramite);
        List<Tramite> tramites = reunionConsejos.stream().map(x -> x.getTramite()).collect(Collectors.toList());
        Map<Long, Tramite> map = TypesUtil.convertListToMap("id", tramites);
        List<CursoDirigido> cursoDirigidos = cursoDirigidoDAO.allByTramites(tramites);
        for (CursoDirigido cursoDirigido : cursoDirigidos) {
            Tramite tramite = map.get(cursoDirigido.getTramite().getId());
            tramite.getCursoDirigido().add(cursoDirigido);
        }
        List<Reincorporacion> reincorporacions = reincorporacionDAO.allByTramite(tramites);
        for (Reincorporacion reincorporacion : reincorporacions) {
            Tramite tramite = map.get(reincorporacion.getTramite().getId());
            tramite.getReincorporaciones().add(reincorporacion);
        }
        return reunionConsejos;
    }

    @Override
    public Resolucion findResolucion(Long resolucionId) {
        return resolucionDAO.findById(resolucionId);
    }

    @Override
    public Tramite findTramite(Long tramiteId) {
        List<CursoDirigido> dirigidos = new ArrayList<>();
        Tramite tramite = tramiteDAO.findById(new Tramite(tramiteId));
        List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
        tramite.setReincorporaciones(reincorporaciones);

        CursoDirigido cursoDirigidos = cursoDirigidoDAO.findByTramite(tramite);
        dirigidos.add(cursoDirigidos);
        tramite.setCursoDirigido(dirigidos);
        return tramite;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveConfirmarSubirDocumento(Resolucion resolucionForm, DataSessionPivot ds) {
        DateTime today = new DateTime();
        Resolucion resolucion = resolucionDAO.findById(resolucionForm.getId());

        CicloAcademico cicloReincorporacion = resolucionForm.getCicloReincorporacion();

        Resolucion resolucionUpd = new Resolucion();
        resolucionUpd.setId(resolucionForm.getId());
        resolucionUpd.setUserActualizacion(ds.getUsuario());
        resolucionUpd.setFechaActualizacion(today.toDate());
        resolucionUpd.setEstadoEnum(ResolucionEstadoEnum.DOC_CONF);
        //   resolucionUpd.setCicloReincorporacion(resolucion.getCicloReincorporacion());
        resolucionDAO.updateEstado(resolucionUpd);

        if (resolucion.getTipoResolucion().getEsTipoResolucionRei()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByResolucion(resolucionForm);
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

                    Alumno alumno = alumnoDAO.find(tramite.getAlumno());
                    AlumnoCiclo lastAlumnoCiclo = alumnoCicloDAO.findLastActiveRegByAlumno(alumno);
                    if (lastAlumnoCiclo.getSituacionFinal().isCodigoD()) {
                        alumno.setSituacionAcademica(lastAlumnoCiclo.getSituacionInicio());
                    } else {
                        alumno.setSituacionAcademica(lastAlumnoCiclo.getSituacionFinal());
                    }
                    reincorporacion.setCicloReincorporacion(cicloReincorporacion);
                    reincorporacionDAO.update(reincorporacion);
                    alumnoDAO.update(alumno);

                    matriculableService.revisarSituacionAcademica(tramite.getAlumno(), ds);
                    matriculableService.saveMatriculable(tramite.getAlumno(), TipoCondicionalEnum.REI.name(), ds);
                } else {
                    tramiteUpd.setEstadoEnum(TramiteEstadoEnum.RCHR);
                }
                tramiteDAO.updateEstado(tramiteUpd);

                tramitesAcademicosService.procesarTramite(tramite, accionesTramitesAcademicos.get(0), ds);
            }
        } else if (resolucion.getTipoResolucion().getEsTipoCursoDirigido()) {
            List<CursoDirigido> cursosDirigidos = cursoDirigidoDAO.allByResolucion(resolucionForm);
            for (CursoDirigido cursosDirigido : cursosDirigidos) {
                Tramite tramite = tramiteDAO.find(cursosDirigido.getTramite().getId());
                Tramite tramiteUpd = new Tramite(tramite.getId());
                tramiteUpd.setUserModificacion(ds.getUsuario());
                tramiteUpd.setFechaModificacion(today.toDate());

                List<AccionTramiteAcademico> accionesTramitesAcademicos = accionTramiteAcademicoDAO.allByTipoTramiteAndEstadoTramiteInicial(tramite.getTipoTramite(), cursosDirigido.getEstado());

                if (cursosDirigido.getAceptado()) {
                    tramiteUpd.setEstadoEnum(TramiteEstadoEnum.ACEP);

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
        } else if (resolucion.getTipoResolucion().getEsTipoCursoDirigido()) {
            List<CursoDirigido> cursosDirigidos = cursoDirigidoDAO.allByResolucion(resolucion);
            for (CursoDirigido cursoDirigido : cursosDirigidos) {
                if (!cursoDirigido.getAceptado()) {
                    continue;
                }
                List<AccionTramiteAcademico> accionesTramitesAcademicos = accionTramiteAcademicoDAO.allByTipoTramiteAndEstadoTramiteInicial(cursoDirigido.getTramite().getTipoTramite(), cursoDirigido.getEstado());
                AnexoBoletin anexoBoletin = anexoBoletinDAO.findDepartamento(cursoDirigido.getCurso().getDepartamentoAcademico());
                Assert.isNotNull(anexoBoletin, "No existe el anexo boletín para el departamento " + cursoDirigido.getCurso().getDepartamentoAcademico().getNombre());
                List<GrupoSeccion> grupoSeccions = null;
                GrupoSeccion grupoSeccion = gpoSeccionService.findByCursoAndDocenteDirigido(cursoDirigido.getCurso(), cursoDirigido.getDocenteAsignado(), cicloAcademico);
                if (grupoSeccion == null) {
                    grupoSeccion = new GrupoSeccion();
                    grupoSeccion.setCantidad(1);
                    grupoSeccion.setCursoDirigido(Boolean.TRUE);
                    grupoSeccion.setCurso(cursoDirigido.getCurso());
                    grupoSeccion.setDocenteResponsable(cursoDirigido.getDocenteAsignado());
                    grupoSeccion.setAnexoBoletin(anexoBoletin);
                    grupoSeccions = gpoSeccionService.saveGpoSeccionHeader(grupoSeccion, cicloAcademico, ds);
                }else{
                    grupoSeccions = new ArrayList<>();
                    grupoSeccions.add(grupoSeccion);
                }
                    
                this.matricular(grupoSeccions.get(0), cursoDirigido.getTramite().getAlumno(), cursoDirigido.getCurso(), ds.getUsuario(), cicloAcademico);
                tramitesAcademicosService.procesarTramite(cursoDirigido.getTramite(), accionesTramitesAcademicos.get(0), ds);
            }

        }
    }

    @Override
    public List<CicloAcademico> allCiclosToReincorporacion() {
        List<CicloAcademico> ciclosActivos = cicloAcademicoDAO.allActivos();
        List<CicloAcademico> ciclos = cicloAcademicoDAO.allUltimosByNext(5, ciclosActivos);
        return ciclos;
    }

    @Override
    public List<Oficina> allOFicinasByUser(DataSessionPivot ds) {
        List<Oficina> oficinasResUser = new ArrayList();
        List<Oficina> oficinasResolucion = oficinaDAO.allForResoluciones();
        Map<Long, Oficina> mapOficina = TypesUtil.convertListToMap("id", oficinasResolucion);
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == OficinaEnum.OERA) {
                return oficinasResolucion;
            }
            Oficina ofiRes = mapOficina.get(oficina.getId());
            if (ofiRes != null) {
                oficinasResUser.add(oficina);
            }
        }
        return oficinasResUser;
    }

    @Override
    public List<CursoDirigido> allCursoDirigido(DynatableFilter filter, Resolucion resolucion) {
        return cursoDirigidoDAO.allByResolucion(filter, resolucion);
    }

    @Transactional
    private void matricular(GrupoSeccion gpoSeccion, Alumno alumno, Curso curso, Usuario usuario, CicloAcademico academico) {

        MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, academico);
        List<Seccion> seccions = seccionDAO.allActivosByGpoSeccion(gpoSeccion);
        for (Seccion seccion : seccions) {
            seccion.setVacantes(seccion.getVacantes() + 1);
            seccion.setMatriculados(seccion.getMatriculados() + 1);
            seccionDAO.update(seccion);

            MatriculaSeccion matriculaSeccion = new MatriculaSeccion();
            matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriculaSeccion.setFechaRegistro(new Date());
            matriculaSeccion.setUserRegistro(usuario);
            matriculaSeccion.setSeccion(seccion);
            matriculaSeccion.setMatriculaResumen(matriculaResumen);
            matriculaSeccion.setVisible(1);
            matriculaSeccion.setFechaMatricula(new Date());
            matriculaSeccion.setUserMatricula(usuario);

            matriculaSeccionDAO.save(matriculaSeccion);
        }
        AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursoCurriculaDAO.findByAlumnoCurso(alumno, curso);
        alumnoCursoCurricula.setEstadoMatriculaEnum(EstadoMatriculaEnum.MAT);
        alumnoCursoCurriculaDAO.updateEstado(alumnoCursoCurricula);

        MatriculaCurso matriculaCurso = new MatriculaCurso();
        matriculaCurso.setCurso(curso);
        matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
        matriculaCurso.setMatriculaResumen(matriculaResumen);
        matriculaCurso.setNotaAcumulada("0");
        matriculaCurso.setNotaAvance("0");
        matriculaCurso.setNotaFinal("0");
        matriculaCurso.setPorcentajeAvanceNota(0);
        matriculaCurso.setCreditosAprobados(0);
        matriculaCurso.setCreditos(curso.getCreditos());
        matriculaCurso.setTipoCursoCurricula(alumnoCursoCurricula.getTipoCursoCurricula());
        matriculaCurso.setInasistencias(0);
        matriculaCurso.setInasistenciasExoneradas(0);
        matriculaCurso.setUserMatricula(usuario);
        matriculaCurso.setFechaMatricula(new Date());
        matriculaCursoDAO.save(matriculaCurso);

    }
}

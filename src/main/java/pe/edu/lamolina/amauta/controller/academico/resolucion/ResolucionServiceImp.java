package pe.edu.lamolina.amauta.controller.academico.resolucion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.file.system.FileHelper;
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
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.ResolucionEstadoEnum;
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
import pe.edu.lamolina.amauta.controller.academico.tramitesacademicos.TramitesAcademicosService;
import pe.edu.lamolina.amauta.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.GpoSeccionService;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.controller.test.VisorCalculoNotas;
import static pe.edu.lamolina.amauta.controller.test.VisorCalculoNotas.TOKEN_CURRICULA;
import static pe.edu.lamolina.amauta.controller.test.VisorCalculoNotas.TOKEN_MATRICULABLE;
import static pe.edu.lamolina.amauta.controller.test.VisorCalculoNotas.TOKEN_PROMEDIOS;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.tramite.AccionTramiteAcademicoDAO;
import pe.edu.lamolina.amauta.dao.tramite.CursoDirigidoDAO;
import pe.edu.lamolina.amauta.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.amauta.dao.tramite.ResolucionDAO;
import pe.edu.lamolina.amauta.dao.tramite.ReunionConsejoDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoResolucionDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteReunionConsejoDAO;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.controller.general.oficina.util.OficinaService;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ResolucionServiceImp implements ResolucionService {

    private final AccionTramiteAcademicoDAO accionTramiteAcademicoDAO;
    private final AlumnoCicloDAO alumnoCicloDAO;
    private final AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;
    private final AlumnoDAO alumnoDAO;
    private final AnexoBoletinDAO anexoBoletinDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final CursoDirigidoDAO cursoDirigidoDAO;
    private final MatriculaCursoDAO matriculaCursoDAO;
    private final MatriculaResumenDAO matriculaResumenDAO;
    private final MatriculaSeccionDAO matriculaSeccionDAO;
    private final ReincorporacionDAO reincorporacionDAO;
    private final ResolucionDAO resolucionDAO;
    private final ReunionConsejoDAO reunionConsejoDAO;
    private final SeccionDAO seccionDAO;
    private final StorageService swiftService;
    private final TipoResolucionDAO tipoResolucionDAO;
    private final TipoTramiteDAO tipoTramiteDAO;
    private final TramiteDAO tramiteDAO;
    private final TramiteReunionConsejoDAO alumnoReunionConsejoDAO;

    private final GpoSeccionService gpoSeccionService;
    private final MatriculableService matriculableService;
    private final OficinaService oficinaService;
    private final TramitesAcademicosService tramitesAcademicosService;
    private final VerificadorService verificadorService;
    private final VisorCalculoNotas visorCalculoNotas;

    @Override
    public List<Resolucion> allResolucionesByFilter(DynatableFilter filter, DataSessionPivot ds) {

        List<Oficina> oficinas = oficinaService.allOficinasMainByPersona(ds.getPersona());
        boolean esTrabajadorOera = verificadorService.isTrabajadorOera(ds);

        List<Resolucion> resoluciones = resolucionDAO.allByDyna(filter);

        for (Resolucion resolucion : resoluciones) {
            resolucion.setAutorizado(Boolean.FALSE);
            if (esTrabajadorOera) {
                resolucion.setAutorizado(Boolean.TRUE);
                continue;
            }

            if (resolucion.getAplicacionDirecta() == 1) {
                resolucion.setAutorizado(Boolean.TRUE);
                continue;
            }

            for (Oficina ofi : oficinas) {
                if (ofi.getId() == resolucion.getOficina().getId().longValue()) {
                    resolucion.setAutorizado(Boolean.TRUE);
                    break;
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
        return reincorporacionDAO.allByDyna(filter);
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
        TipoResolucion tipoRes = resolucion.getTipoResolucion();
        TipoResolucion tipoResBD = tipoResolucionDAO.find(tipoRes.getId());
        if (tipoResBD.getCodigo().equals("CREA-PROFE")) {
            resolucion.setEstadoEnum(ResolucionEstadoEnum.ACT);
        }

        resolucion.setUserRegistro(ds.getUsuario());
        resolucion.setFechaRegistro(today.toDate());
        resolucion.setUserActualizacion(null);
        resolucion.setFechaActualizacion(null);
        resolucionDAO.save(resolucion);

        for (TramiteReunionConsejo tramiteReunionConsejo : resolucion.getTramitesReunionConsejo()) {
            List<AccionTramiteAcademico> accionesTramitesAcademicos = new ArrayList<>();
            Tramite tramite = new Tramite();
            if (resolucion.getTipoResolucion().isReincorporacion()) {

                Reincorporacion reincorporacion = reincorporacionDAO.findByTramiteEstadoTram(tramiteReunionConsejo.getTramite(), TramiteEstadoEnum.AGE_CON_FAC);
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
            } else if (resolucion.getTipoResolucion().isCursoDirigido()) {
                CursoDirigido cursoDirigido = cursoDirigidoDAO.findByTramite(tramiteReunionConsejo.getTramite());
                tramite = cursoDirigido.getTramite();
                accionesTramitesAcademicos = accionTramiteAcademicoDAO.allByTipoTramiteAndEstadoTramiteInicial(tramite.getTipoTramite(), cursoDirigido.getEstado());

                cursoDirigido.setAceptado(tramiteReunionConsejo.getSeleccionado());
                cursoDirigido.setResolucion(resolucion);
                cursoDirigidoDAO.update(cursoDirigido);

            }

            tramitesAcademicosService.procesarTramite(tramite, accionesTramitesAcademicos.get(0), null, ds);
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
            if (resolucion.getTipoResolucion().isReincorporacion()) {

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

                } else {
                    reincorporacion.setAceptado(BigDecimal.ZERO.intValue());
                    reincorporacion.setResolucion(resolucion);
                    reincorporacionDAO.update(reincorporacion);

                }
            } else if (resolucion.getTipoResolucion().isCursoDirigido()) {
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
            absoluteName = GlobalConstantine.TMP_DIR + name;
            FileHelper.saveToDisk(file, absoluteName);

        } catch (Exception e) {
            throw new PhobosException("Error al guardar el archivo");
        }

        resolucion = resolucionDAO.find(resolucion.getId());

        Resolucion resolucionUpd = new Resolucion(resolucion.getId());

        resolucionUpd.setRutaUrl(AcademicoConstantine.S3_URL_ACADEMICO + AcademicoConstantine.S3_RESOLUCIONES_DIR + name);
        swiftService.uploadFileSync(AcademicoConstantine.S3_BUCKET_ACADEMICO, AcademicoConstantine.S3_RESOLUCIONES_DIR, GlobalConstantine.TMP_DIR, name, true);

        resolucionUpd.setUserActualizacion(ds.getUsuario());
        resolucionUpd.setFechaActualizacion(today.toDate());
        resolucionUpd.setEstadoEnum(ResolucionEstadoEnum.ACT);
        resolucionDAO.updateResolucionFile(resolucionUpd);

    }

    @Override
    public List<ReunionConsejo> allReunionesConsejoByOficina(Oficina oficina) {
        return reunionConsejoDAO.allByOficina(oficina);
    }

    @Override
    public List<TramiteReunionConsejo> allTramiteReunionConsejoByReunion(ReunionConsejo reunionConsejo, TipoResolucion tipoResolucion) {
        tipoResolucion = tipoResolucionDAO.find(tipoResolucion.getId());
        TipoTramite tipoTramite = null;
        if (tipoResolucion.isReincorporacion()) {
            tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.REI.name());
        } else if (tipoResolucion.isRetiroCiclo()) {
            tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.RCI.name());
        } else if (tipoResolucion.isCursoDirigido()) {
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

        if (resolucion.getTipoResolucion().isReincorporacion()) {
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
                    if (lastAlumnoCiclo.getSituacionFinal().isDesertor()) {
                        alumno.setSituacionAcademica(lastAlumnoCiclo.getSituacionInicio());
                    } else {
                        alumno.setSituacionAcademica(lastAlumnoCiclo.getSituacionFinal());
                    }
                    reincorporacion.setCicloReincorporacion(cicloReincorporacion);
                    reincorporacionDAO.update(reincorporacion);
                    alumnoDAO.update(alumno);

                    String token = RandomStringUtils.randomAlphanumeric(43);
                    String tokenProm = token + TOKEN_PROMEDIOS;
                    String tokenCurri = token + TOKEN_CURRICULA;
                    String tokenMatri = token + TOKEN_MATRICULABLE;
                    List<Alumno> alumnos = new ArrayList<>();
                    alumnos.add(alumno);

                    visorCalculoNotas.createToken(tokenProm, alumnos);
                    visorCalculoNotas.createToken(tokenCurri, alumnos);
                    visorCalculoNotas.createToken(tokenMatri, alumnos);

                    matriculableService.calcularPromedios(token, ds);
                    matriculableService.revisarCurriculaAlumnos(ds, token);
                    matriculableService.revisarMatriculables(ds, token);

                } else {
                    tramiteUpd.setEstadoEnum(TramiteEstadoEnum.RCHR);
                }
                tramiteDAO.updateEstado(tramiteUpd);

                tramitesAcademicosService.procesarTramite(tramite, accionesTramitesAcademicos.get(0), null, ds);
            }
        } else if (resolucion.getTipoResolucion().isCursoDirigido()) {
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

                tramitesAcademicosService.procesarTramite(tramite, accionesTramitesAcademicos.get(0), null, ds);
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

        if (resolucion.getTipoResolucion().isReincorporacion()) {
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

                tramitesAcademicosService.procesarTramite(tramite, accionesTramitesAcademicos.get(0), null, ds);
            }
        } else if (resolucion.getTipoResolucion().isCursoDirigido()) {
            List<CursoDirigido> cursosDirigidos = cursoDirigidoDAO.allByResolucion(resolucion);
            for (CursoDirigido cursoDirigido : cursosDirigidos) {
                if (!cursoDirigido.getAceptado()) {
                    continue;
                }
                List<AccionTramiteAcademico> accionesTramitesAcademicos = accionTramiteAcademicoDAO.allByTipoTramiteAndEstadoTramiteInicial(cursoDirigido.getTramite().getTipoTramite(), cursoDirigido.getEstado());
                AnexoBoletin anexoBoletin = anexoBoletinDAO.findDepartamento(cursoDirigido.getCurso().getDepartamentoAcademico());

                if (anexoBoletin == null) {

                    throw new PhobosException("No existe el anexo boletín para el departamento " + cursoDirigido.getCurso().getDepartamentoAcademico().getNombre());
                }

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
                } else {
                    grupoSeccions = new ArrayList<>();
                    grupoSeccions.add(grupoSeccion);
                }

                this.matricular(grupoSeccions.get(0), cursoDirigido.getTramite().getAlumno(), cursoDirigido.getCurso(), ds.getUsuario(), cicloAcademico);
                tramitesAcademicosService.procesarTramite(cursoDirigido.getTramite(), accionesTramitesAcademicos.get(0), null, ds);
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
    public List<CursoDirigido> allCursoDirigido(DynatableFilter filter, Resolucion resolucion) {
        return cursoDirigidoDAO.allByResolucion(filter, resolucion);
    }

    @Transactional
    private void matricular(GrupoSeccion gpoSeccion, Alumno alumno, Curso curso, Usuario usuario, CicloAcademico ciclo) {

        MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, ciclo);
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

        int cursosMat = 0;
        int creditosMat = 0;
        List<MatriculaCurso> cursosMatriculados = matriculaCursoDAO.allMatriculadosByAlumnoCiclo(alumno, ciclo);
        for (MatriculaCurso matCurso : cursosMatriculados) {
            if (matCurso.getEstadoEnum() == MAT) {
                cursosMat++;
                creditosMat += matCurso.getCreditos();
            }
        }

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

        MatriculaResumen matriculaResumenUpd = new MatriculaResumen(matriculaResumen.getId());
        matriculaResumenUpd.setEstadoEnum(EstadoMatriculaEnum.MAT);
        matriculaResumenUpd.setCursosMatriculados(cursosMat + 1);
        matriculaResumenUpd.setCreditosMatriculados(creditosMat + curso.getCreditos());
        matriculaResumenDAO.updateColumns(matriculaResumenUpd, "estado", "cursosMatriculados", "creditosMatriculados");

    }
}

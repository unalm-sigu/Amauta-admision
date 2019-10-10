package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import pe.edu.lamolina.pivot.zelper.bean.FormDataBean;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import static pe.edu.lamolina.model.enums.OficinaEnum.OERA;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import static pe.edu.lamolina.model.enums.TipoOficinaEnum.FAC;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.AccionTramiteDocumento;
import pe.edu.lamolina.model.tramite.AutorizacionRegistro;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.TramiteReunionConsejo;
import pe.edu.lamolina.model.tramite.FlujoTramiteAcademico;
import pe.edu.lamolina.model.tramite.FormularioEstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteCorreccionHistorial;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.flujo.FlujoTramiteAcademicoService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.tramite.AccionTramiteAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.AutorizacionRegistroDAO;
import pe.edu.lamolina.pivot.dao.tramite.CursoDirigidoDAO;
import pe.edu.lamolina.pivot.dao.tramite.FlujoTramiteAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.FormularioEstadoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReunionConsejoDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteReunionConsejoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.pdf.PdfContent;
import pe.edu.lamolina.pivot.zelper.pdf.PdfGenerator;
import pe.edu.lamolina.pivot.zelper.pdf.TipoPdfEnum;
import pe.edu.lamolina.pivot.controller.academico.infoacademico.InfoAcademicoService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.controller.academico.reunionconsejo.ReunionConsejoService;
import pe.edu.lamolina.pivot.controller.general.oficina.OficinaService;
import pe.edu.lamolina.pivot.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.tramite.AccionTramiteDocumentoDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteCorreccionHistorialDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDocumentoAcademicoDAO;

@Service
@Transactional(readOnly = true)
public class TramitesAcademicosServiceImp implements TramitesAcademicosService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    FacultadDAO facultadDAO;

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

    @Autowired
    AccionTramiteAcademicoDAO accionTramiteAcademicoDAO;

    @Autowired
    AccionTramiteDocumentoDAO accionTramiteDocumentoDAO;

    @Autowired
    FormularioEstadoTramiteDAO formularioEstadoTramiteDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    CursoDirigidoDAO cursoDirigidoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    PdfGenerator pdfGenerator;

    @Autowired
    InfoAcademicoService infoAcademicoService;

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    AutorizacionRegistroDAO autorizacionRegistroDAO;

    @Autowired
    PromedioService promedioService;

    @Autowired
    MatriculableService matriculableService;

    @Autowired
    OficinaService oficinaService;

    @Autowired
    DocenteDAO docenteDAO;

    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;

    @Autowired
    TramiteDocumentoAcademicoDAO tramiteDocumentoAcademicoDAO;

    @Autowired
    TramiteCorreccionHistorialDAO correccionHistorialDAO;

    @Autowired
    ReunionConsejoService reunionConsejoService;

    private DateTime today = new DateTime();

    @Override
    public List<Tramite> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds) {
        List<Tramite> tramites = tramiteDAO.allByFilter(filter);
        List<AccionTramiteAcademico> accionesTramitesAcademicos = accionTramiteAcademicoDAO.all();
        List<FormularioEstadoTramite> formulariosEstadoTramite = formularioEstadoTramiteDAO.all();
        List<Reincorporacion> reincorporacions = reincorporacionDAO.allByTramite(tramites);
        List<CursoDirigido> cursosDirigidos = cursoDirigidoDAO.allByTramites(tramites);
        List<Oficina> oficinas = new ArrayList();
        for (Tramite tramite : tramites) {

            List<Reincorporacion> reincorporacionesTramite = reincorporacions.stream().filter(x -> Objects.equals(x.getTramite().getId(), tramite.getId())).collect(Collectors.toList());
            List<CursoDirigido> cursosDirigidosTramite = cursosDirigidos.stream().filter(x -> Objects.equals(x.getTramite().getId(), tramite.getId())).collect(Collectors.toList());
            tramite.setReincorporaciones(reincorporacionesTramite);
            tramite.setCursoDirigido(cursosDirigidosTramite);

            TramiteReunionConsejo tramiteReunionConsejo = tramiteReunionConsejoDAO.findByTramite(tramite);

            if (tramiteReunionConsejo != null) {
                tramite.setTramitesReunionConsejo(new ArrayList<>());
                tramite.getTramitesReunionConsejo().add(tramiteReunionConsejo);
            } else {
                tramite.setTramitesReunionConsejo(null);
            }

            if (tramite.getReincorporaciones() != null && !tramite.getReincorporaciones().isEmpty()) {
                Reincorporacion reincorporacion = tramite.getReincorporaciones().get(0);
                tramite.setEstadoTramite(reincorporacion.getEstadoTramite());
            } else {
                CursoDirigido cd = cursoDirigidoDAO.findByTramite(tramite);
                if (cd != null) {
                    logger.debug("Setting el estado {}", cd.getEstado().getNombre());
                    tramite.setEstadoTramite(cd.getEstado());
                }
            }
            List<AccionTramiteAcademico> accionesTramitesAcademicosBy = new ArrayList<>();
            CursoDirigido cursoDirigido = null;
            if (tramite.isTipoCursoDirigido()) {
                cursoDirigido = tramite.getCursoDirigido().get(0);
                if (cursoDirigido.getEstado().getEsRevicionDepartamento()) {
                    if (tramite.getOficina().getJefeEncargado() != null && !Objects.equals(tramite.getOficina().getJefeEncargado().getId(), ds.getPersona().getId())) {
                        continue;
                    } else if (tramite.getOficina().getJefeEncargado() == null && !Objects.equals(tramite.getOficina().getPersonaJefe().getId(), ds.getPersona().getId())) {
                        continue;
                    }
                }
                if (cursoDirigido.getEstado().getEsVBDepartamentoDocente()) {
                    this.findOficina(oficinas, ds);
                    Long instancia = cursoDirigido.getFacultad().getId();
                    if (!oficinas.stream().anyMatch(x -> Objects.equals(x.getInstanciaOficina(), instancia) && x.getTipoOficina().getCodigoEnum() == FAC)
                            || (ds.getOficinaMain() != null && ds.getOficinaMain().getCodigoEnum() == OERA)) {
                        continue;
                    }
                }
            }
            accionesTramitesAcademicosBy = accionesTramitesAcademicos.stream().filter(
                    req -> req.getTipoTramite().equals(tramite.getTipoTramite())
                    && req.getEstadoTramiteInicio().equals(tramite.getEstadoTramite())
            ).collect(Collectors.toList());

            tramite.setAccionesTramitesAcademico(accionesTramitesAcademicosBy);

            FormularioEstadoTramite formularioEstadoTramite = formulariosEstadoTramite.stream().filter(x
                    -> x.getEstadoTramite().equals(tramite.getEstadoTramite())
                    && x.getTipoTramite().equals(tramite.getTipoTramite())).findFirst().orElse(null);

            tramite.setFormularioEstadoTramite(formularioEstadoTramite);

        }
        return tramites;
    }

    @Override
    public List<ReunionConsejo> allReunionConsejoByDyna(DynatableFilter filter, List<Oficina> oficina) {
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

    @Override
    public Tramite findTramite(Long tramiteId) {
        Tramite tramite = tramiteDAO.findById(new Tramite(tramiteId));
        if (tramite.getTipoTramite().getEsTipoTramiteRei()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
            if (!reincorporaciones.isEmpty()) {
                tramite.setEstadoTramite(reincorporaciones.get(0).getEstadoTramite());
            }
        } else if (tramite.getTipoTramite().getEsTramiteDocumento()) {
            TramiteDocumentoAcademico documentoAcademico = tramiteDocumentoAcademicoDAO.findTramite(tramite);
            if (documentoAcademico != null) {
                tramite.setEstadoTramite(documentoAcademico.getEstadoTramite());
                tramite.setTipoDocumentoAcademico(documentoAcademico.getTipoDocumentoAcademico());
            }
        } else if (tramite.getTipoTramite().getEsTramiteCorrHisto()) {
            TramiteCorreccionHistorial tramiteCorrecionHisto = correccionHistorialDAO.findTramite(tramite);
            if (tramiteCorrecionHisto != null) {
                tramite.setEstadoTramite(tramiteCorrecionHisto.getEstadoTramite());
            }
        } else {
            CursoDirigido cd = cursoDirigidoDAO.findByTramite(tramite);
            if (cd != null) {
                logger.debug("Setting el estado {}", cd.getEstado().getNombre());
                tramite.setEstadoTramite(cd.getEstado());
            }
        }
        TramiteReunionConsejo tramiteReunionConsejo = tramiteReunionConsejoDAO.findByTramite(tramite);
        tramite.setAccionesTramitesAcademico(accionTramiteAcademicoDAO.allByTipoTramiteAndEstadoTramiteInicial(tramite.getTipoTramite(), tramite.getEstadoTramite()));
        if (tramite.getTipoDocumentoAcademico() != null) {
            tramite.setAccionesTramitesDocumentos(accionTramiteDocumentoDAO.allByTipoTramiteAndEstadoTramiteInicial(tramite.getTipoDocumentoAcademico(), tramite.getEstadoTramite()));
        }
        tramite.setFormularioEstadoTramite(formularioEstadoTramiteDAO.findByTipoTramiteAndEstadoTramite(tramite.getTipoTramite(), tramite.getEstadoTramite()));
        tramite.setTramiteReunionConsejo(tramiteReunionConsejo);
        return tramite;
    }

    @Override
    @Transactional
    public void aceptarSolReincorporacion(Tramite tramite, AccionTramiteAcademico accionTramiteAcademico, DataSessionPivot ds) {
        today = new DateTime();

        accionTramiteAcademico = accionTramiteAcademicoDAO.find(accionTramiteAcademico.getId());
        this.procesarTramite(tramite, accionTramiteAcademico, null, ds);
    }

    @Override
    @Transactional
    public void procesarTramite(Tramite tramiteForm, AccionTramiteAcademico accionTramiteAcademico, AccionTramiteDocumento accionTramiteDocumento, DataSessionPivot ds) {
        today = new DateTime();

        Tramite tramite = this.findTramite(tramiteForm.getId());

        AutorizacionRegistro autorizacionRegistro = autorizacionRegistroDAO.findByTramite(tramite);
//        logger.debug("EstadoTramite Inicio {}, Estado Fin {}", ObjectUtil.getParentTree(accionTramiteAcademico, "estadoTramiteInicio.nombre"), ObjectUtil.getParentTree(accionTramiteAcademico, "estadoTramiteFinal.nombre"));
//        logger.debug("Autorizacion Registro {}", autorizacionRegistro != null ? autorizacionRegistro.getId() : "No tiene");

        Tramite tramiteUpd = new Tramite();
        tramiteUpd.setId(tramite.getId());
        tramiteUpd.setEstadoEnum(TramiteEstadoEnum.PROC);
        if (accionTramiteAcademico != null) {

            if (accionTramiteAcademico.getEsFinalBool()) {
                if (accionTramiteAcademico.getEsSatisfactorio()) {
                    tramiteUpd.setEstadoEnum(TramiteEstadoEnum.ACEP);
                } else {
                    tramiteUpd.setEstadoEnum(TramiteEstadoEnum.RCHZ);
                }
            }
        }
        tramiteUpd.setUserModificacion(ds.getUsuario());
        tramiteUpd.setFechaModificacion(today.toDate());
        tramiteDAO.updateEstado(tramiteUpd);

        if (accionTramiteAcademico != null ? accionTramiteAcademico.getEsSolicitarMotivo() : accionTramiteDocumento.getSolicitaMotivo() == 1) {
            tramiteUpd.setObservacion(tramiteForm.getObservacion());
            tramiteDAO.updateObservacion(tramite);
        }
        if (accionTramiteAcademico != null && accionTramiteAcademico.getEstadoTramiteFinal().getEsAgendadoConsejoFacultad()) {
            this.agendarSolicitud(tramite, tramiteForm.getTramiteReunionConsejo().getReunionConsejo(), today, ds.getUsuario());
        }
        if (accionTramiteAcademico != null ? accionTramiteAcademico.getEstadoTramiteFinal().getEsVistoBuenoUR() : accionTramiteDocumento.getEstadoTramiteFinal().getEsVistoBuenoUR()) {
            this.vistoBuenoUR(autorizacionRegistro, ds.getUsuario());
        }
        if ((accionTramiteAcademico != null ? accionTramiteAcademico.getEstadoTramiteInicio().getEsVistoBuenoUR() : accionTramiteDocumento.getEstadoTramite().getEsVistoBuenoUR())) {
            if (accionTramiteAcademico != null ? accionTramiteAcademico.getEsSatisfactorio() : accionTramiteDocumento.getEsSatisfactorio()) {

                this.aprobadoUR(tramite, accionTramiteAcademico, autorizacionRegistro, ds.getUsuario(), today);
                infoAcademicoService.calcularPromedio(tramite.getAlumno(), ds);
            }
        }
        if (accionTramiteAcademico != null ? accionTramiteAcademico.getEstadoTramiteFinal().getEsControlCalidad() : accionTramiteDocumento.getEstadoTramiteFinal().getEsControlCalidad()) {
            //Si no hubo modificaciones en el historial del alumno, creamos la autorizacion registro
//            if (tramite.getTipoTramite().getEsTipoTramiteCurDir()) {
//            accionTramiteAcademico.setOficinaDestino(tramiteForm.getOficina());
//            }
            if (autorizacionRegistro == null) {
                this.crearAutorizacionRegistro(tramite.getAlumno(), tramite, ds);
            }
        }
        if (tramite.getTipoTramite().getEsTipoTramiteCurDir()) {
            CursoDirigido cursoDirigido = cursoDirigidoDAO.findByTramite(tramite);
            Oficina oficinaDestino = oficinaDAO.findByTipoAndFacultad(FAC, cursoDirigido.getFacultad());
            accionTramiteAcademico.setOficinaDestino(oficinaDestino);
            accionTramiteAcademico.setOficinaOrigen(oficinaDestino);

        }

//        if (accionTramiteAcademico.getEsFinalBool() && accionTramiteAcademico.getEsNegado()) {
//            List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allByAutorizacionRegistro(autorizacionRegistro);
//            for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
//                if (ObjectUtil.getParentTree(alumnoCicloCurso, "alumnoCicloCursoOrigen.id") != null) {
//                    AlumnoCicloCurso alumnoCicloCursoOrigenUpd = new AlumnoCicloCurso();
//                    alumnoCicloCursoOrigenUpd.setId(alumnoCicloCurso.getAlumnoCicloCursoOrigen().getId());
//                    alumnoCicloCursoOrigenUpd.setEstado(EstadoMatriculaEnum.MAT);
//                    alumnoCicloCursoOrigenUpd.setRegistroActivo(BigDecimal.ONE.intValue());
//                    alumnoCicloCursoDAO.updateEstadoRegistroActivo(alumnoCicloCursoOrigenUpd);
//
////                    alumnoCicloCursoDAO.delete(alumnoCicloCurso);
//                }
//            }
//        }
        this.saveFlujoTramite(tramite, accionTramiteAcademico, accionTramiteDocumento, ds.getUsuario(), today);

    }

    private AutorizacionRegistro crearAutorizacionRegistro(Alumno alumno, Tramite tramite, DataSessionPivot ds) {
        AutorizacionRegistro autorizacionRegistro = autorizacionRegistroDAO.findByTramite(tramite);
        if (autorizacionRegistro == null) {
            autorizacionRegistro = new AutorizacionRegistro();
        }
        autorizacionRegistro.setMotivo("");
        autorizacionRegistro.setAlumno(alumno);
        autorizacionRegistro.setEstado(EstadoEnum.CRE.name());
        autorizacionRegistro.setFechaRegistro(today.toDate());
        autorizacionRegistro.setUserRegistro(ds.getUsuario());
        autorizacionRegistro.setTramite(tramite);
        autorizacionRegistroDAO.save(autorizacionRegistro);

        return autorizacionRegistro;
    }

    @Transactional(readOnly = false)
    public void saveFlujoTramite(Tramite tramite, AccionTramiteAcademico accionTramiteAcademico, AccionTramiteDocumento accionTramiteDocumento, Usuario usuario, DateTime today) {

        Alumno alumnoTramite = alumnoDAO.find(tramite.getAlumno());

        FlujoTramiteAcademico flujoTramiteAcademico = new FlujoTramiteAcademico();
        Map oficinas = new HashMap();
        if (accionTramiteAcademico != null) {
            oficinas = oficinaDAO.findOficinaOrigenDestinoByEstadoTramiteAcad(accionTramiteAcademico, alumnoTramite);
            flujoTramiteAcademico.setEstadoTramite(accionTramiteAcademico.getEstadoTramiteFinal());
            flujoTramiteAcademico.setOrden(accionTramiteAcademico.getOrdenOpcion());
            if (accionTramiteAcademico.getEsSolicitarMotivo()) {
                flujoTramiteAcademico.setMotivo(tramite.getObservacion());
            }
        } else {
            oficinas = oficinaDAO.findOficinaOrigenDestinoByEstadoTramiteDoc(accionTramiteDocumento, alumnoTramite);
            flujoTramiteAcademico.setEstadoTramite(accionTramiteDocumento.getEstadoTramiteFinal());
            flujoTramiteAcademico.setOrden(accionTramiteDocumento.getOrden());
            if (accionTramiteDocumento.getSolicitaMotivo() == 1) {
                flujoTramiteAcademico.setMotivo(tramite.getObservacion());
            }
        }
        flujoTramiteAcademico.setOficinaOrigen((Oficina) oficinas.get("oficinaOrigen"));
        flujoTramiteAcademico.setOficinaDestino((Oficina) oficinas.get("oficinaDestino"));
        flujoTramiteAcademico.setFechaRegistro(today.toDate());
        flujoTramiteAcademico.setTramiteAcademico(tramite);
        flujoTramiteAcademico.setUserRegistro(usuario);
        flujoTramiteAcademicoDAO.save(flujoTramiteAcademico);

        if (tramite.getTipoTramite().getEsTipoTramiteRei()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
            Reincorporacion reincorporacion = reincorporaciones.get(0);
            Reincorporacion reincorporacionUpd = new Reincorporacion();
            reincorporacionUpd.setId(reincorporacion.getId());
            reincorporacionUpd.setEstadoTramite(accionTramiteAcademico.getEstadoTramiteFinal());
            reincorporacionDAO.updateEstado(reincorporacionUpd);
        } else if (tramite.getTipoTramite().getEsTipoTramiteCurDir()) {
            CursoDirigido cd = cursoDirigidoDAO.findByTramite(tramite);
            cd.setEstado(accionTramiteAcademico.getEstadoTramiteFinal());
            cursoDirigidoDAO.update(cd);
        } else if (tramite.getTipoTramite().getEsTramiteDocumento()) {
            TramiteDocumentoAcademico tramiteDocumentoAcademico = tramiteDocumentoAcademicoDAO.findTramite(tramite);
            tramiteDocumentoAcademico.setEstadoTramite(accionTramiteDocumento.getEstadoTramiteFinal());
            tramiteDocumentoAcademicoDAO.updateColumns(tramiteDocumentoAcademico, "estadoTramite");
        } else if (tramite.getTipoTramite().getEsTramiteCorrHisto()) {
            TramiteCorreccionHistorial tramiteCorrecion = correccionHistorialDAO.findTramite(tramite);
            tramiteCorrecion.setEstadoTramite(accionTramiteAcademico.getEstadoTramiteFinal());
            tramiteCorrecion.setFechaModificacion(new Date());
            tramiteCorrecion.setUserModificacion(usuario);
            correccionHistorialDAO.updateColumns(tramiteCorrecion, "estadoTramite", "fechaModificacion", "userModificacion");
        }
    }

    public void aprobadoUR(Tramite tramite, AccionTramiteAcademico accionTramiteAcademico, AutorizacionRegistro autorizacionRegistro, Usuario usuario, DateTime today) {
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allByAutorizacionRegistro(autorizacionRegistro);
        for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
            if (alumnoCicloCurso.getIsEstadoMatriculado()) {
                alumnoCicloCurso.setEstado(EstadoMatriculaEnum.MAT);
                alumnoCicloCurso.setRegistroActivo(BigDecimal.ONE.intValue());
                alumnoCicloCursoDAO.updateEstadoRegistroActivo(alumnoCicloCurso);
            }
        }
        autorizacionRegistro.setEstadoEnum(EstadoEnum.ACT);
        autorizacionRegistro.setFechaCierre(new Date());
        autorizacionRegistro.setIdUserCierre(usuario.getId());
        autorizacionRegistroDAO.updateColumns(autorizacionRegistro, "estado", "fechaCierre", "idUserCierre");

    }

    @Override
    @Transactional
    public void agendarSolicitud(Tramite tramite, ReunionConsejo reunionConsejo, DateTime today, Usuario usuario) {
        //   tramite = tramiteDAO.find(tramite.getId());
        //  reunionConsejo = reunionConsejoDAO.find(reunionConsejo.getId());
        if (reunionConsejo == null) {
            throw new PhobosException("Debe seleccionar la reunión consejo.");
        }
        List<TramiteReunionConsejo> tramiteReunionesConsejo = tramiteReunionConsejoDAO.allByReunionConsejoAndTipoTramite(reunionConsejo, tramite.getTipoTramite());

        TramiteReunionConsejo tramiteReunionConsejoActiva = null;
        for (TramiteReunionConsejo alumnoReunionConsejo : tramiteReunionesConsejo) {
            if (alumnoReunionConsejo.getEsActivo() && alumnoReunionConsejo.getTramite().equals(tramite)) {
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
        /*
        Facultad facultad = tramite.getAlumno().getCarrera().getFacultad();

        List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
        Reincorporacion reincorporacion = reincorporaciones.get(0);

        if (!reincorporacion.getEstadoTramite()
                .getEsSolicitudHistorialRevisado()) {
            throw new PhobosException("Estado incorrecto");
        }

        flujoTramiteAcademicoService.saveFlujoTramite(tramite, usuario, today);*/
    }

    @Override
    public String cursoDirigidoReporte(Tramite tramite, DataSessionPivot ds) {
        List<String> pdfs = createInfoPDF(tramite, ds);
        return pdfGenerator.concatPDFs(pdfs, "CursoDirigido", true);
    }

    private List<String> createInfoPDF(Tramite tramite, DataSessionPivot ds) {
        tramite = tramiteDAO.find(tramite.getId());
        CursoDirigido cursoDirigido = cursoDirigidoDAO.findByTramite(tramite);

        FormDataBean data = convertStringToJSON(cursoDirigido.getSituacionActual());

        Alumno alumno = tramite.getAlumno();
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        List<MatriculaSeccion> matriculados = matriculaSeccionDAO.allMatriculadosByAlumnoCiclo(alumno, cicloAcademico);

        Map<GrupoSeccion, List<Seccion>> gpoSecciones = matriculados.stream().map(MatriculaSeccion::getSeccion).collect(Collectors.groupingBy(x -> x.getGrupoSeccion()));

        Curso curso = cursoDirigido.getCurso();
//        PlanCurricular planCurricular = alumno.getPlanCurricular();

//        Map<Integer, List<CursoCurricula>> cursosPlanCurricular = planCurricular.getCursoCurricula().stream().filter(cc -> cc.getNumeroCiclo() != null).collect(Collectors.groupingBy(cc -> cc.getNumeroCiclo()));
//
//        Map<Integer, List<AlumnoCursoCurricula>> avanceCurricular = alumnoCursoCurriculaDAO.allCiclosAlumno(alumno)
//                .stream()
//                .collect(Collectors.groupingBy(x -> x.getNumeroCiclo()));
        TipoCursoCurricula tipoCursoCurricula = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.DEP);
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivosByAlumno(alumno);
        for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
            if (alumnoCicloCurso.getTipoCursoCurricula() == null) {
                alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurricula);
            }
        }
        Map<TipoCursoCurricula, List<AlumnoCicloCurso>> historial = alumnoCicloCursos
                .stream()
                .filter(x -> x.isAprobado())
                .collect(Collectors.groupingBy(acc -> acc.getTipoCursoCurricula()));

        Context ctx = new Context();

        SortedMap<TipoCursoCurricula, List<AlumnoCicloCurso>> historialSorted = new TreeMap<>(Comparator.comparing(TipoCursoCurricula::getOrden));
        historialSorted.putAll(historial);

        List< AlumnoCiclo> alumnoCiclo = alumnoCicloCursos.stream().map(x -> x.getAlumnoCiclo()).collect(Collectors.toList());

        int creditosConvalidados = 0;

        List<AlumnoCicloCurso> listAlumnoCicloCurso = alumnoCicloCursoDAO.allByAlumnoOrderByTipoCurso(alumno);

        for (AlumnoCicloCurso alumnoCicloCurso : listAlumnoCicloCurso) {
            if (alumnoCicloCurso.getNota().equals("TE")) {
                creditosConvalidados = creditosConvalidados + alumnoCicloCurso.getCreditos();
            }
        }

        alumno.setCreditosConvalidadosTransient(creditosConvalidados);

        ctx.setVariable("alumno", alumno);
        ctx.setVariable("ciclo", cicloAcademico);
        ctx.setVariable("curso", curso);
//        ctx.setVariable("avanceCurricular", avanceCurricular);
        ctx.setVariable("historial", historialSorted);
//        ctx.setVariable("planCurricular", planCurricular);
//        ctx.setVariable("planCurricularCursos", cursosPlanCurricular);
        ctx.setVariable("alumnoCiclo", alumnoCiclo.get(0));
        ctx.setVariable("matriculados", matriculados);
        ctx.setVariable("gpoSecciones", gpoSecciones);
        ctx.setVariable("cursoDirigido", cursoDirigido);
        ctx.setVariable("matriculaResumen", matriculaResumen);

        ctx.setVariable("situacionActual", data);
        ctx.setVariable("fecha", TypesUtil.getStringDate(new DateTime().toDate(), " dd 'de' MMMM 'del' yyyy", "es"));
        ctx.setVariable("alumnoCicloCurso", listAlumnoCicloCurso);

        PdfContent pdfMatriculados = new PdfContent();
        pdfMatriculados.setContext(ctx);
        pdfMatriculados.setTipoPdfEnum(TipoPdfEnum.CURSOS_MATRICULADOS);

        PdfContent pdfHistorial = new PdfContent();
        pdfHistorial.setContext(ctx);
        pdfHistorial.setTipoPdfEnum(TipoPdfEnum.HISTORIAL_ACADEMICO_CURDIR);

//        PdfContent pdfHistorialListado = new PdfContent();
//        pdfHistorialListado.setContext(ctx);
//        pdfHistorialListado.setTipoPdfEnum(TipoPdfEnum.HISTORIAL_ACADEMICO_LISTADO);
//        PdfContent pdfPlanCurricular = new PdfContent();
//        pdfPlanCurricular.setContext(ctx);
//        pdfPlanCurricular.setTipoPdfEnum(TipoPdfEnum.PLAN_CURRICULAR);
        PdfContent pdfHorario = new PdfContent();
        pdfHorario.setContext(ctx);
        pdfHorario.setTipoPdfEnum(TipoPdfEnum.HORARIO);

        PdfContent pdfCursoDirigido = new PdfContent();
        pdfCursoDirigido.setContext(ctx);
        pdfCursoDirigido.setTipoPdfEnum(TipoPdfEnum.DETALLE_CURSO_DIRIGIDO);

        List<Dia> dias = diaDAO.allDia();
        List<HorarioSeccion> hss = infoAcademicoService.allSeccionHorarioAlumnoByAlumnoCicloACademico(alumno, cicloAcademico);
        List<Hora> horas = findLimiteHoras(hss);
        ctx.setVariable("horas", horas);
        ctx.setVariable("dias", dias);
        ctx.setVariable("datosHorario", findHorario(alumno, cicloAcademico, horas, dias));

        List<String> pdfs = Arrays.asList(
                pdfGenerator.generateDocument(pdfCursoDirigido),
                //                pdfGenerator.generateDocument(pdfPlanCurricular),
                pdfGenerator.generateDocument(pdfHistorial),
                //                pdfGenerator.generateDocument(pdfHistorialListado),
                pdfGenerator.generateDocument(pdfMatriculados),
                pdfGenerator.generateDocument(pdfHorario)
        );

        return pdfs;
    }

    private List<Hora> findLimiteHoras(List<HorarioSeccion> clases) {
        Hora horaMin = null;
        Hora horaMax = null;
        if (clases.isEmpty()) {
            return new ArrayList<>();
        }
        for (HorarioSeccion hs : clases) {
            if (horaMin == null || hs.getHora().getCodigo().compareTo(horaMin.getCodigo()) < 0) {
                horaMin = hs.getHora();
            }
            if (horaMax == null || hs.getHora().getCodigo().compareTo(horaMax.getCodigo()) > 0) {
                horaMax = hs.getHora();
            }
        }

        return horaDAO.allByInicioFin(horaMin, horaMax);
    }

    private Map<Dia, List<HorarioSeccion>> findHorario(Alumno alumno, CicloAcademico ciclo, List<Hora> horas, List<Dia> dias) {
        List<HorarioSeccion> hss = infoAcademicoService.allSeccionHorarioAlumnoByAlumnoCicloACademico(alumno, ciclo);

        Map<Dia, List<HorarioSeccion>> mapDia = hss.stream().collect(Collectors.groupingBy(HorarioSeccion::getDia));
        Map<Dia, Map<Integer, HorarioSeccion>> mapHoras = new HashMap<>();

        for (Map.Entry<Dia, List<HorarioSeccion>> entry : mapDia.entrySet()) {
            mapHoras.put(entry.getKey(), entry.getValue().stream().collect(Collectors.toMap(x -> x.getHora().getNumero(), x -> x)));
        }

        SortedMap<Dia, List<HorarioSeccion>> mapDiaCompleto = new TreeMap<>(Comparator.comparing(Dia::getNumeroDia));

        for (Dia dia : dias) {
            List<HorarioSeccion> clasesDelDia = new ArrayList<>();
            for (Hora hora : horas) {
                if (mapHoras.containsKey(dia)) {
                    clasesDelDia.add(mapHoras.get(dia).get(hora.getNumero()));
                } else {
                    clasesDelDia.add(null);
                }
            }
            mapDiaCompleto.put(dia, clasesDelDia);
        }

        return mapDiaCompleto;

    }

    @Override
    public List<Curso> allCursos() {
        return cursoDAO.all();
    }

    @Override
    public List<Curso> allCursosByName(String nombre, Integer limit) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        return cursoDAO.searchLikeNombre(nombre, limit);
    }

    @Override
    public List<CicloAcademico> allCiclosAcademicosByName(String nombre, Alumno alumno) {
        alumno = alumnoDAO.find(alumno);
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allActivesByAlumnoAsc(alumno);
        List<CicloAcademico> ciclosAcademicos = alumnoCiclos.stream().map(x -> x.getCicloAcademico()).collect(Collectors.toList());
        return cicloAcademicoDAO.allByLikeName(nombre, alumno.getModalidadEstudio(), ciclosAcademicos, 15);
    }

    @Override
    public ArrayNode allAlumnoCicloJson(Alumno alumno, AlumnoCiclo ciclo) {
        List<AlumnoCicloCurso> alumnosCiclosCurso = alumnoCicloCursoDAO.allByAlumnoAndAlumnoCiclo(alumno, ciclo);
        ArrayNode promediosJson = infoAcademicoService.allPromediosJson(alumnosCiclosCurso);
        return promediosJson;
    }

    @Override
    public AlumnoCiclo findAlumnoCiclo(AlumnoCiclo alumnoCiclo, Tramite tramite) {
        alumnoCiclo = alumnoCicloDAO.find(alumnoCiclo.getId());
        AutorizacionRegistro autorizacionRegistro = autorizacionRegistroDAO.findByTramite(tramite);
        if (alumnoCiclo != null) {
            List<AlumnoCicloCurso> alumnosCicloCursos = alumnoCicloCursoDAO.allByAlumnoCicloActivosOrAutorizacionRegistro(alumnoCiclo, autorizacionRegistro);
            alumnoCiclo.setAlumnoCicloCurso(alumnosCicloCursos);
        }
        return alumnoCiclo;
    }

    @Override
    public List<AlumnoCiclo> allAlumnoCicloByAlumno(Alumno alumno, Tramite tramite) {
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumno(alumno);
        logger.debug("Cantidad de alumno ciclos {}", alumnoCiclos.size());

        AutorizacionRegistro autorizacionRegistro = autorizacionRegistroDAO.findByTramite(tramite);

        List<AlumnoCiclo> alumnoCiclosReturn = new ArrayList<>();

        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            List<AlumnoCicloCurso> alumnosCicloCursos = alumnoCicloCursoDAO.allByAlumnoCicloActivosAndAutorizacionRegistro(alumnoCiclo, autorizacionRegistro);
            if (!alumnosCicloCursos.isEmpty()) {
                alumnoCiclo.setAlumnoCicloCurso(alumnosCicloCursos);
                AlumnoCiclo alumnoCicloClone = (AlumnoCiclo) alumnoCiclo.clone();
                alumnoCicloClone.setAlumnoCicloCurso(alumnosCicloCursos);
                alumnoCiclosReturn.add(alumnoCiclo);
            }
        }
        return alumnoCiclosReturn;
    }

    @Override
    @Transactional
    public void saveAlumnoCicloFromRevision(AlumnoCiclo alumnoCiclo, Long tramiteId, DataSessionPivot ds) {
        today = new DateTime();
        Tramite tramite = tramiteDAO.find(tramiteId);
        AutorizacionRegistro autorizacionRegistro = autorizacionRegistroDAO.findByTramite(tramite);
        List<AlumnoCicloCurso> alumnosCicloCursosOmBD = alumnoCicloCursoDAO.allByAlumnoCicloActivosOrAutorizacionRegistro(alumnoCiclo, autorizacionRegistro);

        if (autorizacionRegistro == null) {
            autorizacionRegistro = new AutorizacionRegistro();
            autorizacionRegistro.setMotivo("");
            autorizacionRegistro.setAlumno(alumnoCiclo.getAlumno());
            autorizacionRegistro.setEstado(EstadoEnum.CRE.name());
            autorizacionRegistro.setFechaRegistro(today.toDate());
            autorizacionRegistro.setUserRegistro(ds.getUsuario());
            autorizacionRegistro.setTramite(new Tramite(tramiteId));
            autorizacionRegistroDAO.save(autorizacionRegistro);
        }

        logger.debug("Alumno Ciclo {}", alumnoCiclo.getId());
        boolean noChanges = true;
        for (AlumnoCicloCurso alumnoCicloCursoForm : alumnoCiclo.getAlumnoCicloCurso()) {

            if (alumnoCicloCursoForm.getIsEstadoNotaModificada()) {
                continue;
            }

            if (alumnoCicloCursoForm.getId().compareTo(0L) > 0) {
                //old records
                AlumnoCicloCurso alumnoCicloCursoDB = alumnosCicloCursosOmBD.stream().filter(x -> x.getId().compareTo(alumnoCicloCursoForm.getId()) == 0).findFirst().orElse(null);
                if (alumnoCicloCursoDB == null) {
                    throw new PhobosException("Curso %s no encontrado", alumnoCicloCursoForm.getCurso().getNombre());
                }
                if (alumnoCicloCursoForm.getNota().compareTo(alumnoCicloCursoDB.getNota()) != 0
                        || alumnoCicloCursoForm.getCreditos().compareTo(alumnoCicloCursoDB.getCreditos()) != 0) {

                    if (alumnoCicloCursoForm.getIsEstadoMatriculado()) {
                        alumnoCicloCursoDB.setEstado(EstadoMatriculaEnum.NMOD);
                        alumnoCicloCursoDB.setRegistroActivo(BigDecimal.ZERO.intValue());
                        alumnoCicloCursoDB.setUserModificacion(ds.getUsuario());
                        alumnoCicloCursoDB.setAutorizacionRegistro(autorizacionRegistro);
                        if (!alumnoCicloCursoForm.getEstaActivo()) {
                            alumnoCicloCursoDB.setEstado(EstadoMatriculaEnum.MAT);
                            alumnoCicloCursoDB.setNota(alumnoCicloCursoForm.getNota());
                            alumnoCicloCursoDB.setCreditos(alumnoCicloCursoForm.getCreditos());
                        }
                        alumnoCicloCursoDAO.update(alumnoCicloCursoDB);
                        if (!alumnoCicloCursoForm.getEstaActivo()) {
                            noChanges = false;
                            continue;
                        }
                    }
                    AlumnoCicloCurso alumnoCursoNew = (AlumnoCicloCurso) alumnoCicloCursoDB.clone();
                    alumnoCursoNew.setId(null);
                    alumnoCursoNew.setNota(alumnoCicloCursoForm.getNota());
                    alumnoCursoNew.setCreditos(alumnoCicloCursoForm.getCreditos());
                    if (tramite.getTipoTramite().getEsTipoTramiteRei()) {
                        alumnoCursoNew.setOrigenData(OrigenDataSituacionAcademicaEnum.TA_REI);
                    } else if (tramite.getTipoTramite().getEsTramiteCorrHisto()) {
                        alumnoCursoNew.setOrigenData(OrigenDataSituacionAcademicaEnum.TA_CORR_HISTO);
                    } else if (tramite.getTipoTramite().getEsTramiteDocumento()) {
                        alumnoCursoNew.setOrigenData(OrigenDataSituacionAcademicaEnum.TR_DOCUMENTO);
                    }
                    alumnoCursoNew.setAlumnoCicloCursoOrigen(alumnoCicloCursoDB);
                    alumnoCursoNew.setFechaRegistro(today.toDate());
                    alumnoCursoNew.setUsuarioRegistro(ds.getUsuario());
                    alumnoCursoNew.setRegistroActivo(BigDecimal.ZERO.intValue());
                    alumnoCursoNew.setEstado(EstadoMatriculaEnum.MAT);
                    alumnoCursoNew.setAutorizacionRegistro(autorizacionRegistro);
                    Integer vecesEstudiadoCurso = alumnoCicloCursoDAO.countByCursoAlumnoAnterioresCiclo(alumnoCicloCursoForm.getCurso(), alumnoCiclo.getAlumno(), alumnoCiclo.getCicloAcademico()).intValue();
                    alumnoCursoNew.setVecesCursado(vecesEstudiadoCurso);
                    alumnoCicloCursoDAO.save(alumnoCursoNew);
                    noChanges = false;
                }
            } else {
                AlumnoCicloCurso alumnoCursoNew = new AlumnoCicloCurso();
                alumnoCursoNew.setAlumnoCiclo(alumnoCiclo);
                alumnoCursoNew.setAutorizacionRegistro(autorizacionRegistro);
                alumnoCursoNew.setCreditos(alumnoCicloCursoForm.getCreditos());
                alumnoCursoNew.setCurso(alumnoCicloCursoForm.getCurso());
                //  alumnoCursoNew.setEstaAprobado(Integer.MAX_VALUE);
                alumnoCursoNew.setEstado(EstadoMatriculaEnum.MAT);
                alumnoCursoNew.setFechaRegistro(today.toDate());
                alumnoCursoNew.setNota(alumnoCicloCursoForm.getNota());
                alumnoCursoNew.setOrigenData(OrigenDataSituacionAcademicaEnum.CARTA);
                alumnoCursoNew.setRegistroActivo(BigDecimal.ZERO.intValue());
                alumnoCursoNew.setUsuarioRegistro(ds.getUsuario());
                // alumnoCursoNew.setVecesCursado(Integer.BYTES);
                Integer aprobado = promedioService.evaluateEstaAprobado(new BigDecimal(alumnoCicloCursoForm.getNota()), alumnoCiclo.getAlumno());
                alumnoCursoNew.setEstaAprobado(aprobado);

                Integer vecesEstudiadoCurso = alumnoCicloCursoDAO.countByCursoAlumnoAnterioresCiclo(alumnoCicloCursoForm.getCurso(), alumnoCiclo.getAlumno(), alumnoCiclo.getCicloAcademico()).intValue();
                alumnoCursoNew.setVecesCursado(vecesEstudiadoCurso);

                alumnoCicloCursoDAO.save(alumnoCursoNew);
                noChanges = false;
            }
        }

        if (noChanges) {
            throw new PhobosException("Sin cambios en las notas, verifique.");
        }
    }

    @Override
    public AccionTramiteAcademico findAccionTramiteAcademico(AccionTramiteAcademico accionTramiteAcademico) {
        AccionTramiteAcademico accionTramiteAcademicoReturn = accionTramiteAcademicoDAO.find(accionTramiteAcademico.getId());
        return accionTramiteAcademicoReturn;
    }

    @Override
    public AccionTramiteDocumento findAccionTramiteDocumento(AccionTramiteDocumento accionTramiteDoc) {

        return accionTramiteDocumentoDAO.find(accionTramiteDoc.getId());
    }

    private List<Oficina> findOficina(List<Oficina> oficinas, DataSessionPivot ds) {
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());

        for (Oficina oficina : oficinasMain) {
            logger.debug("codigo oficina es {}", oficina.getCodigo());
            logger.debug("tipo oficina es {} ", oficina.getTipoOficina().getCodigo());

            if (oficina.getCodigoEnum() == OficinaEnum.OERA) {
                oficinas.addAll(reunionConsejoService.allOficinaFac());
                break;
            }
            if (oficina.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.FAC) {
                oficinas.add(oficina);
            }

        }
        return oficinas;
    }

    private FormDataBean convertStringToJSON(String situacionActual) {
        ObjectMapper mapper = new ObjectMapper();
        FormDataBean obj = new FormDataBean();
        try {
            obj = (FormDataBean) mapper.readValue(situacionActual, FormDataBean.class);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(TramitesAcademicosServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    @Override
    public List<Docente> allByNombre(String nombre) {
        return docenteDAO.allByName(nombre);
    }

    @Override
    public List<Tramite> allTramitesByFac(Facultad facultad, DataSessionPivot ds) {
        List<Tramite> tramites = tramiteDAO.allByFacultad(facultad);
        return tramites;
    }

    @Override
    public String allcursoDirigidoFac(Facultad fac, DataSessionPivot ds) {
        List<Tramite> tramites = allTramitesByFac(fac, ds);
        List<String> pdfs = new ArrayList();
        for (Tramite tramite : tramites) {
            pdfs.addAll(createInfoPDF(tramite, ds));
        }
        return pdfGenerator.concatPDFs(pdfs, "CursoDirigido", true);
    }

    @Override
    public String alllistCursoDirigidoFac(Facultad facultad, DataSessionPivot ds) {
        facultad = facultadDAO.find(facultad.getId());
        List<CursoDirigido> cursoDirigidos = cursoDirigidoDAO.allByfacultades(facultad);

        Context ctx = new Context();

        PdfContent pdfList = new PdfContent();
        pdfList.setContext(ctx);
        pdfList.setTipoPdfEnum(TipoPdfEnum.LIST_CURSOS_DIRIGIDOS);

        ctx.setVariable("fecha", TypesUtil.getStringDate(new DateTime().toDate(), " dd 'de' MMMM 'del' yyyy", "es"));
        ctx.setVariable("cursoDirigido", cursoDirigidos);
        ctx.setVariable("facultad", facultad.getNombre().toUpperCase(Locale.ROOT));

        List<String> pdfs = Arrays.asList(
                pdfGenerator.generateDocument(pdfList)
        );
        return pdfGenerator.concatPDFs(pdfs, "ListCursoDirigido", true);
    }

    private void vistoBuenoUR(AutorizacionRegistro autorizacionRegistro, Usuario usuario) {
        autorizacionRegistro.setFechaAutorizacion(new Date());
        autorizacionRegistro.setIdUserAutoriza(usuario.getId());
        autorizacionRegistroDAO.updateColumns(autorizacionRegistro, "fechaAutorizacion", "idUserAutoriza");
    }

    @Override
    @Transactional
    public void revertirCambioHistorial(AlumnoCiclo alumnoCiclo, DataSessionPivot ds) {
        AutorizacionRegistro autorizacionRegistro = new AutorizacionRegistro();
        Boolean nochange = false;
        for (AlumnoCicloCurso alumnoCicloCurso : alumnoCiclo.getAlumnoCicloCurso()) {
            if (alumnoCicloCurso.getAutorizacionRegistro() != null && alumnoCicloCurso.getAutorizacionRegistro().getEstadoEnum() == EstadoEnum.CRE) {
                autorizacionRegistro = alumnoCicloCurso.getAutorizacionRegistro();
                alumnoCicloCurso.setRegistroActivo(alumnoCicloCurso.getIsEstadoNotaModificada() || alumnoCicloCurso.getIsEstadoNotaEliminada() ? 1 : 0);
                alumnoCicloCurso.setEstado(alumnoCicloCurso.getIsEstadoNotaModificada() || alumnoCicloCurso.getIsEstadoNotaEliminada() ? EstadoMatriculaEnum.MAT : EstadoMatriculaEnum.RHZ);
                alumnoCicloCurso.setFechaModificacion(new Date());
                alumnoCicloCurso.setUserModificacion(ds.getUsuario());
                alumnoCicloCursoDAO.updateColumns(alumnoCicloCurso, "estado", "registroActivo", "fechaModificacion", "userModificacion");
                nochange = true;
            }
        }
        Assert.isTrue(nochange, "No hubo modificaciones.");
        autorizacionRegistro.setEstadoEnum(EstadoEnum.INA);
        autorizacionRegistro.setIdUserCierre(ds.getUsuario().getId());
        autorizacionRegistro.setFechaCierre(new Date());
        autorizacionRegistroDAO.updateColumns(autorizacionRegistro, "estado", "idUserCierre", "fechaCierre");
    }

    @Override
    public void deleteCicloCurso(AlumnoCicloCurso alumnoCicloCurso, Long idTramite, DataSessionPivot ds) {
        Tramite tramite = tramiteDAO.find(idTramite);
        AutorizacionRegistro autorizacionRegistro = crearAutorizacionRegistro(tramite.getAlumno(), tramite, ds);

        alumnoCicloCurso.setAutorizacionRegistro(autorizacionRegistro);
        alumnoCicloCurso.setRegistroActivo(0);
        alumnoCicloCurso.setEstado(EstadoMatriculaEnum.NELI);
        alumnoCicloCurso.setFechaModificacion(new Date());
        alumnoCicloCurso.setUserModificacion(ds.getUsuario());
        alumnoCicloCursoDAO.updateColumns(alumnoCicloCurso, "registroActivo", "estado", "fechaModificacion", "userModificacion", "autorizacionRegistro");

    }

    @Override
    public TipoTramite findTipoTramite(Long id) {
        return tramiteDAO.find(id).getTipoTramite();
    }

}

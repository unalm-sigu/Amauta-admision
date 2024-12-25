package pe.edu.lamolina.amauta.controller.academico.tramitesacademicos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import pe.edu.lamolina.amauta.zelper.bean.FormDataBean;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
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
import pe.edu.lamolina.model.tramite.FlujoTramiteDocumento;
import pe.edu.lamolina.model.tramite.FormularioEstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteCorreccionHistorial;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.general.DiaDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.horario.HoraDAO;
import pe.edu.lamolina.amauta.dao.tramite.AccionTramiteAcademicoDAO;
import pe.edu.lamolina.amauta.dao.tramite.AutorizacionRegistroDAO;
import pe.edu.lamolina.amauta.dao.tramite.CursoDirigidoDAO;
import pe.edu.lamolina.amauta.dao.tramite.FlujoTramiteAcademicoDAO;
import pe.edu.lamolina.amauta.dao.tramite.FormularioEstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.amauta.dao.tramite.ReunionConsejoDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteReunionConsejoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.controller.academico.infoacademico.InfoAcademicoService;
import pe.edu.lamolina.amauta.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.amauta.controller.academico.reunionconsejo.ReunionConsejoService;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.tramite.AccionTramiteDocumentoDAO;
import pe.edu.lamolina.amauta.dao.tramite.FlujoTramiteDocumentoDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteBachillerDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteCorreccionHistorialDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDocumentoAcademicoDAO;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.tramite.TramiteBachiller;
import pe.edu.lamolina.amauta.controller.general.oficina.util.OficinaService;
import pe.edu.lamolina.amauta.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.amauta.controller.test.VisorCalculoNotas;
import static pe.edu.lamolina.amauta.controller.test.VisorCalculoNotas.TOKEN_CURRICULA;
import static pe.edu.lamolina.amauta.controller.test.VisorCalculoNotas.TOKEN_PROMEDIOS;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import static pe.edu.lamolina.model.enums.oficina.OficinaEnum.OERA;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class TramitesAcademicosServiceImp implements TramitesAcademicosService {

    private final AccionTramiteAcademicoDAO accionTramiteAcademicoDAO;
    private final AccionTramiteDocumentoDAO accionTramiteDocumentoDAO;
    private final AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    private final AlumnoCicloDAO alumnoCicloDAO;
    private final AlumnoConsejeroDAO alumnoConsejeroDAO;
    private final AlumnoDAO alumnoDAO;
    private final AutorizacionRegistroDAO autorizacionRegistroDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final CursoDAO cursoDAO;
    private final CursoDirigidoDAO cursoDirigidoDAO;
    private final DiaDAO diaDAO;
    private final DocenteDAO docenteDAO;
    private final FlujoTramiteAcademicoDAO flujoTramiteAcademicoDAO;
    private final FlujoTramiteDocumentoDAO flujoTramiteDocumentoDAO;
    private final FormularioEstadoTramiteDAO formularioEstadoTramiteDAO;
    private final HoraDAO horaDAO;
    private final MatriculaResumenDAO matriculaResumenDAO;
    private final MatriculaSeccionDAO matriculaSeccionDAO;
    private final OficinaDAO oficinaDAO;
    private final ReincorporacionDAO reincorporacionDAO;
    private final ReunionConsejoDAO reunionConsejoDAO;
    private final TipoCursoCurriculaDAO tipoCursoCurriculaDAO;
    private final TramiteBachillerDAO tramiteBachillerDAO;
    private final TramiteCorreccionHistorialDAO correccionHistorialDAO;
    private final TramiteDAO tramiteDAO;
    private final TramiteDocumentoAcademicoDAO tramiteDocumentoAcademicoDAO;
    private final TramiteReunionConsejoDAO tramiteReunionConsejoDAO;

    private final InfoAcademicoService infoAcademicoService;
    private final OficinaService oficinaService;
    private final PromedioService promedioService;
    private final ReunionConsejoService reunionConsejoService;
    private final VisorCalculoNotas visorCalculoNotas;
    private final MatriculableService matriculableService;

    private final String CODIGO_REGISTRO = "UR";

    @Override
    public List<Tramite> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds) {
        List<Tramite> tramites = tramiteDAO.allByFilter(filter);
        List<AccionTramiteAcademico> accionesTramitesAcademicos = accionTramiteAcademicoDAO.all();
        List<FormularioEstadoTramite> formulariosEstadoTramite = formularioEstadoTramiteDAO.all();
        List<Reincorporacion> reincorporacions = reincorporacionDAO.allByTramite(tramites);
        List<CursoDirigido> cursosDirigidos = cursoDirigidoDAO.allByTramites(tramites);
        List<TramiteBachiller> bachillers = tramiteBachillerDAO.allByTramites(tramites);
        List<Oficina> oficinas = new ArrayList();
        for (Tramite tramite : tramites) {

            List<Reincorporacion> reincorporacionesTramite = reincorporacions.stream().filter(x -> Objects.equals(x.getTramite().getId(), tramite.getId())).collect(Collectors.toList());
            List<CursoDirigido> cursosDirigidosTramite = cursosDirigidos.stream().filter(x -> Objects.equals(x.getTramite().getId(), tramite.getId())).collect(Collectors.toList());
            List<TramiteBachiller> bachillersTramite = bachillers.stream().filter(x -> Objects.equals(x.getTramite().getId(), tramite.getId())).collect(Collectors.toList());
            tramite.setReincorporaciones(reincorporacionesTramite);
            tramite.setCursoDirigido(cursosDirigidosTramite);
            tramite.setTramiteBachiller(bachillersTramite);;

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
                    log.debug("Setting el estado {}", cd.getEstado().getNombre());
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
        if (tramite.getTipoTramite().getEsReincorporacionPregrado()) {
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
        }
    }

    @Override
    public Tramite findTramite(Long tramiteId) {
        Tramite tramite = tramiteDAO.findById(new Tramite(tramiteId));
        if (tramite.getTipoTramite().getEsReincorporacionPregrado()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
            if (!reincorporaciones.isEmpty()) {
                tramite.setEstadoTramite(reincorporaciones.get(0).getEstadoTramite());
            }
        } else if (tramite.getTipoTramite().getEsTramiteConstancia()) {
            TramiteDocumentoAcademico documentoAcademico = tramiteDocumentoAcademicoDAO.findTramite(tramite);
            if (documentoAcademico != null) {
                tramite.setEstadoTramite(documentoAcademico.getEstadoTramite());
                tramite.setTipoDocumentoAcademico(documentoAcademico.getTipoDocumentoAcademico());
            }
        } else if (tramite.getTipoTramite().getEsCorreccionHistorial()) {
            TramiteCorreccionHistorial tramiteCorrecionHisto = correccionHistorialDAO.findTramite(tramite);
            if (tramiteCorrecionHisto != null) {
                tramite.setEstadoTramite(tramiteCorrecionHisto.getEstadoTramite());
            }
        } else {
            CursoDirigido cd = cursoDirigidoDAO.findByTramite(tramite);
            if (cd != null) {
                log.debug("Setting el estado {}", cd.getEstado().getNombre());
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
        DateTime today = new DateTime();

        accionTramiteAcademico = accionTramiteAcademicoDAO.find(accionTramiteAcademico.getId());
        this.procesarTramite(tramite, accionTramiteAcademico, null, ds);
    }

    @Override
    @Transactional
    public void procesarTramite(Tramite tramiteForm, AccionTramiteAcademico accionTramiteAcademico, AccionTramiteDocumento accionTramiteDocumento, DataSessionPivot ds) {
        DateTime today = new DateTime();

        Tramite tramite = this.findTramite(tramiteForm.getId());

        AutorizacionRegistro autorizacionRegistro = autorizacionRegistroDAO.findByTramite(tramite);

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

        if (accionTramiteAcademico != null && accionTramiteAcademico.getEsSolicitarMotivo()) {
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
//                infoAcademicoService.calcularPromedio(tramite.getAlumno(), ds);
                String token = RandomStringUtils.randomAlphanumeric(43);
                String tokenProm = token + TOKEN_PROMEDIOS;
                String tokenCurri = token + TOKEN_CURRICULA;

                visorCalculoNotas.createToken(tokenProm, Arrays.asList(tramite.getAlumno()));
                visorCalculoNotas.createToken(tokenCurri, Arrays.asList(tramite.getAlumno()));

                matriculableService.calcularPromedios(token, ds);
                matriculableService.revisarCurriculaAlumnos(ds, token);
            }
        }
        if (accionTramiteAcademico != null ? accionTramiteAcademico.getEstadoTramiteFinal().getEsControlCalidad() : accionTramiteDocumento.getEstadoTramiteFinal().getEsControlCalidad()) {
            if (autorizacionRegistro == null) {
                if (accionTramiteDocumento != null) {

                    List<AccionTramiteDocumento> accionTramiteDocumentos = accionTramiteDocumentoDAO.allByTipoTramiteAndEstadoTramiteInicial(accionTramiteDocumento.getTipoDocumentoAcademico(), accionTramiteDocumento.getEstadoTramite());
                    accionTramiteDocumento = accionTramiteDocumentos.stream().filter(x -> x.getRespuesta().equals("SALTO")).findAny().orElse(null);
                }
            }
        }
        if (tramite.getTipoTramite().getEsCursoDirigido()) {
            CursoDirigido cursoDirigido = cursoDirigidoDAO.findByTramite(tramite);
            Oficina oficinaDestino = oficinaDAO.findByTipoOficinaFacultad(FAC, cursoDirigido.getFacultad());
            accionTramiteAcademico.setOficinaDestino(oficinaDestino);
            accionTramiteAcademico.setOficinaOrigen(oficinaDestino);

        }
        this.saveFlujoTramite(tramite, accionTramiteAcademico, accionTramiteDocumento, ds.getUsuario(), today);

    }

    private AutorizacionRegistro crearAutorizacionRegistro(Alumno alumno, Tramite tramite, DataSessionPivot ds) {
        DateTime today = new DateTime();

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
        TramiteDocumentoAcademico tramiteDocumentoAcademico = tramiteDocumentoAcademicoDAO.findTramite(tramite);
        Alumno alumnoTramite = alumnoDAO.find(tramite.getAlumno());

        Map oficinas = new HashMap();
        if (accionTramiteAcademico != null) {
            FlujoTramiteAcademico flujoTramiteAcademico = new FlujoTramiteAcademico();
            oficinas = oficinaDAO.findOficinaOrigenDestinoByEstadoTramiteAcad(accionTramiteAcademico, alumnoTramite);
            flujoTramiteAcademico.setEstadoTramite(accionTramiteAcademico.getEstadoTramiteFinal());
            flujoTramiteAcademico.setOrden(accionTramiteAcademico.getOrdenOpcion());
            if (accionTramiteAcademico.getEsSolicitarMotivo()) {
                flujoTramiteAcademico.setMotivo(tramite.getObservacion());
            }
            flujoTramiteAcademico.setOficinaOrigen((Oficina) oficinas.get("oficinaOrigen"));
            flujoTramiteAcademico.setOficinaDestino((Oficina) oficinas.get("oficinaDestino"));
            flujoTramiteAcademico.setFechaRegistro(today.toDate());
            flujoTramiteAcademico.setTramiteAcademico(tramite);
            flujoTramiteAcademico.setUserRegistro(usuario);
            flujoTramiteAcademicoDAO.save(flujoTramiteAcademico);
        } else {
            FlujoTramiteDocumento flujoTramiteDocumento = new FlujoTramiteDocumento();
            oficinas = oficinaDAO.findOficinaOrigenDestinoByEstadoTramiteDoc(accionTramiteDocumento, alumnoTramite);
            flujoTramiteDocumento.setEstadoTramite(accionTramiteDocumento.getEstadoTramiteFinal());
            flujoTramiteDocumento.setOrden(accionTramiteDocumento.getOrden());
            if (accionTramiteDocumento.getSolicitaMotivo()) {
                flujoTramiteDocumento.setMotivo(tramite.getObservacion());
            }
            flujoTramiteDocumento.setOficinaOrigen((Oficina) oficinas.get("oficinaOrigen"));
            flujoTramiteDocumento.setOficinaDestino((Oficina) oficinas.get("oficinaDestino"));
            flujoTramiteDocumento.setFechaRegistro(today.toDate());
            flujoTramiteDocumento.setTramiteDocumentoAcademico(tramiteDocumentoAcademico);
            flujoTramiteDocumento.setUserRegistro(usuario);
            flujoTramiteDocumentoDAO.save(flujoTramiteDocumento);
        }

        if (tramite.getTipoTramite().getEsReincorporacionPregrado()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
            Reincorporacion reincorporacion = reincorporaciones.get(0);
            Reincorporacion reincorporacionUpd = new Reincorporacion();
            reincorporacionUpd.setId(reincorporacion.getId());
            reincorporacionUpd.setEstadoTramite(accionTramiteAcademico.getEstadoTramiteFinal());
            reincorporacionDAO.updateEstado(reincorporacionUpd);
        } else if (tramite.getTipoTramite().getEsCursoDirigido()) {
            CursoDirigido cd = cursoDirigidoDAO.findByTramite(tramite);
            cd.setEstado(accionTramiteAcademico.getEstadoTramiteFinal());
            cursoDirigidoDAO.update(cd);
        } else if (tramite.getTipoTramite().getEsTramiteConstancia()) {

            tramiteDocumentoAcademico.setEstadoTramite(accionTramiteDocumento.getEstadoTramiteFinal());
            tramiteDocumentoAcademicoDAO.updateColumns(tramiteDocumentoAcademico, "estadoTramite");
        } else if (tramite.getTipoTramite().getEsCorreccionHistorial()) {
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
                alumnoCicloCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
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
    }

    @Override
    public void cursoDirigidoReporte(Tramite tramite, Model model, DataSessionPivot ds) {

        tramite = tramiteDAO.find(tramite.getId());
        CursoDirigido cursoDirigido = cursoDirigidoDAO.findByTramite(tramite);

        FormDataBean data = convertStringToJSON(cursoDirigido.getSituacionActual());

        Alumno alumno = tramite.getAlumno();
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        List<MatriculaSeccion> matriculados = matriculaSeccionDAO.allMatriculadosByAlumnoCiclo(alumno, cicloAcademico);

        Map<GrupoSeccion, List<Seccion>> gpoSecciones = matriculados.stream().map(MatriculaSeccion::getSeccion).collect(Collectors.groupingBy(x -> x.getGrupoSeccion()));

        Curso curso = cursoDirigido.getCurso();

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

        List< AlumnoCiclo> alumnosCiclos = alumnoCicloCursos.stream().map(x -> x.getAlumnoCiclo()).collect(Collectors.toList());

        String codigo = "10000000";
        String codigoFin = "1";
        CicloAcademico cicloInicio = new CicloAcademico();
        AlumnoCiclo alumnoCiclo = null;
        for (AlumnoCiclo alumnoCic : alumnosCiclos) {
            Integer cod = Integer.parseInt(codigo);
            Integer codFin = Integer.parseInt(codigoFin);
            Integer coda = Integer.parseInt(alumnoCic.getCicloAcademico().getCodigo());
            if (coda < cod) {
                cicloInicio = alumnoCic.getCicloAcademico();
                codigo = alumnoCic.getCicloAcademico().getCodigo();
            }
            if (coda > codFin) {
                codigoFin = alumnoCic.getCicloAcademico().getCodigo();
                alumnoCiclo = alumnoCic;
            }
        }
        int creditosConvalidados = 0;

        List<AlumnoCicloCurso> listAlumnoCicloCurso = alumnoCicloCursoDAO.allByAlumnoOrderByTipoCurso(alumno);

        for (AlumnoCicloCurso alumnoCicloCurso : listAlumnoCicloCurso) {
            if (alumnoCicloCurso.getNota().equals("TE")) {
                creditosConvalidados = creditosConvalidados + alumnoCicloCurso.getCreditos();
            }
        }

        alumno.setCreditosConvalidadosTransient(creditosConvalidados);
        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        if (alumnoConsejero != null) {
            alumno.setConsejero(alumnoConsejero.getConsejero());
        }
        Oficina oficina = oficinaDAO.findByCode(CODIGO_REGISTRO);

        model.addAttribute("oficina", oficina);
        model.addAttribute("alumno", alumno);
        model.addAttribute("ciclo", cicloAcademico);
        model.addAttribute("curso", curso);
        model.addAttribute("historial", historialSorted);
        model.addAttribute("alumnoCiclo", alumnoCiclo);
        model.addAttribute("matriculados", matriculados);
        model.addAttribute("gpoSecciones", gpoSecciones);
        model.addAttribute("cursoDirigido", cursoDirigido);
        model.addAttribute("matriculaResumen", matriculaResumen);

        model.addAttribute("situacionActual", data);
        model.addAttribute("fecha", TypesUtil.getStringDate(new DateTime().toDate(), " dd 'de' MMMM 'del' yyyy", "es"));
        model.addAttribute("alumnoCicloCurso", listAlumnoCicloCurso);

        List<Dia> dias = diaDAO.allDia();
        List<HorarioSeccion> hss = infoAcademicoService.allSeccionHorarioAlumnoByAlumnoCicloACademico(alumno, cicloAcademico);
        List<Hora> horas = findLimiteHoras(hss);
        model.addAttribute("horas", horas);
        model.addAttribute("dias", dias);
        model.addAttribute("datosHorario", findHorario(alumno, cicloAcademico, horas, dias));

        model.addAttribute("nombrePdf", "Información");
        model.addAttribute("templatePdf", "detalleCursoDirigido,historialAcademicoCurdir,cursosMatriculados,horario");

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
        log.debug("Cantidad de alumno ciclos {}", alumnoCiclos.size());

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
        DateTime today = new DateTime();
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

        log.debug("Alumno Ciclo {}", alumnoCiclo.getId());
        Alumno alumno = alumnoDAO.find(alumnoCiclo.getAlumno());
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
                        alumnoCicloCursoDB.setEstadoEnum(EstadoMatriculaEnum.NMOD);
                        alumnoCicloCursoDB.setRegistroActivo(BigDecimal.ZERO.intValue());
                        alumnoCicloCursoDB.setUserModificacion(ds.getUsuario());
                        alumnoCicloCursoDB.setAutorizacionRegistro(autorizacionRegistro);
                        if (!alumnoCicloCursoForm.getEstaActivo()) {
                            alumnoCicloCursoDB.setEstadoEnum(EstadoMatriculaEnum.MAT);
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
                    alumnoCursoNew.setEstaAprobado(promedioService.evaluateEstaAprobado(alumnoCursoNew, alumno));
                    alumnoCursoNew.setCreditos(alumnoCicloCursoForm.getCreditos());
                    if (tramite.getTipoTramite().getEsReincorporacionPregrado()) {
                        alumnoCursoNew.setOrigenData(OrigenDataSituacionAcademicaEnum.TA_REI);
                    } else if (tramite.getTipoTramite().getEsCorreccionHistorial()) {
                        alumnoCursoNew.setOrigenData(OrigenDataSituacionAcademicaEnum.TA_CORR_HISTO);
                    } else if (tramite.getTipoTramite().getEsTramiteConstancia()) {
                        alumnoCursoNew.setOrigenData(OrigenDataSituacionAcademicaEnum.TR_DOCUMENTO);
                    }
                    alumnoCursoNew.setAlumnoCicloCursoOrigen(alumnoCicloCursoDB);
                    alumnoCursoNew.setFechaRegistro(today.toDate());
                    alumnoCursoNew.setUsuarioRegistro(ds.getUsuario());
                    alumnoCursoNew.setRegistroActivo(BigDecimal.ZERO.intValue());
                    alumnoCursoNew.setEstadoEnum(EstadoMatriculaEnum.MAT);
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
                alumnoCursoNew.setEstadoEnum(EstadoMatriculaEnum.MAT);
                alumnoCursoNew.setFechaRegistro(today.toDate());
                alumnoCursoNew.setNota(alumnoCicloCursoForm.getNota());
                alumnoCursoNew.setOrigenData(OrigenDataSituacionAcademicaEnum.CARTA);
                alumnoCursoNew.setRegistroActivo(BigDecimal.ZERO.intValue());
                alumnoCursoNew.setUsuarioRegistro(ds.getUsuario());
                // alumnoCursoNew.setVecesCursado(Integer.BYTES);
                Integer aprobado = promedioService.evaluateEstaAprobado(alumnoCicloCursoForm, alumnoCiclo.getAlumno());
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
            log.debug("codigo oficina es {}", oficina.getCodigo());
            log.debug("tipo oficina es {} ", oficina.getTipoOficina().getCodigo());

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

        return tramiteDAO.allByFacultad(facultad, ds.getCicloAcademico());

    }

    @Override
    public List<Context> allcursoDirigidoFac(Facultad facultad, Model model, DataSessionPivot ds) {

        List<Tramite> tramites = allTramitesByFac(facultad, ds);
        List<Context> multipleContext = new ArrayList();

        for (Tramite tramite : tramites) {
            cursoDirigidoReporte(tramite, model, ds);
        }

        return multipleContext;

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
                alumnoCicloCurso.setEstadoEnum(alumnoCicloCurso.getIsEstadoNotaModificada() || alumnoCicloCurso.getIsEstadoNotaEliminada() ? EstadoMatriculaEnum.MAT : EstadoMatriculaEnum.RHZ);
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
    @Transactional
    public void deleteCicloCurso(AlumnoCicloCurso alumnoCurso, Long idTramite, DataSessionPivot ds) {
        Tramite tramite = tramiteDAO.find(idTramite);
        AutorizacionRegistro autorizacionRegistro = crearAutorizacionRegistro(tramite.getAlumno(), tramite, ds);

        alumnoCurso.setAutorizacionRegistro(autorizacionRegistro);
        alumnoCurso.setRegistroActivo(0);
        alumnoCurso.setEstadoEnum(EstadoMatriculaEnum.NELI);
        alumnoCurso.setFechaModificacion(new Date());
        alumnoCurso.setUserModificacion(ds.getUsuario());
        alumnoCicloCursoDAO.updateColumns(alumnoCurso, "registroActivo", "estado", "fechaModificacion", "userModificacion", "autorizacionRegistro");

        AlumnoCicloCurso alumnoCursoBD = alumnoCicloCursoDAO.find(alumnoCurso);
        AlumnoCiclo alumnoCiclo = alumnoCursoBD.getAlumnoCiclo();

        List<AlumnoCicloCurso> alumnoCursosAll = alumnoCicloCursoDAO.allActivoByAlumnoCiclo(alumnoCiclo);
        if (alumnoCursosAll.isEmpty()) {
            if (alumnoCursoBD.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                alumnoCursoBD.setEstadoEnum(EstadoMatriculaEnum.ANCI);
                alumnoCursoBD.setUserModificacion(ds.getUsuario());
                alumnoCursoBD.setFechaModificacion(new Date());
                alumnoCicloDAO.update(alumnoCiclo);
            }
        }
    }

    @Override
    public TipoTramite findTipoTramite(Long id) {
        return tramiteDAO.find(id).getTipoTramite();
    }
}

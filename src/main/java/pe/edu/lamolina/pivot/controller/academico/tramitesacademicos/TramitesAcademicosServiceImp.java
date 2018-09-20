package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.TramiteReunionConsejo;
import pe.edu.lamolina.model.tramite.EstadoTramiteAcademico;
import pe.edu.lamolina.model.tramite.FlujoTramiteAcademico;
import pe.edu.lamolina.model.tramite.FormularioEstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.ReunionConsejo;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.controller.academico.infoacademico.infoAcademicoService;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.flujo.FlujoTramiteAcademicoService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.tramite.AccionTramiteAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.CursoDirigidoDAO;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteAcademicoDAO;
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

@Service
@Transactional(readOnly = true)
public class TramitesAcademicosServiceImp implements TramitesAcademicosService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @Autowired
    AccionTramiteAcademicoDAO accionTramiteAcademicoDAO;

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
    infoAcademicoService infoAcademicoService;

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    DiaDAO diaDAO;

    @Override
    public List<Tramite> allTramitesByFilter(DynatableFilter filter) {
        List<Tramite> tramites = tramiteDAO.allByFilter(filter);
        List<AccionTramiteAcademico> accionesTramitesAcademicos = accionTramiteAcademicoDAO.all();
        List<FormularioEstadoTramite> formulariosEstadoTramite = formularioEstadoTramiteDAO.all();

        for (Tramite tramite : tramites) {
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

            List<AccionTramiteAcademico> accionesTramitesAcademicosBy = accionesTramitesAcademicos.stream().filter(
                    req -> req.getTipoTramite().equals(tramite.getTipoTramite())
                    && req.getEstadoTramiteInicio().equals(tramite.getEstadoTramite())
            ).collect(Collectors.toList());

            tramite.setAccionesTramitesAcademico(accionesTramitesAcademicosBy);

            FormularioEstadoTramite formularioEstadoTramite = formulariosEstadoTramite.stream().filter(x
                    -> x.getEstadoTramite().equals(tramite.getEstadoTramite())
                    && x.getTipoTramite().equals(tramite.getTipoTramite())).findFirst().orElse(null);

            tramite.setFormularioEstadoTramite(formularioEstadoTramite);

            for (Tramite tramite1 : tramites) {
                logger.debug("Tiene {} tramites", tramite.getAccionesTramitesAcademico().size());
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

        //     Map oficinas = oficinaDAO.findOficinaOrigenDestinoByEstadoTramiteAcad(estadoTramiteAcademico, alumnoTramite);
        Map oficinas = new HashMap();
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

    @Override
    public Tramite findTramite(Long tramiteId) {
        Tramite tramite = tramiteDAO.findById(new Tramite(tramiteId));
        if (tramite.getTipoTramite().getEsTipoTramiteRei()) {
            List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
            if (!reincorporaciones.isEmpty()) {
                tramite.setEstadoTramite(reincorporaciones.get(0).getEstadoTramite());
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
        tramite.setFormularioEstadoTramite(formularioEstadoTramiteDAO.findByTipoTramiteAndEstadoTramite(tramite.getTipoTramite(), tramite.getEstadoTramite()));
        tramite.setTramiteReunionConsejo(tramiteReunionConsejo);
        return tramite;
    }

    @Override
    @Transactional
    public void procesarTramite(Tramite tramiteForm, AccionTramiteAcademico accionTramiteAcademico, DataSessionPivot ds) {
        DateTime today = new DateTime();

        Tramite tramite = this.findTramite(tramiteForm.getId());
        accionTramiteAcademico = accionTramiteAcademicoDAO.find(accionTramiteAcademico.getId());
        logger.debug("EstadoTramite Inicio {}, Estado Fin {}", ObjectUtil.getParentTree(accionTramiteAcademico, "estadoTramiteInicio.nombre"), ObjectUtil.getParentTree(accionTramiteAcademico, "estadoTramiteFinal.nombre"));

        Tramite tramiteUpd = new Tramite();
        tramiteUpd.setId(tramite.getId());
        tramiteUpd.setEstadoEnum(TramiteEstadoEnum.PROC);
        if (accionTramiteAcademico.getEsFinalBool()) {
            if (accionTramiteAcademico.getEsSatisfactorio()) {
                tramiteUpd.setEstadoEnum(TramiteEstadoEnum.ACEP);
            } else {
                tramiteUpd.setEstadoEnum(TramiteEstadoEnum.RCHZ);
            }
        }
        tramiteUpd.setUserModificacion(ds.getUsuario());
        tramiteUpd.setFechaModificacion(today.toDate());
        tramiteDAO.updateEstado(tramiteUpd);

        if (accionTramiteAcademico.getEsSolicitarMotivo()) {
            tramiteUpd.setObservacion(tramiteForm.getObservacion());
            tramiteDAO.updateObservacion(tramite);
        }
        if (accionTramiteAcademico.getEstadoTramiteFinal().getEsAgendadoConsejoFacultad()) {
            this.agendarSolicitud(tramite, tramiteForm.getTramiteReunionConsejo().getReunionConsejo(), today, ds.getUsuario());
        }

        this.saveFlujoTramite(tramite, accionTramiteAcademico, ds.getUsuario(), today);
    }

    @Transactional(readOnly = false)
    public void saveFlujoTramite(Tramite tramite, AccionTramiteAcademico accionTramiteAcademico, Usuario usuario, DateTime today) {

        Alumno alumnoTramite = alumnoDAO.find(tramite.getAlumno());
        Map oficinas = oficinaDAO.findOficinaOrigenDestinoByEstadoTramiteAcad(accionTramiteAcademico, alumnoTramite);

        FlujoTramiteAcademico flujoTramiteAcademico = new FlujoTramiteAcademico();
        flujoTramiteAcademico.setEstadoTramite(accionTramiteAcademico.getEstadoTramiteFinal());
        flujoTramiteAcademico.setFechaRegistro(today.toDate());
        flujoTramiteAcademico.setOficinaOrigen((Oficina) oficinas.get("oficinaOrigen"));
        flujoTramiteAcademico.setOficinaDestino((Oficina) oficinas.get("oficinaDestino"));
        flujoTramiteAcademico.setTramiteAcademico(tramite);
        flujoTramiteAcademico.setUserRegistro(usuario);
        flujoTramiteAcademico.setOrden(accionTramiteAcademico.getOrdenOpcion());
        if (accionTramiteAcademico.getEsSolicitarMotivo()) {
            flujoTramiteAcademico.setMotivo(tramite.getObservacion());
        }
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
        }
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
        tramite = tramiteDAO.find(tramite.getId());
        CursoDirigido cursoDirigido = cursoDirigidoDAO.findByTramite(tramite);

        Alumno alumno = tramite.getAlumno();
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        List<MatriculaSeccion> matriculados = matriculaSeccionDAO.allMatriculadosByAlumnoCiclo(alumno, cicloAcademico);

        Map<GrupoSeccion, List<Seccion>> gpoSecciones = matriculados.stream().map(MatriculaSeccion::getSeccion).collect(Collectors.groupingBy(x -> x.getGrupoSeccion()));

        Curso curso = cursoDirigido.getCurso();
        PlanCurricular planCurricular = alumno.getPlanCurricular();

        Map<Integer, List<CursoCurricula>> cursosPlanCurricular = planCurricular.getCursoCurricula().stream().filter(cc -> cc.getNumeroCiclo() != null).collect(Collectors.groupingBy(cc -> cc.getNumeroCiclo()));

        Map<Integer, List<AlumnoCursoCurricula>> avanceCurricular = alumnoCursoCurriculaDAO.allCiclosAlumno(alumno)
                .stream()
                .collect(Collectors.groupingBy(x -> x.getNumeroCiclo()));

        Map<CicloAcademico, List<AlumnoCicloCurso>> historial = alumnoCicloCursoDAO.allByAlumno(alumno)
                .stream()
                .collect(Collectors.groupingBy(acc -> acc.getAlumnoCiclo().getCicloAcademico()));

        Context ctx = new Context();

        SortedMap<CicloAcademico, List<AlumnoCicloCurso>> historialSorted = new TreeMap<>(Comparator.comparing(CicloAcademico::getCodigo).reversed());
        historialSorted.putAll(historial);

        Map<CicloAcademico, AlumnoCiclo> alumnoCiclo = historial.values()
                .stream()
                .flatMap(x -> x.stream())
                .collect(Collectors.toMap(x -> x.getAlumnoCiclo().getCicloAcademico(), x -> x.getAlumnoCiclo(), (a, b) -> b));

        ctx.setVariable("alumno", alumno);
        ctx.setVariable("ciclo", cicloAcademico);
        ctx.setVariable("curso", curso);
        ctx.setVariable("avanceCurricular", avanceCurricular);
        ctx.setVariable("historial", historialSorted);
        ctx.setVariable("planCurricular", planCurricular);
        ctx.setVariable("planCurricularCursos", cursosPlanCurricular);
        ctx.setVariable("alumnoCiclo", alumnoCiclo);
        ctx.setVariable("matriculados", matriculados);
        ctx.setVariable("gpoSecciones", gpoSecciones);
        ctx.setVariable("alumnoCicloCurso", alumnoCicloCursoDAO.allByAlumnoOrderByCurso(alumno));

        PdfContent pdfMatriculados = new PdfContent();
        pdfMatriculados.setContext(ctx);
        pdfMatriculados.setTipoPdfEnum(TipoPdfEnum.CURSOS_MATRICULADOS);

        PdfContent pdfHistorial = new PdfContent();
        pdfHistorial.setContext(ctx);
        pdfHistorial.setTipoPdfEnum(TipoPdfEnum.HISTORIAL_ACADEMICO);

        PdfContent pdfHistorialListado = new PdfContent();
        pdfHistorialListado.setContext(ctx);
        pdfHistorialListado.setTipoPdfEnum(TipoPdfEnum.HISTORIAL_ACADEMICO_LISTADO);

        PdfContent pdfPlanCurricular = new PdfContent();
        pdfPlanCurricular.setContext(ctx);
        pdfPlanCurricular.setTipoPdfEnum(TipoPdfEnum.PLAN_CURRICULAR);

        PdfContent pdfHorario = new PdfContent();
        pdfHorario.setContext(ctx);
        pdfHorario.setTipoPdfEnum(TipoPdfEnum.HORARIO);

        List<Dia> dias = diaDAO.allDia();
        List<HorarioSeccion> hss = infoAcademicoService.allSeccionHorarioAlumnoByAlumnoCicloACademico(alumno, cicloAcademico);
        List<Hora> horas = findLimiteHoras(hss);
        ctx.setVariable("horas", horas);
        ctx.setVariable("dias", dias);
        ctx.setVariable("datosHorario", findHorario(alumno, cicloAcademico, horas, dias));

        List<String> pdfs = Arrays.asList(
                pdfGenerator.generateDocument(pdfPlanCurricular),
                pdfGenerator.generateDocument(pdfHistorial),
                pdfGenerator.generateDocument(pdfHistorialListado),
                pdfGenerator.generateDocument(pdfMatriculados),
                pdfGenerator.generateDocument(pdfHorario)
        );

        return pdfGenerator.concatPDFs(pdfs, "CursoDirigido", true);
    }

    private List<Hora> findLimiteHoras(List<HorarioSeccion> clases) {
        Hora horaMin = null;
        Hora horaMax = null;

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

}

package pe.edu.lamolina.amauta.controller.tramite.bachiller;

import java.util.*;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.inscripcion.PostulanteDAO;
import pe.edu.lamolina.amauta.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.ObtencionGradoDAO;
import pe.edu.lamolina.amauta.dao.tramite.SerieDocumentoDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteBachillerDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.DEP;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum;
import pe.edu.lamolina.model.enums.TipoSolicitanteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

@Service
@Transactional(readOnly = true)
public class TramitesBachillerServiceImp implements TramitesBachillerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DateTime today = new DateTime();

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    TramiteBachillerDAO tramiteBachillerDAO;

    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;

    @Autowired
    SerieDocumentoService serieDocumentoService;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    EgresadoDAO egresadoDAO;

    @Autowired
    SerieDocumentoDAO serieDocumentoDAO;

    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;

    @Autowired
    ObtencionGradoDAO obtencionGradoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;

    @Autowired
    PostulanteDAO postulanteDAO;

    @Override
    public List<TramiteBachiller> allTramitesByFilter(DynatableFilter filter) {

        return tramiteBachillerDAO.allByDynatable(filter);

    }

    @Override
    public Context reporte(Long idTramite, DataSessionPivot ds) {

        Tramite tramite = this.findByTramite(idTramite);
        TramiteBachiller tramiteBachiller = tramiteBachillerDAO.findByTramite(tramite);

        Alumno alumno = alumnoDAO.find(tramite.getAlumno());
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        TipoCursoCurricula tipoCursoCurriculaGen = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.GEN);
        TipoCursoCurricula tipoCursoCurriculaCPRO = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.CPRO);
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);

        if (alumno.getPlanCurricular() == null) {
            throw new PhobosException("El plan de estudios no está especificado");
        }

        List<CursoCurricula> cursoCurriculas = cursoCurriculaDAO.allByPlanCurricularCAD(alumno.getPlanCurricular());
        Map<Long, CursoCurricula> mapCursoCurricula = TypesUtil.convertListToMap("curso.id", cursoCurriculas);

        List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumno(alumno);
        Map<Long, TipoCursoCurricula> mapAlumnoCursoCurricula = TypesUtil.convertListToMap("curso.id", "tipoCursoCurricula", alumnoCursoCurriculas);

        for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
            if (alumnoCicloCurso.getTipoCursoCurricula() != null && alumnoCicloCurso.getTipoCursoCurricula().getCodigoEnum() == DEP) {
                if (alumnoCicloCurso.getCreditos() > 0) {

                    alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurriculaGen);
                }
            }
            if (alumnoCicloCurso.getTipoCursoCurricula() == null) {
                TipoCursoCurricula tipoCursoCurricula = mapAlumnoCursoCurricula.get(alumnoCicloCurso.getCurso().getId());
                if (tipoCursoCurricula == null) {
                    alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurriculaCPRO);
                } else {
                    alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurricula);
                }
            }
        }

        validadCaducos(mapCursoCurricula, alumnoCicloCursos);

        Map<TipoCursoCurricula, List<AlumnoCicloCurso>> historial = alumnoCicloCursos
                .stream()
                .filter(x -> x.isAprobado() && x.getEsCaduco() == 0)
                .collect(Collectors.groupingBy(acc -> acc.getTipoCursoCurricula()));

        Context ctx = new Context();

        SortedMap<TipoCursoCurricula, List<AlumnoCicloCurso>> historialSorted = new TreeMap<>(Comparator.comparing(TipoCursoCurricula::getOrden));
        historialSorted.putAll(historial);

        List< AlumnoCiclo> alumnosCiclos = alumnoCicloCursos.stream().map(x -> x.getAlumnoCiclo()).collect(Collectors.toList());
        
        Map<Long,AlumnoCiclo> alumnosCiclosFiltrados=alumnosCiclos.stream()
                .collect(Collectors.toMap(x->x.getId(),y->y,(w,z)->w));

        long ciclosRegular = alumnosCiclosFiltrados.values().stream()
                .filter(ac -> ac.getEstadoEnum() == EstadoMatriculaEnum.MAT)
                .filter(ac -> ac.getCicloAcademico().getTipoEnum() == TipoCicloEnum.REG)
                .count();

        int creditosConvalidados = 0;

        List<AlumnoCicloCurso> listAlumnoCicloCurso = alumnoCicloCursoDAO.allByAlumnoOrderByTipoCurso(alumno);

        for (AlumnoCicloCurso alumnoCicloCurso : listAlumnoCicloCurso) {
            if (alumnoCicloCurso.getNota().equals("TE")) {
                creditosConvalidados = creditosConvalidados + alumnoCicloCurso.getCreditos();
            }
        }

        alumno.setCreditosConvalidadosTransient(creditosConvalidados);

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
        
        if(alumno.getCicloActivo()==null){
            throw new PhobosException("El alumno no tiene ciclo activo");
        }

        EventoCicloAcademico eventoActual = eventoCicloAcademicoDAO.findByCicloAndEvento(alumno.getCicloActivo(), EventoAcademicoEnum.FECHAS_BACH);
        if (eventoActual == null) {
            throw new PhobosException(String.format("No se ha configurado el evento fecha primera matricula y egreso para el ciclo %s", alumno.getCicloActivo().getDescripcion()));
        }
        EventoCicloAcademico eventoIngreso = eventoCicloAcademicoDAO.findByCicloAndEvento(cicloInicio, EventoAcademicoEnum.FECHAS_BACH);
        if (eventoIngreso == null) {
            throw new PhobosException(String.format("No se ha configurado el evento fecha primera matricula y egreso para el ciclo %s", cicloInicio.getDescripcion()));
        }

        Oficina oficinaColaborador = null;

        alumnoCiclo = alumnoCicloDAO.find(alumnoCiclo.getId());
        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findByAlumnoCiclo(alumno, ds.getCicloAcademico());
        if (alumnoConsejero != null) {
            alumno.setConsejero(alumnoConsejero.getConsejero());
        }

        if (alumno.getConsejero() == null || alumno.getConsejero().getColaborador() == null) {
            oficinaColaborador = oficinaDAO.findByCode("CT-" + alumno.getCarrera().getCodigo());
        }
        
        Postulante postulante=postulanteDAO.findByPersonaCicloAcademico(alumno.getPersona(),alumno.getCicloIngreso());

        ctx.setVariable("alumno", alumno);
        ctx.setVariable("oficinaColaborador", oficinaColaborador);
        ctx.setVariable("ciclo", cicloAcademico);
        ctx.setVariable("historial", historialSorted);
        ctx.setVariable("alumnoCiclo", alumnoCiclo);
        ctx.setVariable("bachiller", tramiteBachiller);
        ctx.setVariable("fechaPrimaMatricula", TypesUtil.getStringDate(eventoIngreso.getFechaInicio(), " dd'/'MM'/'yyyy", "es"));
        ctx.setVariable("fechaEgreso", TypesUtil.getStringDate(eventoActual.getFechaFin(), " dd'/'MM'/'yyyy", "es"));
        ctx.setVariable("planCurricular", alumno.getPlanCurricular() != null ? alumno.getPlanCurricular().getCicloInicioVigencia().getDescripcion() : "");
        ctx.setVariable("ciclosRegularesEstudiados", ciclosRegular);
        ctx.setVariable("modalidadIngreso",postulante!=null? postulante.getModalidadIngreso().getNombre():null);

        ctx.setVariable("fecha", TypesUtil.getStringDate(new DateTime().toDate(), " dd 'de' MMMM 'del' yyyy", "es"));

        ctx.setVariable("nombrePdf", "Informe Bachiller " + tramite.getAlumno().getPersona().getPaterno() + " " + tramite.getNumero());
        ctx.setVariable("templatePdf", "detalleBachiller,historialAcademicoBachiller");

        return ctx;
    }

    @Override
    @Transactional
    public void saveBachiller(TramiteBachiller tramiteBachillerForm, DataSessionPivot ds) {
        LocalDate today = new LocalDate();

        Alumno alumnoDB = alumnoDAO.find(tramiteBachillerForm.getAlumno());
        if (!alumnoDB.getModalidadEstudio().isOperativePRE()) {
            throw new PhobosException("El trámite es solo para alumnos de pre grado");
        }

        Integer creditosAprobados = Objects.nonNull(alumnoDB.getCreditosAprobados()) ? alumnoDB.getCreditosAprobados(): 0;
        Integer creditosConvalidados = Objects.nonNull(alumnoDB.getCreditosConvalidados()) ? alumnoDB.getCreditosConvalidados(): 0;
        Integer totalCreditos = creditosAprobados + creditosConvalidados;
        
        if (totalCreditos.intValue() < 200) {
            throw new PhobosException(String.format("Alumno %s no es egresado, cuenta con %s créditos", alumnoDB.getCodigo(), totalCreditos.intValue()));
        }

        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM_BACHI);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());

        Oficina oficina = oficinaDAO.findByCode(OficinaEnum.UR.name());

        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.BACHI.name());
        Tramite tramite = tramiteDAO.findByAlumnoTipoTramEstado(alumnoDB, tipoTramite);

        if (tramite != null) {
            throw new PhobosException(String.format(" Alumno %s ya cuenta con tramite bachiller en el ciclo %s", alumnoDB.getCodigo(), tramite.getCicloAcademico().getDescripcion2()));
        }

        tramite = new Tramite();
        tramite.setUserRegistro(ds.getUsuario());
        tramite.setCompania(ds.getCompania());
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setTipoSolicitante(TipoSolicitanteEnum.ALU.name());
        tramite.setPersona(alumnoDB.getPersona());
        tramite.setAlumno(alumnoDB);
        tramite.setTipoTramite(tipoTramite);
        tramite.setCicloAcademico(ds.getCicloAcademico());
        tramite.setOficina(oficina);
        tramite.setEstadoEnum(TramiteEstadoEnum.SOL);
        tramite.setFechaRegistro(new Date());
        tramite.setNumeroVisible(tramite.getDescripcion());
        tramiteDAO.save(tramite);

        TramiteBachiller bachiller = new TramiteBachiller();
        bachiller.setTramite(tramite);
        bachiller.setEstado(TramiteEstadoEnum.SOL.name());
        bachiller.setFechaRegistro(new Date());
        bachiller.setUsuario(ds.getUsuario());
        tramiteBachillerDAO.save(bachiller);

        Egresado egresado = egresadoDAO.findByAlumno(alumnoDB);
        if (egresado == null) {
            egresado = new Egresado();
            egresado.setAlumno(alumnoDB);
            egresado.setCarrera(alumnoDB.getCarrera());
            egresado.setCicloAcademico(alumnoDB.getCicloActivoRegular());
            egresado.setFacultad(alumnoDB.getCarrera().getFacultad());
            egresado.setEsPrincipal(0);
            egresadoDAO.save(egresado);
        } else {
            egresado.setCarrera(alumnoDB.getCarrera());
            egresado.setCicloAcademico(alumnoDB.getCicloActivoRegular());
            egresado.setFacultad(alumnoDB.getCarrera().getFacultad());
            egresado.setEsPrincipal(0);
            egresadoDAO.updateColumns(egresado, "carrera", "facultad", "cicloAcademico", "esPrincipal");
        }
    }

    @Override
    public void anular(TramiteBachiller tramiteBachiller, DataSessionPivot ds) {
        tramiteBachiller = tramiteBachillerDAO.find(tramiteBachiller.getId());
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ANU);
        ObtencionGrado obtencionGrado = obtencionGradoDAO.findByAlumnoAndTipo(tramiteBachiller.getTramite().getAlumno(), TipoGradoAcademicoEnum.BACH);
        if (obtencionGrado != null) {

            obtencionGrado.setEstadoTramite(estadoTramite);
            obtencionGrado.setFechaAnula(new Date());
            obtencionGrado.setUserAnula(ds.getUsuario());
            obtencionGradoDAO.updateColumns(obtencionGrado, "estadoTramite", "fechaAnula", "userAnula");
        }

        Tramite tramite = tramiteBachiller.getTramite();
        tramite.setFechaModificacion(new Date());
        tramite.setUserModificacion(ds.getUsuario());
        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramiteDAO.updateEstado(tramite);

        tramiteBachiller.setEstado(TramiteEstadoEnum.ANU.name());
        tramiteBachillerDAO.updateColumns(tramiteBachiller, "estado");

    }

    private void validadCaducos(Map<Long, CursoCurricula> mapCursoCurricula, List<AlumnoCicloCurso> alumnoCicloCursos) {

        for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
            if (mapCursoCurricula.get(alumnoCicloCurso.getCurso().getId()) != null && !alumnoCicloCurso.isAprobado()) {
                alumnoCicloCurso.setEsCaduco(1);
            }
        }
    }

    public Tramite findByTramite(Long id) {
        return tramiteDAO.findById(new Tramite(id));
    }

}

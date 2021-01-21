package pe.edu.lamolina.amauta.controller.tramite.tramiteBachiller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
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
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.SerieDocumentoDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteBachillerDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.zelper.pdf.PdfContent;
import pe.edu.lamolina.amauta.zelper.pdf.PdfGenerator;
import pe.edu.lamolina.amauta.zelper.pdf.TipoPdfEnum;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.DEP;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoSolicitanteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
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
    PdfGenerator pdfGenerator;

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

    @Override
    public List<TramiteBachiller> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds) {

        List<TramiteBachiller> bachillers = tramiteBachillerDAO.allByDynatable(filter, ds.getCicloAcademico());
        return bachillers;
    }

    @Override
    public String bachillerReporte(Tramite tramite, DataSessionPivot ds) {
        List<String> pdfs = createInfoBachillerPDF(tramite, ds);
        return pdfGenerator.concatPDFs(pdfs, "bachiller", true);
    }

    private List<String> createInfoBachillerPDF(Tramite tramite, DataSessionPivot ds) {
        tramite = tramiteDAO.find(tramite.getId());
        TramiteBachiller tramiteBachiller = tramiteBachillerDAO.findByTramite(tramite);

        Alumno alumno = alumnoDAO.find(tramite.getAlumno());
        CicloAcademico cicloAcademico = ds.getCicloAcademico();

        TipoCursoCurricula tipoCursoCurriculaDeporte = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.DEP);
        TipoCursoCurricula tipoCursoCurriculaGen = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.GEN);
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivosByAlumno(alumno);
        for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
            if (alumnoCicloCurso.getTipoCursoCurricula() != null && alumnoCicloCurso.getTipoCursoCurricula().getCodigoEnum() == DEP) {
                if (alumnoCicloCurso.getCreditos() > 0) {

                    alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurriculaGen);
                }
            }
            if (alumnoCicloCurso.getTipoCursoCurricula() == null) {
                alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurriculaDeporte);
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

        EventoCicloAcademico eventoActual = eventoCicloAcademicoDAO.findByCicloAndEvento(alumno.getCicloActivo(), EventoAcademicoEnum.FECHAS_BACH);
        EventoCicloAcademico eventoIngreso = eventoCicloAcademicoDAO.findByCicloAndEvento(cicloInicio, EventoAcademicoEnum.FECHAS_BACH);
        Oficina oficinaColaborador = null;
        if (alumno.getConsejero() == null || alumno.getConsejero().getColaborador() == null) {
            oficinaColaborador = oficinaDAO.findByCode("CT-" + alumno.getCarrera().getCodigo());
        }

        ctx.setVariable("alumno", alumno);
        ctx.setVariable("oficinaColaborador", oficinaColaborador);
        ctx.setVariable("ciclo", cicloAcademico);
        ctx.setVariable("historial", historialSorted);
        ctx.setVariable("alumnoCiclo", alumnoCiclo);
        ctx.setVariable("bachiller", tramiteBachiller);
        ctx.setVariable("fechaPrimaMatricula", TypesUtil.getStringDate(eventoIngreso.getFechaInicio(), " dd'/'MM'/'yyyy", "es"));
        ctx.setVariable("fechaEgreso", TypesUtil.getStringDate(eventoActual.getFechaFin(), " dd'/'MM'/'yyyy", "es"));

        ctx.setVariable("fecha", TypesUtil.getStringDate(new DateTime().toDate(), " dd 'de' MMMM 'del' yyyy", "es"));
//        ctx.setVariable("alumnoCicloCurso", listAlumnoCicloCurso);

        PdfContent pdfHistorial = new PdfContent();
        pdfHistorial.setContext(ctx);
        pdfHistorial.setTipoPdfEnum(TipoPdfEnum.HISTORIAL_ACADEMICO_TRAMITE);

        PdfContent pdfBachiller = new PdfContent();
        pdfBachiller.setContext(ctx);
        pdfBachiller.setTipoPdfEnum(TipoPdfEnum.DETALLE_BACHILLER);

        List<String> pdfs = Arrays.asList(
                pdfGenerator.generateDocument(pdfBachiller),
                pdfGenerator.generateDocument(pdfHistorial)
        );

        return pdfs;
    }

    @Override
    @Transactional
    public void saveBachiller(TramiteBachiller tramiteBachillerForm, DataSessionPivot ds) {
        LocalDate today = new LocalDate();

        logger.debug("PAse 1");
        Alumno alumnoDB = alumnoDAO.find(tramiteBachillerForm.getAlumno());
        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM_BACHI);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());

        Oficina oficina = oficinaDAO.findByCode(OficinaEnum.UR.name());
        logger.debug("PAse 2");
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.BACHI.name());
        Tramite tramite = tramiteDAO.findByAlumnoTipoTramEstado(alumnoDB, tipoTramite, TramiteEstadoEnum.SOL);
        logger.debug("PAse 3");
        Assert.isNull(tramite, "Ya cuenta con un tramite bachiller en proceso.");
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

        Egresado egresado = new Egresado();
        egresado.setAlumno(alumnoDB);
        egresado.setCarrera(alumnoDB.getCarrera());
        egresado.setCicloAcademico(ds.getCicloAcademico());
        egresado.setFacultad(alumnoDB.getCarrera().getFacultad());
        egresado.setEsPrincipal(0);
        egresadoDAO.save(egresado);
    }

}

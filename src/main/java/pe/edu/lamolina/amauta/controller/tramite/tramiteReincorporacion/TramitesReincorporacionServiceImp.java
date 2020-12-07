package pe.edu.lamolina.amauta.controller.tramite.tramiteReincorporacion;

import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.zelper.pdf.PdfGenerator;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;

@Service
@Transactional(readOnly = true)
public class TramitesReincorporacionServiceImp implements TramiteReincorporacionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;

    @Autowired
    SerieDocumentoService serieDocumentoService;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;

    @Autowired
    PdfGenerator pdfGenerator;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Override
    public List<Reincorporacion> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds) {

        List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByDynatableCiclo(filter, ds.getCicloAcademico());
        return reincorporaciones;
    }

    @Override
    @Transactional
    public void saveReincorporacion(Reincorporacion reincorporacionForm, DataSessionPivot ds) {

        DateTime today = new DateTime();
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL);

        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM_REIN);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.REI.name());
        Alumno alumnoDB = alumnoDAO.find(reincorporacionForm.getAlumno());
        Tramite tramite = new Tramite();
        tramite.setActivo(true);
        tramite.setCompania(ds.getCompania());
        tramite.setAlumno(reincorporacionForm.getAlumno());
        tramite.setCicloAcademico(ds.getCicloAcademico());
        tramite.setEstadoEnum(TramiteEstadoEnum.SOL);
        tramite.setEstadoTramite(estadoTramite);
        tramite.setFechaRegistro(new Date());
        tramite.setPersona(alumnoDB.getPersona());
        tramite.setTipoTramite(tipoTramite);
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setUserRegistro(ds.getUsuario());
        tramiteDAO.save(tramite);

        Facultad facultad = tramite.getAlumno().getCarrera().getFacultad();
        Reincorporacion reincorporacione = new Reincorporacion();
        reincorporacione.setAceptado(0);
        reincorporacione.setFechaRegistro(new Date());
        reincorporacione.setEstadoTramite(estadoTramite);
        reincorporacione.setUserRegistro(ds.getUsuario());
        reincorporacione.setAlumno(tramite.getAlumno());
        reincorporacione.setCicloReincorporacion(reincorporacionForm.getCicloReincorporacion());
        reincorporacione.setMotivoDesercion(reincorporacionForm.getMotivoDesercion());
        reincorporacione.setFacultad(facultad);
        reincorporacione.setTramite(tramite);
        reincorporacione.setAceptado(0);
        reincorporacione.setEsCondicional(Boolean.FALSE);
        reincorporacionDAO.save(reincorporacione);
    }

    @Override
    public String reporte(Tramite tramite, DataSessionPivot ds) {
        List<String> pdfs = createInfoRetiroExcepcionalPDF(tramite, ds);
        return pdfGenerator.concatPDFs(pdfs, "reincorporacion", true);
    }

    private List<String> createInfoRetiroExcepcionalPDF(Tramite tramite, DataSessionPivot ds) {
//        tramite = tramiteDAO.find(tramite.getId());
//        Alumno alumno = tramite.getAlumno();
//        Context ctx = new Context();
//        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allActivoByAlumnoCiclo(alumno, ds.getCicloAcademico());
//        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allActivesByAlumnoAsc(alumno);
//        List<RetiroCiclo> retiroCiclos = retiroCicloDAO.allByRetiroCiclo(alumno);
//        AlumnoCiclo ac = null;
//
//        InfoRetiroExcepcional infoRetiroExcepcional = new InfoRetiroExcepcional();
//        int i = 1;
//        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
//            switch (alumnoCiclo.getSituacionFinal().getCodigoEnum()) {
//                case S_N:
//                case S_A:
//                case S_5:
//                    infoRetiroExcepcional.setVecesNormal(infoRetiroExcepcional.getVecesNormal() + 1);
//                    break;
//                case S_1:
//                case S_2:
//                    infoRetiroExcepcional.setVecesObservado(infoRetiroExcepcional.getVecesObservado() + 1);
//                    break;
//                case S_U:
//                case S_2U:
//                    infoRetiroExcepcional.setVecesSuspendido(infoRetiroExcepcional.getVecesSuspendido() + 1);
//                    break;
//            }
//            if (alumnoCiclo.getCicloAcademico().isTipoRegular()) {
//                if (alumnoCiclo.isAprobado()) {
//                    infoRetiroExcepcional.setCiclosRegularesApro(infoRetiroExcepcional.getCiclosRegularesApro() + 1);
//                } else {
//                    infoRetiroExcepcional.setCiclosRegularesDesap(infoRetiroExcepcional.getCiclosRegularesDesap() + 1);
//                }
//            } else {
//                if (alumnoCiclo.isAprobado()) {
//                    infoRetiroExcepcional.setCiclosVeranoApro(infoRetiroExcepcional.getCiclosVeranoApro() + 1);
//                } else {
//                    infoRetiroExcepcional.setCiclosVeranoDesap(infoRetiroExcepcional.getCiclosVeranoDesap() + 1);
//                }
//            }
//            if (i == alumnoCiclos.size()) {
//                ac = alumnoCiclo;
//            }
//            i++;
//        }
//
//        BigDecimal relacionEficacion = new BigDecimal(ac.getCreditosAprobadosAcumulados()).divide(new BigDecimal(ac.getCreditosAcumulados()), 2, RoundingMode.FLOOR);
//        infoRetiroExcepcional.setCaa(ac.getCreditosAprobadosAcumulados());
//        infoRetiroExcepcional.setCca(ac.getCreditosAcumulados());
//        infoRetiroExcepcional.setPpa(ac.getPromedioAcumulado());
//        infoRetiroExcepcional.setPps(ac.getPromedioCiclo());
//        infoRetiroExcepcional.setRelacionEficiencia(relacionEficacion);
//        infoRetiroExcepcional.setSituacion(ac.getSituacionFinal().getNombre().toUpperCase());
//
//        ctx.setVariable("tramite", tramite);
//        ctx.setVariable("infoRetiroExcepcional", infoRetiroExcepcional);
//        ctx.setVariable("alumno", alumno);
//        ctx.setVariable("alumnoCiclo", ac);
//        ctx.setVariable("ciclo", ds.getCicloAcademico());
//        ctx.setVariable("matriculaCursos", matriculaCursos);
//        ctx.setVariable("retiroCiclos", retiroCiclos);
//        ctx.setVariable("fecha", TypesUtil.getStringDate(new DateTime().toDate(), " dd 'de' MMMM 'del' yyyy", "es"));
//
//        PdfContent pdfRetiroExcepcional = new PdfContent();
//        pdfRetiroExcepcional.setContext(ctx);
//        pdfRetiroExcepcional.setTipoPdfEnum(TipoPdfEnum.DETALLE_RETIRO_EXCEPCIONAL);
//
//        List<String> pdfs = Arrays.asList(
//                pdfGenerator.generateDocument(pdfRetiroExcepcional)
//        );
//
        return null;
    }

    @Override
    public List<CicloAcademico> getCiclos(DataSessionPivot ds) {

        return cicloAcademicoDAO.allUltimosByModalidadEnum(ModalidadEstudioEnum.PRE, 3);
    }

}

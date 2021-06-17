package pe.edu.lamolina.amauta.controller.tramite.tramiteRetiroCicloExcepcional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.RetiroCicloDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.zelper.pdf.PdfContent;
import pe.edu.lamolina.amauta.zelper.pdf.PdfGenerator;
import pe.edu.lamolina.amauta.zelper.pdf.TipoPdfEnum;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoRetiroCicloEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;

@Service
@Transactional(readOnly = true)
public class TramitesRetiroExepcionalServiceImp implements TramiteRetiroExcepcionalService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    TramiteDAO tramiteDAO;
    
    @Autowired
    RetiroCicloDAO retiroCicloDAO;
    
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
    OficinaDAO oficinaDAO;
    
    @Override
    public List<RetiroCiclo> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds) {
        
        return retiroCicloDAO.allByDynatableExcepcional(filter, ds.getCicloAcademico());
    }
    
    @Override
    @Transactional
    public void saveRetiro(RetiroCiclo retiroForm, DataSessionPivot ds) {
        Boolean esCondicional = retiroForm.getAlumno().getEsMatriculaCondicional();
        Alumno alumnoDB = alumnoDAO.find(retiroForm.getAlumno());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumnoDescRegular(alumnoDB);
        List<CicloAcademico> ciclo = alumnoCiclos.stream().map(x -> x.getCicloAcademico()).collect(Collectors.toList());
        Boolean exist = false;
        for (CicloAcademico cicloAcademico : ciclo) {
            if (Objects.equals(cicloAcademico.getId(), retiroForm.getCicloAcademico().getId())) {
                exist = true;
                break;
            }
        }
        Assert.isTrue(exist, "El alumno " + alumnoDB.getPersona().getApellidosNombres() + " no tiene actividad en el ciclo " + retiroForm.getCicloAcademico().getDescripcion());
        
        DateTime today = new DateTime();
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL);
        Oficina oficina = oficinaDAO.findByCode(OficinaEnum.UR.name());
        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.RCI.name());
        
        Tramite tramite = new Tramite();
        tramite.setActivo(true);
        tramite.setCompania(ds.getCompania());
        tramite.setAlumno(retiroForm.getAlumno());
        tramite.setCicloAcademico(ds.getCicloAcademico());
        tramite.setEstadoEnum(TramiteEstadoEnum.SOL);
        tramite.setEstadoTramite(estadoTramite);
        tramite.setFechaRegistro(new Date());
        tramite.setPersona(alumnoDB.getPersona());
        tramite.setTipoTramite(tipoTramite);
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setUserRegistro(ds.getUsuario());
        tramite.setOficina(oficina);
        tramite.setNumeroVisible(tramite.getDescripcion());
        tramiteDAO.save(tramite);
        
        RetiroCiclo retiroCiclo = new RetiroCiclo();
        retiroCiclo.setAlumno(alumnoDB);
        retiroCiclo.setCicloAcademico(retiroForm.getCicloAcademico());
        retiroCiclo.setCicloRegistro(ds.getCicloAcademico());
        retiroCiclo.setEsCondicional(esCondicional);
        retiroCiclo.setEstadoEnum(TramiteEstadoEnum.SOL);
        retiroCiclo.setEstadoTramite(estadoTramite);
        retiroCiclo.setFechaRegistro(new Date());
        retiroCiclo.setMotivo(retiroForm.getMotivo());
        retiroCiclo.setTipoEnum(TipoRetiroCicloEnum.EXCEP);
        retiroCiclo.setTramite(tramite);
        retiroCiclo.setUsuario(ds.getUsuario());
        retiroCicloDAO.save(retiroCiclo);
    }
    
    @Override
    public String reporte(Tramite tramite, DataSessionPivot ds) {
        List<String> pdfs = createInfoRetiroExcepcionalPDF(tramite, ds);
        return pdfGenerator.concatPDFs(pdfs, "bachiller", true);
    }
    
    private List<String> createInfoRetiroExcepcionalPDF(Tramite tramite, DataSessionPivot ds) {
        tramite = tramiteDAO.find(tramite.getId());
        Alumno alumno = tramite.getAlumno();
        Context ctx = new Context();
        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allActivoByAlumnoCiclo(alumno, ds.getCicloAcademico());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allActivesByAlumnoAsc(alumno);
        List<RetiroCiclo> retiroCiclos = retiroCicloDAO.allByRetiroCiclo(alumno);
        AlumnoCiclo ac = null;
        
        InfoRetiroExcepcional infoRetiroExcepcional = new InfoRetiroExcepcional();
        int i = 1;
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            switch (alumnoCiclo.getSituacionFinal().getCodigoEnum()) {
                case S_N:
                case S_A:
                case S_5:
                    infoRetiroExcepcional.setVecesNormal(infoRetiroExcepcional.getVecesNormal() + 1);
                    break;
                case S_1:
                case S_2:
                    infoRetiroExcepcional.setVecesObservado(infoRetiroExcepcional.getVecesObservado() + 1);
                    break;
                case S_6:
                case S_U:
                case S_6U:
                case S_6B:
                    logger.debug("------ VecesSuspendido ----- {}", infoRetiroExcepcional.getVecesSuspendido() + 1);
                    infoRetiroExcepcional.setVecesSuspendido(infoRetiroExcepcional.getVecesSuspendido() + 1);
                    break;
            }
            if (alumnoCiclo.getCicloAcademico().isTipoRegular()) {
                if (alumnoCiclo.isAprobado()) {
                    infoRetiroExcepcional.setCiclosRegularesApro(infoRetiroExcepcional.getCiclosRegularesApro() + 1);
                } else {
                    infoRetiroExcepcional.setCiclosRegularesDesap(infoRetiroExcepcional.getCiclosRegularesDesap() + 1);
                }
            } else {
                if (alumnoCiclo.isAprobado()) {
                    infoRetiroExcepcional.setCiclosVeranoApro(infoRetiroExcepcional.getCiclosVeranoApro() + 1);
                } else {
                    infoRetiroExcepcional.setCiclosVeranoDesap(infoRetiroExcepcional.getCiclosVeranoDesap() + 1);
                }
            }
            if (i == alumnoCiclos.size()) {
                ac = alumnoCiclo;
            }
            i++;
        }
        
        BigDecimal relacionEficacion = new BigDecimal(ac.getCreditosAprobadosAcumulados()).divide(new BigDecimal(ac.getCreditosAcumulados()), 2, RoundingMode.FLOOR);
        infoRetiroExcepcional.setCaa(ac.getCreditosAprobadosAcumulados());
        infoRetiroExcepcional.setCca(ac.getCreditosAcumulados());
        infoRetiroExcepcional.setPpa(ac.getPromedioAcumulado());
        infoRetiroExcepcional.setPps(ac.getPromedioCiclo());
        infoRetiroExcepcional.setRelacionEficiencia(relacionEficacion);
        infoRetiroExcepcional.setSituacion(ac.getSituacionFinal().getNombre().toUpperCase());
        
        ctx.setVariable("tramite", tramite);
        ctx.setVariable("infoRetiroExcepcional", infoRetiroExcepcional);
        ctx.setVariable("alumno", alumno);
        ctx.setVariable("alumnoCiclo", ac);
        ctx.setVariable("ciclo", ds.getCicloAcademico());
        ctx.setVariable("matriculaCursos", matriculaCursos);
        ctx.setVariable("retiroCiclos", retiroCiclos);
        ctx.setVariable("fecha", TypesUtil.getStringDate(new DateTime().toDate(), " dd 'de' MMMM 'del' yyyy", "es"));
        
        PdfContent pdfRetiroExcepcional = new PdfContent();
        pdfRetiroExcepcional.setContext(ctx);
        pdfRetiroExcepcional.setTipoPdfEnum(TipoPdfEnum.DETALLE_RETIRO_EXCEPCIONAL);
        
        List<String> pdfs = Arrays.asList(
                pdfGenerator.generateDocument(pdfRetiroExcepcional)
        );
        
        return pdfs;
    }
    
    @Override
    @Transactional
    public void anular(RetiroCiclo retiroCicloForm, DataSessionPivot ds) {
        
        RetiroCiclo retiroCiclo = retiroCicloDAO.find(retiroCicloForm.getId());
        
        if (retiroCiclo == null) {
            throw new PhobosException("No existe el trámite");
        }
        
        if (!(retiroCiclo.getEstadoEnum() == TramiteEstadoEnum.SOL)) {
            throw new PhobosException("Solo puede anular trámites solicitados");
        }
        
        retiroCiclo.setEstadoEnum(TramiteEstadoEnum.ANU);
        
        retiroCicloDAO.updateColumns(retiroCiclo, "estado");
        
        Tramite tramite = retiroCiclo.getTramite();
        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramite.setFechaModificacion(new Date());
        tramite.setUserModificacion(ds.getUsuario());
        tramiteDAO.updateEstado(tramite);
        
    }
    
}

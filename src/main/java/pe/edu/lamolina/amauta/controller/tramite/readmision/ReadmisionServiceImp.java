package pe.edu.lamolina.amauta.controller.tramite.readmision;

import java.util.Comparator;
import java.util.Date;
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
import org.springframework.ui.Model;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.ReadmisionDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.DEP;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Readmision;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;

@Service
@Transactional(readOnly = true)
public class ReadmisionServiceImp implements ReadmisionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteDAO tramiteDAO;

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
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;

    @Autowired
    ReadmisionDAO readmisionDAO;

    @Override
    public List<Readmision> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds) {

        return readmisionDAO.allByDynatableCiclo(filter, ds.getCicloAcademico());

    }

    @Override
    @Transactional
    public void save(Readmision readmision, DataSessionPivot ds) {

        DateTime today = new DateTime();

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL);

        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAMITE_READMISION);

        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());

        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.READMISION.name());

        Alumno alumnoDB = alumnoDAO.find(readmision.getAlumno());

        Boolean esCondicional = alumnoDB.getEsMatriculaCondicional();

        Readmision readmisionDb = readmisionDAO.findByAlumnoCiclo(alumnoDB, readmision.getCicloReadmitido());

        if (readmisionDb != null) {
            throw new PhobosException("EL alumno ya tiene tramite pendiente");
        }

        Oficina oficina = oficinaDAO.findByCode(OficinaEnum.UR.name());
        Tramite tramite = new Tramite();
        tramite.setActivo(true);
        tramite.setCompania(ds.getCompania());
        tramite.setAlumno(readmision.getAlumno());
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

        Facultad facultad = alumnoDB.getCarrera().getFacultad();

        readmision.setAceptado(0);
        readmision.setFechaRegistro(new Date());
        readmision.setEstadoTramite(estadoTramite);
        readmision.setUserRegistro(ds.getUsuario());
        readmision.setAlumno(alumnoDB);
        readmision.setFacultad(facultad);
        readmision.setTramite(tramite);
        readmision.setEsCondicional(esCondicional);
        readmisionDAO.save(readmision);

    }

    @Override
    public List<CicloAcademico> getCiclosSeis() {
        return cicloAcademicoDAO.allUltimosByModalidadEnum(ModalidadEstudioEnum.PRE, 6);
    }

    @Override
    @Transactional
    public void anular(Long readmisionId, DataSessionPivot ds) {

        Readmision readmision = readmisionDAO.find(readmisionId);

        if (readmision == null) {
            throw new PhobosException("No existe el trámite");
        }

        EstadoTramite estadoAnulado = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL_ANU);

        readmision.setEstadoTramite(estadoAnulado);

        readmisionDAO.updateColumns(readmision, "estadoTramite");

        Tramite tramite = readmision.getTramite();
        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramite.setFechaModificacion(new Date());
        tramite.setUserModificacion(ds.getUsuario());
        tramiteDAO.updateEstado(tramite);

    }

    @Override
    public List<Alumno> searchAlumno(String nombre, DataSessionPivot ds) {
        return alumnoDAO.allByName(nombre);
    }

    @Override
    public void reporte(Model model, Long idReadmision, DataSessionPivot ds) {

        logger.debug("idReadmision {}", idReadmision);

        Readmision readmision = readmisionDAO.find(idReadmision);

        logger.debug("readmision {}", readmision.getId());

        Tramite tramite = readmision.getTramite();

        Alumno alumno = alumnoDAO.find(tramite.getAlumno());
        
        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findLastActiveRegByAlumno(alumno);

        TipoCursoCurricula tipoCursoCurriculaPropedeutico = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.CPRO);
        TipoCursoCurricula tipoCursoCurriculaGeneral = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.GEN);

        
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivosByAlumno(alumno);
        
        List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumno(alumno);
        
        Map<Long, TipoCursoCurricula> mapTipoAlumnoCursoCurricula = TypesUtil.convertListToMap("curso.id", "tipoCursoCurricula", alumnoCursoCurriculas);

        for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {

            if (alumnoCicloCurso.getTipoCursoCurricula() != null && alumnoCicloCurso.getTipoCursoCurricula().getCodigoEnum() == DEP) {
                if (alumnoCicloCurso.getCreditos() > 0) {
                    alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurriculaGeneral);
                }
            }

            if (alumnoCicloCurso.getTipoCursoCurricula() == null) {
                TipoCursoCurricula tipoCursoCurricula = mapTipoAlumnoCursoCurricula.get(alumnoCicloCurso.getCurso().getId());
                if (tipoCursoCurricula == null) {
                    alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurriculaPropedeutico);
                } else {
                    alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurricula);
                }
            }

        }

        Map<TipoCursoCurricula, List<AlumnoCicloCurso>> historial = alumnoCicloCursos
                .stream()
                .collect(Collectors.groupingBy(acc -> acc.getTipoCursoCurricula()));

        SortedMap<TipoCursoCurricula, List<AlumnoCicloCurso>> historialSorted = new TreeMap<>(Comparator.comparing(TipoCursoCurricula::getOrden));
        historialSorted.putAll(historial);

        int creditosConvalidados = 0;

        List<AlumnoCicloCurso> listAlumnoCicloCurso = alumnoCicloCursoDAO.allByAlumnoOrderByTipoCurso(alumno);

        for (AlumnoCicloCurso alumnoCicloCurso : listAlumnoCicloCurso) {
            if (alumnoCicloCurso.getNota().equals("TE")) {
                creditosConvalidados = creditosConvalidados + alumnoCicloCurso.getCreditos();
            }
        }

        alumno.setCreditosConvalidadosTransient(creditosConvalidados);
        Oficina oficinaColaborador = null;
        if (alumno.getConsejero() == null || alumno.getConsejero().getColaborador() == null) {
            oficinaColaborador = oficinaDAO.findByCode("CT-" + alumno.getCarrera().getCodigo());
        }

        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findByAlumnoCiclo(alumno, ds.getCicloAcademico());
        if (alumnoConsejero != null) {
            alumno.setConsejero(alumnoConsejero.getConsejero());
        }

        Context ctx = new Context();
        ctx.setVariable("alumno", alumno);
        ctx.setVariable("oficinaColaborador", oficinaColaborador);
        ctx.setVariable("alumnoCiclo", alumnoCiclo);
        ctx.setVariable("historial", historialSorted);
        ctx.setVariable("tramite", tramite);
        ctx.setVariable("ciclo", ds.getCicloAcademico());
        ctx.setVariable("fecha", TypesUtil.getStringDate(new DateTime().toDate(), " dd 'de' MMMM 'del' yyyy", "es"));
        //metadata
        ctx.setVariable("nombrePdf", "Informe Readmisión " + tramite.getAlumno().getPersona().getPaterno() + " " + tramite.getNumero());
        ctx.setVariable("templatePdf", "detalleReadmision,historialAcademicoReadmision");

        model.addAllAttributes(ctx.getVariables());

    }

}

package pe.edu.lamolina.amauta.controller.tramite.reincorporacion;

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
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.DEP;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.enums.tramite.TipoTramiteEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
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
    MatriculaCursoDAO matriculaCursoDAO;

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

    @Override
    public List<Reincorporacion> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds) {

        return reincorporacionDAO.allByDynatableCiclo(filter, ds.getCicloAcademico());

    }

    @Override
    @Transactional
    public void saveReincorporacion(Reincorporacion reincorporacionForm, DataSessionPivot ds) {

        Boolean esCondicional = reincorporacionForm.getAlumno().getEsMatriculaCondicional();
        DateTime today = new DateTime();
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL);

        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM_REIN);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.REI.name());
        Alumno alumnoDB = alumnoDAO.find(reincorporacionForm.getAlumno());

        Reincorporacion reincorporacione = reincorporacionDAO.findByAlumnoCiclo(alumnoDB, reincorporacionForm.getCicloReincorporacion());

        if (reincorporacione != null) {
            throw new PhobosException(String.format("EL alumno ya tiene tramite en proceso en el ciclo %s", reincorporacione.getTramite().getCicloAcademico().getDescripcion2()));
        }

        Oficina oficina = oficinaDAO.findByCode(OficinaEnum.UR.name());
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
        tramite.setOficina(oficina);
        tramite.setNumeroVisible(tramite.getDescripcion());
        tramiteDAO.save(tramite);

        Facultad facultad = alumnoDB.getCarrera().getFacultad();
        reincorporacione = new Reincorporacion();
        reincorporacione.setAceptado(0);
        reincorporacione.setFechaRegistro(new Date());
        reincorporacione.setEstadoTramite(estadoTramite);
        reincorporacione.setUserRegistro(ds.getUsuario());
        reincorporacione.setAlumno(alumnoDB);
        reincorporacione.setCicloReincorporacion(reincorporacionForm.getCicloReincorporacion());
        reincorporacione.setMotivoDesercion(reincorporacionForm.getMotivoDesercion());
        reincorporacione.setFacultad(facultad);
        reincorporacione.setTramite(tramite);
        reincorporacione.setEsCondicional(esCondicional);
        reincorporacionDAO.save(reincorporacione);
    }

    @Override
    public void reporte(Long idTramite, Model model, DataSessionPivot ds) {

        Tramite tramite = this.findByTramite(idTramite);
        Alumno alumno = alumnoDAO.find(tramite.getAlumno());
        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findLastActiveRegByAlumno(alumno);

        TipoCursoCurricula tipoCursoCurriculaCPRO = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.CPRO);
        TipoCursoCurricula tipoCursoCurriculaGen = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.GEN);
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivosByAlumno(alumno);
        List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumno(alumno);
        Map<Long, TipoCursoCurricula> mapTipoAlumnoCursoCurricula = TypesUtil.convertListToMap("curso.id", "tipoCursoCurricula", alumnoCursoCurriculas);

        for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {

            if (alumnoCicloCurso.getTipoCursoCurricula() != null && alumnoCicloCurso.getTipoCursoCurricula().getCodigoEnum() == DEP) {
                // Para asignar un curso de deporte como curso general
                if (alumnoCicloCurso.getCreditos() > 0) {
                    alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurriculaGen);
                }
            }
            if (alumnoCicloCurso.getTipoCursoCurricula() == null) {
                TipoCursoCurricula tipoCursoCurricula = mapTipoAlumnoCursoCurricula.get(alumnoCicloCurso.getCurso().getId());
                if (tipoCursoCurricula == null) {
                    // se asigna para cursos propedeuticos.
                    alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurriculaCPRO);
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

        model.addAttribute("alumno", alumno);
        model.addAttribute("oficinaColaborador", oficinaColaborador);
        model.addAttribute("alumnoCiclo", alumnoCiclo);
        model.addAttribute("historial", historialSorted);
        model.addAttribute("tramite", tramite);
        model.addAttribute("ciclo", ds.getCicloAcademico());
        model.addAttribute("fecha", TypesUtil.getStringDate(new DateTime().toDate(), " dd 'de' MMMM 'del' yyyy", "es"));
        model.addAttribute("nombrePdf", "Informe Reincorporacion " + tramite.getAlumno().getPersona().getPaterno() + " " + tramite.getNumero());

        if (alumno.getCicloActivo() != null) {
            model.addAttribute("templatePdf", "detalleReincorporacion,historialAcademicoCurdir");
        } else {
            model.addAttribute("templatePdf", "detalleReincorporacion");
        }
    }

    @Override
    public List<CicloAcademico> getCiclos(DataSessionPivot ds) {

        CicloAcademico ca = ds.getCicloAcademico();
        int rango = 10;
        return cicloAcademicoDAO.allPregradoFuturosByRange(ca.getYear() - rango, ca.getYear() + 3);
    }

    private void validadCaducos(Map<Long, CursoCurricula> mapCursoCurricula, List<AlumnoCicloCurso> alumnoCicloCursos) {

        for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
            if (mapCursoCurricula.get(alumnoCicloCurso.getCurso().getId()) != null) {
                alumnoCicloCurso.setEsCaduco(1);
            }
        }

    }

    public Tramite findByTramite(Long id) {
        return tramiteDAO.findById(new Tramite(id));
    }
}

package pe.edu.lamolina.amauta.controller.tramite.trasladointerno;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteTrasladoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.DEP;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteTrasladoEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.ACEP;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteTraslado;

@Service
@Transactional(readOnly = true)
public class TramitesTrasladoServiceImp implements TramiteTrasladoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    CarreraDAO carreraDAO;

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
    OficinaDAO oficinaDAO;

    @Autowired
    TramiteTrasladoDAO tramiteTrasladoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;

    @Override
    public List<TramiteTraslado> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds) {

        List<TramiteTraslado> tramitesTraslado = tramiteTrasladoDAO.allByDynatableCiclo(filter, ds.getCicloAcademico());
        return tramitesTraslado;
    }

    @Override
    @Transactional
    public void saveTramiteTraslado(TramiteTraslado tramiteTrasForm, DataSessionPivot ds) {

        DateTime today = new DateTime();
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL);

        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM_TRAS_INT);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.TRAS_INT.name());
        Alumno alumnoDB = alumnoDAO.find(tramiteTrasForm.getAlumno());

        TramiteTraslado tramiteTras = tramiteTrasladoDAO.findTramiteExistenteByAlumnoCiclo(alumnoDB, ds.getCicloAcademico());

        if (tramiteTras != null) {
            throw new PhobosException(String.format(" Ya cuenta con un tramite titulo en proceso en el ciclo %s", tramiteTras.getCicloAcademico().getDescripcion2()));
        }

        Oficina oficina = oficinaDAO.findByCode(OficinaEnum.UR.name());
        Tramite tramite = new Tramite();
        tramite.setActivo(true);
        tramite.setCompania(ds.getCompania());
        tramite.setAlumno(tramiteTrasForm.getAlumno());
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

        tramiteTras = new TramiteTraslado();
        tramiteTras.setAlumno(alumnoDB);
        tramiteTras.setFechaRegistro(new Date());
        tramiteTras.setEstado(estadoTramite.getCodigo());
        tramiteTras.setUserRegistro(ds.getUsuario());
        tramiteTras.setTramite(tramite);
        tramiteTras.setCarrera(tramiteTrasForm.getCarrera());
        tramiteTras.setCarreraOrigen(alumnoDB.getCarrera());
        tramiteTras.setCicloAcademico(ds.getCicloAcademico());
        tramiteTras.setTipoTramiteTrasladoEnum(TipoTramiteTrasladoEnum.TRAS_INT);
        tramiteTrasladoDAO.save(tramiteTras);
    }

    @Override
    public Context reporte(Tramite idTramite, DataSessionPivot ds) {

        Tramite tramite = tramiteDAO.findById(idTramite);
        TramiteTraslado traslado = tramiteTrasladoDAO.findByTramite(tramite);
        tramite = tramiteDAO.find(tramite.getId());
        Alumno alumno = alumnoDAO.find(tramite.getAlumno());
        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findLastActiveEstudiadoByAlumno(alumno);

        TipoCursoCurricula tipoCursoCurriculaCPRO = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.CPRO);
        TipoCursoCurricula tipoCursoCurriculaGen = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.GEN);
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivosByAlumno(alumno);

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
                .filter(x -> x.isAprobado())
                .collect(Collectors.groupingBy(acc -> acc.getTipoCursoCurricula()));

        SortedMap<TipoCursoCurricula, List<AlumnoCicloCurso>> historialSorted = new TreeMap<>(Comparator.comparing(TipoCursoCurricula::getOrden));
        historialSorted.putAll(historial);

        Oficina oficinaColaborador = null;

        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findByAlumnoCiclo(alumno, ds.getCicloAcademico());
        if (alumnoConsejero != null) {
            alumno.setConsejero(alumnoConsejero.getConsejero());

        }

        if (alumno.getConsejero() == null || alumno.getConsejero().getColaborador() == null) {
            oficinaColaborador = oficinaDAO.findByCode("CT-" + alumno.getCarrera().getCodigo());
        }
        List<Carrera> carreras = carreraDAO.allByFilter(traslado.getCarrera().getFacultad(), EstadoEnum.ACT);
        boolean especificarCarrera = false;
        if (carreras.size() > 1 || Objects.equals(traslado.getCarrera().getFacultad().getId(), traslado.getCarreraOrigen().getFacultad().getId())) {
            especificarCarrera = true;
        }
        List<Carrera> carrerasOrigen = carreraDAO.allByFilter(traslado.getCarreraOrigen().getFacultad(), EstadoEnum.ACT);
        boolean especificarCarreraOrigen = false;
        if (carrerasOrigen.size() > 1 || Objects.equals(traslado.getCarrera().getFacultad().getId(), traslado.getCarreraOrigen().getFacultad().getId())) {
            especificarCarreraOrigen = true;
        }

        Context ctx = new Context();
        ctx.setVariable("especificarCarrera", especificarCarrera);
        ctx.setVariable("especificarCarreraOrigen", especificarCarreraOrigen);
        ctx.setVariable("traslado", traslado);
        ctx.setVariable("alumno", alumno);
        ctx.setVariable("oficinaColaborador", oficinaColaborador);
        ctx.setVariable("alumnoCiclo", alumnoCiclo);
        ctx.setVariable("historial", historialSorted);
        ctx.setVariable("tramite", tramite);
        ctx.setVariable("ciclo", ds.getCicloAcademico());
        ctx.setVariable("fecha", TypesUtil.getStringDate(new DateTime().toDate(), " dd 'de' MMMM 'del' yyyy", "es"));

        ctx.setVariable("nombrePdf", "Informe Traslado " + tramite.getAlumno().getPersona().getPaterno() + " " + tramite.getNumero());
        ctx.setVariable("templatePdf", "detalleTrasladoInterno,historialAcademicoCurdir");

        return ctx;
    }

    @Override
    public List<Carrera> getCarreras(DataSessionPivot ds) {

        return carreraDAO.allActivasByModalidadEnum(ModalidadEstudioEnum.PRE);
    }

    private void validadCaducos(Map<Long, CursoCurricula> mapCursoCurricula, List<AlumnoCicloCurso> alumnoCicloCursos) {
        for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
            if (mapCursoCurricula.get(alumnoCicloCurso.getCurso().getId()) != null) {
                alumnoCicloCurso.setEsCaduco(1);
            }
        }
    }

    @Override
    @Transactional
    public void anular(Long idTramiteTraslado, Usuario  usuario) {

        TramiteTraslado tramiteTraslado = tramiteTrasladoDAO.findAll(idTramiteTraslado);

        if (tramiteTraslado == null) {
            throw new PhobosException("El trámite no fue encontrado");
        }

        if (tramiteTraslado.getEstado().equalsIgnoreCase(ACEP.name())) {
            throw new PhobosException("El trámite ya fue aceptado");
        }

        if (tramiteTraslado.getEstado().equalsIgnoreCase(TramiteEstadoEnum.ANU.name())) {
            throw new PhobosException("El trámite ya fue anulado");
        }

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ANU);

        Tramite tramite = tramiteTraslado.getTramite();
        tramite.setFechaModificacion(new Date());
        tramite.setUserModificacion(usuario);
        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramite.setEstadoTramite(estadoTramite);
        tramiteDAO.updateEstado(tramite);

        tramiteTraslado.setEstado(TramiteEstadoEnum.ANU.name());
        tramiteTrasladoDAO.updateColumns(tramiteTraslado, "estado");
    }

}

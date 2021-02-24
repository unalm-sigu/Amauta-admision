package pe.edu.lamolina.amauta.controller.academico.resolucion.resolucionExistentes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.bean.AlumnoCicloCursoBean;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.ResolucionEstadoEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.TipoResolucionEnum;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.ING_HIS;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.TRAS;
import pe.edu.lamolina.model.enums.TipoRetiroCicloEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteTrasladoEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.ACEP;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.RCHZ;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.CambioNota;
import pe.edu.lamolina.model.tramite.CambioNotaMasBaja;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteTraslado;
import pe.edu.lamolina.amauta.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.amauta.controller.matricula.matriculable.MatriculableService;
import pe.edu.lamolina.amauta.controller.programacionhorarios.gposeccion.GpoSeccionService;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.controller.test.VisorCalculoNotas;
import static pe.edu.lamolina.amauta.controller.test.VisorCalculoNotas.TOKEN_CURRICULA;
import static pe.edu.lamolina.amauta.controller.test.VisorCalculoNotas.TOKEN_MATRICULABLE;
import static pe.edu.lamolina.amauta.controller.test.VisorCalculoNotas.TOKEN_PROMEDIOS;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoOpcionalCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.GradoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.posgrado.CambioNotaMasBajaDAO;
import pe.edu.lamolina.amauta.dao.tramite.AccionTramiteAcademicoDAO;
import pe.edu.lamolina.amauta.dao.tramite.CambioNotaDAO;
import pe.edu.lamolina.amauta.dao.tramite.CursoDirigidoDAO;
import pe.edu.lamolina.amauta.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.ObtencionGradoDAO;
import pe.edu.lamolina.amauta.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.amauta.dao.tramite.ResolucionDAO;
import pe.edu.lamolina.amauta.dao.tramite.RetiroCicloDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoResolucionDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteBachillerDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramitePracticaPreProfesionalesDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteTituloDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteTrasladoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.GradoAcademico;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import static pe.edu.lamolina.model.enums.TipoCondicionalEnum.TRAS_INT;
import pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum;
import static pe.edu.lamolina.model.enums.TipoTramiteEnum.INTES;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.PracticasPreProfesional;
import pe.edu.lamolina.model.tramite.TramiteBachiller;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

@Service
@Transactional(readOnly = true)
public class ResolucionExistentesServiceImp implements ResolucionExistenteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    PlanCurricularDAO planCurricularDAO;

    @Autowired
    TipoResolucionDAO tipoResolucionDAO;

    @Autowired
    ResolucionDAO resolucionDAO;
    @Autowired
    CursoDirigidoDAO cursoDirigidoDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;
    @Autowired
    TramiteDAO tramiteDAO;
    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;
    @Autowired
    TipoTramiteDAO tipoTramiteDAO;
    @Autowired
    RetiroCicloDAO retiroCicloDAO;
    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;
    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    MatriculableService matriculableService;
    @Autowired
    SerieDocumentoService serieDocumentoService;
    @Autowired
    CambioNotaDAO cambioNotaDAO;
    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;
    @Autowired
    AccionTramiteAcademicoDAO accionTramiteAcademicoDAO;
    @Autowired
    SeccionDAO seccionDAO;
    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;
    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    TramiteTrasladoDAO tramiteTrasladoDAO;
    @Autowired
    AvanceCurricularService avanceCurricularService;
    @Autowired
    GpoSeccionService gpoSeccionService;
    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;
    @Autowired
    CursoOpcionalCurriculaDAO cursoOpcionalCurriculaDAO;
    @Autowired
    OficinaDAO oficinaDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    CambioNotaMasBajaDAO cambioNotaMasBajaDAO;

    @Autowired
    ObtencionGradoDAO obtencionGradoDAO;
    @Autowired
    TramiteBachillerDAO tramiteBachillerDAO;

    @Autowired
    VisorCalculoNotas visorCalculoNotas;

    @Autowired
    GradoAcademicoDAO gradoAcademicoDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Autowired
    EgresadoDAO egresadoDAO;

    @Autowired
    TramiteTituloDAO tramiteTituloDAO;

    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;

    @Autowired
    TramitePracticaPreProfesionalesDAO practicaPreProfesionalesDAO;

    @Override
    public List<Alumno> allAlumnoByOficina(String nombre, Long instanciaOficina) {
        Oficina oficina = instanciaOficina == null ? null : oficinaDAO.find(instanciaOficina);
        if (oficina != null && oficina.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.FAC) {
            return alumnoDAO.allByNameFacultad(nombre, new Facultad(oficina.getInstanciaOficina()));
        }

        if (oficina != null && oficina.getCodigoEnum() == OficinaEnum.EPG) {
            return alumnoDAO.allByNamePosgrado(nombre);
        }
        if (oficina == null || oficina.getCodigoEnum() == OficinaEnum.UNA) {
            return alumnoDAO.allByName(nombre);
        }

        return new ArrayList();
    }

    @Override
    @Transactional
    public String saveReincorporacion(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {

        TipoResolucion tipoResolucion = tipoResolucionDAO.finByCodigo(TipoResolucionEnum.REIC);
        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setCicloAplica(resolucionForm.getCicloAplica());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setTipoResolucion(tipoResolucion);
        resolucion.setUserRegistro(usuario);
        resolucion.setAplicacionDirecta(1l);
        resolucionDAO.save(resolucion);

        Assert.isFalse(resolucionForm.getReincorporaciones().isEmpty(), "Debe Agregar alumnos.");

        return this.saveReincorporaciones(resolucionForm, resolucion, ds);
    }

    @Override
    public Resolucion findByResolucion(Long resolucionId, DataSessionPivot ds) {
        Resolucion resolucion = resolucionDAO.findById(resolucionId);

        return resolucion;
    }

    @Override
    public List<TipoResolucion> allTipoResolucion() {

        return tipoResolucionDAO.all();
    }

    @Override
    @Transactional
    public String saveRetiroCiclo(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {

        TipoResolucion tipoResolucion = tipoResolucionDAO.finByCodigo(resolucionForm.getTipoResolucion().getTipoEnum());
        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setTipoResolucion(tipoResolucion);
        resolucion.setUserRegistro(usuario);
        resolucion.setAplicacionDirecta(1l);
        resolucion.setCicloAplica(resolucionForm.getCicloAplica());
        resolucionDAO.save(resolucion);

        Assert.isFalse(resolucionForm.getRetiroCiclo().isEmpty(), "Debe Agregar alumnos.");

        return this.saveRetirosCiclos(resolucionForm, resolucion, ds);
    }

    @Override
    public List<CicloAcademico> ciclosAnteriores(int i) {
        CicloAcademico cicloAcademico = cicloAcademicoDAO.findActivoPregrado();
        return cicloAcademicoDAO.allMenorIgual(i, cicloAcademico);
    }

    @Override
    public List<Reincorporacion> allReincorporacionByResolucion(Resolucion resolucionDB) {
        return reincorporacionDAO.allByResolucion(resolucionDB);
    }

    @Override
    public List<RetiroCiclo> allRetiroCicloByResolucion(Resolucion resolucionDB) {
        return retiroCicloDAO.allByResolucion(resolucionDB);
    }

    @Override
    @Transactional
    public String saveCambioNota(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {

        TipoResolucion tipoResolucion = tipoResolucionDAO.finByCodigo(TipoResolucionEnum.CAM_NOTA);
        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setTipoResolucion(tipoResolucion);
        resolucion.setUserRegistro(usuario);
        resolucion.setAplicacionDirecta(1l);
        resolucion.setCicloAplica(resolucionForm.getCicloAplica());
        resolucionDAO.save(resolucion);

        Assert.isFalse(resolucionForm.getCambioNota().isEmpty(), "Debe Agregar alumnos.");

        return this.saveCambioNotas(resolucionForm, resolucion, ds);
    }

    @Override
    public List<CambioNota> allCambioNota(Resolucion resolucionDB) {
        return cambioNotaDAO.allByResolucion(resolucionDB);

    }

    @Override
    public List<CursoDirigido> allCursodirigido(Resolucion resolucionDB) {
        return cursoDirigidoDAO.allByResolucion(resolucionDB);
    }

    private Integer evaluateEstaAprobado(BigDecimal nota, Alumno alumno) {
        Integer aprobado = BigDecimal.ZERO.intValue();
        if (alumno.isPostgrado()) {
            if (nota.compareTo(new BigDecimal(13)) >= 0) {
                aprobado = BigDecimal.ONE.intValue();
            }
        } else if (nota.compareTo(new BigDecimal(11)) >= 0) {
            aprobado = BigDecimal.ONE.intValue();
        }
        return aprobado;
    }

    @Override
    @Transactional
    public List<String> saveCursoDirigido(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {
        List<String> msg = new ArrayList();

        Assert.isFalse(resolucionForm.getCursoDirigido().isEmpty(), "Debe Agregar alumnos.");
        List<CursoDirigido> cursoDirigidos = cursoDirigidoDAO.allByCicloAcademicoSol(ds.getCicloAcademico());
        Map<Long, CursoDirigido> map = TypesUtil.convertListToMap("tramite.alumno.id", cursoDirigidos);
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RES_FAC);
        EstadoTramite estadoTramiteRech = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RHZ_SOL);

        List<Alumno> alumnos = resolucionForm.getCursoDirigido().stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allByAlumnosCicloActivo(alumnos);
        Map<Long, List<MatriculaCurso>> mapMatriculaCursos = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", matriculaCursos);

        for (CursoDirigido cursoDirigidoForm : resolucionForm.getCursoDirigido()) {
            String message = "";
            CursoDirigido cursoDirigidoTram = map.get(cursoDirigidoForm.getAlumno().getId());
            Assert.isNotNull(cursoDirigidoTram, "El alumno " + cursoDirigidoForm.getAlumno().getCodigo() + " no cuenta con un tramite de curso dirigido.");

            List<MatriculaCurso> matriculasCursoAlumno = mapMatriculaCursos.get(cursoDirigidoForm.getAlumno().getId());
            if (matriculasCursoAlumno != null
                    && matriculasCursoAlumno.stream().filter(x -> x.getEstadoEnum() == EstadoMatriculaEnum.MAT && Objects.equals(x.getCurso().getId(), cursoDirigidoTram.getCurso().getId())).findAny().orElse(null) != null) {
                message = "El alumno " + cursoDirigidoForm.getAlumno().getCodigo() + " está matriculado en el curso " + cursoDirigidoTram.getCurso().getNombre();
                msg.add(message);
            }
        }
        if (!msg.isEmpty()) {
            return msg;
        }

        TipoResolucion tipoResolucion = tipoResolucionDAO.finByCodigo(TipoResolucionEnum.CURDIR);
        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setTipoResolucion(tipoResolucion);
        resolucion.setUserRegistro(usuario);
        resolucion.setAplicacionDirecta(1l);
        resolucion.setCicloAplica(resolucionForm.getCicloAplica());
        resolucionDAO.save(resolucion);

        for (CursoDirigido cursoDirigidoForm : resolucionForm.getCursoDirigido()) {

            EstadoTramite estado = cursoDirigidoForm.getSeleccionado() ? estadoTramite : estadoTramiteRech;
            TramiteEstadoEnum estadotram = cursoDirigidoForm.getSeleccionado() ? TramiteEstadoEnum.ACEP : TramiteEstadoEnum.RCHZ;
            CursoDirigido cursoDirigidoTram = map.get(cursoDirigidoForm.getAlumno().getId());

            cursoDirigidoTram.setMotivoRechazo(cursoDirigidoTram.getMotivoRechazo());
            cursoDirigidoTram.setResolucion(resolucion);
            cursoDirigidoTram.setDocenteAsignado(cursoDirigidoForm.getDocenteAsignado());
            cursoDirigidoTram.setEstado(estado);
            cursoDirigidoDAO.update(cursoDirigidoTram);

            Tramite tramite = cursoDirigidoTram.getTramite();
            tramite.setEstadoEnum(estadotram);
            tramiteDAO.update(tramite);

            if (!cursoDirigidoForm.getSeleccionado()) {
                continue;
            }
            AnexoBoletin anexoBoletin = anexoBoletinDAO.findDepartamento(cursoDirigidoTram.getCurso().getDepartamentoAcademico());
            Assert.isNotNull(anexoBoletin, "No existe el anexo boletín para el departamento " + cursoDirigidoTram.getCurso().getDepartamentoAcademico().getNombre());
            List<GrupoSeccion> grupoSeccions = null;
            GrupoSeccion grupoSeccion = gpoSeccionService.findByCursoAndDocenteDirigido(cursoDirigidoTram.getCurso(), cursoDirigidoTram.getDocenteAsignado(), ds.getCicloAcademico());
            if (grupoSeccion == null) {
                grupoSeccion = new GrupoSeccion();
                grupoSeccion.setCantidad(1);
                grupoSeccion.setCursoDirigido(Boolean.TRUE);
                grupoSeccion.setCurso(cursoDirigidoTram.getCurso());
                grupoSeccion.setDocenteResponsable(cursoDirigidoTram.getDocenteAsignado());
                grupoSeccion.setAnexoBoletin(anexoBoletin);
                grupoSeccions = gpoSeccionService.saveGpoSeccionHeader(grupoSeccion, ds.getCicloAcademico(), ds);
            } else {
                grupoSeccions = new ArrayList<>();
                grupoSeccions.add(grupoSeccion);
            }
            this.matricular(grupoSeccions.get(0), cursoDirigidoTram.getTramite().getAlumno(), cursoDirigidoTram.getCurso(), ds.getUsuario(), ds.getCicloAcademico(), mapMatriculaCursos);
        }

        return msg;
    }

    @Transactional
    private void matricular(GrupoSeccion gpoSeccion, Alumno alumno, Curso curso, Usuario usuario, CicloAcademico ciclo, Map<Long, List<MatriculaCurso>> mapMatriculaCursos) {

        MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, ciclo);
        List<Seccion> seccions = seccionDAO.allActivosByGpoSeccion(gpoSeccion);
        for (Seccion seccion : seccions) {
            seccion.setVacantes(seccion.getVacantes() + 1);
            seccion.setMatriculados(seccion.getMatriculados() + 1);
            seccionDAO.update(seccion);

            MatriculaSeccion matriculaSeccion = new MatriculaSeccion();
            matriculaSeccion.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriculaSeccion.setFechaRegistro(new Date());
            matriculaSeccion.setUserRegistro(usuario);
            matriculaSeccion.setSeccion(seccion);
            matriculaSeccion.setMatriculaResumen(matriculaResumen);
            matriculaSeccion.setVisible(1);
            matriculaSeccion.setFechaMatricula(new Date());
            matriculaSeccion.setUserMatricula(usuario);

            matriculaSeccionDAO.save(matriculaSeccion);
        }
        AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursoCurriculaDAO.findByAlumnoCurso(alumno, curso);
        if (alumnoCursoCurricula != null) {
            alumnoCursoCurricula.setEstadoMatriculaEnum(EstadoMatriculaEnum.MAT);
            alumnoCursoCurriculaDAO.updateEstado(alumnoCursoCurricula);
        } else {
            TipoCursoCurricula tipoCursoCurricula = tipoCursoCurriculaDAO.findByCodigo(TipoCursoCurriculaEnum.ELC);
            CursoOpcionalCurricula opcionalCurricula = cursoOpcionalCurriculaDAO.findByPlanCurricularAndCurso(alumno.getPlanCurricular(), curso);
            alumnoCursoCurricula = new AlumnoCursoCurricula();
            alumnoCursoCurricula.setAlumno(alumno);
            alumnoCursoCurricula.setTipoCursoCurricula(tipoCursoCurricula);
            alumnoCursoCurricula.setCurso(curso);
            alumnoCursoCurricula.setCursoOpcional(opcionalCurricula);
            alumnoCursoCurricula.setCursoCurricula(null);
            alumnoCursoCurricula.setEstadoEnum(CursoCurriculaEstadoEnum.HAB);
            alumnoCursoCurricula.setEstadoRegistro(EstadoEnum.ACT.name());
            alumnoCursoCurricula.setNumeroCiclo(10);
            alumnoCursoCurricula.setValidado(true);
            alumnoCursoCurricula.setVecesCursado(0);
            alumnoCursoCurricula.setEstadoMatriculaEnum(EstadoMatriculaEnum.MAT);
            alumnoCursoCurricula.setCreditos(curso.getCreditos());
            alumnoCursoCurriculaDAO.save(alumnoCursoCurricula);
        }
        List<MatriculaCurso> matriculaCursos = mapMatriculaCursos.get(alumno.getId());
        if (matriculaCursos != null && matriculaCursos.stream().filter((MatriculaCurso x) -> Objects.equals(x.getCurso().getId(), curso.getId())).findAny().orElse(null) != null) {
            MatriculaCurso matriculaCurso = matriculaCursos.stream().filter(x -> Objects.equals(x.getCurso().getId(), curso.getId())).findAny().orElse(null);
            matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriculaCurso.setUserMatricula(usuario);
            matriculaCurso.setFechaMatricula(new Date());
            matriculaCursoDAO.updateColumns(matriculaCurso, "estado", "userMatricula", "fechaMatricula");
        } else {

            MatriculaCurso matriculaCurso = new MatriculaCurso();
            matriculaCurso.setCurso(curso);
            matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
            matriculaCurso.setMatriculaResumen(matriculaResumen);
            matriculaCurso.setNotaAcumulada("0");
            matriculaCurso.setNotaAvance("0");
            matriculaCurso.setNotaFinal("0");
            matriculaCurso.setPorcentajeAvanceNota(0);
            matriculaCurso.setCreditosAprobados(0);
            matriculaCurso.setCreditos(curso.getCreditos());
            matriculaCurso.setTipoCursoCurricula(alumnoCursoCurricula.getTipoCursoCurricula());
            matriculaCurso.setInasistencias(0);
            matriculaCurso.setInasistenciasExoneradas(0);
            matriculaCurso.setUserMatricula(usuario);
            matriculaCurso.setFechaMatricula(new Date());
            matriculaCursoDAO.save(matriculaCurso);
        }

        matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.MAT);
        matriculaResumen.setCursosMatriculados(matriculaResumen.getCursosMatriculados() + 1);
        matriculaResumen.setCreditosMatriculados(matriculaResumen.getCreditosMatriculados() + curso.getCreditos());
        matriculaResumenDAO.update(matriculaResumen);
    }

    @Override
    @Transactional
    public void saveTramiteTraslado(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {

        Assert.isFalse(resolucionForm.getTramiteTraslado().isEmpty(), "Debe Agregar alumnos.");

        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setUserRegistro(usuario);
        resolucion.setTipoResolucion(resolucionForm.getTipoResolucion());
        resolucion.setAplicacionDirecta(1l);
        if (resolucionForm.getTipoResolucion().getCodigo().equals(TRAS_INT.name())) {
            resolucion.setCicloAplica(resolucionForm.getCicloAplica());
        } else {
            resolucion.setCicloAplica(ds.getCicloAcademico());
        }
        resolucionDAO.save(resolucion);
        if (!resolucionForm.getTipoResolucion().getCodigo().equals(TRAS_INT.name())) {
            this.saveTramitesTraslado(resolucionForm, resolucion, ds);
        } else {
            this.saveTramitesTrasladoInterno(resolucionForm, resolucion, ds);
        }
    }

    @Override
    public void saveIngresoHisto(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {
        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setUserRegistro(usuario);
        resolucion.setTipoResolucion(resolucionForm.getTipoResolucion());
        resolucion.setAplicacionDirecta(1l);
        resolucionDAO.save(resolucion);

        Assert.isFalse(resolucionForm.getTramiteTraslado().isEmpty(), "Debe Agregar alumnos.");
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL_ACEP);
    }

    @Override
    public List<TramiteTraslado> allTramiteTraslado(Resolucion resolucionDB) {
        return tramiteTrasladoDAO.allByResolucion(resolucionDB);
    }

    @Override
    public List<Carrera> allCarrera() {

        return carreraDAO.allActivasByModalidadEnum(ModalidadEstudioEnum.PRE);
    }

    @Override
    public void generarNuevoPlan(Resolucion resolucionForm, DataSessionPivot ds) {
        List<Alumno> alumnos = new ArrayList();
        for (TramiteTraslado tramiteTraslado : resolucionForm.getTramiteTraslado()) {
            if (tramiteTraslado.getSeleccionado() && tramiteTraslado.getId() != null) {

                Alumno alumno = alumnoDAO.find(tramiteTraslado.getAlumno());

                alumnos.add(alumno);
            }
        }
        avanceCurricularService.generarAvanceCurricularByAlumnosPregrados(alumnos, ds, null);
    }

    private String getIndiceCicloAcademico(String codigoCicloAlumno, List<String> codigosCiclosPlanes) {
        for (String codigoCicloPlan : codigosCiclosPlanes) {
            if (codigoCicloAlumno.compareTo(codigoCicloPlan) >= 0) {
                return codigoCicloPlan;
            }
        }
        return null;
    }

    @Override
    public String saveNotaMasBaja(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {
        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setUserRegistro(usuario);
        resolucion.setTipoResolucion(resolucionForm.getTipoResolucion());
        resolucion.setAplicacionDirecta(1l);
        resolucion.setCicloAplica(resolucionForm.getCicloAplica());
        resolucionDAO.save(resolucion);

        return this.saveNotasMasBajas(resolucionForm, resolucion, usuario, ds);
    }

    @Override
    public List<AlumnoCicloCursoBean> allCiclosRepetido(Long idAlumno, DataSessionPivot ds) {
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivosByAlumno(new Alumno(idAlumno));

        Map<Long, List<AlumnoCicloCurso>> map = TypesUtil.convertListToMapList("curso.id", alumnoCicloCursos);

        List<AlumnoCicloCurso> cicloCursos = new ArrayList();
        for (Long cursoId : map.keySet()) {
            if (map.get(cursoId).size() > 1) {
                cicloCursos.addAll(map.get(cursoId));
            }
        }
        List<AlumnoCicloCursoBean> alumnoCicloCursoBeans = new ArrayList<>();
        for (AlumnoCicloCurso cicloCurso : cicloCursos) {
            AlumnoCicloCursoBean alumnoCicloCursoBean = new AlumnoCicloCursoBean();
            alumnoCicloCursoBean.setAlumno(cicloCurso.getAlumnoCiclo().getAlumno());
            alumnoCicloCursoBean.setCicloAcademico(cicloCurso.getAlumnoCiclo().getCicloAcademico());
            alumnoCicloCursoBean.setCurso(cicloCurso.getCurso());
            alumnoCicloCursoBean.setNota(cicloCurso.getNota());
            alumnoCicloCursoBean.setKey(cicloCurso.getAlumnoCicloCursoKey());
            alumnoCicloCursoBeans.add(alumnoCicloCursoBean);
        }
        return alumnoCicloCursoBeans;
    }

    @Override
    @Transactional
    public List<String> updateResolucion(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {
        Resolucion resolucionBD = resolucionDAO.findById(resolucionForm.getId());
        resolucionBD.setFecha(resolucionForm.getFecha());
        resolucionBD.setSerie(resolucionForm.getSerie());
        resolucionBD.setNumero(resolucionForm.getNumero());
        resolucionBD.setOficina(resolucionForm.getOficina());
        if (Arrays.asList(TRAS_INT.name(), TRAS.name(), INTES.name(), ING_HIS.name()).contains(resolucionBD.getTipoResolucion().getCodigo())) {
            resolucionBD.setCicloAplica(resolucionForm.getCicloAplica());
            resolucionDAO.updateColumns(resolucionBD, "fecha", "serie", "numero", "oficina", "cicloAplica");
        } else {
            resolucionDAO.updateColumns(resolucionBD, "fecha", "serie", "numero", "oficina");
        }

        if (resolucionBD.isTipoReincorporacion()) {
            return Arrays.asList(this.saveReincorporaciones(resolucionForm, resolucionBD, ds));
        } else if (resolucionBD.isTipoRetiroCiclo() || resolucionBD.isTipoAnulacionCiclo()) {
            return Arrays.asList(this.saveRetirosCiclos(resolucionForm, resolucionBD, ds));
        } else if (resolucionBD.isTipoCambioNota()) {
            return Arrays.asList(this.saveCambioNotas(resolucionForm, resolucionBD, ds));

        } else if (Arrays.asList(TRAS_INT.name(), TRAS.name(), INTES.name(), ING_HIS.name()).contains(resolucionBD.getTipoResolucion().getCodigo())) {
            this.saveTramitesTraslado(resolucionForm, resolucionBD, ds);
        } else if (resolucionBD.isTipoCursoDirigido()) {
            return this.updateCursosDirigidos(resolucionForm, resolucionBD, usuario, ds);
        } else if (resolucionBD.isTipoNotaBaja()) {
            return Arrays.asList(this.saveNotasMasBajas(resolucionForm, resolucionBD, usuario, ds));
        }
        return Arrays.asList("");
    }

    private String saveReincorporaciones(Resolucion resolucionForm, Resolucion resolucionBD, DataSessionPivot ds) {
        List<Alumno> alumnos = new ArrayList();
        Map<Long, Long> couterMap = resolucionForm.getReincorporaciones().stream().collect(Collectors.groupingBy(e -> e.getAlumno().getId(), Collectors.counting()));
        for (Long count : couterMap.values()) {
            Assert.isFalse(count > 1, "Está repitiendo alumno");
        }
        List<Reincorporacion> reincorporacions = reincorporacionDAO.allByCicloReincorporacion(ds.getCicloAcademico());
        Map<Long, Reincorporacion> map = TypesUtil.convertListToMap("alumno.id", reincorporacions);

        EstadoTramite estadoTramiteAceptado = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL_ACEP);
        EstadoTramite estadoTramiteRechazado = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RCHR);
        for (Reincorporacion reincorporacioneForm : resolucionForm.getReincorporaciones()) {
            if (reincorporacioneForm.getId() != null) {
                continue;
            }

//            reincorporacioneForm.setCicloReincorporacion(resolucionForm.getCicloAplica());
            Reincorporacion reincorporacion = map.get(reincorporacioneForm.getAlumno().getId());
            if (reincorporacion == null) {
                throw new PhobosException("El alumno " + reincorporacioneForm.getAlumno().getCodigo() + " no cuenta con un trámite de reincorporación en el ciclo" + ds.getCicloAcademico().getCodigo());
            }

            reincorporacion.setAceptado(reincorporacioneForm.isSeleccionado() ? 1 : 0);
            reincorporacion.setResolucion(resolucionBD);
            reincorporacion.setEstadoTramite(reincorporacioneForm.isSeleccionado() ? estadoTramiteAceptado : estadoTramiteRechazado);
            reincorporacionDAO.update(reincorporacion);

            Tramite tramite = reincorporacion.getTramite();
            tramite.setEstadoEnum(reincorporacioneForm.isSeleccionado() ? TramiteEstadoEnum.ACEP : TramiteEstadoEnum.RCHR);
            tramite.setEstadoTramite(reincorporacioneForm.isSeleccionado() ? estadoTramiteAceptado : estadoTramiteRechazado);
            tramiteDAO.update(tramite);
            alumnos.add(reincorporacion.getAlumno());
        }

        String token = RandomStringUtils.randomAlphanumeric(43);
        String tokenProm = token + TOKEN_PROMEDIOS;
        String tokenCurri = token + TOKEN_CURRICULA;
        String tokenMatri = token + TOKEN_MATRICULABLE;

        visorCalculoNotas.createToken(tokenProm, alumnos);
        visorCalculoNotas.createToken(tokenCurri, alumnos);
        visorCalculoNotas.createToken(tokenMatri, alumnos);

        return token;
    }

    private String saveRetirosCiclos(Resolucion resolucionForm, Resolucion resolucionBD, DataSessionPivot ds) {
        List<Alumno> alumnos = new ArrayList<>();
        EstadoTramite estadoTramiteAcep = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ACEP);
        EstadoTramite estadoTramiteRechz = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RCHR);

        for (RetiroCiclo retiroCicloForm : resolucionForm.getRetiroCiclo()) {
            CicloAcademico cicloAplica = null;
            if (retiroCicloForm.getId() != null) {
                continue;
            }
            Alumno alumnoDB = alumnoDAO.find(retiroCicloForm.getAlumno());
            RetiroCiclo retiroCicloDB = null;
            MatriculaResumen matriculaResumen = null;
            if (resolucionBD.isTipoRetiroCiclo()) {
                retiroCicloDB = retiroCicloDAO.findByExcepcional(alumnoDB);
                if (retiroCicloDB == null) {
                    throw new PhobosException("El alumno " + retiroCicloForm.getAlumno().getCodigo() + " no cuenta con un trámite de retiro ciclo.");
                }
                cicloAplica = retiroCicloDB.getCicloAcademico();
                matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumnoDB, cicloAplica);
                if (retiroCicloForm.getSeleccionado()) {

                    matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.RCI);
                    matriculaResumenDAO.updateColumns(matriculaResumen, "estado");

                    List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allActivoByAlumnoCiclo(alumnoDB, cicloAplica);
                    for (MatriculaCurso matriculaCurso : matriculaCursos) {
                        matriculaCurso.setFechaAnula(new Date());
                        matriculaCurso.setUserAnula(ds.getUsuario());
                        matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.RCI);
                        matriculaCursoDAO.update(matriculaCurso);
                    }

                    ModalidadEstudioEnum modalidadEnum = alumnoDB.getModalidadEstudio().getOperativeModalidadEnum();
                    cicloAplica = cicloAcademicoDAO.findByCodigoCicloModalidadEnum(cicloAplica.getCodigo(), modalidadEnum);
                    retiroCicloDB.setEstadoEnum(ACEP);
                    retiroCicloDB.setEstadoTramite(estadoTramiteAcep);
                    retiroCicloDB.setCicloAcademico(cicloAplica);

                    Tramite tramite = retiroCicloDB.getTramite();
                    tramite.setEstadoEnum(TramiteEstadoEnum.ACEP);
                    tramite.setEstadoTramite(estadoTramiteAcep);
                    tramite.setFechaModificacion(new Date());
                    tramite.setUserModificacion(ds.getUsuario());
                    tramiteDAO.update(tramite);
                } else {
                    retiroCicloDB.setEstadoEnum(TramiteEstadoEnum.RCHR);
                    retiroCicloDB.setEstadoTramite(estadoTramiteRechz);

                    Tramite tramite = retiroCicloDB.getTramite();
                    tramite.setEstadoEnum(TramiteEstadoEnum.RCHR);
                    tramite.setEstadoTramite(estadoTramiteRechz);
                    tramite.setFechaModificacion(new Date());
                    tramite.setUserModificacion(ds.getUsuario());
                    tramiteDAO.update(tramite);

                    continue;
                }
            } else {
                cicloAplica = resolucionForm.getCicloAplica();
                retiroCicloDB = retiroCicloDAO.findByAlumnoCicloRetiro(alumnoDB, cicloAplica);
                if (retiroCicloDB != null) {
                    throw new PhobosException("El alumno " + retiroCicloForm.getAlumno().getCodigo() + "ya no cuenta con un trámite de anulación de ciclo.");
                }
                matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumnoDB, cicloAplica);
                matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.ANCI);
                matriculaResumenDAO.updateColumns(matriculaResumen, "estado");

                List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allMatriculadosByAlumnoCiclo(alumnoDB, cicloAplica);

                for (MatriculaCurso matriculaCurso : matriculaCursos) {
                    matriculaCurso.setFechaAnula(new Date());
                    matriculaCurso.setUserAnula(ds.getUsuario());
                    matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.ANCI);
                    matriculaCursoDAO.update(matriculaCurso);
                }

                DateTime today = new DateTime();
                TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM_ANU_CICLO);
                SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());

                TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.ANCI.name());

                Tramite tramite = new Tramite();
                tramite.setActivo(true);
                tramite.setCompania(ds.getCompania());
                tramite.setAlumno(alumnoDB);
                tramite.setCicloAcademico(ds.getCicloAcademico());
                tramite.setEstadoEnum(TramiteEstadoEnum.ACEP);
                tramite.setEstadoTramite(estadoTramiteAcep);
                tramite.setFechaRegistro(new Date());
                tramite.setPersona(alumnoDB.getPersona());
                tramite.setTipoTramite(tipoTramite);
                tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
                tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
                tramite.setUserRegistro(ds.getUsuario());
                tramiteDAO.save(tramite);

                retiroCicloDB = new RetiroCiclo();
                retiroCicloDB.setAlumno(retiroCicloForm.getAlumno());
                retiroCicloDB.setMotivo(retiroCicloForm.getMotivo());
                retiroCicloDB.setCicloAcademico(cicloAplica);
                retiroCicloDB.setTipoEnum(TipoRetiroCicloEnum.RESEPG);
                retiroCicloDB.setEstadoEnum(TramiteEstadoEnum.ACEP);
                retiroCicloDB.setCicloRegistro(ds.getCicloAcademico());
                retiroCicloDB.setUsuario(ds.getUsuario());
                retiroCicloDB.setEsCondicional(false);
                retiroCicloDB.setTramite(tramite);
                retiroCicloDB.setResolucion(resolucionBD);
                retiroCicloDB.setFechaRegistro(new Date());
                retiroCicloDAO.save(retiroCicloDB);
            }

            List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumnoDescRegular(alumnoDB);
            List<CicloAcademico> ciclo = alumnoCiclos.stream().map(x -> x.getCicloAcademico()).collect(Collectors.toList());
            Boolean exist = false;
            for (CicloAcademico cicloAcademico : ciclo) {
                if (Objects.equals(cicloAcademico.getId(), cicloAplica.getId())) {
                    exist = true;
                    break;
                }
            }
            Assert.isTrue(exist, "El alumno " + alumnoDB.getPersona().getApellidosNombres() + " no tiene actividad en el ciclo " + cicloAplica.getDescripcion());

            AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumnoDB, cicloAplica);
            if (resolucionBD.isTipoRetiroCiclo()) {
                alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.RCI);
            } else if (resolucionBD.isTipoAnulacionCiclo()) {
                alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.ANCI);
            }
            alumnoCicloDAO.update(alumnoCiclo);

            List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivoByAlumnoCiclo(alumnoCiclo);
            for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
                Integer count = alumnoCicloCurso.getVecesCursado() - 1;
                alumnoCicloCurso.setVecesCursado(count);
                if (cicloAplica.isTipoRegular()) {
                    Integer countRegu = alumnoCicloCurso.getVecesCursadoRegular() - 1;
                    alumnoCicloCurso.setVecesCursadoRegular(countRegu);
                }
                if (resolucionBD.isTipoRetiroCiclo()) {
                    alumnoCicloCurso.setEstadoEnum(EstadoMatriculaEnum.RCI);
                } else if (resolucionBD.isTipoAnulacionCiclo()) {
                    alumnoCicloCurso.setEstadoEnum(EstadoMatriculaEnum.ANCI);
                }
                alumnoCicloCursoDAO.update(alumnoCicloCurso);
            }

            alumnos.add(alumnoDB);
        }
        String token = RandomStringUtils.randomAlphanumeric(43);
        String tokenProm = token + TOKEN_PROMEDIOS;
        String tokenCurri = token + TOKEN_CURRICULA;

        visorCalculoNotas.createToken(tokenProm, alumnos);
        visorCalculoNotas.createToken(tokenCurri, alumnos);

        return token;
    }

    private String saveCambioNotas(Resolucion resolucionForm, Resolucion resolucionBD, DataSessionPivot ds) {
        List<Alumno> alumnos = new ArrayList<>();
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL_ACEP);
        for (CambioNota cambioNota : resolucionForm.getCambioNota()) {

            if (cambioNota.getId() != null) {
                continue;
            }

            cambioNota.setCicloAcademico(resolucionForm.getCicloAplica());

            Tramite tramite = new Tramite();
            DateTime today = new DateTime();
            TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
            SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
            TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.CAM_NOTA.name());
            Alumno alumno = alumnoDAO.find(cambioNota.getAlumno());

            tramite.setActivo(true);
            tramite.setCompania(ds.getCompania());
            tramite.setAlumno(alumno);
            tramite.setCicloAcademico(ds.getCicloAcademico());
            tramite.setEstadoEnum(TramiteEstadoEnum.ACEP);
            tramite.setEstadoTramite(estadoTramite);
            tramite.setFechaRegistro(new Date());
            tramite.setPersona(alumno.getPersona());
            tramite.setTipoTramite(tipoTramite);
            tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
            tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
            tramite.setUserRegistro(ds.getUsuario());
            tramiteDAO.save(tramite);

            CambioNota cambioNotaNew = new CambioNota();
            cambioNotaNew.setAlumno(alumno);
            cambioNotaNew.setCicloRegistro(ds.getCicloAcademico());
            cambioNotaNew.setEstado(TramiteEstadoEnum.ACEP);
            cambioNotaNew.setMotivo(cambioNota.getMotivo());
            cambioNotaNew.setTramite(tramite);
            cambioNotaNew.setUsuario(ds.getUsuario());
            cambioNotaNew.setCurso(cambioNota.getCurso());
            cambioNotaNew.setNota(cambioNota.getNota());
            cambioNotaNew.setCicloAcademico(cambioNota.getCicloAcademico());
            cambioNotaNew.setResolucion(resolucionBD);
            cambioNotaNew.setFechaRegistro(new Date());
            cambioNotaNew.setAceptado(Boolean.TRUE);
            cambioNotaNew.setEsCondicional(Boolean.FALSE);
            cambioNotaDAO.save(cambioNotaNew);

            AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, cambioNota.getCicloAcademico(), cambioNota.getCurso());

            AlumnoCicloCurso alumnoCicloCursosMod = new AlumnoCicloCurso();
            alumnoCicloCursosMod.setAlumnoCiclo(alumnoCicloCurso.getAlumnoCiclo());
            alumnoCicloCursosMod.setCreditos(alumnoCicloCurso.getCreditos());
            alumnoCicloCursosMod.setCurso(alumnoCicloCurso.getCurso());
            alumnoCicloCursosMod.setCursoEquivalente(alumnoCicloCurso.getCursoEquivalente());
            alumnoCicloCursosMod.setEstaAprobado(evaluateEstaAprobado(cambioNota.getNota(), alumno));
            alumnoCicloCursosMod.setEstadoEnum(alumnoCicloCurso.getEstadoEnum());
            alumnoCicloCursosMod.setFechaMigracion(alumnoCicloCurso.getFechaMigracion());
            alumnoCicloCursosMod.setFechaRegistro(new Date());
            alumnoCicloCursosMod.setNota(cambioNota.getNota().toString());
            alumnoCicloCursosMod.setRegistroActivo(1);
            alumnoCicloCursosMod.setTipoCursoCurricula(alumnoCicloCurso.getTipoCursoCurricula());
            alumnoCicloCursosMod.setUsuarioRegistro(ds.getUsuario());
            alumnoCicloCursosMod.setVecesCursado(alumnoCicloCurso.getVecesCursado());
            alumnoCicloCursosMod.setOrigenData(OrigenDataSituacionAcademicaEnum.MOD);
            alumnoCicloCursoDAO.save(alumnoCicloCursosMod);

            alumnoCicloCurso.setEstadoEnum(EstadoMatriculaEnum.NMOD);
            alumnoCicloCurso.setFechaModificacion(new Date());
            alumnoCicloCurso.setUserModificacion(ds.getUsuario());
            alumnoCicloCurso.setRegistroActivo(0);
            alumnoCicloCursoDAO.update(alumnoCicloCurso);

            alumnos.add(alumno);
        }
        String token = RandomStringUtils.randomAlphanumeric(43);
        String tokenProm = token + TOKEN_PROMEDIOS;
        String tokenCurri = token + TOKEN_CURRICULA;

        visorCalculoNotas.createToken(tokenProm, alumnos);
        visorCalculoNotas.createToken(tokenCurri, alumnos);

        return token;
    }

    private void saveTramitesTraslado(Resolucion resolucionForm, Resolucion resolucionBD, DataSessionPivot ds) {

        for (TramiteTraslado tramiteTraslado : resolucionForm.getTramiteTraslado()) {

            if (tramiteTraslado.getId() != null) {
//                tramiteTrasladoDAO.update(tramiteTraslado);
                continue;
            }

            Tramite tramite = new Tramite();
            DateTime today = new DateTime();
            TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
            SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
            TipoTramite tipoTramite = null;
            if (resolucionForm.getTipoResolucion().getCodigo().equals(INTES.name())) {
                tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.INTES.name());
            } else if (resolucionForm.getTipoResolucion().getCodigo().equals(TRAS.name())) {
                tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.TRAS.name());
            } else if (resolucionForm.getTipoResolucion().getCodigo().equals(ING_HIS.name())) {
                tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.ING_HIS.name());
            }
            Alumno alumno = alumnoDAO.find(tramiteTraslado.getAlumno());

            tramite.setActivo(true);
            tramite.setCompania(ds.getCompania());
            tramite.setAlumno(alumno);
            tramite.setCicloAcademico(ds.getCicloAcademico());
            tramite.setEstadoEnum(tramiteTraslado.getSeleccionado() ? TramiteEstadoEnum.ACEP : TramiteEstadoEnum.RCHZ);
            tramite.setFechaRegistro(new Date());
            tramite.setPersona(alumno.getPersona());
            tramite.setTipoTramite(tipoTramite);
            tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
            tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
            tramite.setUserRegistro(ds.getUsuario());
            tramiteDAO.save(tramite);

            tramiteTraslado.setTramite(tramite);
            tramiteTraslado.setResolucion(resolucionBD);
            tramiteTraslado.setFechaRegistro(new Date());
            if (resolucionForm.getTipoResolucion().getCodigo().equals(TipoResolucionEnum.TRAS.name())) {
                tramiteTraslado.setTipoTramiteTrasladoEnum(TipoTramiteTrasladoEnum.TRAS);
            } else if (resolucionForm.getTipoResolucion().getCodigo().equals(TipoResolucionEnum.INTES.name())) {
                tramiteTraslado.setTipoTramiteTrasladoEnum(TipoTramiteTrasladoEnum.INTES);
            } else if (resolucionForm.getTipoResolucion().getCodigo().equals(TipoResolucionEnum.ING_HIS.name())) {
                tramiteTraslado.setTipoTramiteTrasladoEnum(TipoTramiteTrasladoEnum.ING_HIS);
            } else if (resolucionForm.getTipoResolucion().getCodigo().equals(TipoResolucionEnum.TRAS_INT.name())) {

                tramiteTraslado.setTipoTramiteTrasladoEnum(TipoTramiteTrasladoEnum.TRAS_INT);

                if (tramiteTraslado.getSeleccionado()) {

                    tramiteTraslado.setCarreraOrigen(alumno.getCarrera());
                    alumno.setCarrera(tramiteTraslado.getCarrera());

                    OrientacionCarrera orientacionCarrera = alumno.getOrientacionCarrera();
                    List<PlanCurricular> planCurriculars = planCurricularDAO.allActivoByCarreraOrientacion(tramiteTraslado.getCarrera());
                    Map<String, List<PlanCurricular>> mapPlanesByCiclo = TypesUtil.convertListToMapList("cicloInicioVigencia.codigo", planCurriculars);
                    Map<String, CicloAcademico> mapCiclosPlanes = TypesUtil.convertListToMap("cicloInicioVigencia.codigo", "cicloInicioVigencia", planCurriculars);
                    String codigoCicloAlumno = (String) ObjectUtil.getParentTree(alumno, "cicloIngreso.codigo");

                    List<String> codigosCiclosPlanes = new ArrayList<String>(mapCiclosPlanes.keySet());

                    Collections.sort(codigosCiclosPlanes);
                    Collections.reverse(codigosCiclosPlanes);

                    String codigoCicloPlan = this.getIndiceCicloAcademico(codigoCicloAlumno, codigosCiclosPlanes);
                    List<PlanCurricular> planesBD = mapPlanesByCiclo.get(codigoCicloPlan);
                    PlanCurricular planCurricularBD = null;
                    for (PlanCurricular planCurricular : planesBD) {
                        if (planCurricular.getOrientacionCarrera() == null) {
                            planCurricularBD = planCurricular;
                            alumno.setOrientacionCarrera(null);
                            break;
                        } else {
                            if (orientacionCarrera != null && Objects.equals(planCurricular.getOrientacionCarrera().getId(), orientacionCarrera.getId())) {
                                alumno.setOrientacionCarrera(planCurricular.getOrientacionCarrera());
                                planCurricularBD = planCurricular;
                            }
                        }
                    }
//                    if (orientacionCarrera != null) {
//                    } else {
//                        planCurricularBD = planesBD.get(0);
//                    }
                    alumno.setPlanCurricular(planCurricularBD);
                    alumnoDAO.updateColumns(alumno, "carrera", "planCurricular");
                }
            }
            tramiteTraslado.setUserRegistro(ds.getUsuario());
            tramiteTraslado.setEstado(tramiteTraslado.getSeleccionado() ? ACEP.name() : RCHZ.name());
            tramiteTrasladoDAO.save(tramiteTraslado);
        }
    }

    private List<String> updateCursosDirigidos(Resolucion resolucionForm, Resolucion resolucionBD, Usuario usuario, DataSessionPivot ds) {
        List<String> msg = new ArrayList();

        Assert.isFalse(resolucionForm.getCursoDirigido().isEmpty(), "Debe Agregar alumnos.");
        List<CursoDirigido> cursoDirigidos = cursoDirigidoDAO.allByCicloAcademicoSol(ds.getCicloAcademico());
        Map<Long, CursoDirigido> map = TypesUtil.convertListToMap("tramite.alumno.id", cursoDirigidos);
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RES_FAC);
        EstadoTramite estadoTramiteRech = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RHZ_SOL);

        List<Alumno> alumnos = resolucionForm.getCursoDirigido().stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allByAlumnosCicloActivo(alumnos);
        Map<Long, List<MatriculaCurso>> mapMatriculaCursos = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", matriculaCursos);

        for (CursoDirigido cursoDirigidoForm : resolucionForm.getCursoDirigido()) {
            if (cursoDirigidoForm.getId() != null) {
                continue;
            }
            String message = "";
            CursoDirigido cursoDirigidoTram = map.get(cursoDirigidoForm.getAlumno().getId());
            Assert.isNotNull(cursoDirigidoTram, "El alumno " + cursoDirigidoForm.getAlumno().getCodigo() + " no cuenta con un tramite de curso dirigido.");

            List<MatriculaCurso> matriculasCursoAlumno = mapMatriculaCursos.get(cursoDirigidoForm.getAlumno().getId());
            if (matriculasCursoAlumno != null
                    && matriculasCursoAlumno.stream().filter(x -> x.getEstadoEnum() == EstadoMatriculaEnum.MAT && Objects.equals(x.getCurso().getId(), cursoDirigidoTram.getCurso().getId())).findAny().orElse(null) != null) {
                message = "El alumno " + cursoDirigidoForm.getAlumno().getCodigo() + " está matriculado en el curso " + cursoDirigidoTram.getCurso().getNombre();
                msg.add(message);
            }
        }
        if (!msg.isEmpty()) {
            return msg;
        }

        for (CursoDirigido cursoDirigidoForm : resolucionForm.getCursoDirigido()) {

            if (cursoDirigidoForm.getId() != null) {
                continue;
            }

            EstadoTramite estado = cursoDirigidoForm.getSeleccionado() ? estadoTramite : estadoTramiteRech;
            TramiteEstadoEnum estadotram = cursoDirigidoForm.getSeleccionado() ? TramiteEstadoEnum.ACEP : TramiteEstadoEnum.RCHZ;
            CursoDirigido cursoDirigidoTram = map.get(cursoDirigidoForm.getAlumno().getId());

            cursoDirigidoTram.setMotivoRechazo(cursoDirigidoTram.getMotivoRechazo());
            cursoDirigidoTram.setResolucion(resolucionBD);
            cursoDirigidoTram.setDocenteAsignado(cursoDirigidoForm.getDocenteAsignado());
            cursoDirigidoTram.setEstado(estado);
            cursoDirigidoDAO.update(cursoDirigidoTram);

            Tramite tramite = cursoDirigidoTram.getTramite();
            tramite.setEstadoEnum(estadotram);
            tramiteDAO.update(tramite);

            if (!cursoDirigidoForm.getSeleccionado()) {
                continue;
            }
            AnexoBoletin anexoBoletin = anexoBoletinDAO.findDepartamento(cursoDirigidoTram.getCurso().getDepartamentoAcademico());
            Assert.isNotNull(anexoBoletin, "No existe el anexo boletín para el departamento " + cursoDirigidoTram.getCurso().getDepartamentoAcademico().getNombre());
            List<GrupoSeccion> grupoSeccions = null;
            GrupoSeccion grupoSeccion = gpoSeccionService.findByCursoAndDocenteDirigido(cursoDirigidoTram.getCurso(), cursoDirigidoTram.getDocenteAsignado(), ds.getCicloAcademico());
            if (grupoSeccion == null) {
                grupoSeccion = new GrupoSeccion();
                grupoSeccion.setCantidad(1);
                grupoSeccion.setCursoDirigido(Boolean.TRUE);
                grupoSeccion.setCurso(cursoDirigidoTram.getCurso());
                grupoSeccion.setDocenteResponsable(cursoDirigidoTram.getDocenteAsignado());
                grupoSeccion.setAnexoBoletin(anexoBoletin);
                grupoSeccions = gpoSeccionService.saveGpoSeccionHeader(grupoSeccion, ds.getCicloAcademico(), ds);
            } else {
                grupoSeccions = new ArrayList<>();
                grupoSeccions.add(grupoSeccion);
            }
            this.matricular(grupoSeccions.get(0), cursoDirigidoTram.getTramite().getAlumno(), cursoDirigidoTram.getCurso(), ds.getUsuario(), ds.getCicloAcademico(), mapMatriculaCursos);
        }

        return msg;
    }

    private String saveNotasMasBajas(Resolucion resolucionForm, Resolucion resolucionBD, Usuario usuario, DataSessionPivot ds) {
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL_ACEP);
        List<Alumno> alumnos = new ArrayList<>();
        for (CambioNotaMasBaja cambioNotaMasBaja : resolucionForm.getCambioNotaMasBajas()) {
            if (cambioNotaMasBaja.getId() != null) {
                continue;
            }

            Alumno alumno = alumnoDAO.find(cambioNotaMasBaja.getAlumno());

            Tramite tramite = new Tramite();
            DateTime today = new DateTime();
            TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
            SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), usuario);
            TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.NOTA_BAJA.name());

            tramite.setActivo(true);
            tramite.setCompania(ds.getCompania());
            tramite.setAlumno(alumno);
            tramite.setCicloAcademico(ds.getCicloAcademico());
            tramite.setEstadoEnum(TramiteEstadoEnum.ACEP);
            tramite.setEstadoTramite(estadoTramite);
            tramite.setFechaRegistro(new Date());
            tramite.setPersona(alumno.getPersona());
            tramite.setTipoTramite(tipoTramite);
            tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
            tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
            tramite.setUserRegistro(usuario);
            tramiteDAO.save(tramite);

            cambioNotaMasBaja.setCicloAcademico(cambioNotaMasBaja.getAlumnoCicloCursoBean().getCicloAcademico());
            cambioNotaMasBaja.setCurso(cambioNotaMasBaja.getAlumnoCicloCursoBean().getCurso());
            cambioNotaMasBaja.setAlumno(cambioNotaMasBaja.getAlumno());
            cambioNotaMasBaja.setEstadoTramite(estadoTramite);
            cambioNotaMasBaja.setNotaAnulada(cambioNotaMasBaja.getAlumnoCicloCursoBean().getNota());
            cambioNotaMasBaja.setResolucion(resolucionBD);
            cambioNotaMasBaja.setTramite(tramite);
            cambioNotaMasBajaDAO.save(cambioNotaMasBaja);

            AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, cambioNotaMasBaja.getCicloAcademico(), cambioNotaMasBaja.getCurso());
            alumnoCicloCurso.setEstadoEnum(EstadoMatriculaEnum.ANMB);
            alumnoCicloCurso.setUserModificacion(usuario);
            alumnoCicloCurso.setFechaModificacion(new Date());
            alumnoCicloCursoDAO.updateColumns(alumnoCicloCurso, "estado", "userModificacion", "fechaModificacion");

            alumnos.add(alumno);
        }

        String token = RandomStringUtils.randomAlphanumeric(43);
        String tokenProm = token + TOKEN_PROMEDIOS;
        String tokenCurri = token + TOKEN_CURRICULA;

        visorCalculoNotas.createToken(tokenProm, alumnos);
        visorCalculoNotas.createToken(tokenCurri, alumnos);

        return token;
    }

    @Override
    @Transactional
    public void saveTramiteBachiller(Resolucion resolucionForm, DataSessionPivot ds) {

        TipoResolucion tipoResolucion = tipoResolucionDAO.finByCodigo(TipoResolucionEnum.BACHI);
        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setNumeroVisible(resolucion.getDescripcion());
        resolucion.setCicloAplica(resolucionForm.getCicloAplica());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setTipoResolucion(tipoResolucion);
        resolucion.setUserRegistro(ds.getUsuario());
        resolucion.setAplicacionDirecta(1l);
        resolucionDAO.save(resolucion);

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ACEP);
        EstadoTramite estadoTramiteRech = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RHZ_SOL);

        EventoCicloAcademico eventoCicloAcademico = eventoCicloAcademicoDAO.findByCicloAndEvento(ds.getCicloAcademico(), EventoAcademicoEnum.FECHAS_BACH);
        Assert.isNotNull(eventoCicloAcademico, "No se ha configurado las fechas de inicio y fin de ciclo");
        List<Alumno> alumnos = resolucionForm.getTramiteBachiller().stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<AlumnoCicloCurso> alumnosCiclosCursosActivos = alumnoCicloCursoDAO.allOperativesByAlumnos(alumnos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosActivos);
        for (TramiteBachiller bachiller : resolucionForm.getTramiteBachiller()) {

            TramiteBachiller tramiteBachiller = tramiteBachillerDAO.findByAlumnoAct(bachiller.getAlumno());
            Assert.isNotNull(tramiteBachiller, "El alumno no tiene un trámite bachiller");

            tramiteBachiller.setResolucion(resolucion);
            tramiteBachiller.setEstado(bachiller.getSeleccionado() ? TramiteEstadoEnum.ACEP.name() : TramiteEstadoEnum.RCHZ.name());
            tramiteBachiller.setFechaResolucion(new Date());
            tramiteBachiller.setUsuarioResolucion(ds.getUsuario());
            tramiteBachillerDAO.update(tramiteBachiller);

            Tramite tramite = tramiteBachiller.getTramite();
            tramite.setEstadoEnum(bachiller.getSeleccionado() ? TramiteEstadoEnum.ACEP : TramiteEstadoEnum.RCHZ);
            tramite.setEstadoTramite(bachiller.getSeleccionado() ? estadoTramite : estadoTramiteRech);
            tramiteDAO.update(tramite);

            if (tramite.getEstadoEnum() == TramiteEstadoEnum.ACEP) {
                Alumno alumno = alumnoDAO.find(tramiteBachiller.getTramite().getAlumno());
                GradoAcademico gradoAcademico = gradoAcademicoDAO.findByTipoAndCarrera(TipoGradoAcademicoEnum.BACH, alumno.getCarrera());
                ObtencionGrado obtencionGrado = new ObtencionGrado();
                obtencionGrado.setAlumno(alumno);
                obtencionGrado.setCicloAcademico(ds.getCicloAcademico());
                obtencionGrado.setEstadoTramite(tramite.getEstadoTramite());
                obtencionGrado.setFechaRegistro(new Date());
                obtencionGrado.setGradoAcademico(gradoAcademico);
                obtencionGrado.setResolucion(resolucion);
                obtencionGrado.setFechaObtencion(resolucion.getFecha());
                obtencionGrado.setTramite(tramite);
                obtencionGrado.setUserObtencion(ds.getUsuario());
                obtencionGrado.setUserRegistro(ds.getUsuario());
                obtencionGradoDAO.save(obtencionGrado);

                BigDecimal sumNotasCreditos = BigDecimal.ZERO;
                BigDecimal sumCreditos = BigDecimal.ZERO;

                List<AlumnoCicloCurso> alumnoCicloCursos = mapAlumnoCicloCurso.get(alumno.getId());
                AlumnoCiclo alumnoCiclo = new AlumnoCiclo();
                for (AlumnoCicloCurso cursoAluCicloEach : alumnoCicloCursos) {
                    alumnoCiclo = cursoAluCicloEach.getAlumnoCiclo();
                    if (cursoAluCicloEach.getCreditos() > 0
                            && cursoAluCicloEach.isAprobado()
                            && !Arrays.asList("AP", "TE").contains(cursoAluCicloEach.getNota())) {

                        BigDecimal notaBig = TypesUtil.getBigDecimal(cursoAluCicloEach.getNota());
                        BigDecimal creditosBig = TypesUtil.getBigDecimal(cursoAluCicloEach.getCreditos());

                        sumNotasCreditos = sumNotasCreditos.add(notaBig.multiply(creditosBig));
                        sumCreditos = sumCreditos.add(creditosBig);

                    }
                }
                BigDecimal ppg = sumNotasCreditos.divide(sumCreditos, 2, RoundingMode.HALF_UP);
                Egresado egresado = egresadoDAO.findByAlumno(alumno);
                egresado.setAlumno(alumno);
                egresado.setCarrera(alumno.getCarrera());
                egresado.setCicloAcademico(alumno.getCicloActivoRegular());
                egresado.setFacultad(alumno.getCarrera().getFacultad());
                egresado.setFechaRegistroEgresado(resolucion.getFecha());
                egresado.setUserRegistroEgresado(ds.getUsuario());
                egresado.setFechaEgresado(eventoCicloAcademico.getFechaFin());
                egresado.setGrado(gradoAcademico);
                egresado.setPromedioGraduacion(ppg);
                egresado.setEsPrincipal(1);
                egresado.setCreditosAcumulados(alumnoCiclo.getCreditosAcumulados());
                egresado.setCreditosAprobadosAcumulados(alumnoCiclo.getCreditosAprobadosAcumulados());
                egresado.setPromedioAcumulado(alumnoCiclo.getPromedioAcumulado());
                egresado.setPuntajeAcumulado(alumnoCiclo.getPuntajeAcumulado());
                egresadoDAO.update(egresado);
            }
        }
    }

    @Override
    public List<ObtencionGrado> allTramiteBachiller(Resolucion resolucion) {
        return obtencionGradoDAO.allByResolucion(resolucion);
    }

    @Override
    @Transactional
    public void saveTramiteTitulo(Resolucion resolucionForm, DataSessionPivot ds) {

        TipoResolucion tipoResolucion = tipoResolucionDAO.finByCodigo(TipoResolucionEnum.TITUL);
        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setNumeroVisible(resolucion.getDescripcion());
        resolucion.setCicloAplica(resolucionForm.getCicloAplica());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setTipoResolucion(tipoResolucion);
        resolucion.setUserRegistro(ds.getUsuario());
        resolucion.setAplicacionDirecta(1l);
        resolucionDAO.save(resolucion);

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL_ACEP);
        EstadoTramite estadoTramiteRech = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RHZ_SOL);

        for (TramiteTitulo titulo : resolucionForm.getTramiteTitulos()) {

            TramiteTitulo tramiteTitulo = tramiteTituloDAO.findByAlumnoAct(titulo.getAlumno());
            Assert.isNotNull(tramiteTitulo, "El alumno no tiene un trámite titulo");

            tramiteTitulo.setEstado(titulo.getSeleccionado() ? TramiteEstadoEnum.ACEP.name() : TramiteEstadoEnum.RCHZ.name());
            tramiteTitulo.setFechaResolucion(new Date());
            tramiteTitulo.setUsuarioResolucion(ds.getUsuario());
            tramiteTitulo.setResolucion(resolucion);
            tramiteTituloDAO.update(tramiteTitulo);

            Tramite tramite = tramiteTitulo.getTramite();
            tramite.setEstadoEnum(titulo.getSeleccionado() ? TramiteEstadoEnum.ACEP : TramiteEstadoEnum.RCHZ);
            tramite.setEstadoTramite(titulo.getSeleccionado() ? estadoTramite : estadoTramiteRech);
            tramiteDAO.update(tramite);
            if (tramite.getEstadoEnum() == TramiteEstadoEnum.ACEP) {
                Alumno alumno = tramiteTitulo.getTramite().getAlumno();
                GradoAcademico gradoAcademico = gradoAcademicoDAO.findByTipoAndCarrera(TipoGradoAcademicoEnum.TIT, alumno.getCarrera());
                ObtencionGrado obtencionGrado = new ObtencionGrado();
                obtencionGrado.setAlumno(alumno);
                obtencionGrado.setCicloAcademico(ds.getCicloAcademico());
                obtencionGrado.setEstadoTramite(tramite.getEstadoTramite());
                obtencionGrado.setFechaRegistro(new Date());
                obtencionGrado.setGradoAcademico(gradoAcademico);
                obtencionGrado.setResolucion(resolucion);
                obtencionGrado.setTramite(tramite);
                obtencionGrado.setUserObtencion(ds.getUsuario());
                obtencionGrado.setUserRegistro(ds.getUsuario());
                obtencionGrado.setFechaObtencion(resolucion.getFecha());
                obtencionGradoDAO.save(obtencionGrado);

                Egresado egresado = egresadoDAO.findByAlumno(alumno);
                egresado.setPromedioAcumulado(alumno.getPromedioAcumulado());
                egresado.setFechaTitulacion(resolucion.getFecha());
                egresado.setUserRegistroTitulado(ds.getUsuario());
                egresado.setTitulo(gradoAcademico);
                egresadoDAO.update(egresado);
            }
        }

    }

    @Override
    @Transactional
    public String saveTramitePracticas(Resolucion resolucionForm, DataSessionPivot ds) {
        TipoResolucion tipoResolucion = tipoResolucionDAO.finByCodigo(TipoResolucionEnum.PRACTICAS);
        Resolucion resolucion = new Resolucion();
        resolucion.setOficina(resolucionForm.getOficina());
        resolucion.setFecha(resolucionForm.getFecha());
        resolucion.setNumero(resolucionForm.getNumero());
        resolucion.setSerie(resolucionForm.getSerie());
        resolucion.setNumeroVisible(resolucion.getDescripcion());
        resolucion.setCicloAplica(resolucionForm.getCicloAplica());
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucion.setFechaRegistro(new Date());
        resolucion.setTipoResolucion(tipoResolucion);
        resolucion.setUserRegistro(ds.getUsuario());
        resolucion.setAplicacionDirecta(1l);
        resolucionDAO.save(resolucion);

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL_ACEP);

        List<Alumno> alumnos = new ArrayList<>();
        for (PracticasPreProfesional practicasForm : resolucionForm.getTramitePracticasPreProfesionales()) {

            DateTime today = new DateTime();
            TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM_PRAC_PROF);
            SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
            TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.PRAC_PROF.name());
            Alumno alumno = alumnoDAO.find(practicasForm.getAlumno());

            Tramite tramite = new Tramite();
            tramite.setActivo(true);
            tramite.setCompania(ds.getCompania());
            tramite.setAlumno(alumno);
            tramite.setCicloAcademico(ds.getCicloAcademico());
            tramite.setEstadoEnum(TramiteEstadoEnum.ACEP);
            tramite.setEstadoTramite(estadoTramite);
            tramite.setFechaRegistro(new Date());
            tramite.setPersona(alumno.getPersona());
            tramite.setTipoTramite(tipoTramite);
            tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
            tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
            tramite.setUserRegistro(ds.getUsuario());
            tramiteDAO.save(tramite);

            AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursoCurriculaDAO.findPracticaPreProfesional(alumno);
            Assert.isNotNull(alumnoCursoCurricula, "El alumno no tiene Practicas habilitadas");
            CursoCurricula cursoCurricula = alumnoCursoCurricula.getCursoCurricula();
            PracticasPreProfesional preProfesionales = new PracticasPreProfesional();
            preProfesionales.setAlumno(practicasForm.getAlumno());
            preProfesionales.setCurso(cursoCurricula.getCurso());
            preProfesionales.setResolucion(resolucion);
            preProfesionales.setTramite(tramite);
            preProfesionales.setUsuario(ds.getUsuario());
            preProfesionales.setEstado(EstadoEnum.ACT.name());
            preProfesionales.setFechaRegistro(new Date());
            practicaPreProfesionalesDAO.save(preProfesionales);

            AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findLastActiveRegByAlumno(alumno);
            AlumnoCicloCurso alumnoCicloCurso = new AlumnoCicloCurso();
            alumnoCicloCurso.setAlumnoCiclo(alumnoCiclo);
            alumnoCicloCurso.setCreditos(cursoCurricula.getCreditos());
            alumnoCicloCurso.setCurso(cursoCurricula.getCurso());
            alumnoCicloCurso.setEstaAprobado(1);
            alumnoCicloCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
            alumnoCicloCurso.setFechaRegistro(new Date());
            alumnoCicloCurso.setNota("AP");
            alumnoCicloCurso.setOrigenData(OrigenDataSituacionAcademicaEnum.RES);
            alumnoCicloCurso.setRegistroActivo(1);
            alumnoCicloCurso.setTipoCursoCurricula(cursoCurricula.getTipoCursoCurricula());
            alumnoCicloCurso.setVecesCursado(1);
            alumnoCicloCurso.setVecesCursadoRegular(1);
            alumnoCicloCurso.setUsuarioRegistro(ds.getUsuario());
            alumnoCicloCursoDAO.save(alumnoCicloCurso);

            alumnos.add(alumno);
        }
        String token = RandomStringUtils.randomAlphanumeric(43);
        String tokenProm = token + TOKEN_PROMEDIOS;
        String tokenCurri = token + TOKEN_CURRICULA;

        visorCalculoNotas.createToken(tokenProm, alumnos);
        visorCalculoNotas.createToken(tokenCurri, alumnos);

        return token;
    }

    @Transactional
    private void saveTramitesTrasladoInterno(Resolucion resolucionForm, Resolucion resolucion, DataSessionPivot ds) {
        for (TramiteTraslado tramiteTrasladoForm : resolucionForm.getTramiteTraslado()) {
            if (tramiteTrasladoForm.getId() != null) {
//                tramiteTrasladoDAO.update(tramiteTraslado);
                continue;
            }
            TramiteTraslado traslado = tramiteTrasladoDAO.findByAlumnoCiclo(tramiteTrasladoForm.getAlumno(), ds.getCicloAcademico());
            Assert.isTrue(traslado != null, "El alumno" + tramiteTrasladoForm.getAlumno().getCodigo() + " no cuenta con una solicitud pendiente.");
            Tramite tramite = traslado.getTramite();
            tramite.setEstadoEnum(tramiteTrasladoForm.getSeleccionado() ? TramiteEstadoEnum.ACEP : TramiteEstadoEnum.RCHZ);
            tramite.setUserModificacion(ds.getUsuario());
            tramite.setFechaModificacion(new Date());
            tramiteDAO.updateEstado(tramite);

            traslado.setResolucion(resolucion);
            traslado.setEstado(tramiteTrasladoForm.getSeleccionado() ? TramiteEstadoEnum.ACEP.name() : TramiteEstadoEnum.RCHZ.name());
            tramiteTrasladoDAO.updateColumns(traslado, "estado", "resolucion");

            if (tramiteTrasladoForm.getSeleccionado()) {

                Alumno alumno = tramite.getAlumno();
                alumno.setCarrera(traslado.getCarrera());

                OrientacionCarrera orientacionCarrera = alumno.getOrientacionCarrera();
                List<PlanCurricular> planCurriculars = planCurricularDAO.allActivoByCarreraOrientacion(traslado.getCarrera());
                Map<String, List<PlanCurricular>> mapPlanesByCiclo = TypesUtil.convertListToMapList("cicloInicioVigencia.codigo", planCurriculars);
                Map<String, CicloAcademico> mapCiclosPlanes = TypesUtil.convertListToMap("cicloInicioVigencia.codigo", "cicloInicioVigencia", planCurriculars);
                String codigoCicloAlumno = (String) ObjectUtil.getParentTree(alumno, "cicloIngreso.codigo");

                List<String> codigosCiclosPlanes = new ArrayList<String>(mapCiclosPlanes.keySet());

                Collections.sort(codigosCiclosPlanes);
                Collections.reverse(codigosCiclosPlanes);

                String codigoCicloPlan = this.getIndiceCicloAcademico(codigoCicloAlumno, codigosCiclosPlanes);
                List<PlanCurricular> planesBD = mapPlanesByCiclo.get(codigoCicloPlan);
                PlanCurricular planCurricularBD = null;
                for (PlanCurricular planCurricular : planesBD) {
                    if (planCurricular.getOrientacionCarrera() == null) {
                        planCurricularBD = planCurricular;
                        alumno.setOrientacionCarrera(null);
                        break;
                    } else {
                        if (orientacionCarrera != null && Objects.equals(planCurricular.getOrientacionCarrera().getId(), orientacionCarrera.getId())) {
                            alumno.setOrientacionCarrera(planCurricular.getOrientacionCarrera());
                            planCurricularBD = planCurricular;
                        }
                    }
                }
//                    if (orientacionCarrera != null) {
//                    } else {
//                        planCurricularBD = planesBD.get(0);
//                    }
                alumno.setPlanCurricular(planCurricularBD);
                alumnoDAO.updateColumns(alumno, "carrera", "planCurricular");
            }

        }
    }

}

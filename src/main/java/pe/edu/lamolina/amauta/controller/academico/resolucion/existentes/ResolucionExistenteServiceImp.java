package pe.edu.lamolina.amauta.controller.academico.resolucion.existentes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.bean.AlumnoCicloCursoBean;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
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
import pe.edu.lamolina.amauta.controller.general.oficina.util.OficinaService;
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
import pe.edu.lamolina.amauta.dao.academico.CursoOpcionalCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.GradoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.posgrado.CambioNotaMasBajaDAO;
import pe.edu.lamolina.amauta.dao.tramite.CambioNotaDAO;
import pe.edu.lamolina.amauta.dao.tramite.CambioPlanCurricularDAO;
import pe.edu.lamolina.amauta.dao.tramite.CursoDirigidoDAO;
import pe.edu.lamolina.amauta.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.ObtencionGradoDAO;
import pe.edu.lamolina.amauta.dao.tramite.ReadmisionDAO;
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
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.TITUL;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.BACHI;
import static pe.edu.lamolina.model.enums.TipoResolucionEnum.PRACTICAS;
import static pe.edu.lamolina.model.enums.TipoTramiteEnum.INTES;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.tramite.CambioPlanCurricular;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.PracticasPreProfesional;
import pe.edu.lamolina.model.tramite.Readmision;
import pe.edu.lamolina.model.tramite.TramiteBachiller;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ResolucionExistenteServiceImp implements ResolucionExistenteService {

    private final AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    private final AlumnoCicloDAO alumnoCicloDAO;
    private final AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;
    private final AlumnoDAO alumnoDAO;
    private final AnexoBoletinDAO anexoBoletinDAO;
    private final CambioNotaDAO cambioNotaDAO;
    private final CambioNotaMasBajaDAO cambioNotaMasBajaDAO;
    private final CambioPlanCurricularDAO cambioPlanCurricularDAO;
    private final CarreraDAO carreraDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final CursoDirigidoDAO cursoDirigidoDAO;
    private final CursoOpcionalCurriculaDAO cursoOpcionalCurriculaDAO;
    private final EgresadoDAO egresadoDAO;
    private final EstadoTramiteDAO estadoTramiteDAO;
    private final EventoCicloAcademicoDAO eventoCicloAcademicoDAO;
    private final GradoAcademicoDAO gradoAcademicoDAO;
    private final MatriculaCursoDAO matriculaCursoDAO;
    private final MatriculaResumenDAO matriculaResumenDAO;
    private final MatriculaSeccionDAO matriculaSeccionDAO;
    private final ObtencionGradoDAO obtencionGradoDAO;
    private final OficinaDAO oficinaDAO;
    private final ReadmisionDAO readmisionDAO;
    private final ReincorporacionDAO reincorporacionDAO;
    private final ResolucionDAO resolucionDAO;
    private final RetiroCicloDAO retiroCicloDAO;
    private final SeccionDAO seccionDAO;
    private final TipoCursoCurriculaDAO tipoCursoCurriculaDAO;
    private final TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;
    private final TipoResolucionDAO tipoResolucionDAO;
    private final TipoTramiteDAO tipoTramiteDAO;
    private final TramiteBachillerDAO tramiteBachillerDAO;
    private final TramiteDAO tramiteDAO;
    private final TramitePracticaPreProfesionalesDAO practicaPreProfesionalesDAO;
    private final TramiteTituloDAO tramiteTituloDAO;
    private final TramiteTrasladoDAO tramiteTrasladoDAO;
    private final VisorCalculoNotas visorCalculoNotas;

    private final AvanceCurricularService avanceCurricularService;
    private final GpoSeccionService gpoSeccionService;
    private final OficinaService oficinaService;
    private final SerieDocumentoService serieDocumentoService;

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
    public List<TramiteTraslado> allTrasladoInterno(CicloAcademico cicloAcademico) {
        List<TramiteTraslado> tramiteTraslados = tramiteTrasladoDAO.allTrasladoInternoByCicloSolicito(cicloAcademico);
        for (TramiteTraslado tramiteTraslado : tramiteTraslados) {
            tramiteTraslado.setAlumno(tramiteTraslado.getTramite().getAlumno());
        }
        return tramiteTraslados;
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
    public List<TramiteTraslado> allTramiteTraslado(Resolucion resolucionDB) {
        return tramiteTrasladoDAO.allByResolucion(resolucionDB);
    }

    @Override
    public List<Carrera> allCarrera() {
        return carreraDAO.allActivasByModalidadEnum(ModalidadEstudioEnum.PRE);
    }

    @Override
    public Resolucion findByResolucion(Long resolucionId, DataSessionPivot ds) {
        return resolucionDAO.findById(resolucionId);
    }

    @Override
    public List<CicloAcademico> ciclosAnteriores(int i) {
        return cicloAcademicoDAO.allUltimosByModalidadEnum(PRE, 50);
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
    public List<CambioNota> allCambioNota(Resolucion resolucionDB) {
        return cambioNotaDAO.allByResolucion(resolucionDB);

    }

    @Override
    public List<CursoDirigido> allCursodirigido(Resolucion resolucionDB) {
        return cursoDirigidoDAO.allByResolucion(resolucionDB);
    }

    @Override
    public List<ObtencionGrado> allObtencionGrado(Resolucion resolucion) {
        return obtencionGradoDAO.allByResolucion(resolucion);
    }

    @Override
    public List<CambioPlanCurricular> allCambioPlanCurricular() {
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL);
        return cambioPlanCurricularDAO.allPendienteByEstado(estadoTramite);
    }

    @Override
    public List<Readmision> allReadmisionByResolucion(Resolucion resolucion) {
        return readmisionDAO.allByResolucion(resolucion);
    }

    @Override
    public List<CambioPlanCurricular> allCambioPlanCurricularByResolucion(Resolucion resolucion) {
        return cambioPlanCurricularDAO.allByResolucion(resolucion);
    }

    @Override
    public List<TramiteBachiller> allTramiteBachiller(Resolucion resolucionDB) {

        return tramiteBachillerDAO.allByResolucion(resolucionDB);
    }

    @Override
    public List<TramiteTitulo> allTramiteTitulo(Resolucion resolucionDB) {
        return tramiteTituloDAO.allByResolucion(resolucionDB);
    }

    @Override
    public List<PracticasPreProfesional> allPracticasPreProfesionales(Resolucion resolucionDB) {
        return practicaPreProfesionalesDAO.allByResolucion(resolucionDB);
    }

    @Override
    public List<TramiteBachiller> allBachiller(DataSessionPivot ds) {
        return tramiteBachillerDAO.allBySolicitados();
    }

    @Override
    public List<TramiteTitulo> allTitulos(DataSessionPivot ds) {
        return tramiteTituloDAO.allBySolicitados();
    }

    @Override
    public List<PracticasPreProfesional> allPracticas(DataSessionPivot ds) {
        return practicaPreProfesionalesDAO.allBySolicitados();
    }

    @Override
    public List<RetiroCiclo> allRetiroCiclo(DataSessionPivot ds) {
        return retiroCicloDAO.allExepcionalByCiclo(ds.getCicloAcademico());
    }

    @Override
    public List<Reincorporacion> allReincorporacion() {
        return reincorporacionDAO.allPendientesByCicloReincorporacion();
    }

    @Override
    public List<TipoResolucion> allTipoResolucionByCodigo(List<TipoResolucionEnum> codigos) {
        return tipoResolucionDAO.allByCodigo(codigos);
    }

    @Override
    public List<Readmision> allReadmision() {
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL);
        return readmisionDAO.allPendienteByEstado(estadoTramite);
    }

    private void requiereCicloAplica(CicloAcademico cicloAplica) {
        if (null == cicloAplica) {
            throw new PhobosException("Es requerido el ciclo de aplicación");
        }
    }

    @Override
    @Transactional
    public List<String> saveResolucion(Resolucion resolucion, DataSessionPivot ds) {

        TipoResolucion tipoResolucion = tipoResolucionDAO.find(resolucion.getTipoResolucion().getId());

        if (tipoResolucion == null) {
            throw new PhobosException("No ha especificado el tipo de resolución.");
        }

        resolucion.setTipoResolucion(tipoResolucion);

        log.debug("oficina {}", resolucion.getOficina().getId());
        log.debug("serie {}", resolucion.getSerie());
        log.debug("numero {}", resolucion.getNumero());
        log.debug("tipoResolucion {}", tipoResolucion.getTipoEnum().name());

        resolucion.setSerie(cleanNumero(resolucion.getSerie()));
        resolucion.setNumero(cleanNumero(resolucion.getNumero()));

        Resolucion resolucionValidacion = resolucionDAO.findByOficinaSerieNumero(resolucion.getOficina(), resolucion.getSerie(), resolucion.getNumero());

        log.debug("tipoResolucion existe {}", resolucionValidacion != null);

        List<String> respuesta = new ArrayList();
        if (resolucionValidacion != null) {
            respuesta.add(String.format("Ya fue registrado una resolución con la serie %s y número %s en la oficina de %s",
                    resolucion.getSerie(),
                    resolucion.getNumero(),
                    resolucion.getOficina().getNombre()));
//            throw new PhobosException(
//                    String.format(" Ya fue registrado una resolución con la serie %s y número %s en la oficina de %s",
//                            resolucion.getSerie(),
//                            resolucion.getNumero(),
//                            resolucion.getOficina().getNombre()));
            return respuesta;
        }
        ObjectUtil.eliminarAttrSinId(resolucion, "cicloAplica");

        TipoResolucionEnum tipoResolucionEnum = resolucion.getTipoResolucion().getTipoEnum();

        switch (tipoResolucionEnum) {
            case TRAS_INT:
                this.validarTrasladoInterno(resolucion, respuesta, ds);// aca pendtien
                if (!respuesta.isEmpty()) {
                    return respuesta;
                }
                break;
            case REIC:
            case RCI:
            case ANCI:
            case CAM_NOTA:
            case NOTA_BAJA:
            case READMISION:
            case INTES:
            case CAMBIO_PLAN_CURRICULAR:
            case CURDIR:
                this.requiereCicloAplica(resolucion.getCicloAplica());
                break;
            case TRAS:
            case ING_HIS:
                resolucion.setCicloAplica(ds.getCicloAcademico());
                break;
            case BACHI:
                resolucion.setNumeroVisible(resolucion.getCodigoTituloBachiller());
                break;
            case BACHIFAC:
                resolucion.setNumeroVisible(resolucion.getDescripcion());
            case TITUL:
                resolucion.setNumeroVisible(resolucion.getCodigoTituloBachiller());
                break;
            case PRACTICAS:
                this.requiereCicloAplica(resolucion.getCicloAplica());
                resolucion.setNumeroVisible(resolucion.getCodigoPracticas());
                break;
            default:
                break;
        }

        resolucion.setFechaRegistro(new Date());
        resolucion.setUserRegistro(ds.getUsuario());
        resolucion.setAplicacionDirecta(1l);
        resolucion.setEstadoEnum(ResolucionEstadoEnum.VB_RES);
        resolucionDAO.save(resolucion);

        switch (tipoResolucionEnum) {
            case REIC:
                respuesta = Arrays.asList(this.saveReincorporaciones(resolucion, ds));
                break;
            case RCI:
            case ANCI:
                respuesta = Arrays.asList(this.saveRetirosCiclos(resolucion, ds));
                break;
            case CAM_NOTA:
                respuesta = Arrays.asList(this.saveCambioNotas(resolucion, ds));
                break;
            case NOTA_BAJA:
                respuesta = Arrays.asList(this.saveNotasMasBajas(resolucion, ds));
                break;
            case READMISION:
                respuesta = Arrays.asList(this.saveReadmision(resolucion));
                break;
            case TRAS_INT:
                this.saveTramitesTrasladoInterno(resolucion, ds);
                break;
            case TRAS:
            case INTES:
            case ING_HIS:
                this.saveTramitesTraslado(resolucion, ds);
                break;
            case BACHI:
                this.saveTramiteBachiller(resolucion, ds);
                break;
            case BACHIFAC:
                this.saveTramiteBachiller(resolucion, ds);
                break;                
            case TITUL:
                this.saveTramiteTitulo(resolucion, ds);
                break;
            case CAMBIO_PLAN_CURRICULAR:
                this.saveCambioPlanCurricular(resolucion);
                break;
            case CURDIR:
                respuesta = this.saveCursoDirigido(resolucion, ds);
                if (!respuesta.isEmpty()) {
                    resolucionDAO.delete(resolucion);
                }
                break;
            case PRACTICAS:
                respuesta = Arrays.asList(this.saveTramitePracticas(resolucion, ds));
                break;
            default:
                break;
        }
        return respuesta;
    }

    private List<String> validarTrasladoInterno(Resolucion resolucion, List<String> respuesta, DataSessionPivot ds) {
        for (TramiteTraslado tramiteTrasladoForm : resolucion.getTramiteTraslado()) {

            TramiteTraslado traslado = tramiteTrasladoDAO.findSolicitadoByAlumnoCiclo(tramiteTrasladoForm.getAlumno(), ds.getCicloAcademico());

            if (traslado == null) {
                respuesta.add(String.format("El alumno con codigo %s no cuenta con una solicitud de traslado interno pendiente.",
                        tramiteTrasladoForm.getAlumno().getCodigo()));
            }
        }
        return respuesta;

    }

    private List<String> saveCursoDirigido(Resolucion resolucion, DataSessionPivot ds) {

        if (resolucion.getCursoDirigido().isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }

        List<CursoDirigido> tramiteCursoDirigidoAceptadoOrRechazado = resolucion.getCursoDirigido()
                .stream().filter(x -> x.isSeleccionado() || x.isRechazado()).collect(Collectors.toList());

        if (tramiteCursoDirigidoAceptadoOrRechazado.isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }

        Map<Long, Long> couterMap = tramiteCursoDirigidoAceptadoOrRechazado.stream()
                .collect(Collectors.groupingBy(e -> e.getAlumno().getId(), Collectors.counting()));

        for (Map.Entry<Long, Long> entry : couterMap.entrySet()) {

            Long count = entry.getValue();

            if (count > 1) {

                CursoDirigido cursoDirigido = resolucion.getCursoDirigido()
                        .stream()
                        .filter(x -> x.getAlumno().getId().longValue() == entry.getKey())
                        .findFirst().orElse(null);

                throw new PhobosException(String.format("Está repitiendo al alumno %s en la lista", cursoDirigido.getAlumno().getCodigo()));

            }

        }

        List<CursoDirigido> cursoDirigidos = cursoDirigidoDAO.allByCicloAcademicoSol(ds.getCicloAcademico());
        Map<Long, CursoDirigido> map = TypesUtil.convertListToMap("tramite.alumno.id", cursoDirigidos);
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RES_FAC);
        EstadoTramite estadoTramiteRech = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RHZ_SOL);

        List<Alumno> alumnos = resolucion.getCursoDirigido().stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allByAlumnosCicloActivo(alumnos);
        Map<Long, List<MatriculaCurso>> mapMatriculaCursos = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", matriculaCursos);

        List<String> msg = new ArrayList();

        for (CursoDirigido cursoDirigidoForm : tramiteCursoDirigidoAceptadoOrRechazado) {
            String message = "";
            CursoDirigido cursoDirigidoTram = map.get(cursoDirigidoForm.getAlumno().getId());

            if (cursoDirigidoTram == null) {
                throw new PhobosException("El alumno " + cursoDirigidoForm.getAlumno().getCodigo() + " no cuenta con un tramite de curso dirigido.");
            }

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

        for (CursoDirigido cursoDirigidoForm : tramiteCursoDirigidoAceptadoOrRechazado) {

            EstadoTramite estado = cursoDirigidoForm.isSeleccionado() ? estadoTramite : estadoTramiteRech;
            TramiteEstadoEnum estadotram = cursoDirigidoForm.isSeleccionado() ? TramiteEstadoEnum.ACEP : TramiteEstadoEnum.RCHZ;
            CursoDirigido cursoDirigidoTram = map.get(cursoDirigidoForm.getAlumno().getId());

            cursoDirigidoTram.setMotivoRechazo(cursoDirigidoTram.getMotivoRechazo());
            cursoDirigidoTram.setResolucion(resolucion);
            cursoDirigidoTram.setDocenteAsignado(cursoDirigidoForm.getDocenteAsignado());
            cursoDirigidoTram.setEstado(estado);
            cursoDirigidoDAO.update(cursoDirigidoTram);

            Tramite tramite = cursoDirigidoTram.getTramite();
            tramite.setEstadoEnum(estadotram);
            tramiteDAO.update(tramite);

            if (!cursoDirigidoForm.isSeleccionado()) {
                continue;
            }
            AnexoBoletin anexoBoletin = anexoBoletinDAO.findDepartamento(cursoDirigidoTram.getCurso().getDepartamentoAcademico());
            if (anexoBoletin == null) {
                throw new PhobosException("No existe el anexo boletín para el departamento " + cursoDirigidoTram.getCurso().getDepartamentoAcademico().getNombre());
            }

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
    public List<String> updateResolucion(Resolucion resolucionForm, Usuario usuario, DataSessionPivot ds) {

        Resolucion resolucionValidacion = resolucionDAO.findByOficinaSerieNumero(resolucionForm.getOficina(), resolucionForm.getSerie(), resolucionForm.getNumero());

        if (resolucionValidacion != null && resolucionForm.getId() != resolucionValidacion.getId().longValue()) {
            throw new PhobosException(
                    String.format(" Ya fue registrado una resolución con la serie %s y número %s en la oficina de %s",
                            resolucionForm.getSerie(),
                            resolucionForm.getNumero(),
                            resolucionForm.getOficina().getNombre()));
        }

        Resolucion resolucionBD = resolucionDAO.findById(resolucionForm.getId());
        TipoResolucionEnum tipoResolucionEnum = resolucionForm.getTipoResolucion().getTipoEnum();

        switch (tipoResolucionEnum) {
            case REIC:
            case RCI:
            case ANCI:
            case CAM_NOTA:
            case NOTA_BAJA:
            case READMISION:
            case TRAS_INT:
            case INTES:
                this.requiereCicloAplica(resolucionForm.getCicloAplica());
                break;
            case TRAS:
            case ING_HIS:
                resolucionForm.setCicloAplica(ds.getCicloAcademico());
                break;
            case BACHI:
            case TITUL:
                resolucionForm.setNumeroVisible(resolucionForm.getCodigoTituloBachiller());
                break;
            case CAMBIO_PLAN_CURRICULAR:
            case CURDIR:
                this.requiereCicloAplica(resolucionForm.getCicloAplica());
                break;
            case PRACTICAS:
                this.requiereCicloAplica(resolucionForm.getCicloAplica());
                resolucionForm.setNumeroVisible(resolucionForm.getCodigoPracticas());
                break;
            default:
                break;
        }

        resolucionBD.setSerie(resolucionForm.getSerie());
        resolucionBD.setNumero(resolucionForm.getNumero());
        resolucionBD.setOficina(resolucionForm.getOficina());
        resolucionBD.setCicloAplica(resolucionForm.getCicloAplica());
        resolucionBD.setNumeroVisible(resolucionForm.getNumeroVisible());

        resolucionBD.setUserActualizacion(usuario);
        resolucionBD.setFechaActualizacion(new Date());
        resolucionDAO.updateColumns(resolucionBD, "fecha", "serie", "numero", "oficina", "cicloAplica", "numeroVisible", "userActualizacion", "fechaActualizacion");

        TipoResolucionEnum tipo = resolucionBD.getTipoResolucion().getTipoEnum();
        List<String> respuesta = new ArrayList();

        switch (tipo) {
            case REIC:
                break;
            case RCI:
            case ANCI:
                respuesta = Arrays.asList(this.saveRetirosCiclos(resolucionForm, ds));
                break;
            case CAM_NOTA:
                break;
            case NOTA_BAJA:
                break;
            case READMISION:
                break;
            case TRAS_INT:
                this.updateTramitesTrasladoInterno(resolucionForm, ds);
            case TRAS:
            case INTES:
            case ING_HIS:
                break;
            case BACHI:
                this.saveTramiteBachiller(resolucionForm, ds);
                break;
            case TITUL:
                this.saveTramiteTitulo(resolucionForm, ds);
                break;
            case CAMBIO_PLAN_CURRICULAR:
                break;
            case CURDIR:
                this.saveCursoDirigido(resolucionForm, ds);
                break;
            case PRACTICAS:
                respuesta = Arrays.asList(this.saveTramitePracticas(resolucionForm, ds));
                break;
            default:
                break;
        }

        return respuesta;
    }

    private String saveReincorporaciones(Resolucion resolucion, DataSessionPivot ds) {

        if (resolucion.getReincorporaciones().isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }

        List<Reincorporacion> tramiteReincorporacionAceptadoOrRechazado = resolucion.getReincorporaciones()
                .stream().filter(x -> x.isSeleccionado() || x.isRechazado()).collect(Collectors.toList());

        if (tramiteReincorporacionAceptadoOrRechazado.isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }

        Map<Long, Long> couterMap = tramiteReincorporacionAceptadoOrRechazado.stream()
                .collect(Collectors.groupingBy(e -> e.getAlumno().getId(), Collectors.counting()));

        for (Map.Entry<Long, Long> entry : couterMap.entrySet()) {

            Long count = entry.getValue();

            if (count > 1) {

                Reincorporacion reincorporacion = resolucion.getReincorporaciones()
                        .stream()
                        .filter(x -> x.getAlumno().getId().longValue() == entry.getKey())
                        .findFirst().orElse(null);

                throw new PhobosException(String.format("Está repitiendo al alumno %s en la lista", reincorporacion.getAlumno().getCodigo()));

            }

        }

        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(ModalidadEstudioEnum.PRE);

        List<Reincorporacion> reincorporacions = reincorporacionDAO.allPendientesByCicloReincorporacion();

        Map<Long, Reincorporacion> map = TypesUtil.convertListToMap("alumno.id", reincorporacions);

        EstadoTramite estadoTramiteAceptado = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL_ACEP);
        EstadoTramite estadoTramiteRechazado = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RCHR);

        List<Alumno> alumnos = new ArrayList();

        for (Reincorporacion reincorporacioneForm : tramiteReincorporacionAceptadoOrRechazado) {

            Reincorporacion reincorporacion = map.get(reincorporacioneForm.getAlumno().getId());
            reincorporacion.setAceptado(reincorporacioneForm.isSeleccionado() ? 1 : 0);
            reincorporacion.setResolucion(resolucion);
            reincorporacion.setEstadoTramite(reincorporacioneForm.isSeleccionado() ? estadoTramiteAceptado : estadoTramiteRechazado);
            reincorporacionDAO.updateColumns(reincorporacion, "aceptado", "resolucion", "estadoTramite");

            Tramite tramite = reincorporacion.getTramite();
            tramite.setEstadoEnum(reincorporacioneForm.isSeleccionado() ? TramiteEstadoEnum.ACEP : TramiteEstadoEnum.RCHR);
            tramiteDAO.update(tramite);
            if (reincorporacion.getCicloReincorporacion().getId().equals(cicloActivo.getId())) {
                alumnos.add(reincorporacion.getAlumno());
            }
        }

        String token = "";

        if (!alumnos.isEmpty()) {

            token = RandomStringUtils.randomAlphanumeric(43);
            String tokenProm = token + TOKEN_PROMEDIOS;
            String tokenCurri = token + TOKEN_CURRICULA;
            String tokenMatri = token + TOKEN_MATRICULABLE;

            visorCalculoNotas.createToken(tokenProm, alumnos);
            visorCalculoNotas.createToken(tokenCurri, alumnos);
            visorCalculoNotas.createToken(tokenMatri, alumnos);
        }

        return token;
    }

    private String saveRetirosCiclos(Resolucion resolucion, DataSessionPivot ds) {

        if (resolucion.getRetiroCiclo().isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }

        List<RetiroCiclo> tramiteRetiroCicloAceptadoOrRechazado = resolucion.getRetiroCiclo()
                .stream().filter(x -> x.isSeleccionado() || x.isRechazado()).collect(Collectors.toList());

        if (tramiteRetiroCicloAceptadoOrRechazado.isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }

        Map<Long, Long> couterMap = tramiteRetiroCicloAceptadoOrRechazado.stream()
                .collect(Collectors.groupingBy(e -> e.getAlumno().getId(), Collectors.counting()));

        for (Map.Entry<Long, Long> entry : couterMap.entrySet()) {

            Long count = entry.getValue();

            if (count > 1) {

                RetiroCiclo retiroCiclo = resolucion.getRetiroCiclo()
                        .stream()
                        .filter(x -> x.getAlumno().getId().longValue() == entry.getKey())
                        .findFirst().orElse(null);

                throw new PhobosException(String.format("Está repitiendo al alumno %s en la lista", retiroCiclo.getAlumno().getCodigo()));

            }

        }

        List<Alumno> alumnos = new ArrayList<>();
        EstadoTramite estadoTramiteAcep = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ACEP);
        EstadoTramite estadoTramiteRechz = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RCHR);

        for (RetiroCiclo retiroCicloForm : tramiteRetiroCicloAceptadoOrRechazado) {
            CicloAcademico cicloAplica = null;

            Alumno alumnoDB = alumnoDAO.find(retiroCicloForm.getAlumno());
            RetiroCiclo retiroCicloDB = null;
            MatriculaResumen matriculaResumen = null;
            if (resolucion.isTipoRetiroCiclo()) {
                retiroCicloDB = retiroCicloDAO.findByExcepcional(alumnoDB);
                if (retiroCicloDB == null) {
                    throw new PhobosException("El alumno " + retiroCicloForm.getAlumno().getCodigo() + " no cuenta con un trámite de retiro ciclo.");
                }
                cicloAplica = retiroCicloDB.getCicloAcademico();
                matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumnoDB, cicloAplica);
                if (retiroCicloForm.isSeleccionado()) {

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
                    retiroCicloDB.setResolucion(resolucion);
                    retiroCicloDAO.updateColumns(retiroCicloDB, "estado", "estadoTramite", "resolucion");

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
                cicloAplica = resolucion.getCicloAplica();
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

                CicloAcademico cicloAcademicoAplicaDB = cicloAcademicoDAO.find(cicloAplica);

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
                retiroCicloDB.setResolucion(resolucion);
                retiroCicloDB.setFechaRegistro(new Date());
                if (alumnoDB.isPregrado()) {
                    retiroCicloDB.setEsContable(cicloAcademicoAplicaDB.isTipoRegular());
                } else {
                    retiroCicloDB.setEsContable(Boolean.FALSE);
                }
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

            if (!exist) {
                throw new PhobosException("El alumno " + alumnoDB.getPersona().getApellidosNombres() + " no tiene actividad en el ciclo " + cicloAplica.getDescripcion());
            }

            AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumnoDB, cicloAplica);
            if (resolucion.isTipoRetiroCiclo()) {
                alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.RCI);
            } else if (resolucion.isTipoAnulacionCiclo()) {
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
                if (resolucion.isTipoRetiroCiclo()) {
                    alumnoCicloCurso.setEstadoEnum(EstadoMatriculaEnum.RCI);
                } else if (resolucion.isTipoAnulacionCiclo()) {
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

    private String saveCambioNotas(Resolucion resolucion, DataSessionPivot ds) {
        List<Alumno> alumnos = new ArrayList<>();
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL_ACEP);
        for (CambioNota cambioNota : resolucion.getCambioNota()) {

            if (cambioNota.getId() != null) {
                continue;
            }

            cambioNota.setCicloAcademico(resolucion.getCicloAplica());

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
            cambioNotaNew.setResolucion(resolucion);
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

    private void saveTramitesTraslado(Resolucion resolucion, DataSessionPivot ds) {

        for (TramiteTraslado tramiteTraslado : resolucion.getTramiteTraslado()) {

            if (tramiteTraslado.getId() != null) {
                continue;
            }

            Tramite tramite = new Tramite();
            DateTime today = new DateTime();
            TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
            SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
            TipoTramite tipoTramite = null;
            if (resolucion.getTipoResolucion().getCodigo().equals(INTES.name())) {
                tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.INTES.name());
            } else if (resolucion.getTipoResolucion().getCodigo().equals(TRAS.name())) {
                tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.TRAS.name());
            } else if (resolucion.getTipoResolucion().getCodigo().equals(ING_HIS.name())) {
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
            tramiteTraslado.setResolucion(resolucion);
            tramiteTraslado.setFechaRegistro(new Date());
            if (resolucion.getTipoResolucion().getCodigo().equals(TipoResolucionEnum.TRAS.name())) {
                tramiteTraslado.setTipoTramiteTrasladoEnum(TipoTramiteTrasladoEnum.TRAS);
            } else if (resolucion.getTipoResolucion().getCodigo().equals(TipoResolucionEnum.INTES.name())) {
                tramiteTraslado.setTipoTramiteTrasladoEnum(TipoTramiteTrasladoEnum.INTES);
            } else if (resolucion.getTipoResolucion().getCodigo().equals(TipoResolucionEnum.ING_HIS.name())) {
                tramiteTraslado.setTipoTramiteTrasladoEnum(TipoTramiteTrasladoEnum.ING_HIS);
            }
            tramiteTraslado.setUserRegistro(ds.getUsuario());
            tramiteTraslado.setEstado(tramiteTraslado.getSeleccionado() ? ACEP.name() : RCHZ.name());
            tramiteTrasladoDAO.save(tramiteTraslado);
        }
    }

    private List<String> updateCursosDirigidos(Resolucion resolucionForm, DataSessionPivot ds) {
        List<String> msg = new ArrayList();

        if (resolucionForm.getCursoDirigido().isEmpty()) {
            throw new PhobosException("Debe Agregar alumnos.");
        }

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

            if (cursoDirigidoTram == null) {
                throw new PhobosException("El alumno " + cursoDirigidoForm.getAlumno().getCodigo() + " no cuenta con un tramite de curso dirigido.");
            }

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

            EstadoTramite estado = cursoDirigidoForm.isSeleccionado() ? estadoTramite : estadoTramiteRech;
            TramiteEstadoEnum estadotram = cursoDirigidoForm.isSeleccionado() ? TramiteEstadoEnum.ACEP : TramiteEstadoEnum.RCHZ;
            CursoDirigido cursoDirigidoTram = map.get(cursoDirigidoForm.getAlumno().getId());

            cursoDirigidoTram.setMotivoRechazo(cursoDirigidoTram.getMotivoRechazo());
            cursoDirigidoTram.setResolucion(resolucionForm);
            cursoDirigidoTram.setDocenteAsignado(cursoDirigidoForm.getDocenteAsignado());
            cursoDirigidoTram.setEstado(estado);
            cursoDirigidoDAO.update(cursoDirigidoTram);

            Tramite tramite = cursoDirigidoTram.getTramite();
            tramite.setEstadoEnum(estadotram);
            tramiteDAO.update(tramite);

            if (!cursoDirigidoForm.isSeleccionado()) {
                continue;
            }
            AnexoBoletin anexoBoletin = anexoBoletinDAO.findDepartamento(cursoDirigidoTram.getCurso().getDepartamentoAcademico());

            if (anexoBoletin == null) {
                throw new PhobosException("No existe el anexo boletín para el departamento " + cursoDirigidoTram.getCurso().getDepartamentoAcademico().getNombre());
            }

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

    private String saveNotasMasBajas(Resolucion resolucion, DataSessionPivot ds) {
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL_ACEP);
        List<Alumno> alumnos = new ArrayList<>();
        for (CambioNotaMasBaja cambioNotaMasBaja : resolucion.getCambioNotaMasBajas()) {
            if (cambioNotaMasBaja.getId() != null) {
                continue;
            }

            Alumno alumno = alumnoDAO.find(cambioNotaMasBaja.getAlumno());

            Tramite tramite = new Tramite();
            DateTime today = new DateTime();
            TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
            SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
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
            tramite.setUserRegistro(ds.getUsuario());
            tramiteDAO.save(tramite);

            cambioNotaMasBaja.setCicloAcademico(cambioNotaMasBaja.getAlumnoCicloCursoBean().getCicloAcademico());
            cambioNotaMasBaja.setCurso(cambioNotaMasBaja.getAlumnoCicloCursoBean().getCurso());
            cambioNotaMasBaja.setAlumno(cambioNotaMasBaja.getAlumno());
            cambioNotaMasBaja.setEstadoTramite(estadoTramite);
            cambioNotaMasBaja.setNotaAnulada(cambioNotaMasBaja.getAlumnoCicloCursoBean().getNota());
            cambioNotaMasBaja.setResolucion(resolucion);
            cambioNotaMasBaja.setTramite(tramite);
            cambioNotaMasBajaDAO.save(cambioNotaMasBaja);

            AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, cambioNotaMasBaja.getCicloAcademico(), cambioNotaMasBaja.getCurso());
            alumnoCicloCurso.setEstadoEnum(EstadoMatriculaEnum.ANMB);
            alumnoCicloCurso.setUserModificacion(ds.getUsuario());
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

    void saveTramiteBachiller(Resolucion resolucion, DataSessionPivot ds) {

        if (resolucion.getTramiteBachiller().isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ACEP);

        EventoCicloAcademico eventoCicloAcademico = eventoCicloAcademicoDAO.findByCicloAndEvento(ds.getCicloAcademico(), EventoAcademicoEnum.FECHAS_BACH);

        if (eventoCicloAcademico == null) {
            throw new PhobosException("No se ha configurado las fechas de inicio y fin del  ciclo " + ds.getCicloAcademico().getDescripcion());
        }

        List<Alumno> alumnos = resolucion.getTramiteBachiller().stream().map(x -> x.getAlumno())
                .collect(Collectors.toList());

        List<AlumnoCicloCurso> alumnosCiclosCursosActivos = alumnoCicloCursoDAO.allOperativesByAlumnos(alumnos);

        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosActivos);

        List<TramiteBachiller> tramiteBachillers = resolucion.getTramiteBachiller()
                .stream().filter(x -> x.getSeleccionado() != null && x.getSeleccionado() == true)
                .collect(Collectors.toList());

        for (TramiteBachiller bachiller : tramiteBachillers) {

            TramiteBachiller tramiteBachiller = tramiteBachillerDAO.findByAlumnoAct(bachiller.getAlumno());

            if (tramiteBachiller == null) {
                throw new PhobosException("El alumno " + bachiller.getAlumno().getCodigo() + " no tiene un trámite bachiller");
            }

            if (!tramiteBachiller.getEstado().equalsIgnoreCase(TramiteEstadoEnum.SOL.name())) {
                log.debug("Solo esta permitido agregar alumnos en modo edición");
                continue;
            }

            if (resolucion.getOficina().getCodigoDocumento().equals("UNA")) {
                tramiteBachiller.setResolucion(resolucion);
                tramiteBachiller.setEstado(TramiteEstadoEnum.ACEP.name());
            } else {
                tramiteBachiller.setResolucionFacultad(resolucion);
            }
            tramiteBachiller.setFechaResolucion(new Date());
            tramiteBachiller.setUsuarioResolucion(ds.getUsuario());
            tramiteBachillerDAO.update(tramiteBachiller);

            Tramite tramite = tramiteBachiller.getTramite();
            tramite.setEstadoEnum(TramiteEstadoEnum.ACEP);
            tramite.setFechaRespuesta(new Date());
            tramite.setUserRespuesta(ds.getUsuario());
            tramite.setFinalizado(Boolean.TRUE);
            tramite.setEstadoTramite(estadoTramite);
            tramiteDAO.update(tramite);

            if (resolucion.getOficina().getCodigoEnum() == OficinaEnum.UNA) {
                if (tramite.getEstadoEnum() == TramiteEstadoEnum.ACEP) {
                    Alumno alumno = alumnoDAO.find(tramiteBachiller.getTramite().getAlumno());
                    GradoAcademico gradoAcademico = gradoAcademicoDAO.findByTipoAndCarrera(TipoGradoAcademicoEnum.BACH, alumno.getCarrera());
                    ObtencionGrado obtencionGradoRegistrado = obtencionGradoDAO.getByAlumnoGrado(alumno, gradoAcademico);
                    if (obtencionGradoRegistrado == null) {
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
                    }
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

                    SituacionAcademica situacionAcademica = alumno.getSituacionAcademica();

                    if (situacionAcademica == null || (!situacionAcademica.isEgresado())) {

                        alumno.setSituacionAcademica(new SituacionAcademica(SituacionAcademicaEnum.S_E.getId()));
                        alumnoDAO.updateColumns(alumno, "situacionAcademica");

                        AlumnoCiclo alumnoCicloDb = alumnoCicloDAO.findLastActiveEstudiadoByAlumno(alumno);

                        if (alumnoCicloDb.getSituacionFinal() == null
                                || !alumnoCicloDb.getSituacionFinal().isEgresado()) {

                            alumnoCicloDb.setSituacionFinal(new SituacionAcademica(SituacionAcademicaEnum.S_E.getId()));
                            alumnoCicloDAO.updateColumns(alumnoCicloDb, "situacionFinal");
                        }
                    }
                }
            }
        }
    }

    private void saveTramiteTitulo(Resolucion resolucion, DataSessionPivot ds) {

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ACEP);

        if (resolucion.getTramiteTitulos().isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }

        List<TramiteTitulo> tramiteTitulos = resolucion.getTramiteTitulos()
                .stream()
                .filter(x -> x.getSeleccionado() != null && x.getSeleccionado())
                .collect(Collectors.toList());

        for (TramiteTitulo titulo : tramiteTitulos) {

            TramiteTitulo tramiteTitulo = tramiteTituloDAO.findByAlumnoAct(titulo.getAlumno());
            if (tramiteTitulo == null) {
                throw new PhobosException("El alumno " + titulo.getAlumno().getCodigo() + " no tiene un trámite titulo");
            }

            if (!tramiteTitulo.getEstado().equalsIgnoreCase(TramiteEstadoEnum.SOL.name())) {
                log.debug("Solo esta permitido agregar alumnos en modo edición");
                continue;
            }

            tramiteTitulo.setEstado(TramiteEstadoEnum.ACEP.name());
            tramiteTitulo.setFechaResolucion(new Date());
            tramiteTitulo.setUsuarioResolucion(ds.getUsuario());
            tramiteTitulo.setResolucion(resolucion);
            tramiteTituloDAO.update(tramiteTitulo);

            Tramite tramite = tramiteTitulo.getTramite();
            tramite.setEstadoEnum(TramiteEstadoEnum.ACEP);
            tramite.setFechaRespuesta(new Date());
            tramite.setUserRespuesta(ds.getUsuario());
            tramite.setFinalizado(Boolean.TRUE);
            tramite.setEstadoTramite(estadoTramite);
            tramiteDAO.update(tramite);
            if (tramite.getEstadoEnum() == TramiteEstadoEnum.ACEP) {
                Alumno alumno = tramiteTitulo.getTramite().getAlumno();
                GradoAcademico gradoAcademico = gradoAcademicoDAO.findByTipoAndCarrera(TipoGradoAcademicoEnum.TIT, alumno.getCarrera());
                ObtencionGrado obtencionGradoRegistrado = obtencionGradoDAO.getByAlumnoGrado(alumno, gradoAcademico);
                if (obtencionGradoRegistrado == null) {
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
                }

                Egresado egresado = egresadoDAO.findByAlumno(alumno);
                egresado.setPromedioAcumulado(alumno.getPromedioAcumulado());
                egresado.setFechaTitulacion(resolucion.getFecha());
                egresado.setUserRegistroTitulado(ds.getUsuario());
                egresado.setTitulo(gradoAcademico);
                egresadoDAO.update(egresado);
            }
        }
    }

    private String saveTramitePracticas(Resolucion resolucion, DataSessionPivot ds) {
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL_ACEP);

        List<Alumno> alumnos = new ArrayList<>();
        for (PracticasPreProfesional practicasForm : resolucion.getTramitePracticasPreProfesionales()) {

            if (practicasForm.getId() != null) {
                continue;
            }
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

            if (alumnoCursoCurricula == null) {
                throw new PhobosException("El alumno " + alumno.getCodigo() + " no tiene Practicas habilitadas");
            }

            log.debug("alumnoCursoCurricula {}", alumnoCursoCurricula.getId());

            CursoCurricula cursoCurricula = alumnoCursoCurricula.getCursoCurricula();

            Integer creditos = practicasForm.getCreditos() == null ? cursoCurricula.getCreditos() : practicasForm.getCreditos();

            PracticasPreProfesional preProfesionales = new PracticasPreProfesional();
            preProfesionales.setAlumno(practicasForm.getAlumno());
            preProfesionales.setCurso(cursoCurricula.getCurso());
            preProfesionales.setResolucion(resolucion);
            preProfesionales.setTramite(tramite);
            preProfesionales.setUsuario(ds.getUsuario());
            preProfesionales.setEstado(EstadoEnum.ACT.name());
            preProfesionales.setFechaRegistro(new Date());
            preProfesionales.setCreditos(creditos);
            practicaPreProfesionalesDAO.save(preProfesionales);

            AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findLastActiveRegByAlumno(alumno);
            AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCurso(alumno, cursoCurricula.getCurso());
            if (alumnoCicloCurso == null) {

                alumnoCicloCurso = new AlumnoCicloCurso();
                alumnoCicloCurso.setAlumnoCiclo(alumnoCiclo);
                alumnoCicloCurso.setCreditos(creditos);
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
            } else {

                if (resolucion.getOficina().getCodigo().equalsIgnoreCase("F040")) {
                    if (practicasForm.getCreditos() == null) {
                        throw new PhobosException("Hay inconsistencia con el alumno " + alumno.getCodigo() + ". Facultad no permite ingreso de créditos por separado.");
                    }
                }

                Integer sumaCreditos = alumnoCicloCurso.getCreditos() + creditos;
                if (cursoCurricula.getCreditos() < sumaCreditos) {
                    sumaCreditos = cursoCurricula.getCreditos();
                }

                alumnoCicloCurso.setCreditos(sumaCreditos);
                alumnoCicloCurso.setUserModificacion(ds.getUsuario());
                alumnoCicloCurso.setFechaModificacion(new Date());
                alumnoCicloCursoDAO.updateColumns(alumnoCicloCurso, "creditos", "userModificacion", "fechaModificacion");
            }

            alumnos.add(alumno);
        }
        String token = RandomStringUtils.randomAlphanumeric(43);
        String tokenProm = token + TOKEN_PROMEDIOS;
        String tokenCurri = token + TOKEN_CURRICULA;

        visorCalculoNotas.createToken(tokenProm, alumnos);
        visorCalculoNotas.createToken(tokenCurri, alumnos);

        return token;
    }

    private void saveTramitesTrasladoInterno(Resolucion resolucion, DataSessionPivot ds) {

        if (resolucion.getTramiteTraslado().isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }
        log.debug("solicitantes {}", resolucion.getTramiteTraslado().size());
        for (TramiteTraslado tramiteTrasladoForm : resolucion.getTramiteTraslado()) {

            TramiteTraslado traslado = tramiteTrasladoDAO.findSolicitadoByAlumnoCiclo(tramiteTrasladoForm.getAlumno(), ds.getCicloAcademico());

            if (traslado == null) {
                throw new PhobosException("El alumno" + tramiteTrasladoForm.getAlumno().getCodigo() + " no cuenta con una solicitud pendiente.");
            }

            TramiteEstadoEnum estado = tramiteTrasladoForm.getEstadoEnum();

            Tramite tramite = traslado.getTramite();
            tramite.setEstadoEnum(estado);
            tramite.setUserModificacion(ds.getUsuario());
            tramite.setFechaModificacion(new Date());
            tramiteDAO.updateEstado(tramite);

            traslado.setResolucion(resolucion);
            traslado.setEstadoEnum(estado);
            tramiteTrasladoDAO.updateColumns(traslado, "estado", "resolucion");

            log.debug("success update {}", traslado.getId());

        }
    }

    private String saveReadmision(Resolucion resolucion) {

        log.debug("after save tramite readmision {}", resolucion.getId());

        if (resolucion.getReadmisiones().isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }

        List<Readmision> tramiteReadmisionAceptadoOrRechazado = resolucion.getReadmisiones()
                .stream().filter(x -> x.isSeleccionado() || x.isRechazado()).collect(Collectors.toList());

        if (tramiteReadmisionAceptadoOrRechazado.isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }

        Map<Long, Long> couterMap = tramiteReadmisionAceptadoOrRechazado.stream()
                .collect(Collectors.groupingBy(e -> e.getAlumno().getId(), Collectors.counting()));

        for (Map.Entry<Long, Long> entry : couterMap.entrySet()) {

            Long count = entry.getValue();

            if (count > 1) {

                Readmision readmision = resolucion.getReadmisiones()
                        .stream()
                        .filter(x -> x.getAlumno().getId().longValue() == entry.getKey())
                        .findFirst().orElse(null);

                throw new PhobosException(String.format("Está repitiendo al alumno %s en la lista", readmision.getAlumno().getCodigo()));

            }

        }

        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(ModalidadEstudioEnum.PRE);
        List<Readmision> readmisiones = readmisionDAO.allPendientesByCicloReadmision();

        Map<Long, Readmision> readmisionXalumno = TypesUtil.convertListToMap("alumno.id", readmisiones);

        EstadoTramite estadoTramiteAceptado = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL_ACEP);
        EstadoTramite estadoTramiteRechazado = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RCHR);

        List<Alumno> alumnos = new ArrayList();

        for (Readmision readmisionForm : resolucion.getReadmisiones()) {

            Readmision readmision = readmisionXalumno.get(readmisionForm.getAlumno().getId());
            if (readmision == null) {
                throw new PhobosException(String.format("El alumno %s no ha tramitado su readmisión", readmision.getAlumno().getCodigo()));
            }
            readmision.setAceptado(readmisionForm.isSeleccionado() ? 1 : 0);
            readmision.setResolucion(resolucion);
            readmision.setMotivoRechazo(readmisionForm.getMotivoRechazo());
            readmision.setEstadoTramite(readmisionForm.isSeleccionado() ? estadoTramiteAceptado : estadoTramiteRechazado);
            readmisionDAO.updateColumns(readmision, "aceptado", "resolucion", "estadoTramite", "motivoRechazo");

            Tramite tramite = readmision.getTramite();
            tramite.setEstadoEnum(readmisionForm.isSeleccionado() ? TramiteEstadoEnum.ACEP : TramiteEstadoEnum.RCHR);
            tramiteDAO.update(tramite);

            if (readmision.getCicloReadmitido().getId().equals(cicloActivo.getId())) {
                alumnos.add(readmision.getAlumno());
            }
        }

        String token = "";

        if (!alumnos.isEmpty()) {

            token = RandomStringUtils.randomAlphanumeric(43);
            String tokenProm = token + TOKEN_PROMEDIOS;
            String tokenCurri = token + TOKEN_CURRICULA;
            String tokenMatri = token + TOKEN_MATRICULABLE;

            visorCalculoNotas.createToken(tokenProm, alumnos);
            visorCalculoNotas.createToken(tokenCurri, alumnos);
            visorCalculoNotas.createToken(tokenMatri, alumnos);
        }

        return token;
    }

    private void saveCambioPlanCurricular(Resolucion resolucion) {

        if (resolucion.getCambioPlanCurriculares().isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }

        List<CambioPlanCurricular> tramiteCambioPlanCurricularAceptadoOrRechazado = resolucion.getCambioPlanCurriculares()
                .stream().filter(x -> x.isSeleccionado() || x.isRechazado()).collect(Collectors.toList());

        if (tramiteCambioPlanCurricularAceptadoOrRechazado.isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }

        Map<Long, Long> couterMap = tramiteCambioPlanCurricularAceptadoOrRechazado.stream()
                .collect(Collectors.groupingBy(e -> e.getAlumno().getId(), Collectors.counting()));

        for (Map.Entry<Long, Long> entry : couterMap.entrySet()) {

            Long count = entry.getValue();

            if (count > 1) {

                CambioPlanCurricular cambioPlanCurricular = resolucion.getCambioPlanCurriculares()
                        .stream()
                        .filter(x -> x.getAlumno().getId().longValue() == entry.getKey())
                        .findFirst().orElse(null);

                throw new PhobosException(String.format("Está repitiendo al alumno %s en la lista", cambioPlanCurricular.getAlumno().getCodigo()));

            }

        }

        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(ModalidadEstudioEnum.PRE);
        List<CambioPlanCurricular> cambioPlanCurriculares = cambioPlanCurricularDAO.allPendientesByCicloAcademico();
        Map<Long, CambioPlanCurricular> cambioPlanCurricularXalumno = TypesUtil.convertListToMap("alumno.id", cambioPlanCurriculares);

        EstadoTramite estadoTramiteAceptado = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL_ACEP);
        EstadoTramite estadoTramiteRechazado = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.RCHR);

        List<Alumno> alumnos = new ArrayList();

        for (CambioPlanCurricular cambioPlanCurricularForm : tramiteCambioPlanCurricularAceptadoOrRechazado) {

            CambioPlanCurricular cambioPlanCurricular = cambioPlanCurricularXalumno.get(cambioPlanCurricularForm.getAlumno().getId());
            if (cambioPlanCurricular == null) {
                throw new PhobosException(String.format("Alumno %s no tiene tramite de cambio plan curricular", cambioPlanCurricularForm.getAlumno().getCodigo()));
            }
            cambioPlanCurricular.setAceptado(cambioPlanCurricularForm.isSeleccionado() ? 1 : 0);
            cambioPlanCurricular.setResolucion(resolucion);
            cambioPlanCurricular.setMotivoRechazo(cambioPlanCurricularForm.getMotivoRechazo());
            cambioPlanCurricular.setEstadoTramite(cambioPlanCurricularForm.isSeleccionado() ? estadoTramiteAceptado : estadoTramiteRechazado);
            cambioPlanCurricularDAO.updateColumns(cambioPlanCurricular, "aceptado", "resolucion", "estadoTramite", "motivoRechazo");

            Tramite tramite = cambioPlanCurricular.getTramite();
            tramite.setEstadoEnum(cambioPlanCurricularForm.isSeleccionado() ? TramiteEstadoEnum.ACEP : TramiteEstadoEnum.RCHR);
            tramiteDAO.update(tramite);

            if (cambioPlanCurricular.getCicloAcademico().getId().equals(cicloActivo.getId())) {
                alumnos.add(cambioPlanCurricular.getAlumno());
            }

        }

        String token = "";

        if (!alumnos.isEmpty()) {

            token = RandomStringUtils.randomAlphanumeric(43);
            String tokenProm = token + TOKEN_PROMEDIOS;
            String tokenCurri = token + TOKEN_CURRICULA;
            String tokenMatri = token + TOKEN_MATRICULABLE;

            visorCalculoNotas.createToken(tokenProm, alumnos);
            visorCalculoNotas.createToken(tokenCurri, alumnos);
            visorCalculoNotas.createToken(tokenMatri, alumnos);
        }

    }

    private String cleanNumero(String numeroString) {
        if (StringUtils.isBlank(numeroString)) {
            throw new PhobosException("La serie o número es incorrecto");
        }

        try {

            Long numeroLong = new Long(numeroString);

            if (numeroLong < 1) {
                throw new PhobosException("La serie o número es incorrecto");
            }

            return numeroLong.toString();

        } catch (Exception e) {
            throw new PhobosException("La serie o número es incorrecto");
        }

    }

    @Override
    public List<Oficina> allOficinasResolucion(DataSessionPivot ds) {
        List<TipoOficinaEnum> tiposEnum = Arrays.asList(
                TipoOficinaEnum.CUN,
                TipoOficinaEnum.FAC,
                TipoOficinaEnum.EPG
        );

        List<Oficina> oficinasMainUser = oficinaService.allOficinasMainByPersona(ds.getPersona());

        Optional<Oficina> estudios = oficinasMainUser.stream()
                .filter(ofi -> ofi.getCodigoEnum() == OficinaEnum.OERA)
                .findFirst();

        if (estudios.isPresent()) {
            return oficinaDAO.allByTiposOficinas(tiposEnum);
        }

        Optional<TipoOficina> tipoEscuela = oficinasMainUser.stream()
                .filter(ofi -> ofi.getCodigoEnum() == OficinaEnum.EPG)
                .map(ofi -> ofi.getTipoOficina())
                .findFirst();

        List<TipoOficina> tiposOficinas = oficinasMainUser.stream()
                .map(ofi -> ofi.getTipoOficina())
                .filter(tipo -> tipo.getCodigoEnum() == TipoOficinaEnum.FAC)
                .collect(Collectors.toList());

        if (tipoEscuela.isPresent()) {
            tiposOficinas.add(tipoEscuela.get());
        }

        if (tiposOficinas.isEmpty()) {
            return new ArrayList();
        }

        tiposEnum = tiposOficinas.stream()
                .map(tipo -> tipo.getCodigoEnum())
                .collect(Collectors.toList());

        return oficinaDAO.allByTiposOficinas(tiposEnum);
    }

    @Override
    public List<CicloAcademico> allCicloAplica(DataSessionPivot ds) {
        CicloAcademico ca = ds.getCicloAcademico();
        int rango = 20;
        return cicloAcademicoDAO.allPregradoFuturosByRange(ca.getYear() - rango, ca.getYear() + 3);
    }

    @Override
    public List<TramiteTraslado> allTramiteTrasladoByResolucion(Resolucion resolucion) {
        List<TramiteTraslado> tramiteTraslados = tramiteTrasladoDAO.allTramiteTrasladoByResolucion(resolucion);
        log.debug("tramiteTraslados {}", tramiteTraslados.size());
        for (TramiteTraslado tramiteTraslado : tramiteTraslados) {
            tramiteTraslado.setAlumno(tramiteTraslado.getTramite().getAlumno());
            tramiteTraslado.setSeleccionado(tramiteTraslado.getEstadoEnum() == TramiteEstadoEnum.ACEP);
        }
        return tramiteTraslados;
    }

    @Override
    @Transactional
    public boolean anularAlumnoDeResolucionTitulo(Resolucion resolucion, TramiteTitulo tramiteTitulo, Usuario usuario, DataSessionPivot ds) {

        boolean tramiteTituloAnulado = false;

        TramiteTitulo tramiteTituloDB = tramiteTituloDAO.find(tramiteTitulo.getId());
        Alumno alumno = tramiteTituloDB.getTramite().getAlumno();
        GradoAcademico gradoAcademico = gradoAcademicoDAO.findByTipoAndCarrera(TipoGradoAcademicoEnum.TIT, alumno.getCarrera());
        Resolucion resolucionBD = resolucionDAO.findById(resolucion.getId());
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL);
        Resolucion resolucionValidacion = resolucionDAO.findByOficinaSerieNumero(resolucionBD.getOficina(), resolucionBD.getSerie(), resolucionBD.getNumero());
        ObtencionGrado obtencionGradoRegistrado = obtencionGradoDAO.getByAlumnoGrado(alumno, gradoAcademico);

        if (tramiteTituloDB == null) {
            throw new PhobosException("No se encontró el Trámite Título");
        }

        if (resolucionBD == null) {
            throw new PhobosException("No se encontró la Resolución de Título");
        }

        log.debug("oficina {}", resolucionBD.getOficina().getId());
        log.debug("serie {}", resolucionBD.getSerie());
        log.debug("numero {}", resolucionBD.getNumero());
        log.debug("tipoResolucion {}", resolucionBD.getTipoResolucion().getTipoEnum().name());
        log.debug("Resolucion válido? {}", resolucionValidacion != null);

        if (resolucionValidacion == null) {
            throw new PhobosException("La Resolución de Título no cuenta con número de serie ni oficina válido");
        }

        if (obtencionGradoRegistrado != null) {
            obtencionGradoDAO.delete(obtencionGradoRegistrado);
        } else {
            throw new PhobosException("No existe el Grado Obtencion Titulo para el alumno " + alumno.getCodigo());
        }

        Tramite tramite = tramiteTituloDB.getTramite();
        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramite.setFechaRespuesta(null);
        tramite.setUserRespuesta(null);
        tramite.setFinalizado(Boolean.FALSE);
        tramite.setEstadoTramite(estadoTramite);
        tramite.setFechaModificacion(new Date());
        tramite.setUserModificacion(ds.getUsuario());
        tramiteDAO.update(tramite);

        tramiteTituloDB.setEstado(TramiteEstadoEnum.ANU.name());
        tramiteTituloDB.setFechaResolucion(null);
        tramiteTituloDB.setUsuarioResolucion(null);
        tramiteTituloDB.setResolucion(null);
        tramiteTituloDB.setTramite(tramite);
        tramiteTituloDB.setMotivo(tramiteTitulo.getMotivo());
        tramiteTituloDB.setFechaAnulacion(new Date());
        tramiteTituloDB.setUsuarioAnulaTramite(ds.getUsuario());
        tramiteTituloDAO.update(tramiteTituloDB);

        Egresado egresado = egresadoDAO.findByAlumno(alumno);
        egresado.setPromedioAcumulado(alumno.getPromedioAcumulado());
        egresado.setFechaTitulacion(resolucionBD.getFecha());
        egresado.setUserRegistroTitulado(ds.getUsuario());
        egresado.setTitulo(gradoAcademico);
        egresadoDAO.update(egresado);

        tramiteTituloAnulado = true;

        return tramiteTituloAnulado;

    }

    @Override
    @Transactional
    public boolean anularAlumnoDeResolucionBachiller(Alumno alumno, Resolucion resolucion, TramiteBachiller tramiteBachiller, Usuario usuario, DataSessionPivot ds) {
        boolean tramiteBachillerAnulado = false;

        Resolucion resolucionBD = resolucionDAO.findById(resolucion.getId());
        if (resolucionBD == null) {
            throw new PhobosException("No se encontró la Resolución de Bachiller");
        }

        TipoResolucionEnum tipoResolucionEnum = resolucionBD.getTipoResolucion().getTipoEnum();
        Resolucion resolucionValidacion = resolucionDAO.validaResolucion(resolucionBD.getOficina(), resolucionBD.getSerie(), resolucionBD.getNumero(), tipoResolucionEnum);
        if (resolucionValidacion.getId() == null) {
            throw new PhobosException("La Resolución de Bachiller no cuenta con número de serie ni oficina válido");
        }

        TramiteBachiller tramiteBachillerDB = tramiteBachillerDAO.find(tramiteBachiller.getId());
        if (tramiteBachillerDB == null) {
            throw new PhobosException("No se encontró el Trámite Bachiller");
        }
        Tramite tramite = tramiteBachillerDB.getTramite();

        Alumno alumnoDB = alumnoDAO.find(alumno);
        if (alumnoDB == null) {
            throw new PhobosException("No se encontró el alumno en esta Resolución de Bachiller");
        }

        GradoAcademico gradoAcademico = gradoAcademicoDAO.findByTipoAndCarrera(TipoGradoAcademicoEnum.BACH, alumnoDB.getCarrera());
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ANU);
        ObtencionGrado obtencionGradoRegistrado = obtencionGradoDAO.getByAlumnoGrado(alumnoDB, gradoAcademico);

        if (obtencionGradoRegistrado == null) {
            throw new PhobosException("No existe el Grado Obtencion Bachiller para el alumno " + alumnoDB.getCodigo());
        }
        log.debug("oficina {}", resolucionBD.getOficina().getId());
        log.debug("serie {}", resolucionBD.getSerie());
        log.debug("numero {}", resolucionBD.getNumero());
        log.debug("tipoResolucion {}", resolucionBD.getTipoResolucion().getTipoEnum().name());
        log.debug("Resolucion válido? {}", resolucionValidacion != null);

        obtencionGradoDAO.delete(obtencionGradoRegistrado);

        tramite.setAlumno(alumnoDB);
        tramite.setResolucion(null);
        tramite.setAceptado(false);
        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramite.setFechaRespuesta(null);
        tramite.setUserRespuesta(null);
        tramite.setFinalizado(Boolean.FALSE);
        tramite.setEstadoTramite(estadoTramite);
        tramite.setFechaModificacion(new Date());
        tramite.setUserModificacion(ds.getUsuario());
        tramiteDAO.update(tramite);

        tramiteBachillerDB.setEstado(TramiteEstadoEnum.ANU.name());
        tramiteBachillerDB.setFechaResolucion(null);
        tramiteBachillerDB.setUsuarioResolucion(null);
        tramiteBachillerDB.setResolucion(null);
        tramiteBachillerDB.setTramite(tramite);
        tramiteBachillerDB.setUsuario(ds.getUsuario());
        tramiteBachillerDB.setMotivo(tramiteBachiller.getMotivo());
        tramiteBachillerDB.setFechaAnulacion(new Date());
        tramiteBachillerDB.setUsuarioAnulaTramite(ds.getUsuario());
        tramiteBachillerDAO.update(tramiteBachillerDB);

        Egresado egresado = egresadoDAO.findByAlumno(alumnoDB);
        if (egresado == null) {
            throw new PhobosException("No es egresado alumno " + alumnoDB.getCodigo());
        }
        egresado.setAlumno(alumnoDB);
        egresado.setCarrera(alumnoDB.getCarrera());
        egresado.setFacultad(alumnoDB.getCarrera().getFacultad());
        egresado.setCicloAcademico(alumnoDB.getCicloActivoRegular());
        egresado.setTitulo(null);
        egresado.setGrado(null);
        egresadoDAO.update(egresado);

        tramiteBachillerAnulado = true;
        return tramiteBachillerAnulado;

    }

    @Override
    @Transactional
    public boolean anularAlumnoDeResolucionCursoDirigido(Alumno alumno, Resolucion resolucion, CursoDirigido cursoDirigido, DataSessionPivot ds) {

        boolean tramiteBachillerAnulado;

        Resolucion resolucionBD = resolucionDAO.findById(resolucion.getId());
        if (resolucionBD == null) {
            throw new PhobosException("No se encontró la Resolución de Curso Dirigido");
        }

        TipoResolucionEnum tipoResolucionEnum = resolucionBD.getTipoResolucion().getTipoEnum();
        Resolucion resolucionValidacion = resolucionDAO.validaResolucion(resolucionBD.getOficina(), resolucionBD.getSerie(), resolucionBD.getNumero(), tipoResolucionEnum);
        if (resolucionValidacion.getId() == null) {
            throw new PhobosException("La Resolución de Cursdo Dirigido no cuenta con número de serie ni oficina válido");
        }

        CursoDirigido cursoDirigidoDB = cursoDirigidoDAO.find(cursoDirigido.getId());
        if (cursoDirigidoDB == null) {
            throw new PhobosException("No se encontró registro de Curso Dirigido");
        }
        if (cursoDirigidoDB.getEstado().getCodigoEnum() == TramiteEstadoEnum.ANU) {
            throw new PhobosException(String.format("Alumno con codigo %s, no forma parte de la Resolución de Curso Dirigido", alumno.getCodigo()));
        }

        Tramite tramite = cursoDirigidoDB.getTramite();

        Alumno alumnoDB = alumnoDAO.find(alumno);
        if (alumnoDB == null) {
            throw new PhobosException("No se encontró el alumno en esta Resolución de Curso Dirigido");
        }

        log.debug("oficina {}", resolucionBD.getOficina().getId());
        log.debug("serie {}", resolucionBD.getSerie());
        log.debug("numero {}", resolucionBD.getNumero());
        log.debug("tipoResolucion {}", resolucionBD.getTipoResolucion().getTipoEnum().name());
        log.debug("Resolucion válido? {}", Objects.nonNull(resolucionValidacion));

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ANU);

        tramite.setAlumno(alumnoDB);
        tramite.setResolucion(null);
        tramite.setAceptado(false);
        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramite.setFechaRespuesta(null);
        tramite.setUserRespuesta(null);
        tramite.setFinalizado(Boolean.FALSE);
        tramite.setEstadoTramite(estadoTramite);
        tramite.setFechaModificacion(new Date());
        tramite.setUserModificacion(ds.getUsuario());
        tramiteDAO.update(tramite);

        cursoDirigidoDB.setEstado(estadoTramite);
        cursoDirigidoDB.setResolucion(null);
        cursoDirigidoDB.setTramite(tramite);
        cursoDirigidoDB.setMotivoAnulacion(cursoDirigido.getMotivoAnulacion());
        cursoDirigidoDB.setFechaAnulacion(new Date());
        cursoDirigidoDB.setUsuarioAnulaTramite(ds.getUsuario());
        cursoDirigidoDAO.update(cursoDirigidoDB);

        tramiteBachillerAnulado = true;
        return tramiteBachillerAnulado;

    }

    private void updateTramitesTrasladoInterno(Resolucion resolucion, DataSessionPivot ds) {

        if (resolucion.getTramiteTraslado().isEmpty()) {
            throw new PhobosException("Debe seleccionar como mínimo un alumno.");
        }

        log.debug("solicitantes {}", resolucion.getTramiteTraslado().size());

        for (TramiteTraslado tramiteTrasladoForm : resolucion.getTramiteTraslado()) {

            log.debug("tramiteTrasladoForm#{}", tramiteTrasladoForm.getId());

            if (tramiteTrasladoForm.getId() != null) {
                continue;
            }

            log.debug("alumno#{} CicloAcademico#{}", tramiteTrasladoForm.getAlumno().getId(), ds.getCicloAcademico().getId());

            TramiteTraslado traslado = tramiteTrasladoDAO.findSolicitadoByAlumnoCiclo(tramiteTrasladoForm.getAlumno(), ds.getCicloAcademico());

            if (traslado == null) {
                throw new PhobosException("El alumno " + tramiteTrasladoForm.getAlumno().getCodigo() + " no cuenta con una solicitud pendiente.");
            }

            TramiteEstadoEnum estado = tramiteTrasladoForm.getEstadoEnum();

            log.debug("TramiteTraslado#{} estado {}", tramiteTrasladoForm.getId(), estado.name());

            Tramite tramite = traslado.getTramite();
            tramite.setEstadoEnum(estado);
            tramite.setUserModificacion(ds.getUsuario());
            tramite.setFechaModificacion(new Date());
            tramiteDAO.updateEstado(tramite);

            traslado.setResolucion(resolucion);
            traslado.setEstadoEnum(estado);
            tramiteTrasladoDAO.updateColumns(traslado, "estado", "resolucion");

        }
    }

    @Override
    public List<TramiteBachiller> allResulucionFacultad(Resolucion resolucion) {
        return tramiteBachillerDAO.allBySolicitadosFacultad(resolucion);
    }
}

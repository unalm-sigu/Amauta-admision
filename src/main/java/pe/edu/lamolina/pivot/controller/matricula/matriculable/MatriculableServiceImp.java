package pe.edu.lamolina.pivot.controller.matricula.matriculable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguracionTurnosAtencion;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.aporte.ResumenAporteAlumno;
import static pe.edu.lamolina.model.enums.DeudaEstadoEnum.DEU;
import static pe.edu.lamolina.model.enums.DeudaEstadoEnum.PAG;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.ESP;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_1;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_5;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_9;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_E;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_EM;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_N;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_TU;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_X;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_XD;
import pe.edu.lamolina.model.enums.TipoCondicionalEnum;
import static pe.edu.lamolina.model.enums.TipoTramiteEnum.CAM_NOTA;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.PEND;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.model.tramite.CambioNota;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioReviewService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo.ResponseRestService;
import pe.edu.lamolina.pivot.controller.bienestar.alumnoAporte.AporteAlumnoService;
import pe.edu.lamolina.pivot.controller.matricula.configuracionturno.ConfiguracionMatriculaService;
import pe.edu.lamolina.pivot.controller.visores.RespositorVisor;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.ConfiguracionTurnosAtencionDAO;
import pe.edu.lamolina.pivot.dao.academico.EgresadoDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.dao.academico.TurnoAtencionDAO;
import pe.edu.lamolina.pivot.dao.aporte.AporteAlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.aporte.ResumenAporteAlumnoDAO;
import pe.edu.lamolina.pivot.dao.finanza.AcreenciaDAO;
import pe.edu.lamolina.pivot.dao.finanza.DeudaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.tramite.CambioNotaDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCicloDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.CAPA_ULTIMO_CICLO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class MatriculableServiceImp implements MatriculableService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;
    @Autowired
    TurnoAtencionDAO turnoAtencionDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    FacultadDAO facultadDAO;
    @Autowired
    EgresadoDAO egresadoDAO;
    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    @Autowired
    ConfiguracionTurnosAtencionDAO configuracionTurnosAtencionDAO;

    @Autowired
    VisorCalculaSituacion visorCalculaSituacion;

    @Autowired
    MatriculableConnector matriculableConector;

    @Autowired
    ConfiguracionMatriculaService configuracionMatriculaService;

    @Autowired
    PromedioService promedioService;

    @Autowired
    PromedioReviewService promedioReviewService;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    RetiroCicloDAO retiroCicloDAO;

    @Autowired
    AporteAlumnoService aporteAlumnoService;

    @Autowired
    AporteAlumnoCicloDAO aporteAlumnoCicloDAO;

    @Autowired
    CambioNotaDAO cambioNotaDAO;

    @Autowired
    ResponseRestService responseRestService;

    @Autowired
    AvanceCurricularService avanceCurricularService;

    @Autowired
    RespositorVisor respositorVisor;

    @Autowired
    ResumenAporteAlumnoDAO resumenAporteAlumnoDAO;

    @Autowired
    DeudaAlumnoDAO deudaAlumnoDAO;

    @Autowired
    AcreenciaDAO acreenciaDAO;

    @Override
    public AlumnoResumen allResumenAlumnosByCicloRol(CicloAcademico cicloAcademico, String codigo, List<Long> filtros) {
        return matriculaResumenDAO.findResumenByCicloRolDynateable(cicloAcademico, codigo, filtros);
    }

    @Override
    public List<MatriculaResumen> allAlumnosByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, List<Carrera> carreras, String todo) {
        long t10 = System.currentTimeMillis();
        long t1 = System.currentTimeMillis();
        List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allByCicloCarrerasDynatable(filter, cicloAcademico, carreras, todo);
        long t2 = System.currentTimeMillis();
        logger.debug("Consulta main ejecutada en {} mseg con {} registros", (t2 - t1), matriculaResumens.size());

        logger.debug("cicloAcademico {}", cicloAcademico.getCodigo());
        t1 = System.currentTimeMillis();
        List<ResumenAporteAlumno> resumenAporteAlumnos = resumenAporteAlumnoDAO.allByCicloMatriculaResumen(cicloAcademico, matriculaResumens);
        t2 = System.currentTimeMillis();
        logger.debug("aporteAlumnoCicloss {} ejecutadad en {} mseg", resumenAporteAlumnos.size(), (t2 - t1));
        Map<Long, List<ResumenAporteAlumno>> mapResumenAporteAlumno = TypesUtil.convertListToMapList("matriculaResumen.id", resumenAporteAlumnos);

        List<Alumno> alumnos = matriculaResumens.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        t1 = System.currentTimeMillis();
        List<DeudaAlumno> boletas = deudaAlumnoDAO.allDeudaAlumnoByCicloAlumno(alumnos, cicloAcademico);
        t2 = System.currentTimeMillis();
        logger.debug("boletas {} ejecutada en {} mseg", boletas.size(), (t2 - t1));
        Map<Long, List<DeudaAlumno>> mapBoletas = TypesUtil.convertListToMapList("alumno.id", boletas);

        t1 = System.currentTimeMillis();
        List<AporteAlumnoCiclo> aportesCarnetAlumnos = aporteAlumnoCicloDAO.allAporteCarnetByCicloMatriculaResumen(cicloAcademico, matriculaResumens);
        t2 = System.currentTimeMillis();
        logger.debug("aportes-alumnos {} ejecutada en {} mseg", aportesCarnetAlumnos.size(), (t2 - t1));
        Map<Long, AporteAlumnoCiclo> mapAporteCarnet = TypesUtil.convertListToMap("resumenAporteAlumno.matriculaResumen.id", aportesCarnetAlumnos);

        t1 = System.currentTimeMillis();
        List<AporteAlumnoCiclo> aportesDuplicadoCarnetAlumnos = aporteAlumnoCicloDAO.allAporteDuplicadoCarnetByCicloMatriculaResumen(cicloAcademico, matriculaResumens);
        t2 = System.currentTimeMillis();
        logger.debug("aportes-alumnos {} ejecutada en {} mseg", aportesCarnetAlumnos.size(), (t2 - t1));
        Map<Long, AporteAlumnoCiclo> mapAporteDuplicadoCarnet = TypesUtil.convertListToMap("resumenAporteAlumno.matriculaResumen.id", aportesDuplicadoCarnetAlumnos);

        for (MatriculaResumen matriculaResumen : matriculaResumens) {

            matriculaResumen.setAporteCarnet(Boolean.FALSE);
            if (mapAporteCarnet.get(matriculaResumen.getId()) != null) {
                matriculaResumen.setAporteCarnet(Boolean.TRUE);
            }

            matriculaResumen.setAporteDuplicadoCarnet(Boolean.FALSE);
            if (mapAporteDuplicadoCarnet.get(matriculaResumen.getId()) != null) {
                matriculaResumen.setAporteDuplicadoCarnet(Boolean.TRUE);
            }

            matriculaResumen.setBoletaPendiente(Boolean.FALSE);
            if (mapBoletas.get(matriculaResumen.getAlumno().getId()) != null) {
                matriculaResumen.setBoletaPendiente(Boolean.TRUE);
            }

            logger.debug("boletas {}", mapBoletas.get(matriculaResumen.getAlumno().getId()) != null);

            List<ResumenAporteAlumno> misResumenAporteAlumnos = TypesUtil.getListNotNull(mapResumenAporteAlumno.get(matriculaResumen.getId()));

            misResumenAporteAlumnos = misResumenAporteAlumnos.stream()
                    .collect(Collectors.toMap(y -> y.getId(), y -> y, (f, s) -> s))
                    .values().stream().collect(Collectors.toList());

            matriculaResumen.setResumenesAportes(misResumenAporteAlumnos);

        }

        long t20 = System.currentTimeMillis();
        logger.debug("Query de {} matriculables ejecutado en {} mseg", matriculaResumens.size(), (t20 - t10));
        return matriculaResumens;
    }

    @Override
    public MatriculableResumen findResumenByCiclo(CicloAcademico cicloAcademico) {
        return alumnoDAO.findResumenByCiclo(cicloAcademico);
    }

    @Override
    @Transactional
    public List<ModalidadEstudio> allModalidadEstudioByCodigos(List<String> codigos) {
        return modalidadEstudioDAO.allByCodigos(codigos);
    }

    @Override
    @Transactional
    public void generar(CicloAcademico ciclo, DataSessionPivot ds) {
        DateTime today = new DateTime();

        generarPregrado(ciclo, ds);
//        generarPosgrado(ciclo);

        CicloAcademico cicloAcademicoUpd = new CicloAcademico();
        cicloAcademicoUpd.setId(ciclo.getId());
        cicloAcademicoUpd.setFechaMatriculables(today.toDate());
        cicloAcademicoDAO.updateFechaMatriculables(cicloAcademicoUpd);
    }

    private void generarPosgrado(CicloAcademico ciclo) {
        List<SituacionAcademicaEnum> situaciones = Arrays.asList(S_N, S_1, S_2, S_3, S_5);

        CicloAcademico cicloBD = cicloAcademicoDAO.find(ciclo);
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(EPG);
        CicloAcademico cicloEpg = cicloAcademicoDAO.findByCodigoModalidadEstudio(cicloBD.getCodigo(), modalidad);

        Map<Long, Alumno> mapMatriculable = new LinkedHashMap();
        Map<Long, Alumno> mapMatriculableExist = new LinkedHashMap();
        List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allByCiclo(ciclo);
        for (MatriculaResumen matriculaResumen : matriculaResumens) {
            mapMatriculableExist.put(matriculaResumen.getAlumno().getId(), matriculaResumen.getAlumno());
        }

        List<CicloAcademico> ciclosPreviosPregrado = cicloAcademicoDAO.allActivosAnteriores(2, cicloBD);
        List<CicloAcademico> ciclosPreviosEpg = cicloAcademicoDAO.allActivosAnteriores(2, cicloEpg);
        ciclosPreviosEpg.addAll(ciclosPreviosPregrado);

        List<Alumno> matriculados = alumnoDAO.allMatriculadosNoEgresadosByCiclos(ciclosPreviosEpg);
        List<Alumno> estudiantes = alumnoDAO.allEstudiaronByCiclos(ciclosPreviosEpg);

        for (Alumno matriculado : matriculados) {
            Alumno alumnoExist = mapMatriculableExist.get(matriculado.getId());
            if (alumnoExist != null) {
                continue;
            }
            Alumno alumno = mapMatriculable.get(matriculado.getId());
            if (alumno != null) {
                continue;
            }
            if (!situaciones.contains(matriculado.getSituacionAcademica().getCodigoEnum())) {
                continue;
            }
            if (modalidad.getCodigoEnum() != matriculado.getModalidadEstudio().getCodigoEnum()) {
                continue;
            }
            mapMatriculable.put(matriculado.getId(), matriculado);
        }
        for (Alumno estudiante : estudiantes) {
            Alumno alumnoExist = mapMatriculableExist.get(estudiante.getId());
            if (alumnoExist != null) {
                continue;
            }
            Alumno alumno = mapMatriculable.get(estudiante.getId());
            if (alumno != null) {
                continue;
            }
            if (!situaciones.contains(estudiante.getSituacionAcademica().getCodigoEnum())) {
                continue;
            }
            if (modalidad.getCodigoEnum() != estudiante.getModalidadEstudio().getCodigoEnum()) {
                continue;
            }
            mapMatriculable.put(estudiante.getId(), estudiante);
        }

        List<Alumno> alumnos = new ArrayList(mapMatriculable.values());
        List<Long> alumnosIds = alumnos.stream().map(x -> x.getId()).collect(Collectors.toList());
        System.out.println("Finalmente quedan " + alumnosIds.size() + " alumnos EPG para ser matriculables");
        if (!alumnosIds.isEmpty()) {
            matriculaResumenDAO.saveMatriculables(alumnosIds, cicloEpg);
        }
//        for (Alumno alumno : alumnos) {
//            MatriculaResumen matriculable = new MatriculaResumen();
//            matriculable.setAlumno(alumno);
//            matriculable.setCicloAcademico(cicloBD);
//            matriculable.setCreditosMatriculados(0);
//            matriculable.setCreditosRetirados(0);
//            matriculable.setCreditosTrikaPagados(0);
//            matriculable.setCreditosTrikaSeparados(0);
//            matriculable.setCursosMatriculados(0);
//            matriculable.setCursosRetirados(0);
//            matriculable.setEstadoEnum(EstadoMatriculaEnum.NMAT);
//            matriculable.setSituacionInicio(alumno.getSituacionAcademica());
//            matriculable.setEsUltimoCiclo(false);
//            matriculaResumenDAO.save(matriculable);
//        }

    }

    private void generarPregrado(CicloAcademico ciclo, DataSessionPivot ds) {
        List<SituacionAcademicaEnum> situaciones = Arrays.asList(S_N, S_1, S_2, S_3, S_5, S_8, S_9, S_3U, S_2U, S_4U, S_6U, S_TU, S_EM);

        CicloAcademico cicloBD = cicloAcademicoDAO.find(ciclo);
        CicloAcademico cicloAntes = cicloAcademicoDAO.findAnteriorActivo(cicloBD);

        List<RetiroCiclo> retiroCiclos = retiroCicloDAO.allByCiclo(ciclo);
        List<Reincorporacion> reincorporacion = reincorporacionDAO.allByCicloReincorporacion(ciclo);
        List<CambioNota> cambioNota = cambioNotaDAO.allByCicloRegistro(ciclo);
        List<Alumno> alumosConTramite = retiroCiclos.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        alumosConTramite.addAll(reincorporacion.stream().filter(x -> x.getEsCondicional() && Arrays.asList(PEND.name()).contains(x.getTramite().getEstado())).map(x -> x.getAlumno()).collect(Collectors.toList()));
        alumosConTramite.addAll(cambioNota.stream().filter(x -> x.getEsCondicional()).map(x -> x.getAlumno()).collect(Collectors.toList()));

        List<CicloAcademico> ciclosIngresantes = Arrays.asList(cicloBD, cicloAntes);
        ModalidadEstudio modalidad = cicloBD.getModalidadEstudio();

        List<Alumno> ingresantes = alumnoDAO.allIngresantesByCiclos(ciclosIngresantes, modalidad.getCodigo());
        Map<Long, Alumno> mapMatriculable = TypesUtil.convertListToMap("id", ingresantes);

        List<CicloAcademico> ciclosPrevios = cicloAcademicoDAO.allActivosAnteriores(3, cicloBD);

        Map<Long, Alumno> mapMatriculableCondicional = new LinkedHashMap();

        Map<Long, Alumno> mapMatriculableExist = new LinkedHashMap();
        List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allByCiclo(ciclo);
        for (MatriculaResumen matriculaResumen : matriculaResumens) {
            mapMatriculable.remove(matriculaResumen.getAlumno().getId());
            mapMatriculableExist.put(matriculaResumen.getAlumno().getId(), matriculaResumen.getAlumno());
        }

        List<Alumno> matriculados = alumnoDAO.allMatriculadosNoEgresadosByCiclos(ciclosPrevios);
        System.out.println("=== vienen " + matriculados.size() + " matriculados de esos ciclos");
        List<Alumno> estudiantes = alumnoDAO.allEstudiaronByCiclos(ciclosPrevios);
        System.out.println("=== vienen " + estudiantes.size() + " estudiantes de esos ciclos");
        for (Alumno matriculado : matriculados) {
            Alumno alumnoExist = mapMatriculableExist.get(matriculado.getId());
            if (alumnoExist != null) {
                continue;
            }
            Alumno alumno = mapMatriculable.get(matriculado.getId());
            if (alumno != null) {
//                System.out.println("Ya existe el " + matriculado.getCodigo());
                continue;
            }

            if (!situaciones.contains(matriculado.getSituacionAcademica().getCodigoEnum())) {
//                if (matriculado.getSituacionAcademica().getCodigoEnum() == S_T && matriculado.getCreditosAprobados() > 180) {
//                System.out.println("Es Trika pero no se bota porque tiene  " + matriculado.getCreditosAprobados() + " creditos aprobados");
//                } else {
                System.out.println("Se bota porque su situacion es  " + matriculado.getSituacionAcademica().getCodigo());
//
//                    continue;
//                }
                continue;
            }
            if (modalidad.getCodigoEnum() != matriculado.getModalidadEstudio().getCodigoEnum()) {
                System.out.println("Se bota porque su modalidad es " + matriculado.getModalidadEstudio().getCodigo());
                continue;
            }
            System.out.println("AGREGAR " + matriculado.getCodigo() + " " + matriculado.getModalidadEstudio().getCodigo());
            mapMatriculable.put(matriculado.getId(), matriculado);
        }
        for (Alumno estudiante : estudiantes) {
            Alumno alumnoExist = mapMatriculableExist.get(estudiante.getId());
            if (alumnoExist != null) {
                continue;
            }
            Alumno alumno = mapMatriculable.get(estudiante.getId());
            if (alumno != null) {
                continue;
            }
            if (!situaciones.contains(estudiante.getSituacionAcademica().getCodigoEnum())) {
                continue;
            }
            if (modalidad.getCodigoEnum() != estudiante.getModalidadEstudio().getCodigoEnum()) {
                continue;
            }

            System.out.println("AGREGAR " + estudiante.getCodigo() + " " + estudiante.getModalidadEstudio().getCodigo());
            mapMatriculable.put(estudiante.getId(), estudiante);
        }

        for (Alumno alumnoTramite : alumosConTramite) {
            Alumno alumno = mapMatriculableCondicional.get(alumnoTramite.getId());
            if (alumno != null) {
                continue;
            }
            mapMatriculableCondicional.put(alumnoTramite.getId(), alumnoTramite);
        }

        List<Alumno> alumnos = new ArrayList(mapMatriculable.values());
        for (Alumno alumno : alumnos) {
            System.out.println("Finalmente quedan " + alumno.getCodigo() + " alumnos Reg para ser matriculables" + alumno.getModalidadEstudio().getCodigo());
        }
        List<Long> alumnosIds = alumnos.stream().map(x -> x.getId()).collect(Collectors.toList());
        System.out.println("Finalmente quedan " + alumnosIds.size() + " alumnos Reg para ser matriculables");
        if (!alumnosIds.isEmpty()) {
            matriculaResumenDAO.saveMatriculables(alumnosIds, ciclo);
        }

        List<Alumno> alumnosCondicional = new ArrayList(mapMatriculableCondicional.values());
        for (Alumno alumnoCondicional : alumnosCondicional) {
            Alumno alumnoExist = mapMatriculableExist.get(alumnoCondicional.getId());
            if (alumnoExist != null) {
                continue;
            }
            Alumno alumno = mapMatriculable.get(alumnoCondicional.getId());
            if (alumno != null) {
                continue;
            }

            logger.debug("Codigo Alumno {}", alumnoCondicional.getCodigo());
            MatriculaResumen matriculable = new MatriculaResumen();
            matriculable.setAlumno(alumnoCondicional);
            matriculable.setCicloAcademico(cicloBD);
            matriculable.setCreditosMatriculados(0);
            matriculable.setCreditosRetirados(0);
            matriculable.setCreditosTrikaPagados(0);
            matriculable.setCursosMatriculados(0);
            matriculable.setCursosRetirados(0);
            matriculable.setEstadoEnum(EstadoMatriculaEnum.NMAT);
            matriculable.setSituacionInicio(alumnoCondicional.getSituacionAcademica());
            matriculable.setEsUltimoCiclo(alumnoCondicional.getCreditosAprobadosConvalidados() >= 172);
            matriculable.setCreditosTrikaSeparados(0);
            matriculable.setEsCondicional(Boolean.TRUE);
            matriculable.setFechaCondicional(new Date());
            matriculaResumenDAO.save(matriculable);
        }
    }

    @Override
    @Transactional
    public void revisarSituacionAcademica(Alumno alumno, DataSessionPivot ds) {
        promedioService.calcularSituacionAcademica(alumno, ds);
    }

    @Override
    public void revisarSituacionesAcademicas(CicloAcademico ciclo, DataSessionPivot ds) {
        CicloAcademico cicloBD = cicloAcademicoDAO.find(ciclo.getId());
        List<MatriculaResumen> matriculables = matriculaResumenDAO.allByCiclo(cicloBD);
        List<Alumno> alumnos = matriculables.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<Egresado> egresados = egresadoDAO.allByAlumnos(alumnos);
        Map<Long, Egresado> mapEgresado = TypesUtil.convertListToMap("alumno.id", egresados);
        int loop = 0;
        if (!visorCalculaSituacion.iniciar(matriculables.size())) {
            throw new PhobosException("Ya se solicitó un procesamiento de situaciones académicas");
        }

        for (MatriculaResumen matriculable : matriculables) {
            Egresado egresado = mapEgresado.get(matriculable.getAlumno().getId());
            promedioService.calulcarSituacionAcademicaNewSession(matriculable.getAlumno(), egresado, ds);
            loop++;
            System.out.println("Se envio al matriculable " + loop + " de " + matriculables.size() + "");
            System.out.println("Se envio al matriculable " + loop + " de " + matriculables.size() + "");
            System.out.println("Se envio al matriculable " + loop + " de " + matriculables.size() + "");
            System.out.println("Se envio al matriculable " + loop + " de " + matriculables.size() + "");
            System.out.println("Se envio al matriculable " + loop + " de " + matriculables.size() + "");
            System.out.println("Se envio al matriculable " + loop + " de " + matriculables.size() + "");
            System.out.println("Se envio al matriculable " + loop + " de " + matriculables.size() + "");
            System.out.println("Se envio al matriculable " + loop + " de " + matriculables.size() + "");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void generarPrioridad(CicloAcademico ciclo) {
        for (;;) {
            if (visorCalculaSituacion.finalizo()) {
                break;
            }
            TypesUtil.delay(2000);
        }

        DateTime today = new DateTime();
        CicloAcademico cicloBD = cicloAcademicoDAO.find(ciclo.getId());

        if (cicloBD.getFechaMatriculables() == null) {
            throw new PhobosException("Primero debe generar los Alumnos matriculables");
        }

        cicloBD.setFechaPrioridades(today.toDate());
        cicloAcademicoDAO.updateFechaPrioridades(cicloBD);

        List<AlumnoCiclo> alumnosCiclos = alumnoCicloDAO.allActivosRegularesByCicloResumen(cicloBD);
        Map<Long, AlumnoCiclo> mapAlumnoCiclo = TypesUtil.convertListToMap("alumno.id", alumnosCiclos);
        List<MatriculaResumen> matriculables = matriculaResumenDAO.allByCicloMATAndNMAT(cicloBD);
        List<Long> alumnos = matriculables.stream().map(x -> x.getAlumno().getId()).collect(Collectors.toList());

        int cachimbos = 8000;
        int escuela = 10000;
        List<RetiroCiclo> retiroCiclos = retiroCicloDAO.allAlumnosByCiclo(alumnos, ciclo);
        Map<Long, RetiroCiclo> mapAlumnoTramiteRetiro = TypesUtil.convertListToMap("alumno.id", retiroCiclos);
        for (MatriculaResumen matriculable : matriculables) {
            matriculable.setPrioridad(null);
            matriculable.setPuntajePrioridad(null);
            matriculable.setTurnoAtencion(null);

            SituacionAcademica sit = matriculable.getAlumno().getSituacionAcademica();
            if (Arrays.asList(S_8, S_9).contains(sit.getCodigoEnum())) {
                matriculable.setPrioridad(BigDecimal.valueOf(cachimbos));
                matriculable.setPuntajePrioridad(BigDecimal.ZERO);
                cachimbos++;
                continue;
            }

            ModalidadEstudio modalidad = matriculable.getAlumno().getModalidadEstudio();
            if (Arrays.asList(ModalidadEstudioEnum.EPG, ModalidadEstudioEnum.ESP).contains(modalidad.getCodigoEnum())) {
                matriculable.setPrioridad(BigDecimal.valueOf(escuela));
                escuela++;
                continue;
            }
            AlumnoCiclo alumnoCiclo = null;
            RetiroCiclo retiroCiclo = mapAlumnoTramiteRetiro.get(matriculable.getAlumno().getId());
            if (retiroCiclo != null) {
                List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumnoDescRegular(matriculable.getAlumno());
                AlumnoCiclo alumnoCicloPenultimo = alumnoCiclos.get(1);
                alumnoCiclo = alumnoCicloDAO.findActivosRegularesByCiclo(alumnoCicloPenultimo.getCicloAcademico(), matriculable.getAlumno());
            } else {
                alumnoCiclo = mapAlumnoCiclo.get(matriculable.getAlumno().getId());
            }
            if (alumnoCiclo != null) {
                matriculable = matriculableConector.procesarPrioridadAlumno(matriculable, alumnoCiclo);
            }
        }

        List<MatriculaResumen> matriculablesConPuntaje = matriculables.stream()
                .filter(x -> (x.getPuntajePrioridad() != null && x.getPrioridad() == null))
                .collect(Collectors.toList());
        Collections.sort(matriculablesConPuntaje, new MatriculaResumen.ComparePrioridadCapa());

        List<MatriculaResumen> matriculablesUltimoCiclo = matriculablesConPuntaje.stream()
                .filter(x -> x.getAlumno().getCreditosAprobadosConvalidados() >= CAPA_ULTIMO_CICLO)
                .collect(Collectors.toList());

        int indice = 0;
        for (MatriculaResumen mr : matriculablesUltimoCiclo) {
            indice++;
            mr.setPrioridad(BigDecimal.valueOf(indice));
        }

        for (MatriculaResumen mr : matriculablesConPuntaje) {
            if (mr.getPrioridad() == null) {
                indice++;
                mr.setPrioridad(BigDecimal.valueOf(indice));
            }
        }

        for (MatriculaResumen mr : matriculables) {
            if (mr.getPrioridad() == null) {
                indice++;
                mr.setPrioridad(BigDecimal.valueOf(indice));
            }
        }

        for (MatriculaResumen mr : matriculables) {
            matriculaResumenDAO.update(mr);
        }
    }

    @Override
    public List<ConfiguracionTurnosAtencion> allConfiguracionTurnoByCiclo(CicloAcademico cicloAcademico) {
        return configuracionTurnosAtencionDAO.allByCiclo(cicloAcademico);
    }

    @Override
    @Transactional
    public void procesarTurnoMatricula(CicloAcademico ciclo, Long configuracionTurnoAtencion) {
        DateTime today = new DateTime();
        CicloAcademico cicloBD = cicloAcademicoDAO.find(ciclo.getId());

        if (cicloBD.getFechaPrioridades() == null) {
            throw new PhobosException("Primero debe procesar las prioridades de los Alumnos");
        }

        cicloBD.setFechaTurnosAsignados(today.toDate());

        ConfiguracionTurnosAtencion configuracionTurnosAtencion = configuracionTurnosAtencionDAO.find(configuracionTurnoAtencion);
        configuracionTurnosAtencion.setIsGenerado(Boolean.TRUE);
        configuracionTurnosAtencionDAO.update(configuracionTurnosAtencion);

        List<TurnoAtencion> turnosAtencion = turnoAtencionDAO.allByConfiguracion(configuracionTurnosAtencion);

        for (TurnoAtencion turnoAtencionEach : turnosAtencion) {
            matriculaResumenDAO.updateTurnoAtencion(ciclo, turnoAtencionEach);
        }

    }

    @Override
    @Transactional
    public void loadEgresados(MultipartFile file) {
        logger.debug("Service File {}");
        String rutaFile = saveEgresados(file);
        cargarEgresados(rutaFile);
    }

    private String saveEgresados(MultipartFile file) {
        try {
            String fileName = TypesUtil.getUnixTime() + "." + TypesUtil.getClean(file.getOriginalFilename());
            FileHelper.createDirectory(Constantine.TMP_DIR);
            String absoluteName = Constantine.TMP_DIR + fileName;
            FileHelper.saveToDisk(file, absoluteName);

            return absoluteName;
        } catch (IOException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        }
    }

    private List<Egresado> cargarEgresados(String rutaFile) {

        List<Egresado> lista;
        try {
            lista = new ArrayList<>();
            FileInputStream fis = new FileInputStream(rutaFile);
            String extension = FilenameUtils.getExtension(rutaFile);

            if (!extension.equals("xlsx")) {
                throw new PhobosException("El archivo debe tener la extensiÃ³n .xlsx");
            }

            Workbook myWorkBook = new XSSFWorkbook(fis);
            Sheet mySheet = myWorkBook.getSheetAt(0);
            Iterator<Row> rowIterator = mySheet.iterator();
            int loop = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                loop = row.getRowNum();

                if (loop < 1) {
                    continue;
                }

                String codigoAlumno = getCellValue(1, row);
                String codigoCarrera = getCellValue(3, row);
                String codigoFacultad = getCellValue(2, row);
                String codigoCiclo = getCellValue(5, row);
                Integer creditosAcumulados = TypesUtil.getInt(getCellValue(5, row));
                Integer creditosAprobadosAcumulados = TypesUtil.getInt(getCellValue(8, row));
                Integer puntajeAcumulado = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(9, row)) ? getCellValue(9, row) : null);
                BigDecimal promedioPonderadoAcumulado = TypesUtil.getBigDecimal(getCellValue(10, row));
                //  BigDecimal promedioAcumuladoMerito= TypesUtil.getBigDecimal(getCellValue(10, row));
                BigDecimal promedioGraduacion = TypesUtil.getBigDecimal(StringUtils.isNotBlank(getCellValue(11, row)) ? getCellValue(11, row) : null);
                Integer omg = TypesUtil.getInt(getCellValue(14, row));
                Integer omgf = TypesUtil.getInt(getCellValue(12, row));
                Integer omgCarrera = TypesUtil.getInt(getCellValue(13, row));
                Integer cuadrohonorCiclo = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(28, row)) ? getCellValue(28, row) : null);
                Integer quintoSuperiorCiclo = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(27, row)) ? getCellValue(27, row) : null);
                Integer tercioSuperiorCiclo = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(26, row)) ? getCellValue(26, row) : null);

                Integer cuadroHonorFacultad = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(25, row)) ? getCellValue(25, row) : null);
                Integer quintoSupFacultad = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(18, row)) ? getCellValue(18, row) : null);
                Integer tercioSupFacultad = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(15, row)) ? getCellValue(15, row) : null);

                Integer cuadroHonorCarrera = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(24, row)) ? getCellValue(24, row) : null);
                Integer quintoSupCarrera = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(19, row)) ? getCellValue(19, row) : null);
                Integer tercioSupCarrera = TypesUtil.getInt(StringUtils.isNotBlank(getCellValue(15, row)) ? getCellValue(15, row) : null);
                /*
                Date fechaEgresado;
                GradoAcademico grado;
                Date fechaGraduacion;
                TituloAcademico tituloAcademico;
                Date fechaTitulacion;

                Usuario usuarioRegistroEgresado;
                Date fechaRegistroEgresado;
                Usuario usuarioRegistroGraduado;
                Date fechaRegistroGraduado;
                Usuario usuarioRegistroTitulado;
                Date fechaRegistroTitulado;
                 */

                Egresado egresado = new Egresado();

                egresado.setCreditosAcumulados(creditosAcumulados);
                egresado.setCreditosAprobadosAcumulados(creditosAprobadosAcumulados);
                egresado.setCuadroHonorCarrera(cuadroHonorCarrera);
                egresado.setCuadroHonorCiclo(cuadrohonorCiclo);
                egresado.setCuadroHonorFacultad(cuadroHonorFacultad);
                egresado.setEsPrincipal(BigDecimal.ZERO.intValue());

                // egresado.setFechaEgresado(fechaEgresado);
                // egresado.setFechaGraduacion(fechaGraduacion);
                // egresado.setFechaRegistroEgresado(fechaRegistroEgresado);
                // egresado.setFechaRegistroGraduado(fechaRegistroGraduado);
                //egresado.setFechaRegistroTitulado(fechaRegistroTitulado);
                // egresado.setFechaTitulacion(fechaTitulacion);
                // egresado.setGrado(grado);
                egresado.setOrdenMeritoCarrera(omgCarrera);
                egresado.setOrdenMeritoCiclo(omg);
                egresado.setOrdenMeritoFacultad(omgf);
                egresado.setPromedioAcumulado(promedioPonderadoAcumulado);
                //  egresado.setPromedioAcumuladoMerito(promedioGraduacion);
                egresado.setPromedioGraduacion(promedioGraduacion);
                egresado.setPuntajeAcumulado(puntajeAcumulado);
                egresado.setQuintoSuperiorCarrera(quintoSupCarrera);
                egresado.setQuintoSuperiorCiclo(quintoSuperiorCiclo);
                egresado.setQuintoSuperiorFacultad(quintoSupFacultad);
                egresado.setTercioSuperiorCarrera(tercioSupCarrera);
                egresado.setTercioSuperiorCiclo(tercioSuperiorCiclo);
                egresado.setTercioSuperiorFacultad(tercioSupFacultad);

                // egresado.setTitulo(titulo);
                // egresado.setUserRegistroEgresado(Long.MIN_VALUE);
                // egresado.setUserRegistroGraduado(Long.MIN_VALUE);
                // egresado.setUserRegistroTitulado(Long.MIN_VALUE);
                matriculableConector.procesarEgresado(codigoAlumno, codigoCarrera, codigoFacultad, codigoCiclo, egresado);
            }
            logger.debug("Se han leido un total de {} filas", loop);
        } catch (FileNotFoundException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        } catch (IOException ex) {
            throw new PhobosException("El archivo no puede ser leido");
        }
        return lista;
    }

    private String getCellValue(int pos, Row row) {
        Cell cell = row.getCell(pos);
        if (cell == null) {
            return null;
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        String dato = cell.getStringCellValue();
        if (dato != null) {
            dato = StringUtils.replaceChars(dato, '\t', ' ');
            dato = StringUtils.replaceChars(dato, '\r', ' ');
            dato = StringUtils.replaceChars(dato, '\n', ' ');
            dato = StringUtils.replaceChars(dato, ',', ' ');
            dato = StringUtils.replaceChars(dato, '|', ' ');
            dato = StringUtils.trim(dato);
        }
        return dato;
    }

    private BigDecimal getPuntaje(String dato, Integer slace) {
        if (StringUtils.isEmpty(dato)) {
            return null;
        }
        return new BigDecimal(dato).setScale(slace, RoundingMode.DOWN);
    }

    @Override
    public CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.find(cicloAcademico);
    }

    @Override
    @Transactional
    public void eliminarPrioridad(CicloAcademico cicloAcademico) {
        CicloAcademico cicloBD = cicloAcademicoDAO.find(cicloAcademico.getId());
        cicloBD.setFechaPrioridades(null);
        cicloAcademicoDAO.updateFechaPrioridades(cicloBD);

        List<Long> ids = new ArrayList<>();
        List<MatriculaResumen> matriculables = matriculaResumenDAO.allByCiclo(cicloBD);
        for (MatriculaResumen matriculable : matriculables) {
            ids.add(matriculable.getId());
        }
        matriculaResumenDAO.updateList(ids);

    }

    @Override
    @Transactional
    public void finalizarPrioridad(CicloAcademico cicloAcademico) {
        CicloAcademico cicloBD = cicloAcademicoDAO.find(cicloAcademico.getId());
        cicloBD.setFechaCierrePrioridades(new Date());
        cicloAcademicoDAO.update(cicloBD);

    }

    @Override
    @Transactional
    public void finalizarMatriculable(CicloAcademico cicloAcademico) {
        CicloAcademico cicloBD = cicloAcademicoDAO.find(cicloAcademico.getId());
        cicloBD.setFechaCierreMatriculable(new Date());
        cicloAcademicoDAO.update(cicloBD);
    }

    @Override
    @Transactional
    public void limpiarMatriculable(CicloAcademico cicloAcademico) {
        CicloAcademico cicloBD = cicloAcademicoDAO.find(cicloAcademico.getId());
        cicloBD.setFechaMatriculables(null);
        cicloAcademicoDAO.updateFechaMatriculables(cicloBD);
        matriculaResumenDAO.deleteMatriculable(cicloAcademico);
    }

    @Override
    public List<Alumno> allAlumnoByNombre(String nombre, DataSessionPivot ds) {
        return alumnoDAO.allByNameSinMatriculaResumen(nombre, ds.getCicloAcademico());
    }

    @Override
    @Transactional
    public MatriculaResumen saveMatriculable(Alumno alumnoForm, String tipoCondicional, DataSessionPivot ds) {

        //CicloAcademico ciclo = cicloAcademicoDAO.find(ds.getCicloAcademico());
        Alumno alumno = alumnoDAO.find(alumnoForm);
        if (tipoCondicional.equals(CAM_NOTA.name())) {
            List<SituacionAcademicaEnum> situaciones = Arrays.asList(S_N, S_1, S_2, S_3, S_5, S_8, S_9, S_3U, S_2U, S_4U, S_6U, S_TU, S_EM);
            if (!situaciones.contains(alumno.getSituacionAcademica().getCodigoEnum())) {
                return null;
            }
        }
        CicloAcademico ciclo = cicloAcademicoDAO.findByCodigoCicloModalidadEnum(ds.getCicloAcademico().getCodigo(), alumno.getModalidadEstudio().getOperativeModalidadEnum());
        if (ciclo.getFechaMatriculables() == null && alumno.getModalidadEstudio().isPregrado()) {
            return null;
        }

        MatriculaResumen matri = matriculaResumenDAO.findByAlumnoCiclo(alumno, ciclo);
        matri = matri == null ? new MatriculaResumen() : matri;

        SituacionAcademica sit = alumno.getSituacionAcademica();

        ModalidadEstudio modalidad = alumno.getModalidadEstudio();
        List<SituacionAcademicaEnum> sitEnum = Arrays.asList(S_8, S_9);
        List<ModalidadEstudioEnum> modEnum = Arrays.asList(EPG, ESP);

        matri.setAlumno(alumno);
        matri.setCicloAcademico(ciclo);
        matri.setSituacionInicio(alumno.getSituacionAcademica());

        matri.setUserRegistro(ds.getUsuario());
        matri.setFechaRegistro(new Date());
        matri.setCreditosMatriculados(0);
        matri.setCreditosRetirados(0);
        matri.setCursosMatriculados(0);
        matri.setCursosRetirados(0);
        matri.setCreditosTrikaPagados(0);
        matri.setCreditosTrikaSeparados(0);
        matri.setPorcentajeAvance(0);
        matri.setNotaAcumulada("0");
        matri.setNotaAvance("0");
        matri.setNotaFinal("0");
        matri.setEstadoEnum(EstadoMatriculaEnum.NMAT);
        matri.setMotivoMatriculable(alumnoForm.getMotivoMatriculable());
        matri.setEsCondicional(alumnoForm.getEsMatriculaCondicional());

        if (!sitEnum.contains(sit.getCodigoEnum()) && !modEnum.contains(modalidad.getCodigoEnum()) && ciclo.getFechaPrioridades() != null) {
            AlumnoCiclo alumnoCiclo = null;
            if (tipoCondicional.equals(TipoCondicionalEnum.RETIRO_CICLO.name())) {
                List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumnoDescRegular(alumno);
                AlumnoCiclo alumnoCicloPenultimo = alumnoCiclos.get(1);
                alumnoCiclo = alumnoCicloDAO.findActivosRegularesByCiclo(alumnoCicloPenultimo.getCicloAcademico(), alumno);

            } else {
                alumnoCiclo = alumnoCicloDAO.findActivosRegularesByCicloResumen(alumno.getCicloActivoRegular(), alumnoForm);
            }

            matri = matriculableConector.procesarPrioridadAlumno(matri, alumnoCiclo);

            boolean esUltimoCiclo = alumno.getCreditosAprobadosConvalidados() > CAPA_ULTIMO_CICLO;
            MatriculaResumen matriculaAnt = matriculaResumenDAO.findByAntPrioridad(matri, ciclo, esUltimoCiclo);
            MatriculaResumen matriculaDes = matriculaResumenDAO.findByDesPrioridad(matri, ciclo, esUltimoCiclo);
            if (matriculaAnt != null && matriculaDes != null) {

                BigDecimal prioridad = matriculaAnt.getPrioridad().add(matriculaDes.getPrioridad()).divide(new BigDecimal(2));
                matri.setPrioridad(prioridad);
                if (ciclo.getFechaTurnosAsignados() != null) {
                    TurnoAtencion turnosAtencion = turnoAtencionDAO.findByPrioridad(prioridad, ciclo);
                    BigDecimal numPrioridad = turnosAtencion.getPrioridadFin().add(new BigDecimal("0.01"));
                    Integer cantAlum = turnosAtencion.getAlumnos() + 1;
                    turnosAtencion.setAlumnos(cantAlum);
                    turnosAtencion.setPrioridadFin(numPrioridad);
//                    turnoAtencionDAO.update(turnosAtencion);

                    configuracionMatriculaService.updateTurnos(turnosAtencion.getId(), cantAlum.toString());

                    matri.setTurnoAtencion(turnosAtencion);

                }
            }
        }

        if (matri.getId() != null) {
            matriculaResumenDAO.update(matri);
            aporteAlumnoService.generarAportes(alumno, ciclo, matri, ds);
            logger.debug("enviando generar boletas del alumno {} en el ciclo {} con matri-resumen {}", alumno.getId(), ciclo.getId(), matri.getId());
            matri.setProcesado(1);

        } else {
            matriculaResumenDAO.save(matri);
        }
        return matri;
    }

    @Override
    public void generarAportes(Alumno alumnoForm, MatriculaResumen matriculable, DataSessionPivot ds) {
        Alumno alumno = alumnoDAO.find(alumnoForm);
        CicloAcademico ciclo = cicloAcademicoDAO.findByCodigoModalidadEstudio(ds.getCicloAcademico().getCodigo(), alumno.getModalidadEstudio());
        aporteAlumnoService.generarAportes(alumno, ciclo, matriculable, ds);
        logger.debug("enviando generar boletas del alumno {} en el ciclo {} con matri-resumen {}", alumno.getId(), ciclo.getId(), matriculable.getId());
    }

    @Override
    @Transactional(readOnly = false)
    public void generarVerano(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        DateTime today = new DateTime();

        List<CicloAcademico> academicosAnterior = cicloAcademicoDAO.allAnteriorRegistroActivoPre(3, cicloAcademico);
        CicloAcademico academicoAnterior = academicosAnterior.get(2);

        List<CicloAcademico> academicosAnteriorPos = cicloAcademicoDAO.allAnteriorRegistroActivoPos(3, cicloAcademico);
        CicloAcademico academicoAnteriorPos = academicosAnteriorPos.get(2);
        List<String> situacionesPregrado
                = Arrays.asList(S_4.getValue(), S_X.getValue(), S_XD.getValue(), S_4U.getValue(), S_E.getValue());

        matriculaResumenDAO.savePosGradoVerano(situacionesPregrado, academicoAnteriorPos, cicloAcademico);
        matriculaResumenDAO.savePreGradoVerano(situacionesPregrado, academicoAnterior, cicloAcademico);

        CicloAcademico cicloAcademicoUpd = new CicloAcademico();
        cicloAcademicoUpd.setId(cicloAcademico.getId());
        cicloAcademicoUpd.setFechaMatriculables(today.toDate());
        cicloAcademicoDAO.updateFechaMatriculables(cicloAcademicoUpd);
    }

    private void asignarPprioridad(Alumno alumno, CicloAcademico cicloActivo) {
        //Alumno alum = alumnoDAO.find(alumno);
        SituacionAcademica sit = alumno.getSituacionAcademica();
        ModalidadEstudio modalidad = alumno.getModalidadEstudio();
        List<SituacionAcademicaEnum> sitEnum = Arrays.asList(S_8, S_9);
        Assert.isFalse(sitEnum.contains(sit.getCodigo()), "El alumno tiene una situaciÃ³n " + sit.getCodigoEnum().getNombre() + ". No se puede realizar su prioridad.");

        List<ModalidadEstudioEnum> modEnum = Arrays.asList(EPG, ESP);
        Assert.isFalse(modEnum.contains(modalidad.getCodigo()), "El alumno estÃ¡ en la modalidad " + modalidad.getCodigoEnum().name() + ". No se puede realizar su prioridad.");

        Assert.isNotNull(alumno.getCicloActivoRegular(), "El alumno no cuenta con un ciclo activo regular.");

        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findActivosRegularesByCicloResumen(alumno.getCicloActivoRegular(), alumno);
        Assert.isNotNull(alumnoCiclo, "El alumno no cuenta con historial de ciclos");

        MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloActivo);
        matriculableConector.procesarPrioridadAlumno(matriculaResumen, alumnoCiclo);

        boolean esUltimoCiclo = alumno.getCreditosAprobadosConvalidados() > CAPA_ULTIMO_CICLO;
        MatriculaResumen matriculaAnt = matriculaResumenDAO.findByAntPrioridad(matriculaResumen, cicloActivo, esUltimoCiclo);
        MatriculaResumen matriculaDes = matriculaResumenDAO.findByDesPrioridad(matriculaResumen, cicloActivo, esUltimoCiclo);

        BigDecimal prioridad = matriculaAnt.getPrioridad().add(matriculaDes.getPrioridad()).divide(new BigDecimal(2));
        matriculaResumen.setPrioridad(prioridad);
        if (cicloActivo.getFechaTurnosAsignados() != null) {
            TurnoAtencion turnosAtencion = turnoAtencionDAO.findByPrioridad(prioridad, cicloActivo);
            BigDecimal numPrioridad = turnosAtencion.getPrioridadFin().add(new BigDecimal("0.01"));
            turnosAtencion.setPrioridadFin(numPrioridad);
            turnoAtencionDAO.update(turnosAtencion);

            matriculaResumen.setTurnoAtencion(turnosAtencion);
        }
        matriculaResumenDAO.update(matriculaResumen);
    }

    @Override
    @Transactional
    public void inhabilitarMatriculable(MatriculaResumen matriculaResumenForm, DataSessionPivot ds) {
        MatriculaResumen matriculaResumen = matriculaResumenDAO.find(matriculaResumenForm.getId());
        Assert.isNotNull(matriculaResumen, "El alumno no es matriculable");
        Assert.isFalse(matriculaResumen.getEstadoEnum() == EstadoMatriculaEnum.INH,
                "El alumno ya se encontraba deshabilitado");
        Assert.isFalse(matriculaResumen.getEstadoEnum() == EstadoMatriculaEnum.MAT,
                "Primero debe retirarlo de sus cursos matriculados");
        Assert.isFalse(matriculaResumen.getEstadoEnum() == EstadoMatriculaEnum.PMAT,
                "Primero debe retirarlo de sus cursos prematriculados");
        Assert.isTrue(matriculaResumen.getEstadoEnum() == EstadoMatriculaEnum.NMAT,
                "El alumno debe tener estado No Matriculado para ser inhabilitado");

        JsonResponse jsonResponse = responseRestService.anularBoletas(matriculaResumen, ds);
        if (!jsonResponse.getSuccess()) {
            throw new PhobosException(jsonResponse.getMessage());
        }

        matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.INH);
        matriculaResumen.setMotivoMatriculable(matriculaResumenForm.getMotivoMatriculable());
        matriculaResumenDAO.update(matriculaResumen);
    }

    @Async
    @Override
    @Transactional
    public void recalcularPrioridad(GrupoSeccion gpoSecc, CicloAcademico ciclo) {
        List<Alumno> alumnos = alumnoDAO.allByGpoSeccion(gpoSecc);
        CicloAcademico cicloSgte = cicloAcademicoDAO.findSiguienteConfOrAct(ciclo);
        if (cicloSgte.getFechaPrioridades() != null) {
            for (Alumno alumno : alumnos) {
                asignarPprioridad(alumno, cicloSgte);
            }
        }
    }

    @Async
    @Override
    @Transactional
    public void verificarAlumnosNmat(DataSessionPivot ds, List<AlumnoCiclo> alumnoCiclos) {

        List<Alumno> alumnos = alumnoCiclos.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        alumnos = alumnoDAO.allInfoByAlumno(alumnos);
        logger.debug("Cantidad de alumnos {}", alumnoCiclos.size());
        CicloAcademico academico = ds.getCicloAcademico();
        List<CicloAcademico> ciclosAcademicos = cicloAcademicoDAO.all();
        List<CicloAcademico> ciclosActivo = cicloAcademicoDAO.allActivos();
        Map<String, CicloAcademico> mapCiclo = TypesUtil.convertListToMap("modalidadEstudio.codigo", ciclosActivo);
        Map<Long, Alumno> mapAlumno = TypesUtil.convertListToMap("id", alumnos);

        List<AlumnoCicloCurso> alumnosCiclosCursosActivos = alumnoCicloCursoDAO.allOperativesByAlumnos(alumnos);
        List<AlumnoCicloCurso> alumnosCiclosCursosAll = alumnoCicloCursoDAO.allByAlumnos(alumnos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoActivos = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosActivos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoAll = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosAll);

        List<AlumnoCiclo> alumnosCiclos = alumnoCicloDAO.allByAlumnos(alumnos);
        Map<Long, List<AlumnoCiclo>> mapAlumnoCiclo = TypesUtil.convertListToMapList("alumno.id", alumnosCiclos);

        List<Egresado> egresados = egresadoDAO.allByAlumnos(alumnos);
        Map<Long, Egresado> mapEgresados = TypesUtil.convertListToMap("alumno.id", egresados);
        List<Reincorporacion> reincorporacions = reincorporacionDAO.allByEstadoTramiteAndAlumnos(alumnos, new EstadoTramite(EstadoTramiteEnum.SOL_ACEP.getId()));
        Map<Long, List<Reincorporacion>> mapReincorporaciones = TypesUtil.convertListToMapList("alumno.id", reincorporacions);

//        List<SituacionAcademica> situacionAcademicas = situacionAcademicaDAO.all();
//        Map<String, SituacionAcademica> mapSituacionAcademicas = TypesUtil.convertListToMap("codigo", situacionAcademicas);
        for (Alumno alumno : alumnos) {
            CicloAcademico cicloActivoMod = mapCiclo.get(alumno.getModalidadEstudio().getCodigo());
            List<AlumnoCiclo> allAlumnoCiclos = TypesUtil.getListNotNull(mapAlumnoCiclo.get(alumno.getId()));
            List<AlumnoCicloCurso> alumnoCicloCursosActivos = TypesUtil.getListNotNull(mapAlumnoCicloCursoActivos.get(alumno.getId()));
            List<AlumnoCicloCurso> alumnoCicloCursosAll = TypesUtil.getListNotNull(mapAlumnoCicloCursoAll.get(alumno.getId()));
            Egresado egresado = mapEgresados.get(alumno.getId());
            List<Reincorporacion> reincorporados = TypesUtil.getListNotNull(mapReincorporaciones.get(alumno.getId()));
            logger.info("Alumno codigo {}", alumno.getCodigo());
            logger.debug("Cantidad de {} total {}", respositorVisor.getContador(), respositorVisor.getCantidadTotal());
//            Map<Long, AlumnoCiclo> mapAllAlumnoCicloByCiclo = TypesUtil.convertListToMap("cicloAcademico.id", allAlumnoCiclos);
//            Map<Long, List<AlumnoCicloCurso>> mapAllAlumnoCicloByAlumnoCiclo = TypesUtil.convertListToMapList("alumnoCiclo.id", fillList(allAlumnoCicloCurso));

            promedioService.promediarAllCicloSync(
                    alumno,
                    cicloActivoMod,
                    egresado,
                    ciclosAcademicos,
                    allAlumnoCiclos,
                    alumnoCicloCursosActivos,
                    alumnoCicloCursosAll,
                    reincorporados, ds, true, true);
//            this.revisarSituacionAcademica(alumnoCiclo.getAlumno(), ds);
            respositorVisor.incrementar();
            logger.debug("Cantidad de {} total {}", respositorVisor.getContador(), respositorVisor.getCantidadTotal());
        }
        academico.setFechaVerificaNmat(new Date());
        cicloAcademicoDAO.update(academico);

    }

    @Override
    @Transactional
    public void beneficiar(MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        ObjectUtil.eliminarAttrSinId(matriculaResumen);
        if (!matriculaResumen.getEsBeneficiadoUltimoCiclo()) {
            matriculaResumen.setEsBeneficiadoUltimoCiclo(true);
            matriculaResumen.setFechaBeneficiadoUtlCiclo(new Date());
        } else {
            matriculaResumen.setEsBeneficiadoUltimoCiclo(false);
            matriculaResumen.setFechaBeneficiadoUtlCiclo(null);
        }
        matriculaResumenDAO.updateBeneficiado(matriculaResumen);
    }

    @Override
    public List<CicloAcademico> allCiclosActivos() {
        return cicloAcademicoDAO.allActivosAlModalidades();
    }

    @Override
    public List<AlumnoCiclo> allAlumnosCicloNmat(CicloAcademico cicloActivo) {

        List<CicloAcademico> cicloAnt = cicloAcademicoDAO.findAnteriorRegular(cicloActivo);
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByNmatAndInh(cicloAnt);
        respositorVisor.iniciar(alumnoCiclos.size());
        return alumnoCiclos;
    }

    @Override
    public void quitarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        matriculaResumen = matriculaResumenDAO.find(matriculaResumen.getId());
        aporteAlumnoService.quitarAporteCarnet(matriculaResumen.getCicloAcademico(), matriculaResumen, ds);
    }

    @Override
    public void agregarAporteCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        matriculaResumen = matriculaResumenDAO.find(matriculaResumen.getId());
        aporteAlumnoService.generarAporteCarnet(matriculaResumen.getCicloAcademico(), matriculaResumen, ds);
    }

    @Override
    public void agregarAporteSegundaCarrera(MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        matriculaResumen = matriculaResumenDAO.find(matriculaResumen.getId());
        aporteAlumnoService.generarAporteSegundaCarrera(matriculaResumen.getCicloAcademico(), matriculaResumen, ds);
    }

    @Override
    @Transactional
    public void actualizarPrioridadCero(DataSessionPivot ds) {
        List<MatriculaResumen> matriculable = matriculaResumenDAO.allUltimosCiclosMatriculables(ds.getCicloAcademico());
        List<Alumno> alumnos = matriculable.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumnosReg(alumnos);
        Map<Long, List<AlumnoCiclo>> map = TypesUtil.convertListToMapList("alumno.id", alumnoCiclos);
        for (MatriculaResumen matriculaResumen : matriculable) {
            List<AlumnoCiclo> alumnoCiclo = map.get(matriculaResumen.getAlumno().getId());
            matriculaResumen = matriculableConector.procesarPrioridadAlumno(matriculaResumen, alumnoCiclo.get(0));

            MatriculaResumen matriculaAnt = matriculaResumenDAO.findByAntPrioridadTemp(matriculaResumen, ds.getCicloAcademico(), (matriculaResumen.getAlumno().getCreditosCarreraAprobados() > CAPA_ULTIMO_CICLO));
            MatriculaResumen matriculaDes = matriculaResumenDAO.findByDesPrioridadTemp(matriculaResumen, ds.getCicloAcademico(), (matriculaResumen.getAlumno().getCreditosCarreraAprobados() > CAPA_ULTIMO_CICLO));
            if (matriculaAnt != null && matriculaDes != null) {

                BigDecimal prioridad = matriculaAnt.getPrioridad().add(matriculaDes.getPrioridad()).divide(new BigDecimal(2));
                BigDecimal bigDecimal = new BigDecimal(0.5);
                if (matriculaResumen.getPrioridad().compareTo(prioridad) <= 0 || matriculaResumen.getPrioridad().subtract(BigDecimal.ONE).compareTo(bigDecimal) == 0) {
                    System.out.println("No menviene");
                    continue;
                }
                matriculaResumen.setPrioridad(prioridad);

                TurnoAtencion turnosAtencion = turnoAtencionDAO.findByPrioridad(prioridad, ds.getCicloAcademico());
                if (matriculaResumen.getTurnoAtencion() != null && Objects.equals(matriculaResumen.getTurnoAtencion().getId(), matriculaResumen.getId())) {
                    continue;
                }
                BigDecimal numPrioridad = turnosAtencion.getPrioridadFin().add(new BigDecimal("0.01"));
                Integer cantAlum = turnosAtencion.getAlumnos() + 1;
                turnosAtencion.setAlumnos(cantAlum);
                turnosAtencion.setPrioridadFin(numPrioridad);
//                    turnoAtencionDAO.update(turnosAtencion);

                configuracionMatriculaService.updateTurnos(turnosAtencion.getId(), cantAlum.toString());

                matriculaResumen.setTurnoAtencion(turnosAtencion);

            }
            matriculaResumenDAO.update(matriculaResumen);
        }
    }

    @Override
    public ResumenAporteAlumno findResumenAporteAlumno(ResumenAporteAlumno resumenAporteAlumno) {
        ResumenAporteAlumno resumen = resumenAporteAlumnoDAO.find(resumenAporteAlumno);
        List<AporteAlumnoCiclo> aportesCiclo = aporteAlumnoCicloDAO.allByResumenAporteAlumno(resumen);
        resumen.setAporteAlumnoCiclo(aportesCiclo);

        return resumen;
    }

    @Override
    public MatriculaResumen findMatriculaResumen(MatriculaResumen matriculaResumen) {
        return matriculaResumenDAO.findFull(matriculaResumen);
    }

    @Override
    public List<DeudaAlumno> allByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        List<DeudaAlumno> deudasVer = new ArrayList();
        List<DeudaAlumno> deudas = deudaAlumnoDAO.allByAlumnoCiclo(alumno, cicloAcademico);
        for (DeudaAlumno deuda : deudas) {
            if (Arrays.asList(DEU, PAG).contains(deuda.getEstadoEnum())
                    && deuda.getMonto().compareTo(BigDecimal.ZERO) > 0) {
                deudasVer.add(deuda);
            }
        }

        List<Acreencia> acreencias = acreenciaDAO.allByDeudaAlumno(deudas);
        Map<Long, Acreencia> mapAcreencias = TypesUtil.convertListToMap("instanciaTabla", acreencias);

        for (DeudaAlumno deuda : deudasVer) {
            Acreencia acree = mapAcreencias.get(deuda.getId());
            deuda.setAcreencia(acree);
        }

        return deudasVer;
    }

    @Override
    public List<AptoPreBean> allAptosPregrado(CicloAcademico cicloAcademico, String tipoReporte) {
        List<AptoPreBean> listAptoPreBean = new ArrayList<>();
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        if ("candidatosAptPre".equals(tipoReporte)) {
            CicloAcademico cicloAnterior = cicloAcademicoDAO.findAnteriorRegular(cicloAcademico).get(0);
            listAptoPreBean = alumnoCicloDAO.allCandidadosAptosPregrado(cicloAcademico, cicloAnterior, modalidadEstudio);
        }
        if ("votantesAptPre".equals(tipoReporte)) {
            listAptoPreBean = alumnoCicloDAO.allVotantesAptosPregrado(cicloAcademico, modalidadEstudio);
        }
        return listAptoPreBean;
    }

    @Override
    public void agregarAporteDuplicadoCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        matriculaResumen = matriculaResumenDAO.find(matriculaResumen.getId());
        aporteAlumnoService.generarAporteDuplicadoCarnet(matriculaResumen.getCicloAcademico(), matriculaResumen, ds);
    }

    @Override
    public void quitarAporteDuplicadoCarnet(MatriculaResumen matriculaResumen, DataSessionPivot ds) {
        matriculaResumen = matriculaResumenDAO.find(matriculaResumen.getId());
        aporteAlumnoService.quitarAporteDuplicadoCarnet(matriculaResumen.getCicloAcademico(), matriculaResumen, ds);
    }

}

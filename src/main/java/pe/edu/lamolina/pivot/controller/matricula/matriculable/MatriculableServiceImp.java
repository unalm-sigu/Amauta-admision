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
import org.apache.commons.lang3.RandomStringUtils;
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
import org.springframework.transaction.annotation.Propagation;
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
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.INH;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.NMAT;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.MAT_REG;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.MAT_VER;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.EPG;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_1;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4T;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_5;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_7;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_9;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_D;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_E;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_N;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_Q;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_R;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_T;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_X;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_XD;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.CambioNota;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.pivot.controller.academico.promedio.ListBeanPromedios;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioLoadDataService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioReviewService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.controller.academico.promedio.ReincorporadosService;
import pe.edu.lamolina.pivot.controller.responserest.ResponseRestService;
import pe.edu.lamolina.pivot.controller.bienestar.alumnoAporte.AporteAlumnoService;
import pe.edu.lamolina.pivot.controller.matricula.configuracionturno.ConfiguracionMatriculaService;
import pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas;
import static pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas.TOKEN_CURRICULA;
import static pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas.TOKEN_HISTORIAL;
import static pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas.TOKEN_MATRICULABLE;
import static pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas.TOKEN_PROMEDIOS;
import pe.edu.lamolina.pivot.controller.visores.RespositorVisor;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.ConfiguracionTurnosAtencionDAO;
import pe.edu.lamolina.pivot.dao.academico.EgresadoDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
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
import static pe.edu.lamolina.model.constantines.AcademicoConstantine.CAPA_ULTIMO_CICLO;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
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
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;
    @Autowired
    RetiroCicloDAO retiroCicloDAO;
    @Autowired
    AporteAlumnoCicloDAO aporteAlumnoCicloDAO;
    @Autowired
    ResumenAporteAlumnoDAO resumenAporteAlumnoDAO;
    @Autowired
    DeudaAlumnoDAO deudaAlumnoDAO;
    @Autowired
    AcreenciaDAO acreenciaDAO;
    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Autowired
    VisorCalculaSituacion visorCalculaSituacion;

    @Autowired
    MatriculableConnector matriculableConector;

    @Autowired
    ConfiguracionMatriculaService configuracionMatriculaService;

    @Autowired
    PromedioService promedioService;
    @Autowired
    PromedioLoadDataService promedioLoadDataService;

    @Autowired
    PromedioReviewService promedioReviewService;

    @Autowired
    AporteAlumnoService aporteAlumnoService;

    @Autowired
    CambioNotaDAO cambioNotaDAO;

    @Autowired
    ResponseRestService responseRestService;

    @Autowired
    AvanceCurricularService avanceCurricularService;
    @Autowired
    ReincorporadosService reincorporadosService;

    @Autowired
    RespositorVisor respositorVisor;

    @Autowired
    VisorCalculoNotas visorCalculoNotas;

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
        List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allHabilesByCiclo(ciclo);
        for (MatriculaResumen matriculaResumen : matriculaResumens) {
            mapMatriculableExist.put(matriculaResumen.getAlumno().getId(), matriculaResumen.getAlumno());
        }

        List<CicloAcademico> ciclosPreviosPregrado = cicloAcademicoDAO.allActivosRegularAnteriores(2, cicloBD);
        List<CicloAcademico> ciclosPreviosEpg = cicloAcademicoDAO.allActivosRegularAnteriores(2, cicloEpg);
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

    }

    private void generarPregrado(CicloAcademico ciclo, DataSessionPivot ds) {

        List<String> situacionesNoAptas
                = Arrays.asList(
                        S_D.getValue(), S_4.getValue(), S_X.getValue(), S_XD.getValue(), S_4U.getValue(), S_E.getValue(),
                        S_7.getValue(), S_4T.getValue(), S_Q.getValue(), S_R.getValue(),
                        S_6.getValue(), S_T.getValue());

        List<Alumno> alumnosPregrado = alumnoDAO.allByModalidadSituacionesNoAptas(ModalidadEstudioEnum.PRE, situacionesNoAptas);
        CicloAcademico cicloBD = cicloAcademicoDAO.find(ciclo);

        List<RetiroCiclo> retiroCiclos = retiroCicloDAO.allByCicloCondicional(ciclo);
        List<Reincorporacion> reincorporacion = reincorporacionDAO.allByCicloReincorporacion(ciclo);
        List<CambioNota> cambioNota = cambioNotaDAO.allByCicloRegistro(ciclo);
        List<Alumno> alumosConTramite = retiroCiclos.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        alumosConTramite.addAll(reincorporacion.stream().map(x -> x.getAlumno()).collect(Collectors.toList()));
        alumosConTramite.addAll(cambioNota.stream().filter(x -> x.getEsCondicional()).map(x -> x.getAlumno()).collect(Collectors.toList()));

        ModalidadEstudio modalidad = cicloBD.getModalidadEstudio();
        List<MatriculaResumen> matriculables = new ArrayList();

        Map<Long, Reincorporacion> mapReincorporacion = TypesUtil.convertListToMap("alumno.id", reincorporacion);

        Map<Long, Alumno> mapMatriculableExist = new LinkedHashMap();
        List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allHabilesByCiclo(cicloBD);
        for (MatriculaResumen matriculaResumen : matriculaResumens) {
            mapMatriculableExist.put(matriculaResumen.getAlumno().getId(), matriculaResumen.getAlumno());
        }

        for (Alumno alumno : alumnosPregrado) {
            if (alumno.getModalidadEstudio().isPostgrado()) {
                continue;
            }
            Alumno alumnoExist = mapMatriculableExist.get(alumno.getId());
            if (alumnoExist != null) {
                continue;
            }

            if (modalidad.getCodigoEnum() != alumno.getModalidadEstudio().getCodigoEnum()) {
                System.out.println("Se bota porque su modalidad es " + alumno.getModalidadEstudio().getCodigo());
                continue;
            }
            System.out.println("AGREGAR " + alumno.getCodigo() + " " + alumno.getModalidadEstudio().getCodigo());

            MatriculaResumen resumen = new MatriculaResumen();
            resumen.setAlumno(alumno);
            resumen.setCicloAcademico(cicloBD);
            resumen.setSituacionInicio(alumno.getSituacionAcademica());
            resumen.settingValoresDefecto();
            resumen.setCreditosPagados(0);
            resumen.setCreditosConsumidos(0);

            mapMatriculableExist.put(alumno.getId(), alumno);
            matriculables.add(resumen);
        }

        for (Alumno alumnoCondicional : alumosConTramite) {
            if (alumnoCondicional.getModalidadEstudio().isPostgrado()) {
                continue;
            }

            Alumno alumnoExist = mapMatriculableExist.get(alumnoCondicional.getId());
            if (alumnoExist != null) {
                continue;
            }
            Boolean escondicional = true;
            Reincorporacion reincorpor = mapReincorporacion.get(alumnoCondicional.getId());
            if (reincorpor != null) {
                escondicional = reincorpor.getEsCondicional();
            }

            MatriculaResumen resumen = new MatriculaResumen();
            resumen.setAlumno(alumnoCondicional);
            resumen.setCicloAcademico(cicloBD);
            resumen.setSituacionInicio(alumnoCondicional.getSituacionAcademica());
            resumen.settingValoresDefecto();
            resumen.setCreditosPagados(0);
            resumen.setCreditosConsumidos(0);
            resumen.setEsCondicional(escondicional);
            mapMatriculableExist.put(alumnoCondicional.getId(), alumnoCondicional);
            matriculables.add(resumen);
        }
        matriculaResumenDAO.saveList(matriculables);

    }

    @Async
    @Override
    @Transactional
    public void calcularPromedios(String token22, DataSessionPivot ds) {
        String tokenHisto = token22 + TOKEN_HISTORIAL;
        for (;;) {
            if (visorCalculoNotas.estaCompletoToken(tokenHisto)) {
                logger.info("Terminó Historial ....");
                break;
            }
        }

        visorCalculoNotas.destroyToken(tokenHisto);
        String tokenProm = token22 + TOKEN_PROMEDIOS;

        List<Alumno> alumnos = visorCalculoNotas.allAlumnosByToken(tokenProm);

        TypesUtil.delay(2000);

        ListBeanPromedios bean = promedioLoadDataService.loadDataAlumno(alumnos);

        List<CicloAcademico> ciclos = bean.getCiclos();
        List<CicloAcademico> ciclosActivos = bean.getCiclosActivos();
        CicloAcademico cicloPregrado = bean.getCicloPregrado();
        CicloAcademico cicloPosgrado = bean.getCicloPosgrado();

        List<Egresado> egresados = bean.getEgresados();
        List<AlumnoCiclo> alumnosCiclosAll = bean.getAlumnosCiclosAll();
        List<AlumnoCicloCurso> alumnosCiclosCursosActivos = bean.getAlumnosCiclosCursosActivos();
        List<AlumnoCicloCurso> alumnosCiclosCursosAll = bean.getAlumnosCiclosCursosAll();
        List<Reincorporacion> reincorporaciones = bean.getReincorporaciones();

        Map<Long, List<AlumnoCiclo>> mapAlumnoCiclo = TypesUtil.convertListToMapList("alumno.id", alumnosCiclosAll);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoActivo = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosActivos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoAll = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosAll);
        Map<Long, List<Reincorporacion>> mapReincorporacion = TypesUtil.convertListToMapList("alumno.id", reincorporaciones);
        Map<Long, Egresado> mapEgresado = TypesUtil.convertListToMap("alumno.id", egresados);

        for (Alumno alumno : alumnos) {
            Egresado egresado = mapEgresado.get(alumno.getId());
            List<AlumnoCiclo> alumnoCiclos = TypesUtil.getListNotNull(mapAlumnoCiclo.get(alumno.getId()));
            List<AlumnoCicloCurso> alumnoCiclosCursosActivosByAlu = TypesUtil.getListNotNull(mapAlumnoCicloCursoActivo.get(alumno.getId()));
            List<AlumnoCicloCurso> alumnoCiclosCursosAllByAlu = TypesUtil.getListNotNull(mapAlumnoCicloCursoAll.get(alumno.getId()));
            List<Reincorporacion> reincorporacionesByAlumno = TypesUtil.getListNotNull(mapReincorporacion.get(alumno.getId()));

            ModalidadEstudioEnum modalidadEnum = alumno.getModalidadEstudio().getOperativeModalidadEnum();
            CicloAcademico cicloActivo = cicloPregrado;
            if (modalidadEnum == EPG) {
                cicloActivo = cicloPosgrado;
            }

            boolean showError = true;
            promedioService.promediarAllCicloAsync(
                    alumno,
                    cicloActivo,
                    egresado,
                    ciclos,
                    alumnoCiclos,
                    alumnoCiclosCursosActivosByAlu,
                    alumnoCiclosCursosAllByAlu,
                    reincorporacionesByAlumno,
                    ds,
                    tokenProm, false, showError);

        }
    }

    @Async
    @Override
    public void revisarCurriculaAlumnos(DataSessionPivot ds, String token22) {
        String tokenProm = token22 + TOKEN_PROMEDIOS;
        for (;;) {
            if (visorCalculoNotas.estaCompletoToken(tokenProm)) {
                break;
            }
        }
        logger.info("Terminó promedios ....");

        visorCalculoNotas.destroyToken(tokenProm);
        TypesUtil.delay(2000);

        String tokenCurri = token22 + TOKEN_CURRICULA;
        List<Alumno> alumnos = visorCalculoNotas.allAlumnosByToken(tokenCurri);
        avanceCurricularService.generarAvanceCurricularByAlumnosPregrados(alumnos, ds, tokenCurri);
    }

    @Async
    @Override
    @Transactional
    public void revisarMatriculables(DataSessionPivot ds, String token22) {
        String tokenCurri = token22 + TOKEN_CURRICULA;
        for (;;) {

            if (visorCalculoNotas.estaCompletoToken(tokenCurri)) {
                break;
            }
        }
        logger.info("Terminó curricula ....");
        TypesUtil.delay(2000);

        List<Alumno> alumnos = visorCalculoNotas.allAlumnosByToken(tokenCurri);
        alumnos = alumnoDAO.allByAlumnos(alumnos);
        visorCalculoNotas.destroyToken(tokenCurri);

        String tokenMat = token22 + TOKEN_MATRICULABLE;
        this.recalcularPrioridad(alumnos, ds.getUsuario(), tokenMat);
        logger.info("Se terminó el ingreso a matriculables ... ");
    }

    @Override
    public void revisarSituacionesAcademicas(CicloAcademico ciclo, DataSessionPivot ds) {
        CicloAcademico cicloBD = cicloAcademicoDAO.find(ciclo.getId());
        List<MatriculaResumen> matriculables = matriculaResumenDAO.allHabilesByCiclo(cicloBD);
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
        }
    }

    @Override
    @Transactional
    public void generarPrioridad(CicloAcademico cicloForm) {

        DateTime today = new DateTime();
        CicloAcademico cicloBD = cicloAcademicoDAO.find(cicloForm.getId());

        if (cicloBD.getFechaMatriculables() == null) {
            throw new PhobosException("Primero debe generar los Alumnos matriculables");
        }

        cicloBD.setFechaPrioridades(today.toDate());
        cicloAcademicoDAO.updateFechaPrioridades(cicloBD);

        List<AlumnoCiclo> alumnosCiclos = alumnoCicloDAO.allActivosRegularesByCicloResumen(cicloBD);
        Map<Long, AlumnoCiclo> mapAlumnoCiclo = TypesUtil.convertListToMap("alumno.id", alumnosCiclos);
        List<MatriculaResumen> matriculables = matriculaResumenDAO.allMatriculablesByCiclo(cicloBD);
        List<Alumno> alumnos = matriculables.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

        int cachimbos = 8000;
        int escuela = 10000;
        List<RetiroCiclo> retiroCiclos = retiroCicloDAO.allAlumnosByCicloCondicional(alumnos, cicloForm);
        Map<Long, RetiroCiclo> mapAlumnoTramiteRetiro = TypesUtil.convertListToMap("alumno.id", retiroCiclos);

        for (MatriculaResumen matriculable : matriculables) {
            matriculable.setPrioridad(null);
            matriculable.setPuntajePrioridad(null);
            matriculable.setTurnoAtencion(null);

            Alumno alumno = matriculable.getAlumno();
            SituacionAcademica sit = alumno.getSituacionAcademica();

            if (Arrays.asList(S_8, S_9).contains(sit.getCodigoEnum()) && cicloBD.isTipoRegular()) {
                matriculable.setPrioridad(BigDecimal.valueOf(cachimbos));
                matriculable.setPuntajePrioridad(BigDecimal.ZERO);
                cachimbos++;
                continue;
            }

            if (alumno.isPostgrado() || alumno.isEspecial()) {
                matriculable.setPrioridad(BigDecimal.valueOf(escuela));
                escuela++;
                continue;
            }

            AlumnoCiclo alumnoCiclo;
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
        Collections.sort(matriculablesConPuntaje, new MatriculaResumen.ComparePuntajeCapa());

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
            FileHelper.createDirectory(GlobalConstantine.TMP_DIR);
            String absoluteName = GlobalConstantine.TMP_DIR + fileName;
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
        List<MatriculaResumen> matriculables = matriculaResumenDAO.allHabilesByCiclo(cicloBD);
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
        matriculaResumenDAO.deleteMatriculable(cicloBD);
    }

    @Override
    public List<Alumno> allAlumnoByNombre(String nombre, DataSessionPivot ds) {
        return alumnoDAO.allByNameSinMatriculaResumen(nombre, ds.getCicloAcademico());
    }

    @Override
    @Transactional
    public String saveMatriculable(Alumno alumnoForm, CicloAcademico ciclo, DataSessionPivot ds) {

        Alumno alumno = alumnoDAO.find(alumnoForm);
        MatriculaResumen resumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, ciclo);
        Assert.isNull(resumen, "Este alumno ya es matriculable");

        resumen = new MatriculaResumen();
        resumen.setAlumno(alumno);
        resumen.setCicloAcademico(ciclo);
        resumen.setSituacionInicio(alumno.getSituacionAcademica());
        resumen.settingValoresDefecto();
        resumen.setCreditosPagados(0);
        resumen.setCreditosConsumidos(0);
        resumen.setEstadoEnum(NMAT);
        matriculaResumenDAO.save(resumen);

        alumno.setEsMatriculaCondicional(alumnoForm.getEsMatriculaCondicional());
        alumno.setMotivoMatriculable(alumnoForm.getMotivoMatriculable());
        alumno.setEsMatBeneficioUltCicl(alumnoForm.getEsMatBeneficioUltCicl());
        List<Alumno> alumnos = new ArrayList();
        alumnos.add(alumno);

        String token = RandomStringUtils.randomAlphanumeric(43);
        String tokenMatri = token + TOKEN_MATRICULABLE;

        visorCalculoNotas.createToken(tokenMatri, alumnos);
        this.recalcularPrioridad(alumnos, ds.getUsuario(), tokenMatri);
        return token;
    }

    @Async
    @Override
    public void generarAportes(DataSessionPivot ds, String token22) {
        String tokenMatricula = token22 + TOKEN_MATRICULABLE;
        for (;;) {
            if (visorCalculoNotas.estaCompletoToken(tokenMatricula)) {
                break;
            }
        }
        TypesUtil.delay(2000);
        List<Alumno> alumnos = visorCalculoNotas.allAlumnosByToken(tokenMatricula);

        visorCalculoNotas.destroyToken(tokenMatricula);
        List<MatriculaResumen> matriculaResumens = matriculaResumenDAO.allByAlumnosCiclo(alumnos, ds.getCicloAcademico());
        Map<Long, MatriculaResumen> mapMatriculables = TypesUtil.convertListToMap("alumno.id", matriculaResumens);
        for (Alumno alumno : alumnos) {

            MatriculaResumen matriculaResumen = mapMatriculables.get(alumno.getId());

            CicloAcademico ciclo = cicloAcademicoDAO.findByCodigoModalidadEstudio(ds.getCicloAcademico().getCodigo(), alumno.getModalidadEstudio());
            aporteAlumnoService.generarAportes(alumno, ciclo, matriculaResumen, ds);
            logger.debug("enviando generar boletas del alumno {} en el ciclo {} con matri-resumen {}", alumno.getId(), ciclo.getId(), matriculaResumen.getId());
        }
    }

    @Override
    public void generarAportes(Alumno alumnoForm, CicloAcademico ciclo, DataSessionPivot ds) {
        Alumno alumno = alumnoDAO.findAllInfo(alumnoForm.getId());
        MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, ciclo);
        aporteAlumnoService.generarAportes(alumno, ciclo, matriculaResumen, ds);

    }

    @Override
    @Transactional(readOnly = false)
    public void generarVerano(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        DateTime today = new DateTime();

        List<String> situacionesNoAptas
                = Arrays.asList(
                        S_D.getValue(), S_4.getValue(), S_X.getValue(), S_XD.getValue(), S_4U.getValue(), S_E.getValue(),
                        S_7.getValue(), S_4T.getValue(), S_Q.getValue(), S_R.getValue(), S_8.getValue());

        List<Alumno> alumnosPregrado = alumnoDAO.allByModalidadSituacionesNoAptas(ModalidadEstudioEnum.PRE, situacionesNoAptas);

        List<MatriculaResumen> matriculables = new ArrayList();
        for (Alumno alumno : alumnosPregrado) {
            MatriculaResumen resumen = new MatriculaResumen();
            resumen.setAlumno(alumno);
            resumen.setCicloAcademico(cicloAcademico);
            resumen.setSituacionInicio(alumno.getSituacionAcademica());
            resumen.settingValoresDefecto();
            resumen.setCreditosPagados(0);
            resumen.setCreditosConsumidos(0);
            matriculables.add(resumen);
        }

        matriculaResumenDAO.saveList(matriculables);

        CicloAcademico cicloAcademicoUpd = new CicloAcademico();
        cicloAcademicoUpd.setId(cicloAcademico.getId());
        cicloAcademicoUpd.setFechaMatriculables(today.toDate());
        cicloAcademicoDAO.updateFechaMatriculables(cicloAcademicoUpd);
    }

    private void asignarPrioridad(
            Alumno alumno,
            CicloAcademico cicloActivo,
            List<MatriculaResumen> matriculables,
            Map<Long, MatriculaResumen> mapMatriculable,
            List<TurnoAtencion> turnosAtenciones,
            Map<Long, AlumnoCiclo> mapAlumnoCiclo) {

        if (!alumno.isPregrado()) {
            System.out.println("Alumno " + alumno.getCodigo() + " no es de pregrado");
            return;
        }

        MatriculaResumen matriculable = mapMatriculable.get(alumno.getId());
        if (matriculable == null) {
            System.out.println("Alumno " + alumno.getCodigo() + " no es de matriculable");
            return;
        }

        if (!Arrays.asList(NMAT, MAT).contains(matriculable.getEstadoEnum())) {
            matriculable.setPrioridad(null);
            matriculable.setTurnoAtencion(null);
            System.out.println("Alumno " + alumno.getCodigo() + " es un matriculable deshabilitado");
            return;
        }

        List<String> situacionesNoAptas = Arrays.asList(
                S_D.getValue(), S_4.getValue(), S_X.getValue(), S_XD.getValue(), S_8.getValue(),
                S_7.getValue(), S_4T.getValue(), S_Q.getValue(), S_R.getValue(), S_E.getValue());

        if (cicloActivo.isTipoRegular()) {
            situacionesNoAptas = Arrays.asList(
                    S_D.getValue(), S_4.getValue(), S_X.getValue(), S_XD.getValue(),
                    S_7.getValue(), S_4T.getValue(), S_Q.getValue(), S_R.getValue(), S_E.getValue());
        }

        SituacionAcademica situacion = alumno.getSituacionAcademica();
        if (situacionesNoAptas.contains(situacion.getCodigo()) && !alumno.getEsMatriculableSuspendido()) {
            matriculable.setPrioridad(null);
            matriculable.setTurnoAtencion(null);
            matriculable.setEstadoEnum(INH);
            matriculaResumenDAO.update(matriculable);
            System.out.println("Alumno " + alumno.getCodigo() + " tiene una situacion-academica no apta");
            return;
        }

        AlumnoCiclo alumnoCiclo = mapAlumnoCiclo.get(alumno.getId());
        if (alumnoCiclo == null) {
            alumnoCiclo = new AlumnoCiclo();
            alumnoCiclo.setCreditosAcumulados(0);
            alumnoCiclo.setCreditosAprobadosAcumulados(0);
            alumnoCiclo.setCreditosConvalidadosAcumulados(0);
            alumnoCiclo.setCreditosCursadosCiclo(0);
            alumnoCiclo.setCreditosAprobadosCiclo(0);
            alumnoCiclo.setPromedioCiclo(BigDecimal.ZERO);
        }

        matriculableConector.procesarPrioridadAlumno(matriculable, alumnoCiclo);
        if (matriculable.getPuntajePrioridad() == null) {
            matriculable.setPuntajePrioridad(BigDecimal.ZERO);
        }

        boolean esUltimoCiclo = alumno.isPerteneceUltimoCiclo();
        System.out.print("alumno.codigo=" + alumno.getCodigo());
        System.out.print("\tmatriculaResumen.puntaje=" + matriculable.getPuntajePrioridad());
        System.out.println("\tesUltimoCiclo=" + esUltimoCiclo);

        MatriculaResumen puntajeMenor = this.findMatriculableWithPuntajeMenor(matriculable, matriculables, esUltimoCiclo, cicloActivo);
        MatriculaResumen puntajeMayor = this.findMatriculableWithPuntajeMayor(matriculable, matriculables, esUltimoCiclo);
        BigDecimal prioridad = null;

        if (puntajeMayor == null) {
            puntajeMayor = new MatriculaResumen();

            if (esUltimoCiclo) {
                puntajeMayor.setPrioridad(BigDecimal.ZERO);
            } else {
                MatriculaResumen prioridadMin = this.findMatriculableWithPrioridadMin(matriculables, esUltimoCiclo, cicloActivo);
                puntajeMayor.setPrioridad(prioridadMin.getPrioridad());
            }
        }

        if (puntajeMenor == null) {
            puntajeMenor = new MatriculaResumen();

            if (esUltimoCiclo) {
                MatriculaResumen prioridadMin = this.findMatriculableWithPrioridadMin(matriculables, esUltimoCiclo, cicloActivo);
                puntajeMenor.setPrioridad(prioridadMin.getPrioridad());
            } else {
                MatriculaResumen prioridadMax = this.findMatriculableWithPrioridadMax(matriculables, esUltimoCiclo, cicloActivo);
                prioridad = prioridadMax.getPrioridad().add(BigDecimal.ONE);
                puntajeMenor.setPrioridad(prioridad.add(BigDecimal.ONE));

            }

        }

        if (prioridad == null) {
            prioridad = puntajeMenor.getPrioridad().add(puntajeMayor.getPrioridad()).divide(new BigDecimal(2), 4, RoundingMode.FLOOR);
        }

        matriculable.setPrioridad(prioridad);
        System.out.println("prioridad=" + prioridad);

        if (cicloActivo.getFechaTurnosAsignados() != null) {
            TurnoAtencion turnoAtencion = findTurnoByPrioridad(prioridad, turnosAtenciones);
            if (turnoAtencion == null) {
                if (prioridad.subtract(BigDecimal.ONE).compareTo(BigDecimal.ZERO) < 0) {
                    BigDecimal prioridadNew = prioridad.add(BigDecimal.ONE);
                    turnoAtencion = findTurnoByPrioridad(prioridadNew, turnosAtenciones);
                    turnoAtencion.setPrioridadInicio(prioridad);

                } else {
                    BigDecimal prioridadNew = prioridad.subtract(BigDecimal.ONE);
                    turnoAtencion = findTurnoByPrioridad(prioridadNew, turnosAtenciones);
                    turnoAtencion.setPrioridadFin(prioridad);
                }
            }

            matriculable.setTurnoAtencion(turnoAtencion);
        }

        System.out.print("PuntajeMayor.prioridad=" + puntajeMayor.getPrioridad());
        System.out.print("\tPuntajeMenor.prioridad=" + puntajeMenor.getPrioridad());
        System.out.println("\tMatriculable.prioridad=" + matriculable.getPrioridad());
    }

    private TurnoAtencion findTurnoByPrioridad(BigDecimal prioridad, List<TurnoAtencion> turnos) {
        for (TurnoAtencion turno : turnos) {
            if (turno.getPrioridadInicio().compareTo(prioridad) <= 0
                    && turno.getPrioridadFin().compareTo(prioridad) >= 0) {
                return turno;
            }
        }
        return turnos.get(turnos.size() - 1);
    }

    private MatriculaResumen findMatriculableWithPrioridadMin(
            List<MatriculaResumen> matriculables,
            boolean esUltimoCiclo,
            CicloAcademico cicloAcademico) {

        Collections.sort(matriculables, new MatriculaResumen.ComparePrioridadAsc());
        for (MatriculaResumen matriculable : matriculables) {
            Alumno alumno = matriculable.getAlumno();
            if (!alumno.isPregrado()) {
                continue;
            }
            if (alumno.isPerteneceUltimoCiclo() && !esUltimoCiclo) {
                continue;
            }
            if (!alumno.isPerteneceUltimoCiclo() && esUltimoCiclo) {
                continue;
            }
            if (matriculable.getPrioridad() == null) {
                continue;
            }
            if (cicloAcademico.isTipoRegular()) {
                if (Arrays.asList(S_8.getValue(), S_9.getValue()).contains(alumno.getSituacionAcademica().getCodigo())) {
                    continue;
                }
            }
            return matriculable;
        }
        return null;
    }

    private MatriculaResumen findMatriculableWithPrioridadMax(
            List<MatriculaResumen> matriculables,
            boolean esUltimoCiclo,
            CicloAcademico cicloAcademico) {

        Collections.sort(matriculables, new MatriculaResumen.ComparePrioridadDesc());
        for (MatriculaResumen matriculable : matriculables) {
            Alumno alumno = matriculable.getAlumno();
            if (!alumno.isPregrado()) {
                continue;
            }
            if (alumno.isPerteneceUltimoCiclo() && !esUltimoCiclo) {
                continue;
            }
            if (!alumno.isPerteneceUltimoCiclo() && esUltimoCiclo) {
                continue;
            }
            if (matriculable.getPrioridad() == null) {
                continue;
            }
            if (cicloAcademico.isTipoRegular()) {
                if (Arrays.asList(S_8.getValue(), S_9.getValue()).contains(alumno.getSituacionAcademica().getCodigo())) {
                    continue;
                }
            }
            return matriculable;
        }
        return null;
    }

    private MatriculaResumen findMatriculableWithPuntajeMenor(
            MatriculaResumen matriculableMain,
            List<MatriculaResumen> matriculables,
            boolean esUltimoCiclo,
            CicloAcademico cicloAcademico) {

        if (matriculableMain.getPuntajePrioridad() == null) {
            return null;
        }
        Collections.sort(matriculables, new MatriculaResumen.ComparePuntajeDescPrioridadAsc());
        Alumno alumnoMain = matriculableMain.getAlumno();

        List<MatriculaResumen> matblesiguales = new ArrayList();
        for (MatriculaResumen matriculable : matriculables) {
            if (matriculableMain.getId() == matriculable.getId().longValue()) {
                continue;
            }

            Alumno alumno = matriculable.getAlumno();
            if (!alumno.isPregrado()) {
                continue;
            }
            if (alumno.isPerteneceUltimoCiclo() && !esUltimoCiclo) {
                continue;
            }
            if (!alumno.isPerteneceUltimoCiclo() && esUltimoCiclo) {
                continue;
            }
            if (cicloAcademico.isTipoRegular()) {
                if (Arrays.asList(S_8.getValue(), S_9.getValue()).contains(alumno.getSituacionAcademica().getCodigo())) {
                    continue;
                }
            }
            if (matriculable.getPuntajePrioridad() == null) {
                continue;
            }
            if (matriculable.getPrioridad() != null && matriculableMain.getPuntajePrioridad() != null) {
                if (matriculable.getPuntajePrioridad().compareTo(matriculableMain.getPuntajePrioridad()) == 0) {
                    matblesiguales.add(matriculable);
                    continue;
                }
                if (!matblesiguales.isEmpty()) {
                    break;
                }
                if (matriculable.getPuntajePrioridad().compareTo(matriculableMain.getPuntajePrioridad()) < 0) {
                    return matriculable;
                }
            }
        }
        if (matblesiguales.isEmpty()) {
            return null;
        }
        if (matblesiguales.size() == 1) {
            return matblesiguales.get(0);
        }

        Collections.sort(matblesiguales, new MatriculaResumen.CompareCapaAsc());
        for (MatriculaResumen matriculable : matblesiguales) {
            Alumno alumno = matriculable.getAlumno();
            if (alumnoMain.getCreditosAprobadosConvalidados().compareTo(alumno.getCreditosAprobadosConvalidados()) <= 0) {
                return matriculable;
            }
        }

        return matblesiguales.get(matblesiguales.size() - 1);

    }

    private MatriculaResumen findMatriculableWithPuntajeMayor(
            MatriculaResumen matriculableMain,
            List<MatriculaResumen> matriculables,
            boolean esUltimoCiclo) {

        if (matriculableMain.getPuntajePrioridad() == null) {
            return null;
        }
        Collections.sort(matriculables, new MatriculaResumen.ComparePuntajeAscPrioridadDesc());
        Alumno alumnoMain = matriculableMain.getAlumno();

        List<MatriculaResumen> matblesiguales = new ArrayList();
        for (MatriculaResumen matriculable : matriculables) {
            if (matriculableMain.getId() == matriculable.getId().longValue()) {
                continue;
            }

            Alumno alumno = matriculable.getAlumno();
            if (!alumno.isPregrado()) {
                continue;
            }
            if (alumno.isPerteneceUltimoCiclo() && !esUltimoCiclo) {
                continue;
            }
            if (!alumno.isPerteneceUltimoCiclo() && esUltimoCiclo) {
                continue;
            }
            //if (cicloAcademico.isTipoRegular()) {
            if (Arrays.asList(S_8.getValue(), S_9.getValue()).contains(alumno.getSituacionAcademica().getCodigo())) {
                continue;
            }
            //}
            if (matriculable.getPuntajePrioridad() == null) {
                continue;
            }
            if (matriculable.getPrioridad() != null && matriculableMain.getPuntajePrioridad() != null) {
                if (matriculable.getPuntajePrioridad().compareTo(matriculableMain.getPuntajePrioridad()) == 0) {
                    matblesiguales.add(matriculable);
                    continue;
                }
                if (!matblesiguales.isEmpty()) {
                    break;
                }
                if (matriculable.getPuntajePrioridad().compareTo(matriculableMain.getPuntajePrioridad()) > 0) {
                    return matriculable;
                }
            }
        }
        if (matblesiguales.isEmpty()) {
            return null;
        }
        if (matblesiguales.size() == 1) {
            return matblesiguales.get(0);
        }

        Collections.sort(matblesiguales, new MatriculaResumen.CompareCapaDesc());
        for (MatriculaResumen matriculable : matblesiguales) {
            Alumno alumno = matriculable.getAlumno();
            if (alumnoMain.getCreditosAprobadosConvalidados().compareTo(alumno.getCreditosAprobadosConvalidados()) >= 0) {
                return matriculable;
            }
        }

        return matblesiguales.get(matblesiguales.size() - 1);

    }

    @Override
    @Transactional
    public void inhabilitarMatriculable(MatriculaResumen matriculaResumenForm, DataSessionPivot ds) {

        CicloAcademico academico = cicloAcademicoDAO.find(ds.getCicloAcademico());

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

        if (ds.getCicloAcademico().getTipoEnum() == TipoCicloEnum.REG && academico.getFechaTurnosAsignados() != null) {
            TokenIngresante token = responseRestService.createToken(ds);
            JsonResponse jsonResponse = responseRestService.anularBoletas(matriculaResumen, ds, token);
            Assert.isTrue(jsonResponse.getSuccess(), jsonResponse.getMessage());
        }

        matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.INH);
        matriculaResumen.setMotivoMatriculable(matriculaResumenForm.getMotivoMatriculable());
        matriculaResumenDAO.update(matriculaResumen);
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recalcularPrioridad(GrupoSeccion gpoSecc, CicloAcademico ciclo) {
        List<Alumno> alumnos = alumnoDAO.allMatriculadosByGpoSeccion(gpoSecc);
        CicloAcademico cicloSgte = cicloAcademicoDAO.findSiguienteConfOrAct(ciclo);

        List<MatriculaResumen> matriculables = matriculaResumenDAO.allByCiclo(cicloSgte);
        Map<Long, MatriculaResumen> mapMatriculable = TypesUtil.convertListToMap("alumno.id", matriculables);

        List<AlumnoCiclo> alumnosCiclos = alumnoCicloDAO.allActivosRegularesByCicloResumen(cicloSgte);
        Map<Long, AlumnoCiclo> mapAlumnoCiclo = TypesUtil.convertListToMap("alumno.id", alumnosCiclos);

        if (cicloSgte.getFechaPrioridades() != null) {
            EventoAcademicoEnum eventoEnum = cicloSgte.isTipoRegular() ? MAT_REG : MAT_VER;
            List<TurnoAtencion> turnos = turnoAtencionDAO.allByCicloEventoEnum(cicloSgte, eventoEnum);

            for (Alumno alumno : alumnos) {
                asignarPrioridad(alumno, cicloSgte, matriculables, mapMatriculable, turnos, mapAlumnoCiclo);
            }
            for (MatriculaResumen matriculable : matriculables) {
                matriculaResumenDAO.update(matriculable);
            }
            for (TurnoAtencion turno : turnos) {
                turnoAtencionDAO.update(turno);
            }
        }

    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recalcularPrioridad(GrupoSeccion grupoSeccion, Usuario usuario) {
        List<Alumno> alumnos = alumnoDAO.allMatriculadosByGpoSeccion(grupoSeccion);
        recalcularPrioridad(alumnos, usuario, null);
    }

    private void recalcularPrioridad(List<Alumno> alumnos, Usuario usuario, String tokenMat) {

        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo(ModalidadEstudioEnum.PRE);

        List<String> situacionesNoAptas = Arrays.asList(
                S_D.getValue(), S_4.getValue(), S_X.getValue(), S_XD.getValue(), S_4U.getValue(), S_8.getValue(),
                S_7.getValue(), S_4T.getValue(), S_Q.getValue(), S_R.getValue(), S_E.getValue());

        List<String> situacionesExepcionAptas = Arrays.asList(
                S_X.getValue(), S_4.getValue(), S_4U.getValue());

        if (cicloActivo.isTipoRegular()) {
            situacionesNoAptas = Arrays.asList(
                    S_D.getValue(), S_4.getValue(), S_X.getValue(), S_XD.getValue(), S_4U.getValue(),
                    S_7.getValue(), S_4T.getValue(), S_Q.getValue(), S_R.getValue(), S_E.getValue());
        }

        List<Reincorporacion> reincorporacions = reincorporacionDAO.allAceptadasPendientesByAlumnosCiclo(alumnos, cicloActivo);
        Map<Long, Reincorporacion> mapReincorporacion = TypesUtil.convertListToMap("alumno.id", reincorporacions);

        List<MatriculaResumen> matriculables = matriculaResumenDAO.allByCiclo(cicloActivo);
        Map<Long, MatriculaResumen> mapMatriculable = TypesUtil.convertListToMap("alumno.id", matriculables);

        if (cicloActivo.getFechaMatriculables() != null) {
            for (Alumno alumno : alumnos) {
                MatriculaResumen matriculable = mapMatriculable.get(alumno.getId());
                if (situacionesExepcionAptas.contains(alumno.getSituacionAcademica().getCodigo())) {
                    if (mapReincorporacion.get(alumno.getId()) != null || alumno.getEsMatBeneficioUltCicl() || alumno.getEsMatriculaCondicional()) {
                        alumno.setEsMatriculableSuspendido(Boolean.TRUE);
                        this.addMatriculable(matriculable, alumno, cicloActivo, matriculables, mapMatriculable, usuario);
                        continue;
                    }
                }
                if (situacionesNoAptas.contains(alumno.getSituacionAcademica().getCodigo())) {
                    if (matriculable != null && Arrays.asList(NMAT, MAT).contains(matriculable.getEstadoEnum())) {
                        matriculable.setEstadoEnum(INH);
                        matriculaResumenDAO.update(matriculable);
                    }
                } else {
                    this.addMatriculable(matriculable, alumno, cicloActivo, matriculables, mapMatriculable, usuario);
                }
            }
        }

        if (cicloActivo.getFechaPrioridades() != null) {
            EventoAcademicoEnum eventoEnum = cicloActivo.isTipoRegular() ? MAT_REG : MAT_VER;
            List<TurnoAtencion> turnos = turnoAtencionDAO.allByCicloEventoEnum(cicloActivo, eventoEnum);

            List<AlumnoCiclo> alumnosCiclos = alumnoCicloDAO.allActivosRegularesByCicloResumen(cicloActivo);
            Map<Long, AlumnoCiclo> mapAlumnoCiclo = TypesUtil.convertListToMap("alumno.id", alumnosCiclos);

            for (Alumno alumno : alumnos) {
                asignarPrioridad(alumno, cicloActivo, matriculables, mapMatriculable, turnos, mapAlumnoCiclo);

                visorCalculoNotas.incrementarToken(tokenMat);
            }
            for (MatriculaResumen matriculable : matriculables) {
                matriculaResumenDAO.update(matriculable);
            }
            for (TurnoAtencion turno : turnos) {
                turnoAtencionDAO.update(turno);
            }
        }
    }

    private void addMatriculable(MatriculaResumen matriculable, Alumno alumno, CicloAcademico cicloActivo, List<MatriculaResumen> matriculables, Map<Long, MatriculaResumen> mapMatriculable, Usuario usuario) {
        if (matriculable != null && !Arrays.asList(NMAT, MAT).contains(matriculable.getEstadoEnum())) {
            matriculable.setEstadoEnum(NMAT);
            matriculable.setEsCondicional(alumno.getEsMatriculaCondicional());
            matriculaResumenDAO.update(matriculable);

        } else if (matriculable == null && alumno.isPregrado()) {
            matriculable = new MatriculaResumen();
            matriculable.setAlumno(alumno);
            matriculable.setCicloAcademico(cicloActivo);
            matriculable.setSituacionInicio(alumno.getSituacionAcademica());
            matriculable.settingValoresDefecto();
            matriculable.setEsCondicional(alumno.getEsMatriculaCondicional());
            matriculable.setMotivoMatriculable(alumno.getMotivoMatriculable());
            matriculable.setCreditosPagados(0);
            matriculable.setCreditosConsumidos(0);
            matriculable.setFechaRegistro(new Date());
            matriculable.setUserRegistro(usuario);
            matriculaResumenDAO.save(matriculable);

            matriculables.add(matriculable);
            mapMatriculable.put(alumno.getId(), matriculable);
        }
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revisarPrioridad(CicloAcademico cicloForm, DataSessionPivot ds) {
        CicloAcademico cicloActivo = cicloAcademicoDAO.find(cicloForm.getId());
        List<MatriculaResumen> matriculables = matriculaResumenDAO.allByCiclo(cicloActivo);
        Map<Long, MatriculaResumen> mapMatriculable = TypesUtil.convertListToMap("alumno.id", matriculables);
        Collections.sort(matriculables, new MatriculaResumen.ComparePrioridadAsc());
        List<Alumno> alumnos = matriculables.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

        List<AlumnoCiclo> alumnosCiclos = alumnoCicloDAO.allActivosRegularesByCicloResumen(cicloActivo);
        Map<Long, AlumnoCiclo> mapAlumnoCiclo = TypesUtil.convertListToMap("alumno.id", alumnosCiclos);

        if (cicloActivo.getFechaPrioridades() != null) {
            EventoAcademicoEnum eventoEnum = cicloActivo.isTipoRegular() ? MAT_REG : MAT_VER;
            List<TurnoAtencion> turnos = turnoAtencionDAO.allByCicloEventoEnum(cicloActivo, eventoEnum);

            for (Alumno alumno : alumnos) {
                asignarPrioridad(alumno, cicloActivo, matriculables, mapMatriculable, turnos, mapAlumnoCiclo);
            }
            for (MatriculaResumen matriculable : matriculables) {
                matriculaResumenDAO.update(matriculable);
            }
            for (TurnoAtencion turno : turnos) {
                turnoAtencionDAO.update(turno);
            }
        }
    }

    @Async
    @Override
    public void verificarAlumnosNmat(DataSessionPivot ds, List<AlumnoCiclo> alumnoCiclosForm) {

        List<Alumno> alumnos = alumnoCiclosForm.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        alumnos = alumnoDAO.allInfoByAlumnos(alumnos);
        TypesUtil.delay(2000);

        String token = RandomStringUtils.randomAlphanumeric(34);
        visorCalculoNotas.createToken(token, alumnos);

        ListBeanPromedios bean = promedioLoadDataService.loadDataAlumno(alumnos);

        CicloAcademico ciclo = ds.getCicloAcademico();

        List<CicloAcademico> ciclosAcademicos = bean.getCiclos();
        List<CicloAcademico> ciclosActivos = bean.getCiclosActivos();
        Map<String, CicloAcademico> mapCiclo = TypesUtil.convertListToMap("modalidadEstudio.codigo", ciclosActivos);

        List<Egresado> egresados = bean.getEgresados();
        List<AlumnoCiclo> alumnosCiclosAll = bean.getAlumnosCiclosAll();
        List<AlumnoCicloCurso> alumnosCiclosCursosActivos = bean.getAlumnosCiclosCursosActivos();
        List<AlumnoCicloCurso> alumnosCiclosCursosAll = bean.getAlumnosCiclosCursosAll();
        List<Reincorporacion> reincorporaciones = bean.getReincorporaciones();

        Map<Long, List<AlumnoCiclo>> mapAlumnoCiclo = TypesUtil.convertListToMapList("alumno.id", alumnosCiclosAll);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoActivos = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosActivos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCursoAll = TypesUtil.convertListToMapList("alumnoCiclo.alumno.id", alumnosCiclosCursosAll);
        Map<Long, List<Reincorporacion>> mapReincorporaciones = TypesUtil.convertListToMapList("alumno.id", reincorporaciones);
        Map<Long, Egresado> mapEgresados = TypesUtil.convertListToMap("alumno.id", egresados);

        for (Alumno alumno : alumnos) {
            CicloAcademico cicloActivo = mapCiclo.get(alumno.getModalidadEstudio().getCodigo());
            List<AlumnoCiclo> allAlumnoCiclos = TypesUtil.getListNotNull(mapAlumnoCiclo.get(alumno.getId()));
            List<AlumnoCicloCurso> alumnoCicloCursosActivos = TypesUtil.getListNotNull(mapAlumnoCicloCursoActivos.get(alumno.getId()));
            List<AlumnoCicloCurso> alumnoCicloCursosAll = TypesUtil.getListNotNull(mapAlumnoCicloCursoAll.get(alumno.getId()));
            Egresado egresado = mapEgresados.get(alumno.getId());
            List<Reincorporacion> reincorporados = TypesUtil.getListNotNull(mapReincorporaciones.get(alumno.getId()));
            logger.info("Alumno codigo {}", alumno.getCodigo());
            logger.info("Cantidad de {} total {}", respositorVisor.getContador(), respositorVisor.getCantidadTotal());

            promedioService.promediarAllCicloAsync(
                    alumno,
                    cicloActivo,
                    egresado,
                    ciclosAcademicos,
                    allAlumnoCiclos,
                    alumnoCicloCursosActivos,
                    alumnoCicloCursosAll,
                    reincorporados, ds, token, true, false);

        }

        for (;;) {
            for (;;) {
                if (respositorVisor.getContador() < visorCalculoNotas.getCantidadByToken(token)) {
                    System.out.println("Ya van " + visorCalculoNotas.getCantidadByToken(token) + " alumnos procesados");
                    respositorVisor.incrementar();
                    logger.info("Cantidad de {} total {}", respositorVisor.getContador(), respositorVisor.getCantidadTotal());
                } else {
                    break;
                }
            }
            if (visorCalculoNotas.estaCompletoToken(token)) {
                break;
            }
        }

        promedioService.verificarAlumnosNmat(ciclo);

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
        CicloAcademico ciclo = ds.getCicloAcademico();
        List<MatriculaResumen> matriculable = matriculaResumenDAO.allUltimosCiclosMatriculables(ciclo);
        List<Alumno> alumnos = matriculable.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByAlumnosReg(alumnos);
        Map<Long, List<AlumnoCiclo>> map = TypesUtil.convertListToMapList("alumno.id", alumnoCiclos);
        for (MatriculaResumen matriculaResumen : matriculable) {
            List<AlumnoCiclo> alumnoCiclo = map.get(matriculaResumen.getAlumno().getId());
            matriculaResumen = matriculableConector.procesarPrioridadAlumno(matriculaResumen, alumnoCiclo.get(0));

            MatriculaResumen matriculaAnt = matriculaResumenDAO.findByAntPrioridadTemp(matriculaResumen, ciclo, (matriculaResumen.getAlumno().getCreditosCarreraAprobados() > CAPA_ULTIMO_CICLO));
            MatriculaResumen matriculaDes = matriculaResumenDAO.findByDesPrioridadTemp(matriculaResumen, ciclo, (matriculaResumen.getAlumno().getCreditosCarreraAprobados() > CAPA_ULTIMO_CICLO));
            if (matriculaAnt != null && matriculaDes != null) {

                BigDecimal prioridad = matriculaAnt.getPrioridad().add(matriculaDes.getPrioridad()).divide(new BigDecimal(2), 4, RoundingMode.FLOOR);
                BigDecimal bigDecimal = new BigDecimal(0.5);
                if (matriculaResumen.getPrioridad().compareTo(prioridad) <= 0 || matriculaResumen.getPrioridad().subtract(BigDecimal.ONE).compareTo(bigDecimal) == 0) {
                    continue;
                }
                matriculaResumen.setPrioridad(prioridad);

                EventoAcademicoEnum eventoEnum = ciclo.isTipoRegular() ? MAT_REG : MAT_VER;
                TurnoAtencion turnosAtencion = turnoAtencionDAO.findByPrioridad(prioridad, ciclo, eventoEnum);
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
    public List<DeudaAlumno> allDeudasByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
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

    @Override
    public void habilitarMatriculable(MatriculaResumen matriculaResumenForm, DataSessionPivot ds) {
        CicloAcademico academico = cicloAcademicoDAO.find(ds.getCicloAcademico());

        MatriculaResumen matriculaResumen = matriculaResumenDAO.find(matriculaResumenForm.getId());
        Assert.isNotNull(matriculaResumen, "El alumno no es matriculable");
        Assert.isFalse(matriculaResumen.getEstadoEnum() == EstadoMatriculaEnum.NMAT, "El alumno ya se encuentra habilitado");

        if (ds.getCicloAcademico().getTipoEnum() == TipoCicloEnum.REG && academico.getFechaTurnosAsignados() != null) {
            TokenIngresante token = responseRestService.createToken(ds);
            JsonResponse jsonResponse = responseRestService.generarAporte(matriculaResumen.getAlumno(), academico, matriculaResumen, ds, token);
            Assert.isTrue(jsonResponse.getSuccess(), jsonResponse.getMessage());
        }

        matriculaResumen.setEstadoEnum(EstadoMatriculaEnum.NMAT);
        matriculaResumen.setMotivoMatriculable(matriculaResumenForm.getMotivoMatriculable());
        matriculaResumenDAO.update(matriculaResumen);
    }

}

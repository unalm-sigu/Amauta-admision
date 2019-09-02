package pe.edu.lamolina.pivot.controller.academico.infoacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.OrientacionCarrera;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.aporte.BoletaIngresante;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.PMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCU;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RET;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.matricula.AlumnoAvanceCurricular;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas;
import pe.edu.lamolina.pivot.dao.academico.AlumnoAvanceCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoSimultaneoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.OrientacionCarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.ResumenPlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.aporte.AporteAlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class InfoAcademicoServiceImpl implements InfoAcademicoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    AvanceCurricularService avanceCurricularService;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    PlanCurricularDAO planCurricularDAO;

    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;

    @Autowired
    PromedioService promedioService;

    @Autowired
    VisorCalculoNotas visorCalculoNotas;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AporteAlumnoCicloDAO aporteAlumnoCicloDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    OrientacionCarreraDAO orientacionCarreraDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    RequisitoCursoCurriculaDAO requisitoCursoCurriculaDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    AlumnoCursoSimultaneoDAO alumnoCursoSimultaneoDAO;

    @Autowired
    AlumnoAvanceCurricularDAO alumnoAvanceCurricularDAO;

    @Autowired
    ResumenPlanCurricularDAO resumenPlanCurricularDAO;

    @Override
    public Alumno findAlumno(Long idAlumno) {
        return alumnoDAO.find(new Alumno(idAlumno));
    }

    @Override
    public ObjectNode allAvanceCurricular(Alumno alumno) {
        ArrayNode ciclosJson = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode cursosJson = new ArrayNode(JsonNodeFactory.instance);
        ObjectNode avanceCurrJson = new ObjectNode(JsonNodeFactory.instance);
        alumno = alumnoDAO.findAllInfo(alumno.getId());

        if (alumno.getPlanCurricular() == null) {
            avanceCurrJson.set("cursos", cursosJson);
            return avanceCurrJson;
        }

        List<AlumnoCursoCurricula> ciclosAlumno = alumnoCursoCurriculaDAO.allCiclosAlumno(alumno);
//             cicloInicia = planesCurricular.stream().map(x -> x.getCicloInicioVigencia()).min(Comparator.comparing(CicloAcademico::getCodigo)).get();
        AlumnoCursoCurricula max = ciclosAlumno.stream().max(Comparator.comparing(AlumnoCursoCurricula::getNumeroCiclo)).get();
        Map<Integer, Long> counters = ciclosAlumno.stream()
                .collect(Collectors.groupingBy(c -> c.getNumeroCiclo(),
                        Collectors.counting()));

        Set<Map.Entry<Integer, Long>> entry = counters.entrySet();
        for (Integer i = 1; i <= max.getNumeroCiclo(); i++) {
            Integer a = i;

            Map.Entry<Integer, Long> value = entry.stream().filter(x -> Objects.equals(x.getKey(), a)).findAny().orElse(null);
            if (value == null) {
                counters.put(a, 0l);
                entry = counters.entrySet();
                value = entry.stream().filter(x -> Objects.equals(x.getKey(), a)).findAny().orElse(null);
            }
            ObjectNode objCiclo = new ObjectNode(JsonNodeFactory.instance);
            objCiclo.put("numeroRoman", NumberFormat.roman(value.getKey()));
            objCiclo.put("cantidad", "(" + value.getValue() + ")");
            objCiclo.put("numero", value.getKey());
            ciclosJson.add(objCiclo);
        }
        avanceCurrJson.set("ciclos", ciclosJson);

        List<CursoCurricula> cursosCicloPlan = cursoCurriculaDAO.allByPlanCurricular(alumno.getPlanCurricular());
        List<AlumnoCursoCurricula> cursosPlanAlumno = alumnoCursoCurriculaDAO.allByAlumnoCursosCurricula(alumno, cursosCicloPlan);
        List<AlumnoCursoCurricula> cursosPlanAlumnoOpcional = alumnoCursoCurriculaDAO.allByAlumnoCursosOpcional(alumno);
        List<RequisitoCursoCurricula> requisitoCursoCurriculas = requisitoCursoCurriculaDAO.allByCursosCurricula(cursosCicloPlan);
        Map<Long, Long> mapRequisitoByCurricula = requisitoCursoCurriculas.stream().collect(Collectors.groupingBy(x -> x.getCursoCurricula().getId(), Collectors.counting()));
        Map<Long, List<RequisitoCursoCurricula>> mapCountRequisitos = TypesUtil.convertListToMapList("cursoCurricula.id", requisitoCursoCurriculas);
        Map<Long, AlumnoCursoCurricula> mapAlumnoCurso = TypesUtil.convertListToMap("cursoCurricula.id", cursosPlanAlumno);

        List<AlumnoCursoCurricula> cursosComodin = alumnoCursoCurriculaDAO.allByAlumnoComodin(alumno);
        for (AlumnoCursoCurricula alumnoCursoCurricula : cursosPlanAlumno) {
            ObjectNode objNode = JsonHelper.createJson(alumnoCursoCurricula, JsonNodeFactory.instance, true, new String[]{
                "numeroCiclo", "estado", "estadoEnum", "vecesCursado", "nota", "creditos", "estadoMatricula", "estadoMatriculaEnum",
                "estadoRegistro",
                "curso.codigo",
                "curso.codigoAnterior1",
                "curso.nombre",
                "curso.tpc",
                "cursoCurricula.tipoCursoCurricula.nombre",
                "cursoCurricula.creditosRequisito",
                "tipoCursoCurricula.nombre",
                "tipoCursoCurricula.codigo",
                "cicloAprobado.descripcion"
            });

            Long countReq = mapRequisitoByCurricula.get(alumnoCursoCurricula.getCursoCurricula().getId());
            objNode.put("cantRequisitos", countReq == null ? 0 : countReq);
            List<RequisitoCursoCurricula> preRequisitos = mapCountRequisitos.get(alumnoCursoCurricula.getCursoCurricula().getId());

            ArrayNode arrayPreRequisitos = new ArrayNode(JsonNodeFactory.instance);
            List<RequisitoCursoCurricula> cursosRequisitos = preRequisitos == null ? new ArrayList<>() : preRequisitos;
            for (RequisitoCursoCurricula requisito : cursosRequisitos) {
                ObjectNode nodeRequisito = new ObjectNode(JsonNodeFactory.instance);
                nodeRequisito.put("curso", requisito.getCursoRequisito().getCurso().getNombre());
                nodeRequisito.put("codigo", requisito.getCursoRequisito().getCurso().getCodigo());
                nodeRequisito.put("codigo2", requisito.getCursoRequisito().getCurso().getCodigoAnterior1());
                nodeRequisito.put("simultaneo", requisito.getSimultaneo());
                nodeRequisito.put("tipoCurso", requisito.getCursoRequisito().getTipoCursoCurricula().getNombre());
                nodeRequisito.put("numeroRomano", NumberFormat.roman(requisito.getCursoRequisito().getNumeroCiclo()));
                nodeRequisito.put("tpc", requisito.getCursoRequisito().getCurso().getTpc());
                nodeRequisito.put("tipoDictadoCurso", requisito.getCursoRequisito().getCurso().getTipoCursoEnum().getValue());

                AlumnoCursoCurricula alumnoCurs = mapAlumnoCurso.get(requisito.getCursoRequisito().getId());
                nodeRequisito.put("estado", alumnoCurs.getEstadoEnum().name());
                nodeRequisito.put("estadoMatricula", alumnoCurs.getEstadoMatricula());
                arrayPreRequisitos.add(nodeRequisito);
            }
            objNode.set("prerrequisitos", arrayPreRequisitos);
            cursosJson.add(objNode);
        }
        for (AlumnoCursoCurricula alumnoCursoCurricula : cursosPlanAlumnoOpcional) {
            ObjectNode objNode = JsonHelper.createJson(alumnoCursoCurricula, JsonNodeFactory.instance, true, new String[]{
                "numeroCiclo", "estado", "estadoEnum", "vecesCursado", "nota", "creditos", "estadoMatricula", "estadoMatriculaEnum",
                "estadoRegistro",
                "curso.codigo",
                "curso.codigoAnterior1",
                "curso.nombre",
                "curso.tpc",
                "cursoCurricula.tipoCursoCurricula.nombre",
                "tipoCursoCurricula.nombre",
                "tipoCursoCurricula.codigo",
                "cicloAprobado.descripcion"
            });
            cursosJson.add(objNode);
        }
        for (AlumnoCursoCurricula alumnoCursoCurricula : cursosComodin) {
            ObjectNode objNode = JsonHelper.createJson(alumnoCursoCurricula, JsonNodeFactory.instance, true, new String[]{
                "numeroCiclo", "estado", "estadoEnum", "vecesCursado", "nota", "creditos", "estadoMatricula", "estadoMatriculaEnum",
                "estadoRegistro",
                "curso.codigo",
                "curso.codigoAnterior1",
                "curso.nombre",
                "curso.tpc",
                "cursoCurricula.tipoCursoCurricula.nombre",
                "tipoCursoCurricula.nombre",
                "tipoCursoCurricula.codigo",
                "cicloAprobado.descripcion"
            });
            cursosJson.add(objNode);
        }
        avanceCurrJson.set("cursos", cursosJson);

        ArrayNode resumenAlumnoJson = new ArrayNode(JsonNodeFactory.instance);
        List<AlumnoAvanceCurricular> resumenAlumno = alumnoAvanceCurricularDAO.allByAlumno(alumno);
        for (AlumnoAvanceCurricular resumen : resumenAlumno) {
            ObjectNode resumenJson = JsonHelper.createJson(resumen, JsonNodeFactory.instance, true, new String[]{
                "id", "creditos", "cursos",
                "tipoCursoCurricula.nombre",
                "tipoCursoCurricula.codigo"
            });
            resumenAlumnoJson.add(resumenJson);
        }
        avanceCurrJson.set("resumenAlumno", resumenAlumnoJson);

        ArrayNode resumenPlanJson = new ArrayNode(JsonNodeFactory.instance);
        List<ResumenPlanCurricular> resumenPlan = resumenPlanCurricularDAO.allByPlan(alumno.getPlanCurricular());
        for (ResumenPlanCurricular resumen : resumenPlan) {
            ObjectNode resumenJson = JsonHelper.createJson(resumen, JsonNodeFactory.instance, true, new String[]{
                "id", "creditos", "cursos",
                "tipoCursoCurricula.nombre",
                "tipoCursoCurricula.codigo"
            });
            resumenPlanJson.add(resumenJson);
        }
        avanceCurrJson.set("resumenPlan", resumenPlanJson);

        return avanceCurrJson;
    }

    @Override
    public List<Hora> allHoras() {
        return horaDAO.all();
    }

    @Override
    public Alumno findWithallInfo(Alumno alumno) {
        Alumno alu = alumnoDAO.findAllInfo(alumno.getId());
        Carrera carrera = alu.getCarrera();
        List<OrientacionCarrera> orientaciones = orientacionCarreraDAO.allByCarrera(carrera);
        carrera.setOrientacionCarrera(orientaciones);
        return alu;
    }

    @Override
    public List<MatriculaCurso> allCursosMatriculadosByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {

        List<Seccion> secciones = new ArrayList();
        Map<Long, Seccion> mapSecciones = new LinkedHashMap();
        Map<Long, List<MatriculaSeccion>> mapMatriculaSecciones = new LinkedHashMap();

        List<String> estadosMat = Arrays.asList(MAT.name(), PMAT.name(), RET.name(), RCU.name(), RCI.name());
        List<MatriculaSeccion> matriculaSecciones = depurarMatriculaSecciones(matriculaSeccionDAO.allByAlumnoCicloEstados(alumno, ciclo, estadosMat));
        for (MatriculaSeccion ms : matriculaSecciones) {
            Seccion seccion = ms.getSeccion();
            seccion.setDocenteSeccion(new ArrayList());
            secciones.add(seccion);
            mapSecciones.put(seccion.getId(), seccion);

            Curso curso = ms.getSeccion().getGrupoSeccion().getCurso();
            List<MatriculaSeccion> matriculaSeccionesCurso = mapMatriculaSecciones.get(curso.getId());
            if (matriculaSeccionesCurso == null) {
                matriculaSeccionesCurso = new ArrayList();
                mapMatriculaSecciones.put(curso.getId(), matriculaSeccionesCurso);
            }
            matriculaSeccionesCurso.add(ms);
        }

        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allActivosBySecciones(secciones);
        for (DocenteSeccion profeSeccion : docentesSecciones) {
            if (!profeSeccion.esDocentePrincipal()) {
                continue;
            }
            Seccion seccionProfe = profeSeccion.getSeccion();
            Seccion seccion = mapSecciones.get(seccionProfe.getId());
            profeSeccion.setSeccion(seccion);
            seccion.getDocenteSeccion().add(profeSeccion);
        }

        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allActivoByAlumnoCiclo(alumno, ciclo);
        for (MatriculaCurso mc : matriculaCursos) {
            Curso curso = mc.getCurso();
            mc.setMatriculaSeccion(mapMatriculaSecciones.get(curso.getId()));
        }

        return matriculaCursos;

    }

    private List<MatriculaSeccion> depurarMatriculaSecciones(List<MatriculaSeccion> matriSecciones) {
        List<MatriculaSeccion> depurados = new ArrayList();

        Map<Long, List<MatriculaSeccion>> mapMatriSecc = TypesUtil.convertListToMapList("seccion.grupoSeccion.curso.id", matriSecciones);
        for (Map.Entry<Long, List<MatriculaSeccion>> entry : mapMatriSecc.entrySet()) {
            List<MatriculaSeccion> depuradosCurso = new ArrayList();
            List<MatriculaSeccion> matriSeccCurso = entry.getValue();
            for (MatriculaSeccion matSecc : matriSeccCurso) {
                if (matSecc.getEstadoEnum() == MAT) {
                    depuradosCurso.add(matSecc);
                }
            }
            if (!depuradosCurso.isEmpty()) {
                depurados.addAll(depuradosCurso);
                continue;
            }
            for (MatriculaSeccion matSecc : matriSeccCurso) {
                if (matSecc.getEstadoEnum() == PMAT) {
                    depuradosCurso.add(matSecc);
                }
            }
            if (!depuradosCurso.isEmpty()) {
                depurados.addAll(depuradosCurso);
                continue;
            }
            for (MatriculaSeccion matSecc : matriSeccCurso) {
                if (matSecc.getEstadoEnum() == RCI) {
                    depuradosCurso.add(matSecc);
                }
            }
            if (!depuradosCurso.isEmpty()) {
                depurados.addAll(depuradosCurso);
                continue;
            }
            for (MatriculaSeccion matSecc : matriSeccCurso) {
                if (matSecc.getEstadoEnum() == RCU) {
                    depuradosCurso.add(matSecc);
                }
            }
            if (!depuradosCurso.isEmpty()) {
                depurados.addAll(depuradosCurso);
                continue;
            }

            Collections.sort(matriSeccCurso, new MatriculaSeccion.CompareReverseId());
            GrupoSeccion gpoSecc = null;
            for (MatriculaSeccion matSecc : matriSeccCurso) {
                if (matSecc.getEstadoEnum() == RET) {
                    gpoSecc = matSecc.getSeccion().getGrupoSeccion();
                    break;
                }
            }
            for (MatriculaSeccion matSecc : matriSeccCurso) {
                GrupoSeccion gpoSeccBD = matSecc.getSeccion().getGrupoSeccion();
                if (matSecc.getEstadoEnum() == RET && gpoSecc.getId().longValue() == gpoSeccBD.getId()) {
                    depuradosCurso.add(matSecc);
                    if (depuradosCurso.size() > 1) {
                        break;
                    }
                }
            }
        }

        return depurados;
    }

    @Override
    public List<PlanCurricular> allPlanCurricularByAlumno(Alumno alumno) {
        Carrera carrera = alumno.getCarrera();
        OrientacionCarrera orientacion = alumno.getOrientacionCarrera();
        if (orientacion == null) {
            return planCurricularDAO.allActivoByCarrera(carrera);
        } else {
            return planCurricularDAO.allActivoByOrientacion(carrera, orientacion);
        }
    }

    @Override
    @Transactional
    public void cambiarPlan(Alumno alumno, PlanCurricular planCurricular, DataSessionPivot ds) {
        Alumno alumnoBD = alumnoDAO.find(alumno);
        PlanCurricular planCurricularBD = planCurricularDAO.find(planCurricular.getId());
        Assert.isTrue(planCurricularBD != null, "No existe el plan curricular indicado");

        Carrera carreraAlu = alumnoBD.getCarrera();
        Carrera carreraPlan = planCurricularBD.getCarrera();
        Assert.isTrue(carreraAlu.getId().longValue() == carreraPlan.getId(), "El cambio de plan no corresponde a la misma especialidad del alumno");

        alumnoBD.setPlanCurricular(planCurricularBD);
        alumnoDAO.update(alumnoBD);
        if (alumnoBD.getModalidadEstudio().isPregrado()) {
            avanceCurricularService.generarAvanceCurricularByAlumno(alumno, ds);
        } else {
            avanceCurricularService.generarAvanceCurricularByAlumnoEPG(alumno, ds);

        }

    }

    @Override
    public void generarAvance(Alumno alumno, DataSessionPivot ds) {
        Boolean puedeCalcular = usuarioPuedeCalcular(ds);
        Assert.isTrue(puedeCalcular, "Usted no está autorizado para ejecutar esta acción");

        alumno = alumnoDAO.find(alumno);
        if (alumno.getModalidadEstudio().isPregrado()) {
            avanceCurricularService.generarAvanceCurricularByAlumno(alumno, ds);
        } else {
            avanceCurricularService.generarAvanceCurricularByAlumnoEPG(alumno, ds);

        }
    }

    @Override
    public List<AlumnoCicloCurso> allHistorialAlumno(Alumno alumno) {
        return alumnoCicloCursoDAO.allByAlumno(alumno);
    }

    @Override
    public ArrayNode allPromediosJson(List<AlumnoCicloCurso> cursosCiclos) {
        Map<Long, AlumnoCiclo> mapAlumnoCiclo = TypesUtil.convertListToMap("alumnoCiclo.id", "alumnoCiclo", cursosCiclos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso = TypesUtil.convertListToMapList("alumnoCiclo.id", cursosCiclos);

        List<AlumnoCiclo> promedios = new ArrayList(mapAlumnoCiclo.values());
        for (AlumnoCiclo promedio : promedios) {
            List<AlumnoCicloCurso> cursos = mapAlumnoCicloCurso.get(promedio.getId());
            promedio.setAlumnoCicloCurso(cursos);
        }

        Collections.sort(promedios, new AlumnoCiclo.CompareReverseCiclo());
        ArrayNode promediosCicloJson = new ArrayNode(JsonNodeFactory.instance);
        for (AlumnoCiclo promedio : promedios) {
            ObjectNode promedioJson = JsonHelper.createJson(promedio, JsonNodeFactory.instance, true, new String[]{
                "id", "estadoEnum", "estaAprobado",
                "creditosCursadosCiclo", "creditosAprobadosCiclo", "promedioCiclo", "puntajeCiclo",
                "creditosAcumulados", "creditosAprobadosAcumulados", "promedioAcumulado", "puntajeAcumulado",
                "creditosConvalidados",
                /* --- */
                "tercioSuperiorCarrera",
                "tercioSuperiorFacultad",
                "tercioSuperiorCiclo",
                "quintoSuperiorCarrera",
                "quintoSuperiorFacultad",
                "quintoSuperiorCiclo",
                "cuadroHonorCarrera",
                "cuadroHonorFacultad",
                "cuadroHonorCiclo",
                "ordenMeritoCarrera",
                "ordenMeritoFacultad",
                "ordenMeritoCiclo",
                /* --- */
                "nivel",
                "tercioSuperiorCarreraNivel",
                "tercioSuperiorFacultadNivel",
                "tercioSuperiorCicloNivel",
                "quintoSuperiorCarreraNivel",
                "quintoSuperiorFacultadNivel",
                "quintoSuperiorCicloNivel",
                "cuadroHonorCarreraNivel",
                "cuadroHonorFacultadNivel",
                "cuadroHonorCicloNivel",
                "ordenMeritoCarreraNivel",
                "ordenMeritoFacultadNivel",
                "ordenMeritoCicloNivel",
                /* --- */
                "controlMeritoCiclo.alumnosComputados",
                "controlMeritoCiclo.computadosNivel1",
                "controlMeritoCiclo.computadosNivel2",
                "controlMeritoCiclo.computadosNivel3",
                "controlMeritoCiclo.computadosNivel4",
                "controlMeritoCiclo.computadosNivel5",
                "controlMeritoFacultad.alumnosComputados",
                "controlMeritoFacultad.computadosNivel1",
                "controlMeritoFacultad.computadosNivel2",
                "controlMeritoFacultad.computadosNivel3",
                "controlMeritoFacultad.computadosNivel4",
                "controlMeritoFacultad.computadosNivel5",
                "controlMeritoCarrera.alumnosComputados",
                "controlMeritoCarrera.computadosNivel1",
                "controlMeritoCarrera.computadosNivel2",
                "controlMeritoCarrera.computadosNivel3",
                "controlMeritoCarrera.computadosNivel4",
                "controlMeritoCarrera.computadosNivel5",
                /* --- */
                "orientacionCarrera.id",
                "orientacionCarrera.nombre",
                "carrera.nombre",
                "carrera.codigo",
                "carrera.facultad.nombre",
                "carrera.facultad.codigo",
                /* --- */
                "situacionInicio.codigo",
                "situacionInicio.nombre",
                "situacionFinal.codigo",
                "situacionFinal.nombre",
                /* --- */
                "alumnoCicloCurso.estadoEnum",
                "alumnoCicloCurso.nota",
                "alumnoCicloCurso.creditos",
                "alumnoCicloCurso.estaAprobado",
                "alumnoCicloCurso.vecesCursado",
                "alumnoCicloCurso.vecesCursadoRegular",
                "alumnoCicloCurso.curso.codigo",
                "alumnoCicloCurso.curso.nombre",
                "alumnoCicloCurso.curso.tpc",
                /* --- */
                "cicloAcademico.descripcion",
                "cicloAcademico.descripcion2",
                "cicloAcademico.tipoEnum"
            });
            promediosCicloJson.add(promedioJson);
        }
        return promediosCicloJson;
    }

    @Override
    public ArrayNode allCursosJson(List<AlumnoCicloCurso> cursosCiclosOrigen) {
        List<AlumnoCicloCurso> cursosCiclos = new ArrayList();
        cursosCiclos.addAll(cursosCiclosOrigen);
        Collections.sort(cursosCiclos, new AlumnoCicloCurso.CompareCursoCiclo());

        ArrayNode cursosHistoJson = new ArrayNode(JsonNodeFactory.instance);
        for (AlumnoCicloCurso cursoCiclo : cursosCiclos) {
            ObjectNode cursoJson = JsonHelper.createJson(cursoCiclo, JsonNodeFactory.instance, true, new String[]{
                "estadoEnum", "creditos", "nota", "estaAprobado", "vecesCursado", "vecesCursadoRegular",
                "curso.codigo",
                "curso.nombre",
                "curso.tpc",
                "tipoCursoCurricula.*",
                /* -- */
                "alumnoCiclo.cicloAcademico.descripcion",
                "alumnoCiclo.cicloAcademico.tipoEnum"
            });
            cursosHistoJson.add(cursoJson);
        }
        return cursosHistoJson;
    }

    @Override
    @Transactional
    public void calcularPromedio(Alumno alumnoForm, DataSessionPivot ds) {
        Boolean puedeCalcular = usuarioPuedeCalcular(ds);
        Assert.isTrue(puedeCalcular, "Usted no está autorizado para ejecutar esta acción");

        Alumno alumno = alumnoDAO.find(alumnoForm);
        visorCalculoNotas.setActivo(false);
        promedioService.calulcarSituacionAcademica(alumno, ds);
    }

    @Override
    public List<BoletaIngresante> allAportesAlumno(Alumno alumno, CicloAcademico ciclo) {
        Alumno alumnoBD = alumnoDAO.find(alumno);
        CicloAcademico cicloModalidad = findCicloByModalidad(alumnoBD.getModalidadEstudio(), ciclo);

        List<BoletaIngresante> boletas = new ArrayList();
        List<AporteAlumnoCiclo> aportesAlumno = aporteAlumnoCicloDAO.allByAlumnoCiclo(alumnoBD, cicloModalidad);

        Map<Long, List<AporteAlumnoCiclo>> mapCtaAportes = TypesUtil.convertListToMapList("aporteCiclo.cuentaBancaria.id", aportesAlumno);
        Map<Long, CuentaBancaria> mapCtaBanco = TypesUtil.convertListToMap("aporteCiclo.cuentaBancaria.id", "aporteCiclo.cuentaBancaria", aportesAlumno);
        List<CuentaBancaria> ctas = new ArrayList(mapCtaBanco.values());

        for (CuentaBancaria cta : ctas) {
            BigDecimal montoTotal = BigDecimal.ZERO;

            List<AporteAlumnoCiclo> aportes = mapCtaAportes.get(cta.getId());

            for (AporteAlumnoCiclo aporte : aportes) {
                montoTotal = montoTotal.add(aporte.getMonto());
            }

            BoletaIngresante boleta = new BoletaIngresante(cta.getId(), null, cta.getNombre(), cta.getNumero(), cta.getCuentaDescripcion(), montoTotal);
            boleta.setAportesAlumno(aportes);

            boletas.add(boleta);
        }

        return boletas;
    }

    private CicloAcademico findCicloByModalidad(ModalidadEstudio modalidad, CicloAcademico ciclo) {
        ModalidadEstudio modalidadCiclo = ciclo.getModalidadEstudio();
        if (modalidadCiclo.getId() == modalidad.getId().longValue()) {
            return ciclo;
        }

        String codigoCiclo = ciclo.getCodigo();
        CicloAcademico cicloModalidad = cicloAcademicoDAO.findByCodigoModalidadEstudio(codigoCiclo, modalidad);

        return cicloModalidad;

    }

    @Override
    public MatriculaResumen findResumenMatricula(Alumno alumno, CicloAcademico ciclo) {
        MatriculaResumen matResum = matriculaResumenDAO.findByAlumnoCiclo(alumno, ciclo);
        if (matResum == null) {
            matResum = new MatriculaResumen();
            matResum.setCreditosMatriculados(0);
            matResum.setCursosMatriculados(0);
        }
        return matResum;
    }

    @Override
    @Transactional
    public MatriculaResumen findResumenMatricula(Alumno alumno, CicloAcademico ciclo, List<MatriculaCurso> matriculaCursos) {
        MatriculaResumen resumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, ciclo);
        if (resumen == null) {
            resumen = new MatriculaResumen();
            resumen.setEstadoEnum(EstadoMatriculaEnum.NMAT);
        }
        int cursosMat = 0;
        int creditosMat = 0;
        for (MatriculaCurso matCurso : matriculaCursos) {
            if (matCurso.getEstadoEnum() == MAT) {
                cursosMat++;
                creditosMat += matCurso.getCreditos();
            }
        }

        if (cursosMat != resumen.getCursosMatriculados() || creditosMat != resumen.getCreditosMatriculados()) {
            resumen.setCursosMatriculados(cursosMat);
            resumen.setCreditosMatriculados(creditosMat);
            matriculaResumenDAO.update(resumen);
        }

        return resumen;
    }

    @Override
    public List<HorarioSeccion> allSeccionHorarioAlumnoByAlumnoCicloACademico(Alumno alumno, CicloAcademico academico) {
        List<MatriculaSeccion> matriculaSecciones = matriculaSeccionDAO.allByAlumnoCicloEstados(alumno, academico, Arrays.asList("MAT"));
        if (matriculaSecciones.isEmpty()) {
            return new ArrayList();
        }

        List<Seccion> secciones = new ArrayList();
        for (MatriculaSeccion matriculaSeccion : matriculaSecciones) {
            secciones.add(matriculaSeccion.getSeccion());

        }
        return horarioSeccionDAO.allBySecciones(secciones);
    }

    @Override
    public List<HorarioSeccion> allSeccionHorarioAlumnoByDocenteCicloACademico(Docente docente, CicloAcademico academico) {
        List<DocenteSeccion> docenteSecciones = docenteSeccionDAO.allByDocente(docente, academico);
        if (docenteSecciones.isEmpty()) {
            return new ArrayList();
        }
        List<Seccion> secciones = docenteSecciones.stream()
                .filter(x -> x.getSeccion().isEstadoActivo() || x.getSeccion().isEstadoBloqueado())
                .map(x -> x.getSeccion())
                .distinct().collect(Collectors.toList());
        return horarioSeccionDAO.allBySecciones(secciones);
    }

    @Override
    public ObjectNode findHorarioBySeccionesHorarios(List<HorarioSeccion> seccionesHorarios) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;

        Map<Long, List<HorarioSeccion>> mapHorarioSeccByIdHora = TypesUtil.convertListToMapList("hora.id", seccionesHorarios);
        Map<Long, Hora> mapHoras = TypesUtil.convertListToMap("hora.id", "hora", seccionesHorarios);

        List<Seccion> secciones = new ArrayList();
        Map<String, List<HorarioSeccion>> mapSeccionDia = new LinkedHashMap();
        for (HorarioSeccion seccionesHorario : seccionesHorarios) {
            secciones.add(seccionesHorario.getSeccion());
            Dia dia = seccionesHorario.getDia();
            Seccion seccion = seccionesHorario.getSeccion();
            String key = seccion.getId() + "-" + dia.getId();
            List<HorarioSeccion> horariosSecc = mapSeccionDia.get(key);
            if (horariosSecc == null) {
                horariosSecc = new ArrayList();
                mapSeccionDia.put(key, horariosSecc);
            }
            horariosSecc.add(seccionesHorario);
        }

        Map<String, Integer> mapSeccionHora = new LinkedHashMap();
        for (Map.Entry<String, List<HorarioSeccion>> entry : mapSeccionDia.entrySet()) {
            Integer nroHora = 1000;
            List<HorarioSeccion> horariosSecc = entry.getValue();
            for (HorarioSeccion horarioSecc : horariosSecc) {
                Integer nroHoraSecc = horarioSecc.getHora().getNumero();
                nroHora = (nroHora > nroHoraSecc) ? nroHoraSecc : nroHora;
            }
            mapSeccionHora.put(entry.getKey(), nroHora);
        }

        List<DocenteSeccion> profesSecciones = docenteSeccionDAO.allPrincipalesBySeccion(secciones);
        Map<Long, DocenteSeccion> mapProfeSecc = TypesUtil.convertListToMap("seccion.id", profesSecciones);

        List<Dia> dias = diaDAO.allDia();
        List<Hora> horas = new ArrayList();
        List<Hora> horasDB = horaDAO.all();
        Integer horaMax = 0;
        for (Hora hora : mapHoras.values()) {
            horaMax = horaMax < hora.getNumero() ? hora.getNumero() : horaMax;
            horas.add(hora);
        }
        if (!horas.isEmpty()) {
            Map<Integer, Hora> mapHorasDB = TypesUtil.convertListToMap("numero", horasDB);
            Hora horaDB = mapHorasDB.get(horaMax + 1);
            if (horaDB != null) {
                horas.add(horaDB);
            }
        }
        horas = horas.isEmpty() ? horaDAO.all() : horas;
        Collections.sort(horas, new Hora.CompareCodigo());

        ObjectNode horarioJson = new ObjectNode(jsonFactory);
        ArrayNode horaArrayJson = new ArrayNode(jsonFactory);

        for (Hora hora : horas) {
            ObjectNode horaJson = new ObjectNode(jsonFactory);
            horaJson.put("hora", hora.getDescripcion());
            horaJson.put("numeroHora", hora.getNumero());
            List<HorarioSeccion> horariosSeccionesHora = mapHorarioSeccByIdHora.get(hora.getId());
            horariosSeccionesHora = (horariosSeccionesHora == null) ? new ArrayList() : horariosSeccionesHora;

            Map<Long, List<HorarioSeccion>> mapHorarioSeccionDia = TypesUtil.convertListToMapList("dia.id", horariosSeccionesHora);
            ArrayNode diaArrayJson = new ArrayNode(jsonFactory);
            for (Dia dia : dias) {
                ObjectNode diaJson = new ObjectNode(jsonFactory);
                diaJson.put("hora", hora.getDescripcion());
                diaJson.put("dia", dia.getNombre());
                List<HorarioSeccion> horariosSeccionesDia = mapHorarioSeccionDia.get(dia.getId());
                horariosSeccionesDia = (horariosSeccionesDia == null) ? new ArrayList() : horariosSeccionesDia;

                ArrayNode seccionArrayJson = new ArrayNode(jsonFactory);
                for (HorarioSeccion horarioSeccion : horariosSeccionesDia) {
                    Seccion seccion = horarioSeccion.getSeccion();

                    ObjectNode seccionJson = JsonHelper.createJson(seccion, jsonFactory, true, new String[]{
                        "codigo2", "tipoSeccion",
                        "grupoSeccion.curso.codigo",
                        "grupoSeccion.curso.nombre",
                        "grupoSeccion.curso.tpc",
                        "aula.codigo",
                        "grupoHoras.codigo"
                    });

                    String key = seccion.getId() + "-" + dia.getId();
                    List<HorarioSeccion> horariosSecc = mapSeccionDia.get(key);
                    Integer nroHora = mapSeccionHora.get(key);
                    seccionJson.put("horasContinuas", horariosSecc.size());
                    seccionJson.put("horaInicial", nroHora == hora.getNumero());

                    logger.debug("idHorario:{}, idSeccion:{}", horarioSeccion.getId(), horarioSeccion.getSeccion().getId());

                    DocenteSeccion profeSecc = mapProfeSecc.get(horarioSeccion.getSeccion().getId());
                    seccionJson.put("docente", (String) ObjectUtil.getParentTree(profeSecc, "docente.persona.letraNomPaterno"));

                    seccionArrayJson.add(seccionJson);
                }

                diaJson.set("secciones", seccionArrayJson);
                diaArrayJson.add(diaJson);
            }
            horaJson.set("dias", diaArrayJson);
            horaArrayJson.add(horaJson);
        }
        ArrayNode diasArray = new ArrayNode(jsonFactory);
        for (Dia dia : dias) {
            ObjectNode diaObjectNode = new ObjectNode(jsonFactory);
            diaObjectNode.put("dia", dia.getNombre());
            diasArray.add(diaObjectNode);
        }

        horarioJson.set("horarios", horaArrayJson);
        horarioJson.set("dias", diasArray);
        horarioJson.put("horasTotal", horaArrayJson.size());

        return horarioJson;
    }

    @Override
    public Hora getHoraByNroHora(Integer numero) {
        return horaDAO.findByNumeroHora(numero);
    }

    @Override
    @Transactional
    public void cambiarOrientacion(Alumno alumno, OrientacionCarrera orientacion, DataSessionPivot ds) {
        Alumno alumnoBD = alumnoDAO.find(alumno);
        OrientacionCarrera orientacionBD = orientacionCarreraDAO.find(orientacion.getId());
        Assert.isTrue(alumnoBD != null, "El alumno no existe en la base de datos");
        Assert.isTrue(orientacionBD != null, "La orientación no existe en la base de datos");

        Carrera carrAlu = alumnoBD.getCarrera();
        Carrera carrOri = orientacionBD.getCarrera();
        Assert.isTrue(carrAlu.getId() == carrOri.getId().longValue(), "La orientación no corresponde a la especialidad del alumno");

        alumnoBD.setOrientacionCarrera(orientacionBD);
        alumnoDAO.update(alumnoBD);

        List<PlanCurricular> planes = planCurricularDAO.allActivoByOrientacion(carrOri, orientacionBD);
        Map<String, CicloAcademico> mapCiclosPlanes = TypesUtil.convertListToMap("cicloInicioVigencia.codigo", "cicloInicioVigencia", planes);
        Map<String, PlanCurricular> mapPlanesByCiclo = TypesUtil.convertListToMap("cicloInicioVigencia.codigo", planes);
        List<String> codigosCiclosPlanes = new ArrayList<String>(mapCiclosPlanes.keySet());

        Collections.sort(codigosCiclosPlanes);
        Collections.reverse(codigosCiclosPlanes);

        if (planes.isEmpty() || planes.size() > 1) {
            String codigoCicloAlumno = (String) ObjectUtil.getParentTree(alumnoBD, "cicloIngreso.codigo");

            String codigoCicloPlan = this.getIndiceCicloAcademico(codigoCicloAlumno, codigosCiclosPlanes);

            PlanCurricular planBD = mapPlanesByCiclo.get(codigoCicloPlan);
            alumnoCursoSimultaneoDAO.deleteAllByAlumno(alumnoBD);
            alumnoCursoCurriculaDAO.deleteAllByAlumno(alumnoBD);
            alumnoAvanceCurricularDAO.deleteAllByAlumno(alumnoBD);

            alumnoBD.setPlanCurricular(planBD);
            alumnoDAO.update(alumnoBD);
        } else {
            alumnoBD.setPlanCurricular(planes.get(0));
            alumnoDAO.update(alumnoBD);
        }
        if (alumnoBD.getModalidadEstudio().isPregrado()) {
            avanceCurricularService.generarAvanceCurricularByAlumno(alumnoBD, ds);
        } else if (alumnoBD.getModalidadEstudio().isPostgrado()) {
            Assert.isNotNull(alumnoBD.getPlanCurricular(), "La orientación no cuenta con plan curricular.");
            avanceCurricularService.generarAvanceCurricularByAlumnoEPG(alumnoBD, ds);

        }
    }

    private String getIndiceCicloAcademico(String codigoCicloAlumno, List<String> codigosCiclosPlanes) {
        for (String codigoCicloPlan : codigosCiclosPlanes) {
            if (codigoCicloAlumno.compareTo(codigoCicloPlan) >= 0) {
                return codigoCicloPlan;
            }
        }
        return codigosCiclosPlanes.get(0);
    }

    @Override
    public boolean usuarioPuedeCalcular(DataSessionPivot ds) {
        boolean puedeCalcular = false;
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.RACD) {
                puedeCalcular = true;
                break;
            }
            if (rol.getCodigoEnum() == RolEnum.IOREA) {
                puedeCalcular = true;
                break;
            }
        }
        return puedeCalcular;
    }

}

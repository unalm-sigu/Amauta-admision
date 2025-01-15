package pe.edu.lamolina.amauta.controller.academico.infoacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
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
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.aporte.BoletaIngresante;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.PMAT;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCI;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RCU;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.RET;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.matricula.AlumnoAvanceCurricular;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.RetiroCurso;
import pe.edu.lamolina.amauta.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.amauta.controller.academico.infoacademico.dto.AlumnoCursoCicloDTO;
import pe.edu.lamolina.amauta.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoAvanceCurricularDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCursoSimultaneoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoEquivalenteDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.OrientacionCarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.amauta.dao.academico.PrelamolinaDAO;
import pe.edu.lamolina.amauta.dao.academico.RequisitoCursoCurriculaDAO;
import pe.edu.lamolina.amauta.dao.academico.ResumenPlanCurricularDAO;
import pe.edu.lamolina.amauta.dao.admision.EvaluadoDAO;
import pe.edu.lamolina.amauta.dao.admision.TemaCicloDAO;
import pe.edu.lamolina.amauta.dao.admision.TemaExamenDAO;
import pe.edu.lamolina.amauta.dao.aporte.AporteAlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.DiaDAO;
import pe.edu.lamolina.amauta.dao.horario.HoraDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoReplicaNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoTemaExamenDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.NotaAlumnoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.tramite.RetiroCicloDAO;
import pe.edu.lamolina.amauta.dao.tramite.RetiroCursoDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteBachillerDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteTituloDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CursoEquivalente;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.calificacion.TemaCiclo;
import pe.edu.lamolina.model.calificacion.TemaExamen;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import static pe.edu.lamolina.model.enums.PerfilColaboradorEnum.COORDTUTOR;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.Evaluado;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.Prelamolina;
import pe.edu.lamolina.model.nivelacioneegg.CursoReplicaNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoTemaExamen;
import pe.edu.lamolina.model.nivelacioneegg.NotaAlumnoNivelacion;
import pe.edu.lamolina.model.tramite.TramiteBachiller;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class InfoAcademicoServiceImpl implements InfoAcademicoService {

    private final AlumnoAvanceCurricularDAO alumnoAvanceCurricularDAO;
    private final AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    private final AlumnoCicloDAO alumnoCicloDAO;
    private final AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;
    private final AlumnoCursoSimultaneoDAO alumnoCursoSimultaneoDAO;
    private final AlumnoDAO alumnoDAO;
    private final AporteAlumnoCicloDAO aporteAlumnoCicloDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final CursoCurriculaDAO cursoCurriculaDAO;
    private final CursoEquivalenteDAO cursoEquivalenteDAO;
    private final CursoReplicaNivelacionDAO cursoReplicaNivelacionDAO;
    private final CursoTemaExamenDAO cursoTemaExamenDAO;
    private final DiaDAO diaDAO;
    private final DocenteSeccionDAO docenteSeccionDAO;
    private final EgresadoDAO egresadoDAO;
    private final EvaluadoDAO evaluadoDAO;
    private final EventoCicloAcademicoDAO eventoCicloAcademicoDAO;
    private final HoraDAO horaDAO;
    private final HorarioSeccionDAO horarioSeccionDAO;
    private final MatriculaCursoDAO matriculaCursoDAO;
    private final MatriculaResumenDAO matriculaResumenDAO;
    private final MatriculaSeccionDAO matriculaSeccionDAO;
    private final NotaAlumnoNivelacionDAO notaAlumnoNivelacionDAO;
    private final OrientacionCarreraDAO orientacionCarreraDAO;
    private final PlanCurricularDAO planCurricularDAO;
    private final PrelamolinaDAO prelamolinaDAO;
    private final RequisitoCursoCurriculaDAO requisitoCursoCurriculaDAO;
    private final ResumenPlanCurricularDAO resumenPlanCurricularDAO;
    private final RetiroCicloDAO retiroCicloDAO;
    private final RetiroCursoDAO retiroCursoDAO;
    private final TemaCicloDAO temaCicloDAO;
    private final TemaExamenDAO temaExamenDAO;
    private final TramiteBachillerDAO tramiteBachillerDAO;
    private final TramiteTituloDAO tramiteTituloDAO;
    private final ColaboradorDAO colaboradorDAO;
    private final AlumnoConsejeroDAO alumnoConsejeroDAO;

    private final AvanceCurricularService avanceCurricularService;
    private final PromedioService promedioService;

    private final String CODIGO_OFICINA_TUTORIA = "CT-";

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
        if (ciclosAlumno.isEmpty()) {
            throw new PhobosException("No tiene generado su avance curricular");
        }
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
        List<AlumnoCursoCurricula> cursosPlanAlumnoInactivosCaducados = alumnoCursoCurriculaDAO.allByAlumnoCursosCurriculaInactivos(alumno, cursosCicloPlan);
        List<AlumnoCursoCurricula> cursosPlanAlumnoOpcional = alumnoCursoCurriculaDAO.allByAlumnoCursosOpcional(alumno);
        List<RequisitoCursoCurricula> requisitoCursoCurriculas = requisitoCursoCurriculaDAO.allByCursosCurricula(cursosCicloPlan);
        List<CursoEquivalente> cursoEquivalentes = cursoEquivalenteDAO.allActivoByPlanCurricular(alumno.getPlanCurricular());
        Map<Long, Long> mapRequisitoByCurricula = requisitoCursoCurriculas.stream().collect(Collectors.groupingBy(x -> x.getCursoCurricula().getId(), Collectors.counting()));
        Map<Long, List<RequisitoCursoCurricula>> mapCountRequisitos = TypesUtil.convertListToMapList("cursoCurricula.id", requisitoCursoCurriculas);
        Map<Long, AlumnoCursoCurricula> mapAlumnoCurso = TypesUtil.convertListToMap("cursoCurricula.id", cursosPlanAlumno);
        Map<Long, AlumnoCursoCurricula> mapAlumnoCursoInactivosCaducados = TypesUtil.convertListToMap("cursoCurricula.curso.id", cursosPlanAlumnoInactivosCaducados);
        Map<Long, List<CursoEquivalente>> mapCursoEquivalente = TypesUtil.convertListToMapList("cursoCurricula.id", cursoEquivalentes);

        List<AlumnoCursoCurricula> cursosComodin = alumnoCursoCurriculaDAO.allByAlumnoComodin(alumno);

        List<CursoCurricula> cursosCurriculaEquivalenteAlumno = cursosPlanAlumno.stream()
                .filter(x -> x.getEstadoEnum().equals(CursoCurriculaEstadoEnum.EQUIV))
                .map(AlumnoCursoCurricula::getCursoCurricula)
                .collect(Collectors.toList());

        List<CursoEquivalente> cursosEquivalentesAlumno = cursoEquivalenteDAO.allActivoByCursosCurriculas(cursosCurriculaEquivalenteAlumno);
        List<Curso> cursosAprobadosXequivalencia = cursosEquivalentesAlumno.stream().map(CursoEquivalente::getCursoEquivalente).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosComodinMenosAprobadosXequivalente = cursosComodin.stream()
                .filter(x -> !cursosAprobadosXequivalencia.contains(x.getCurso())).collect(Collectors.toList());

        for (AlumnoCursoCurricula alumnoCursoCurricula : cursosPlanAlumno) {
            ObjectNode objNode = JsonHelper.createJson(alumnoCursoCurricula, JsonNodeFactory.instance, true, new String[]{
                "id",
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
            log.debug("CursoCurricula() {} {}", alumnoCursoCurricula.getCursoCurricula().getCurso().getCodigo(), alumnoCursoCurricula.getCursoCurricula().getCurso().getNombre());
            objNode.put("cantRequisitos", countReq == null ? 0 : countReq);
            List<RequisitoCursoCurricula> preRequisitos = mapCountRequisitos.get(alumnoCursoCurricula.getCursoCurricula().getId());

            ArrayNode arrayPreRequisitos = new ArrayNode(JsonNodeFactory.instance);
            List<RequisitoCursoCurricula> cursosRequisitos = preRequisitos == null ? new ArrayList<>() : preRequisitos;
            log.debug("cursosRequisitos {}", cursosRequisitos.size());
            for (RequisitoCursoCurricula cursosRequisito : cursosRequisitos) {
//                log.debug("cursosCurricula {} {}", cursosRequisito.getCursoCurricula().getCurso().getCodigo(), cursosRequisito.getCursoCurricula().getCurso().getNombre());
                log.debug("cursosRequisito {} {}", cursosRequisito.getCursoRequisito().getCurso().getCodigo(), cursosRequisito.getCursoRequisito().getCurso().getNombre());
            }
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
//                log.debug("requisito FOR {} {}", requisito.getCursoRequisito().getCurso().getCodigo(), requisito.getCursoRequisito().getCurso().getNombre());
                if (alumnoCurs == null) {
                    log.debug("AlumnoCursoCurricula es nulo no tiene aprobado por si ha llevado ");
                    List<CursoEquivalente> cursosEquivalentes = TypesUtil.getListNotNull(mapCursoEquivalente.get(requisito.getCursoRequisito().getId()));
                    log.debug("cursosEquivalentes del requisito {}", cursosEquivalentes.size());
                    for (CursoEquivalente cursosEquivalente : cursosEquivalentes) {

                        log.debug("CursoEquivalente del equivalente {} {}", cursosEquivalente.getCursoCurricula().getCurso().getCodigo(), requisito.getCursoCurricula().getCurso().getNombre());
                        if (cursosEquivalente.getCursoCaduco() == null) {
                            log.debug("CursoEquivalente no es caduco ");
                            continue;
                        }
                        log.debug("CursoEquivalente del equivalente caduco {} {}", cursosEquivalente.getCursoCaduco().getCurso().getCodigo(), cursosEquivalente.getCursoCaduco().getCurso().getNombre());

                        alumnoCurs = mapAlumnoCurso.get(cursosEquivalente.getCursoCaduco().getId());
                        if (alumnoCurs == null) {
                            AlumnoCursoCurricula alumnoCursInactivoCaduco = mapAlumnoCursoInactivosCaducados.get(cursosEquivalente.getCursoCaduco().getCurso().getId());
                            if (alumnoCursInactivoCaduco == null) {
                                throw new PhobosException("El curso " + cursosEquivalente.getCursoCaduco().getCurso().getCodigo() + " " + cursosEquivalente.getCursoCaduco().getCurso().getNombre()
                                        + " tiene como equivalente un curso caduco");
                            } else {
                                log.debug("alumnoCursInactivoCaduco {} {}", alumnoCursInactivoCaduco.getCursoCurricula().getCurso().getCodigo(), alumnoCursInactivoCaduco.getCursoCurricula().getCurso().getNombre());
                            }
                            alumnoCurs = alumnoCursInactivoCaduco;
                        }

                        nodeRequisito.put("estado", alumnoCurs.getEstadoEnum().name());
                        nodeRequisito.put("porEquivalencia", true);
                        nodeRequisito.put("estadoMatricula", alumnoCurs.getEstadoMatricula());
                        arrayPreRequisitos.add(nodeRequisito);
                    }
                    continue;
                }
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
//        for (AlumnoCursoCurricula alumnoCursoCurricula : cursosComodin) {
        for (AlumnoCursoCurricula alumnoCursoCurricula : cursosComodinMenosAprobadosXequivalente) {
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
                "id", "creditos", "cursos", "minimoCreditos",
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
    public Alumno findWithallInfo(Alumno alumnoId, DataSessionPivot ds) {

        Alumno alumno = alumnoDAO.findAllInfo(alumnoId.getId());

        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivosByAlumno(alumno);

        Map<Long, AlumnoCiclo> mapAlumnoCiclo = alumnoCicloCursos.stream()
                .collect(toMap(x -> x.getAlumnoCiclo().getId(), y -> y.getAlumnoCiclo(), (w, z) -> w));

        List<AlumnoCiclo> alumnoCiclos = mapAlumnoCiclo.values().stream().collect(toList());;

        long ciclosRegular = alumnoCiclos.stream()
                .filter(ac -> ac.getEstadoEnum() == EstadoMatriculaEnum.MAT)
                .filter(ac -> ac.getCicloAcademico().getTipoEnum() == TipoCicloEnum.REG)
                .count();

        long ciclosVerano = alumnoCiclos.stream()
                .filter(ac -> ac.getEstadoEnum() == EstadoMatriculaEnum.MAT)
                .filter(ac -> ac.getCicloAcademico().getTipoEnum() == TipoCicloEnum.NIV)
                .count();

        log.debug("ciclosRegular {}", ciclosRegular);
        log.debug("ciclosVerano {}", ciclosVerano);
        alumno.setCiclosRegularesTransient(ciclosRegular);
        alumno.setCiclosVeranosTransient(ciclosVerano);

        Carrera carrera = alumno.getCarrera();
        List<OrientacionCarrera> orientaciones = orientacionCarreraDAO.allByCarrera(carrera);
        carrera.setOrientacionCarrera(orientaciones);

        log.debug("tramiteBachiller");
        TramiteBachiller tramiteBachiller = tramiteBachillerDAO.findByAlumnoACEP(alumno);
        if (tramiteBachiller != null) {
            alumno.setResolucionBachiller((String) ObjectUtil.getParentTree(tramiteBachiller, "resolucion.numeroVisible"));
            alumno.setFechaBachiller((Date) ObjectUtil.getParentTree(tramiteBachiller, "resolucion.fecha"));
            log.debug("{}", alumno.getResolucionBachiller());
        }

        log.debug("tramiteBachillerFacultad");
        TramiteBachiller tramiteBachillerFacultad = tramiteBachillerDAO.findByAlumnoFacultadACEP(alumno);
        if (tramiteBachillerFacultad != null) {
            alumno.setResolucionBachillerFacultad(tramiteBachillerFacultad.getResolucionFacultad().getNumeroVisible());
            alumno.setFechaBachillerFacultad(tramiteBachillerFacultad.getResolucionFacultad().getFecha());
            log.debug("{}", alumno.getResolucionBachillerFacultad());
            log.debug("{}", alumno.getFechaBachillerFacultad());
        }

        log.debug("tramiteTitulo");
        TramiteTitulo tramiteTitulo = tramiteTituloDAO.findByAlumnoACEP(alumno);
        if (tramiteTitulo != null) {
            alumno.setResolucionTitulo((String) ObjectUtil.getParentTree(tramiteTitulo, "resolucion.numeroVisible"));
            alumno.setFechaTitulo((Date) ObjectUtil.getParentTree(tramiteTitulo, "resolucion.fecha"));
            log.debug("{}", alumno.getResolucionTitulo());
        }

        log.debug("tramiteTituloFacultad");
        TramiteTitulo tramiteTituloFacultad = tramiteTituloDAO.findByAlumnoFacultadACEP(alumno);
        if (tramiteTituloFacultad != null) {
            alumno.setResolucionTituloFacultad(tramiteTituloFacultad.getResolucionFacultad().getNumeroVisible());
            alumno.setFechaTituloFacultad(tramiteTituloFacultad.getResolucionFacultad().getFecha());
            log.debug("{}", alumno.getResolucionTituloFacultad());
            log.debug("{}", alumno.getFechaTituloFacultad());
        }

        if (alumno.getCicloActivo() != null) {
            if (alumno.getSituacionAcademica() != null) {
                if (alumno.getSituacionAcademica().isEgresado() || alumno.getSituacionAcademica().isGraduado()) {
                    EventoCicloAcademico eventoEgreso = eventoCicloAcademicoDAO.findByCicloAndEvento(alumno.getCicloActivo(), EventoAcademicoEnum.FECHAS_BACH);
                    alumno.setFechaEgreso(eventoEgreso != null ? eventoEgreso.getFechaFin() : null);
                }
            }
        }

        if (alumno.getCicloIngreso() != null) {
            if (alumno.getSituacionAcademica() != null) {
                EventoCicloAcademico eventoMatricula = eventoCicloAcademicoDAO.findByCicloAndEvento(alumno.getCicloIngreso(), EventoAcademicoEnum.FECHAS_BACH);
                alumno.setFechaMatricula(eventoMatricula != null ? eventoMatricula.getFechaInicio() : null);
            }
        }

        log.debug("VALIDA PROMEDIO GRADUADO");
        if (alumno.getSituacionAcademica().isEgresado() || alumno.getSituacionAcademica().isGraduado()) {
            log.debug("PROMEDIO GRADUADO");
            Egresado egresado = egresadoDAO.findByAlumno(alumno);
            DecimalFormat df = new DecimalFormat("#.00");

            if (egresado != null && egresado.getPromedioGraduacion() != null) {
                alumno.setPromedioPonderadoGraduacion(df.format(egresado.getPromedioGraduacion()));
            } else {
                alumno.setPromedioPonderadoGraduacion("0.00");
            }

        }

        AlumnoConsejero alumnoConsejero = alumnoConsejeroDAO.findByAlumnoCiclo(alumno, ds.getCicloAcademico());
        String tutorOcoordinador = "NN";

        if (alumnoConsejero != null) {
            tutorOcoordinador = alumnoConsejero.getConsejero().getColaborador().getPersona().getApellidosNombres();
        } else {
            List<Colaborador> coordinadores = colaboradorDAO.allCoordinatorCodeCareerOfStudent(CODIGO_OFICINA_TUTORIA.concat(alumno.getCarrera().getCodigo()));
            Optional<Persona> coordinadorCarrera = coordinadores.stream()
                    .filter(x -> x.getCargo().getCodigoEnum().equals(COORDTUTOR) && x.getOficina().getPersonaJefe() != null)
                    .map(Colaborador::getOficina).map(Oficina::getPersonaJefe).distinct().findAny();

            tutorOcoordinador = coordinadorCarrera.isPresent() ? coordinadorCarrera.get().getApellidosNombres() : "NN";
        }
        alumno.setTutorOcoordinador(tutorOcoordinador);

        return alumno;
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
        if (planCurricularBD == null) {
            throw new PhobosException("No existe el plan curricular indicado");
        }

        Carrera carreraAlu = alumnoBD.getCarrera();
        Carrera carreraPlan = planCurricularBD.getCarrera();
        if (carreraAlu.getId().longValue() != carreraPlan.getId()) {
            throw new PhobosException("El cambio de plan no corresponde a la misma especialidad del alumno");
        }

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
        if (!puedeCalcular) {
            throw new PhobosException("Usted no estÃ¡ autorizado para ejecutar esta acciÃ³n");
        }
        alumno = alumnoDAO.find(alumno);
        if (alumno.getModalidadEstudio().isPregrado()) {
            avanceCurricularService.generarAvanceCurricularByAlumno(alumno, ds);
        } else {
            avanceCurricularService.generarAvanceCurricularByAlumnoEPG(alumno, ds);

        }
    }

    @Override
    public List<AlumnoCicloCurso> allHistorialAlumno(Alumno alumno) {
        return alumnoCicloCursoDAO.allActivosByAlumno(alumno);
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
                "computadosCicloNivel",
                "computadosFacultadNivel",
                "computadosCarreraNivel",
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
                "alumno.modalidadEstudio.codigo",
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
                "cicloAcademico.tipoEnum",
                "cicloAcademico.id"
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
    public void calcularPromedio(Alumno alumnoForm, DataSessionPivot ds) {
        Boolean puedeCalcular = usuarioPuedeCalcular(ds);
        if (!puedeCalcular) {
            throw new PhobosException("Usted no está autorizado para ejecutar esta acción");
        }
        Alumno alumno = alumnoDAO.find(alumnoForm);
        if (alumno.getSituacionAcademica().getCodigoEnum() == SituacionAcademicaEnum.S_RA) {
            throw new PhobosException("Alumno renunciante no se recalcula promedios.");
        }
        promedioService.calcularSituacionAcademica(alumno, ds);
    }

    @Override
    public void calcularPromedios(DataSessionPivot ds) {
        Boolean puedeCalcular = usuarioPuedeCalcular(ds);
        if (!puedeCalcular) {
            throw new PhobosException("Usted no estÃ¡ autorizado para ejecutar esta acciÃ³n");
        }
        List<Alumno> alumnos = alumnoDAO.pendientesHistorial(ds.getCicloAcademico());

        log.debug("alumnos:::: {}", alumnos.size());
        for (Alumno alumno : alumnos) {
            promedioService.calcularSituacionAcademica(alumno, ds);
        }

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
            resumen.setCursosMatriculados(0);
            resumen.setCreditosMatriculados(0);
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
            MatriculaResumen resumenUpd = new MatriculaResumen(resumen.getId());
            resumenUpd.setCursosMatriculados(cursosMat);
            resumenUpd.setCreditosMatriculados(creditosMat);
            matriculaResumenDAO.updateColumns(resumenUpd, "cursosMatriculados", "creditosMatriculados");

            return resumenUpd;
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
                .filter(x -> x.getSeccion().isEstadoOperativo() || x.getSeccion().isEstadoBloqueado())
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

                    log.debug("idHorario:{}, idSeccion:{}", horarioSeccion.getId(), horarioSeccion.getSeccion().getId());

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

        if (alumnoBD == null) {
            throw new PhobosException("El alumno no existe en la base de datos");
        }

        if (orientacionBD == null) {
            throw new PhobosException("La orientaciÃ³n no existe en la base de datos");
        }

        Carrera carrAlu = alumnoBD.getCarrera();
        Carrera carrOri = orientacionBD.getCarrera();

        if (carrAlu.getId() != carrOri.getId().longValue()) {
            throw new PhobosException("La orientaciÃ³n no corresponde a la especialidad del alumno");
        }

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

            if (alumnoBD.getPlanCurricular() == null) {
                throw new PhobosException("La orientaciÃ³n no cuenta con plan curricular.");
            }

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
            if (rol.getCodigoEnum() == RolEnum.CALCULO_PROM_AVANCE) {
                puedeCalcular = true;
                break;
            }
        }
        return puedeCalcular;
    }

    @Override
    public List<RetiroCiclo> allRetiroCicloByAlumno(Alumno alumno) {
        List<RetiroCiclo> retiros = retiroCicloDAO.allRetiroCicloByAlumno(alumno);
        List<AlumnoCiclo> retirosHisto = alumnoCicloDAO.allRetiroCiclosByAlumno(alumno);

        Map<String, AlumnoCiclo> mapRetiroHisto = TypesUtil.convertListToMap("cicloAcademico.codigo", retirosHisto);
        for (RetiroCiclo retiro : retiros) {
            CicloAcademico ciclo = retiro.getCicloAcademico();
            retiro.setAplicado(mapRetiroHisto.get(ciclo.getCodigo()) != null);
        }
        return retiros;
    }

    @Override
    public List<RetiroCurso> allRetiroCursoByAlumno(Alumno alumno) {
        return retiroCursoDAO.allRetiroCursoByAlumno(alumno);
    }

    @Override
    public ObjectNode allDataAlumnoMerito(Alumno alumno) {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allActivesOrdenMeritoByAlumnoAscCustom(alumno);

        findMerito(alumnoCiclos, node, "CICLO");
        findMerito(alumnoCiclos, node, "FAC");
        findMerito(alumnoCiclos, node, "CAR");

        return node;
    }

    @Override
    @Transactional
    public Alumno aplicarRetiroCiclo(RetiroCiclo retiroForm, DataSessionPivot ds) {
        RetiroCiclo retiroBD = retiroCicloDAO.find(retiroForm.getId());
        if (retiroBD == null) {
            throw new PhobosException("No se ha ubicado el retiro de ciclo");
        }

        Alumno alumno = alumnoDAO.find(retiroBD.getAlumno());
        CicloAcademico ciclo = retiroBD.getCicloAcademico();

        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, ciclo);
        if (alumnoCiclo == null) {
            alumnoCiclo = new AlumnoCiclo();
            alumnoCiclo.defaultValuesToCreate(alumno, ciclo, ds.getUsuario());
            alumnoCiclo.setSituacionInicio(new SituacionAcademica(SituacionAcademicaEnum.S_SS));
            alumnoCiclo.setEstadoEnum(RCI);
            alumnoCicloDAO.save(alumnoCiclo);

        } else {
            if (alumnoCiclo.getEstadoEnum() != RCI) {
                if (alumnoCiclo.getEstadoEnum() == MAT) {
                    List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allByAlumnoCiclo(alumnoCiclo);
                    for (AlumnoCicloCurso aluCicloCurso : alumnoCicloCursos) {
                        aluCicloCurso.setEstadoEnum(RCI);
                        aluCicloCurso.setUserModificacion(ds.getUsuario());
                        aluCicloCurso.setFechaModificacion(new Date());
                        alumnoCicloCursoDAO.update(aluCicloCurso);
                    }
                }
                alumnoCiclo.setEstadoEnum(RCI);
                alumnoCiclo.setUserModificacion(ds.getUsuario());
                alumnoCiclo.setFechaModificacion(new Date());
                alumnoCicloDAO.update(alumnoCiclo);
            }
        }

        return alumno;
    }

    private ObjectNode findMerito(List<AlumnoCiclo> alumnoCiclos, ObjectNode node, String tipo) {
        ArrayNode arrays = new ArrayNode(JsonNodeFactory.instance);

        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
            objectNode.put("ciclo", alumnoCiclo.getCicloAcademico().getDescripcion());
            objectNode.put("cuadroHonor", returnCicloMerito(alumnoCiclo));
            if (tipo.equals("CICLO")) {
                objectNode.put("ordenMeritoNivel", alumnoCiclo.getOrdenMeritoCicloNivel());
                objectNode.put("ordenMeritoTotal", alumnoCiclo.getOrdenMeritoCiclo());
                objectNode.put("cantidadMeritoNivel", alumnoCiclo.getComputadosCicloNivel());
                objectNode.put("cantidadTotalMerito", alumnoCiclo.getControlMeritoCiclo().getAlumnosComputados());
            } else if (tipo.equals("FAC")) {
                objectNode.put("ordenMeritoNivel", alumnoCiclo.getOrdenMeritoFacultadNivel());
                objectNode.put("ordenMeritoTotal", alumnoCiclo.getOrdenMeritoFacultad());
                objectNode.put("cantidadMeritoNivel", alumnoCiclo.getComputadosFacultadNivel());
                objectNode.put("cantidadTotalMerito", alumnoCiclo.getControlMeritoFacultad().getAlumnosComputados());
            } else if (tipo.equals("CAR")) {
                if (alumnoCiclo.getOrdenMeritoCarrera() == null) {
                    continue;
                }
                objectNode.put("ordenMeritoNivel", alumnoCiclo.getOrdenMeritoCarreraNivel());
                objectNode.put("ordenMeritoTotal", alumnoCiclo.getOrdenMeritoCarrera());
                objectNode.put("cantidadMeritoNivel", alumnoCiclo.getComputadosCarreraNivel());
                objectNode.put("cantidadTotalMerito", alumnoCiclo.getControlMeritoCarrera().getAlumnosComputados());
            }
            arrays.add(objectNode);
        }
        node.set(tipo, arrays);
        return node;
    }

    private String returnCicloMerito(AlumnoCiclo alumnoCiclo) {
        if (alumnoCiclo.getCuadroHonorCicloNivel() != null) {
            return "C.Honor";
        } else if (alumnoCiclo.getQuintoSuperiorCicloNivel() != null) {
            return "5to.Super.";

        } else if (alumnoCiclo.getTercioSuperiorCicloNivel() != null) {
            return "3cio.Super.";
        }
        return "-";
    }

    @Override
    public List<AlumnoCicloCurso> dataEquivalente(AlumnoCursoCurricula alumnoCursoCurricula) {
        alumnoCursoCurricula = alumnoCursoCurriculaDAO.findById(alumnoCursoCurricula.getId());
        List<CursoEquivalente> cursoEquivalente = cursoEquivalenteDAO.allActivoByCursoCurricula(alumnoCursoCurricula.getCursoCurricula());
        List<Curso> cursos = cursoEquivalente.stream().map(x -> x.getCursoEquivalente()).collect(Collectors.toList());
        List<AlumnoCicloCurso> alumnoCicloCurso = alumnoCicloCursoDAO.allByAlumnoCursosApr(alumnoCursoCurricula.getAlumno(), cursos);
        List<AlumnoCicloCurso> cursosEquiv = new ArrayList<>();
        for (AlumnoCicloCurso acc : alumnoCicloCurso) {
            for (Curso cur : cursos) {
                if (cur.getId().equals(acc.getCurso().getId())) {
                    cursosEquiv.add(acc);
                }
            }
        }
        return cursosEquiv;
    }

    @Override
    @Transactional
    public void calcularPromediosNivelacion(DataSessionPivot ds) {

        Boolean puedeCalcular = usuarioPuedeCalcular(ds);
        if (!puedeCalcular) {
            throw new PhobosException("Usted no estÃ¡ autorizado para ejecutar esta acciÃ³n");
        }

        List<Alumno> alumnos = alumnoDAO.correccionNivelacion(ds.getCicloAcademico());

        log.debug("se van ha corregir {} alumnos", alumnos.size());

        for (Alumno alumno : alumnos) {
            promedioService.calcularSituacionAcademica(alumno, ds);
        }

    }

    @Override
    public Evaluado findEvaluadoAdmision(Alumno alumnoForm) {
        Alumno alumno = alumnoDAO.findAllInfo(alumnoForm.getId());
        if (alumno.getPostulantePregrado() == null) {
            return new Evaluado();
        }

        Postulante postulante = alumno.getPostulantePregrado();
        Evaluado evaluado = evaluadoDAO.findByPostulante(postulante);
        if (evaluado != null) {
            return evaluado;
        }

        Prelamolina cepre = prelamolinaDAO.findIngresanteByPostulante(postulante);
        if (cepre == null) {
            return new Evaluado();
        }

        evaluado = new Evaluado();
        evaluado.setPostulante(postulante);
        evaluado.setPuntajeAlgebra(cepre.getPuntajeAlgebra());
        evaluado.setPuntajeAritmetica(cepre.getPuntajeAritmetica());
        evaluado.setPuntajeBiologia(cepre.getPuntajeBiologia());
        evaluado.setPuntajeEconomia(cepre.getPuntajeEconomia());
        evaluado.setPuntajeFinal(cepre.getPuntajeFinal());
        evaluado.setPuntajeFisica(cepre.getPuntajeFisica());
        evaluado.setPuntajeGeografia(cepre.getPuntajeGeografia());
        evaluado.setPuntajeGeometria(cepre.getPuntajeGeometria());
        evaluado.setPuntajeHistoria(cepre.getPuntajeHistoria());
        evaluado.setPuntajeMatematicas(cepre.getPuntajeMatematicas());
        evaluado.setPuntajeQuimica(cepre.getPuntajeQuimica());
        evaluado.setPuntajeRm(cepre.getPuntajeRm());
        evaluado.setPuntajeRv(cepre.getPuntajeRv());
        evaluado.setPuntajeTrigonometria(cepre.getPuntajeTrigonometria());

        return evaluado;
    }

    @Override
    public List<TemaCiclo> allTemasAdmision(Alumno alumnoForm) {
        Alumno alumno = alumnoDAO.findAllInfo(alumnoForm.getId());
        Postulante postulante = alumno.getPostulantePregrado();
        if (postulante == null) {
            return new ArrayList();
        }

        List<TemaCiclo> temasCiclo = new ArrayList();
        List<TemaCiclo> temasCicloAll = temaCicloDAO.allByCiclo(postulante.getCicloPostula().getCicloAcademico());

        int orden = 1;
        Map<Long, TemaExamen> mapTemas = new HashMap();
        for (TemaCiclo temaCiclo : temasCicloAll) {
            TemaExamen tema = temaCiclo.getTemaExamen();
            TemaExamen temaSuper = tema.getTemaSuperior();

            if (temaSuper != null) {
                TemaExamen existe = mapTemas.get(temaSuper.getId());

                if (existe == null) {
                    TemaCiclo temaCicloSuper = new TemaCiclo();
                    temaCicloSuper.setId(9000L);
                    temaCicloSuper.setOrden(orden);
                    temaCicloSuper.setTemaExamen(temaSuper);
                    temasCiclo.add(temaCicloSuper);

                    orden++;
                    mapTemas.put(temaSuper.getId(), temaSuper);
                }
            }

            temaCiclo.setOrden(orden);
            temasCiclo.add(temaCiclo);
            orden++;
        }

        return temasCiclo;
    }

    @Override
    public List<NotaAlumnoNivelacion> allNotasNivelacion(Alumno alumno) {
        List<TemaExamen> temas = temaExamenDAO.all();
        List<CursoTemaExamen> cursosTemasAll = cursoTemaExamenDAO.all();
        Map<Long, List<CursoTemaExamen>> mapTemaCursos = cursosTemasAll.stream()
                .collect(Collectors.groupingBy(cutex -> cutex.getTemaExamen().getId()));

        List<NotaAlumnoNivelacion> notas = new ArrayList();
        List<NotaAlumnoNivelacion> notasAll = notaAlumnoNivelacionDAO.allConNotaByAlumno(alumno);

        for (TemaExamen tema : temas) {
            List<CursoTemaExamen> cursosTemas = mapTemaCursos.get(tema.getId());
            if (cursosTemas == null) {
                continue;
            }

            NotaAlumnoNivelacion notaAprobada = notasAll.stream()
                    .filter(nan -> nan.getAprobado() != null)
                    .filter(nan -> nan.getAprobado())
                    .filter(nan -> {
                        Curso curso = nan.getCurso();
                        return cursosTemas.stream()
                                .anyMatch(cutex -> cutex.getCurso().getId().equals(curso.getId()));
                    })
                    .findFirst().orElse(null);
            if (notaAprobada != null) {
                notas.add(notaAprobada);
                continue;
            }

            NotaAlumnoNivelacion notaDesaprobada = notasAll.stream()
                    .filter(nan -> nan.getAprobado() != null)
                    .filter(nan -> !nan.getAprobado())
                    .filter(nan -> {
                        Curso curso = nan.getCurso();
                        return cursosTemas.stream()
                                .anyMatch(cutex -> cutex.getCurso().getId().equals(curso.getId()));
                    })
                    .findFirst().orElse(null);
            if (notaDesaprobada != null) {
                notas.add(notaDesaprobada);
            }
        }

        return notas;
    }

    @Override
    public List<AlumnoCursoCicloDTO> allNotasHistorial(Alumno alumno) {
        List<TemaExamen> temas = temaExamenDAO.all();
        log.info("[allNotasHistorial] temas.size={}", temas.size());

        List<CursoTemaExamen> cursosTemasAll = cursoTemaExamenDAO.all();
        log.info("[allNotasHistorial] cursosTemasAll.size={}", cursosTemasAll.size());
        Map<Long, List<CursoTemaExamen>> mapTemaCursos = cursosTemasAll.stream()
                .collect(Collectors.groupingBy(cutex -> cutex.getTemaExamen().getId()));

        List<CursoReplicaNivelacion> replicasAll = cursoReplicaNivelacionDAO.all();
        log.info("[allNotasHistorial] replicasAll.size={}", replicasAll.size());
        Map<Long, List<CursoReplicaNivelacion>> mapReplicas = replicasAll.stream()
                .collect(Collectors.groupingBy(repli -> repli.getCursoNivelacion().getId()));

        List<AlumnoCicloCurso> alumnoCursos = alumnoCicloCursoDAO.allActivosByAlumno(alumno);
        log.info("[allNotasHistorial] alumnoCursos.size={}", alumnoCursos.size());

        List<AlumnoCursoCicloDTO> historial = new ArrayList();
        for (TemaExamen tema : temas) {
            log.info("[allNotasHistorial] analizado tema ={}", tema.getNombre());
            List<CursoTemaExamen> cursosTemas = mapTemaCursos.get(tema.getId());
            if (cursosTemas == null) {
                log.info("[allNotasHistorial] \tcursosTemas is null");
                continue;
            }

            log.info("[allNotasHistorial] \tcursosTemas.size={}", cursosTemas.size());
            for (CursoTemaExamen cursoTema : cursosTemas) {
                Curso cursoNivela = cursoTema.getCurso();
                log.info("[allNotasHistorial] \tcursoNivela.codigo={}", cursoNivela.getCodigo());
                List<CursoReplicaNivelacion> replicas = mapReplicas.get(cursoNivela.getId());
                if (replicas == null) {
                    log.info("[allNotasHistorial] \treplicas is null");
                    continue;
                }

                log.info("[allNotasHistorial] \treplicas.size={}", replicas.size());
                AlumnoCicloCurso cursoAprobado = alumnoCursos.stream()
                        .filter(acc -> acc.getAlumnoCiclo().getEstadoEnum() == MAT)
                        .filter(acc -> acc.getEstadoEnum() == MAT)
                        .filter(acc -> acc.getEstaAprobado() != null)
                        .filter(acc -> acc.getEstaAprobado() == 1)
                        .filter(acc -> {
                            Curso curso = acc.getCurso();
                            return replicas.stream()
                                    .anyMatch(repli -> repli.getCursoRegular().getId().equals(curso.getId()));
                        })
                        .findFirst().orElse(null);
                if (cursoAprobado != null) {
                    log.info("[allNotasHistorial] \tcursoAprobado.id={}", cursoAprobado.getId());
                    AlumnoCursoCicloDTO acc = new AlumnoCursoCicloDTO();
                    acc.setTemaExamen(tema);
                    acc.setCiclo(cursoAprobado.getAlumnoCiclo().getCicloAcademico());
                    acc.setCurso(cursoAprobado.getCurso());
                    acc.setNota(cursoAprobado.getNota());
                    acc.setAprobado(Boolean.TRUE);

                    historial.add(acc);
                    break;
                }

                AlumnoCicloCurso cursoDesaprobado = alumnoCursos.stream()
                        .filter(acc -> acc.getAlumnoCiclo().getEstadoEnum() == MAT)
                        .filter(acc -> acc.getEstadoEnum() == MAT)
                        .filter(acc -> acc.getEstaAprobado() != null)
                        .filter(acc -> acc.getEstaAprobado() == 0)
                        .filter(acc -> {
                            Curso curso = acc.getCurso();
                            return replicas.stream()
                                    .anyMatch(repli -> repli.getCursoRegular().getId().equals(curso.getId()));
                        })
                        .findFirst().orElse(null);
                if (cursoDesaprobado != null) {
                    log.info("[allNotasHistorial] \tcursoDesprobado.id={}", cursoDesaprobado.getId());
                    AlumnoCursoCicloDTO acc = new AlumnoCursoCicloDTO();
                    acc.setTemaExamen(tema);
                    acc.setCiclo(cursoDesaprobado.getAlumnoCiclo().getCicloAcademico());
                    acc.setCurso(cursoDesaprobado.getCurso());
                    acc.setNota(cursoDesaprobado.getNota());
                    acc.setAprobado(Boolean.FALSE);

                    historial.add(acc);
                    break;
                }
            }
        }

        log.info("[allNotasHistorial] historial.size={}", historial.size());
        return historial;
    }

}

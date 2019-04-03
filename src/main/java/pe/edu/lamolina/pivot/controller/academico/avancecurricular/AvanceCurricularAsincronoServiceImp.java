package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalente;
import pe.edu.lamolina.model.academico.CursoEquivalenteElectivo;
import pe.edu.lamolina.model.academico.CursoOpcionalCurricula;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.AlumnoCursoSimultaneoEstadoEnum;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.APR;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.EQUIV;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.HAB;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.NREQ;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.SIM;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.CONV;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.CULT;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELC;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.PROD;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.TECIND;
import pe.edu.lamolina.model.matricula.AlumnoAvanceCurricular;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoCursoSimultaneo;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.pivot.controller.academico.plancurricular.VisorAsignaCurricula;
import pe.edu.lamolina.pivot.dao.academico.AlumnoAvanceCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoSimultaneoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoEquivalenteDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoEquivalenteElectivoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoOpcionalCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.tramite.RetiroCicloDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AvanceCurricularAsincronoServiceImp implements AvanceCurricularAsincronoService {

    @Autowired
    PlanCurricularDAO planCurricularDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    RequisitoCursoCurriculaDAO requisitoCursoCurriculaDAO;

    @Autowired
    CursoCurriculaDAO cursoCurriculaDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AlumnoCursoSimultaneoDAO alumnoCursoSimultaneoDAO;

    @Autowired
    CursoEquivalenteDAO cursoEquivalenteDAO;

    @Autowired
    CursoEquivalenteElectivoDAO cursoEquivalenteElectivoDAO;

    @Autowired
    AlumnoAvanceCurricularDAO avanceCurricularDAO;

    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    RetiroCicloDAO retiroCicloDAO;

    @Autowired
    CursoOpcionalCurriculaDAO cursoOpcionalCurriculaDAO;

    @Autowired
    VisorAsignaCurricula visorAsignaCurricula;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<CursoCurriculaEstadoEnum> estadosAprobados = Arrays.asList(APR, CONV, EQUIV);

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAllAlumnoCursoCurriculaByAlumno(Alumno alumno) {
        alumnoCursoCurriculaDAO.deleteAllByAlumno(alumno);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAllAlumnoCursoSimultaneoByAlumno(Alumno alumno) {
        alumnoCursoSimultaneoDAO.deleteAllByAlumno(alumno);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void procesarAlumno(
            Alumno alumno,
            Map<Long, CursoCurricula> cursosCurricula,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitos,
            Map<Long, List<CursoEquivalente>> mapEquivalentes,
            Map<String, AlumnoCicloCurso> mapCursosVecesLlevado,
            List<MatriculaCurso> matriculaCursos,
            List<AlumnoCicloCurso> cursosAprobados,
            List<AlumnoCursoCurricula> alumnoCursoCurricula,
            DataSessionPivot ds) {

        procesarAlumnoSincrono(alumno, cursosCurricula, mapRequisitos, mapEquivalentes, mapCursosVecesLlevado, matriculaCursos, cursosAprobados, alumnoCursoCurricula, ds);

    }

    private void generarAvanceCurricular(Collection<AlumnoCursoCurricula> alumnoCursos, Alumno alumno) {
        Map<TipoCursoCurriculaEnum, TipoCursoCurricula> tipos = tipoCursoCurriculaDAO.all()
                .stream()
                .filter(x -> x.getCodigo() != null)
                .collect(Collectors.toMap(x -> x.getCodigoEnum(), x -> x, (a, b) -> a));

        Map<TipoCursoCurriculaEnum, AlumnoAvanceCurricular> avances = avanceCurricularDAO.allByAlumno(alumno)
                .stream()
                .filter(x -> x.getTipoCursoCurricula() != null)
                .collect(Collectors.toMap(x -> x.getTipoCursoCurricula().getCodigoEnum(), x -> x, (a, b) -> a));

        Map<TipoCursoCurriculaEnum, Integer> creditos = new HashMap();
        Map<TipoCursoCurriculaEnum, Integer> cursos = new HashMap();

        for (TipoCursoCurricula tipo : tipos.values()) {
            creditos.put(tipo.getCodigoEnum(), 0);
            cursos.put(tipo.getCodigoEnum(), 0);
        }

        for (AlumnoCursoCurricula curso : alumnoCursos) {
            if (curso.getEstadoEnum() == APR || curso.getEstadoEnum() == EQUIV) {

                TipoCursoCurriculaEnum tipo = curso.getCursoCurricula().getTipoCursoCurricula().getCodigoEnum();
                Integer prevCreditos = creditos.get(tipo);
                prevCreditos += curso.getCreditos();

                Integer prevCursos = cursos.get(tipo);
                prevCursos++;

                creditos.replace(tipo, prevCreditos);
                cursos.replace(tipo, prevCursos);

            }
        }

        for (TipoCursoCurricula tipo : tipos.values()) {
            AlumnoAvanceCurricular avance = avances.get(tipo.getCodigoEnum());
            if (avance == null) {
                avance = new AlumnoAvanceCurricular();
                avance.setTipoCursoCurricula(tipo);
                avance.setAlumno(alumno);
            }
            avance.setCreditos(creditos.get(tipo.getCodigoEnum()));
            avance.setCursos(cursos.get(tipo.getCodigoEnum()));

            avanceCurricularDAO.save(avance);
        }

    }

    @Override
    @Transactional
    public void procesarAlumnoSincrono(
            Alumno alumno,
            Map<Long, CursoCurricula> mapCursosCurricula,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitosCurricula,
            Map<Long, List<CursoEquivalente>> mapEquivalentesCurricula,
            Map<String, AlumnoCicloCurso> mapCursosVecesLlevado,
            List<MatriculaCurso> cursosMatriculados,
            List<AlumnoCicloCurso> cursosAprobados,
            List<AlumnoCursoCurricula> alumnoCurso,
            DataSessionPivot ds) {

        Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAluByCurso = new LinkedHashMap();
        Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAlu = new LinkedHashMap();
        Map<Long, List<AlumnoCursoCurricula>> mapAlumnoCurso = new HashMap<>();
        List<AlumnoCursoSimultaneo> cursosSimultaneosAlu = new ArrayList();

        List<TipoCursoCurricula> tipoCursoCurriculas = tipoCursoCurriculaDAO.all();
        List<PlanCurricular> planCurriculars = planCurricularDAO.all();
        int creditosAprobados = alumno.getCreditosAprobados();
        int creditosCurriculaAprobados = alumno.getCreditosCarreraAprobados();

        List<AlumnoCursoCurricula> cursosCurriObligatoriosAlu = alumnoCursoCurriculaDAO.allObligatoriosByAlumno(alumno);
        for (AlumnoCursoCurricula cursoCurriObligatorio : cursosCurriObligatoriosAlu) {
            cursoCurriObligatorio.setValidado(false);
            mapCursoCurriculaAluByCurso.put(cursoCurriObligatorio.getCurso().getId(), cursoCurriObligatorio);
            mapCursoCurriculaAlu.put(cursoCurriObligatorio.getCursoCurricula().getId(), cursoCurriObligatorio);
        }

        sincronizarConCurricula(mapCursosCurricula, mapCursoCurriculaAluByCurso, mapCursoCurriculaAlu, alumno);

        validarCreditosAprobados(mapCursosCurricula, mapCursoCurriculaAlu.values(), creditosAprobados, creditosCurriculaAprobados);
        validarTramiteRetiroCiclo(cursosAprobados, alumno, ds.getCicloAcademico());
        validarEquivalencias(mapCursoCurriculaAlu, mapEquivalentesCurricula, cursosAprobados);
        validarHistorial(mapCursoCurriculaAluByCurso, cursosAprobados, alumno);
        validarCursosComodin(alumno, mapCursoCurriculaAlu, mapCursoCurriculaAluByCurso, cursosAprobados, ds);
        validarCursosLibresCurricula(alumno, mapCursoCurriculaAlu, mapCursoCurriculaAluByCurso, cursosAprobados, tipoCursoCurriculas, planCurriculars, ds);
        validarCursosRequisito(mapCursoCurriculaAlu, mapRequisitosCurricula);
        validarCursosSimultaneo(mapCursoCurriculaAlu, cursosSimultaneosAlu, mapRequisitosCurricula, ds);
        validarCursosMatriculados(mapCursoCurriculaAluByCurso, cursosMatriculados, ds);
        mapAlumnoCurso = TypesUtil.convertListToMapList("alumno.id", alumnoCurso);

        for (AlumnoCursoCurricula alumnoCursoCurricula : mapCursoCurriculaAlu.values()) {
            alumnoCursoCurricula.setVecesCursado(0);
            Curso curso = alumnoCursoCurricula.getCurso();
            AlumnoCicloCurso cursoVeces = mapCursosVecesLlevado.get(alumno.getId() + "-" + curso.getId());
            System.out.println(alumno.getId() + "-" + curso.getId() + " cursoVeces = " + cursoVeces);
            if (cursoVeces != null) {
                System.out.println("Hay " + cursoVeces.getVecesCursado() + " veces cursado");
                alumnoCursoCurricula.setVecesCursado(cursoVeces.getVecesCursado());
            }
            alumnoCursoCurriculaDAO.save(alumnoCursoCurricula);
        }

        for (AlumnoCursoSimultaneo cursosSimultaneo : cursosSimultaneosAlu) {
            alumnoCursoSimultaneoDAO.save(cursosSimultaneo);
        }
        for (AlumnoCicloCurso cursosAprobado : cursosAprobados) {
            alumnoCicloCursoDAO.update(cursosAprobado);
        }
        generarAvanceCurricular(mapCursoCurriculaAlu.values(), alumno);
    }

    private void validarEquivalencias(
            Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAlu,
            Map<Long, List<CursoEquivalente>> mapEquivalentes,
            List<AlumnoCicloCurso> cursosAprobados) {

        Map<Long, AlumnoCicloCurso> mapCursosAprobados = TypesUtil.convertListToMap("curso.id", cursosAprobados);

        for (Map.Entry<Long, AlumnoCursoCurricula> entry : mapCursoCurriculaAlu.entrySet()) {
            AlumnoCursoCurricula cursoCurriAlu = entry.getValue();
            CursoCurricula cursoEvaluado = cursoCurriAlu.getCursoCurricula();

            List<CursoEquivalente> cursosEquivalentes = fillList(mapEquivalentes.get(cursoEvaluado.getId()));
            if (cursosEquivalentes.isEmpty()) {
                continue;
            }

            Map<Integer, List<CursoEquivalente>> mapGruposEquiv = TypesUtil.convertListToMapList("grupo", cursosEquivalentes);

            for (Map.Entry<Integer, List<CursoEquivalente>> entryGrupos : mapGruposEquiv.entrySet()) {
                boolean equivalenciaEncontrada = true;
                List<CursoEquivalente> cursosEquivGrupo = entryGrupos.getValue();

                for (CursoEquivalente cursoEq : cursosEquivGrupo) {
                    if (!mapCursosAprobados.containsKey(cursoEq.getCursoEquivalente().getId())) {
                        equivalenciaEncontrada = false;
                        break;
                    }
                }

                if (equivalenciaEncontrada) {
                    cursoCurriAlu.setEstadoEnum(EQUIV);
                    cursoCurriAlu.setValidado(true);
                    break;
                }
            }
        }
    }

    private void validarHistorial(
            Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAluByCurso,
            List<AlumnoCicloCurso> cursosAprobados,
            Alumno alumno) {

        Map<Long, AlumnoCicloCurso> mapCursosAprobados = TypesUtil.convertListToMap("curso.id", cursosAprobados);
        Map<Long, AlumnoCicloCurso> mapCursosVeces = TypesUtil.convertListToMap("alumnoCursoKey", cursosAprobados);

        for (AlumnoCicloCurso cursoAprobado : mapCursosAprobados.values()) {
            AlumnoCursoCurricula cursoCurriAlu = mapCursoCurriculaAluByCurso.get(cursoAprobado.getCurso().getId());
            if (cursoCurriAlu == null) {
                continue;
            }
            if (cursoAprobado.getNota().equals("TE")) {
                cursoCurriAlu.setEstadoEnum(CONV);
            } else {
                cursoCurriAlu.setEstadoEnum(APR);
            }
            cursoAprobado.setTipoCursoCurricula(cursoCurriAlu.getTipoCursoCurricula());
            cursoCurriAlu.setCicloAprobado(cursoAprobado.getAlumnoCiclo().getCicloAcademico());
            cursoCurriAlu.setCreditos(cursoAprobado.getCreditos());
            cursoCurriAlu.setNota(cursoAprobado.getNota());
            cursoCurriAlu.setValidado(true);
        }

        for (AlumnoCursoCurricula alumnoCursoCurricula : mapCursoCurriculaAluByCurso.values()) {
            alumnoCursoCurricula.setVecesCursado(0);
        }

        for (AlumnoCursoCurricula alumnoCursoCurricula : mapCursoCurriculaAluByCurso.values()) {
            Long idAlumno = alumnoCursoCurricula.getAlumno().getId();
            Long idCurso = alumnoCursoCurricula.getCurso().getId();
            AlumnoCicloCurso alumnoCicloCurso = mapCursosVeces.get(idAlumno + "-" + idCurso);
            if (alumnoCicloCurso != null) {
                alumnoCursoCurricula.setVecesCursado(alumnoCicloCurso.getVecesCursadoTransient());
            }
        }
    }

    private void sincronizarConCurricula(
            Map<Long, CursoCurricula> mapCursosCurricula,
            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAluByCurso,
            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAlu,
            Alumno alumno) {
        sincronizarCursosEliminados(mapCursosCurricula, mapCursosCurriculaAlu);
        sincronizarCursosAgregados(mapCursosCurricula, mapCursosCurriculaAluByCurso, mapCursosCurriculaAlu, alumno);
    }

    private void sincronizarCursosEliminados(Map<Long, CursoCurricula> mapCursosCurricula, Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAlu) {
        List<Long> toBeRemoved = new LinkedList();
        for (Map.Entry<Long, AlumnoCursoCurricula> entry : mapCursosCurriculaAlu.entrySet()) {
            if (!mapCursosCurricula.containsKey(entry.getKey())) {
                toBeRemoved.add(entry.getKey());
            }
        }
        for (Long id : toBeRemoved) {
            mapCursosCurriculaAlu.remove(id);
        }
    }

    private void sincronizarCursosAgregados(
            Map<Long, CursoCurricula> mapCursosCurricula,
            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAluByCurso,
            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAlu,
            Alumno alumno) {

        for (Map.Entry<Long, CursoCurricula> entry : mapCursosCurricula.entrySet()) {
            Long key = entry.getKey();
            CursoCurricula cursoCurri = entry.getValue();
            Curso curso = cursoCurri.getCurso();

            if (!mapCursosCurriculaAlu.containsKey(key)) {
                AlumnoCursoCurricula newCursoAlumno = new AlumnoCursoCurricula();
                newCursoAlumno.setAlumno(alumno);
                newCursoAlumno.setCicloAprobado(null);
                newCursoAlumno.setCreditos(cursoCurri.getCreditos());
                newCursoAlumno.setCurso(curso);
                newCursoAlumno.setNumeroCiclo(cursoCurri.getNumeroCiclo());
                newCursoAlumno.setCursoCurricula(cursoCurri);
                newCursoAlumno.setEstadoEnum(NREQ);
                newCursoAlumno.setNota(null);
                newCursoAlumno.setValidado(false);
                newCursoAlumno.setVecesCursado(0);
                newCursoAlumno.setTipoCursoCurricula(cursoCurri.getTipoCursoCurricula());
                mapCursosCurriculaAluByCurso.put(newCursoAlumno.getCurso().getId(), newCursoAlumno);
                mapCursosCurriculaAlu.put(newCursoAlumno.getCursoCurricula().getId(), newCursoAlumno);

            } else {
                AlumnoCursoCurricula oldCursoAlumno = mapCursosCurriculaAlu.get(key);
                oldCursoAlumno.setCurso(curso);
                oldCursoAlumno.setNumeroCiclo(cursoCurri.getNumeroCiclo());
                oldCursoAlumno.setTipoCursoCurricula(cursoCurri.getTipoCursoCurricula());
            }
        }

    }

    private void validarCreditosAprobados(
            Map<Long, CursoCurricula> mapCursosCurricula,
            Collection<AlumnoCursoCurricula> cursosCurriculaAlumno,
            int creditosAprobados, int creditosCurriculaAprobados) {

        for (AlumnoCursoCurricula cursoCurriAlu : cursosCurriculaAlumno) {
            if (cursoCurriAlu.isValidado()) {
                continue;
            }

            Long idCursoCurri = cursoCurriAlu.getCursoCurricula().getId();
            CursoCurricula cursoCurri = mapCursosCurricula.get(idCursoCurri);

            Integer creditosAprobadosRequisito = fillInteger(cursoCurri.getCreditosRequisito(), 0);
            Integer credidosCurriculaRequisito = fillInteger(cursoCurri.getCreditosCurriculaRequisito(), 0);

            if (creditosAprobadosRequisito > creditosAprobados) {
                cursoCurriAlu.setEstadoEnum(NREQ);
                cursoCurriAlu.setValidado(true);
            }
        }

    }

    private Integer fillInteger(Integer numero, int defecto) {
        if (numero == null) {
            return defecto;
        }
        return numero;
    }

    private void validarCursosLibresCurricula(Alumno alumno,
            Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAlu,
            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAluByCurso,
            List<AlumnoCicloCurso> cursosAprobados,
            List<TipoCursoCurricula> tipoCursoCurriculas,
            List<PlanCurricular> planCurriculars,
            DataSessionPivot ds) {

        List<TipoCursoCurriculaEnum> cursosElectivos = Arrays.asList(ELC, PROD, CULT, TECIND);
        for (AlumnoCursoCurricula cursoCurriAlu : mapCursoCurriculaAlu.values()) {
            if (cursosElectivos.contains(cursoCurriAlu.getTipoCursoCurricula().getCodigoEnum())) {
                AlumnoCicloCurso alumnoCicloCurso = cursosAprobados.stream().filter(x-> mapCursoCurriculaAlu.get(x.getCurso().getId()) == null).findAny().orElse(null);
                CursoOpcionalCurricula cursoOpcionalCurricula = cursoOpcionalCurriculaDAO.allByPlanCurricularAndCurso(alumno.getPlanCurricular(), alumnoCicloCurso.getCurso());
                cursoCurriAlu.setCursoCurricula(null);
                cursoCurriAlu.setCursoOpcional(cursoOpcionalCurricula);
                cursoCurriAlu.setTipoCursoCurricula(cursoCurriAlu.getTipoCursoCurricula());
                mapCursoCurriculaAlu.replace(cursoCurriAlu.getCursoCurricula().getId(), cursoCurriAlu);
            }
        }
        
        for (AlumnoCicloCurso cursosAprobado : cursosAprobados) {
            if (mapCursosCurriculaAluByCurso.get(cursosAprobado.getCurso().getId()) == null) {

                CursoOpcionalCurricula cursoOpcionalCurricula = cursoOpcionalCurriculaDAO.allByPlanCurricularAndCurso(alumno.getPlanCurricular(), cursosAprobado.getCurso());
                if (cursoOpcionalCurricula != null) {
                    cursosAprobado.setTipoCursoCurricula(tipoCursoCurriculas.stream().filter(x -> x.getCodigoEnum() == ELC).findAny().orElse(null));
                    addAlumnoCursoCurricula(alumno, cursosAprobado, mapCursoCurriculaAlu, cursoOpcionalCurricula);
                } else {
                    CursoEquivalenteElectivo cursoEquivalenteElectivo = cursoEquivalenteElectivoDAO.findCursoPlanCurricula(cursosAprobado.getCurso(), alumno.getPlanCurricular());
                    if (cursoEquivalenteElectivo == null) {
                        for (PlanCurricular planCurricular : planCurriculars) {

                            CursoOpcionalCurricula curricula = cursoOpcionalCurriculaDAO.allByPlanCurricularAndCurso(planCurricular, cursosAprobado.getCurso());
                            if (curricula != null) {

                            }
                        }
                    } else {

                    }
                }
            }
        }
    }

    private void addAlumnoCursoCurricula(Alumno alumno, AlumnoCicloCurso alumnoCicloCurso, Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAlu, CursoOpcionalCurricula opcionalCurricula) {
        AlumnoCursoCurricula convalidacion = new AlumnoCursoCurricula();
        convalidacion.setAlumno(alumno);
        convalidacion.setCicloAprobado(alumnoCicloCurso.getAlumnoCiclo().getCicloAcademico());
        convalidacion.setCreditos(alumnoCicloCurso.getCreditos());
        convalidacion.setCurso(alumnoCicloCurso.getCurso());
        convalidacion.setCursoOpcional(opcionalCurricula);
        convalidacion.setCursoOpcional(null);
        if (alumnoCicloCurso.getNota().equals("TE")) {
            convalidacion.setEstadoEnum(CONV);
        } else {
            convalidacion.setEstadoEnum(APR);
        }
        convalidacion.setNota(alumnoCicloCurso.getNota());
//        convalidacion.setNumeroCiclo(opcionalCurricula.getNumeroCiclo());
        convalidacion.setValidado(true);
        convalidacion.setVecesCursado(alumnoCicloCurso.getVecesCursado());
        mapCursoCurriculaAlu.put(opcionalCurricula.getId(), convalidacion);
    }

    private void validarCursosComodin(
            Alumno alumno,
            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAlu,
            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAluByCurso,
            List<AlumnoCicloCurso> cursosAprobados,
            DataSessionPivot ds) {

        Map<String, List<AlumnoCicloCurso>> mapCursosAprobadosByDpto = new HashMap();
        for (AlumnoCicloCurso aprobado : cursosAprobados) {
            if (aprobado.getCurso().getDepartamentoAcademico() == null) {
                continue;
            }

            String key = aprobado.getCurso().getDepartamentoAcademico().getCodigo();
            List<AlumnoCicloCurso> lista = mapCursosAprobadosByDpto.get(key);
            if (lista == null) {
                lista = new ArrayList();
                mapCursosAprobadosByDpto.put(key, lista);
            }
            lista.add(aprobado);
        }

        Set<String> codsCursosComodines = new HashSet(Arrays.asList("EG1006"));
        for (AlumnoCursoCurricula cursoCurriAlu : mapCursosCurriculaAlu.values()) {
            if (codsCursosComodines.contains(cursoCurriAlu.getCurso().getCodigo())) {
                List<String> departamentos = null;
                if (cursoCurriAlu.getCurso().getCodigo().equals("EG1006")) {
                    departamentos = Arrays.asList("ME", "OE", "FS");
                }

                AlumnoCicloCurso alumnoCicloCurso = validarConvalidaciones(departamentos, mapCursosAprobadosByDpto, mapCursosCurriculaAluByCurso);

                if (alumnoCicloCurso != null //&& alumnoCicloCurso.getCreditos() > 0
                        ) {
                    AlumnoCursoCurricula convalidacion = new AlumnoCursoCurricula();
                    convalidacion.setAlumno(alumno);
                    convalidacion.setCicloAprobado(alumnoCicloCurso.getAlumnoCiclo().getCicloAcademico());
                    convalidacion.setCreditos(alumnoCicloCurso.getCreditos());
                    convalidacion.setCurso(alumnoCicloCurso.getCurso());
                    convalidacion.setCursoCurricula(cursoCurriAlu.getCursoCurricula());
                    convalidacion.setCursoOpcional(null);
                    if (alumnoCicloCurso.getNota().equals("TE")) {
                        convalidacion.setEstadoEnum(CONV);
                    } else {
                        convalidacion.setEstadoEnum(APR);
                    }
                    convalidacion.setNota(alumnoCicloCurso.getNota());
                    convalidacion.setNumeroCiclo(cursoCurriAlu.getNumeroCiclo());
                    convalidacion.setValidado(true);
                    convalidacion.setVecesCursado(alumnoCicloCurso.getVecesCursado());
                    mapCursosCurriculaAlu.replace(cursoCurriAlu.getCursoCurricula().getId(), convalidacion);
                }
            }
        }
    }

    AlumnoCicloCurso validarConvalidaciones(
            List<String> departamentos,
            Map<String, List<AlumnoCicloCurso>> mapCursosAprobadosByDpto,
            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAlyByCurso) {

        for (String departamento : departamentos) {
            List<AlumnoCicloCurso> cursosAlumno = fillList(mapCursosAprobadosByDpto.get(departamento));
            for (AlumnoCicloCurso cursoAlumno : cursosAlumno) {
                if (!mapCursosCurriculaAlyByCurso.containsKey(cursoAlumno.getCurso().getId())) {
                    return cursoAlumno;
                }
            }

        }
        return null;
    }

    private void validarCursosRequisito(
            Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAlu,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitos) {

        for (Map.Entry<Long, AlumnoCursoCurricula> entry : mapCursoCurriculaAlu.entrySet()) {
            AlumnoCursoCurricula evaluado = entry.getValue();

            if (evaluado.isValidado() || estadosAprobados.contains(evaluado.getEstadoEnum())) {
                continue;
            }

            List<RequisitoCursoCurricula> requisitos = mapRequisitos.get(evaluado.getCursoCurricula().getId());
            if (requisitos == null || cumpleRequisitos(requisitos, mapCursoCurriculaAlu, evaluado)) {
                evaluado.setEstadoEnum(HAB);
            } else {
                evaluado.setEstadoEnum(NREQ);
                evaluado.setValidado(true);
            }
        }

    }

    private boolean cumpleRequisitos(
            List<RequisitoCursoCurricula> requisitos,
            Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAlu,
            AlumnoCursoCurricula evaluado) {

        List<RequisitoCursoCurricula> requisitosNoSimultaneos = requisitos.stream().filter(x -> x.getSimultaneo() != 1).collect(Collectors.toList());
        if (requisitosNoSimultaneos.isEmpty()) {
            return true;
        }

        boolean requisitosCumplidos = false;

        for (RequisitoCursoCurricula requisito : requisitos) {
            AlumnoCursoCurricula cursoRequisito = mapCursoCurriculaAlu.get(requisito.getCursoRequisito().getId());
            if (cursoRequisito == null || !estadosAprobados.contains(cursoRequisito.getEstadoEnum())) {
                if (!evaluado.getCursoCurricula().getRequisitosOr()) {
                    requisitosCumplidos = false;
                    break;
                }
            } else {
                requisitosCumplidos = requisitosCumplidos || true;
            }

        }

        return requisitosCumplidos;
    }

    private void validarCursosSimultaneo(
            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAlu,
            List<AlumnoCursoSimultaneo> cursosSimultaneoAlu,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitos, DataSessionPivot ds) {

        for (Map.Entry<Long, AlumnoCursoCurricula> entry : mapCursosCurriculaAlu.entrySet()) {

            AlumnoCursoCurricula evaluado = entry.getValue();
            if (evaluado.getEstadoEnum() != HAB) {
                continue;
            }

            List<RequisitoCursoCurricula> requisitos = mapRequisitos.get(evaluado.getCursoCurricula().getId());
            List<AlumnoCursoSimultaneo> requisitosSimultaneo = new ArrayList();

            if (requisitos == null || validarSimultaneos(requisitosSimultaneo, requisitos, mapCursosCurriculaAlu, evaluado, ds)) {
                if (requisitosSimultaneo.size() > 0) {
                    evaluado.setEstadoEnum(SIM);
                    cursosSimultaneoAlu.addAll(requisitosSimultaneo);
                } else {
                    evaluado.setEstadoEnum(HAB);
                }
            } else {
                evaluado.setEstadoEnum(NREQ);
            }
            evaluado.setValidado(true);
        }

    }

    private boolean validarSimultaneos(
            List<AlumnoCursoSimultaneo> simultaneos,
            List<RequisitoCursoCurricula> requisitos,
            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAlu,
            AlumnoCursoCurricula evaluado, DataSessionPivot ds) {
        boolean requisitosCumplidos = true;

        for (RequisitoCursoCurricula requisito : requisitos) {
            if (requisito.getSimultaneo() == 0) {
                continue;
            }

            AlumnoCursoCurricula cursoRequisito = mapCursosCurriculaAlu.get(requisito.getCursoRequisito().getId());
            if (cursoRequisito == null) {
                continue;
            }

            if (cursoRequisito.getEstadoEnum() == APR) {
            } else if (cursoRequisito.getEstadoEnum() == HAB) {
                AlumnoCursoSimultaneo alumnoCursoSimultaneo = new AlumnoCursoSimultaneo();
                alumnoCursoSimultaneo.setAlumnoCursoCurricula(evaluado);
                alumnoCursoSimultaneo.setCurso(requisito.getCursoRequisito().getCurso());
                alumnoCursoSimultaneo.setEstadoEnum(AlumnoCursoSimultaneoEstadoEnum.NMAT);
                alumnoCursoSimultaneo.setFechaRegistro(new Date());
                alumnoCursoSimultaneo.setUserRegistro(ds.getUsuario());
                simultaneos.add(alumnoCursoSimultaneo);
            } else {
                requisitosCumplidos = false;
                break;
            }
        }

        return requisitosCumplidos;
    }

    private void validarCursosMatriculados(
            Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAluByCurso,
            List<MatriculaCurso> cursosMatriculados, DataSessionPivot ds) {

        for (MatriculaCurso cursoMatriculado : cursosMatriculados) {
            AlumnoCursoCurricula cursoCurriAlu = mapCursoCurriculaAluByCurso.get(cursoMatriculado.getCurso().getId());
            if (cursoCurriAlu != null) {
                cursoCurriAlu.setEstadoMatriculaEnum(EstadoMatriculaEnum.MAT);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void settingPlanCurricular(Alumno alumno, PlanCurricular planBD) {
        alumno.setPlanCurricular(planBD);
        alumno.setOrientacionCarrera(null);
        if (planBD != null && planBD.getOrientacionCarrera() != null) {
            alumno.setOrientacionCarrera(planBD.getOrientacionCarrera());
        }
        alumnoDAO.updatePlanCurricular(alumno);
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void limpiarAlumno(Alumno alumno) {
        this.deleteAllAlumnoCursoSimultaneoByAlumno(alumno);
        this.deleteAllAlumnoCursoCurriculaByAlumno(alumno);
        this.settingPlanCurricular(alumno, null);

        visorAsignaCurricula.incrementar(alumno.getCarrera());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void crearAvanceCurricular(
            Alumno alumno,
            PlanCurricular planBD,
            Map<Long, CursoCurricula> mapCursoCurricula,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitoCursoCurricula,
            Map<Long, List<CursoEquivalente>> mapCursosEquivalentes,
            Map<String, AlumnoCicloCurso> mapCursosVecesLlevado,
            List<MatriculaCurso> cursosMatriculados,
            List<AlumnoCicloCurso> cursosAprobados,
            List<AlumnoCursoCurricula> alumnoCursoCurricula, DataSessionPivot ds) {

        Carrera carrera = alumno.getCarrera();
        this.settingPlanCurricular(alumno, planBD);
        logger.debug("Cantidad de Cursos: {}", mapCursoCurricula.size());
        this.deleteAllAlumnoCursoSimultaneoByAlumno(alumno);
        this.procesarAlumno(alumno, mapCursoCurricula, mapRequisitoCursoCurricula, mapCursosEquivalentes, mapCursosVecesLlevado, cursosMatriculados, cursosAprobados, alumnoCursoCurricula, ds);
        visorAsignaCurricula.incrementar(carrera);
    }

    private List fillList(List lista) {
        if (lista == null) {
            return new ArrayList();
        }
        return lista;
    }

    private void validarTramiteRetiroCiclo(List<AlumnoCicloCurso> cursosAprobados, Alumno alumno, CicloAcademico cicloAcademico) {
        RetiroCiclo retiroCiclo = retiroCicloDAO.findByAlumnoCicloRegistro(alumno, cicloAcademico);
        if (retiroCiclo != null) {

            List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumnoCicloRegularAct(alumno);

            for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoCurriculas) {
                alumnoCursoCurricula.setEstadoEnum(CursoCurriculaEstadoEnum.LIMB);
                alumnoCursoCurriculaDAO.update(alumnoCursoCurricula);

                AlumnoCicloCurso alumnoCicloCurso = cursosAprobados.stream().filter(x -> x.getCurso() == alumnoCursoCurricula.getCurso()).findAny().orElse(null);
                cursosAprobados.remove(alumnoCicloCurso);

            }
        }
    }

}

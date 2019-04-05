package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import pe.edu.lamolina.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.AlumnoCursoSimultaneoEstadoEnum;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.APR;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.HAB;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.NREQ;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.SIM;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.CONV;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.EQUIV;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.PEND;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.CULT;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.EEP;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELC;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELE;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.GEN;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.OBL;
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
import pe.edu.lamolina.pivot.dao.academico.ResumenPlanCurricularDAO;
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
    ResumenPlanCurricularDAO resumenPlanCurricularDAO;

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
            List<CursoOpcionalCurricula> cursoOpcional,
            Map<Long, CursoCurricula> mapCursoCurriculaByCurso,
            DataSessionPivot ds) {

        procesarAlumnoSincrono(alumno, cursosCurricula, mapRequisitos, mapEquivalentes, mapCursosVecesLlevado, matriculaCursos, cursosAprobados, alumnoCursoCurricula, cursoOpcional, mapCursoCurriculaByCurso, ds);

    }

    private void generarAvanceCurricular(List<AlumnoCursoCurricula> alumnoCursoElcCarreraNew,
            List<AlumnoCursoCurricula> alumnoCursoNew,
            Alumno alumno) {
        List<ResumenPlanCurricular> resumenPlanCurriculars = resumenPlanCurricularDAO.allByPlan(alumno.getPlanCurricular());

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
        ResumenPlanCurricular resumenPlanCurricular = resumenPlanCurriculars.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() == ELC).findAny().orElse(null);
        for (AlumnoCursoCurricula curso : alumnoCursoNew) {
            if (curso.getEstadoEnum() == APR || curso.getEstadoEnum() == EQUIV) {

                TipoCursoCurriculaEnum tipo = curso.getCursoCurricula().getTipoCursoCurricula().getCodigoEnum();
                Integer prevCreditos = creditos.get(tipo);
                prevCreditos += curso.getCreditos();

                if (Arrays.asList(ELE, ELC).contains(tipo)) {
                    Integer tmp = 0;
                    if (tipo == ELE) {
                        tmp = creditos.get(ELC);
                    } else {
                        tmp = creditos.get(ELE);
                    }
                    Integer sum = tmp + prevCreditos;
                    if (sum > resumenPlanCurricular.getCreditos()) {
                        TipoCursoCurricula tipoCursoCurriculaEEP = tipos.get(EEP);
                        curso.setTipoCursoCurricula(tipoCursoCurriculaEEP);
                        continue;
                    }
                }
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

    private void validarCursosELC(List<AlumnoCursoCurricula> alumnoCursoElcCarreraNew, List<AlumnoCursoCurricula> alumnoCursoNew, Alumno alumno) {
        List<AlumnoCursoCurricula> cursosELC = alumnoCursoElcCarreraNew.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() == ELC).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosComodinELC = alumnoCursoNew.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() == ELC).collect(Collectors.toList());
        Integer creditosELC = alumnoCursoElcCarreraNew.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() == ELC).mapToInt(AlumnoCursoCurricula::getCreditos).sum();

        for (AlumnoCursoCurricula cursoComodinELC : cursosComodinELC) {
            for (AlumnoCursoCurricula alumnoCurso : cursosELC) {
                creditosELC = creditosELC - alumnoCurso.getCreditos();
                if (creditosELC < 0) {
                    break;
                }
                cursoComodinELC.setCreditosCumplidos(alumnoCurso.getCreditos());

            }
            if (creditosELC < 0) {
                break;
            }
        }
    }

    @Override
    @Transactional
    public void procesarAlumnoSincrono(
            Alumno alumno,
            Map<Long, CursoCurricula> mapCursosCurricula, // por el idCursoCurricula
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitosCurricula, // por cursoCurricula
            Map<Long, List<CursoEquivalente>> mapEquivalentesCurricula, // por cursoCurricula
            Map<String, AlumnoCicloCurso> mapCursosVecesLlevado,
            List<MatriculaCurso> cursosMatriculados,
            List<AlumnoCicloCurso> cursosAprobados,
            List<AlumnoCursoCurricula> alumnoCursoOld,
            List<CursoOpcionalCurricula> cursoOpcionalCurriculas,
            Map<Long, CursoCurricula> mapCursosCurriculaByCurso, // por id curso
            DataSessionPivot ds) {
        List<TipoCursoCurriculaEnum> tipoCursoELCEnums = Arrays.asList(ELC, PROD, TECIND, CULT, ELE);

        Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAluByCurso = new LinkedHashMap();
        List<AlumnoCursoCurricula> alumnoCursoNew = new ArrayList<>();
        List<AlumnoCursoCurricula> alumnoCursoElcCarreraNew = new ArrayList<>();
        List<AlumnoCursoCurricula> alumnoCursoComodinDepNew = new ArrayList<>();

        for (CursoCurricula cursocurricula : mapCursosCurricula.values()) {

            AlumnoCursoCurricula cursosOpcionalesNew = new AlumnoCursoCurricula();
            cursosOpcionalesNew.setAlumno(alumno);
            cursosOpcionalesNew.setCreditos(0);
            cursosOpcionalesNew.setTipoCursoCurricula(cursocurricula.getTipoCursoCurricula());
            cursosOpcionalesNew.setCurso(cursocurricula.getCurso());
            cursosOpcionalesNew.setCursoOpcional(null);
            cursosOpcionalesNew.setCursoCurricula(cursocurricula);
            if (tipoCursoELCEnums.contains(cursocurricula.getTipoCursoCurricula())) {
                cursosOpcionalesNew.setEstadoEnum(PEND);
            } else {
                cursosOpcionalesNew.setEstadoEnum(NREQ);

            }
            cursosOpcionalesNew.setEstadoRegistro(EstadoEnum.INA.name());
            cursosOpcionalesNew.setNumeroCiclo(cursocurricula.getNumeroCiclo());
            cursosOpcionalesNew.setValidado(false);
            cursosOpcionalesNew.setVecesCursado(0);
            alumnoCursoNew.add(cursosOpcionalesNew);
        }
        for (AlumnoCursoCurricula cursoCurriObligatorio : alumnoCursoNew) {
            if (cursoCurriObligatorio.getCursoOpcional() == null) {
                cursoCurriObligatorio.setValidado(false);
                mapCursoCurriculaAluByCurso.put(cursoCurriObligatorio.getCurso().getId(), cursoCurriObligatorio);
            }
        }
        List<TipoCursoCurricula> tipoCursoCurriculas = tipoCursoCurriculaDAO.all();

        List<TipoCursoCurriculaEnum> tipoCursoGENEnum = Arrays.asList(GEN, OBL);
        List<PlanCurricular> planCurriculars = planCurricularDAO.all();
        Map<Long, CursoOpcionalCurricula> mapCursoOpcional = TypesUtil.convertListToMap("curso.id", cursoOpcionalCurriculas);
        Set<String> codsCursosComodines = new HashSet(Arrays.asList("EG1006"));
        List<String> departamentos = Arrays.asList("ME", "OE", "FS");;
        for (AlumnoCicloCurso cursosAprobado : cursosAprobados) {
            AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursoNew.stream().filter(x -> x.getCurso().getId() == cursosAprobado.getCurso().getId()).findAny().orElse(null);
            logger.debug("Nombre Curso : {}", cursosAprobado.getCurso().getNombre());
            logger.debug("Codigo Departamento: {}", cursosAprobado.getCurso().getDepartamentoAcademico().getCodigo());
            if (alumnoCursoCurricula != null) {
                cursosAprobado.setTipoCursoCurricula(alumnoCursoCurricula.getTipoCursoCurricula());
                alumnoCursoCurricula.setCreditos(cursosAprobado.getCreditos());
                alumnoCursoCurricula.setVecesCursado(cursosAprobado.getVecesCursado());
                alumnoCursoCurricula.setCicloAprobado(cursosAprobado.getAlumnoCiclo().getCicloAcademico());
                alumnoCursoCurricula.setEstadoRegistro(EstadoEnum.ACT.name());
                alumnoCursoCurricula.setNota(cursosAprobado.getNota());
                alumnoCursoCurricula.setValidado(true);
                if (cursosAprobado.getNota().equals("TE")) {
                    alumnoCursoCurricula.setEstadoEnum(CONV);
                } else {
                    alumnoCursoCurricula.setEstadoEnum(APR);
                }
            } else if (cursosAprobado.getCurso().getDepartamentoAcademico() != null && departamentos.contains(cursosAprobado.getCurso().getDepartamentoAcademico().getCodigo())) {

                validarCursosComodin(alumno, alumnoCursoComodinDepNew, cursosAprobado, ds);

            } else {
                addCursosLibresCurricula(alumno, cursosAprobado, alumnoCursoElcCarreraNew, tipoCursoCurriculas, planCurriculars, mapCursoOpcional);

            }
        }

        generarAvanceCurricular(alumnoCursoElcCarreraNew, alumnoCursoNew, alumno);
        validarCursosELC(alumnoCursoElcCarreraNew, alumnoCursoNew, alumno);
//        List<AlumnoCursoCurricula> alumCursoElc = alumnoCursoNew.stream().filter(x -> tipoCursoELCEnums.contains(x.getTipoCursoCurricula().getCodigoEnum())).collect(Collectors.toList());
//        for (AlumnoCursoCurricula alumnoCursoElc : alumnoCursoElcCarreraNew) {
//            creditos = creditos + alumnoCursoElc.getCreditos();
//            Boolean cumple = alumCursoElc.stream().anyMatch(x -> alumnoCursoElc.getCreditos() >= x.getCreditos());
//            if (cumple) {
//                AlumnoCursoCurricula cursoElc = alumCursoElc.stream().filter(x -> x.getCreditos() <= alumnoCursoElc.getCreditos()).findAny().orElse(null);
//                alumnoCursoElc.setCursoCurricula(cursoElc.getCursoCurricula());
//
//                alumnoCursoNew.remove(cursoElc);
//                alumnoCursoNew.add(alumnoCursoElc);
//            }
//        }
//
//        for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoNew) {
//            AlumnoCursoCurricula cursoCurriculaOld = alumnoCursoOld.stream().filter(x -> x.getCurso().getId() == alumnoCursoCurricula.getCurso().getId()).findAny().orElse(null);
//            if (cursoCurriculaOld != null) {
//                alumnoCursoCurricula.setId(cursoCurriculaOld.getId());
//            }
//        }
//        Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAlu = new LinkedHashMap();
//        Map<Long, AlumnoCursoCurricula> mapCursosElectivos = new LinkedHashMap();
//
//        Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurricula = TypesUtil.convertListToMap("curso.id", alumnoCursoNew);
// //
//        List<AlumnoCursoSimultaneo> cursosSimultaneosAlu = new ArrayList();
//
//        List<TipoCursoCurricula> tipoCursoCurriculas = tipoCursoCurriculaDAO.all();
//        List<PlanCurricular> planCurriculars = planCurricularDAO.all();
//        int creditosAprobados = alumno.getCreditosAprobados();
//        int creditosCurriculaAprobados = alumno.getCreditosCarreraAprobados();
//        
//        sincronizarConCurricula(mapCursosCurricula, mapCursoCurriculaAluByCurso, mapCursoCurriculaAlu, alumno);
//
//        validarCreditosAprobados(mapCursosCurricula, mapCursoCurriculaAlu.values(), creditosAprobados, creditosCurriculaAprobados);
////        validarTramiteRetiroCiclo(cursosAprobados, alumno, ds.getCicloAcademico());
//        validarEquivalencias(mapCursoCurriculaAlu, mapEquivalentesCurricula, cursosAprobados);
//        validarHistorial(mapCursoCurriculaAluByCurso, cursosAprobados, alumno);
//        validarCursosComodin(alumno, mapCursoCurriculaAlu, mapCursoCurriculaAluByCurso, cursosAprobados, ds);
//        addCursosLibresCurricula(alumno, mapCursosElectivos, cursosAprobados, tipoCursoCurriculas, planCurriculars, ds, mapCursosCurriculaByCurso, mapCursoOpcional);
//        validarCursosRequisito(mapCursoCurriculaAlu, mapRequisitosCurricula);
//        validarCursosSimultaneo(mapCursoCurriculaAlu, cursosSimultaneosAlu, mapRequisitosCurricula, ds);
//        validarCursosMatriculados(mapCursoCurriculaAluByCurso, cursosMatriculados, ds);
//        
//        
//        for (AlumnoCursoCurricula alumnoCursoCurricula : mapCursoCurriculaAlu.values()) {
//            alumnoCursoCurricula.setVecesCursado(0);
//            Curso curso = alumnoCursoCurricula.getCurso();
//            AlumnoCicloCurso cursoVeces = mapCursosVecesLlevado.get(alumno.getId() + "-" + curso.getId());
//            System.out.println(alumno.getId() + "-" + curso.getId() + " cursoVeces = " + cursoVeces);
//            if (cursoVeces != null) {
//                System.out.println("Hay " + cursoVeces.getVecesCursado() + " veces cursado");
//                alumnoCursoCurricula.setVecesCursado(cursoVeces.getVecesCursado());
//            }
//            alumnoCursoCurriculaDAO.save(alumnoCursoCurricula);
//        }
//
//        for (AlumnoCursoSimultaneo cursosSimultaneo : cursosSimultaneosAlu) {
//            alumnoCursoSimultaneoDAO.save(cursosSimultaneo);
//        }
//        for (AlumnoCicloCurso cursosAprobado : cursosAprobados) {
//            alumnoCicloCursoDAO.update(cursosAprobado);
//        }
//        generarAvanceCurricular(mapCursoCurriculaAlu.values(), alumno);
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

    private void addCursosLibresCurricula(Alumno alumno,
            AlumnoCicloCurso cursosAprobado,
            List<AlumnoCursoCurricula> alumnoCursoElecCarreraNew,
            List<TipoCursoCurricula> tipoCursoCurriculas,
            List<PlanCurricular> planCurriculars,
            Map<Long, CursoOpcionalCurricula> mapCursoOpcional
    ) {

        CursoOpcionalCurricula cursoOpcionalCurricula = mapCursoOpcional.get(cursosAprobado.getCurso().getId());
        if (cursoOpcionalCurricula != null) {
            cursosAprobado.setTipoCursoCurricula(cursoOpcionalCurricula.getTipoCursoCurricula());
            addAlumnoCursoCurricula(alumno, cursosAprobado, cursoOpcionalCurricula, alumnoCursoElecCarreraNew);
        } else {
            CursoEquivalenteElectivo cursoEquivalenteElectivo = cursoEquivalenteElectivoDAO.findCursoPlanCurricula(cursosAprobado.getCurso(), alumno.getPlanCurricular());
            if (cursoEquivalenteElectivo == null) {
                for (PlanCurricular planCurricular : planCurriculars) {
                    if (Objects.equals(planCurricular.getId(), alumno.getPlanCurricular().getId())) {
                        continue;
                    }
                    CursoOpcionalCurricula curricula = cursoOpcionalCurriculaDAO.allByPlanCurricularAndCurso(planCurricular, cursosAprobado.getCurso());
                    if (curricula != null) {
                        curricula.setTipoCursoCurricula(tipoCursoCurriculas.stream().filter(x -> x.getCodigoEnum() == ELE).findAny().orElse(null));
                        addAlumnoCursoCurricula(alumno, cursosAprobado, curricula, alumnoCursoElecCarreraNew);
                        break;
                    }
                }
            } else {
                cursosAprobado.setCursoEquivalente(cursoEquivalenteElectivo.getCursoOpcionalCurricula().getCurso());
                cursosAprobado.setEsEquivalente(Boolean.TRUE);
                addAlumnoCursoCurricula(alumno, cursosAprobado, cursoEquivalenteElectivo.getCursoOpcionalCurricula(), alumnoCursoElecCarreraNew);
            }
        }
    }

    private void addAlumnoCursoCurricula(Alumno alumno, AlumnoCicloCurso alumnoCicloCurso, CursoOpcionalCurricula opcionalCurricula, List<AlumnoCursoCurricula> alumnoCursoElecCarreraNew) {

        AlumnoCursoCurricula cursosOpcionalesNew = new AlumnoCursoCurricula();
        cursosOpcionalesNew.setAlumno(alumno);
        cursosOpcionalesNew.setCicloAprobado(alumnoCicloCurso.getAlumnoCiclo().getCicloAcademico());
        cursosOpcionalesNew.setCreditos(alumnoCicloCurso.getCreditos());
        cursosOpcionalesNew.setTipoCursoCurricula(opcionalCurricula.getTipoCursoCurricula());
        cursosOpcionalesNew.setCurso(alumnoCicloCurso.getCurso());
        cursosOpcionalesNew.setCursoOpcional(opcionalCurricula);
        cursosOpcionalesNew.setCursoCurricula(null);
        if (alumnoCicloCurso.getNota().equals("TE")) {
            cursosOpcionalesNew.setEstadoEnum(CONV);
        } else {
            cursosOpcionalesNew.setEstadoEnum(APR);
        }
        cursosOpcionalesNew.setNota(alumnoCicloCurso.getNota());
        cursosOpcionalesNew.setValidado(true);
        cursosOpcionalesNew.setVecesCursado(alumnoCicloCurso.getVecesCursado());
        alumnoCursoElecCarreraNew.add(cursosOpcionalesNew);
    }

    private void validarCursosComodin(
            Alumno alumno,
            List<AlumnoCursoCurricula> alumnoCursoComodinNew,
            AlumnoCicloCurso aprobado,
            DataSessionPivot ds) {

        Set<String> codsCursosComodines = new HashSet(Arrays.asList("EG1006"));

        logger.debug("Creditos Curso: {}", aprobado.getCreditos());
        if (aprobado.getCreditos() > 0) {
            AlumnoCursoCurricula convalidacion = new AlumnoCursoCurricula();
            convalidacion.setAlumno(alumno);
            convalidacion.setCicloAprobado(aprobado.getAlumnoCiclo().getCicloAcademico());
            convalidacion.setCreditos(aprobado.getCreditos());
            convalidacion.setCurso(aprobado.getCurso());
            convalidacion.setCursoCurricula(null);
            convalidacion.setCursoOpcional(null);
            if (aprobado.getNota().equals("TE")) {
                convalidacion.setEstadoEnum(CONV);
            } else {
                convalidacion.setEstadoEnum(APR);
            }
            convalidacion.setEstadoRegistro(EstadoEnum.ACT.name());
            convalidacion.setNota(aprobado.getNota());
            convalidacion.setValidado(true);
            convalidacion.setVecesCursado(aprobado.getVecesCursado());
            alumnoCursoComodinNew.add(convalidacion);
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
            List<AlumnoCursoCurricula> alumnoCursoCurricula,
            List<CursoOpcionalCurricula> opcionalCurriculas,
            Map<Long, CursoCurricula> mapCursoCurriculaByCurso,
            DataSessionPivot ds) {

        Carrera carrera = alumno.getCarrera();
        this.settingPlanCurricular(alumno, planBD);
        logger.debug("Cantidad de Cursos: {}", mapCursoCurricula.size());
        this.deleteAllAlumnoCursoSimultaneoByAlumno(alumno);
        this.procesarAlumno(alumno, mapCursoCurricula, mapRequisitoCursoCurricula, mapCursosEquivalentes, mapCursosVecesLlevado, cursosMatriculados, cursosAprobados, alumnoCursoCurricula, opcionalCurriculas, mapCursoCurriculaByCurso, ds);
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

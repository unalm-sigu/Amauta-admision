package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Carrera;
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
import static pe.edu.lamolina.model.enums.EstadoEnum.INA;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.CULT;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.DEP;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.EAD;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.EEP;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELC;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELE;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.GEN;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.PROD;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.TECIND;
import pe.edu.lamolina.model.matricula.AlumnoAvanceCurricular;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoCursoSimultaneo;
import pe.edu.lamolina.model.posgrado.CursoHabilEscuela;
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

    private final List<String> tipoCursoELCEnums = Arrays.asList("CULT", "ELC", "PROD", "TECIND");
    private final List<CursoCurriculaEstadoEnum> estadosAprobados = Arrays.asList(APR, CONV, EQUIV);
    private final List<String> codesDptosCultDepMed = Arrays.asList("ME", "OE", "FS", "OB");

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
            List<TipoCursoCurricula> tipoCursoCurriculas,
            List<ResumenPlanCurricular> resumenPlanCurriculars,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            List<CursoEquivalenteElectivo> equivalenteElectivos,
            Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcionalAll,
            List<PlanCurricular> planCurriculars,
            Map<Long, List<CursoCurricula>> mapCursoCurriculaAll,
            DataSessionPivot ds) {

        procesarAlumnoSincrono(
                alumno,
                cursosCurricula,
                mapRequisitos,
                mapEquivalentes,
                mapCursosVecesLlevado,
                matriculaCursos,
                cursosAprobados,
                alumnoCursoCurricula,
                cursoOpcional,
                mapCursoCurriculaByCurso,
                tipoCursoCurriculas,
                resumenPlanCurriculars,
                alumnoAvanceCurriculars,
                equivalenteElectivos,
                mapCursoOpcionalAll,
                planCurriculars,
                mapCursoCurriculaAll,
                ds);

    }

    private void generarAvanceCurricular(List<AlumnoCursoCurricula> alumnoCursoElcCarreraNew,
            List<AlumnoCursoCurricula> alumnoCursoNew,
            List<ResumenPlanCurricular> resumenPlanCurriculars,
            List<TipoCursoCurricula> tipoCursoCurriculas,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            Alumno alumno,
            Map<String, AlumnoCicloCurso> mapCursosVecesLlevado) {
//       

        Map<TipoCursoCurriculaEnum, TipoCursoCurricula> tipos = tipoCursoCurriculas
                .stream()
                .filter(x -> x.getCodigo() != null)
                .collect(Collectors.toMap(x -> x.getCodigoEnum(), x -> x, (a, b) -> a));

        Map<TipoCursoCurriculaEnum, AlumnoAvanceCurricular> avances = alumnoAvanceCurriculars
                .stream()
                .filter(x -> x.getTipoCursoCurricula() != null)
                .collect(Collectors.toMap(x -> x.getTipoCursoCurricula().getCodigoEnum(), x -> x, (a, b) -> a));

        Map<TipoCursoCurriculaEnum, Integer> creditos = new HashMap();
        Map<TipoCursoCurriculaEnum, Integer> cursos = new HashMap();
        Map<TipoCursoCurriculaEnum, Boolean> excepción = new HashMap();

        ResumenPlanCurricular resumenPlanCurricularELC = resumenPlanCurriculars.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() == ELC).findAny().orElse(null);
        ResumenPlanCurricular resumenPlanCurricularELE = resumenPlanCurriculars.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() == ELE).findAny().orElse(null);

        for (TipoCursoCurricula tipo : tipos.values()) {
            creditos.put(tipo.getCodigoEnum(), 0);
            cursos.put(tipo.getCodigoEnum(), 0);
            excepción.put(tipo.getCodigoEnum(), Boolean.FALSE);
        }
        alumnoCursoNew.addAll(alumnoCursoElcCarreraNew);
        Collections.sort(alumnoCursoNew, new AlumnoCursoCurricula.CompareCodigo());
        Collections.sort(alumnoCursoNew, new AlumnoCursoCurricula.CompareCreditos());
        List<Long> idsEEP = new ArrayList();
        Integer sum = 0;

        for (AlumnoCursoCurricula curso : alumnoCursoNew) {

            if (curso.getVecesCursado() == 0) {
                AlumnoCicloCurso cat = mapCursosVecesLlevado.get(curso.getAlumno().getId() + "-" + curso.getCurso().getId());
                if (cat != null) {
                    curso.setVecesCursado(cat.getVecesCursadoTransient());
                }
            }
            if (Arrays.asList(APR, EQUIV, CONV).contains(curso.getEstadoEnum())) {
                TipoCursoCurriculaEnum tipo = null;

                tipo = curso.getTipoCursoCurricula().getCodigoEnum();

                ResumenPlanCurricular rpc = resumenPlanCurriculars.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() == curso.getTipoCursoCurricula().getCodigoEnum()).findAny().orElse(new ResumenPlanCurricular());
                if (rpc == null) {
                    continue;
                }
                Integer prevCreditos = creditos.get(tipo);
                prevCreditos += curso.getCreditos();

                if (Arrays.asList(ELE, ELC, CULT, PROD, TECIND).contains(tipo)) {
                    Integer tmp = 0;
                    Boolean res = prevCreditos >= rpc.getCreditos();
                    if (Arrays.asList(ELE, ELC).contains(tipo) && resumenPlanCurricularELE.getCreditos() > 0) {
                        tmp = creditos.get(tipo == ELC ? ELE : ELC);
                        sum = tmp + prevCreditos;
                        resumenPlanCurricularELC = resumenPlanCurricularELC == null ? new ResumenPlanCurricular() : resumenPlanCurricularELC;
                        res = sum >= resumenPlanCurricularELC.getCreditos();
                    }

                    if (res) {
                        res = excepción.get(tipo);
                        excepción.replace(tipo, Boolean.TRUE);
                    }
                    if (res || rpc.getCreditos() == 0) {
                        tipo = EEP;
                        prevCreditos = creditos.get(tipo);
                        prevCreditos += curso.getCreditos();
                        TipoCursoCurricula tipoCursoCurriculaEEP = tipos.get(EEP);
                        curso.setTipoCursoCurricula(tipoCursoCurriculaEEP);
                        idsEEP.add(curso.getCurso().getId());
                    }
                }
                Integer prevCursos = cursos.get(tipo);
                prevCursos++;

                creditos.replace(tipo, prevCreditos);
                cursos.replace(tipo, prevCursos);
            }
        }
        List<AlumnoCursoCurricula> alumnoCursoTemp = new ArrayList();
        for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoNew) {
            alumnoCursoTemp.add(alumnoCursoCurricula);
        }
        alumnoCursoNew.clear();
        for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoTemp) {
            if (idsEEP.contains(alumnoCursoCurricula.getCurso().getId())) {
                continue;
            }
            alumnoCursoNew.add(alumnoCursoCurricula);
        }
        Integer cred = 0;
        Integer credCarrera = 0;
        Integer cur = 0;
        Integer curCarrera = 0;
        for (TipoCursoCurricula tipo : tipos.values()) {
            AlumnoAvanceCurricular avance = avances.get(tipo.getCodigoEnum());
            if (avance == null) {
                avance = new AlumnoAvanceCurricular();
                avance.setTipoCursoCurricula(tipo);
                avance.setAlumno(alumno);
            }
            Integer creditosDep = 0;
            Integer cursosDep = 0;
            if (tipo.getCodigoEnum() == GEN) {
                creditosDep = creditos.get(DEP);
                cursosDep = cursos.get(DEP);
            }
            avance.setCreditos(creditos.get(tipo.getCodigoEnum()) + creditosDep);
            avance.setCursos(cursos.get(tipo.getCodigoEnum()) + cursosDep);

            avanceCurricularDAO.save(avance);
            cred = cred + creditos.get(tipo.getCodigoEnum());
            cur = cur + cursos.get(tipo.getCodigoEnum());
            if (tipo.getCodigoEnum() != EEP) {
                credCarrera = credCarrera + creditos.get(tipo.getCodigoEnum());
                curCarrera = +cursos.get(tipo.getCodigoEnum());
            }
        }
        alumno.setCursosCarreraAprobados(curCarrera);
        alumno.setCreditosCarreraAprobados(credCarrera);
        alumnoDAO.update(alumno);
    }

    private void validarCursosComodin(List<AlumnoCursoCurricula> alumnoCursoComodinDepNew, List<AlumnoCursoCurricula> alumnoCursoNew, List<ResumenPlanCurricular> resumenPlanCurriculars, TipoCursoCurricula tipoCursoCurricula) {
        List<AlumnoCursoCurricula> cursosComodinNew = alumnoCursoNew.stream().filter(x -> x.getCurso().getCodigo().equals("EG1006")).collect(Collectors.toList());
        List<Long> ids = new ArrayList();
        ResumenPlanCurricular resumenPlanCurricularDep = resumenPlanCurriculars.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() == DEP).findAny().orElse(null);
        Integer cred = resumenPlanCurricularDep.getCreditos();
        for (AlumnoCursoCurricula alumnoCursoCurricula : cursosComodinNew) {
            for (AlumnoCursoCurricula comodin : alumnoCursoComodinDepNew) {
                if (ids.contains(comodin.getCurso().getId())) {
                    continue;
                }
                if (cred == 0) {
                    break;
                }

                cred = cred - comodin.getCreditos();
                ids.add(comodin.getCurso().getId());
                alumnoCursoCurricula.setEstadoRegistro(INA.name());
                comodin.setNumeroCiclo(alumnoCursoCurricula.getNumeroCiclo());
                comodin.setTipoCursoCurricula(tipoCursoCurricula);
                comodin.setCursoCurricula(alumnoCursoCurricula.getCursoCurricula());
                alumnoCursoNew.add(comodin);
                break;
            }
        }
    }

    private void validarCursosELC(List<AlumnoCursoCurricula> alumnoCursoElcCarreraNew, List<AlumnoCursoCurricula> alumnoCursoNew, Alumno alumno) {
        List<AlumnoCursoCurricula> cursosELC = alumnoCursoElcCarreraNew.stream().filter(x -> Arrays.asList(ELC, ELE).contains(x.getTipoCursoCurricula().getCodigoEnum()) && x.getCicloAprobado() != null).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosPRO = alumnoCursoElcCarreraNew.stream().filter(x -> Arrays.asList(PROD).contains(x.getTipoCursoCurricula().getCodigoEnum()) && x.getCicloAprobado() != null).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosCULT = alumnoCursoElcCarreraNew.stream().filter(x -> Arrays.asList(CULT).contains(x.getTipoCursoCurricula().getCodigoEnum()) && x.getCicloAprobado() != null).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosTECIND = alumnoCursoElcCarreraNew.stream().filter(x -> Arrays.asList(TECIND).contains(x.getTipoCursoCurricula().getCodigoEnum()) && x.getCicloAprobado() != null).collect(Collectors.toList());

        List<AlumnoCursoCurricula> cursosComodinELC = alumnoCursoNew.stream().filter(x -> x.getCurso().getCodigo().equals("ELC")).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosComodinPRO = alumnoCursoNew.stream().filter(x -> x.getCurso().getCodigo().equals("PROD")).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosComodinCULT = alumnoCursoNew.stream().filter(x -> x.getCurso().getCodigo().equals("CULT")).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosComodinTECIND = alumnoCursoNew.stream().filter(x -> x.getCurso().getCodigo().equals("TECIND")).collect(Collectors.toList());

        Integer creditosELC = cursosELC.stream().mapToInt(AlumnoCursoCurricula::getCreditos).sum();
        Integer creditosPRO = cursosComodinPRO.stream().mapToInt(AlumnoCursoCurricula::getCreditos).sum();
        Integer creditosCULT = cursosComodinCULT.stream().mapToInt(AlumnoCursoCurricula::getCreditos).sum();
        Integer creditosTECIND = cursosComodinTECIND.stream().mapToInt(AlumnoCursoCurricula::getCreditos).sum();

        List<Long> idsAgregados = new ArrayList();
        Collections.sort(cursosComodinELC, new AlumnoCursoCurricula.CompareCreditos());
        Collections.sort(cursosPRO, new AlumnoCursoCurricula.CompareCreditos());
        Collections.sort(cursosCULT, new AlumnoCursoCurricula.CompareCreditos());
        Collections.sort(cursosTECIND, new AlumnoCursoCurricula.CompareCreditos());
        Collections.sort(cursosELC, new AlumnoCursoCurricula.CompareCreditos());
        Collections.sort(cursosComodinPRO, new AlumnoCursoCurricula.CompareCreditos());
        Collections.sort(cursosComodinCULT, new AlumnoCursoCurricula.CompareCreditos());
        Collections.sort(cursosComodinTECIND, new AlumnoCursoCurricula.CompareCreditos());

        for (AlumnoCursoCurricula cursoComodinELC : cursosComodinELC) {
            for (AlumnoCursoCurricula alumnoCurso : cursosELC) {
                if (idsAgregados.contains(alumnoCurso.getCurso().getId()) && alumnoCurso.getCicloAprobado() != null) {
                    continue;
                }
                idsAgregados.add(alumnoCurso.getCurso().getId());
                creditosELC = creditosELC - alumnoCurso.getCreditos();
                if (cursoComodinELC.getCreditosCumplidos() == null) {
                    cursoComodinELC.setCreditosCumplidos(0);
                }
//                if (alumnoCurso.getCreditos() >= ((cursoComodinELC.getCreditos() + creditosAdic) - cursoComodinELC.getCreditosCumplidos())) {
//                    alumnoCurso.setNumeroCiclo(cursoComodinELC.getNumeroCiclo());
//                    cursoComodinELC.setCreditosCumplidos(cursoComodinELC.getCreditosCumplidos() + alumnoCurso.getCreditos());
//                    if (cursoComodinELC.getCreditos() <= cursoComodinELC.getCreditosCumplidos()) {
//                        cursoComodinELC.setNumeroCiclo(cursoComodinELC.getNumeroCiclo());
//                        cursoComodinELC.setEstadoRegistro(EstadoEnum.INA.name());
//                    }
//                    break;
//                } else {
                alumnoCurso.setNumeroCiclo(cursoComodinELC.getNumeroCiclo());
                cursoComodinELC.setCreditosCumplidos(cursoComodinELC.getCreditosCumplidos() + alumnoCurso.getCreditos());
                if (cursoComodinELC.getCreditos() <= cursoComodinELC.getCreditosCumplidos()) {
                    cursoComodinELC.setNumeroCiclo(cursoComodinELC.getNumeroCiclo());
                    cursoComodinELC.setEstadoRegistro(EstadoEnum.INA.name());
                }
//                }
                if (creditosELC < 0) {
                    creditosELC = creditosELC + alumnoCurso.getCreditos();
                    break;
                }
            }
        }

        for (AlumnoCursoCurricula cursoComodinPRO : cursosComodinPRO) {

            for (AlumnoCursoCurricula alumnoCurso : cursosPRO) {
                if (idsAgregados.contains(alumnoCurso.getCurso().getId()) && alumnoCurso.getCicloAprobado() != null) {
                    continue;
                }
                idsAgregados.add(alumnoCurso.getCurso().getId());
                creditosPRO = creditosPRO - alumnoCurso.getCreditos();
                if (cursoComodinPRO.getCreditosCumplidos() == null) {
                    cursoComodinPRO.setCreditosCumplidos(0);
                }
                if (alumnoCurso.getCreditos() >= (cursoComodinPRO.getCreditos() - cursoComodinPRO.getCreditosCumplidos())) {
                    alumnoCurso.setNumeroCiclo(cursoComodinPRO.getNumeroCiclo());
                    cursoComodinPRO.setNumeroCiclo(cursoComodinPRO.getNumeroCiclo());
                    cursoComodinPRO.setCreditosCumplidos(cursoComodinPRO.getCreditosCumplidos() + alumnoCurso.getCreditos());
                    if (cursoComodinPRO.getCreditos() <= cursoComodinPRO.getCreditosCumplidos()) {
                        cursoComodinPRO.setEstadoRegistro(EstadoEnum.INA.name());
                    }
                    break;
                } else {
                    alumnoCurso.setNumeroCiclo(cursoComodinPRO.getNumeroCiclo());
                    cursoComodinPRO.setCreditosCumplidos(cursoComodinPRO.getCreditosCumplidos() + alumnoCurso.getCreditos());
                }
                if (creditosPRO < 0) {
                    creditosPRO = creditosPRO + alumnoCurso.getCreditos();
                    break;
                }
            }
        }
        for (AlumnoCursoCurricula cursoComodinCULT : cursosComodinCULT) {
            for (AlumnoCursoCurricula alumnoCurso : cursosCULT) {
                if (idsAgregados.contains(alumnoCurso.getCurso().getId()) && alumnoCurso.getCicloAprobado() != null) {
                    continue;
                }
                idsAgregados.add(alumnoCurso.getCurso().getId());
                creditosCULT = creditosCULT - alumnoCurso.getCreditos();
                if (cursoComodinCULT.getCreditosCumplidos() == null) {
                    cursoComodinCULT.setCreditosCumplidos(0);
                }
                if (alumnoCurso.getCreditos() >= (cursoComodinCULT.getCreditos() - cursoComodinCULT.getCreditosCumplidos())) {
                    alumnoCurso.setNumeroCiclo(cursoComodinCULT.getNumeroCiclo());
                    cursoComodinCULT.setNumeroCiclo(cursoComodinCULT.getNumeroCiclo());
                    cursoComodinCULT.setCreditosCumplidos(cursoComodinCULT.getCreditosCumplidos() + alumnoCurso.getCreditos());
                    if (cursoComodinCULT.getCreditos() <= cursoComodinCULT.getCreditosCumplidos()) {
                        cursoComodinCULT.setEstadoRegistro(EstadoEnum.INA.name());
                    }
                    break;
                } else {
                    alumnoCurso.setNumeroCiclo(cursoComodinCULT.getNumeroCiclo());
                    cursoComodinCULT.setCreditosCumplidos(cursoComodinCULT.getCreditosCumplidos() + alumnoCurso.getCreditos());
                }
                if (creditosCULT < 0) {
                    creditosCULT = creditosCULT + alumnoCurso.getCreditos();
                    break;
                }
            }
        }
        for (AlumnoCursoCurricula cursoComodinTECIND : cursosComodinTECIND) {
            for (AlumnoCursoCurricula alumnoCurso : cursosTECIND) {
                if (idsAgregados.contains(alumnoCurso.getCurso().getId()) && alumnoCurso.getCicloAprobado() != null) {
                    continue;
                }
                idsAgregados.add(alumnoCurso.getCurso().getId());
                creditosTECIND = creditosTECIND - alumnoCurso.getCreditos();
                if (cursoComodinTECIND.getCreditosCumplidos() == null) {
                    cursoComodinTECIND.setCreditosCumplidos(0);
                }
                if (alumnoCurso.getCreditos() >= (cursoComodinTECIND.getCreditos() - cursoComodinTECIND.getCreditosCumplidos())) {
                    alumnoCurso.setNumeroCiclo(cursoComodinTECIND.getNumeroCiclo());
                    cursoComodinTECIND.setNumeroCiclo(cursoComodinTECIND.getNumeroCiclo());
                    cursoComodinTECIND.setCreditosCumplidos(cursoComodinTECIND.getCreditosCumplidos() + alumnoCurso.getCreditos());
                    if (cursoComodinTECIND.getCreditos() <= cursoComodinTECIND.getCreditosCumplidos()) {
                        cursoComodinTECIND.setEstadoRegistro(EstadoEnum.INA.name());
                    }
                    break;
                } else {
                    alumnoCurso.setNumeroCiclo(cursoComodinTECIND.getNumeroCiclo());
                    cursoComodinTECIND.setCreditosCumplidos(cursoComodinTECIND.getCreditosCumplidos() + alumnoCurso.getCreditos());
                }
                if (creditosTECIND < 0) {
                    creditosTECIND = creditosTECIND + alumnoCurso.getCreditos();
                    break;
                }
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
            List<TipoCursoCurricula> tipoCursoCurriculas,
            List<ResumenPlanCurricular> resumenPlanCurriculars,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            List<CursoEquivalenteElectivo> equivalenteElectivos,
            Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcionalAll,
            List<PlanCurricular> planCurriculars,
            Map<Long, List<CursoCurricula>> mapCursoCurriculaAll,
            DataSessionPivot ds) {

        List<AlumnoCursoSimultaneo> cursosSimultaneosAlu = new ArrayList();
        Map<Long, AlumnoCursoCurricula> mapAlumCursoCurrByCursoCurri = new LinkedHashMap();
        Map<Long, AlumnoCursoCurricula> mapAlumCursoCurrByCurso = new LinkedHashMap();
        List<AlumnoCursoCurricula> alumnoCursoNew = new ArrayList<>();
        List<AlumnoCursoCurricula> alumnoCursoElcCarreraNew = new ArrayList<>();
        List<AlumnoCursoCurricula> alumnoCursoComodinDepNew = new ArrayList<>();
        cursosAprobados = cursosAprobados.stream().filter(x -> x.getEstadoEnum() != EstadoMatriculaEnum.RCI).collect(Collectors.toList());
        for (CursoCurricula cursocurricula : mapCursosCurricula.values()) {

            AlumnoCursoCurricula cursosNew = new AlumnoCursoCurricula();
            cursosNew.setAlumno(alumno);
            cursosNew.setTipoCursoCurricula(cursocurricula.getTipoCursoCurricula());
            cursosNew.setCurso(cursocurricula.getCurso());
            cursosNew.setCursoOpcional(null);
            cursosNew.setCursoCurricula(cursocurricula);

            if (tipoCursoELCEnums.contains(cursocurricula.getCurso().getCodigo())) {
                cursosNew.setEstadoEnum(PEND);
            } else {
                cursosNew.setEstadoEnum(NREQ);

            }
            cursosNew.setEstadoRegistro(EstadoEnum.ACT.name());
            cursosNew.setNumeroCiclo(cursocurricula.getNumeroCiclo());
            cursosNew.setValidado(false);
            cursosNew.setVecesCursado(0);
            cursosNew.setCreditos(cursocurricula.getCreditos());
            alumnoCursoNew.add(cursosNew);
        }

        for (AlumnoCursoCurricula cursoCurriObligatorio : alumnoCursoNew) {
            if (cursoCurriObligatorio.getCursoOpcional() == null) {
                cursoCurriObligatorio.setValidado(false);
                mapAlumCursoCurrByCursoCurri.put(cursoCurriObligatorio.getCursoCurricula().getId(), cursoCurriObligatorio);
                mapAlumCursoCurrByCurso.put(cursoCurriObligatorio.getCurso().getId(), cursoCurriObligatorio);
            }
        }

        cursoOpcionalCurriculas = cursoOpcionalCurriculas == null ? new ArrayList<>() : cursoOpcionalCurriculas;
        Map<Long, CursoOpcionalCurricula> mapCursoOpcional = TypesUtil.convertListToMap("curso.id", cursoOpcionalCurriculas);

        for (AlumnoCicloCurso cursosAprobado : cursosAprobados) {

            AlumnoCursoCurricula alumnoCursoCurricula = alumnoCursoNew.stream().filter(x -> Objects.equals(x.getCurso().getId(), cursosAprobado.getCurso().getId())).findAny().orElse(null);

            if (alumnoCursoCurricula != null) {
                cursosAprobado.setTipoCursoCurricula(alumnoCursoCurricula.getTipoCursoCurricula());

                if (cursosAprobado.getEstaAprobado() == 1) {
                    alumnoCursoCurricula.setCicloAprobado(cursosAprobado.getAlumnoCiclo().getCicloAcademico());
                    alumnoCursoCurricula.setNota(cursosAprobado.getNota());
                    if (cursosAprobado.getNota().equals("TE")) {
                        alumnoCursoCurricula.setEstadoEnum(CONV);
                    } else {
                        alumnoCursoCurricula.setEstadoEnum(APR);
                    }
                    alumnoCursoCurricula.setValidado(true);
                } else {
                    alumnoCursoCurricula.setEstadoEnum(HAB);
                }
                alumnoCursoCurricula.setCreditos(cursosAprobado.getCreditos());
                alumnoCursoCurricula.setEstadoRegistro(EstadoEnum.ACT.name());
                alumnoCursoCurricula.setVecesCursado(cursosAprobado.getVecesCursadoTransient());
                alumnoCursoCurricula.setNumeroCiclo(alumnoCursoCurricula.getNumeroCiclo());
            } else if (cursosAprobado.getCurso().getDepartamentoAcademico() != null && codesDptosCultDepMed.contains(cursosAprobado.getCurso().getDepartamentoAcademico().getCodigo())) {

                addCursosComodin(alumno, alumnoCursoComodinDepNew, cursosAprobado, ds);

            } else {
                addCursosLibresCurricula(alumno, cursosAprobado, alumnoCursoElcCarreraNew, tipoCursoCurriculas, planCurriculars, mapCursoOpcional, equivalenteElectivos, mapCursoOpcionalAll, mapCursoCurriculaAll);

            }
        }

        validarCursosComodin(alumnoCursoComodinDepNew, alumnoCursoNew, resumenPlanCurriculars, tipoCursoCurriculas.stream().filter(x -> x.getCodigoEnum() == DEP).findAny().orElse(null));
        validarCursosRequisito(mapAlumCursoCurrByCursoCurri, mapRequisitosCurricula, alumno);
        validarCursosSimultaneo(mapAlumCursoCurrByCursoCurri, cursosSimultaneosAlu, mapRequisitosCurricula, ds);
        validarEquivalencias(mapAlumCursoCurrByCursoCurri, mapEquivalentesCurricula, cursosAprobados);
        validarCursosMatriculados(mapAlumCursoCurrByCurso, cursosMatriculados, ds, alumno, alumnoCursoNew, equivalenteElectivos, cursoOpcionalCurriculas, tipoCursoCurriculas);
        generarAvanceCurricular(alumnoCursoElcCarreraNew, alumnoCursoNew, resumenPlanCurriculars, tipoCursoCurriculas, alumnoAvanceCurriculars, alumno, mapCursosVecesLlevado);
        validarCursosELC(alumnoCursoElcCarreraNew, alumnoCursoNew, alumno);

        alumnoCursoOld = alumnoCursoOld == null ? new ArrayList<>() : alumnoCursoOld;
        for (AlumnoCursoCurricula alumnoCursoCurriculaNew : alumnoCursoNew) {
//            if (alumnoCursoCurriculaNew.getCursoCurricula() != null
//                    && !estadosAprobados.contains(alumnoCursoCurriculaNew.getEstadoEnum())) {
//                alumnoCursoCurriculaNew.setEstadoEnum(NREQ);
//            }
            AlumnoCursoCurricula cursoCurricula = alumnoCursoOld.stream().filter(x -> Objects.equals(x.getCurso().getId(), alumnoCursoCurriculaNew.getCurso().getId()) && !x.isValidado()).findAny().orElse(null);
            if (cursoCurricula != null) {
                cursoCurricula.setValidado(true);
                alumnoCursoCurriculaNew.setId(cursoCurricula.getId());
                alumnoCursoCurriculaDAO.update(alumnoCursoCurriculaNew);
            } else {

                alumnoCursoCurriculaDAO.save(alumnoCursoCurriculaNew);
            }
        }
        for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoOld) {
            if (!alumnoCursoCurricula.isValidado()) {
                alumnoCursoCurricula.setEstadoRegistro(INA.name());
                alumnoCursoCurriculaDAO.update(alumnoCursoCurricula);
            }
        }
        for (AlumnoCursoSimultaneo alumnoCursoSimultaneo : cursosSimultaneosAlu) {
            alumnoCursoSimultaneoDAO.save(alumnoCursoSimultaneo);
        }
        for (AlumnoCicloCurso cursosAprobado : cursosAprobados) {
            if (cursosAprobado.getTipoCursoCurricula() != null && cursosAprobado.getTipoCursoCurricula().getCodigoEnum() == EEP) {
                cursosAprobado.setTipoCursoCurricula(null);
            }
            alumnoCicloCursoDAO.updateCurso(cursosAprobado);
        }

    }

    private void validarEquivalencias(
            Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAlu,
            Map<Long, List<CursoEquivalente>> mapEquivalentes,
            List<AlumnoCicloCurso> cursosAprobados) {
        List<AlumnoCicloCurso> cursosApr = cursosAprobados.stream().filter(x -> x.isAprobado()).collect(Collectors.toList());
        Map<Long, AlumnoCicloCurso> mapCursosAprobados = TypesUtil.convertListToMap("curso.id", cursosApr);

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

    private void sincronizarCursosAgregados(
            Map<Long, CursoCurricula> mapCursosCurricula,
            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAluByCurso,
            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAlu,
            Alumno alumno) {

        for (Map.Entry<Long, CursoCurricula> entry : mapCursosCurricula.entrySet()) {
            Long key = entry.getKey();
            CursoCurricula cursoCurri = entry.getValue();
            Curso curso = cursoCurri.getCurso();

            String codeDptoCurso = (String) ObjectUtil.getParentTree(curso, "departamentoAcademico.codigo");
            boolean esCursoDeporte = false;
            if (codesDptosCultDepMed.contains(codeDptoCurso)) {
                esCursoDeporte = true;
                continue;
            }

            if (!mapCursosCurriculaAlu.containsKey(key) && !esCursoDeporte) {
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
            Map<Long, CursoOpcionalCurricula> mapCursoOpcional,
            List<CursoEquivalenteElectivo> equivalenteElectivos,
            Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcionalAll,
            Map<Long, List<CursoCurricula>> mapCursoCurriculaAll
    ) {

        CursoOpcionalCurricula cursoOpcionalCurricula = mapCursoOpcional.get(cursosAprobado.getCurso().getId());
        if (cursoOpcionalCurricula != null) {

            TipoCursoCurricula tipoCursoCurricula = cursoOpcionalCurricula.getTipoCursoCurricula();
            addAlumnoCursoCurricula(alumno, cursosAprobado, cursoOpcionalCurricula, null, alumnoCursoElecCarreraNew, tipoCursoCurricula);
        } else {
            equivalenteElectivos = equivalenteElectivos == null ? new ArrayList() : equivalenteElectivos;

            CursoEquivalenteElectivo cursoEquivalenteElectivo = equivalenteElectivos.stream().filter(x -> Objects.equals(x.getCursoEquivalente().getId(), cursosAprobado.getCurso().getId())).findAny().orElse(null);
            if (cursoEquivalenteElectivo == null) {
                for (PlanCurricular planCurricular : planCurriculars) {
                    if (Objects.equals(planCurricular.getId(), alumno.getPlanCurricular().getId())) {
                        continue;
                    }
                    List<CursoCurricula> cursoCurriculas = mapCursoCurriculaAll.get(planCurricular.getId());
                    cursoCurriculas = cursoCurriculas == null ? new ArrayList() : cursoCurriculas;
                    CursoCurricula cursoCurricula = cursoCurriculas.stream().filter(x -> Objects.equals(x.getCurso().getId(), cursosAprobado.getCurso().getId())).findAny().orElse(null);
                    if (cursoCurricula != null) {
                        TipoCursoCurricula tipoCursoCurricula = tipoCursoCurriculas.stream().filter(x -> x.getCodigoEnum() == ELE).findAny().orElse(null);
                        addAlumnoCursoCurricula(alumno, cursosAprobado, null, cursoCurricula, alumnoCursoElecCarreraNew, tipoCursoCurricula);
                        break;
                    } else {
                        List<CursoOpcionalCurricula> curriculasOpcional = mapCursoOpcionalAll.get(planCurricular.getId());
                        curriculasOpcional = curriculasOpcional == null ? new ArrayList() : curriculasOpcional;
                        CursoOpcionalCurricula curricula = curriculasOpcional.stream().filter((CursoOpcionalCurricula x) -> Objects.equals(x.getCurso().getId(), cursosAprobado.getCurso().getId())).findAny().orElse(null);
                        if (curricula != null) {
                            TipoCursoCurricula tipoCursoCurricula = tipoCursoCurriculas.stream().filter(x -> x.getCodigoEnum() == ELE).findAny().orElse(null);
                            addAlumnoCursoCurricula(alumno, cursosAprobado, curricula, null, alumnoCursoElecCarreraNew, tipoCursoCurricula);
                            break;
                        }
                    }
                }
            } else {

                cursosAprobado.setCursoEquivalente(cursoEquivalenteElectivo.getCursoOpcionalCurricula().getCurso());
                cursosAprobado.setEsEquivalente(Boolean.TRUE);
                TipoCursoCurricula tipoCursoCurricula = cursoEquivalenteElectivo.getCursoOpcionalCurricula().getTipoCursoCurricula();
                addAlumnoCursoCurricula(alumno, cursosAprobado, cursoEquivalenteElectivo.getCursoOpcionalCurricula(), null, alumnoCursoElecCarreraNew, tipoCursoCurricula);
            }
        }
    }

    private void addAlumnoCursoCurricula(Alumno alumno, AlumnoCicloCurso alumnoCicloCurso, CursoOpcionalCurricula opcionalCurricula, CursoCurricula cursoCurricula,
            List<AlumnoCursoCurricula> alumnoCursoElecCarreraNew, TipoCursoCurricula tipoCursoCurricula) {

        AlumnoCursoCurricula cursosOpcionalesNew = new AlumnoCursoCurricula();
        if (alumnoCicloCurso.getEstaAprobado() == 1) {
            if (alumnoCicloCurso.getNota().equals("TE")) {
                cursosOpcionalesNew.setEstadoEnum(CONV);
            } else if (alumnoCicloCurso.getCursoEquivalente() != null) {
                cursosOpcionalesNew.setEstadoEnum(EQUIV);
            } else {
                cursosOpcionalesNew.setEstadoEnum(APR);

            }
            cursosOpcionalesNew.setCicloAprobado(alumnoCicloCurso.getAlumnoCiclo().getCicloAcademico());
            cursosOpcionalesNew.setNota(alumnoCicloCurso.getNota());
        } else {
            cursosOpcionalesNew.setEstadoEnum(HAB);
            if (Arrays.asList(ELE, ELC, PROD, CULT, TECIND).contains(tipoCursoCurricula.getCodigoEnum())) {
                cursosOpcionalesNew.setNumeroCiclo(10);
            }
        }
        cursosOpcionalesNew.setAlumno(alumno);
        cursosOpcionalesNew.setCreditos(alumnoCicloCurso.getCreditos());
        cursosOpcionalesNew.setTipoCursoCurricula(tipoCursoCurricula);
        if (alumnoCicloCurso.getCursoEquivalente() != null) {
            cursosOpcionalesNew.setCurso(alumnoCicloCurso.getCursoEquivalente());
        } else {
            cursosOpcionalesNew.setCurso(alumnoCicloCurso.getCurso());
        }
        if (tipoCursoCurricula.getCodigoEnum() != ELE) {
            cursosOpcionalesNew.setCursoOpcional(opcionalCurricula);
            cursosOpcionalesNew.setCursoCurricula(cursoCurricula);
        }
        cursosOpcionalesNew.setValidado(true);
        cursosOpcionalesNew.setVecesCursado(alumnoCicloCurso.getVecesCursadoTransient());
        alumnoCursoElecCarreraNew.add(cursosOpcionalesNew);

        alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurricula);
    }

    private void addCursosComodin(
            Alumno alumno,
            List<AlumnoCursoCurricula> alumnoCursoComodinNew,
            AlumnoCicloCurso aprobado,
            DataSessionPivot ds) {

        if (aprobado.getCreditos() > 0) {

            AlumnoCursoCurricula cursoComodin = new AlumnoCursoCurricula();
            cursoComodin.setAlumno(alumno);
            cursoComodin.setCicloAprobado(aprobado.getAlumnoCiclo().getCicloAcademico());
            cursoComodin.setCreditos(aprobado.getCreditos());
            cursoComodin.setCurso(aprobado.getCurso());
            cursoComodin.setCursoCurricula(null);
            cursoComodin.setCursoOpcional(null);
            if (aprobado.getNota().equals("TE")) {
                cursoComodin.setEstadoEnum(CONV);
            } else {
                cursoComodin.setEstadoEnum(APR);
            }
            cursoComodin.setEstadoRegistro(EstadoEnum.ACT.name());
            cursoComodin.setNota(aprobado.getNota());
            cursoComodin.setValidado(true);
            cursoComodin.setVecesCursado(aprobado.getVecesCursadoTransient());
            alumnoCursoComodinNew.add(cursoComodin);
        }
    }

    private AlumnoCicloCurso getCursoEquivaleByDptoAcad(
            List<String> departamentos,
            Map<String, List<AlumnoCicloCurso>> mapCursosAprobadosByDpto,
            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAlyByCurso) {

        List<AlumnoCicloCurso> cursosAlumno = new ArrayList();
        for (String departamento : departamentos) {
            List<AlumnoCicloCurso> cursosAlumnoDpto = TypesUtil.getListNotNull(mapCursosAprobadosByDpto.get(departamento));
            cursosAlumno.addAll(cursosAlumnoDpto);
        }

        if (cursosAlumno.isEmpty()) {
            return null;
        }

        Collections.sort(cursosAlumno, new AlumnoCicloCurso.CompareCiclo());
        for (AlumnoCicloCurso cursoAlumno : cursosAlumno) {
            if (cursoAlumno.getCreditos() == 0) {
                continue;
            }
            return cursoAlumno;
        }
        return null;
    }

    private void validarCursosRequisito(
            Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAlu,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitos,
            Alumno alumno) {

        for (Map.Entry<Long, AlumnoCursoCurricula> entry : mapCursoCurriculaAlu.entrySet()) {
            AlumnoCursoCurricula evaluado = entry.getValue();

            if (evaluado.isValidado() || estadosAprobados.contains(evaluado.getEstadoEnum())) {
                continue;
            }

            List<RequisitoCursoCurricula> requisitos = mapRequisitos.get(evaluado.getCursoCurricula().getId());
            if (requisitos == null || requisitos.isEmpty() || cumpleRequisitos(requisitos, mapCursoCurriculaAlu, evaluado, alumno)) {
                if (!tipoCursoELCEnums.contains(evaluado.getCurso().getCodigo())
                        && evaluado.getCursoCurricula().getCreditosRequisito() <= alumno.getCreditosAprobados()) {
                    evaluado.setEstadoEnum(HAB);
                }
            } else {
                if (tipoCursoELCEnums.contains(evaluado.getCurso().getCodigo())) {
                    evaluado.setEstadoEnum(PEND);
                } else {
                    evaluado.setEstadoEnum(NREQ);
                }
                evaluado.setValidado(true);
            }

        }

    }

    private boolean cumpleRequisitos(
            List<RequisitoCursoCurricula> requisitos,
            Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAlu,
            AlumnoCursoCurricula evaluado, Alumno alumno) {

//        List<RequisitoCursoCurricula> requisitosNoSimultaneos = requisitos.stream().filter(x -> x.getSimultaneo() != 1).collect(Collectors.toList());
//        if (requisitosNoSimultaneos.isEmpty()) {
//            return true;
//        }
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

            if (Arrays.asList(HAB, MAT).contains(evaluado.getEstadoEnum())) {
                continue;
            }

            List<RequisitoCursoCurricula> requisitos = mapRequisitos.get(evaluado.getCursoCurricula().getId());
            List<AlumnoCursoSimultaneo> requisitosSimultaneo = new ArrayList();

            if (requisitos == null || validarSimultaneos(requisitosSimultaneo, requisitos, mapCursosCurriculaAlu, evaluado, ds)) {
                if (requisitosSimultaneo.size() > 0) {
                    evaluado.setEstadoEnum(SIM);
                    evaluado.setEsSimultaneo(Boolean.TRUE);
                    cursosSimultaneoAlu.addAll(requisitosSimultaneo);
                } else {
                    evaluado.setEstadoEnum(evaluado.getEstadoEnum());
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
            } else if (Arrays.asList(HAB, MAT).contains(cursoRequisito.getEstadoEnum())) {
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
            List<MatriculaCurso> cursosMatriculados, DataSessionPivot ds,
            Alumno alumno,
            List<AlumnoCursoCurricula> alumnoCursoNew,
            List<CursoEquivalenteElectivo> equivalenteElectivos,
            List<CursoOpcionalCurricula> cursoOpcionalCurriculas,
            List<TipoCursoCurricula> tipoCursoCurriculas) {
        cursosMatriculados = cursosMatriculados == null ? new ArrayList<>() : cursosMatriculados;
        for (MatriculaCurso cursoMatriculado : cursosMatriculados) {
            if (cursoMatriculado.isEstadoMAT()) {
                AlumnoCursoCurricula cursoCurriAlu = mapCursoCurriculaAluByCurso.get(cursoMatriculado.getCurso().getId());
                if (cursoCurriAlu != null) {
                    cursoCurriAlu.setEstadoMatriculaEnum(cursoMatriculado.getEstadoEnum());
                } else {
                    TipoCursoCurricula tipoCursoCurricula = null;
                    AlumnoCursoCurricula cursosOpcionalesNew = new AlumnoCursoCurricula();
                    if (alumno.getModalidadEstudio().isPostgrado()) {
                        tipoCursoCurricula = tipoCursoCurriculas.stream().filter(x -> x.getCodigoEnum() == EAD).findAny().orElse(null);
                        cursosOpcionalesNew.setNumeroCiclo(4);
                    } else {
                        tipoCursoCurricula = tipoCursoCurriculas.stream().filter(x -> x.getCodigoEnum() == ELE).findAny().orElse(null);
                        cursosOpcionalesNew.setNumeroCiclo(10);
                    }
                    cursoOpcionalCurriculas = cursoOpcionalCurriculas == null ? new ArrayList<>() : cursoOpcionalCurriculas;
                    Map<Long, CursoOpcionalCurricula> mapCursoOpcional = TypesUtil.convertListToMap("curso.id", cursoOpcionalCurriculas);

                    CursoOpcionalCurricula cursoOpcionalCurricula = mapCursoOpcional.get(cursoMatriculado.getCurso().getId());
                    if (cursoOpcionalCurricula == null) {
                        equivalenteElectivos = equivalenteElectivos == null ? new ArrayList() : equivalenteElectivos;

                        CursoEquivalenteElectivo cursoEquivalenteElectivo = equivalenteElectivos.stream().filter(x -> Objects.equals(x.getCursoEquivalente().getId(), cursoMatriculado.getCurso().getId())).findAny().orElse(null);
                        if (cursoEquivalenteElectivo != null) {
                            tipoCursoCurricula = cursoEquivalenteElectivo.getCursoOpcionalCurricula().getTipoCursoCurricula();
                        }
                    } else {
                        tipoCursoCurricula = cursoOpcionalCurricula.getTipoCursoCurricula();
                    }

                    cursosOpcionalesNew.setAlumno(alumno);
                    cursosOpcionalesNew.setTipoCursoCurricula(tipoCursoCurricula);
                    cursosOpcionalesNew.setCurso(cursoMatriculado.getCurso());
                    cursosOpcionalesNew.setCursoOpcional(cursoOpcionalCurricula);
                    cursosOpcionalesNew.setEstadoMatriculaEnum(cursoMatriculado.getEstadoEnum());
                    cursosOpcionalesNew.setEstadoRegistro(EstadoEnum.ACT.name());
                    cursosOpcionalesNew.setEstadoEnum(HAB);
                    cursosOpcionalesNew.setValidado(true);
                    cursosOpcionalesNew.setVecesCursado(0);
                    cursosOpcionalesNew.setCreditos(cursoMatriculado.getCreditos());
                    alumnoCursoNew.add(cursosOpcionalesNew);

                    cursoMatriculado.setTipoCursoCurricula(tipoCursoCurricula);
                }
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void settingPlanCurricular(Alumno alumno, PlanCurricular planBD) {
        alumno.setPlanCurricular(planBD);
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
            List<TipoCursoCurricula> tipoCursoCurriculas,
            List<ResumenPlanCurricular> resumenPlanCurriculars,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            List<CursoEquivalenteElectivo> equivalenteElectivos,
            Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcionalAll,
            List<PlanCurricular> planCurriculars,
            Map<Long, List<CursoCurricula>> mapCursoCurriculaAll,
            List<CursoHabilEscuela> habilEscuelas,
            DataSessionPivot ds) {
//  Map<Long, List<CursoEquivalente>> mapCursosEquivalentes, List<CursoEquivalenteElectivo> equivalenteElectivos,
        Carrera carrera = alumno.getCarrera();
        this.settingPlanCurricular(alumno, planBD);

        this.deleteAllAlumnoCursoSimultaneoByAlumno(alumno);
        if (alumno.getModalidadEstudio().isPregrado()) {

            this.procesarAlumno(
                    alumno,
                    mapCursoCurricula,
                    mapRequisitoCursoCurricula,
                    mapCursosEquivalentes,
                    mapCursosVecesLlevado,
                    cursosMatriculados,
                    cursosAprobados,
                    alumnoCursoCurricula,
                    opcionalCurriculas,
                    mapCursoCurriculaByCurso,
                    tipoCursoCurriculas,
                    resumenPlanCurriculars,
                    alumnoAvanceCurriculars,
                    equivalenteElectivos,
                    mapCursoOpcionalAll,
                    planCurriculars,
                    mapCursoCurriculaAll,
                    ds);
        } else {

            this.procesarAlumnoSincronoPros(alumno,
                    mapCursoCurricula, mapCursosVecesLlevado,
                    cursosMatriculados, cursosAprobados,
                    mapCursoCurriculaByCurso, opcionalCurriculas,
                    resumenPlanCurriculars, tipoCursoCurriculas,
                    alumnoAvanceCurriculars, alumnoCursoCurricula,
                    habilEscuelas,
                    ds);
        }
        visorAsignaCurricula.incrementar(carrera);
    }

    private List fillList(List lista) {
        if (lista == null) {
            return new ArrayList();
        }
        return lista;
    }

    @Override
    @Transactional
    public void procesarAlumnoSincronoPros(Alumno alumno,
            Map<Long, CursoCurricula> mapCursosCurricula, Map<String, AlumnoCicloCurso> mapCursosVecesLlevado,
            List<MatriculaCurso> cursosMatriculados, List<AlumnoCicloCurso> cursosAprobados,
            Map<Long, CursoCurricula> mapCursoCurriculaByCurso, List<CursoOpcionalCurricula> cursoOpcionaPlan,
            List<ResumenPlanCurricular> resumenPlanCurriculars, List<TipoCursoCurricula> tipoCursoCurriculas,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars, List<AlumnoCursoCurricula> alumnoCursoOld,
            List<CursoHabilEscuela> habilEscuelas,
            DataSessionPivot ds) {
        Map<Long, CursoHabilEscuela> mapCursoHabil = TypesUtil.convertListToMap("curso.id", fillList(habilEscuelas));
        Map<Long, CursoOpcionalCurricula> mapCursosOpcional = TypesUtil.convertListToMap("curso.id", fillList(cursoOpcionaPlan));
        Map<String, TipoCursoCurricula> mapTipoCursoCurricula = TypesUtil.convertListToMap("codigo", fillList(tipoCursoCurriculas));
        Map<Long, AlumnoCursoCurricula> mapAlumCursoCurrByCurso = new LinkedHashMap();
        List<AlumnoCursoCurricula> alumnoCursoNew = new ArrayList<>();
        List<AlumnoCursoCurricula> alumnoCursoElcCarreraNew = new ArrayList<>();
        for (CursoCurricula cursocurricula : mapCursosCurricula.values()) {

            AlumnoCursoCurricula cursosNew = new AlumnoCursoCurricula();
            cursosNew.setAlumno(alumno);
            cursosNew.setTipoCursoCurricula(cursocurricula.getTipoCursoCurricula());
            cursosNew.setCurso(cursocurricula.getCurso());
            cursosNew.setCursoOpcional(null);
            cursosNew.setCursoCurricula(cursocurricula);

//            if (tipoCursoELCEnums.contains(cursocurricula.getCurso().getCodigo())) {s
            cursosNew.setEstadoEnum(HAB);
//            } else {
//                cursosNew.setEstadoEnum(NREQ);
//
//            }
            cursosNew.setEstadoRegistro(EstadoEnum.ACT.name());
            cursosNew.setNumeroCiclo(cursocurricula.getNumeroCiclo());
            cursosNew.setValidado(false);
            cursosNew.setVecesCursado(0);
            cursosNew.setCreditos(cursocurricula.getCreditos());
            alumnoCursoNew.add(cursosNew);
        }
        for (AlumnoCursoCurricula cursoCurriObligatorio : alumnoCursoNew) {
            if (cursoCurriObligatorio.getCursoOpcional() == null) {
                cursoCurriObligatorio.setValidado(false);
                mapAlumCursoCurrByCurso.put(cursoCurriObligatorio.getCurso().getId(), cursoCurriObligatorio);
            }
        }
        Map<Long, AlumnoCursoCurricula> mapAlumnoCursoNew = TypesUtil.convertListToMap("curso.id", alumnoCursoNew);
        for (AlumnoCicloCurso cursosAprobado : cursosAprobados) {

            AlumnoCursoCurricula alumnoCursoCurricula = mapAlumnoCursoNew.get(cursosAprobado.getCurso().getId());

            if (alumnoCursoCurricula != null) {
                addCurso(alumnoCursoCurricula, cursosAprobado);
            } else {
                CursoHabilEscuela cursoHabilEscuela = mapCursoHabil.get(cursosAprobado.getCurso().getId());
                if (cursoHabilEscuela != null && mapCursosOpcional.get(cursosAprobado.getCurso().getId()) == null) {
                    alumnoCursoCurricula = new AlumnoCursoCurricula();
                    alumnoCursoCurricula.setTipoCursoCurricula(mapTipoCursoCurricula.get(EAD.name()));
                    alumnoCursoCurricula.setNumeroCiclo(4);
                    alumnoCursoCurricula.setAlumno(alumno);
                    cursosAprobado.setTipoCursoCurricula(alumnoCursoCurricula.getTipoCursoCurricula());
                    addCurso(alumnoCursoCurricula, cursosAprobado);
                    alumnoCursoNew.add(alumnoCursoCurricula);
                    cursoHabilEscuela.setAgregado(Boolean.TRUE);
                } else {
                    addCursosLibresCurriculaEpg(alumno, cursosAprobado, alumnoCursoElcCarreraNew, mapCursosOpcional, mapTipoCursoCurricula, mapCursoHabil);
                }
            }
        }
        for (CursoHabilEscuela habilEscuela : mapCursoHabil.values()) {
            if (!habilEscuela.getAgregado() && mapAlumnoCursoNew.get(habilEscuela.getCurso().getId()) == null) {
                AlumnoCursoCurricula alumnoCursoCurricula = new AlumnoCursoCurricula();
                alumnoCursoCurricula.setTipoCursoCurricula(mapTipoCursoCurricula.get(EAD.name()));
                alumnoCursoCurricula.setNumeroCiclo(4);
                alumnoCursoCurricula.setEstadoEnum(HAB);
                alumnoCursoCurricula.setEstadoRegistro(EstadoEnum.ACT.name());
                alumnoCursoCurricula.setVecesCursado(0);
                alumnoCursoCurricula.setCurso(habilEscuela.getCurso());
                alumnoCursoCurricula.setCreditos(habilEscuela.getCurso().getCreditos());
                alumnoCursoCurricula.setAlumno(alumno);
                alumnoCursoNew.add(alumnoCursoCurricula);
                habilEscuela.setAgregado(Boolean.TRUE);
            }

        }
        validarCursosMatriculados(mapAlumCursoCurrByCurso, cursosMatriculados, ds, alumno, alumnoCursoNew, null, cursoOpcionaPlan, tipoCursoCurriculas);
        generarAvanceCurricularEpg(alumnoCursoElcCarreraNew, alumnoCursoNew, resumenPlanCurriculars, tipoCursoCurriculas, alumnoAvanceCurriculars, alumno, mapCursosVecesLlevado);

        alumnoCursoOld = alumnoCursoOld == null ? new ArrayList<>() : alumnoCursoOld;
        for (AlumnoCursoCurricula alumnoCursoCurriculaNew : alumnoCursoNew) {
//            if (alumnoCursoCurriculaNew.getCursoCurricula() != null
//                    && !estadosAprobados.contains(alumnoCursoCurriculaNew.getEstadoEnum())) {
//                alumnoCursoCurriculaNew.setEstadoEnum(NREQ);
//            }
            AlumnoCursoCurricula cursoCurricula = alumnoCursoOld.stream().filter(x -> Objects.equals(x.getCurso().getId(), alumnoCursoCurriculaNew.getCurso().getId()) && !x.isValidado()).findAny().orElse(null);
            if (cursoCurricula != null) {
                cursoCurricula.setValidado(true);
                alumnoCursoCurriculaNew.setId(cursoCurricula.getId());
                alumnoCursoCurriculaDAO.update(alumnoCursoCurriculaNew);
            } else {

                alumnoCursoCurriculaDAO.save(alumnoCursoCurriculaNew);
            }
        }
        for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoOld) {
            if (!alumnoCursoCurricula.isValidado()) {
                alumnoCursoCurricula.setEstadoRegistro(INA.name());
                alumnoCursoCurriculaDAO.update(alumnoCursoCurricula);
            }
        }

        for (AlumnoCicloCurso cursosAprobado : cursosAprobados) {
            if (cursosAprobado.getTipoCursoCurricula() != null && cursosAprobado.getTipoCursoCurricula().getCodigoEnum() == EAD) {
                cursosAprobado.setTipoCursoCurricula(null);
            }
            alumnoCicloCursoDAO.updateCurso(cursosAprobado);
        }

    }

    private void addCurso(AlumnoCursoCurricula alumnoCursoCurricula, AlumnoCicloCurso cursosAprobado) {
        cursosAprobado.setTipoCursoCurricula(alumnoCursoCurricula.getTipoCursoCurricula());

        if (cursosAprobado.getEstaAprobado() == 1) {
            alumnoCursoCurricula.setCicloAprobado(cursosAprobado.getAlumnoCiclo().getCicloAcademico());
            alumnoCursoCurricula.setNota(cursosAprobado.getNota());
            if (cursosAprobado.getNota().equals("TE")) {
                alumnoCursoCurricula.setEstadoEnum(CONV);
            } else {
                alumnoCursoCurricula.setEstadoEnum(APR);
            }
            alumnoCursoCurricula.setValidado(true);
        } else {
            alumnoCursoCurricula.setEstadoEnum(HAB);
        }
        alumnoCursoCurricula.setCurso(cursosAprobado.getCurso());
        alumnoCursoCurricula.setCreditos(cursosAprobado.getCreditos());
        alumnoCursoCurricula.setEstadoRegistro(EstadoEnum.ACT.name());
        alumnoCursoCurricula.setVecesCursado(cursosAprobado.getVecesCursadoTransient());
        alumnoCursoCurricula.setNumeroCiclo(alumnoCursoCurricula.getNumeroCiclo());
    }

    private void addCursosLibresCurriculaEpg(Alumno alumno, AlumnoCicloCurso cursosAprobado,
            List<AlumnoCursoCurricula> alumnoCursoElcCarreraNew, Map<Long, CursoOpcionalCurricula> mapCursosOpcional,
            Map<String, TipoCursoCurricula> mapTipoCursoCurricula, Map<Long, CursoHabilEscuela> mapCursoHabil) {

        CursoOpcionalCurricula cursoOpcionalCurricula = mapCursosOpcional.get(cursosAprobado.getId());
        if (cursoOpcionalCurricula != null) {
            TipoCursoCurricula tipoCursoCurricula = cursoOpcionalCurricula.getTipoCursoCurricula();
            addAlumnoCursoCurriculaEpg(alumno, cursosAprobado, cursoOpcionalCurricula, null, alumnoCursoElcCarreraNew, tipoCursoCurricula);
        } else {
            TipoCursoCurricula tipoCursoCurricula = mapTipoCursoCurricula.get(EAD.name());

            CursoHabilEscuela cursoHabilEscuela = mapCursoHabil.get(cursosAprobado.getCurso().getId());
            if (cursoHabilEscuela != null) {
                AlumnoCursoCurricula alumnoCursoCurricula = new AlumnoCursoCurricula();
                alumnoCursoCurricula.setTipoCursoCurricula(tipoCursoCurricula);
                alumnoCursoCurricula.setNumeroCiclo(4);
                alumnoCursoCurricula.setAlumno(alumno);
                cursosAprobado.setTipoCursoCurricula(alumnoCursoCurricula.getTipoCursoCurricula());
                addCurso(alumnoCursoCurricula, cursosAprobado);
                alumnoCursoElcCarreraNew.add(alumnoCursoCurricula);
                cursoHabilEscuela.setAgregado(Boolean.TRUE);
            }
            logger.debug("Alumno {}. Curso {} ", alumno.getCodigo(), cursosAprobado.getCurso().getCodigo());
//            NO SE AGREGA POR QUE NO SE LE ENCUENTRA
//            else {
//                tipoCursoCurricula = null;
//                addAlumnoCursoCurriculaEpg(alumno, cursosAprobado, null, null, alumnoCursoElcCarreraNew, tipoCursoCurricula);
//            }
        }
    }

    private void addAlumnoCursoCurriculaEpg(Alumno alumno, AlumnoCicloCurso alumnoCicloCurso, CursoOpcionalCurricula opcionalCurricula, CursoCurricula cursoCurricula,
            List<AlumnoCursoCurricula> alumnoCursoElecCarreraNew, TipoCursoCurricula tipoCursoCurricula) {

        AlumnoCursoCurricula cursosOpcionalesNew = new AlumnoCursoCurricula();
        if (alumnoCicloCurso.getEstaAprobado() == 1) {
            if (alumnoCicloCurso.getNota().equals("TE")) {
                cursosOpcionalesNew.setEstadoEnum(CONV);
            } else if (alumnoCicloCurso.getCursoEquivalente() != null) {
                cursosOpcionalesNew.setEstadoEnum(EQUIV);
            } else {
                cursosOpcionalesNew.setEstadoEnum(APR);
            }
            cursosOpcionalesNew.setCicloAprobado(alumnoCicloCurso.getAlumnoCiclo().getCicloAcademico());
            cursosOpcionalesNew.setNota(alumnoCicloCurso.getNota());
        } else {
            cursosOpcionalesNew.setEstadoEnum(HAB);
        }
        if (tipoCursoCurricula.getCodigoEnum() == EAD) {
            cursosOpcionalesNew.setNumeroCiclo(4);
        }
        cursosOpcionalesNew.setAlumno(alumno);
        cursosOpcionalesNew.setCreditos(alumnoCicloCurso.getCreditos());
        cursosOpcionalesNew.setTipoCursoCurricula(tipoCursoCurricula);
        cursosOpcionalesNew.setCurso(alumnoCicloCurso.getCurso());

        if (tipoCursoCurricula.getCodigoEnum() != EAD) {
            cursosOpcionalesNew.setCursoOpcional(opcionalCurricula);
            cursosOpcionalesNew.setCursoCurricula(cursoCurricula);
        }
        cursosOpcionalesNew.setValidado(true);
        cursosOpcionalesNew.setVecesCursado(alumnoCicloCurso.getVecesCursadoTransient());
        alumnoCursoElecCarreraNew.add(cursosOpcionalesNew);

        alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurricula);
    }

    private void generarAvanceCurricularEpg(List<AlumnoCursoCurricula> alumnoCursoElcCarreraNew, List<AlumnoCursoCurricula> alumnoCursoNew, List<ResumenPlanCurricular> resumenPlanCurriculars, List<TipoCursoCurricula> tipoCursoCurriculas, List<AlumnoAvanceCurricular> alumnoAvanceCurriculars, Alumno alumno, Map<String, AlumnoCicloCurso> mapCursosVecesLlevado) {
        Map<TipoCursoCurriculaEnum, TipoCursoCurricula> tipos = tipoCursoCurriculas
                .stream()
                .filter(x -> x.getCodigo() != null)
                .collect(Collectors.toMap(x -> x.getCodigoEnum(), x -> x, (a, b) -> a));

        Map<TipoCursoCurriculaEnum, AlumnoAvanceCurricular> avances = alumnoAvanceCurriculars
                .stream()
                .filter(x -> x.getTipoCursoCurricula() != null)
                .collect(Collectors.toMap(x -> x.getTipoCursoCurricula().getCodigoEnum(), x -> x, (a, b) -> a));

        Map<TipoCursoCurriculaEnum, Integer> creditos = new HashMap();
        Map<TipoCursoCurriculaEnum, Integer> cursos = new HashMap();
        Map<TipoCursoCurriculaEnum, Boolean> excepción = new HashMap();

        for (TipoCursoCurricula tipo : tipos.values()) {
            creditos.put(tipo.getCodigoEnum(), 0);
            cursos.put(tipo.getCodigoEnum(), 0);
            excepción.put(tipo.getCodigoEnum(), Boolean.FALSE);
        }
        alumnoCursoNew.addAll(alumnoCursoElcCarreraNew);
        Collections.sort(alumnoCursoNew, new AlumnoCursoCurricula.CompareCodigo());
        Collections.sort(alumnoCursoNew, new AlumnoCursoCurricula.CompareCreditos());

        for (AlumnoCursoCurricula curso : alumnoCursoNew) {

            if (curso.getVecesCursado() == 0) {
                AlumnoCicloCurso cat = mapCursosVecesLlevado.get(curso.getAlumno().getId() + "-" + curso.getCurso().getId());
                if (cat != null) {
                    curso.setVecesCursado(cat.getVecesCursadoTransient());
                }
            }
            if (Arrays.asList(APR, EQUIV, CONV).contains(curso.getEstadoEnum())) {
                TipoCursoCurriculaEnum tipo = curso.getTipoCursoCurricula().getCodigoEnum();

                ResumenPlanCurricular rpc = resumenPlanCurriculars.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() == curso.getTipoCursoCurricula().getCodigoEnum()).findAny().orElse(new ResumenPlanCurricular());
                if (rpc == null) {
                    continue;
                }
                Integer prevCreditos = creditos.get(tipo);
                prevCreditos += curso.getCreditos();

                Integer prevCursos = cursos.get(tipo);
                prevCursos++;

                creditos.replace(tipo, prevCreditos);
                cursos.replace(tipo, prevCursos);
            }
        }
        Integer cred = 0;
        Integer credCarrera = 0;
        Integer cur = 0;
        Integer curCarrera = 0;
        for (TipoCursoCurricula tipo : tipos.values()) {
            AlumnoAvanceCurricular avance = avances.get(tipo.getCodigoEnum());
            if (avance == null) {
                avance = new AlumnoAvanceCurricular();
                avance.setTipoCursoCurricula(tipo);
                avance.setAlumno(alumno);
            }
            Integer creditosDep = 0;
            Integer cursosDep = 0;
            avance.setCreditos(creditos.get(tipo.getCodigoEnum()) + creditosDep);
            avance.setCursos(cursos.get(tipo.getCodigoEnum()) + cursosDep);

            avanceCurricularDAO.save(avance);
            cred = cred + creditos.get(tipo.getCodigoEnum());
            cur = cur + cursos.get(tipo.getCodigoEnum());
            credCarrera = credCarrera + creditos.get(tipo.getCodigoEnum());
            curCarrera = +cursos.get(tipo.getCodigoEnum());

        }
        alumno.setCursosCarreraAprobados(curCarrera);
        alumno.setCreditosCarreraAprobados(credCarrera);
        alumnoDAO.update(alumno);
    }
}

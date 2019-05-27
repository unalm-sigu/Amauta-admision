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
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.EEP;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELC;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.ELE;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.GEN;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.PROD;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.TECIND;
import pe.edu.lamolina.model.matricula.AlumnoAvanceCurricular;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoCursoSimultaneo;
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
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.CREDITOS_ADIC_ELC;
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

        for (TipoCursoCurricula tipo : tipos.values()) {
            creditos.put(tipo.getCodigoEnum(), 0);
            cursos.put(tipo.getCodigoEnum(), 0);
        }
        ResumenPlanCurricular resumenPlanCurricular = resumenPlanCurriculars.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() == ELC).findAny().orElse(null);
        ResumenPlanCurricular resumenPlanCurricularELE = resumenPlanCurriculars.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() == ELE).findAny().orElse(null);
        alumnoCursoNew.addAll(alumnoCursoElcCarreraNew);
        Collections.sort(alumnoCursoNew, new AlumnoCursoCurricula.CompareCodigo());
//        List<Long> idsEEP = new ArrayList();
        Integer sum = 0;
        Integer credAdic = CREDITOS_ADIC_ELC;
        for (AlumnoCursoCurricula curso : alumnoCursoNew) {

            if (curso.getVecesCursado() == 0) {
                AlumnoCicloCurso cat = mapCursosVecesLlevado.get(curso.getAlumno().getId() + "-" + curso.getCurso().getId());
                if (cat != null) {
                    curso.setVecesCursado(cat.getVecesCursadoTransient());
                }
            }
            if (curso.getEstadoEnum() == APR || curso.getEstadoEnum() == EQUIV) {
                TipoCursoCurriculaEnum tipo = null;

                tipo = curso.getTipoCursoCurricula().getCodigoEnum();

                ResumenPlanCurricular rpc = resumenPlanCurriculars.stream().filter(x -> x.getTipoCursoCurricula().getCodigoEnum() == curso.getTipoCursoCurricula().getCodigoEnum()).findAny().orElse(null);
                if (rpc == null) {
                    continue;
                }
                Integer prevCreditos = creditos.get(tipo);
                prevCreditos += curso.getCreditos();

                if (Arrays.asList(ELE, ELC).contains(tipo)) {
                    Integer tmp = 0;

                    if (tipo == ELE) {
                        tmp = creditos.get(ELC);
                    } else {
                        tmp = creditos.get(ELE);
                    }

                    sum = tmp + prevCreditos;
                    if ((resumenPlanCurricular != null && sum > resumenPlanCurricular.getCreditos() + credAdic)
                            || (resumenPlanCurricularELE != null && tipo == ELE && resumenPlanCurricularELE.getCreditos() == 0)) {
                        credAdic = 0;
                        tipo = EEP;
                        prevCreditos = creditos.get(tipo);
                        prevCreditos += curso.getCreditos();
                        TipoCursoCurricula tipoCursoCurriculaEEP = tipos.get(EEP);
                        curso.setTipoCursoCurricula(tipoCursoCurriculaEEP);
//                        idsEEP.add(curso.getCurso().getId());
                    }
                }
                Integer prevCursos = cursos.get(tipo);
                prevCursos++;

                creditos.replace(tipo, prevCreditos);
                cursos.replace(tipo, prevCursos);
            }
        }
//        List<AlumnoCursoCurricula> alumnoCursoTemp = new ArrayList();
//        for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoNew) {
//            alumnoCursoTemp.add(alumnoCursoCurricula);
//        }
//        alumnoCursoNew.clear();
//        for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoTemp) {
//            if (idsEEP.contains(alumnoCursoCurricula.getCurso().getId())) {
//                continue;
//            }
//            alumnoCursoNew.add(alumnoCursoCurricula);
//        }
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
                logger.debug("Curso comodin asignado {}", comodin.getCurso().getNombre());
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
        List<AlumnoCursoCurricula> cursosELC = alumnoCursoElcCarreraNew.stream().filter(x -> Arrays.asList(ELC, ELE, PROD, CULT, TECIND).contains(x.getTipoCursoCurricula().getCodigoEnum()) && x.getCicloAprobado() != null).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosComodinELC = alumnoCursoNew.stream().filter(x -> x.getCurso().getCodigo().equals("ELC") && x.getTipoCursoCurricula() != null && Arrays.asList(ELC, ELE, PROD, CULT, TECIND).contains(x.getTipoCursoCurricula().getCodigoEnum())).collect(Collectors.toList());
        Integer creditosELC = cursosELC.stream().filter(x -> Arrays.asList(ELC, ELE, PROD, CULT, TECIND).contains(x.getTipoCursoCurricula().getCodigoEnum())).mapToInt(AlumnoCursoCurricula::getCreditos).sum();
        List<Long> idsAgregados = new ArrayList();
        Collections.sort(cursosComodinELC, new AlumnoCursoCurricula.CompareCreditos());
        Collections.sort(cursosELC, new AlumnoCursoCurricula.CompareCreditos());

        for (AlumnoCursoCurricula cursoComodinELC : cursosComodinELC) {
            Integer creditosAdic = 0;
            if (Objects.equals(cursoComodinELC.getId(), cursosComodinELC.get(cursosComodinELC.size() - 1).getId())) {
                creditosAdic = CREDITOS_ADIC_ELC;
            }
            for (AlumnoCursoCurricula alumnoCurso : cursosELC) {
                if (idsAgregados.contains(alumnoCurso.getCurso().getId()) && alumnoCurso.getCicloAprobado() != null) {
                    continue;
                }
                idsAgregados.add(alumnoCurso.getCurso().getId());
                creditosELC = creditosELC - alumnoCurso.getCreditos();
                if (cursoComodinELC.getCreditosCumplidos() == null) {
                    cursoComodinELC.setCreditosCumplidos(0);
                }
                if (alumnoCurso.getCreditos() >= ((cursoComodinELC.getCreditos() + creditosAdic) - cursoComodinELC.getCreditosCumplidos())) {
                    alumnoCurso.setNumeroCiclo(cursoComodinELC.getNumeroCiclo());
                    cursoComodinELC.setNumeroCiclo(cursoComodinELC.getNumeroCiclo());
                    cursoComodinELC.setCreditosCumplidos(cursoComodinELC.getCreditosCumplidos() + alumnoCurso.getCreditos());
                    Integer res = cursoComodinELC.getCreditos() - cursoComodinELC.getCreditosCumplidos();
                    if (res <= 0) {
                        cursoComodinELC.setEstadoRegistro(EstadoEnum.INA.name());
                    }
                    break;
                } else {
                    alumnoCurso.setNumeroCiclo(cursoComodinELC.getNumeroCiclo());
                    cursoComodinELC.setCreditosCumplidos(cursoComodinELC.getCreditosCumplidos() + alumnoCurso.getCreditos());
                }
                if (creditosELC < 0) {
                    creditosELC = creditosELC + alumnoCurso.getCreditos();
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

            AlumnoCursoCurricula cursosOpcionalesNew = new AlumnoCursoCurricula();
            cursosOpcionalesNew.setAlumno(alumno);
            cursosOpcionalesNew.setCreditos(0);
            cursosOpcionalesNew.setTipoCursoCurricula(cursocurricula.getTipoCursoCurricula());
            cursosOpcionalesNew.setCurso(cursocurricula.getCurso());
            cursosOpcionalesNew.setCursoOpcional(null);
            cursosOpcionalesNew.setCursoCurricula(cursocurricula);

            if (tipoCursoELCEnums.contains(cursocurricula.getCurso().getCodigo())) {
                cursosOpcionalesNew.setEstadoEnum(PEND);
            } else {
                cursosOpcionalesNew.setEstadoEnum(NREQ);

            }
            cursosOpcionalesNew.setEstadoRegistro(EstadoEnum.ACT.name());
            cursosOpcionalesNew.setNumeroCiclo(cursocurricula.getNumeroCiclo());
            cursosOpcionalesNew.setValidado(false);
            cursosOpcionalesNew.setVecesCursado(0);
            cursosOpcionalesNew.setCreditos(cursocurricula.getCreditos());
            alumnoCursoNew.add(cursosOpcionalesNew);
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
                } else {
                    alumnoCursoCurricula.setEstadoEnum(HAB);
                }
                alumnoCursoCurricula.setCreditos(cursosAprobado.getCreditos());
                alumnoCursoCurricula.setValidado(true);
                alumnoCursoCurricula.setEstadoRegistro(EstadoEnum.ACT.name());
                alumnoCursoCurricula.setVecesCursado(cursosAprobado.getVecesCursadoTransient());
            } else if (cursosAprobado.getCurso().getDepartamentoAcademico() != null && codesDptosCultDepMed.contains(cursosAprobado.getCurso().getDepartamentoAcademico().getCodigo())) {

                addCursosComodin(alumno, alumnoCursoComodinDepNew, cursosAprobado, ds);

            } else {
                addCursosLibresCurricula(alumno, cursosAprobado, alumnoCursoElcCarreraNew, tipoCursoCurriculas, planCurriculars, mapCursoOpcional, equivalenteElectivos, mapCursoOpcionalAll, mapCursoCurriculaAll);

            }
        }

        validarCursosComodin(alumnoCursoComodinDepNew, alumnoCursoNew, resumenPlanCurriculars, tipoCursoCurriculas.stream().filter(x -> x.getCodigoEnum() == DEP).findAny().orElse(null));
        validarCursosRequisito(mapAlumCursoCurrByCursoCurri, mapRequisitosCurricula);
        validarCursosSimultaneo(mapAlumCursoCurrByCursoCurri, cursosSimultaneosAlu, mapRequisitosCurricula, ds);
        validarEquivalencias(mapAlumCursoCurrByCursoCurri, mapEquivalentesCurricula, cursosAprobados);
        validarCursosMatriculados(mapAlumCursoCurrByCurso, cursosMatriculados, ds);
        generarAvanceCurricular(alumnoCursoElcCarreraNew, alumnoCursoNew, resumenPlanCurriculars, tipoCursoCurriculas, alumnoAvanceCurriculars, alumno, mapCursosVecesLlevado);
        validarCursosELC(alumnoCursoElcCarreraNew, alumnoCursoNew, alumno);

        alumnoCursoOld = alumnoCursoOld == null ? new ArrayList<>() : alumnoCursoOld;
        for (AlumnoCursoCurricula alumnoCursoCurriculaNew : alumnoCursoNew) {
            if (alumnoCursoCurriculaNew.getCursoCurricula() != null
                    && !estadosAprobados.contains(alumnoCursoCurriculaNew.getEstadoEnum())
                    && alumnoCursoCurriculaNew.getCursoCurricula().getCreditosRequisito() > alumno.getCreditosCarreraAprobados()) {
                alumnoCursoCurriculaNew.setEstadoEnum(NREQ);
            }
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
                logger.debug(" ----------------------------- ");
                logger.debug("Curso  equivalente {} ", cursoEvaluado.getCurso().getNombre());
                boolean equivalenciaEncontrada = true;
                List<CursoEquivalente> cursosEquivGrupo = entryGrupos.getValue();

                for (CursoEquivalente cursoEq : cursosEquivGrupo) {
                    logger.debug("Curso  equivalente {} ", cursoEq.getCursoEquivalente().getNombre() + " - " + mapCursosAprobados.containsKey(cursoEq.getCursoEquivalente().getId()));
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

//    private void validarHistorial(
//            Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAluByCurso,
//            List<AlumnoCicloCurso> cursosAprobados,
//            Alumno alumno) {
//
//        Map<Long, AlumnoCicloCurso> mapCursosAprobados = TypesUtil.convertListToMap("curso.id", cursosAprobados);
//        Map<Long, AlumnoCicloCurso> mapCursosVeces = TypesUtil.convertListToMap("alumnoCursoKey", cursosAprobados);
//
//        for (AlumnoCicloCurso cursoAprobado : mapCursosAprobados.values()) {
//            AlumnoCursoCurricula cursoCurriAlu = mapCursoCurriculaAluByCurso.get(cursoAprobado.getCurso().getId());
//            if (cursoCurriAlu == null) {
//                continue;
//            }
//            if (cursoAprobado.getNota().equals("TE")) {
//                cursoCurriAlu.setEstadoEnum(CONV);
//            } else {
//                cursoCurriAlu.setEstadoEnum(APR);
//            }
//            cursoAprobado.setTipoCursoCurricula(cursoCurriAlu.getTipoCursoCurricula());
//            cursoCurriAlu.setCicloAprobado(cursoAprobado.getAlumnoCiclo().getCicloAcademico());
//            cursoCurriAlu.setCreditos(cursoAprobado.getCreditos());
//            cursoCurriAlu.setNota(cursoAprobado.getNota());
//            cursoCurriAlu.setValidado(true);
//        }
//
//        for (AlumnoCursoCurricula alumnoCursoCurricula : mapCursoCurriculaAluByCurso.values()) {
//            alumnoCursoCurricula.setVecesCursado(0);
//        }
//
//        for (AlumnoCursoCurricula alumnoCursoCurricula : mapCursoCurriculaAluByCurso.values()) {
//            Long idAlumno = alumnoCursoCurricula.getAlumno().getId();
//            Long idCurso = alumnoCursoCurricula.getCurso().getId();
//            AlumnoCicloCurso alumnoCicloCurso = mapCursosVeces.get(idAlumno + "-" + idCurso);
//            if (alumnoCicloCurso != null) {
//                alumnoCursoCurricula.setVecesCursado(alumnoCicloCurso.getVecesCursadoTransient());
//            }
//        }
//    }
//
//    private void sincronizarConCurricula(
//            Map<Long, CursoCurricula> mapCursosCurricula,
//            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAluByCurso,
//            Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAlu,
//            Alumno alumno) {
//        sincronizarCursosEliminados(mapCursosCurricula, mapCursosCurriculaAlu);
//        sincronizarCursosAgregados(mapCursosCurricula, mapCursosCurriculaAluByCurso, mapCursosCurriculaAlu, alumno);
//    }
//
//    private void sincronizarCursosEliminados(Map<Long, CursoCurricula> mapCursosCurricula, Map<Long, AlumnoCursoCurricula> mapCursosCurriculaAlu) {
//        List<Long> toBeRemoved = new LinkedList();
//        for (Map.Entry<Long, AlumnoCursoCurricula> entry : mapCursosCurriculaAlu.entrySet()) {
//            Long key = entry.getKey();
//            AlumnoCursoCurricula cursoCurri = entry.getValue();
//            Curso curso = cursoCurri.getCurso();
//            String codeDptoCurso = (String) ObjectUtil.getParentTree(curso, "departamentoAcademico.codigo");
//            if (codesDptosCultDepMed.contains(codeDptoCurso) && curso.getTipoCurriculaEnum() == TipoCurriculaEnum.REG) {
//                continue;
//            }
//
//            if (!mapCursosCurricula.containsKey(key)) {
//                toBeRemoved.add(key);
//            }
//        }
//        for (Long id : toBeRemoved) {
//            mapCursosCurriculaAlu.remove(id);
//        }
//    }
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
            CursoEquivalenteElectivo cursoEquivalenteElectivo = equivalenteElectivos.stream().filter(x -> x.getCursoEquivalente().getId() == cursosAprobado.getCurso().getId()).findAny().orElse(null);
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
                        CursoOpcionalCurricula curricula = curriculasOpcional.stream().filter((CursoOpcionalCurricula x) -> x.getCurso().getId() == cursosAprobado.getCurso().getId()).findAny().orElse(null);
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
        if (opcionalCurricula != null) {
            cursosOpcionalesNew.setTipoCursoCurricula(tipoCursoCurricula);
            alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurricula);
        } else {
            cursosOpcionalesNew.setTipoCursoCurricula(tipoCursoCurricula);
            alumnoCicloCurso.setTipoCursoCurricula(tipoCursoCurricula);
        }
        cursosOpcionalesNew.setCurso(alumnoCicloCurso.getCurso());
        cursosOpcionalesNew.setCursoOpcional(opcionalCurricula);
        cursosOpcionalesNew.setCursoCurricula(cursoCurricula);
        cursosOpcionalesNew.setValidado(true);
        cursosOpcionalesNew.setVecesCursado(alumnoCicloCurso.getVecesCursadoTransient());
        alumnoCursoElecCarreraNew.add(cursosOpcionalesNew);
    }

    private void addCursosComodin(
            Alumno alumno,
            List<AlumnoCursoCurricula> alumnoCursoComodinNew,
            AlumnoCicloCurso aprobado,
            DataSessionPivot ds) {

        logger.debug("Creditos Curso: {}", aprobado.getCreditos());
        if (aprobado.getCreditos() > 0) {
            logger.debug("Curso Comodin: {}", aprobado.getCurso().getNombre());
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
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitos) {

        for (Map.Entry<Long, AlumnoCursoCurricula> entry : mapCursoCurriculaAlu.entrySet()) {
            AlumnoCursoCurricula evaluado = entry.getValue();

            if (evaluado.isValidado() || estadosAprobados.contains(evaluado.getEstadoEnum())) {
                continue;
            }

            List<RequisitoCursoCurricula> requisitos = mapRequisitos.get(evaluado.getCursoCurricula().getId());
            if (requisitos == null || requisitos.isEmpty() || cumpleRequisitos(requisitos, mapCursoCurriculaAlu, evaluado)) {
                if (!tipoCursoELCEnums.contains(evaluado.getCurso().getCodigo())) {
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
            AlumnoCursoCurricula evaluado) {

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
            List<MatriculaCurso> cursosMatriculados, DataSessionPivot ds) {
        cursosMatriculados = cursosMatriculados == null ? new ArrayList<>() : cursosMatriculados;
        for (MatriculaCurso cursoMatriculado : cursosMatriculados) {
            if (cursoMatriculado.isEstadoMAT()) {
                AlumnoCursoCurricula cursoCurriAlu = mapCursoCurriculaAluByCurso.get(cursoMatriculado.getCurso().getId());
                if (cursoCurriAlu != null) {
                    cursoCurriAlu.setEstadoMatriculaEnum(EstadoMatriculaEnum.MAT);
                }
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
            List<TipoCursoCurricula> tipoCursoCurriculas,
            List<ResumenPlanCurricular> resumenPlanCurriculars,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            List<CursoEquivalenteElectivo> equivalenteElectivos,
            Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcionalAll,
            List<PlanCurricular> planCurriculars,
            Map<Long, List<CursoCurricula>> mapCursoCurriculaAll,
            DataSessionPivot ds) {

        Carrera carrera = alumno.getCarrera();
        this.settingPlanCurricular(alumno, planBD);
        logger.debug("Cantidad de Cursos: {}", mapCursoCurricula.size());
        this.deleteAllAlumnoCursoSimultaneoByAlumno(alumno);
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
        visorAsignaCurricula.incrementar(carrera);
    }

    private List fillList(List lista) {
        if (lista == null) {
            return new ArrayList();
        }
        return lista;
    }

//    private void validarTramiteRetiroCiclo(List<AlumnoCicloCurso> cursosAprobados, Alumno alumno, CicloAcademico cicloAcademico) {
//        List<RetiroCiclo> retiroCiclo = retiroCicloDAO.allByRetiroCiclo(alumno);
//        if (!retiroCiclo.isEmpty()) {
//            for (RetiroCiclo retiroCiclo1 : retiroCiclo) {
//
//                List<AlumnoCursoCurricula> alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allByAlumnoCicloRegularAct(alumno, retiroCiclo1.getCicloAcademico());
//
//                for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoCurriculas) {
//                    alumnoCursoCurricula.setEstadoEnum(CursoCurriculaEstadoEnum.LIMB);
//                    alumnoCursoCurriculaDAO.update(alumnoCursoCurricula);
//
//                    AlumnoCicloCurso alumnoCicloCurso = cursosAprobados.stream().filter(x -> x.getCurso() == alumnoCursoCurricula.getCurso()).findAny().orElse(null);
//                    cursosAprobados.remove(alumnoCicloCurso);
//                }
//            }
//        }
//    }
}

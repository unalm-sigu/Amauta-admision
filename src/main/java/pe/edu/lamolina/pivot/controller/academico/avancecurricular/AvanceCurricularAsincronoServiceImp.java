package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import java.util.ArrayList;
import java.util.Arrays;
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
import pe.edu.lamolina.model.academico.RequisitoCursoOpcional;
import pe.edu.lamolina.model.academico.ResumenPlanCurricular;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.AlumnoCursoSimultaneoEstadoEnum;
import pe.edu.lamolina.model.enums.CurriculaEstadoEnum;
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
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.PROD;
import static pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum.TECIND;
import pe.edu.lamolina.model.matricula.AlumnoAvanceCurricular;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoCursoSimultaneo;
import pe.edu.lamolina.model.posgrado.CursoHabilEscuela;
import pe.edu.lamolina.pivot.controller.academico.plancurricular.VisorAsignaCurricula;
import pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas;
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
    @Autowired
    VisorCalculoNotas visorCalculoNotas;
    
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
    
    @Async
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
            List<TipoCursoCurricula> tipoCursoCurriculas,
            Map<TipoCursoCurriculaEnum, ResumenPlanCurricular> mapResumenPlanCurricular,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            List<CursoEquivalenteElectivo> equivalenteElectivos,
            Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcionalAll,
            List<PlanCurricular> planCurriculars,
            Map<Long, List<CursoCurricula>> mapCursoCurriculaAll,
            Map<Long, List<RequisitoCursoOpcional>> mapRequisitoCursoOpcionals,
            DataSessionPivot ds,
            String token) {
        
        try {
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
                    tipoCursoCurriculas,
                    mapResumenPlanCurricular,
                    alumnoAvanceCurriculars,
                    equivalenteElectivos,
                    mapCursoOpcionalAll,
                    planCurriculars,
                    mapCursoCurriculaAll,
                    mapRequisitoCursoOpcionals,
                    ds, false);
            logger.info("Finalizo revision avance curricular del alumno {}", alumno.getCodigo());
            visorCalculoNotas.incrementarToken(token);
            
        } catch (Exception e) {
            logger.error("Error en revision avance curricular del alumno {}", alumno.getCodigo());
            visorCalculoNotas.incrementarToken(token);
        }
        
    }
    
    private void generarAvanceCurricular(
            List<AlumnoCursoCurricula> aluCursosElectivosNew,
            List<AlumnoCursoCurricula> aluCursosCurriculaNew,
            Map<TipoCursoCurriculaEnum, ResumenPlanCurricular> mapResumenPlanCurricular,
            Map<TipoCursoCurriculaEnum, TipoCursoCurricula> mapTipoCursoCurrila,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            Alumno alumno,
            Map<String, AlumnoCicloCurso> mapCursosVecesLlevado,
            boolean showLogger) {
        
        Map<TipoCursoCurriculaEnum, AlumnoAvanceCurricular> avances = alumnoAvanceCurriculars
                .stream()
                .filter(x -> x.getTipoCursoCurricula() != null)
                .collect(Collectors.toMap(x -> x.getTipoCursoCurricula().getCodigoEnum(), x -> x, (a, b) -> a));
        
        Map<TipoCursoCurriculaEnum, Integer> mapCreditosByTipo = new HashMap();
        Map<TipoCursoCurriculaEnum, Integer> mapCursosByTipo = new HashMap();
        Map<TipoCursoCurriculaEnum, Boolean> mapExcesoByTipo = new HashMap();
        
        ResumenPlanCurricular resumenPlanELC = mapResumenPlanCurricular.get(ELC);
        if (resumenPlanELC == null) {
            resumenPlanELC = new ResumenPlanCurricular();
            resumenPlanELC.setCreditos(0);
            resumenPlanELC.setMinimoCreditos(0);
            mapResumenPlanCurricular.put(ELC, resumenPlanELC);
        }
        
        Integer creditosMinimoCULT = 0;
        for (TipoCursoCurricula tipoCurr : mapTipoCursoCurrila.values()) {
            ResumenPlanCurricular resumenPlan = mapResumenPlanCurricular.get(tipoCurr.getCodigoEnum());
            mapCreditosByTipo.put(tipoCurr.getCodigoEnum(), 0);
            mapCursosByTipo.put(tipoCurr.getCodigoEnum(), 0);
            mapExcesoByTipo.put(tipoCurr.getCodigoEnum(), Boolean.FALSE);
            if (resumenPlan != null) {
                mapExcesoByTipo.put(tipoCurr.getCodigoEnum(), resumenPlan.getCreditos() == 0);
                if (Arrays.asList(CULT, PROD, TECIND).contains(tipoCurr.getCodigoEnum())) {
                    creditosMinimoCULT += resumenPlan.getCreditos();
                }
            }
            
        }
        Integer topeELCyELE = resumenPlanELC.getCreditos() - creditosMinimoCULT;
        Integer credAcumAllElect = 0;
        Integer credAcumELCyELE = 0;
        
        aluCursosCurriculaNew.addAll(aluCursosElectivosNew);
        Collections.sort(aluCursosCurriculaNew, new AlumnoCursoCurricula.CompareCreditos());
        Collections.sort(aluCursosCurriculaNew, new AlumnoCursoCurricula.CompareOrdenTipoCurr());
        
        this.printLogger(":::: Verificando tipo de curricula ::::", showLogger);
        this.printLogger("Tope cred electivos = " + resumenPlanELC.getCreditos(), showLogger);
        for (AlumnoCursoCurricula aluCursoCurricula : aluCursosCurriculaNew) {
            if (aluCursoCurricula.getVecesCursado() == 0) {
                String key = aluCursoCurricula.getAlumno().getId() + "-" + aluCursoCurricula.getCurso().getId();
                AlumnoCicloCurso cat = mapCursosVecesLlevado.get(key);
                if (cat != null) {
                    aluCursoCurricula.setVecesCursado(cat.getVecesCursadoTransient());
                }
            }
            
            TipoCursoCurriculaEnum tipo = aluCursoCurricula.getTipoCursoCurricula().getCodigoEnum();
            this.printLogger("Curso=" + aluCursoCurricula.getCurso().getCodigo()
                    + " creditos=" + aluCursoCurricula.getCreditos()
                    + " estado=" + aluCursoCurricula.getEstado()
                    + " tipo=" + tipo.name(), showLogger);
            
            if (Arrays.asList(APR, EQUIV, CONV).contains(aluCursoCurricula.getEstadoEnum())) {
                TipoCursoCurriculaEnum tipo2 = TipoCursoCurriculaEnum.valueOf(tipo.name());
                
                ResumenPlanCurricular resumenPlan = mapResumenPlanCurricular.get(tipo);
                if (resumenPlan == null) {
                    continue;
                }
                
                if (Arrays.asList(ELE, ELC, CULT, PROD, TECIND).contains(tipo)) {
                    Integer prevCreditos = mapCreditosByTipo.get(tipo);
                    Integer prevCursos = mapCursosByTipo.get(tipo);
                    
                    Boolean seExcedidoPost = false;
                    if (mapExcesoByTipo.get(tipo)) {
                        if (Arrays.asList(CULT, PROD, TECIND).contains(tipo)) {
                            tipo = ELC;
                        }
                        
                        if ((tipo == ELC && mapExcesoByTipo.get(ELC)) || (tipo == ELE && mapExcesoByTipo.get(ELE))) {
                            tipo = EEP;
                        } else {
                            credAcumAllElect += aluCursoCurricula.getCreditos();
                        }
                        
                        prevCursos = mapCursosByTipo.get(tipo);
                        prevCursos++;
                        prevCreditos = mapCreditosByTipo.get(tipo);
                        prevCreditos += aluCursoCurricula.getCreditos();
                        
                        TipoCursoCurricula tipoCursoCurricula = mapTipoCursoCurrila.get(tipo);
                        aluCursoCurricula.setTipoCursoCurricula(tipoCursoCurricula);
                        
                        if (mapResumenPlanCurricular.get(tipo) == null) {
                            this.printLogger("\tSave-tipo.1=" + tipo.name(), showLogger);
                            continue;
                        }
                        
                        if (tipo != ELC) {
                            seExcedidoPost = prevCreditos >= mapResumenPlanCurricular.get(tipo).getCreditos();
                        }
                        
                        mapCreditosByTipo.replace(tipo, prevCreditos);
                        mapCursosByTipo.replace(tipo, prevCursos);
                        
                    } else {
                        credAcumAllElect += aluCursoCurricula.getCreditos();
                        prevCreditos += aluCursoCurricula.getCreditos();
                        prevCursos++;
                        
                        mapCreditosByTipo.replace(tipo, prevCreditos);
                        mapCursosByTipo.replace(tipo, prevCursos);
                        seExcedidoPost = prevCreditos >= mapResumenPlanCurricular.get(tipo).getCreditos();
                    }
                    
                    if (tipo == ELC || tipo == ELE) {
                        credAcumELCyELE += aluCursoCurricula.getCreditos();
                    }
                    
                    this.printLogger("\tAcumulados-Elect-ALL=" + credAcumAllElect, showLogger);
                    this.printLogger("\tAcumulados-ELE-ELC=" + credAcumELCyELE, showLogger);
                    
                    if (credAcumAllElect >= resumenPlanELC.getCreditos() || credAcumELCyELE >= topeELCyELE) {
                        mapExcesoByTipo.replace(ELC, Boolean.TRUE);
                        mapExcesoByTipo.replace(ELE, Boolean.TRUE);
                    }
                    
                    if (seExcedidoPost && tipo != ELC) {
                        mapExcesoByTipo.replace(tipo, Boolean.TRUE);
                    }
                    
                } else {
                    Integer prevCreditos = mapCreditosByTipo.get(tipo);
                    prevCreditos += aluCursoCurricula.getCreditos();
                    
                    Integer prevCursos = mapCursosByTipo.get(tipo);
                    prevCursos++;
                    
                    mapCreditosByTipo.replace(tipo, prevCreditos);
                    mapCursosByTipo.replace(tipo, prevCursos);
                }
                
                if (tipo2 != tipo) {
                    this.printLogger("\tSave-tipo.2=" + tipo.name(), showLogger);
                }
            }
        }

        //Integer cred = 0;
        //Integer cur = 0;
        Integer credCarrera = 0;
        Integer curCarrera = 0;
        
        for (TipoCursoCurricula tipoCurr : mapTipoCursoCurrila.values()) {
            
            AlumnoAvanceCurricular avance = avances.get(tipoCurr.getCodigoEnum());
            if (avance == null) {
                avance = new AlumnoAvanceCurricular();
                avance.setTipoCursoCurricula(tipoCurr);
                avance.setAlumno(alumno);
                
                avance.setCreditos(mapCreditosByTipo.get(tipoCurr.getCodigoEnum()));
                avance.setCursos(mapCursosByTipo.get(tipoCurr.getCodigoEnum()));
                avanceCurricularDAO.save(avance);
                
            } else {
                AlumnoAvanceCurricular avanceUpd = new AlumnoAvanceCurricular(avance.getId());
                avanceUpd.setCreditos(mapCreditosByTipo.get(tipoCurr.getCodigoEnum()));
                avanceUpd.setCursos(mapCursosByTipo.get(tipoCurr.getCodigoEnum()));
                avanceCurricularDAO.updateColumns(avanceUpd, "creditos", "cursos");
            }

            //cred = cred + mapCreditosByTipo.get(tipo.getCodigoEnum());
            //cur = cur + mapCursosByTipo.get(tipo.getCodigoEnum());
            if (tipoCurr.getCodigoEnum() != EEP) {
                credCarrera = credCarrera + mapCreditosByTipo.get(tipoCurr.getCodigoEnum());
                curCarrera = +mapCursosByTipo.get(tipoCurr.getCodigoEnum());
            }
        }
        
        Alumno alumnoUpd = new Alumno(alumno.getId());
        alumnoUpd.setCursosCarreraAprobados(curCarrera);
        alumnoUpd.setCreditosCarreraAprobados(credCarrera);
        alumnoDAO.updateColumns(alumnoUpd, "cursosCarreraAprobados", "creditosCarreraAprobados");
    }
    
    private void validarCursosComodin(
            List<AlumnoCursoCurricula> alumnoCursosComodinesDepNew,
            List<AlumnoCursoCurricula> alumnoCursosCurriculaNew,
            Map<TipoCursoCurriculaEnum, ResumenPlanCurricular> mapResumenPlanCurricular,
            TipoCursoCurricula tipoCursoCurricula) {
        
        List<AlumnoCursoCurricula> cursosComodinNew = alumnoCursosCurriculaNew.stream().filter(x -> x.getCurso().getCodigo().equals("EG1006")).collect(Collectors.toList());
        List<Long> ids = new ArrayList();
        ResumenPlanCurricular resumenPlanCurricularDep = mapResumenPlanCurricular.get(DEP);
        Integer cred = resumenPlanCurricularDep.getCreditos();
        for (AlumnoCursoCurricula alumnoCursoCurricula : cursosComodinNew) {
            for (AlumnoCursoCurricula comodin : alumnoCursosComodinesDepNew) {
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
                alumnoCursosCurriculaNew.add(comodin);
                break;
            }
        }
    }
    
    private void validarCursosELC(
            List<AlumnoCursoCurricula> alumnoCursosElectivosNewQQ,
            List<AlumnoCursoCurricula> alumnoCursosCurriculaNew, Alumno alumno) {
        
        List<AlumnoCursoCurricula> cursosELC = alumnoCursosCurriculaNew.stream().filter(x -> Arrays.asList(ELC, ELE, PROD, CULT, TECIND).contains(x.getTipoCursoCurricula().getCodigoEnum()) && x.getCicloAprobado() != null).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosPRO = alumnoCursosCurriculaNew.stream().filter(x -> Arrays.asList(PROD).contains(x.getTipoCursoCurricula().getCodigoEnum()) && x.getCicloAprobado() != null).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosCULT = alumnoCursosCurriculaNew.stream().filter(x -> Arrays.asList(CULT).contains(x.getTipoCursoCurricula().getCodigoEnum()) && x.getCicloAprobado() != null).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosTECIND = alumnoCursosCurriculaNew.stream().filter(x -> Arrays.asList(TECIND).contains(x.getTipoCursoCurricula().getCodigoEnum()) && x.getCicloAprobado() != null).collect(Collectors.toList());
        
        List<AlumnoCursoCurricula> cursosComodinELC = alumnoCursosCurriculaNew.stream().filter(x -> x.getCurso().getCodigo().equals("ELC")).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosComodinPRO = alumnoCursosCurriculaNew.stream().filter(x -> x.getCurso().getCodigo().equals("PROD")).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosComodinCULT = alumnoCursosCurriculaNew.stream().filter(x -> x.getCurso().getCodigo().equals("CULT")).collect(Collectors.toList());
        List<AlumnoCursoCurricula> cursosComodinTECIND = alumnoCursosCurriculaNew.stream().filter(x -> x.getCurso().getCodigo().equals("TECIND")).collect(Collectors.toList());
        
        Integer creditosELC = cursosELC.stream().mapToInt(AlumnoCursoCurricula::getCreditos).sum();
        Integer creditosPRO = cursosPRO.stream().mapToInt(AlumnoCursoCurricula::getCreditos).sum();
        Integer creditosCULT = cursosCULT.stream().mapToInt(AlumnoCursoCurricula::getCreditos).sum();
        Integer creditosTECIND = cursosTECIND.stream().mapToInt(AlumnoCursoCurricula::getCreditos).sum();
        
        for (AlumnoCursoCurricula cursoComodin : cursosComodinTECIND) {
            if (creditosTECIND == 0) {
                break;
            }
            if (cursoComodin.getCreditos() <= creditosTECIND) {
                int creditos = cursoComodin.getCreditos();
                cursoComodin.setCreditos(0);
                creditosTECIND -= creditos;
                alumnoCursosCurriculaNew.remove(cursoComodin);
                
            } else {
                int creditos = cursoComodin.getCreditos() - creditosTECIND;
                cursoComodin.setCreditos(creditos);
                creditosTECIND = 0;
            }
        }
        
        for (AlumnoCursoCurricula cursoComodin : cursosComodinCULT) {
            if (creditosCULT == 0) {
                break;
            }
            if (cursoComodin.getCreditos() <= creditosCULT) {
                int creditos = cursoComodin.getCreditos();
                cursoComodin.setCreditos(0);
                creditosCULT -= creditos;
                alumnoCursosCurriculaNew.remove(cursoComodin);
                
            } else {
                int creditos = cursoComodin.getCreditos() - creditosCULT;
                cursoComodin.setCreditos(creditos);
                creditosCULT = 0;
            }
        }
        for (AlumnoCursoCurricula cursoComodin : cursosComodinPRO) {
            if (creditosPRO == 0) {
                break;
            }
            if (cursoComodin.getCreditos() <= creditosPRO) {
                int creditos = cursoComodin.getCreditos();
                cursoComodin.setCreditos(0);
                creditosPRO -= creditos;
                alumnoCursosCurriculaNew.remove(cursoComodin);
                
            } else {
                int creditos = cursoComodin.getCreditos() - creditosPRO;
                cursoComodin.setCreditos(creditos);
                creditosPRO = 0;
            }
        }
        
        for (AlumnoCursoCurricula cursoComodin : cursosComodinELC) {
            if (creditosELC == 0) {
                break;
            }
            if (cursoComodin.getCreditos() <= creditosELC) {
                int creditos = cursoComodin.getCreditos();
                cursoComodin.setCreditos(0);
                creditosELC -= creditos;
                alumnoCursosCurriculaNew.remove(cursoComodin);
                
            } else {
                int creditos = cursoComodin.getCreditos() - creditosELC;
                cursoComodin.setCreditos(creditos);
                creditosELC = 0;
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
            List<TipoCursoCurricula> tipoCursoCurriculax,
            Map<TipoCursoCurriculaEnum, ResumenPlanCurricular> mapResumenPlanCurricular,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            List<CursoEquivalenteElectivo> equivalenteElectivox,
            Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcionalAll,
            List<PlanCurricular> planCurriculars,
            Map<Long, List<CursoCurricula>> mapCursoCurriculaAll,
            Map<Long, List<RequisitoCursoOpcional>> mapRequisitoCursoOpcionals,
            DataSessionPivot ds, boolean showLogger) {
        
        List<AlumnoCursoSimultaneo> cursosSimultaneosAlu = new ArrayList();
        Map<Long, AlumnoCursoCurricula> mapAlumCursoCurrByCursoCurri = new LinkedHashMap();
        Map<Long, AlumnoCursoCurricula> mapAlumCursoCurrByCurso = new LinkedHashMap();
        List<AlumnoCursoCurricula> alumnoCursosCurriculaNew = new ArrayList();
        List<AlumnoCursoCurricula> alumnoCursosElectivosNew = new ArrayList();
        List<AlumnoCursoCurricula> alumnoCursosComodinesDepNew = new ArrayList();
        
        List<AlumnoCicloCurso> cursosAprobadosPrevio = new ArrayList();
        for (AlumnoCicloCurso cursosAprobado : cursosAprobados) {
            cursosAprobadosPrevio.add((AlumnoCicloCurso) cursosAprobado.clone());
        }
        Map<Long, AlumnoCicloCurso> mapCursoAprobadoOld = TypesUtil.convertListToMap("id", cursosAprobadosPrevio);
        
        cursosAprobados = cursosAprobados.stream().filter(x -> x.getEstadoEnum() != EstadoMatriculaEnum.RCI).collect(Collectors.toList());
        Map<TipoCursoCurriculaEnum, TipoCursoCurricula> mapTipoCursoCurricula = TypesUtil.convertListToMap("codigoEnum", tipoCursoCurriculax);
        
        List<CursoCurricula> cursosCurriculaPlan = new ArrayList(mapCursosCurricula.values());
        for (CursoCurricula cursocurricula : cursosCurriculaPlan) {
            
            AlumnoCursoCurricula aluCursoCurrNew = new AlumnoCursoCurricula();
            aluCursoCurrNew.setAlumno(alumno);
            aluCursoCurrNew.setTipoCursoCurricula(cursocurricula.getTipoCursoCurricula());
            aluCursoCurrNew.setCurso(cursocurricula.getCurso());
            aluCursoCurrNew.setCursoOpcional(null);
            aluCursoCurrNew.setCursoCurricula(cursocurricula);
            if (tipoCursoELCEnums.contains(cursocurricula.getCurso().getCodigo())) {
                aluCursoCurrNew.setEstadoEnum(PEND);
            } else {
                aluCursoCurrNew.setEstadoEnum(NREQ);
            }
            aluCursoCurrNew.setEstadoRegistro(EstadoEnum.ACT.name());
            aluCursoCurrNew.setNumeroCiclo(cursocurricula.getNumeroCiclo());
            aluCursoCurrNew.setValidado(false);
            aluCursoCurrNew.setVecesCursado(0);
            aluCursoCurrNew.setCreditos(cursocurricula.getCreditos());
            alumnoCursosCurriculaNew.add(aluCursoCurrNew);
            
            mapAlumCursoCurrByCursoCurri.put(cursocurricula.getId(), aluCursoCurrNew);
            mapAlumCursoCurrByCurso.put(cursocurricula.getCurso().getId(), aluCursoCurrNew);
        }
        
        equivalenteElectivox = TypesUtil.getListNotNull(equivalenteElectivox);
        Map<Long, CursoEquivalenteElectivo> mapEquivalenteElectivo = TypesUtil.convertListToMap("cursoEquivalente.id", equivalenteElectivox);
        
        cursoOpcionalCurriculas = TypesUtil.getListNotNull(cursoOpcionalCurriculas);
        Map<Long, CursoOpcionalCurricula> mapCursoOpcional = TypesUtil.convertListToMap("curso.id", cursoOpcionalCurriculas);
        
        for (AlumnoCicloCurso cursoAprobado : cursosAprobados) {
            this.printLogger(
                    "alumno: " + alumno.getCodigo()
                    + " :::: cursoAprobado:" + cursoAprobado.getCurso().getCodigo()
                    + " :::: creditos:" + cursoAprobado.getCreditos()
                    + " - aprobado:" + cursoAprobado.isAprobado(), showLogger);
            
            AlumnoCursoCurricula alumnoCursoCurricula = mapAlumCursoCurrByCurso.get(cursoAprobado.getCurso().getId());
            
            if (alumnoCursoCurricula != null) {
                this.printLogger("\tCurso-obligatorio", showLogger);
                cursoAprobado.setTipoCursoCurricula(alumnoCursoCurricula.getTipoCursoCurricula());
                
                if (cursoAprobado.isAprobado()) {
                    alumnoCursoCurricula.setCicloAprobado(cursoAprobado.getAlumnoCiclo().getCicloAcademico());
                    alumnoCursoCurricula.setNota(cursoAprobado.getNota());
                    if (cursoAprobado.getNota().equals("TE")) {
                        alumnoCursoCurricula.setEstadoEnum(CONV);
                    } else {
                        alumnoCursoCurricula.setEstadoEnum(APR);
                    }
                    alumnoCursoCurricula.setValidado(true);
                } else {
                    alumnoCursoCurricula.setEstadoEnum(HAB);
                }
                alumnoCursoCurricula.setCreditos(cursoAprobado.getCreditos());
                alumnoCursoCurricula.setEstadoRegistro(EstadoEnum.ACT.name());
                alumnoCursoCurricula.setVecesCursado(cursoAprobado.getVecesCursadoTransient());
                
            } else if (cursoAprobado.getCurso().getDepartamentoAcademico() != null
                    && codesDptosCultDepMed.contains(cursoAprobado.getCurso().getDepartamentoAcademico().getCodigo())) {
                this.printLogger("\tCurso-deportivo-cultural", showLogger);
                TipoCursoCurricula tipoCursoCurricula = mapTipoCursoCurricula.get(DEP);
                addCursosComodin(alumno, alumnoCursosComodinesDepNew, cursoAprobado, tipoCursoCurricula, ds);
                
            } else {
                addCursosLibresCurricula(
                        alumno,
                        cursoAprobado,
                        alumnoCursosElectivosNew,
                        mapTipoCursoCurricula,
                        planCurriculars,
                        mapCursoOpcional,
                        mapEquivalenteElectivo,
                        mapCursoOpcionalAll,
                        mapCursoCurriculaAll, showLogger);
                
            }
        }
        
        cursosMatriculados = TypesUtil.getListNotNull(cursosMatriculados);
        
        validarCursosComodin(alumnoCursosComodinesDepNew, alumnoCursosCurriculaNew, mapResumenPlanCurricular, mapTipoCursoCurricula.get(DEP));
        validarEquivalencias(mapAlumCursoCurrByCursoCurri, mapEquivalentesCurricula, cursosAprobados);
        validarCursosRequisito(mapAlumCursoCurrByCursoCurri, mapRequisitosCurricula, alumno);
        validarCursosMatriculados(mapAlumCursoCurrByCurso, cursosMatriculados, ds, alumno, alumnoCursosCurriculaNew, mapEquivalenteElectivo, cursoOpcionalCurriculas, mapTipoCursoCurricula, mapRequisitoCursoOpcionals);
        generarAvanceCurricular(alumnoCursosElectivosNew, alumnoCursosCurriculaNew, mapResumenPlanCurricular, mapTipoCursoCurricula, alumnoAvanceCurriculars, alumno, mapCursosVecesLlevado, showLogger);
        validarCursosELC(alumnoCursosElectivosNew, alumnoCursosCurriculaNew, alumno);
        
        alumnoCursoOld = TypesUtil.getListNotNull(alumnoCursoOld);
        Map<Long, AlumnoCursoCurricula> mapAluCursoCurriculaOld = TypesUtil.convertListToMap("curso.id", alumnoCursoOld);
        
        this.printLogger("cursosSimultaneosAlu.size.1=" + cursosSimultaneosAlu.size(), showLogger);
        validarCursosSimultaneo(mapAlumCursoCurrByCursoCurri, cursosSimultaneosAlu, mapRequisitosCurricula, ds, showLogger);
        this.printLogger("cursosSimultaneosAlu.size.2=" + cursosSimultaneosAlu.size(), showLogger);
        
        this.saveAlumnoCursoCurricula(alumnoCursosCurriculaNew, mapAluCursoCurriculaOld, showLogger);
        
        for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoOld) {
            if (!alumnoCursoCurricula.isValidado()) {
                alumnoCursoCurricula.setEstadoRegistro(INA.name());
                alumnoCursoCurriculaDAO.updateColumns(alumnoCursoCurricula, "estadoRegistro");
            }
        }
        
        for (AlumnoCursoSimultaneo alumnoCursoSimultaneo : cursosSimultaneosAlu) {
            alumnoCursoSimultaneoDAO.save(alumnoCursoSimultaneo);
        }
        
        for (AlumnoCicloCurso cursosAprobado : cursosAprobados) {
            if (cursosAprobado.getTipoCursoCurricula() != null && cursosAprobado.getTipoCursoCurricula().getCodigoEnum() == EEP) {
                cursosAprobado.setTipoCursoCurricula(null);
            }
            AlumnoCicloCurso cursoAprobadoOld = mapCursoAprobadoOld.get(cursosAprobado.getId());
            Boolean noIguales = !ObjectUtil.equalsAttrs(cursoAprobadoOld, cursosAprobado, Arrays.asList("tipoCursoCurricula", "esEquivalente", "cursoEquivalente"));
            if (noIguales) {
                alumnoCicloCursoDAO.updateColumns(cursosAprobado, "tipoCursoCurricula", "esEquivalente", "cursoEquivalente");
            }
            
        }
        
    }
    
    private void saveAlumnoCursoCurricula(
            List<AlumnoCursoCurricula> alumnoCursosCurricula,
            Map<Long, AlumnoCursoCurricula> mapAluCursoCurriculaOld, boolean showLogger) {
        
        for (AlumnoCursoCurricula aluCursoCurriculaNew : alumnoCursosCurricula) {
            Curso curso = aluCursoCurriculaNew.getCurso();
            this.printLogger("\t.save.curso:" + curso.getCodigo() + " estado=" + aluCursoCurriculaNew.getEstado(), showLogger);
            
            if (aluCursoCurriculaNew.isCaduco()) {
                aluCursoCurriculaNew.setEstadoRegistroEnum(INA);
            }
            
            AlumnoCursoCurricula aluCursoCurriculaOld = mapAluCursoCurriculaOld.get(curso.getId());
            if (aluCursoCurriculaOld == null) {
                alumnoCursoCurriculaDAO.save(aluCursoCurriculaNew);
                
            } else {
                Boolean iguales = ObjectUtil.equalsAttrs(aluCursoCurriculaNew, aluCursoCurriculaOld,
                        Arrays.asList(
                                "nota", "numeroCiclo", "cicloAprobado", "esSimultaneo",
                                "creditos", "creditosCumplidos", "vecesCursado",
                                "estado", "estadoMatricula", "estadoRegistro",
                                "cursoCurricula", "cursoOpcional",
                                "tipoCursoCurriculaOrigen", "tipoCursoCurricula"));
                
                if (iguales) {
                    aluCursoCurriculaOld.setValidado(true);
                    aluCursoCurriculaNew.setId(aluCursoCurriculaOld.getId());
                    continue;
                }
                
                aluCursoCurriculaOld.setValidado(true);
                aluCursoCurriculaNew.setId(aluCursoCurriculaOld.getId());
                alumnoCursoCurriculaDAO.updateColumns(aluCursoCurriculaNew,
                        "nota", "numeroCiclo", "cicloAprobado", "esSimultaneo",
                        "creditos", "creditosCumplidos", "vecesCursado",
                        "estado", "estadoMatricula", "estadoRegistro",
                        "cursoCurricula", "cursoOpcional",
                        "tipoCursoCurriculaOrigen", "tipoCursoCurricula");
            }
            
        }
    }
    
    private void validarEquivalencias(
            Map<Long, AlumnoCursoCurricula> mapAluCursoCurriculaByIdCursoCurricula,
            Map<Long, List<CursoEquivalente>> mapEquivalentesAll,
            List<AlumnoCicloCurso> cursosAprobadosAll) {
        
        List<AlumnoCicloCurso> cursosAprobados = cursosAprobadosAll.stream().filter(x -> x.isAprobado()).collect(Collectors.toList());
        Map<Long, AlumnoCicloCurso> mapCursosAprobados = TypesUtil.convertListToMap("curso.id", cursosAprobados);
        
        List<AlumnoCursoCurricula> aluCursosCurricula = new ArrayList(mapAluCursoCurriculaByIdCursoCurricula.values());
        for (AlumnoCursoCurricula aluCursoCurricula : aluCursosCurricula) {
            CursoCurricula cursoCurricula = aluCursoCurricula.getCursoCurricula();
            List<CursoEquivalente> cursosEquivalentes = TypesUtil.getListNotNull(mapEquivalentesAll.get(cursoCurricula.getId()));
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
                    aluCursoCurricula.setEstadoEnum(EQUIV);
                    aluCursoCurricula.setValidado(true);
                    break;
                }
            }
        }
        
//        List<CursoEquivalente> cursoEquivalentaes = new ArrayList();
//        for (Map.Entry<Long, List<CursoEquivalente>> entry : mapEquivalentesAll.entrySet()) {
//            cursoEquivalentaes.addAll(entry.getValue());
//        }
//        
//        Map<Long, List<CursoEquivalente>> mapCursosEquiInvertido = TypesUtil.convertListToMapList("cursoEquivalente.id", cursoEquivalentaes);
//        for (AlumnoCursoCurricula aluCursoCurricula : aluCursosCurricula) {
//            CursoCurricula cursoCurricula = aluCursoCurricula.getCursoCurricula();
//            List<CursoEquivalente> cursosEquivalentes = TypesUtil.getListNotNull(mapCursosEquiInvertido.get(cursoCurricula.getCurso().getId()));
//            if (cursosEquivalentes.isEmpty()) {
//                continue;
//            }
//            
//            Map<Integer, List<CursoEquivalente>> mapGruposEquiv = TypesUtil.convertListToMapList("grupo", cursosEquivalentes);
//            
//            for (Map.Entry<Integer, List<CursoEquivalente>> entryGrupos : mapGruposEquiv.entrySet()) {
//                
//                boolean equivalenciaEncontrada = true;
//                List<CursoEquivalente> cursosEquivGrupo = entryGrupos.getValue();
//                
//                for (CursoEquivalente cursoEq : cursosEquivGrupo) {
//                    if (!mapCursosAprobados.containsKey(cursoEq.getCursoCurricula().getCurso().getId())) {
//                        equivalenciaEncontrada = false;
//                        break;
//                    }
//                }
//                
//                if (equivalenciaEncontrada) {
//                    aluCursoCurricula.setEstadoEnum(EQUIV);
//                    aluCursoCurricula.setValidado(true);
//                    if (cursoCurricula.getEstadoEnum() == CurriculaEstadoEnum.CAD) {
//                        aluCursoCurricula.setCaduco(true);
//                    }
//                    break;
//                }
//            }
//        }
        
    }
    
    private void validarCreditosAprobados(
            CursoCurricula cursoCurri,
            AlumnoCursoCurricula cursoCurriAlu,
            int creditosAprobados) {
        
        Integer creditosAprobadosRequisito = fillInteger(cursoCurri.getCreditosRequisito(), 0);
        if (creditosAprobadosRequisito > creditosAprobados) {
            cursoCurriAlu.setEstadoEnum(NREQ);
            cursoCurriAlu.setValidado(true);
        }
    }
    
    private Integer fillInteger(Integer numero, int defecto) {
        if (numero == null) {
            return defecto;
        }
        return numero;
    }
    
    private void addCursosLibresCurricula(
            Alumno alumno,
            AlumnoCicloCurso cursoAprobado,
            List<AlumnoCursoCurricula> aluCursosElectivosNew,
            Map<TipoCursoCurriculaEnum, TipoCursoCurricula> mapTipoCursoCurricula,
            List<PlanCurricular> planCurriculars,
            Map<Long, CursoOpcionalCurricula> mapCursoOpcional,
            Map<Long, CursoEquivalenteElectivo> mapEquivalenteElectivo,
            Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcionalAll,
            Map<Long, List<CursoCurricula>> mapCursoCurriculaAll, boolean showLogger) {
        
        CursoOpcionalCurricula cursoOpcionalCurricula = mapCursoOpcional.get(cursoAprobado.getCurso().getId());
        
        if (cursoOpcionalCurricula != null) {
            TipoCursoCurricula tipoCursoCurricula = cursoOpcionalCurricula.getTipoCursoCurricula();
            addAlumnoCursoCurricula(alumno, cursoAprobado, cursoOpcionalCurricula, null, aluCursosElectivosNew, tipoCursoCurricula);
            this.printLogger("\tCurso electivo mismo-plan " + tipoCursoCurricula.getCodigo(), showLogger);
            
        } else {
            CursoEquivalenteElectivo cursoEquivalenteElectivo = mapEquivalenteElectivo.get(cursoAprobado.getCurso().getId());
            if (cursoEquivalenteElectivo == null) {
                boolean ubicado = false;
                for (PlanCurricular planCurricular : planCurriculars) {
                    if (Objects.equals(planCurricular.getId(), alumno.getPlanCurricular().getId())) {
                        continue;
                    }
                    
                    List<CursoCurricula> cursosCurriculas = TypesUtil.getListNotNull(mapCursoCurriculaAll.get(planCurricular.getId()));
                    CursoCurricula cursoCurricula = cursosCurriculas.stream().filter(x -> Objects.equals(x.getCurso().getId(), cursoAprobado.getCurso().getId())).findAny().orElse(null);
                    if (cursoCurricula != null) {
                        TipoCursoCurricula tipoCursoCurricula = mapTipoCursoCurricula.get(ELE);
                        addAlumnoCursoCurricula(alumno, cursoAprobado, null, cursoCurricula, aluCursosElectivosNew, tipoCursoCurricula);
                        this.printLogger("\tCurso obligatorio otro-plan " + tipoCursoCurricula.getCodigo(), showLogger);
                        ubicado = true;
                        break;
                        
                    } else {
                        List<CursoOpcionalCurricula> curriculasOpcional = mapCursoOpcionalAll.get(planCurricular.getId());
                        curriculasOpcional = curriculasOpcional == null ? new ArrayList() : curriculasOpcional;
                        CursoOpcionalCurricula curricula = curriculasOpcional.stream().filter((CursoOpcionalCurricula x) -> Objects.equals(x.getCurso().getId(), cursoAprobado.getCurso().getId())).findAny().orElse(null);
                        if (curricula != null) {
                            TipoCursoCurricula tipoCursoCurricula = mapTipoCursoCurricula.get(ELE);
                            addAlumnoCursoCurricula(alumno, cursoAprobado, curricula, null, aluCursosElectivosNew, tipoCursoCurricula);
                            this.printLogger("\tCurso electivo otro-plan " + tipoCursoCurricula.getCodigo(), showLogger);
                            ubicado = true;
                            break;
                        }
                    }
                }
                
                this.printLogger("\tCurso libre no ubicado en otros planes curriculares ", !ubicado && showLogger);
                
            } else {
                cursoAprobado.setCursoEquivalente(cursoEquivalenteElectivo.getCursoOpcionalCurricula().getCurso());
                cursoAprobado.setEsEquivalente(Boolean.TRUE);
                TipoCursoCurricula tipoCursoCurricula = cursoEquivalenteElectivo.getCursoOpcionalCurricula().getTipoCursoCurricula();
                addAlumnoCursoCurricula(alumno, cursoAprobado, cursoEquivalenteElectivo.getCursoOpcionalCurricula(), null, aluCursosElectivosNew, tipoCursoCurricula);
                this.printLogger("\tCurso equivalente " + tipoCursoCurricula.getCodigo(), showLogger);
                
            }
        }
    }
    
    private void addAlumnoCursoCurricula(
            Alumno alumno,
            AlumnoCicloCurso cursoAprobado,
            CursoOpcionalCurricula opcionalCurricula,
            CursoCurricula cursoCurricula,
            List<AlumnoCursoCurricula> alumnoCursosCurriculaNew,
            TipoCursoCurricula tipoCursoCurricula) {
        
        AlumnoCursoCurricula aluCursoCurrNew = new AlumnoCursoCurricula();
        if (cursoAprobado.getEstaAprobado() == 1) {
            if (cursoAprobado.getNota().equals("TE")) {
                aluCursoCurrNew.setEstadoEnum(CONV);
            } else if (cursoAprobado.getCursoEquivalente() != null) {
                aluCursoCurrNew.setEstadoEnum(EQUIV);
            } else {
                aluCursoCurrNew.setEstadoEnum(APR);
            }
            aluCursoCurrNew.setCicloAprobado(cursoAprobado.getAlumnoCiclo().getCicloAcademico());
            aluCursoCurrNew.setNota(cursoAprobado.getNota());
            
        } else {
            aluCursoCurrNew.setEstadoEnum(HAB);
        }
        
        if (Arrays.asList(ELE, ELC, PROD, CULT, TECIND).contains(tipoCursoCurricula.getCodigoEnum())) {
            aluCursoCurrNew.setNumeroCiclo(10);
        }
        
        if (cursoAprobado.getCursoEquivalente() != null) {
            aluCursoCurrNew.setCurso(cursoAprobado.getCursoEquivalente());
        } else {
            aluCursoCurrNew.setCurso(cursoAprobado.getCurso());
        }
        
        if (tipoCursoCurricula.getCodigoEnum() != ELE) {
            aluCursoCurrNew.setCursoOpcional(opcionalCurricula);
            aluCursoCurrNew.setCursoCurricula(cursoCurricula);
        }
        
        aluCursoCurrNew.setValidado(true);
        aluCursoCurrNew.setEstadoRegistro(EstadoEnum.ACT.name());
        aluCursoCurrNew.setAlumno(alumno);
        aluCursoCurrNew.setCreditos(cursoAprobado.getCreditos());
        aluCursoCurrNew.setVecesCursado(cursoAprobado.getVecesCursado());
        aluCursoCurrNew.setTipoCursoCurricula(tipoCursoCurricula);
        alumnoCursosCurriculaNew.add(aluCursoCurrNew);
        
        cursoAprobado.setTipoCursoCurricula(tipoCursoCurricula);
    }
    
    private void addCursosComodin(
            Alumno alumno,
            List<AlumnoCursoCurricula> aluCursosComodinesNew,
            AlumnoCicloCurso cursoAprobado,
            TipoCursoCurricula tipoCursoCurricula,
            DataSessionPivot ds) {
        
        cursoAprobado.setTipoCursoCurricula(tipoCursoCurricula);
        if (cursoAprobado.getCreditos() > 0) {
            AlumnoCursoCurricula cursoComodin = new AlumnoCursoCurricula();
            cursoComodin.setAlumno(alumno);
            cursoComodin.setCicloAprobado(cursoAprobado.getAlumnoCiclo().getCicloAcademico());
            cursoComodin.setCreditos(cursoAprobado.getCreditos());
            cursoComodin.setCurso(cursoAprobado.getCurso());
            cursoComodin.setCursoCurricula(null);
            cursoComodin.setCursoOpcional(null);
            if (cursoAprobado.getNota().equals("TE")) {
                cursoComodin.setEstadoEnum(CONV);
            } else {
                cursoComodin.setEstadoEnum(APR);
            }
            cursoComodin.setEstadoRegistro(EstadoEnum.ACT.name());
            cursoComodin.setNota(cursoAprobado.getNota());
            cursoComodin.setValidado(true);
            cursoComodin.setVecesCursado(cursoAprobado.getVecesCursadoTransient());
            aluCursosComodinesNew.add(cursoComodin);
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
            Map<Long, AlumnoCursoCurricula> mapAluCursoCurriculaByIdCursoCurricula,
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitos,
            Alumno alumno) {
        
        for (Map.Entry<Long, AlumnoCursoCurricula> entry : mapAluCursoCurriculaByIdCursoCurricula.entrySet()) {
            AlumnoCursoCurricula evaluado = entry.getValue();
            if (evaluado.isValidado()) {
                continue;
            }
            if (estadosAprobados.contains(evaluado.getEstadoEnum())) {
                continue;
            }
            
            List<RequisitoCursoCurricula> requisitos = TypesUtil.getListNotNull(mapRequisitos.get(evaluado.getCursoCurricula().getId()));
            if (requisitos.isEmpty() || cumpleRequisitos(requisitos, mapAluCursoCurriculaByIdCursoCurricula, evaluado)) {
                if (!tipoCursoELCEnums.contains(evaluado.getCurso().getCodigo())
                        && evaluado.getCursoCurricula().getCreditosRequisito() <= alumno.getCreditosAprobadosConvalidados()) {
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
            if (evaluado.getCursoCurricula().getEstadoEnum() == CurriculaEstadoEnum.CAD) {
                System.out.println("EL CURSO HA CADUCADO");
                evaluado.setCaduco(true);
            }
            validarCreditosAprobados(evaluado.getCursoCurricula(), evaluado, alumno.getCreditosAprobadosConvalidados());
        }
        
    }
    
    private boolean cumpleRequisitos(
            List<RequisitoCursoCurricula> requisitos,
            Map<Long, AlumnoCursoCurricula> mapAluCursoCurriculaByIdCursoCurricula,
            AlumnoCursoCurricula evaluado) {
        
        boolean requisitosCumplidos = false;
        
        for (RequisitoCursoCurricula requisito : requisitos) {
            AlumnoCursoCurricula cursoRequisito = mapAluCursoCurriculaByIdCursoCurricula.get(requisito.getCursoRequisito().getId());
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
            Map<Long, List<RequisitoCursoCurricula>> mapRequisitos,
            DataSessionPivot ds, boolean showLogger) {
        
        for (Map.Entry<Long, AlumnoCursoCurricula> entry : mapCursosCurriculaAlu.entrySet()) {
            
            AlumnoCursoCurricula evaluado = entry.getValue();
            if (evaluado.getCicloAprobado() != null) {
                continue;
            }
            if (evaluado.getEstadoEnum() == HAB && evaluado.isValidado()) {
                continue;
            }
            
            List<RequisitoCursoCurricula> requisitos = TypesUtil.getListNotNull(mapRequisitos.get(evaluado.getCursoCurricula().getId()));
            List<AlumnoCursoSimultaneo> requisitosSimultaneo = new ArrayList();
            
            if (requisitos.isEmpty() || validarSimultaneos(requisitosSimultaneo, requisitos, mapCursosCurriculaAlu, evaluado, ds, showLogger)) {
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
            AlumnoCursoCurricula evaluado,
            DataSessionPivot ds, boolean showLogger) {
        
        boolean requisitosCumplidos = true;
        for (RequisitoCursoCurricula requisito : requisitos) {
            
            AlumnoCursoCurricula cursoRequisito = mapCursosCurriculaAlu.get(requisito.getCursoRequisito().getId());
            if (cursoRequisito == null) {
                continue;
            }
            
            if (requisito.getSimultaneo() == 0) {
                if (Arrays.asList(APR, CONV, EQUIV).contains(cursoRequisito.getEstadoEnum())) {
                    requisitosCumplidos = true;
                    continue;
                }
                requisitosCumplidos = false;
                break;
            }
            
            if (Arrays.asList(HAB, MAT).contains(cursoRequisito.getEstadoEnum())) {
                AlumnoCursoSimultaneo alumnoCursoSimultaneo = new AlumnoCursoSimultaneo();
                alumnoCursoSimultaneo.setAlumnoCursoCurricula(evaluado);
                alumnoCursoSimultaneo.setCurso(requisito.getCursoRequisito().getCurso());
                alumnoCursoSimultaneo.setEstadoEnum(AlumnoCursoSimultaneoEstadoEnum.NMAT);
                alumnoCursoSimultaneo.setFechaRegistro(new Date());
                alumnoCursoSimultaneo.setUserRegistro(ds.getUsuario());
                if (showLogger) {
                    logger.info("\tevaluado.curso={}  evaluado.id={}", evaluado.getCurso().getCodigo(), evaluado.getId());
                }
                //ObjectUtil.printAttr(alumnoCursoSimultaneo);
                simultaneos.add(alumnoCursoSimultaneo);
                
            } else {
                if (Arrays.asList(APR, CONV, EQUIV).contains(cursoRequisito.getEstadoEnum())) {
                    requisitosCumplidos = true;
                    continue;
                }
                requisitosCumplidos = false;
                break;
            }
        }
        
        return requisitosCumplidos;
    }
    
    private void validarCursosMatriculados(
            Map<Long, AlumnoCursoCurricula> mapCursoCurriculaAluByCurso,
            List<MatriculaCurso> cursosMatriculados,
            DataSessionPivot ds,
            Alumno alumno,
            List<AlumnoCursoCurricula> alumnoCursoNew,
            Map<Long, CursoEquivalenteElectivo> mapEquivalenteElectivo,
            List<CursoOpcionalCurricula> cursoOpcionalCurriculas,
            Map<TipoCursoCurriculaEnum, TipoCursoCurricula> mapTipoCursoCurricula,
            Map<Long, List<RequisitoCursoOpcional>> mapRequisitoCursoOpcionals) {
        
        for (MatriculaCurso cursoMatriculado : cursosMatriculados) {
            if (cursoMatriculado.isEstadoMAT()) {
                AlumnoCursoCurricula cursoCurriAlu = mapCursoCurriculaAluByCurso.get(cursoMatriculado.getCurso().getId());
                if (cursoCurriAlu != null) {
                    cursoCurriAlu.setEstadoMatriculaEnum(cursoMatriculado.getEstadoEnum());
                    
                } else {
                    TipoCursoCurricula tipoCursoCurricula;
                    AlumnoCursoCurricula cursoOpcionalNew = new AlumnoCursoCurricula();
                    if (alumno.getModalidadEstudio().isPostgrado()) {
                        tipoCursoCurricula = mapTipoCursoCurricula.get(EAD);
                        cursoOpcionalNew.setNumeroCiclo(4);
                        
                    } else {
                        tipoCursoCurricula = mapTipoCursoCurricula.get(ELE);
                        cursoOpcionalNew.setNumeroCiclo(10);
                    }
                    cursoOpcionalCurriculas = cursoOpcionalCurriculas == null ? new ArrayList<>() : cursoOpcionalCurriculas;
                    Map<Long, CursoOpcionalCurricula> mapCursoOpcional = TypesUtil.convertListToMap("curso.id", cursoOpcionalCurriculas);
                    
                    CursoOpcionalCurricula cursoOpcionalCurricula = mapCursoOpcional.get(cursoMatriculado.getCurso().getId());
                    if (cursoOpcionalCurricula == null) {
                        CursoEquivalenteElectivo cursoEquivalenteElectivo = mapEquivalenteElectivo.get(cursoMatriculado.getCurso().getId());
                        if (cursoEquivalenteElectivo != null) {
                            tipoCursoCurricula = cursoEquivalenteElectivo.getCursoOpcionalCurricula().getTipoCursoCurricula();
                        }
                    } else {
                        tipoCursoCurricula = cursoOpcionalCurricula.getTipoCursoCurricula();
                    }
                    
                    cursoOpcionalNew.setAlumno(alumno);
                    cursoOpcionalNew.setTipoCursoCurricula(tipoCursoCurricula);
                    cursoOpcionalNew.setCurso(cursoMatriculado.getCurso());
                    cursoOpcionalNew.setCursoOpcional(cursoOpcionalCurricula);
                    cursoOpcionalNew.setEstadoMatriculaEnum(cursoMatriculado.getEstadoEnum());
                    cursoOpcionalNew.setEstadoRegistro(EstadoEnum.ACT.name());
                    Boolean aprobado = true;
                    if (tipoCursoCurricula.getCodigoEnum() == ELC && mapRequisitoCursoOpcionals != null) {
                        aprobado = validarCursosRequisitoOpcional(mapRequisitoCursoOpcionals, cursoOpcionalNew, alumnoCursoNew, alumno);
                    }
                    cursoOpcionalNew.setEstadoEnum(aprobado ? HAB : NREQ);
                    cursoOpcionalNew.setValidado(true);
                    cursoOpcionalNew.setVecesCursado(0);
                    cursoOpcionalNew.setCreditos(cursoMatriculado.getCreditos());
                    alumnoCursoNew.add(cursoOpcionalNew);
                    
                    cursoMatriculado.setTipoCursoCurricula(tipoCursoCurricula);
                }
            }
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void settingPlanCurricular(Alumno alumnoForm, PlanCurricular planBD) {
        Alumno alumnoUpd = new Alumno(alumnoForm.getId());
        alumnoUpd.setPlanCurricular(planBD);
        if (planBD != null && planBD.getOrientacionCarrera() != null) {
            alumnoUpd.setOrientacionCarrera(planBD.getOrientacionCarrera());
        }
        alumnoDAO.updateColumns(alumnoUpd, "planCurricular", "orientacionCarrera");
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
            List<AlumnoCursoCurricula> alumnoCursoCurriculaOld,
            List<CursoOpcionalCurricula> opcionalCurriculas,
            List<TipoCursoCurricula> tipoCursoCurriculas,
            Map<TipoCursoCurriculaEnum, ResumenPlanCurricular> mapResumenPlanCurricular,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            List<CursoEquivalenteElectivo> equivalenteElectivos,
            Map<Long, List<CursoOpcionalCurricula>> mapCursoOpcionalAll,
            List<PlanCurricular> planCurriculars,
            Map<Long, List<CursoCurricula>> mapCursoCurriculaAll,
            List<CursoHabilEscuela> habilEscuelas,
            Map<Long, List<RequisitoCursoOpcional>> mapRequisitoCursoOpcionals,
            DataSessionPivot ds) {
        
        Carrera carrera = alumno.getCarrera();
        if (alumno.getPlanCurricular() == null) {
            logger.debug("LE CAMBIAREMOS PLAN CURRICULAR");
            this.settingPlanCurricular(alumno, planBD);
        }
        
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
                    alumnoCursoCurriculaOld,
                    opcionalCurriculas,
                    tipoCursoCurriculas,
                    mapResumenPlanCurricular,
                    alumnoAvanceCurriculars,
                    equivalenteElectivos,
                    mapCursoOpcionalAll,
                    planCurriculars,
                    mapCursoCurriculaAll,
                    mapRequisitoCursoOpcionals,
                    ds, null);
        } else {
            
            this.procesarAlumnoSincronoEPG(
                    alumno,
                    mapCursoCurricula,
                    mapCursosVecesLlevado,
                    cursosMatriculados,
                    cursosAprobados,
                    opcionalCurriculas,
                    mapResumenPlanCurricular,
                    tipoCursoCurriculas,
                    alumnoAvanceCurriculars,
                    alumnoCursoCurriculaOld,
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
    public void procesarAlumnoSincronoEPG(
            Alumno alumno,
            Map<Long, CursoCurricula> mapCursosCurricula,
            Map<String, AlumnoCicloCurso> mapCursosVecesLlevado,
            List<MatriculaCurso> cursosMatriculados,
            List<AlumnoCicloCurso> cursosAprobados,
            List<CursoOpcionalCurricula> cursoOpcionaPlan,
            Map<TipoCursoCurriculaEnum, ResumenPlanCurricular> mapResumenPlanCurricular,
            List<TipoCursoCurricula> tipoCursoCurriculas,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            List<AlumnoCursoCurricula> alumnoCursoOld,
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
            cursosNew.setEstadoEnum(HAB);
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
        generarAvanceCurricularEpg(alumnoCursoElcCarreraNew, alumnoCursoNew, mapResumenPlanCurricular, tipoCursoCurriculas, alumnoAvanceCurriculars, alumno, mapCursosVecesLlevado);
        
        alumnoCursoOld = alumnoCursoOld == null ? new ArrayList<>() : alumnoCursoOld;
        for (AlumnoCursoCurricula alumnoCursoCurriculaNew : alumnoCursoNew) {
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
    
    private void generarAvanceCurricularEpg(
            List<AlumnoCursoCurricula> alumnoCursoElcCarreraNew,
            List<AlumnoCursoCurricula> alumnoCursoNew,
            Map<TipoCursoCurriculaEnum, ResumenPlanCurricular> mapResumenPlanCurricular,
            List<TipoCursoCurricula> tipoCursoCurriculas,
            List<AlumnoAvanceCurricular> alumnoAvanceCurriculars,
            Alumno alumno,
            Map<String, AlumnoCicloCurso> mapCursosVecesLlevado) {
        
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
        Collections.sort(alumnoCursoNew, new AlumnoCursoCurricula.CompareOrdenTipoCurr());
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
                
                ResumenPlanCurricular rpc = mapResumenPlanCurricular.get(curso.getTipoCursoCurricula().getCodigoEnum());
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
            Integer creditosDep = 0;
            Integer cursosDep = 0;
            
            AlumnoAvanceCurricular avance = avances.get(tipo.getCodigoEnum());
            if (avance == null) {
                avance = new AlumnoAvanceCurricular();
                avance.setTipoCursoCurricula(tipo);
                avance.setAlumno(alumno);
                avance.setCreditos(creditos.get(tipo.getCodigoEnum()) + creditosDep);
                avance.setCursos(cursos.get(tipo.getCodigoEnum()) + cursosDep);
                avanceCurricularDAO.save(avance);
                
            } else {
                AlumnoAvanceCurricular avanceUpd = new AlumnoAvanceCurricular(avance.getId());
                avanceUpd.setCreditos(creditos.get(tipo.getCodigoEnum()) + creditosDep);
                avanceUpd.setCursos(cursos.get(tipo.getCodigoEnum()) + cursosDep);
                avanceCurricularDAO.updateColumns(avance, "creditos", "cursos");
            }
            
            cred = cred + creditos.get(tipo.getCodigoEnum());
            cur = cur + cursos.get(tipo.getCodigoEnum());
            credCarrera = credCarrera + creditos.get(tipo.getCodigoEnum());
            curCarrera = +cursos.get(tipo.getCodigoEnum());
            
        }
        
        Alumno alumnoUpd = new Alumno(alumno.getId());
        alumnoUpd.setCursosCarreraAprobados(curCarrera);
        alumnoUpd.setCreditosCarreraAprobados(credCarrera);
        alumnoDAO.updateColumns(alumnoUpd, "cursosCarreraAprobados", "creditosCarreraAprobados");
    }
    
    private Boolean validarCursosRequisitoOpcional(
            Map<Long, List<RequisitoCursoOpcional>> mapRequisitoCursoOpcionals,
            AlumnoCursoCurricula cursosOpcionalesNew,
            List<AlumnoCursoCurricula> aluCursosCurriculaNew,
            Alumno alumno) {
        
        List<RequisitoCursoOpcional> requisitoCursoCurriculas = TypesUtil.getListNotNull(mapRequisitoCursoOpcionals.get(cursosOpcionalesNew.getCursoOpcional().getId()));
        CursoOpcionalCurricula cursoOpcionalCurricula = cursosOpcionalesNew.getCursoOpcional();
        if (requisitoCursoCurriculas.isEmpty()) {
            return validarCreditos(alumno, cursoOpcionalCurricula);
        }
        
        Map<Long, AlumnoCursoCurricula> mapAluCursoCurricula = TypesUtil.convertListToMap("curso.id", aluCursosCurriculaNew);
        Boolean requisitoOr = cursoOpcionalCurricula.getRequisitosOr();
        Integer countAprobados = 0;
        for (RequisitoCursoOpcional requisitoCursoCurricula : requisitoCursoCurriculas) {
            AlumnoCursoCurricula alumnoCursoCurricula = mapAluCursoCurricula.get(requisitoCursoCurricula.getCursoRequisito().getId());
            if (alumnoCursoCurricula != null
                    && estadosAprobados.contains(alumnoCursoCurricula.getEstadoEnum())) {
                countAprobados++;
            }
        }
        if (requisitoOr && validarCreditos(alumno, cursoOpcionalCurricula)) {
            return countAprobados >= 1;
        } else if (!requisitoOr && validarCreditos(alumno, cursoOpcionalCurricula)) {
            return countAprobados == requisitoCursoCurriculas.size();
        }
        return false;
    }
    
    private boolean validarCreditos(Alumno alumno, CursoOpcionalCurricula cursoOpcionalCurricula) {
        return cursoOpcionalCurricula.getCreditosRequisito() <= alumno.getCreditosAprobadosConvalidados();
    }
    
    private boolean validadSimultaneo(RequisitoCursoOpcional requisitoCursoCurricula, List<MatriculaCurso> cursosMatriculados) {
        if (requisitoCursoCurricula.getSimultaneo() == 0) {
            return true;
        }
        Map<Long, MatriculaCurso> map = TypesUtil.convertListToMap("curso.id", cursosMatriculados);
        Curso curso = requisitoCursoCurricula.getCursoRequisito();
        if (map.get(curso.getId()) != null) {
            return true;
        }
        return false;
    }
    
    private void printLogger(String log, boolean showLogger) {
        if (showLogger) {
            logger.info(log);
        }
    }
}

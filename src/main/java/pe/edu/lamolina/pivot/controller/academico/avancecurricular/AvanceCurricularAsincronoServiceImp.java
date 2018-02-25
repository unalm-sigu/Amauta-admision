package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import com.sun.media.sound.AlawCodec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.model.enums.AlumnoCursoSimultaneoEstadoEnum;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.APR;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.EQUIV;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.HAB;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.NREQ;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.SIM;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoCursoSimultaneo;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoSimultaneoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void procesarAlumno(AlumnoCiclo alumnoCiclo, DataSessionPivot ds) {

        List<AlumnoCursoSimultaneo> cursosSimultaneos = new ArrayList<>();

        AlumnoCiclo alumnoCicloBD = alumnoCicloDAO.find(alumnoCiclo.getId());
        List<CursoCurricula> cursosCurricula = alumnoCicloBD.getAlumno().getPlanCurricular().getCursoCurricula();
        Map<Long, CursoCurricula> cursosCurriculaById = cursosCurricula
                .stream()
                .filter(p -> p.getCurso() != null)
                .collect(Collectors.toMap(x -> x.getId(), x -> x, (a, b) -> a));

        Map<Long, List<RequisitoCursoCurricula>> requisitosByCursoCurricula = cursosCurricula
                .stream()
                .filter(p -> p.getRequisitosCursoCurricula() != null)
                .collect(Collectors.toMap(x -> x.getId(), x -> x.getCursosCurricula(), (a, b) -> a));

        List<AlumnoCursoCurricula> alumnoCursoCurriculas;
        Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurriculaByCurso = new HashMap<>();
        Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurriculaByCursoCurricula = new HashMap<>();

        int creditosAproboados = alumnoCicloBD.getAlumno().getCreditosAprobados();
        int creditosCurriculaAprobados = alumnoCicloBD.getAlumno().getCreditosCarreraAprobados();

        alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allNoOpcionalByAlumno(alumnoCicloBD.getAlumno());
        mapAlumnoCursoCurriculaByCurso.clear();
        mapAlumnoCursoCurriculaByCursoCurricula.clear();

        for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoCurriculas) {
            alumnoCursoCurricula.setValidado(false);
            mapAlumnoCursoCurriculaByCurso.put(alumnoCursoCurricula.getCurso().getId(), alumnoCursoCurricula);
            mapAlumnoCursoCurriculaByCursoCurricula.put(alumnoCursoCurricula.getCursoCurricula().getId(), alumnoCursoCurricula);
        }

        sincronizarConCurricula(cursosCurriculaById, mapAlumnoCursoCurriculaByCursoCurricula, alumnoCicloBD.getAlumno());

        validarCreditosAprobados(cursosCurriculaById, mapAlumnoCursoCurriculaByCursoCurricula.values(), creditosAproboados, creditosCurriculaAprobados);
        validarHistorial(mapAlumnoCursoCurriculaByCurso, alumnoCicloBD.getAlumno());
        validarCursosRequisito(requisitosByCursoCurricula, mapAlumnoCursoCurriculaByCursoCurricula, ds);
        validarCursosSimultaneo(requisitosByCursoCurricula, mapAlumnoCursoCurriculaByCursoCurricula, cursosSimultaneos, ds);

        for (AlumnoCursoCurricula alumnoCursoCurricula : mapAlumnoCursoCurriculaByCursoCurricula.values()) {
            alumnoCursoCurriculaDAO.save(alumnoCursoCurricula);
        }
        for (AlumnoCursoSimultaneo cursosSimultaneo : cursosSimultaneos) {
            alumnoCursoSimultaneoDAO.save(cursosSimultaneo);
        }
    }

    private void validarHistorial(Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurriculaByCurso, Alumno alumno) {

        Map<Long, AlumnoCicloCurso> mapAlumnoCicloCursoAprobadoByCurso = alumnoCicloCursoDAO.allAprobadoActivoByAlumno(alumno)
                .stream()
                .filter(x -> x.getCurso() != null)
                .collect(Collectors.toMap(x -> x.getCurso().getId(), x -> x, (a, b) -> a));

        for (AlumnoCicloCurso cursoAprobado : mapAlumnoCicloCursoAprobadoByCurso.values()) {
            AlumnoCursoCurricula alumnoCursoCurricula = mapAlumnoCursoCurriculaByCurso.get(cursoAprobado.getCurso().getId());
            if (alumnoCursoCurricula == null) {
                continue;
            }
            alumnoCursoCurricula.setEstado(APR.name());
            alumnoCursoCurricula.setCicloAprobado(cursoAprobado.getAlumnoCiclo().getCicloAcademico());
            alumnoCursoCurricula.setCreditos(cursoAprobado.getCreditos());
            alumnoCursoCurricula.setNota(cursoAprobado.getNota());
            alumnoCursoCurricula.setValidado(true);
        }

        for (AlumnoCursoCurricula alumnoCursoCurricula : mapAlumnoCursoCurriculaByCurso.values()) {
            alumnoCursoCurricula.setVecesCursado(alumnoCicloCursoDAO.countByCursoAlumno(alumnoCursoCurricula.getCurso(), alumno).intValue());
        }
    }

    private void sincronizarConCurricula(Map<Long, CursoCurricula> cursosCurriculaById, Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurriculaByCursoCurricula, Alumno alumno) {
        sincronizarCursosEliminados(cursosCurriculaById, mapAlumnoCursoCurriculaByCursoCurricula);
        sincronizarCursosAgregados(cursosCurriculaById, mapAlumnoCursoCurriculaByCursoCurricula, alumno);
    }

    private void sincronizarCursosEliminados(Map<Long, CursoCurricula> cursosCurricula, Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurriculaByCursoCurricula) {
        for (Map.Entry<Long, AlumnoCursoCurricula> entry : mapAlumnoCursoCurriculaByCursoCurricula.entrySet()) {
            if (!cursosCurricula.containsKey(entry.getKey())) {
                mapAlumnoCursoCurriculaByCursoCurricula.remove(entry.getKey());
            }
        }
    }

    private void sincronizarCursosAgregados(Map<Long, CursoCurricula> cursosCurricula, Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurriculaByCursoCurricula, Alumno alumno) {
        for (Map.Entry<Long, CursoCurricula> entry : cursosCurricula.entrySet()) {
            if (!mapAlumnoCursoCurriculaByCursoCurricula.containsKey(entry.getKey())) {
                Curso curso = entry.getValue().getCurso();
                AlumnoCursoCurricula nuevoCursoAlumno = new AlumnoCursoCurricula();
                nuevoCursoAlumno.setAlumno(alumno);
                nuevoCursoAlumno.setCicloAprobado(null);
                nuevoCursoAlumno.setCreditos(entry.getValue().getCreditos());
                nuevoCursoAlumno.setCurso(curso);
                nuevoCursoAlumno.setCursoCurricula(entry.getValue());
                nuevoCursoAlumno.setEstado(NREQ.name());
                nuevoCursoAlumno.setNota(null);
                nuevoCursoAlumno.setValidado(false);
                nuevoCursoAlumno.setVecesCursado(0);

                mapAlumnoCursoCurriculaByCursoCurricula.put(nuevoCursoAlumno.getCursoCurricula().getId(), nuevoCursoAlumno);
            }
        }

    }

    private void validarCreditosAprobados(Map<Long, CursoCurricula> requisitos, Collection<AlumnoCursoCurricula> alumnoCursos, int creditosAprobados, int creditosCurriculaAprobados) {

        for (AlumnoCursoCurricula alumnoCurso : alumnoCursos) {

            if (alumnoCurso.isValidado()) {
                continue;
            }

            Long idCurso = alumnoCurso.getCursoCurricula().getId();

            Integer creditosAprobadosRequisito = requisitos.get(idCurso).getCreditosRequisito() != null ? requisitos.get(idCurso).getCreditosRequisito() : 0;
            Integer credidosCurriculaRequisito = requisitos.get(idCurso).getCreditosCurriculaRequisito() != null ? requisitos.get(idCurso).getCreditosCurriculaRequisito() : 0;

            if (creditosAprobadosRequisito > creditosAprobados || credidosCurriculaRequisito > creditosCurriculaAprobados) {
                alumnoCurso.setEstado(NREQ.name());
                alumnoCurso.setValidado(true);
            }
        }

    }

    private void validarCursosRequisito(Map<Long, List<RequisitoCursoCurricula>> requisitosPorCurso, Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurriculaByCursoCurricula, DataSessionPivot ds) {

        for (Map.Entry<Long, AlumnoCursoCurricula> entry : mapAlumnoCursoCurriculaByCursoCurricula.entrySet()) {

            AlumnoCursoCurricula evaluado = entry.getValue();

            if (evaluado.isValidado() || evaluado.getEstadoEnum() == APR) {
                continue;
            }

            List<RequisitoCursoCurricula> requisitos = requisitosPorCurso.get(entry.getKey());

            if (cumpleRequisitos(requisitos, mapAlumnoCursoCurriculaByCursoCurricula, evaluado, ds)) {
                evaluado.setEstado(HAB.name());
            }
        }

    }

    private void validarCursosSimultaneo(Map<Long, List<RequisitoCursoCurricula>> requisitosPorCurso, Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurriculaByCursoCurricula, List<AlumnoCursoSimultaneo> cursosSimultaneo, DataSessionPivot ds) {

        for (Map.Entry<Long, AlumnoCursoCurricula> entry : mapAlumnoCursoCurriculaByCursoCurricula.entrySet()) {

            AlumnoCursoCurricula evaluado = entry.getValue();

            if (evaluado.isValidado() || evaluado.getEstadoEnum() == APR) {
                continue;
            }

            List<RequisitoCursoCurricula> requisitos = requisitosPorCurso.get(entry.getKey());
            List<AlumnoCursoSimultaneo> requisitosSimultaneo = new ArrayList<>();
            
            if (validarSimultaneos(requisitosSimultaneo, requisitos, mapAlumnoCursoCurriculaByCursoCurricula, evaluado, ds)) {
                evaluado.setEstado(SIM.name());
                cursosSimultaneo.addAll(requisitosSimultaneo);
                evaluado.setValidado(true);
            }else{
                evaluado.setEstado(NREQ.name());
                evaluado.setValidado(true);
            }
        }
        
    }

     private boolean validarSimultaneos(List<AlumnoCursoSimultaneo> simultaneos, List<RequisitoCursoCurricula> requisitos, Map<Long, AlumnoCursoCurricula> cursos, AlumnoCursoCurricula evaluado, DataSessionPivot ds) {
        boolean requisitosCumplidos = true;

        for (RequisitoCursoCurricula requisito : requisitos) {
            if(requisito.getSimultaneo() == 0){
                logger.debug("pasando");
                continue;
            }
            AlumnoCursoCurricula cursoRequisito = cursos.get(requisito.getCursoRequisito().getId());
            if (cursoRequisito == null || cursoRequisito.getEstadoEnum() == NREQ) {
                logger.debug("No cumple los rquisitos del curso en simultaneo");
                requisitosCumplidos = false;
                break;
            } else {
                logger.debug(("Agregando curso simultaneo"));
                AlumnoCursoSimultaneo alumnoCursoSimultaneo = new AlumnoCursoSimultaneo();
                alumnoCursoSimultaneo.setAlumnoCursoCurricula(evaluado);
                alumnoCursoSimultaneo.setCurso(requisito.getCursoRequisito().getCurso());
                alumnoCursoSimultaneo.setEstado(AlumnoCursoSimultaneoEstadoEnum.NMAT);
                alumnoCursoSimultaneo.setFechaRegistro(new Date());
                alumnoCursoSimultaneo.setUserRegistro(ds.getUsuario());
                simultaneos.add(alumnoCursoSimultaneo);
            }

        }

        return requisitosCumplidos;
    }
     
     
    private boolean cumpleRequisitos(List<RequisitoCursoCurricula> requisitos, Map<Long, AlumnoCursoCurricula> cursos, AlumnoCursoCurricula evaluado, DataSessionPivot ds) {
        boolean requisitosCumplidos = true;

        for (RequisitoCursoCurricula requisito : requisitos) {
            if(requisito.getSimultaneo() == 1) continue;
            AlumnoCursoCurricula cursoRequisito = cursos.get(requisito.getCursoRequisito().getId());
            if (cursoRequisito == null || (cursoRequisito.getEstadoEnum() != APR && cursoRequisito.getEstadoEnum() != EQUIV)) {
                requisitosCumplidos = false;
                break;
            }

        }

        return requisitosCumplidos;
    }

}

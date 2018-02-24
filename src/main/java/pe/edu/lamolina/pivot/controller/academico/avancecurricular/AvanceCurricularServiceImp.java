package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;

@Service
@Transactional(readOnly = true)
public class AvanceCurricularServiceImp implements AvanceCurricularService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @Override
    @Transactional
    public void procesarAlumnos(PlanCurricular planCurricular, CicloAcademico cicloAcademico) {
        PlanCurricular planBD = planCurricularDAO.find(planCurricular.getId());
        CicloAcademico cicloBD = cicloAcademicoDAO.find(cicloAcademico.getId());

        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allByCicloAcademicoPlanCurricular(planBD, cicloBD);


        List<CursoCurricula> cursosCurricula = planBD.getCursoCurricula();

        Map<Long, CursoCurricula> cursosCurriculaById = cursosCurricula
                .stream()
                .filter(p -> p.getCurso() != null)
                .collect(Collectors.toMap(x -> x.getId(), x -> x, (a, b) -> a));

        Map<Long, List<RequisitoCursoCurricula>> requisitosByCursoCurricula = cursosCurricula
                .stream()
                .filter(p -> p.getRequisitosCursoCurricula() != null)
                .collect(Collectors.toMap(x -> x.getId(), x -> x.getRequisitosCursoCurricula(), (a, b) -> a));

        List<AlumnoCursoCurricula> alumnoCursoCurriculas;
        Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurriculaByCurso = new HashMap<>();
        Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurriculaByCursoCurricula = new HashMap<>();

        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            int creditosAproboados = alumnoCiclo.getAlumno().getCreditosAprobados();
            int creditosCurriculaAprobados = alumnoCiclo.getAlumno().getCreditosCarreraAprobados();

            alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allNoOpcionalByAlumno(alumnoCiclo.getAlumno());
            mapAlumnoCursoCurriculaByCurso.clear();
            mapAlumnoCursoCurriculaByCursoCurricula.clear();

            for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoCurriculas) {
                alumnoCursoCurricula.setValidado(false);
                mapAlumnoCursoCurriculaByCurso.put(alumnoCursoCurricula.getCurso().getId(), alumnoCursoCurricula);
                mapAlumnoCursoCurriculaByCursoCurricula.put(alumnoCursoCurricula.getCursoCurricula().getId(), alumnoCursoCurricula);
            }

            sincronizarConCurricula(cursosCurriculaById, mapAlumnoCursoCurriculaByCursoCurricula, alumnoCiclo.getAlumno());

            validarHistorial(mapAlumnoCursoCurriculaByCurso, alumnoCiclo.getAlumno());
            validarCreditosAprobados(cursosCurriculaById, mapAlumnoCursoCurriculaByCursoCurricula.values(), creditosAproboados, creditosCurriculaAprobados);
            validarCursosRequisito(requisitosByCursoCurricula, mapAlumnoCursoCurriculaByCursoCurricula);
            validarEstenValidados(mapAlumnoCursoCurriculaByCursoCurricula.values());

            for (AlumnoCursoCurricula alumnoCursoCurricula : mapAlumnoCursoCurriculaByCursoCurricula.values()) {
                alumnoCursoCurriculaDAO.save(alumnoCursoCurricula);
            }

        }

    }

    private void validarHistorial(Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurriculaByCurso, Alumno alumno) {

        Map<Long, AlumnoCicloCurso> mapAlumnoCicloCursoAprobadoByCurso = alumnoCicloCursoDAO.allAprobadoActivoByAlumno(alumno)
                .stream()
                .filter(x -> x.getCurso() != null)
                .collect(Collectors.toMap(x -> x.getCurso().getId(), x -> x, (a, b) -> a));

        int contNoMatricula = 0;
        for (AlumnoCicloCurso cursoAprobado : mapAlumnoCicloCursoAprobadoByCurso.values()) {
            AlumnoCursoCurricula alumnoCursoCurricula = mapAlumnoCursoCurriculaByCurso.get(cursoAprobado.getCurso().getId());
            if (alumnoCursoCurricula == null) {
                contNoMatricula++;
                continue;
            }
            alumnoCursoCurricula.setEstado(CursoCurriculaEstadoEnum.APR.name());
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
                nuevoCursoAlumno.setEstado(CursoCurriculaEstadoEnum.NREQ.name());
                nuevoCursoAlumno.setNota(null);
                nuevoCursoAlumno.setValidado(false);
                nuevoCursoAlumno.setVecesCursado(0);

                mapAlumnoCursoCurriculaByCursoCurricula.put(nuevoCursoAlumno.getCursoCurricula().getId(), nuevoCursoAlumno);
            }
        }

    }

    private void validarCreditosAprobados(Map<Long, CursoCurricula> requisitos, Collection<AlumnoCursoCurricula> alumnoCursos, int creditosAprobados, int creditosCurriculaAprobados) {

        int cont = 0;
        int contYaValid = 0;
        int contNoValid = 0;
        int contValid = 0;


        for (AlumnoCursoCurricula alumnoCurso : alumnoCursos) {

            if (alumnoCurso.isValidado()) {
                contYaValid++;
                continue;
            }

            Long idCurso = alumnoCurso.getCursoCurricula().getId();

            Integer creditosAprobadosRequisito = requisitos.get(idCurso).getCreditosRequisito() != null ? requisitos.get(idCurso).getCreditosRequisito() : 0;
            Integer credidosCurriculaRequisito = requisitos.get(idCurso).getCreditosCurriculaRequisito() != null ? requisitos.get(idCurso).getCreditosCurriculaRequisito() : 0;

            if (creditosAprobadosRequisito <= creditosAprobados && credidosCurriculaRequisito <= creditosCurriculaAprobados) {
                contValid++;
                alumnoCurso.setEstado(CursoCurriculaEstadoEnum.HAB.name());
                alumnoCurso.setValidado(true);
            } else {
                contNoValid++;
            }
        }

    }

    private void validarCursosRequisito(Map<Long, List<RequisitoCursoCurricula>> requisitosPorCurso, Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurriculaByCursoCurricula) {

        for (Map.Entry<Long, AlumnoCursoCurricula> entry : mapAlumnoCursoCurriculaByCursoCurricula.entrySet()) {

            AlumnoCursoCurricula evaluado = entry.getValue();

            if (evaluado.isValidado() || evaluado.getEstadoEnum() == CursoCurriculaEstadoEnum.APR) {
                continue;
            }

            List<RequisitoCursoCurricula> requisitos = requisitosPorCurso.get(entry.getKey());

            if (cumpleRequisitos(requisitos, mapAlumnoCursoCurriculaByCursoCurricula, evaluado)) {
                evaluado.setEstado(CursoCurriculaEstadoEnum.HAB.name());
                evaluado.setValidado(true);
            }
        }

    }

    private boolean cumpleRequisitos(List<RequisitoCursoCurricula> requisitos, Map<Long, AlumnoCursoCurricula> cursos, AlumnoCursoCurricula evaluado) {
        boolean requisitosCumplidos = true;

        for (RequisitoCursoCurricula requisito : requisitos) {
            AlumnoCursoCurricula cursoRequisito = cursos.get(requisito.getCursoRequisito().getId());
            if (cursoRequisito == null || cursoRequisito.getCicloAprobado() == null) {
                requisitosCumplidos = false;
                break;
            }
        }

        return requisitosCumplidos;
    }

    private void validarEstenValidados(Collection<AlumnoCursoCurricula> alumnoCursoCurriculas) {
        for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoCurriculas) {
            if (!alumnoCursoCurricula.isValidado()) {
                alumnoCursoCurricula.setValidado(true);
                alumnoCursoCurricula.setEstado(CursoCurriculaEstadoEnum.NREQ.name());
            }
        }
    }

}

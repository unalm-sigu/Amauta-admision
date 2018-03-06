package pe.edu.lamolina.pivot.controller.academico.avancecurricular;

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
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.CursoEquivalente;
import pe.edu.lamolina.model.academico.RequisitoCursoCurricula;
import pe.edu.lamolina.model.academico.TipoCursoCurricula;
import pe.edu.lamolina.model.enums.AlumnoCursoSimultaneoEstadoEnum;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.APR;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.EQUIV;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.HAB;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.NREQ;
import static pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum.SIM;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.model.matricula.AlumnoAvanceCurricular;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.model.matricula.AlumnoCursoSimultaneo;
import pe.edu.lamolina.pivot.dao.academico.AlumnoAvanceCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoSimultaneoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoEquivalenteDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.academico.RequisitoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoCursoCurriculaDAO;
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
    AlumnoAvanceCurricularDAO avanceCurricularDAO;

    @Autowired
    TipoCursoCurriculaDAO tipoCursoCurriculaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAllAlumnoCursoSimultaneoByAlumno(Alumno alumno) {
        alumnoCursoSimultaneoDAO.deleteAllByAlumno(alumno);
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void procesarAlumno(Alumno alumno, DataSessionPivot ds) {

        List<AlumnoCursoSimultaneo> cursosSimultaneos = new ArrayList<>();

        Alumno alumnoBD = alumnoDAO.find(alumno.getId());
        Map<Long, CursoCurricula> cursosCurricula = alumnoBD.getPlanCurricular().getCursoCurricula()
                .stream()
                .filter(p -> p.getCurso() != null)
                .collect(Collectors.toMap(x -> x.getId(), x -> x, (a, b) -> a));

        List<AlumnoCursoCurricula> alumnoCursoCurriculas;
        Map<Long, AlumnoCursoCurricula> mapAlumnoCursoCurriculaByCurso = new HashMap<>();

        Map<Long, AlumnoCursoCurricula> cursosAlumno = new HashMap<>();

        int creditosAproboados = alumnoBD.getCreditosAprobados();
        int creditosCurriculaAprobados = alumnoBD.getCreditosCarreraAprobados();

        alumnoCursoCurriculas = alumnoCursoCurriculaDAO.allNoOpcionalByAlumno(alumnoBD);

        for (AlumnoCursoCurricula alumnoCursoCurricula : alumnoCursoCurriculas) {
            alumnoCursoCurricula.setValidado(false);
            mapAlumnoCursoCurriculaByCurso.put(alumnoCursoCurricula.getCurso().getId(), alumnoCursoCurricula);
            cursosAlumno.put(alumnoCursoCurricula.getCursoCurricula().getId(), alumnoCursoCurricula);
        }

        sincronizarConCurricula(cursosCurricula, mapAlumnoCursoCurriculaByCurso, cursosAlumno, alumnoBD);

        validarCreditosAprobados(cursosCurricula, cursosAlumno.values(), creditosAproboados, creditosCurriculaAprobados);
        validarEquivalencias(cursosAlumno, alumno);
        validarHistorial(mapAlumnoCursoCurriculaByCurso, alumnoBD);

        validarCursosRequisito(cursosCurricula, cursosAlumno, ds);
        validarCursosSimultaneo(cursosCurricula, cursosAlumno, cursosSimultaneos, ds);

        for (AlumnoCursoCurricula alumnoCursoCurricula : cursosAlumno.values()) {
            alumnoCursoCurriculaDAO.save(alumnoCursoCurricula);
        }
        for (AlumnoCursoSimultaneo cursosSimultaneo : cursosSimultaneos) {
            alumnoCursoSimultaneoDAO.save(cursosSimultaneo);
        }

        generarAvanceCurricular(cursosAlumno.values(), alumnoBD);
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

        Map<TipoCursoCurriculaEnum, Integer> creditos = new HashMap<>();
        Map<TipoCursoCurriculaEnum, Integer> cursos = new HashMap<>();

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
            if(avance == null){
                avance = new AlumnoAvanceCurricular();
                avance.setTipoCursoCurricula(tipo);
                avance.setAlumno(alumno);
            }
            avance.setCreditos(creditos.get(tipo.getCodigoEnum()));
            avance.setCursos(cursos.get(tipo.getCodigoEnum()));
            
            avanceCurricularDAO.save(avance);
        }

    }
    
    private void validarEquivalencias(Map<Long, AlumnoCursoCurricula> cursosAlumno, Alumno alumno) {

        Map<Long, AlumnoCicloCurso> cursosAprobados = alumnoCicloCursoDAO.allAprobadoActivoByAlumno(alumno)
                .stream()
                .filter(x -> x.getCurso() != null)
                .collect(Collectors.toMap(x -> x.getCurso().getId(), x -> x, (a, b) -> a));

        for (Map.Entry<Long, AlumnoCursoCurricula> entry : cursosAlumno.entrySet()) {
            AlumnoCursoCurricula cursoAlumno = entry.getValue();
            CursoCurricula cursoEvaluado = entry.getValue().getCursoCurricula();

            List<CursoEquivalente> cursosEquivalentes = cursoEquivalenteDAO.allActivoByCursoCurricula(cursoEvaluado);

            if (cursosEquivalentes.isEmpty()) {
                continue;
            }

            Map<Integer, List<CursoEquivalente>> grupos = new HashMap<>();
            for (CursoEquivalente cursoEquivalente : cursosEquivalentes) {
                Integer grupo = cursoEquivalente.getGrupo();
                if (!grupos.containsKey(grupo)) {
                    grupos.put(grupo, new ArrayList<>());
                }
                grupos.get(grupo).add(cursoEquivalente);
            }

            for (Map.Entry<Integer, List<CursoEquivalente>> entryGrupos : grupos.entrySet()) {
                boolean equivalenciaEncontrada = true;
                List<CursoEquivalente> listCursosEquivalentes = entryGrupos.getValue();

                for (CursoEquivalente cursoEq : listCursosEquivalentes) {
                    if (!cursosAprobados.containsKey(cursoEq.getId())) {
                        equivalenciaEncontrada = false;
                        break;
                    }
                }

                if (equivalenciaEncontrada) {
                    cursoAlumno.setEstado(CursoCurriculaEstadoEnum.EQUIV.name());
                    cursoAlumno.setValidado(true);
                    break;
                }

            }
        }
    }

    private void validarHistorial(Map<Long, AlumnoCursoCurricula> cursosAlumnoByIdCurso, Alumno alumno) {

        Map<Long, AlumnoCicloCurso> cursosAprobados = alumnoCicloCursoDAO.allAprobadoActivoByAlumno(alumno)
                .stream()
                .filter(x -> x.getCurso() != null)
                .collect(Collectors.toMap(x -> x.getCurso().getId(), x -> x, (a, b) -> a));

        for (AlumnoCicloCurso cursoAprobado : cursosAprobados.values()) {
            AlumnoCursoCurricula alumnoCursoCurricula = cursosAlumnoByIdCurso.get(cursoAprobado.getCurso().getId());
            if (alumnoCursoCurricula == null) {
                continue;
            }
            alumnoCursoCurricula.setEstado(APR.name());
            alumnoCursoCurricula.setCicloAprobado(cursoAprobado.getAlumnoCiclo().getCicloAcademico());
            alumnoCursoCurricula.setCreditos(cursoAprobado.getCreditos());
            alumnoCursoCurricula.setNota(cursoAprobado.getNota());
            alumnoCursoCurricula.setValidado(true);
        }

        for (AlumnoCursoCurricula alumnoCursoCurricula : cursosAlumnoByIdCurso.values()) {
            alumnoCursoCurricula.setVecesCursado(alumnoCicloCursoDAO.countByCursoAlumno(alumnoCursoCurricula.getCurso(), alumno).intValue());
        }
    }

    private void sincronizarConCurricula(Map<Long, CursoCurricula> cursosCurriculaById, Map<Long, AlumnoCursoCurricula> cursosCurriculaByCurso, Map<Long, AlumnoCursoCurricula> cursosAlumno, Alumno alumno) {
        sincronizarCursosEliminados(cursosCurriculaById, cursosAlumno);
        sincronizarCursosAgregados(cursosCurriculaById, cursosCurriculaByCurso, cursosAlumno, alumno);
    }

    private void sincronizarCursosEliminados(Map<Long, CursoCurricula> cursosCurricula, Map<Long, AlumnoCursoCurricula> cursosAlumno) {
        for (Map.Entry<Long, AlumnoCursoCurricula> entry : cursosAlumno.entrySet()) {
            if (!cursosCurricula.containsKey(entry.getKey())) {
                cursosAlumno.remove(entry.getKey());
            }
        }
    }

    private void sincronizarCursosAgregados(Map<Long, CursoCurricula> cursosCurricula, Map<Long, AlumnoCursoCurricula> cursosCurriculaByCurso, Map<Long, AlumnoCursoCurricula> cursosAlumno, Alumno alumno) {
        for (Map.Entry<Long, CursoCurricula> entry : cursosCurricula.entrySet()) {

            if (!cursosAlumno.containsKey(entry.getKey())) {
                Curso curso = entry.getValue().getCurso();
                AlumnoCursoCurricula nuevoCursoAlumno = new AlumnoCursoCurricula();
                nuevoCursoAlumno.setAlumno(alumno);
                nuevoCursoAlumno.setCicloAprobado(null);
                nuevoCursoAlumno.setCreditos(entry.getValue().getCreditos());
                nuevoCursoAlumno.setCurso(curso);
                nuevoCursoAlumno.setNumeroCiclo(entry.getValue().getNumeroCiclo());
                nuevoCursoAlumno.setCursoCurricula(entry.getValue());
                nuevoCursoAlumno.setEstado(NREQ.name());
                nuevoCursoAlumno.setNota(null);
                nuevoCursoAlumno.setValidado(false);
                nuevoCursoAlumno.setVecesCursado(0);

                cursosCurriculaByCurso.put(nuevoCursoAlumno.getCursoCurricula().getCurso().getId(), nuevoCursoAlumno);
                cursosAlumno.put(nuevoCursoAlumno.getCursoCurricula().getId(), nuevoCursoAlumno);
            } else {
                cursosAlumno.get(entry.getKey()).setNumeroCiclo(entry.getValue().getNumeroCiclo());
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

    private void validarCursosRequisito(Map<Long, CursoCurricula> cursosCurricula, Map<Long, AlumnoCursoCurricula> cursosAlumno, DataSessionPivot ds) {

        for (Map.Entry<Long, AlumnoCursoCurricula> entry : cursosAlumno.entrySet()) {

            AlumnoCursoCurricula evaluado = entry.getValue();

            if (evaluado.isValidado() || evaluado.getEstadoEnum() == APR || evaluado.getEstadoEnum() == EQUIV) {
                continue;
            }

            List<RequisitoCursoCurricula> requisitos = cursosCurricula.get(evaluado.getCursoCurricula().getId()).getCursosCurricula();
            if (cumpleRequisitos(requisitos, cursosAlumno, evaluado, ds)) {
                evaluado.setEstado(HAB.name());
            } else {
                evaluado.setEstado(NREQ.name());
                evaluado.setValidado(true);
            }
        }

    }

    private boolean cumpleRequisitos(List<RequisitoCursoCurricula> requisitos, Map<Long, AlumnoCursoCurricula> cursos, AlumnoCursoCurricula evaluado, DataSessionPivot ds) {
        boolean requisitosCumplidos = true;

        for (RequisitoCursoCurricula requisito : requisitos) {
            if (requisito.getSimultaneo() == 1) {
                continue;
            }
            AlumnoCursoCurricula cursoRequisito = cursos.get(requisito.getCursoRequisito().getId());
            if (cursoRequisito == null || (cursoRequisito.getEstadoEnum() != APR && cursoRequisito.getEstadoEnum() != EQUIV)) {
                requisitosCumplidos = false;
                break;
            }

        }

        return requisitosCumplidos;
    }

    private void validarCursosSimultaneo(Map<Long, CursoCurricula> cursosCurricula, Map<Long, AlumnoCursoCurricula> cursosAlumno, List<AlumnoCursoSimultaneo> cursosSimultaneo, DataSessionPivot ds) {

        for (Map.Entry<Long, AlumnoCursoCurricula> entry : cursosAlumno.entrySet()) {

            AlumnoCursoCurricula evaluado = entry.getValue();

            if (evaluado.getEstadoEnum() != HAB) {
                continue;
            }

            List<RequisitoCursoCurricula> requisitos = cursosCurricula.get(evaluado.getCursoCurricula().getId()).getCursosCurricula();

            List<AlumnoCursoSimultaneo> requisitosSimultaneo = new ArrayList<>();

            if (validarSimultaneos(requisitosSimultaneo, requisitos, cursosAlumno, evaluado, ds)) {
                if (requisitosSimultaneo.size() > 0) {
                    evaluado.setEstado(SIM.name());
                    cursosSimultaneo.addAll(requisitosSimultaneo);
                } else {
                    evaluado.setEstado(HAB.name());
                }
            } else {
                evaluado.setEstado(NREQ.name());
            }
            evaluado.setValidado(true);
        }

    }

    private boolean validarSimultaneos(List<AlumnoCursoSimultaneo> simultaneos, List<RequisitoCursoCurricula> requisitos, Map<Long, AlumnoCursoCurricula> cursos, AlumnoCursoCurricula evaluado, DataSessionPivot ds) {
        boolean requisitosCumplidos = true;

        for (RequisitoCursoCurricula requisito : requisitos) {

            if (requisito.getSimultaneo() == 0) {
                continue;
            }

            AlumnoCursoCurricula cursoRequisito = cursos.get(requisito.getCursoRequisito().getId());

            if (cursoRequisito == null) {
            }

            if (cursoRequisito.getEstadoEnum() == APR) {
            } else if (cursoRequisito.getEstadoEnum() == HAB) {
                AlumnoCursoSimultaneo alumnoCursoSimultaneo = new AlumnoCursoSimultaneo();
                alumnoCursoSimultaneo.setAlumnoCursoCurricula(evaluado);
                alumnoCursoSimultaneo.setCurso(requisito.getCursoRequisito().getCurso());
                alumnoCursoSimultaneo.setEstado(AlumnoCursoSimultaneoEstadoEnum.NMAT);
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

}

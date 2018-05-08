package pe.edu.lamolina.pivot.controller.academico.infoacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCurricula;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.PlanCurricular;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.pivot.controller.academico.avancecurricular.AvanceCurricularService;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCurricularDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class infoAcademicoServiceImpl implements infoAcademicoService {

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

    @Override
    public ObjectNode allAlumnosByCiclo(Alumno alumno, Long numeroCiclo) {
        ArrayNode lstCiclos = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode lstCursos = new ArrayNode(JsonNodeFactory.instance);
        ObjectNode objNodeCursos = new ObjectNode(JsonNodeFactory.instance);
        alumno = alumnoDAO.findAllInfo(alumno.getId());

        if (alumno.getPlanCurricular() != null) {
            List<CursoCurricula> cursoCurriculas = cursoCurriculaDAO.allByPlanCurricularNroCiclo(alumno.getPlanCurricular(), numeroCiclo.intValue());
            List<AlumnoCursoCurricula> lst = alumnoCursoCurriculaDAO.allByAlumnoCursosCurricula(alumno, cursoCurriculas);
            if (numeroCiclo == 1l) {
                List<AlumnoCursoCurricula> ciclosAlumno = alumnoCursoCurriculaDAO.allCiclosAlumno(alumno);

                Map<Integer, Long> counters = ciclosAlumno.stream()
                        .collect(Collectors.groupingBy(c -> c.getNumeroCiclo(),
                                Collectors.counting()));

                for (Map.Entry<Integer, Long> entry : counters.entrySet()) {
                    ObjectNode objCiclo = new ObjectNode(JsonNodeFactory.instance);
                    objCiclo.put("numeroRoman", NumberFormat.roman(entry.getKey()));
                    objCiclo.put("cantidad", "(" + entry.getValue() + ")");
                    objCiclo.put("numero", entry.getKey());
                    lstCiclos.add(objCiclo);
                }

                objNodeCursos.put("ciclos", lstCiclos);
            }

            for (AlumnoCursoCurricula alumnoCursoCurricula : lst) {
                ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
                objNode.put("numeroCiclo", alumnoCursoCurricula.getNumeroCiclo());
                objNode.put("estado", CursoCurriculaEstadoEnum.getNombreAndName(alumnoCursoCurricula.getEstado()));
                objNode.put("codigo", alumnoCursoCurricula.getCurso().getCodigo());
                objNode.put("codigoAnterior", alumnoCursoCurricula.getCurso().getCodigoAnterior1());
                objNode.put("tipoCurso", alumnoCursoCurricula.getCurso() == null ? "" : TipoCursoCurriculaEnum.getNombre(alumnoCursoCurricula.getCursoCurricula().getTipoCursoCurricula().getNombre()));
                objNode.put("vecesCursado", alumnoCursoCurricula.getVecesCursado().toString().equals("0") ? "" : alumnoCursoCurricula.getVecesCursado().toString());
                objNode.put("nombre", alumnoCursoCurricula.getCurso().getNombre());
                objNode.put("nota", alumnoCursoCurricula.getNota());
                objNode.put("creditos", alumnoCursoCurricula.getCreditos());
                objNode.put("descripcion", alumnoCursoCurricula.getCicloAprobado() == null ? "" : alumnoCursoCurricula.getCicloAprobado().getDescripcion());
                lstCursos.add(objNode);
            }
        }
        objNodeCursos.put("cursos", lstCursos);

        return objNodeCursos;
    }

    @Override
    public ObjectNode allAlumnosByCursosMatri(Alumno alumno, CicloAcademico cicloAca) {

        List<Seccion> secciones = new ArrayList();
        Map<Long, Seccion> mapSecciones = new LinkedHashMap();
        Map<Long, List<MatriculaSeccion>> mapMatriculaSecciones = new LinkedHashMap();

        List<MatriculaSeccion> matriculaSecciones = matriculaSeccionDAO.allByAlumnoCiclo(alumno, cicloAca);
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

        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allBySecciones(secciones);
        for (DocenteSeccion profeSeccion : docentesSecciones) {
            Seccion seccionProfe = profeSeccion.getSeccion();
            Seccion seccion = mapSecciones.get(seccionProfe.getId());
            profeSeccion.setSeccion(seccion);
            seccion.getDocenteSeccion().add(profeSeccion);
        }

        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allByAlumnoCiclo(alumno, cicloAca);

        ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
        ObjectNode objNodeSeccion = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode lstNode = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode lstNodeDocente = new ArrayNode(JsonNodeFactory.instance);
        Map<Long, MatriculaResumen> mapMatriculaResumen = TypesUtil.convertListToMap("matriculaResumen.id", "matriculaResumen", matriculaCursos);

        for (Map.Entry<Long, MatriculaResumen> entry : mapMatriculaResumen.entrySet()) {
            objNode.put("cursosMatriculado", entry.getValue().getCursosMatriculados());
            objNode.put("creditosMatriculado", entry.getValue().getCreditosMatriculados());

        }
        for (MatriculaCurso mc : matriculaCursos) {
            Curso curso = mc.getCurso();
            mc.setMatriculaSeccion(mapMatriculaSecciones.get(curso.getId()));
        }

        for (MatriculaCurso matriculaCurso : matriculaCursos) {
            ObjectNode objNodeCursos = new ObjectNode(JsonNodeFactory.instance);
            objNodeCursos.put("curso", matriculaCurso.getCurso().getNombre());
            objNodeCursos.put("codigoCurso", matriculaCurso.getCurso().getCodigo());
            objNodeCursos.put("creditos", matriculaCurso.getCreditos());
            objNodeCursos.put("estado", EstadoMatriculaEnum.getNombreAndName(matriculaCurso.getEstado()));
            objNodeCursos.put("notaFinal", matriculaCurso.getNotaFinal());
            objNodeCursos.put("notaAvance", matriculaCurso.getNotaAvance());
            lstNodeDocente = new ArrayNode(JsonNodeFactory.instance);
            for (MatriculaSeccion obj : matriculaCurso.getMatriculaSeccion()) {
                for (DocenteSeccion docenteSeccion : obj.getSeccion().getDocenteSeccion()) {
                    ObjectNode objNodeDocente = new ObjectNode(JsonNodeFactory.instance);
                    objNodeDocente.put("codigo", obj.getSeccion().getCodigo2());
                    objNodeDocente.put("nombreDocente", docenteSeccion.getDocente().getPersona().getNombreCompleto());
                    lstNodeDocente.add(objNodeDocente);
                }
            }
            objNodeCursos.set("docentes", lstNodeDocente);
            lstNode.add(objNodeCursos);
        }
        objNode.set("cursosMatriculados", lstNode);
        return objNode;

    }

    @Override
    public List<Hora> allHoras() {
        return horaDAO.allHora();
    }

    @Override
    public Alumno allInfo(Alumno alumno) {
        Alumno alu = alumnoDAO.findAllInfo(alumno.getId());
        return alu;
    }

    @Override
    public List<MatriculaCurso> allCursosMatriculadosByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo) {

        List<Seccion> secciones = new ArrayList();
        Map<Long, Seccion> mapSecciones = new LinkedHashMap();
        Map<Long, List<MatriculaSeccion>> mapMatriculaSecciones = new LinkedHashMap();

        List<MatriculaSeccion> matriculaSecciones = matriculaSeccionDAO.allByAlumnoCiclo(alumno, ciclo);
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

        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allBySecciones(secciones);
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

    @Override
    public List<PlanCurricular> allPlanCurricularByCarrera(Carrera carrera) {
        return planCurricularDAO.allActivoByCarrera(carrera);
    }

    @Override
    @Transactional
    public void cambiarPlan(Alumno alumno, PlanCurricular planCurricular) {
        Alumno alumnoBD = alumnoDAO.find(alumno.getId());
        PlanCurricular planCurricularBD = planCurricularDAO.find(planCurricular.getId());
        alumnoBD.setPlanCurricular(planCurricularBD);
        alumnoDAO.update(alumnoBD);
    }

    @Override
    public void generarAvance(Alumno alumno, DataSessionPivot ds) {
        avanceCurricularService.generarAvanceCurricularByAlumno(alumno, ds);
    }

    @Override
    public List<AlumnoCicloCurso> allPromediosByAlumnoOrderByCurso(Alumno alumno) {
        return alumnoCicloCursoDAO.allByAlumnoOrdeyByCurso(alumno);
    }

    @Override
    public List<AlumnoCiclo> allPromediosByAlumno(Alumno alumno) {
        List<AlumnoCicloCurso> cursosCiclos = alumnoCicloCursoDAO.allByAlumno(alumno);
        Map<Long, AlumnoCiclo> mapAlumnoCiclo = TypesUtil.convertListToMap("alumnoCiclo.id", "alumnoCiclo", cursosCiclos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso = TypesUtil.convertListToMapList("alumnoCiclo.id", cursosCiclos);

        List<AlumnoCiclo> promedios = new ArrayList(mapAlumnoCiclo.values());
        for (AlumnoCiclo promedio : promedios) {
            List<AlumnoCicloCurso> cursos = mapAlumnoCicloCurso.get(promedio.getId());
            promedio.setAlumnoCicloCurso(cursos);
        }
        return promedios;
    }

    @Override
    @Transactional
    public void calcularPromedio(Alumno alumnoForm, DataSessionPivot ds) {
        Alumno alumno = alumnoDAO.find(alumnoForm);
        visorCalculoNotas.setActivo(false);
        promedioService.calulcarSituacionAcademica(alumno, ds.getUsuario());
    }

}

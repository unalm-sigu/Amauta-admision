package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.curso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CarreraCachimbos;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCachimbos;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.SeccionCursoCachimbos;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionCursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;

@Service
@Transactional(readOnly = true)
public class HorarioCachimboCursoServiceImp implements HorarioCachimboCursoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CursoCachimbosDAO cursoCachimbosDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    SeccionHorarioCachimbosDAO seccionHorarioCachimbosDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    AlumnoHorarioDAO alumnoHorarioDAO;

    @Autowired
    SeccionCursoCachimbosDAO seccionCursoCachimbosDAO;
    @Autowired
    CarreraCachimbosDAO carreraCachimbosDAO;
    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Override
    public List<CursoCachimbos> allCursoCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico) {
        List<CursoCachimbos> cursosCachimbos = cursoCachimbosDAO.allByDynatableCiclo(filter, cicloAcademico);
        List<Curso> cursos = cursosCachimbos.stream().map(x -> x.getCurso()).collect(Collectors.toList());
        List<CursoCachimbos> cursosCachimbosVer = cursoCachimbosDAO.allByCursosCiclo(cursos, cicloAcademico);

        List<CarreraCachimbos> carrerasCachimbos = carreraCachimbosDAO.allByCicloAcademico(cicloAcademico);
        List<SeccionCursoCachimbos> seccionesCachimbos = seccionCursoCachimbosDAO.allByCursoCachimbos(cursosCachimbosVer);
        List<AlumnoHorario> alumnosHorarios = alumnoHorarioDAO.allByCicloAcademico(cicloAcademico);
        List<Alumno> alumnos = alumnosHorarios.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaCurso> matriculadosCurso = matriculaCursoDAO.allByAlumnosCursosCiclo(alumnos, cursos, cicloAcademico);

        Map<Long, CarreraCachimbos> mapCarrerasCachimbos = TypesUtil.convertListToMap("carrera.id", carrerasCachimbos);
        Map<Long, List<SeccionCursoCachimbos>> mapSeccionesCachimbos = TypesUtil.convertListToMapList("cursoCachimbos.id", seccionesCachimbos);
        Map<Long, List<SeccionCursoCachimbos>> mapSeccionesCursos = TypesUtil.convertListToMapList("cursoCachimbos.curso.id", seccionesCachimbos);
        Map<Long, List<Carrera>> mapCarreras = TypesUtil.convertListToMapList("curso.id", "carrera", cursosCachimbosVer);

        for (CursoCachimbos cursoCachimbo : cursosCachimbos) {
            CarreraCachimbos carreraChm = mapCarrerasCachimbos.get(cursoCachimbo.getCarrera().getId());
            Integer matriculadosCarrera = countMatriculados(matriculadosCurso, cursoCachimbo.getCurso(), carreraChm.getCarrera());
            cursoCachimbo.setDemanda(carreraChm.getSinHorario() - matriculadosCarrera);

            Integer oferta = 0;
            List<SeccionCursoCachimbos> seccionesChm = mapSeccionesCachimbos.get(cursoCachimbo.getId());
            seccionesChm = (seccionesChm == null) ? new ArrayList() : seccionesChm;
            for (SeccionCursoCachimbos seccionChm : seccionesChm) {
                if (seccionChm.getSeccion().getTipoSeccionEnum() != TipoSeccionEnum.TCUR) {
                    oferta += seccionChm.getSeccion().getVacantesDisponibles();
                }
            }
            cursoCachimbo.setOferta(oferta);

            Integer demandaTotal = 0;
            Curso curso = cursoCachimbo.getCurso();
            List<Carrera> carreras = mapCarreras.get(curso.getId());
            for (Carrera carrera : carreras) {
                CarreraCachimbos carreraChmx = mapCarrerasCachimbos.get(carrera.getId());
                matriculadosCarrera = countMatriculados(matriculadosCurso, cursoCachimbo.getCurso(), carrera);
                demandaTotal += carreraChmx.getSinHorario() - matriculadosCarrera;
            }
            cursoCachimbo.setDemandaTotal(demandaTotal);

            Integer ofertaTotal = 0;
            Map<Long, Long> mapSecciones = new HashMap();
            List<SeccionCursoCachimbos> seccionesCurso = mapSeccionesCursos.get(curso.getId());
            seccionesCurso = (seccionesCurso == null) ? new ArrayList() : seccionesCurso;
            for (SeccionCursoCachimbos seccionChm : seccionesCurso) {
                Seccion seccion = seccionChm.getSeccion();
                Long idSeccion = mapSecciones.get(seccion.getId());
                if (idSeccion != null) {
                    continue;
                }
                if (seccion.getTipoSeccionEnum() != TipoSeccionEnum.TCUR) {
                    ofertaTotal += seccion.getVacantesDisponibles();
                }
                mapSecciones.put(seccion.getId(), seccion.getId());
            }
            cursoCachimbo.setOfertaTotal(ofertaTotal);
        }

        return cursosCachimbos;
    }

    private Integer countMatriculados(List<MatriculaCurso> matriculadosCurso, Curso curso, Carrera carrera) {
        Integer conteo = 0;
        for (MatriculaCurso matriculaCurso : matriculadosCurso) {
            Carrera carr = matriculaCurso.getMatriculaResumen().getAlumno().getCarrera();
            Curso cur = matriculaCurso.getCurso();
            if (curso.getId() == cur.getId().longValue()
                    && carrera.getId() == carr.getId().longValue()
                    && matriculaCurso.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                conteo++;
            }
        }
        return conteo;
    }

    @Override
    @Transactional
    public void delete(CursoCachimbos cursoCachimbos) {
        cursoCachimbosDAO.delete(cursoCachimbos);
    }

    @Override
    public List<Curso> allCursoByName(String nombre) {
        return cursoDAO.allCursoByName(nombre);
    }

    @Override
    public List<Carrera> allCarreraByName(String nombre, ModalidadEstudio modalidadEstudio) {
        return carreraDAO.allByNombreModalidad(nombre, modalidadEstudio);
    }

    @Override
    @Transactional
    public void addCurso(CursoCachimbos cursoCachimbos) {
        CursoCachimbos cursoCachimbosDb = cursoCachimbosDAO.findByCursoCiclo(cursoCachimbos);
        if (cursoCachimbosDb == null) {
            cursoCachimbos.setFechaCreacion(new Date());
            cursoCachimbosDAO.save(cursoCachimbos);
        }

    }

    @Override
    public List<CarreraCursoCachimbo> allCarrera(ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico) {

        List<CursoCachimbos> cursosCchimbos = cursoCachimbosDAO.allByCiclo(cicloAcademico);
        List<CarreraCursoCachimbo> carrerasCachimbos = new ArrayList();
        Map<Long, List<CursoCachimbos>> cursosCachimbosMap = TypesUtil.convertListToMapList("carrera.id", cursosCchimbos);
        List<Carrera> carreras = carreraDAO.allByModalidad(modalidadEstudio);
        List<CarreraCachimbos> carreraCachimbos = carreraCachimbosDAO.allCarreraCachimbosByCarreras(carreras, cicloAcademico);
        Map<Long, CarreraCachimbos> carreraCachimbosMap = TypesUtil.convertListToMap("carrera.id", carreraCachimbos);
        for (Carrera carrera : carreras) {
            List<CursoCachimbos> cursosCachimboMap = cursosCachimbosMap.get(carrera.getId());
            CarreraCachimbos carreraCachimboMap = carreraCachimbosMap.get(carrera.getId());
            CarreraCursoCachimbo carreraCursoCachimbo = new CarreraCursoCachimbo();
            carreraCursoCachimbo.setCarrera(carrera);
            if (cursosCachimboMap == null) {
                carreraCursoCachimbo.setCantidad(0);
            } else {
                carreraCursoCachimbo.setCantidad(cursosCachimboMap.size());
            }
            if (carreraCachimboMap == null) {
                continue;
            } else {
                carreraCursoCachimbo.setCarreraCachimbos(carreraCachimboMap);
            }
            carrerasCachimbos.add(carreraCursoCachimbo);
        }

        return carrerasCachimbos;
    }

    @Override
    public Map<Long, Map<Long, HorarioCachimbos>> allSeccionHorarioCachimbos(List<CursoCachimbos> cursoCachimbos, CicloAcademico cicloAcademico) {

        Map<Long, Map<Long, HorarioCachimbos>> cursoHorarioCachimbosMap = new LinkedHashMap();

        if (cursoCachimbos.isEmpty()) {
            return cursoHorarioCachimbosMap;
        }

        List<Curso> cursos = cursoCachimbos.stream()
                .map(CursoCachimbos::getCurso)
                .collect(Collectors.toList());

        List<SeccionHorarioCachimbos> seccionHorarioCachimbos = seccionHorarioCachimbosDAO.allByCursoCiclo(cicloAcademico, cursos);

        for (SeccionHorarioCachimbos seccionHorarioCachimbo : seccionHorarioCachimbos) {

            Curso curso = (Curso) ObjectUtil.getParentTree(seccionHorarioCachimbo, "seccion.grupoSeccion.curso");
            HorarioCachimbos horarioCachimbos = (HorarioCachimbos) ObjectUtil.getParentTree(seccionHorarioCachimbo, "horarioCachimbos");

            if (curso == null) {
                continue;
            }

            if (horarioCachimbos == null) {
                continue;
            }

            Map<Long, HorarioCachimbos> horarioCachimbosMap = cursoHorarioCachimbosMap.get(curso.getId());

            if (horarioCachimbosMap == null) {
                horarioCachimbosMap = new LinkedHashMap();
            }

            horarioCachimbosMap.put(horarioCachimbos.getId(), horarioCachimbos);

            cursoHorarioCachimbosMap.put(curso.getId(), horarioCachimbosMap);

        }

        return cursoHorarioCachimbosMap;
    }

    @Override
    public void fillGrupoSeccion(List<CursoCachimbos> cursoCachimbos, CicloAcademico cicloAcademico) {

        List<Curso> cursos = cursoCachimbos.stream()
                .map(CursoCachimbos::getCurso)
                .collect(Collectors.toList());

        List<Seccion> secciones = seccionDAO.allActivosByCursosCiclo(cursos, cicloAcademico);
        List<SeccionHorarioCachimbos> seccionHorarioCachimbos = seccionHorarioCachimbosDAO.allBySeccions(cicloAcademico, secciones);
        Map<Long, List<SeccionHorarioCachimbos>> seccionHorarioCachimbosMap = TypesUtil.convertListToMapList("seccion.id", seccionHorarioCachimbos);

        List<HorarioCachimbos> horarios = seccionHorarioCachimbos.stream()
                .map(SeccionHorarioCachimbos::getHorarioCachimbos)
                .collect(Collectors.toList());

        List<AlumnoHorario> alumnos = alumnoHorarioDAO.allByCicloHorarios(cicloAcademico, horarios);

        Map<Long, List<AlumnoHorario>> alumnosMap = TypesUtil.convertListToMapList("horarioCachimbos.id", alumnos);

        Map<Long, Map<Long, GrupoSeccion>> cursoGrupoSeccionMap = new LinkedHashMap();
        Map<Long, Map<Long, Seccion>> grupoSeccionMap = new LinkedHashMap();

        for (Seccion seccion : secciones) {

            GrupoSeccion grupoSeccion = (GrupoSeccion) ObjectUtil.getParentTree(seccion, "grupoSeccion");
            Curso curso = (Curso) ObjectUtil.getParentTree(seccion, "grupoSeccion.curso");

            Map<Long, GrupoSeccion> grupoSeccionesMap = cursoGrupoSeccionMap.get(curso.getId());
            if (grupoSeccionesMap == null) {
                grupoSeccionesMap = new LinkedHashMap();
            }
            grupoSeccionesMap.put(grupoSeccion.getId(), grupoSeccion);
            cursoGrupoSeccionMap.put(curso.getId(), grupoSeccionesMap);

            Map<Long, Seccion> seccionesMap = grupoSeccionMap.get(grupoSeccion.getId());
            if (seccionesMap == null) {
                seccionesMap = new LinkedHashMap();
            }

            seccionesMap.put(seccion.getId(), seccion);
            grupoSeccionMap.put(grupoSeccion.getId(), seccionesMap);

        }

        for (Curso curso : cursos) {

            Map<Long, GrupoSeccion> gruposMap = cursoGrupoSeccionMap.get(curso.getId());
            if (gruposMap == null) {
                continue;
            }

            List<GrupoSeccion> grupoSecciones = new ArrayList();

            for (GrupoSeccion grupo : gruposMap.values()) {

                Map<Long, Seccion> seccionesMap = grupoSeccionMap.get(grupo.getId());
                if (seccionesMap == null) {
                    continue;
                }
                List<Seccion> sexs = new ArrayList();

                for (Seccion sex : seccionesMap.values()) {
                    int totalSuscritos = 0;
                    List<SeccionHorarioCachimbos> sexHorarioCachimbo = seccionHorarioCachimbosMap.get(sex.getId());

                    if (sexHorarioCachimbo != null) {
                        for (SeccionHorarioCachimbos ss : sexHorarioCachimbo) {
                            HorarioCachimbos hc = ss.getHorarioCachimbos();
                            List<AlumnoHorario> alumnosList = alumnosMap.get(hc.getId());
                            if (alumnosList != null) {
                                totalSuscritos += alumnosList.size();
                            }
                        }
                    }

                    sex.setReservados(totalSuscritos);
                    sexs.add(sex);
                }
                Collections.sort(sexs, new Seccion.CompareCodigo2());
                grupo.setSecciones(sexs);
                grupoSecciones.add(grupo);
            }

            Collections.sort(grupoSecciones, new GrupoSeccion.CompareCodigo2());

            curso.setGrupoSeccion(grupoSecciones);
        }
    }

    @Override
    public String getClave(Seccion seccion) {
        StringBuilder sb = new StringBuilder();
        sb.append(ObjectUtil.getParentTree(seccion, "codigo").toString());
        sb.append(" ");
        sb.append(ObjectUtil.getParentTree(seccion, "grupoHoras.codigo").toString());
        return sb.toString();
    }

    @Override
    @Transactional
    public void updateSeccionCursoCachimbo(CarreraCursoCachimbo carreraCursoCachimbo, Usuario usuario) {

        ObjectUtil.eliminarAttrSinId(carreraCursoCachimbo, "curso");
        CursoCachimbos curso = carreraCursoCachimbo.getCurso();

        if (curso == null) {
            throw new PhobosException("Curso no esta presente");
        }

        List<Seccion> secciones = carreraCursoCachimbo.getSecciones();
        secciones = (secciones == null) ? new ArrayList() : secciones;

        List<Seccion> seccionesFormBD = seccionDAO.allMatriculablesBySecciones(secciones);
        Map<Long, Seccion> mapSecciones = new LinkedHashMap();
        for (Seccion seccion : seccionesFormBD) {
            mapSecciones.put(seccion.getId(), seccion);
            if (seccion.getSeccionSuperior() != null) {
                mapSecciones.put(seccion.getSeccionSuperior().getId(), seccion.getSeccionSuperior());
            }
        }

        List<SeccionCursoCachimbos> seccionesForm = new ArrayList();
        for (Seccion seccion : mapSecciones.values()) {
            SeccionCursoCachimbos sc = new SeccionCursoCachimbos();
            sc.setSeccion(seccion);
            seccionesForm.add(sc);
        }

        List<SeccionCursoCachimbos> seccionesBD = seccionCursoCachimbosDAO.allByCursoCachimbos(curso);
        if (seccionesBD.isEmpty() && seccionesForm.isEmpty()) {
            throw new PhobosException("Debe marcar al menos una clave");
        }

        ListsInspector inspector = TypesUtil.analizeLists(seccionesBD, seccionesForm, "seccion.id");
        List<SeccionCursoCachimbos> nuevos = inspector.getNewList();
        List<SeccionCursoCachimbos> eliminables = inspector.getDeadList();
        if (nuevos.isEmpty() && eliminables.isEmpty()) {
            throw new PhobosException("No ha efectuado ningún cambio");
        }

        for (SeccionCursoCachimbos nuevo : nuevos) {
            nuevo.setFechaCreacion(new Date());
            nuevo.setUserRegistro(usuario);
            nuevo.setCursoCachimbos(curso);
            seccionCursoCachimbosDAO.save(nuevo);
        }
        for (SeccionCursoCachimbos eliminable : eliminables) {
            seccionCursoCachimbosDAO.delete(eliminable);
        }

    }

    @Override
    public List<SeccionCursoCachimbos> allCursoCachimbos(List<CursoCachimbos> cursoCachimbos) {
        if (cursoCachimbos.isEmpty()) {
            return new ArrayList();
        }
        return seccionCursoCachimbosDAO.allByCursoCachimbos(cursoCachimbos);
    }

}

package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.cursocarrera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCachimbos;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.SeccionCursoCachimbos;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionCursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;

@Service
@Transactional(readOnly = true)
public class HorarioCursoCarreraServiceImp implements HorarioCursoCarreraService {

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

    @Override
    public List<CursoCachimbos> allCursoCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return cursoCachimbosDAO.allCursoCachimbos(filter, cicloAcademico);
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
        return carreraDAO.allCarreraByName(nombre, modalidadEstudio);
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
        List<CursoCachimbos> cursosCchimbos = cursoCachimbosDAO.allCursoCachimbos(cicloAcademico);
        List<CarreraCursoCachimbo> cursos = new ArrayList();
        Map<Long, List<CursoCachimbos>> cursosCachimbosMap = TypesUtil.convertListToMapList("carrera.id", cursosCchimbos);
        List<Carrera> carreras = carreraDAO.allCarreraByModalidadEstudio(modalidadEstudio);
        for (Carrera carrera : carreras) {
            List<CursoCachimbos> cursosCachimboMap = cursosCachimbosMap.get(carrera.getId());
            CarreraCursoCachimbo carreraCursoCachimbo = new CarreraCursoCachimbo();
            carreraCursoCachimbo.setCarrera(carrera);
            if (cursosCachimboMap == null) {
                carreraCursoCachimbo.setCantidad(0);
            } else {
                carreraCursoCachimbo.setCantidad(cursosCachimboMap.size());
            }
            cursos.add(carreraCursoCachimbo);
        }
        return cursos;
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

                    sex.setSuscritos(totalSuscritos);
                    sexs.add(sex);
                }
                grupo.setSecciones(sexs);
                grupoSecciones.add(grupo);
            }

            Collections.sort(grupoSecciones, new GrupoSeccion.CompareCodigo());

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
    public void updateSeccionCursoCachimbo(CarreraCursoCachimbo carreraCursoCachimbo, Usuario usuario) {
        ObjectUtil.eliminarAttrSinId(carreraCursoCachimbo, "curso");
        Curso curso = carreraCursoCachimbo.getCurso();
        if (curso == null) {
            throw new PhobosException("curso no esta presente");
        }
        List<SeccionCursoCachimbos> seccionCursoCachimbos = seccionCursoCachimbosDAO.allByCurso(curso);
        if (!seccionCursoCachimbos.isEmpty()) {
            for (SeccionCursoCachimbos seccionCursoCachimbo : seccionCursoCachimbos) {

            }
        }

    }
}

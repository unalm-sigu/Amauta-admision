package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.cursocarrera;

import java.util.ArrayList;
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
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.CursoCachimbos;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.model.horario.SeccionHorarioCachimbos;

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
    public Map<Long, Map<Long,HorarioCachimbos>> allSeccionHorarioCachimbos(List<CursoCachimbos> cursoCachimbos, CicloAcademico cicloAcademico) {

        Map<Long, Map<Long, HorarioCachimbos>> cursoHorarioCachimbosMap = new LinkedHashMap();
        
        if(cursoCachimbos.isEmpty()){
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
            
            if(horarioCachimbosMap==null){
                horarioCachimbosMap=new LinkedHashMap();
            }
            
            horarioCachimbosMap.put(horarioCachimbos.getId(), horarioCachimbos);
            
            cursoHorarioCachimbosMap.put(curso.getId(), horarioCachimbosMap);
            
        }

        return cursoHorarioCachimbosMap;
    }

}

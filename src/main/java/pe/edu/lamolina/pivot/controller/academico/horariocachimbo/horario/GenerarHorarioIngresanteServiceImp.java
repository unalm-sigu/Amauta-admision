package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.horario;

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
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.CursoCachimbos;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Dia;
import pe.edu.lamolina.pivot.model.horario.Hora;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.model.horario.SeccionHorarioCachimbos;

@Service
@Transactional(readOnly = true)
public class GenerarHorarioIngresanteServiceImp implements GenerarHorarioIngresanteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CursoCachimbosDAO cursoCachimbosDAO;

    @Autowired
    HorarioCachimbosDAO horarioCachimbosDAO;

    @Autowired
    AlumnoHorarioDAO alumnoHorarioDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    HoraDAO horaDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    SeccionHorarioCachimbosDAO seccionHorarioCachimbosDAO;

    @Override
    public List<HorarioCachimbos> allHorarioCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return horarioCachimbosDAO.allHorarioCachimbos(filter, cicloAcademico);
    }

    @Override
    @Transactional
    public void delete(HorarioCachimbos horarioCachimbos) {
        horarioCachimbosDAO.delete(horarioCachimbos);
    }

    @Override
    @Transactional
    public void delete(HorarioCachimboForm form) {
        for (HorarioCachimbos horarioCachimbos : form.getHorarioCachimbos()) {
            horarioCachimbosDAO.delete(horarioCachimbos);
        }
    }

    @Override
    public List<AlumnoHorario> allAlumnoHorarioByName(String nombre, CicloAcademico cicloAcademico) {
        return alumnoHorarioDAO.allAlumnoHorarioByName(nombre, cicloAcademico);
    }

    @Override
    public List<Carrera> allCarrera(ModalidadEstudio modalidadEstudio) {
        return carreraDAO.allCarreraByModalidadEstudio(modalidadEstudio);
    }

    @Override
    public List<Curso> allCursoCachimbosByCicloAcademico(CicloAcademico cicloAcademico, Carrera carrera) {
        return cursoDAO.allCursoCachimbosByCicloAcademico(cicloAcademico, carrera);
    }

    @Override
    public List<HorarioCachimbos> allHorarioCachimbosByCicloAcademico(CicloAcademico cicloAcademico, Carrera carrera) {
        return horarioCachimbosDAO.allByCicloAcademico(cicloAcademico, carrera);
    }

    @Override
    public List<SeccionHorarioCachimbos> allSeccionHorarioCachimbosByCursoHora(Carrera carrera, List<Curso> cursos, CicloAcademico cicloAcademico) {
        return seccionHorarioCachimbosDAO.allByCursoHora(carrera, cursos, cicloAcademico);
    }

    @Override
    public String getClave(String codigo, List<SeccionHorarioCachimbos> shcHorario) {
        if (shcHorario == null) {
            return "";
        }
        for (SeccionHorarioCachimbos shc : shcHorario) {
            if (shc.getSeccion().getGrupoHoras().getTipoSeccion().equalsIgnoreCase(codigo)) {
                StringBuilder sb = new StringBuilder();
                sb.append(ObjectUtil.getParentTree(shc, "seccion.codigo").toString());
                sb.append(" ");
                sb.append(ObjectUtil.getParentTree(shc, "seccion.grupoHoras.codigo").toString());
                return sb.toString();
            }
        }
        return "";
    }

    @Override
    public List<Dia> allDia() {
        return diaDAO.allDia();
    }

    @Override
    public List<Hora> allHora() {
        return horaDAO.allHora();
    }

    @Override
    public List<HorarioSeccion> allSeccionHorarioCachimbosByHorarioCachimbos(HorarioCachimbos horario) {
        List<SeccionHorarioCachimbos> seccionHorarioCachimboses = seccionHorarioCachimbosDAO.allByHorario(horario);
        List<Seccion> secciones = new ArrayList();
        if (seccionHorarioCachimboses.isEmpty()) {
            return new ArrayList();
        }
        for (SeccionHorarioCachimbos seccionHorarioCachimbose : seccionHorarioCachimboses) {
            secciones.add(seccionHorarioCachimbose.getSeccion());
        }
        return horarioSeccionDAO.allBySeccion(secciones);
    }

    @Override
    public void generar(CicloAcademico cicloAcademico, Compania compania) {

        List<Carrera> carreras = carreraDAO.allActivoByModalidad(new ModalidadEstudio(1));
        Carrera carrera = carreraDAO.find(6L);
        List<CursoCachimbos> cursoCachimbos = cursoCachimbosDAO.allByCarreraCiclo(cicloAcademico, carrera);

        List<Curso> cursos = cursoCachimbos.stream().
                map(CursoCachimbos::getCurso).
                collect(Collectors.toList());

        List<HorarioSeccion> horarioSecciones = horarioSeccionDAO.allByCicloCurso(cicloAcademico, cursos);

        Map<Long, List<HorarioSeccion>> horarioSeccionesMap = TypesUtil.convertListToMapList("seccion.grupoSeccion.curso.id", horarioSecciones);
        Map<Long, List<HorarioSeccion>> horarioSeccionesBySeccionMap = TypesUtil.convertListToMapList("seccion.id", horarioSecciones);

        List<Seccion> sessiones = horarioSecciones.stream().
                map(HorarioSeccion::getSeccion).
                collect(Collectors.toList());

        Map<Long, List<Seccion>> seccionesByCursoMap = TypesUtil.convertListToMapList("grupoSeccion.curso.id", sessiones);

        List<GrupoSeccion> grupoSecciones = sessiones.stream().
                map(Seccion::getGrupoSeccion).
                collect(Collectors.toList());

        Map<Long, List<GrupoSeccion>> grupoSeccionesMap = TypesUtil.convertListToMapList("curso.id", grupoSecciones);
        Map<Long, List<Seccion>> seccionesMap = TypesUtil.convertListToMapList("grupoSeccion.id", sessiones);

        for (Curso curso : cursos) {
            logger.debug("CURSO {} {} ", curso.getId(), curso.getNombre());

            List<GrupoSeccion> gss = grupoSeccionesMap.get(curso.getId());
            if (gss == null) {
                continue;
            }
            for (GrupoSeccion gs : gss) {
                logger.debug("****GRUPOSECCION {} {} ", gs.getId(), gs.getCodigo());
                List<Seccion> se = seccionesMap.get(gs.getId());
                if (se == null) {
                    continue;
                }
                for (Seccion seccion : se) {
                    logger.debug("********SECCION {} {} ", seccion.getId(), seccion.getCodigo());
                    List<HorarioSeccion> horarios = horarioSeccionesBySeccionMap.get(seccion.getId());
                    if (horarios == null) {
                        continue;
                    }
                    for (HorarioSeccion horario : horarios) {
                        logger.debug("***********HORARIOS {} {} ", horario.getDia().getNombre(), horario.getHora().getCodigo());
                    }
                }
            }
        }

        for (Curso curso : cursos) {

            int index = cursos.indexOf(curso);
            index++;

            if (index >= cursos.size()) {
                break;
            }

            List<Seccion> secciones = seccionesByCursoMap.get(curso.getId());

            for (Seccion seccione : secciones) {
                Curso cursoSecond = cursos.get(index);
     
                
                List<Seccion> seccionesSecond = seccionesByCursoMap.get(cursoSecond.getId());
                for (Seccion seccion : seccionesSecond) {
                    logger.debug("********SECCION {} {} ", seccion.getId(), seccion.getCodigo());

                }
                index++;
            }

        }

    }

}

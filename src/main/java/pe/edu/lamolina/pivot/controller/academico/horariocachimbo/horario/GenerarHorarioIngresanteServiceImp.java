package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.horario;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
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
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.model.general.Dia;
import pe.edu.lamolina.pivot.model.horario.Hora;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.pivot.zelper.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.pivot.zelper.enums.TipoSeccionEnum;

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

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Override
    public ModalidadEstudio findModalidadPregrado() {
        return modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
    }

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
        return horarioSeccionDAO.allBySecciones(secciones);
    }

    @Override
    @Transactional
    public void generar(CicloAcademico cicloAcademico, ModalidadEstudio modalidad) {

        List<List<Seccion>> horariosTotal = new ArrayList();
        List<Carrera> carreras = carreraDAO.allActivoByModalidad(modalidad);
        //logger.debug("***carreras**** {}", carreras.size());
        for (Carrera carrera : carreras) {
            List<CursoCachimbos> cursoCachimbos = cursoCachimbosDAO.allByCarreraCiclo(cicloAcademico, carrera);
            //logger.debug("***cursoCachimbos**** {}", cursoCachimbos.size());
            if (cursoCachimbos.isEmpty()) {
                continue;
            }
            List<Curso> cursos = allCursosCarrera(cursoCachimbos);
            //logger.debug("***cursos*** {}", cursos.size());
            List<Seccion> secciones = seccionDAO.allActivosByCursosCiclo(cursos, cicloAcademico);
            //logger.debug("***secciones*** {}", secciones.size());
            Map<Long, List<Seccion>> mapSecciones = TypesUtil.convertListToMapList("grupoSeccion.curso.id", secciones);
            //logger.debug("***mapSecciones*** {}", mapSecciones.size());
            List<HorarioSeccion> horarios = horarioSeccionDAO.allBySecciones(secciones);
            //logger.debug("***horarios*** {}", horarios.size());
            Map<Long, List<HorarioSeccion>> mapHorarios = TypesUtil.convertListToMapList("seccion.id", horarios);
            //logger.debug("***mapHorarios*** {}", mapHorarios.size());

            for (Seccion seccion : secciones) {
                ////logger.debug("===seccion {}", seccion.getId());
                List<HorarioSeccion> horariosSecc = mapHorarios.get(seccion.getId());
                horariosSecc = (horariosSecc == null) ? new ArrayList() : horariosSecc;
                seccion.setHorarioSeccion(horariosSecc);
            }

            logger.debug("*** carrera {} {} cursoCachimbos {} secciones {} cursos {} ",
                    carrera.getId(),
                    carrera.getNombre(),
                    cursoCachimbos.size(),
                    secciones.size(),
                    cursos.size());

            Map<String, String> mapHorasDias = new LinkedHashMap();
            List<Seccion> horarioTempo = new ArrayList();
            permutar(1, 1, cursos, mapSecciones, mapHorasDias, horarioTempo, horariosTotal);
        }

    }

    private void permutar(
            int ordenCurso, int ordenSeccion,
            List<Curso> cursos, Map<Long, List<Seccion>> mapSecciones,
            Map<String, String> mapHorasDias, List<Seccion> horarioTempo, List<List<Seccion>> horariosCarrera) {

        ////logger.debug("===call permutar {}");
        logger.debug("ordenCurso {} ordenSeccion {} cursos {} {} {} {} ",
                ordenCurso,
                ordenSeccion,
                cursos.size());
        Curso curso = getCursoOrden(cursos, ordenCurso);
        List<Seccion> seccionesCurso = mapSecciones.get(curso.getId());

        int maxSecciones = cantPermutaSeccion(seccionesCurso);
//        //logger.debug("== maxSecciones {}", maxSecciones);
        List<Seccion> seccionesOrden = allSeccionByOrden(seccionesCurso, ordenSeccion);
        if (seccionesOrden.isEmpty()) {
            return;
        }

        boolean hayCruceHorario = hayCruceHorario(mapHorasDias, seccionesOrden);
        logger.debug("== hayCruceHorario {}", hayCruceHorario);

        if (!hayCruceHorario) {
            List<Seccion> horarioTempo2 = clonarLista(horarioTempo);
            Map<String, String> mapHorasDia2 = clonarMap(mapHorasDias);
            addSecciones(mapHorasDia2, seccionesOrden);
            for (Seccion seccion : seccionesOrden) {
                horarioTempo2.add(seccion);
            }
            //logger.debug("== mapHorasDia2 {}", mapHorasDia2.size());
            if (ordenCurso < cursos.size()) {
                //logger.debug("== call second permutacion {}", mapHorasDia2.size());
                permutar(ordenCurso + 1, 1, cursos, mapSecciones, clonarMap(mapHorasDia2), clonarLista(horarioTempo2), horariosCarrera);
            } else {
                //logger.debug("== nunca lega aqui ");
                horariosCarrera.add(horarioTempo2);
                printHorario(horarioTempo2);
            }
        }

        for (;;) {
            if (ordenSeccion < maxSecciones) {
                ordenSeccion++;
                permutar(ordenCurso, ordenSeccion, cursos, mapSecciones, clonarMap(mapHorasDias), clonarLista(horarioTempo), horariosCarrera);
            } else {
                break;
            }
        }

    }

    private void printHorario(List<Seccion> horarioTempo) {
        System.out.print("[");
        for (Seccion seccion : horarioTempo) {
            System.out.print(seccion.getCodigo() + ",");
        }
        System.out.println("]");
    }

    private Map clonarMap(Map<String, String> mapHorasDias) {
        Map<String, String> clonado = new LinkedHashMap();
        List<String> values = new ArrayList(mapHorasDias.values());
        for (String value : values) {
            clonado.put(value, value);
        }
        return clonado;
    }

    private List clonarLista(List<Seccion> tempo) {
        List<Seccion> clonado = new ArrayList();
        for (Seccion seccion : tempo) {
            clonado.add(seccion);
        }
        return clonado;
    }

    private Curso getCursoOrden(List<Curso> cursos, int orden) {
        int loop = 1;
        for (Curso curso : cursos) {
            if (loop == orden) {
                return curso;
            }
            loop++;
        }
        return null;
    }

    private void addSecciones(Map<String, String> mapHorasDias, List<Seccion> secciones) {
        for (Seccion seccion : secciones) {
            List<HorarioSeccion> horasDias = seccion.getHorarioSeccion();
            for (HorarioSeccion horaDia : horasDias) {
                mapHorasDias.put(horaDia.getHoraDia(), horaDia.getHoraDia());
            }
        }
    }

    private boolean hayCruceHorario(Map<String, String> mapHorasDias, List<Seccion> secciones) {
        for (Seccion seccion : secciones) {
            List<HorarioSeccion> horasDias = seccion.getHorarioSeccion();
            for (HorarioSeccion horaDia : horasDias) {
                String horaDiaMapeada = mapHorasDias.get(horaDia.getHoraDia());
                if (horaDiaMapeada != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private Integer cantPermutaSeccion(List<Seccion> secciones) {
        int loop = 0;
        if (secciones == null) {
            return loop;
        }
        for (Seccion seccion : secciones) {
            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
                continue;
            }
            loop++;
        }
        return loop;
    }

    private List<Seccion> allSeccionByOrden(List<Seccion> secciones, int orden) {
        boolean existe = false;
        int loop = 1;
        List<Seccion> seleccionados = new ArrayList();
        if (secciones == null) {
            return seleccionados;
        }
        for (Seccion seccion : secciones) {
            if (seccion.getTipoSeccionEnum() == TipoSeccionEnum.TCUR) {
                seleccionados.add(seccion);
                continue;
            }
            if (loop == orden) {
                seleccionados.add(seccion);
                existe = true;
                break;
            }
            loop++;
        }
        if (!existe) {
            return new ArrayList();
        }
        return seleccionados;
    }

    private List<Curso> allCursosCarrera(List<CursoCachimbos> cursosCachimbos) {
        List<Curso> cursos = new ArrayList();
        for (CursoCachimbos cursoCachimbo : cursosCachimbos) {
            cursos.add(cursoCachimbo.getCurso());
        }
        return cursos;
    }

}

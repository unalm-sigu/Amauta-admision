package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.horario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCachimbos;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.SeccionCursoCachimbos;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;
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
import pe.edu.lamolina.pivot.dao.horario.SeccionCursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.pivot.zelper.misc.Acumulador;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

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

    @Autowired
    SeccionCursoCachimbosDAO seccionCursoCachimbosDAO;

    @Override
    public ModalidadEstudio findModalidadPregrado() {
        return modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
    }

    @Override
    public List<HorarioCachimbos> allHorarioCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return horarioCachimbosDAO.allByDynatable(filter, cicloAcademico);
    }

    @Override
    @Transactional
    public void delete(HorarioCachimbos horarioCachimbos) {
        HorarioCachimbos horarioDb = horarioCachimbosDAO.find(horarioCachimbos);
        if (horarioDb == null) {
            return;
        }
        seccionHorarioCachimbosDAO.deleteByHorarioCachimbos(horarioCachimbos);
        List<AlumnoHorario> alumnos = alumnoHorarioDAO.allByHorarioCachimbos(horarioCachimbos);
        for (AlumnoHorario alumno : alumnos) {
            alumno.setHorarioCachimbos(null);
            alumnoHorarioDAO.update(alumno);
        }
        horarioCachimbosDAO.delete(horarioDb);
    }

    @Override
    @Transactional
    public void delete(HorarioCachimboForm form) {
        for (HorarioCachimbos horarioCachimbos : form.getHorarioCachimbos()) {
            HorarioCachimbos horarioDb = horarioCachimbosDAO.find(horarioCachimbos);
            if (horarioDb == null) {
                continue;
            }
            seccionHorarioCachimbosDAO.deleteByHorarioCachimbos(horarioCachimbos);
            List<AlumnoHorario> alumnos = alumnoHorarioDAO.allByHorarioCachimbos(horarioCachimbos);
            for (AlumnoHorario alumno : alumnos) {
                alumno.setHorarioCachimbos(null);
                alumnoHorarioDAO.update(alumno);
            }
            horarioCachimbosDAO.delete(horarioDb);
        }
    }

    @Override
    public List<AlumnoHorario> allAlumnoHorarioByName(String nombre, CicloAcademico cicloAcademico, Long horario) {
        HorarioCachimbos horarioCachimbos = horarioCachimbosDAO.find(new HorarioCachimbos(horario));
        Carrera carrera = horarioCachimbos.getCarrera();
        return alumnoHorarioDAO.allAlumnoHorarioByName(nombre, cicloAcademico, carrera);
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
        return horarioCachimbosDAO.allByCicloCarrera(cicloAcademico, carrera);
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
        List<SeccionHorarioCachimbos> seccionesHorarios = seccionHorarioCachimbosDAO.allByHorario(horario);
        if (seccionesHorarios.isEmpty()) {
            return new ArrayList();
        }

        List<Seccion> secciones = new ArrayList();
        for (SeccionHorarioCachimbos seccionHorario : seccionesHorarios) {
            secciones.add(seccionHorario.getSeccion());
        }
        return horarioSeccionDAO.allBySecciones(secciones);
    }

    @Override
    @Transactional
    public void generar(CicloAcademico ciclo, ModalidadEstudio modalidad, DataSessionPivot ds) {
        List<AlumnoHorario> alumnos = alumnoHorarioDAO.allByCicloAcademico(ciclo);
        this.generarHorario(ciclo, modalidad, ds, alumnos);
    }

    @Override
    @Transactional
    public void generarHorario(CicloAcademico ciclo, ModalidadEstudio modalidad, DataSessionPivot ds, List<AlumnoHorario> alumnos) {

        Acumulador code;
        {
            HorarioCachimbos maxcode = horarioCachimbosDAO.findMaxCodeOrderByCiclo(ciclo);
            if (maxcode != null) {
                String codigo = maxcode.getCodigo();
                String numcode = codigo.substring(2);
                Integer numm = new Integer(numcode);
                Integer seed = numm + 1;
                code = new Acumulador(seed);
            } else {
                code = new Acumulador(1);
            }
        }

        List<List<Seccion>> horariosTotal = new ArrayList();
        List<Carrera> carreras = carreraDAO.allActivoByModalidad(modalidad);

        List<CursoCachimbos> cursoCachimbosTodos = cursoCachimbosDAO.allByCicloFromSeccionCursoCachimbo(ciclo);
        Map<Long, List<CursoCachimbos>> mapCursosCachimbos = TypesUtil.convertListToMapList("carrera.id", cursoCachimbosTodos);
        List<Curso> cursosTodos = allCursosCarrera(cursoCachimbosTodos);

        List<Seccion> secciones = seccionDAO.allActivosByCursosCiclo(cursosTodos, ciclo);
        List<SeccionCursoCachimbos> seccionesCachimbos = seccionCursoCachimbosDAO.allByCiclo(ciclo);

        Map<Long, List<SeccionCursoCachimbos>> mapSeccionesCachimbos = TypesUtil.convertListToMapList("cursoCachimbos.carrera.id", seccionesCachimbos);
        Map<Long, Seccion> mapSeccionMain = TypesUtil.convertListToMap("id", secciones);

        List<SeccionHorarioCachimbos> seccionHorarioCachimbos = seccionHorarioCachimbosDAO.allBySeccions(ciclo, secciones);
        Map<Long, List<SeccionHorarioCachimbos>> seccionHorarioCachimbosMap = TypesUtil.convertListToMapList("seccion.id", seccionHorarioCachimbos);

        for (Seccion secc : secciones) {
            int sus = this.getSuscritos(secc, seccionHorarioCachimbosMap);
            secc.setSuscritos(sus);
            Seccion sup = secc.getSeccionSuperior();
            if (sup != null) {
                Seccion superior = mapSeccionMain.get(sup.getId());
                secc.setSeccionSuperior(superior);
            }
        }

        List<HorarioSeccion> horaDiaSecciones = horarioSeccionDAO.allBySecciones(secciones);
        Map<Long, List<HorarioSeccion>> mapHorarios = TypesUtil.convertListToMapList("seccion.id", horaDiaSecciones);

        for (Seccion seccion : secciones) {
            List<HorarioSeccion> horariosSecc = mapHorarios.get(seccion.getId());
            horariosSecc = (horariosSecc == null) ? new ArrayList() : horariosSecc;
            seccion.setHorarioSeccion(horariosSecc);
        }

        Map<Long, List<AlumnoHorario>> mapAlumnos = TypesUtil.convertListToMapList("alumno.carrera.id", alumnos);

        List<HorarioCachimbos> horariosBD = horarioCachimbosDAO.allByCiclo(ciclo);
        List<SeccionHorarioCachimbos> seccionesHorariosBD = seccionHorarioCachimbosDAO.allByHorarios(horariosBD);
        for (SeccionHorarioCachimbos seccHorarioCachimbo : seccionesHorariosBD) {
            Seccion secc = mapSeccionMain.get(seccHorarioCachimbo.getSeccion().getId());
            seccHorarioCachimbo.setSeccion(secc);
        }

        Map<Long, List<SeccionHorarioCachimbos>> mapSeccionHorario = TypesUtil.convertListToMapList("horarioCachimbos.id", seccionesHorariosBD);
        for (HorarioCachimbos horario : horariosBD) {
            List<SeccionHorarioCachimbos> seccionesHorarioCachimbo = mapSeccionHorario.get(horario.getId());
            seccionesHorarioCachimbo = (seccionesHorarioCachimbo == null) ? new ArrayList() : seccionesHorarioCachimbo;
            horario.setSeccionHorarioCachimbos(seccionesHorarioCachimbo);
        }

        Map<String, HorarioCachimbos> mapHorario = mappingHorarios(horariosBD);
        for (;;) {
            boolean noHayAlumnos = true;
            for (Carrera carrera : carreras) {
                List<AlumnoHorario> alumnoCarr = mapAlumnos.get(carrera.getId());

                if (alumnoCarr == null) {
                    continue;
                }

                if (!alumnoCarr.isEmpty()) {
                    noHayAlumnos = false;
                }

                if (alumnoCarr.isEmpty()) {
                    continue;
                }

                AlumnoHorario alumno = alumnoCarr.get(0);
                if (alumno.getHorarioCachimbos() != null) {
                    alumnoCarr.remove(alumno);
                    continue;
                }

                List<CursoCachimbos> cursoCachimbos = mapCursosCachimbos.get(carrera.getId());
                if (cursoCachimbos == null || cursoCachimbos.isEmpty()) {
                    continue;
                }
                List<Curso> cursos = allCursosCarrera(cursoCachimbos);

                Map<Long, List<Seccion>> mapSeccionesCarrera = createMapSeccionesCarrera(mapSeccionMain, mapSeccionesCachimbos, carrera);
                Map<String, String> mapHorasDias;
                List<Seccion> horarioTempo;
                Map<Long, Curso> mapCursos;

                int busquedas = 0;
                for (;;) {
                    busquedas++;
                    mapHorasDias = new LinkedHashMap();
                    horarioTempo = new ArrayList();
                    mapCursos = new LinkedHashMap();

                    reordernarSeccion(cursos, mapSeccionesCarrera);
                    permutarUnico(1, 1, cursos, mapSeccionesCarrera, mapHorasDias, horarioTempo, horariosTotal);
                    for (Seccion seccion : horarioTempo) {
                        Curso curso = seccion.getGrupoSeccion().getCurso();
                        mapCursos.put(curso.getId(), curso);
                    }

                    if (cursos.size() == mapCursos.size()) {
                        break;
                    }
                    if (busquedas > 10) {
                        System.out.println("Se sigue buscando horario para el alumno " + alumno.getAlumno().getCodigo() + " carrera " + carrera.getCodigo());
                    }
                }

                if (!horarioTempo.isEmpty()) {
                    HorarioCachimbos horario = createHorario(horarioTempo, carrera, ciclo, cursos.size(), mapHorario, code, ds);
                    horario.setSuscritos(horario.getSuscritos() + 1);
                    alumno.setHorarioCachimbos(horario);

                    List<SeccionHorarioCachimbos> seccHorCachimbos = horario.getSeccionHorarioCachimbos();
                    for (SeccionHorarioCachimbos seccHorCachimbo : seccHorCachimbos) {
                        Seccion secc = seccHorCachimbo.getSeccion();
                        secc.setSuscritos(secc.getSuscritos() + 1);
                    }
                }

                alumnoCarr.remove(alumno);
            }
            if (noHayAlumnos) {
                break;
            }
        }

        for (AlumnoHorario alumno : alumnos) {
            alumnoHorarioDAO.update(alumno);
        }

    }

    private Map<Long, List<Seccion>> createMapSeccionesCarrera(
            Map<Long, Seccion> mapSeccionesMain,
            Map<Long, List<SeccionCursoCachimbos>> mapSeccionesCachimbos,
            Carrera carrera) {
        Map<Long, List<Seccion>> mapSecciones = new LinkedHashMap();

        List<SeccionCursoCachimbos> seccionesCachimbos = mapSeccionesCachimbos.get(carrera.getId());
        for (SeccionCursoCachimbos seccionCachimbo : seccionesCachimbos) {
            Curso curso = seccionCachimbo.getCursoCachimbos().getCurso();
            Seccion sec = seccionCachimbo.getSeccion();
            Seccion seccion = mapSeccionesMain.get(sec.getId());

            List<Seccion> secciones = mapSecciones.get(curso.getId());
            if (secciones == null) {
                secciones = new ArrayList();
                mapSecciones.put(curso.getId(), secciones);
            }
            secciones.add(seccion);
        }
        return mapSecciones;
    }

    @Override
    @Transactional
    public HorarioCachimbos createHorario(
            List<Seccion> horarioTempo,
            Carrera carrera,
            CicloAcademico ciclo,
            int cursos,
            Map<String, HorarioCachimbos> mapHorario,
            Acumulador code, DataSessionPivot ds) {

        Collections.sort(horarioTempo, new Seccion.CompareCodigo());
        String huella = getHorarioString(horarioTempo);
        HorarioCachimbos horarioAnt = mapHorario.get(huella);
        if (horarioAnt != null) {
            return horarioAnt;
        }

        Map<Long, Curso> mapCursos = new LinkedHashMap();
        for (Seccion seccion : horarioTempo) {
            Curso curso = seccion.getGrupoSeccion().getCurso();
            mapCursos.put(curso.getId(), curso);
        }

        Integer vac = getVacanteMinima(horarioTempo);
        HorarioCachimbos horario = new HorarioCachimbos();
        horario.setCapacidad(vac);
        horario.setCarrera(carrera);
        horario.setCicloAcademico(ciclo);
        horario.setCursos(mapCursos.size());
        horario.setMatriculados(0);
        horario.setSuscritos(0);
        horario.setSeccionHorarioCachimbos(new ArrayList());
        horario.setCodigo("H-" + NumberFormat.codigo(code.getValor(), 3));
        horarioCachimbosDAO.save(horario);
        code.incrementar();

        for (Seccion seccion : horarioTempo) {
            SeccionHorarioCachimbos sh = new SeccionHorarioCachimbos();
            sh.setHorarioCachimbos(horario);
            sh.setSeccion(seccion);
            sh.setUserCreacion(ds.getUsuario());
            sh.setFechaCreacion(new Date());
            seccionHorarioCachimbosDAO.save(sh);
            horario.getSeccionHorarioCachimbos().add(sh);
        }
        mapHorario.put(huella, horario);
        return horario;
    }

    private void permutarVarios(
            int ordenCurso, int ordenSeccion,
            List<Curso> cursos, Map<Long, List<Seccion>> mapSecciones,
            Map<String, String> mapHorasDias, List<Seccion> horarioTempo, List<List<Seccion>> horariosCarrera) {

        Curso curso = getCursoOrden(cursos, ordenCurso);
        List<Seccion> seccionesCurso = mapSecciones.get(curso.getId());
        int maxSecciones = cantPermutaSeccion(seccionesCurso);

        ////logger.debug("===call permutar {}");
//        logger.debug("ordenCurso {} ordenSeccion {} cursos {} secciones {}", ordenCurso, ordenSeccion, cursos.size(), maxSecciones);
//        logger.debug("Pre-Horario: {}", getHorarioString(horarioTempo));
//        //logger.debug("== maxSecciones {}", maxSecciones);
        List<Seccion> seccionesOrden = allSeccionByOrden(seccionesCurso, ordenSeccion);
        if (seccionesOrden.isEmpty()) {
            return;
        }

        boolean hayCruceHorario = hayCruceHorario(mapHorasDias, seccionesOrden);
//        logger.debug("\thayCruceHorario={} con {}", hayCruceHorario, getHorarioString(seccionesOrden));

        if (!hayCruceHorario) {
            List<Seccion> horarioTempo2 = clonarLista(horarioTempo);
            Map<String, String> mapHorasDia2 = clonarMap(mapHorasDias);
            addHoraDiaSecciones(mapHorasDia2, seccionesOrden);
            for (Seccion seccion : seccionesOrden) {
                horarioTempo2.add(seccion);
            }
            //logger.debug("== mapHorasDia2 {}", mapHorasDia2.size());
            if (ordenCurso < cursos.size()) {
//                logger.debug("\tpermuta otro curso");
                permutarVarios(ordenCurso + 1, 1, cursos, mapSecciones, clonarMap(mapHorasDia2), clonarLista(horarioTempo2), horariosCarrera);
            } else {
                //logger.debug("== nunca lega aqui ");
                Integer vac = getVacanteMinima(horarioTempo2);
                if (vac > 0) {
                    horariosCarrera.add(horarioTempo2);
                    logger.debug("\tHorario Final {} vacantes: {}", vac, getHorarioString(horarioTempo2));
                    return;
                }
            }
        }

//        for (;;) {
        ordenSeccion++;
//        logger.debug("\tbuscar la seccion {} de {}", ordenSeccion, maxSecciones);
        if (ordenSeccion <= maxSecciones) {
//            logger.debug("\tpermuta otra seccion");
            permutarVarios(ordenCurso, ordenSeccion, cursos, mapSecciones, clonarMap(mapHorasDias), clonarLista(horarioTempo), horariosCarrera);
        } else {
//            logger.debug("\tFin de permutaciones");
//            break;
        }
//        }
//        logger.debug("\t******* FIN +++++++++++");
    }

    @Override
    @Transactional
    public void permutarUnico(
            int ordenCurso, int ordenSeccion,
            List<Curso> cursos, Map<Long, List<Seccion>> mapSecciones,
            Map<String, String> mapHorasDias, List<Seccion> horarioTempo, List<List<Seccion>> horariosCarrera) {

        Curso curso = getCursoOrden(cursos, ordenCurso);
        List<Seccion> seccionesCurso = mapSecciones.get(curso.getId());
        int maxSecciones = cantPermutaSeccion(seccionesCurso);

        List<Seccion> seccionesOrden = allSeccionByOrden(seccionesCurso, ordenSeccion);
        if (seccionesOrden.isEmpty()) {
            return;
        }

        boolean hayCruceHorario = hayCruceHorario(mapHorasDias, seccionesOrden);
        boolean hayVacantes = hayVacantes(seccionesOrden);

        if (!hayCruceHorario && hayVacantes) {
            addHoraDiaSecciones(mapHorasDias, seccionesOrden);
            for (Seccion seccion : seccionesOrden) {
                horarioTempo.add(seccion);
            }
            if (ordenCurso < cursos.size()) {
                int inicio = horariosCarrera.size();
                permutarUnico(ordenCurso + 1, 1, cursos, mapSecciones, mapHorasDias, horarioTempo, horariosCarrera);
                int fin = horariosCarrera.size();
                if (inicio != fin) {
                    return;
                }
            } else {
                Integer vac = getVacanteMinima(horarioTempo);
                if (vac > 0) {
                    horariosCarrera.add(horarioTempo);
                    //logger.debug("\tHorario Final {} vacantes: {}", vac, getHorarioString(horarioTempo));
                    return;
                }
            }

            for (Seccion seccion : seccionesOrden) {
                horarioTempo.remove(seccion);
            }
            removeHoraDiaSecciones(mapHorasDias, seccionesOrden);
        }

        ordenSeccion++;
        if (ordenSeccion <= maxSecciones) {
            permutarUnico(ordenCurso, ordenSeccion, cursos, mapSecciones, mapHorasDias, horarioTempo, horariosCarrera);
        }
    }

    private Integer getVacanteMinima(List<Seccion> horarioTempo) {
        Integer vac = 1000;
        for (Seccion seccion : horarioTempo) {
            Integer vacSecc = seccion.getVacantes();
            Integer matSecc = 0; //seccion.getMatriculados();
            if (vacSecc == null) {
                vacSecc = 0;
            }
            if (matSecc == null) {
                matSecc = 0;
            }
            vac = ((vacSecc - matSecc) < vac) ? (vacSecc - matSecc) : vac;
        }
        return vac;
    }

    private String getHorarioString(List<Seccion> horarioTempo) {
        String horario = "[";
        for (Seccion seccion : horarioTempo) {
            horario += horario.equals("[") ? "" : ",";
            horario += seccion.getCodigo();
        }
        horario += "]";
        return horario;
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

    private void addHoraDiaSecciones(Map<String, String> mapHorasDias, List<Seccion> secciones) {
        for (Seccion seccion : secciones) {
            List<HorarioSeccion> horasDias = seccion.getHorarioSeccion();
            for (HorarioSeccion horaDia : horasDias) {
                mapHorasDias.put(horaDia.getHoraDia(), horaDia.getHoraDia());
            }
        }
    }

    private void removeHoraDiaSecciones(Map<String, String> mapHorasDias, List<Seccion> secciones) {
        for (Seccion seccion : secciones) {
            List<HorarioSeccion> horasDias = seccion.getHorarioSeccion();
            for (HorarioSeccion horaDia : horasDias) {
                mapHorasDias.remove(horaDia.getHoraDia());
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

        Seccion seccion = seleccionados.get(0);
        Seccion sup = seccion.getSeccionSuperior();
        if (sup != null) {
            seleccionados.add(sup);
        }

        return seleccionados;
    }

    @Override
    public List<Curso> allCursosCarrera(List<CursoCachimbos> cursosCachimbos) {
        List<Curso> cursos = new ArrayList();
        for (CursoCachimbos cursoCachimbo : cursosCachimbos) {
            cursos.add(cursoCachimbo.getCurso());
        }
        return cursos;
    }

    private boolean hayVacantes(List<Seccion> seccionesOrden) {
        for (Seccion seccion : seccionesOrden) {
            if (seccion.getDisponiblesCachimbos() <= 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Map<String, HorarioCachimbos> mappingHorarios(List<HorarioCachimbos> horarios) {
        Map<String, HorarioCachimbos> map = new LinkedHashMap();
        for (HorarioCachimbos horario : horarios) {
            List<Seccion> secciones = new ArrayList();
            List<SeccionHorarioCachimbos> seccionesHorCachimbos = horario.getSeccionHorarioCachimbos();
            for (SeccionHorarioCachimbos seccHorCachimbo : seccionesHorCachimbos) {
                secciones.add(seccHorCachimbo.getSeccion());
            }
            String huella = getHorarioString(secciones);
            map.put(huella, horario);
        }
        return map;
    }

    @Override
    public void reordernarSeccion(List<Curso> cursos, Map<Long, List<Seccion>> mapSecciones) {
        for (Curso curso : cursos) {
            logger.debug("Listado inicial");
            List<Seccion> seccionesCurso = mapSecciones.get(curso.getId());
            logger.debug("Cantidad curso secciones *** {} ", seccionesCurso.size());
            for (Seccion seccion : seccionesCurso) {
                logger.debug("\t" + seccion.getCodigo());
                seccion.setAleatorio(RandomStringUtils.randomAlphabetic(20));
                logger.debug("seccion cod random \t {}", seccion.getAleatorio());
                logger.debug("seccion cod suscritos \t {}", seccion.getSuscritos());
                logger.debug("seccion id  \t {}", seccion.getId());
            }
            Collections.sort(seccionesCurso, new Seccion.CompareSuscritosAleatorio());

            logger.debug("Listado reordenado");
            for (Seccion seccion : seccionesCurso) {
                logger.debug("\t" + seccion.getCodigo());
            }
        }
    }

    @Override
    public String getClave(SeccionHorarioCachimbos shc) {
        StringBuilder sb = new StringBuilder();
        sb.append(ObjectUtil.getParentTree(shc, "seccion.codigo").toString());
        sb.append(" ");
        sb.append(ObjectUtil.getParentTree(shc, "seccion.grupoHoras.codigo").toString());
        return sb.toString();
    }

    @Override
    @Transactional
    public void addAlumno(AlumnoHorario alumno) {
        HorarioCachimbos horarioCachimbos = horarioCachimbosDAO.find(alumno.getHorarioCachimbos());
        horarioCachimbos.setSuscritos(horarioCachimbos.getSuscritos() + 1);
        if (horarioCachimbos.getSuscritos() > horarioCachimbos.getCapacidad()) {
            throw new PhobosException("Sección sobrepaso su capacidad");
        }
        horarioCachimbosDAO.update(horarioCachimbos);
        logger.debug("id AlumnoHorario {}", alumno.getId());
        AlumnoHorario alumnoHorario = alumnoHorarioDAO.find(alumno);
        alumnoHorario.setHorarioCachimbos(horarioCachimbos);
        alumnoHorarioDAO.update(alumnoHorario);
    }

    @Override
    public List<AlumnoHorario> allAlumnoHorarioByHorario(HorarioCachimbos horario) {
        return alumnoHorarioDAO.allByHorario(horario);
    }

    @Override
    public List<CursoCachimbos> allCursoCachimbosByHorario(HorarioCachimbos horario, CicloAcademico cicloAcademico) {
        List<SeccionHorarioCachimbos> seccionesHorCachimbos = seccionHorarioCachimbosDAO.allByHorario(horario);
        HorarioCachimbos horarioCachimbos = horarioCachimbosDAO.find(horario);
        Carrera carrera = horarioCachimbos.getCarrera();
        List<Curso> cursos = new ArrayList();
        for (SeccionHorarioCachimbos seccionesHorCachimbo : seccionesHorCachimbos) {
            Curso cur = (Curso) ObjectUtil.getParentTree(seccionesHorCachimbo, "seccion.grupoSeccion.curso");
            cursos.add(cur);
        }
        if (cursos.isEmpty()) {
            return new ArrayList();
        }
        return cursoCachimbosDAO.allByCursoCiclo(cursos, cicloAcademico, carrera);
    }

    private int getSuscritos(Seccion secc, Map<Long, List<SeccionHorarioCachimbos>> seccionHorarioCachimbosMap) {
        List<SeccionHorarioCachimbos> sexHorarioCachimbo = seccionHorarioCachimbosMap.get(secc.getId());
        int totalSuscritos = 0;
        if (sexHorarioCachimbo != null) {
            for (SeccionHorarioCachimbos ss : sexHorarioCachimbo) {
                HorarioCachimbos hc = ss.getHorarioCachimbos();
                totalSuscritos += hc.getSuscritos();
            }
        }
        return totalSuscritos;
    }

}

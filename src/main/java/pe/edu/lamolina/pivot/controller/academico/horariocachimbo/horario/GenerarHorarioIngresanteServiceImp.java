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
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
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
    public void generar(CicloAcademico ciclo, ModalidadEstudio modalidad, DataSessionPivot ds) {

        Acumulador code = null;
        {
            HorarioCachimbos maxcode = horarioCachimbosDAO.findMaxCodeOrderByCiclo(ciclo);
            if (maxcode != null) {
                String codigo = maxcode.getCodigo();
                String numcode = codigo.substring(2);
                logger.debug("max code {}", codigo);
                logger.debug("max code {}", numcode);
                Integer numm = new Integer(numcode);
                Integer seed = numm + 1;
                code = new Acumulador(seed);
            } else {
                code = new Acumulador(1);
            }
        }

        List<List<Seccion>> horariosTotal = new ArrayList();
        List<Carrera> carreras = carreraDAO.allActivoByModalidad(modalidad);

        List<CursoCachimbos> cursoCachimbosTodos = cursoCachimbosDAO.allByCiclo(ciclo);
        Map<Long, List<CursoCachimbos>> mapCursosCachimbos = TypesUtil.convertListToMapList("carrera.id", cursoCachimbosTodos);
        List<Curso> cursosTodos = allCursosCarrera(cursoCachimbosTodos);

        List<Seccion> secciones = seccionDAO.allActivosByCursosCiclo(cursosTodos, ciclo);
        Map<Long, List<Seccion>> mapSecciones = TypesUtil.convertListToMapList("grupoSeccion.curso.id", secciones);
        Map<Long, Seccion> mapSeccionId = TypesUtil.convertListToMap("id", secciones);
        for (Seccion secc : secciones) {
            secc.setSuscritos(0);
            Seccion sup = secc.getSeccionSuperior();
            if (sup != null) {
                Seccion superior = mapSeccionId.get(sup.getId());
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

        List<AlumnoHorario> alumnos = alumnoHorarioDAO.allByCicloAcademico(ciclo);
        Map<Long, List<AlumnoHorario>> mapAlumnos = TypesUtil.convertListToMapList("alumno.carrera.id", alumnos);

        List<HorarioCachimbos> horariosBD = horarioCachimbosDAO.allByCiclo(ciclo);
        List<SeccionHorarioCachimbos> seccionesHorariosBD = seccionHorarioCachimbosDAO.allByHorarios(horariosBD);
        for (SeccionHorarioCachimbos seccHorarioCachimbo : seccionesHorariosBD) {
            Seccion secc = mapSeccionId.get(seccHorarioCachimbo.getSeccion().getId());
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
                if (cursoCachimbos.isEmpty()) {
                    continue;
                }
                List<Curso> cursos = allCursosCarrera(cursoCachimbos);

                Map<String, String> mapHorasDias = new LinkedHashMap();
                List<Seccion> horarioTempo = new ArrayList();
                logger.debug("Carrera {}", carrera.getNombre());
                reordernarSeccion(cursos, mapSecciones);
                permutarUnico(1, 1, cursos, mapSecciones, mapHorasDias, horarioTempo, horariosTotal);

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

        Integer vac = getVacanteMinima(horarioTempo);
        HorarioCachimbos horario = new HorarioCachimbos();
        horario.setCapacidad(vac);
        horario.setCarrera(carrera);
        horario.setCicloAcademico(ciclo);
        horario.setCursos(cursos);
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
            addSecciones(mapHorasDia2, seccionesOrden);
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
            addSecciones(mapHorasDias, seccionesOrden);
            for (Seccion seccion : seccionesOrden) {
                horarioTempo.add(seccion);
            }
            if (ordenCurso < cursos.size()) {
                permutarUnico(ordenCurso + 1, 1, cursos, mapSecciones, mapHorasDias, horarioTempo, horariosCarrera);
                if (!horariosCarrera.isEmpty()) {
                    return;
                }
            } else {
                Integer vac = getVacanteMinima(horarioTempo);
                if (vac > 0) {
                    horariosCarrera.add(horarioTempo);
                    logger.debug("\tHorario Final {} vacantes: {}", vac, getHorarioString(horarioTempo));
                    return;
                }
            }
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
            for (Seccion seccion : seccionesCurso) {
                logger.debug("\t" + seccion.getCodigo());
                seccion.setAleatorio(RandomStringUtils.randomAlphabetic(20));
            }
            Collections.sort(seccionesCurso, new Seccion.CompareAleatorio());

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

}

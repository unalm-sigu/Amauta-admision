package pe.edu.lamolina.pivot.controller.horariocachimbo.generar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
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
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.RestriccionCarrera;
import pe.edu.lamolina.model.academico.RestriccionFacultad;
import pe.edu.lamolina.model.academico.RestriccionModalidad;
import pe.edu.lamolina.model.academico.RestriccionRepitencia;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoVacanteEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoAlumnoHorarioEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.horario.SeccionCursoCachimbos;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionCarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionFacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionModalidadDAO;
import pe.edu.lamolina.pivot.dao.academico.RestriccionRepitenciaDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.horario.HoraDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionCursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;
import pe.edu.lamolina.pivot.zelper.misc.Acumulador;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class HorarioCachimboGenerarServiceImp implements HorarioCachimboGenerarService {

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

    @Autowired
    CarreraCachimbosDAO carreraCachimbosDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Autowired
    CrearHorarioService crearHorarioService;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    RestriccionCarreraDAO restriccionCarreraDAO;
    @Autowired
    RestriccionFacultadDAO restriccionFacultadDAO;
    @Autowired
    RestriccionModalidadDAO restriccionModalidadDAO;
    @Autowired
    RestriccionRepitenciaDAO restriccionRepitenciaDAO;

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
    public void delete(HorarioCachimbos horarioCachimbos, CicloAcademico ciclo, Usuario usuario) {
        HorarioCachimbos horarioDb = horarioCachimbosDAO.find(horarioCachimbos);
        if (horarioDb == null) {
            return;
        }
        CarreraCachimbos cc = carreraCachimbosDAO.findByCarreraCiclo(horarioDb.getCarrera(), ciclo);
        cc.setConHorario(cc.getConHorario() - horarioDb.getSuscritos());
        cc.setSinHorario(cc.getSinHorario() + horarioDb.getSuscritos());
        carreraCachimbosDAO.update(cc);
        seccionHorarioCachimbosDAO.deleteByHorarioCachimbos(horarioCachimbos);
        List<AlumnoHorario> alumnos = alumnoHorarioDAO.allByHorarioCachimbos(horarioCachimbos);
        for (AlumnoHorario alumno : alumnos) {
            alumno.setHorarioCachimbos(null);
            alumno.setEstado(EstadoAlumnoHorarioEnum.PEND);
            alumnoHorarioDAO.update(alumno);
            List<VacanteAlumno> vacanteAlumnos = vacanteAlumnoDAO.allByAlumno(alumno.getAlumno());
            for (VacanteAlumno vacanteAlumno : vacanteAlumnos) {
                vacanteAlumno.setAlumno(null);
                vacanteAlumno.setUserRegistro(usuario);
                vacanteAlumno.setFechaRegistro(new Date());
                vacanteAlumno.setEstado(AlumnoVacanteEstadoEnum.LIBE.name());
                Seccion seccion = vacanteAlumno.getSeccion();
                seccion.setReservados(seccion.getReservados() - 1);
                seccionDAO.update(seccion);
                vacanteAlumnoDAO.update(vacanteAlumno);
            }

        }
        horarioCachimbosDAO.delete(horarioDb);
    }

    @Override
    @Transactional
    public void delete(HorarioCachimboForm form, CicloAcademico ciclo, Usuario usuario) {
        Map<Long, CarreraCachimbos> carreraCachimbos = carreraCachimbosDAO.allByCicloAcademico(ciclo)
                .stream()
                .collect(Collectors.toMap(x -> x.getCarrera().getId(), x -> x, (a, b) -> a));

        for (HorarioCachimbos horarioCachimbos : form.getHorarioCachimbos()) {
            HorarioCachimbos horarioDb = horarioCachimbosDAO.find(horarioCachimbos);
            if (horarioDb == null) {
                continue;
            }
            CarreraCachimbos cc = carreraCachimbos.get(horarioDb.getCarrera().getId());
            cc.setConHorario(cc.getConHorario() - horarioDb.getSuscritos());
            cc.setSinHorario(cc.getSinHorario() + horarioDb.getSuscritos());
            carreraCachimbosDAO.update(cc);
            seccionHorarioCachimbosDAO.deleteByHorarioCachimbos(horarioCachimbos);
            List<AlumnoHorario> ahs = alumnoHorarioDAO.allByHorarioCachimbos(horarioCachimbos);
            for (AlumnoHorario ah : ahs) {
                ah.setHorarioCachimbos(null);
                ah.setEstado(EstadoAlumnoHorarioEnum.PEND);
                List<VacanteAlumno> vacanteAlumnos = vacanteAlumnoDAO.allByAlumno(ah.getAlumno());
                for (VacanteAlumno vacanteAlumno : vacanteAlumnos) {
                    vacanteAlumno.setAlumno(null);
                    vacanteAlumno.setUserRegistro(usuario);
                    vacanteAlumno.setFechaRegistro(new Date());
                    vacanteAlumno.setEstado(AlumnoVacanteEstadoEnum.LIBE.name());
                    Seccion seccion = vacanteAlumno.getSeccion();
                    seccion.setReservados(seccion.getReservados() - 1);
                    seccionDAO.update(seccion);
                    vacanteAlumnoDAO.update(vacanteAlumno);
                }
                alumnoHorarioDAO.update(ah);
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
    public List<Carrera> allCarrera(ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico) {
        List<Carrera> carreras = new ArrayList();
        List<CarreraCachimbos> carrerasCachimbos = carreraCachimbosDAO.allByCicloAcademico(cicloAcademico);
        for (CarreraCachimbos carrCach : carrerasCachimbos) {
            carreras.add(carrCach.getCarrera());
        }
        return carreras;
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
        return horaDAO.all();
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
    public void generar(CicloAcademico ciclo, ModalidadEstudio modalidad, DataSessionPivot ds) {
        List<AlumnoHorario> alumnos = alumnoHorarioDAO.allByCicloAcademico(ciclo);
        this.generarHorario(ciclo, modalidad, ds, alumnos);
    }

    @Override
    public void generarHorario(CicloAcademico ciclo, ModalidadEstudio modalidad, DataSessionPivot ds, List<AlumnoHorario> alumnosHoarios) {
        logger.debug("ciclo {}", ciclo.getId());
        logger.debug("modalidad {}", modalidad.getId());

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

        List<CarreraCachimbos> carreraCachimbos = carreraCachimbosDAO.allByCicloAcademico(ciclo);
        Map<Long, CarreraCachimbos> mapCarreraCachimbos = TypesUtil.convertListToMap("carrera.id", carreraCachimbos);

        List<List<Seccion>> horariosTotal = new ArrayList();
//        List<Carrera> carreras = carreraDAO.allActivasByModalidad(modalidad);
        List<Carrera> carreras = allCarrera(modalidad, ciclo);

        List<CursoCachimbos> cursoCachimbosTodos = cursoCachimbosDAO.allByCicloFromSeccionCursoCachimbo(ciclo);
        List<CursoCachimbos> cursoCachimbosCiclo = cursoCachimbosDAO.allByCiclo(ciclo);

        Map<Long, List<CursoCachimbos>> mapCursosCachimbos = TypesUtil.convertListToMapList("carrera.id", cursoCachimbosTodos);
        Map<Long, List<CursoCachimbos>> mapCursosCachimbosVer = TypesUtil.convertListToMapList("carrera.id", cursoCachimbosCiclo);

        for (Map.Entry<Long, List<CursoCachimbos>> entry : mapCursosCachimbosVer.entrySet()) {
            Long idCarrera = entry.getKey();
            CarreraCachimbos carr = mapCarreraCachimbos.get(idCarrera);
            List<CursoCachimbos> cursosVer = entry.getValue();
            List<CursoCachimbos> cursosExisten = mapCursosCachimbos.get(idCarrera);
            if (cursosExisten == null) {
                throw new PhobosException("No existe cursos programados para los ingresantes de la carrera " + carr.getCarrera().getNombre());
            }
            if (cursosVer.size() != cursosExisten.size()) {
                throw new PhobosException("Faltan seleccionar claves para los ingresantes de la carrera " + carr.getCarrera().getNombre());
            }
        }

        List<Curso> cursosTodos = allCursosCarrera(cursoCachimbosTodos);

        List<Seccion> secciones = seccionDAO.allActivosByCursosCiclo(cursosTodos, ciclo);

        List<SeccionCursoCachimbos> seccionesCachimbos = seccionCursoCachimbosDAO.allByCiclo(ciclo);

        List<VacanteAlumno> vacanteAlumnos = vacanteAlumnoDAO.allBySecciones(secciones);
        Map<Long, List<VacanteAlumno>> vacanteAlumnosMap = TypesUtil.convertListToMapList("seccion.id", vacanteAlumnos);
        if (vacanteAlumnosMap == null) {
            vacanteAlumnosMap = new LinkedHashMap();
        }
        Map<Long, List<SeccionCursoCachimbos>> mapSeccionesCachimbos = TypesUtil.convertListToMapList("cursoCachimbos.carrera.id", seccionesCachimbos);
        Map<Long, Seccion> mapSeccionMain = TypesUtil.convertListToMap("id", secciones);

        List<SeccionHorarioCachimbos> seccionHorarioCachimbos = seccionHorarioCachimbosDAO.allBySeccions(ciclo, secciones);
        Map<Long, List<SeccionHorarioCachimbos>> seccionHorarioCachimbosMap = TypesUtil.convertListToMapList("seccion.id", seccionHorarioCachimbos);

        List<Alumno> alumnos = alumnosHoarios.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<MatriculaSeccion> matriculadosSecciones = matriculaSeccionDAO.allMatriculadosByAlumnosCiclo(alumnos, ciclo);
        for (MatriculaSeccion matSeccion : matriculadosSecciones) {
            Seccion sec = matSeccion.getSeccion();
            Seccion seccion = mapSeccionMain.get(sec.getId());
            if (seccion == null) {
                seccion = sec;
                mapSeccionMain.put(sec.getId(), sec);
            }
            matSeccion.setSeccion(seccion);
        }

        for (Seccion secc : secciones) {
            int sus = this.getSuscritos(secc, seccionHorarioCachimbosMap);
            secc.setReservados(sus);
            Seccion sup = secc.getSeccionSuperior();
            if (sup != null) {
                Seccion superior = mapSeccionMain.get(sup.getId());
                secc.setSeccionSuperior(superior);
            }

        }

        List<HorarioSeccion> horaDiaSecciones = horarioSeccionDAO.allBySecciones(secciones);
        Map<Long, List<HorarioSeccion>> mapHorarios = TypesUtil.convertListToMapList("seccion.id", horaDiaSecciones);

        List<RestriccionCarrera> restriccionCarr = restriccionCarreraDAO.allActivasBySecciones(secciones);
        List<RestriccionFacultad> restriccionFac = restriccionFacultadDAO.allActivasBySecciones(secciones);
        List<RestriccionModalidad> restriccionMod = restriccionModalidadDAO.allActivasBySecciones(secciones);
        List<RestriccionRepitencia> restriccionRep = restriccionRepitenciaDAO.allActivasBySecciones(secciones);

        Map<Long, List<RestriccionCarrera>> mapRestricCarr = TypesUtil.convertListToMapList("seccion.id", restriccionCarr);
        Map<Long, List<RestriccionFacultad>> mapRestricFac = TypesUtil.convertListToMapList("seccion.id", restriccionFac);
        Map<Long, List<RestriccionModalidad>> mapRestricMod = TypesUtil.convertListToMapList("seccion.id", restriccionMod);
        Map<Long, List<RestriccionRepitencia>> mapRestricRep = TypesUtil.convertListToMapList("seccion.id", restriccionRep);

        for (Seccion seccion : secciones) {
            List<HorarioSeccion> horariosSecc = mapHorarios.get(seccion.getId());
            horariosSecc = (horariosSecc == null) ? new ArrayList() : horariosSecc;
            seccion.setHorarioSeccion(horariosSecc);

            List<RestriccionCarrera> restriccCarSec = mapRestricCarr.get(seccion.getId());
            List<RestriccionFacultad> restriccFacSec = mapRestricFac.get(seccion.getId());
            List<RestriccionModalidad> restriccModSec = mapRestricMod.get(seccion.getId());
            List<RestriccionRepitencia> restriccRepSec = mapRestricRep.get(seccion.getId());

            restriccCarSec = (restriccCarSec == null) ? new ArrayList() : restriccCarSec;
            restriccFacSec = (restriccFacSec == null) ? new ArrayList() : restriccFacSec;
            restriccModSec = (restriccModSec == null) ? new ArrayList() : restriccModSec;
            restriccRepSec = (restriccRepSec == null) ? new ArrayList() : restriccRepSec;

            seccion.setRestriccionesCarrera(restriccCarSec);
            seccion.setRestriccionesFacultad(restriccFacSec);
            seccion.setRestriccionesModalidad(restriccModSec);
            seccion.setRestriccionesRepitencia(restriccRepSec);
        }

        Map<Long, List<AlumnoHorario>> mapAlumnos = TypesUtil.convertListToMapList("alumno.carrera.id", alumnosHoarios);

        List<HorarioCachimbos> horariosBD = horarioCachimbosDAO.allByCiclo(ciclo);
        List<SeccionHorarioCachimbos> seccionesHorariosBD = seccionHorarioCachimbosDAO.allByHorarios(horariosBD);

        for (SeccionHorarioCachimbos seccHorarioCachimbo : seccionesHorariosBD) {
            Seccion seccion = mapSeccionMain.get(seccHorarioCachimbo.getSeccion().getId());
            seccHorarioCachimbo.setSeccion(seccion);

        }

        Map<Long, List<SeccionHorarioCachimbos>> mapSeccionHorario = TypesUtil.convertListToMapList("horarioCachimbos.id", seccionesHorariosBD);
        for (HorarioCachimbos horario : horariosBD) {
            List<SeccionHorarioCachimbos> seccionesHorarioCachimbo = mapSeccionHorario.get(horario.getId());
            seccionesHorarioCachimbo = (seccionesHorarioCachimbo == null) ? new ArrayList() : seccionesHorarioCachimbo;
            horario.setSeccionHorarioCachimbos(seccionesHorarioCachimbo);
        }

        Map<Long, List<Seccion>> mapAlumnoSecciones = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", "seccion", matriculadosSecciones);
        //Map<Long, List<Seccion>> mapAlumnoCursos = TypesUtil.convertListToMapList("matriculaResumen.alumno.id", "seccion.grupoSeccion.curso", matriculadosSecciones);

        Map<String, String> mapOrdenBusqueda = new LinkedHashMap();

        Map<String, HorarioCachimbos> mapHorario = mappingHorarios(horariosBD);
        for (;;) {
            boolean noHayAlumnos = true;
            BUCLE_CARRERA:
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
                Map<Long, Seccion> mapSeccionesAlumno;
                Map<Long, Curso> mapCursosAlumno;

                List<Seccion> seccionesAlumno = mapAlumnoSecciones.get(alumno.getId());
                seccionesAlumno = (seccionesAlumno == null) ? new ArrayList() : seccionesAlumno;

                int busquedas = 0;
                boolean conHorario = false;
                long t1 = System.currentTimeMillis();

                for (;;) {
                    busquedas++;
                    mapHorasDias = new LinkedHashMap();
                    horarioTempo = new ArrayList();
                    mapCursos = new LinkedHashMap();
                    mapSeccionesAlumno = new LinkedHashMap();
                    mapCursosAlumno = new LinkedHashMap();

                    for (Seccion seccion : seccionesAlumno) {
                        mapSeccionesAlumno.put(seccion.getId(), seccion);
                        horarioTempo.add(seccion);
                        if (seccion.getTipoSeccionEnum() != TipoSeccionEnum.TCUR) {
                            Curso curso = seccion.getGrupoSeccion().getCurso();
                            mapCursosAlumno.put(curso.getId(), curso);
                        }
                    }

                    if (!horarioTempo.isEmpty()) {
                        String hhh = "[";
                        for (Seccion seccion : horarioTempo) {
                            hhh += hhh.equals("[") ? "" : "-";
                            hhh += seccion.getCodigo2();
                        }
                        hhh += "]";
                        System.out.println(alumno.getAlumno().getCodigo() + ": previo:" + hhh);
                        //logger.debug("horario previo de {} es {}", alumno.getAlumno().getCodigo(), hhh);
                    }

                    long t10 = System.currentTimeMillis();
                    String ordenKey = reordernarSeccion(cursos, mapSeccionesCarrera);
                    if (mapOrdenBusqueda.get(ordenKey) != null) {
                        for (;;) {
                            ordenKey = reordernarSeccion(cursos, mapSeccionesCarrera);
                            if (mapOrdenBusqueda.get(ordenKey) == null) {
                                break;
                            }
                            long t20 = System.currentTimeMillis();
                            if (t20 - t10 > 5000) {
                                alumnoCarr.remove(alumno);
                                System.out.println(carrera.getCodigo() + ": NO-HAY-COMBINACIONES");
                                //logger.debug("Ya no existen combinaciones de secciones que puedan crear un horario para {}", carrera.getCodigo());
                                break BUCLE_CARRERA;
                            }
                        }
                    }
                    mapOrdenBusqueda.put(ordenKey, ordenKey);
                    //logger.debug("Buscando en: {}", ordenKey);

                    permutarUnico(1, 1, cursos, mapSeccionesCarrera, mapHorasDias, horarioTempo, horariosTotal, mapSeccionesAlumno, mapCursosAlumno, carrera);
                    for (Seccion seccion : horarioTempo) {
                        Curso curso = seccion.getGrupoSeccion().getCurso();
                        Curso cursoAntes = mapCursosAlumno.get(curso.getId());
                        if (cursoAntes == null) {
                            mapCursos.put(curso.getId(), curso);
                        }
                    }
                    //if (cursos.size() == mapCursos.size()) {
                    if (cursosExisten(cursos, mapCursos)) {
                        conHorario = true;
                        break;
                    }
                    if (busquedas > 10) {
                        long t2 = System.currentTimeMillis();
                        if (t2 - t1 > 2000) {
                            System.out.println(alumno.getAlumno().getCodigo() + ":SIN-HOR carrera:" + carrera.getCodigo());
                            //logger.info("No se pudo ubicar horario para el alumno {} carrera {} ", alumno.getAlumno().getCodigo(), carrera.getCodigo());
                            break;
                        }
                        //logger.info("Se sigue buscando horario para el alumno {} carrera {} ", alumno.getAlumno().getCodigo(), carrera.getCodigo());
                    }
                }

                if (conHorario && !horarioTempo.isEmpty()) {
                    if (!horarioTempo.isEmpty()) {
                        String hhh = "[";
                        for (Seccion seccion : horarioTempo) {
                            hhh += hhh.equals("[") ? "" : "-";
                            hhh += seccion.getCodigo2();
                        }
                        hhh += "]";
                        System.out.println(alumno.getAlumno().getCodigo() + ": final:" + hhh);
                        //logger.debug("horario final de {} es {}", alumno.getAlumno().getCodigo(), hhh);
                    }
                    crearHorarioService.saveHorario(alumno, cursos, horarioTempo, carrera, ciclo, mapHorario, mapCarreraCachimbos, vacanteAlumnosMap, code, ds);
                }

                alumnoCarr.remove(alumno);
            }
            if (noHayAlumnos) {
                break;
            }
        }
    }

    private boolean cursosExisten(List<Curso> cursos, Map<Long, Curso> mapCursos) {
        for (Curso curso : cursos) {
            Curso cur = mapCursos.get(curso.getId());
            if (cur == null) {
                return false;
            }
        }
        return true;
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
    public void permutarUnico(
            int ordenCurso, int ordenSeccion,
            List<Curso> cursos, Map<Long, List<Seccion>> mapSecciones,
            Map<String, String> mapHorasDias, List<Seccion> horarioTempo, List<List<Seccion>> horariosCarrera,
            Map<Long, Seccion> mapSeccionesAlumno, Map<Long, Curso> mapCursosAlumno, Carrera carrera) {

        Curso curso = getCursoOrden(cursos, ordenCurso);
        Curso cursoAntes = mapCursosAlumno.get(curso.getId());
        if (cursoAntes != null) {
            permutarUnico(ordenCurso + 1, 1, cursos, mapSecciones, mapHorasDias, horarioTempo, horariosCarrera, mapSeccionesAlumno, mapCursosAlumno, carrera);
            return;
        }

        List<Seccion> seccionesCurso = mapSecciones.get(curso.getId());
        int maxSecciones = cantPermutaSeccion(seccionesCurso);

        List<Seccion> seccionesOrden = allSeccionByOrden(seccionesCurso, ordenSeccion);
        if (seccionesOrden.isEmpty()) {
            return;
        }

        boolean sinCruceHorario = sinCruceHorario(mapHorasDias, seccionesOrden);
        boolean hayVacantes = hayVacantes(seccionesOrden);
        boolean noTieneRestricc = noTieneRestricciones(seccionesOrden, carrera);

        if (sinCruceHorario && hayVacantes && noTieneRestricc) {
            addHoraDiaSecciones(mapHorasDias, seccionesOrden);
            for (Seccion seccion : seccionesOrden) {
                horarioTempo.add(seccion);
            }
            if (ordenCurso < cursos.size()) {
                int inicio = horariosCarrera.size();
                permutarUnico(ordenCurso + 1, 1, cursos, mapSecciones, mapHorasDias, horarioTempo, horariosCarrera, mapSeccionesAlumno, mapCursosAlumno, carrera);
                int fin = horariosCarrera.size();
                if (inicio != fin) {
                    return;
                }
            } else {
                Integer vac = getVacanteMinima(horarioTempo);
                if (vac > 0) {
                    horariosCarrera.add(horarioTempo);
                    System.out.println("vac:" + vac + " / hor-final:" + getHorarioString(horarioTempo));
                    //logger.debug("\tHorario Final {} vacantes: {}", vac, getHorarioString(horarioTempo));
                    return;
                }
            }

            for (Seccion seccion : seccionesOrden) {
                horarioTempo.remove(seccion);
            }
            removeHoraDiaSecciones(mapHorasDias, seccionesOrden, mapSeccionesAlumno);
        }

        ordenSeccion++;
        if (ordenSeccion <= maxSecciones) {
            permutarUnico(ordenCurso, ordenSeccion, cursos, mapSecciones, mapHorasDias, horarioTempo, horariosCarrera, mapSeccionesAlumno, mapCursosAlumno, carrera);
        }
    }

    private boolean noTieneRestricciones(List<Seccion> secciones, Carrera carrera) {
        for (Seccion seccion : secciones) {
            List<RestriccionCarrera> restriccCarr = seccion.getRestriccionesCarrera();
            if (!restriccCarr.isEmpty()) {
                boolean ok = false;
                for (RestriccionCarrera rCarr : restriccCarr) {
                    if (rCarr.getCarrera().getId().compareTo(carrera.getId()) == 0) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    return false;
                }
            }
            List<RestriccionFacultad> restriccFac = seccion.getRestriccionesFacultad();
            if (!restriccFac.isEmpty()) {
                boolean ok = false;
                for (RestriccionFacultad rFacu : restriccFac) {
                    if (rFacu.getFacultad().getId().compareTo(carrera.getFacultad().getId()) == 0) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    return false;
                }
            }
            List<RestriccionModalidad> restriccMod = seccion.getRestriccionesModalidad();
            if (!restriccMod.isEmpty()) {
                boolean ok = false;
                for (RestriccionModalidad rMod : restriccMod) {
                    if (rMod.getModalidadEstudio().getId().compareTo(carrera.getModalidadEstudio().getId()) == 0) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    return false;
                }
            }
            List<RestriccionRepitencia> restriccRep = seccion.getRestriccionesRepitencia();
            if (!restriccRep.isEmpty()) {
                boolean ok = false;
                for (RestriccionRepitencia rRep : restriccRep) {
                    if (rRep.getTipoRepitencia().getCodigo().equals("ING")) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    return false;
                }
            }
        }
        return true;
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

    private void removeHoraDiaSecciones(Map<String, String> mapHorasDias, List<Seccion> secciones, Map<Long, Seccion> mapSeccionesAlumno) {
        for (Seccion seccion : secciones) {
            Seccion seccionAntes = mapSeccionesAlumno.get(seccion.getId());
            if (seccionAntes != null) {
                continue;
            }
            List<HorarioSeccion> horasDias = seccion.getHorarioSeccion();
            for (HorarioSeccion horaDia : horasDias) {
                mapHorasDias.remove(horaDia.getHoraDia());
            }
        }
    }

    private boolean sinCruceHorario(Map<String, String> mapHorasDias, List<Seccion> secciones) {
        for (Seccion seccion : secciones) {
            List<HorarioSeccion> horasDias = seccion.getHorarioSeccion();
            for (HorarioSeccion horaDia : horasDias) {
                String horaDiaMapeada = mapHorasDias.get(horaDia.getHoraDia());
                if (horaDiaMapeada != null) {
                    return false;
                }
            }
        }
        return true;
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

    //@Override
    private List<Curso> allCursosCarrera(List<CursoCachimbos> cursosCachimbos) {
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

    private Map<String, HorarioCachimbos> mappingHorarios(List<HorarioCachimbos> horarios) {
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

    private String reordernarSeccion(List<Curso> cursos, Map<Long, List<Seccion>> mapSecciones) {
        String key = "";
        for (Curso curso : cursos) {
            key += key.equals("") ? "" : "|";
            key += curso.getCodigo() + ":";
            List<Seccion> seccionesCurso = mapSecciones.get(curso.getId());
            for (Seccion seccion : seccionesCurso) {
                seccion.setAleatorio(RandomStringUtils.randomAlphabetic(20));
            }
            Collections.sort(seccionesCurso, new Seccion.CompareReservadosAleatorio());

            for (Seccion seccion : seccionesCurso) {
                key += "[" + seccion.getCodigo() + "]";
            }
        }
        return key;
    }

    @Override
    public String getHoraSeccion(SeccionHorarioCachimbos shc) {
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
        return cursoCachimbosDAO.allByCursosCicloCarrera(cursos, cicloAcademico, carrera);
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

    @Override
    public List<GrupoSeccion> allGrupoSeccionByHorario(HorarioCachimbos horario, CicloAcademico cicloAcademico) {

        List<SeccionHorarioCachimbos> seccionesHorCachimbos = seccionHorarioCachimbosDAO.allByHorario(horario);
        Map<Long, GrupoSeccion> gseccionesMap = TypesUtil.convertListToMap("seccion.grupoSeccion.id", "seccion.grupoSeccion", seccionesHorCachimbos);
        Map<Long, Seccion> seccionesMap = TypesUtil.convertListToMap("seccion.id", "seccion", seccionesHorCachimbos);

        List<GrupoSeccion> gsecciones = new ArrayList(gseccionesMap.values());
        List<Seccion> secciones = new ArrayList(seccionesMap.values());

        Map<Long, List<Seccion>> mapSecciones = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);
        for (GrupoSeccion gseccion : gsecciones) {
            List<Seccion> seccionesGpo = mapSecciones.get(gseccion.getId());
            gseccion.setSecciones(seccionesGpo == null ? new ArrayList() : seccionesGpo);
            for (Seccion seccion : seccionesGpo) {
                seccion.setGrupoSeccion(gseccion);
            }
        }

        List<DocenteSeccion> docenteSeccion = docenteSeccionDAO.allPrincipalesBySecciones(secciones);
        Map<Long, List<DocenteSeccion>> mapDocSeccion = TypesUtil.convertListToMapList("seccion.id", docenteSeccion);

        for (Seccion seccion : secciones) {
            List<DocenteSeccion> doceentesSecc = mapDocSeccion.get(seccion.getId());
            doceentesSecc = (doceentesSecc == null) ? new ArrayList() : doceentesSecc;
            if (doceentesSecc.size() > 1) {
                DocenteSeccion docenteSecc = doceentesSecc.get(0);
                doceentesSecc = new ArrayList();
                doceentesSecc.add(docenteSecc);
            }
            seccion.setDocenteSeccion(doceentesSecc);
        }

        return gsecciones;
    }

}

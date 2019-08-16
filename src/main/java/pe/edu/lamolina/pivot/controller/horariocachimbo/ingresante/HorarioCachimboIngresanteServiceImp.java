package pe.edu.lamolina.pivot.controller.horariocachimbo.ingresante;

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
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.ActividadIngresante;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CarreraCachimbos;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfigRecorridoIngresante;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;
import pe.edu.lamolina.model.enums.AlumnoVacanteEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoAlumnoHorarioEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.RecorridoIngresanteEstadoEnum;
import pe.edu.lamolina.model.enums.TipoActividadIngresanteEnum;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.vacantes.VacanteAlumno;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioCachimbosDAO;
import pe.edu.lamolina.pivot.dao.horario.SeccionHorarioCachimbosDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.controller.horariocachimbo.generar.HorarioCachimboGenerarService;
import pe.edu.lamolina.pivot.dao.academico.ActividadIngresanteDAO;
import pe.edu.lamolina.pivot.dao.academico.ConfigRecorridoIngresanteDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoActividadIngresanteDAO;
import pe.edu.lamolina.pivot.dao.vacante.VacanteAlumnoDAO;

@Service
@Transactional(readOnly = true)
public class HorarioCachimboIngresanteServiceImp implements HorarioCachimboIngresanteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoHorarioDAO alumnoHorarioDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    HorarioCachimbosDAO horarioCachimbosDAO;

    @Autowired
    CursoCachimbosDAO cursoCachimbosDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    HorarioCachimboGenerarService generarHorarioIngresanteService;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    CarreraCachimbosDAO carreraCachimbosDAO;

    @Autowired
    SeccionHorarioCachimbosDAO seccionHorarioCachimbosDAO;

    @Autowired
    VacanteAlumnoDAO vacanteAlumnoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    ConfigRecorridoIngresanteDAO configRecorridoIngresanteDAO;

    @Autowired
    ActividadIngresanteDAO actividadIngresanteDAO;

    @Autowired
    TipoActividadIngresanteDAO tipoActividadIngresanteDAO;

    @Override
    public List<AlumnoHorario> allAlumnoHorario(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return alumnoHorarioDAO.allByAlumnoHorario(filter, cicloAcademico);
    }

    @Override
    @Transactional
    public void addAlumno(Alumno alumno, CicloAcademico cicloAcademico) {
        logger.debug("alumno {} cicloAcademico {}", alumno.getId(), cicloAcademico.getId());
        Alumno alumnoDB = alumnoDAO.find(alumno);
        Carrera carrera = alumnoDB.getCarrera();

        AlumnoHorario alumnoHorario = alumnoHorarioDAO.findByAlumnoCiclo(alumnoDB, cicloAcademico);
        Assert.isNull(alumnoHorario, String.format("El alumno %s, ya se encuentra agregado.", alumnoDB.getPersona().getApellidosNombres()));

        alumnoHorario = new AlumnoHorario();
        alumnoHorario.setAlumno(alumno);
        alumnoHorario.setCicloAcademico(cicloAcademico);
        alumnoHorario.setEstado(EstadoAlumnoHorarioEnum.PEND);
        alumnoHorarioDAO.save(alumnoHorario);

        CarreraCachimbos ch = carreraCachimbosDAO.findByCarreraCiclo(carrera, cicloAcademico);

        if (ch == null) {
            ch = new CarreraCachimbos();
            ch.setCarrera(carrera);
            ch.setCicloAcademico(cicloAcademico);
            ch.setConHorario(0);
            ch.setHorarios(0);
            ch.setIngresantes(1);
            ch.setMatriculados(0);
            ch.setSinHorario(1);
            ch.setSuspendidos(0);
            carreraCachimbosDAO.save(ch);
        } else {
            Integer ingresantesTotal = 1 + ch.getIngresantes();
            ch.setIngresantes(ingresantesTotal);
            Integer ingresantesSinHorarioTotal = 1 + ch.getSinHorario();
            ch.setSinHorario(ingresantesSinHorarioTotal);
            carreraCachimbosDAO.update(ch);
        }

    }

    @Override
    @Transactional
    public void activarMatricula(AlumnoHorario alumnoHorario) {
        AlumnoHorario alumnoHorarioDb = alumnoHorarioDAO.find(alumnoHorario);
        alumnoHorarioDb.setEstado(EstadoAlumnoHorarioEnum.MATR);
        alumnoHorarioDAO.update(alumnoHorarioDb);

        Carrera carrera = (Carrera) ObjectUtil.getParentTree(alumnoHorarioDb, "alumno.carrera");
        CicloAcademico cicloAcademico = (CicloAcademico) ObjectUtil.getParentTree(alumnoHorarioDb, "cicloAcademico");

        CarreraCachimbos carreraCachimbos = carreraCachimbosDAO.findByCarreraCiclo(carrera, cicloAcademico);
        carreraCachimbos.setConHorario(carreraCachimbos.getConHorario() - 1);
        carreraCachimbos.setMatriculados(carreraCachimbos.getMatriculados() + 1);
        carreraCachimbosDAO.update(carreraCachimbos);

    }

    @Override
    @Transactional
    public void suspenderMatricula(AlumnoHorario alumnoHorario) {
        AlumnoHorario alumnoHorarioDb = alumnoHorarioDAO.find(alumnoHorario.getId());
        alumnoHorarioDb.setEstado(EstadoAlumnoHorarioEnum.SUSP);
        alumnoHorarioDAO.update(alumnoHorarioDb);

        Carrera carrera = (Carrera) ObjectUtil.getParentTree(alumnoHorarioDb, "alumno.carrera");
        CicloAcademico cicloAcademico = (CicloAcademico) ObjectUtil.getParentTree(alumnoHorarioDb, "cicloAcademico");

        CarreraCachimbos carreraCachimbos = carreraCachimbosDAO.findByCarreraCiclo(carrera, cicloAcademico);
        carreraCachimbos.setMatriculados(carreraCachimbos.getMatriculados() - 1);
        carreraCachimbos.setSuspendidos(carreraCachimbos.getSuspendidos() + 1);
        carreraCachimbosDAO.update(carreraCachimbos);

    }

    @Override
    @Transactional
    public void asignarHorario(AlumnoHorario alumnoHorario, DataSessionPivot ds) {

        List<AlumnoHorario> alumnos = alumnoHorarioDAO.allByAlumnoHorarioLikeList(alumnoHorario);

        if (alumnos == null || alumnos.isEmpty()) {
            return;
        }

        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        generarHorarioIngresanteService.generarHorario(ds.getCicloAcademico(), modalidad, ds, alumnos);

    }

    @Override
    @Transactional
    public void retirarHorario(AlumnoHorario alumnoHorario, Usuario usuario) {
        AlumnoHorario alumnoHorarioDb = alumnoHorarioDAO.find(alumnoHorario);
        if (alumnoHorarioDb == null) {
            return;
        }
        HorarioCachimbos horarioCachimbos = alumnoHorarioDb.getHorarioCachimbos();
        if (horarioCachimbos != null && horarioCachimbos.getSuscritos() > 0) {
            horarioCachimbos.setSuscritos(horarioCachimbos.getSuscritos() - 1);
            horarioCachimbosDAO.update(horarioCachimbos);
        }

        alumnoHorarioDb.setHorarioCachimbos(null);
        alumnoHorarioDb.setEstado(EstadoAlumnoHorarioEnum.PEND);
        alumnoHorarioDAO.update(alumnoHorarioDb);

        Carrera carrera = (Carrera) ObjectUtil.getParentTree(alumnoHorarioDb, "alumno.carrera");
        CicloAcademico cicloAcademico = (CicloAcademico) ObjectUtil.getParentTree(alumnoHorarioDb, "cicloAcademico");

        CarreraCachimbos carreraCachimbos = carreraCachimbosDAO.findByCarreraCiclo(carrera, cicloAcademico);
        carreraCachimbos.setConHorario(carreraCachimbos.getConHorario() - 1);
        carreraCachimbos.setSinHorario(carreraCachimbos.getSinHorario() + 1);
        carreraCachimbosDAO.update(carreraCachimbos);

        List<VacanteAlumno> vacanteAlumnos = vacanteAlumnoDAO.allByAlumno(alumnoHorarioDb.getAlumno());
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

    @Override
    public void buscarHorario(Alumno alumno, CicloAcademico cicloAcademico) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Alumno> allAlumnoByName(String nombre) {
        return alumnoDAO.allByName(nombre);
    }

    @Override
    @Transactional
    public void cargarIngresantes(CicloAcademico cicloAcademico, Usuario user) {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        List<AlumnoHorario> alumnoHorarios = alumnoHorarioDAO.allByCicloAcademico(cicloAcademico);
        List<CarreraCachimbos> carreraCachimbos = carreraCachimbosDAO.allByCicloAcademico(cicloAcademico);
        List<Alumno> existentes = alumnoHorarios.stream().map(AlumnoHorario::getAlumno).collect(Collectors.toList());
        List<Alumno> alumnosIngresantes = alumnoDAO.allIngresantePregradoByCiclo(modalidad, cicloAcademico, existentes);

        if (alumnosIngresantes.isEmpty()) {
            throw new PhobosException("No existen alumnos nuevos");
        }
        logger.debug("alumnosIngresantes  {}", alumnosIngresantes.size());

        Map<Long, Carrera> mapCarreras = new LinkedHashMap();
        Map<Long, Integer> mapIngresantes = new LinkedHashMap();
        Map<Long, CarreraCachimbos> mapCarreraCachimbos = TypesUtil.convertListToMap("carrera.id", carreraCachimbos);

        for (Alumno ingresante : alumnosIngresantes) {
            AlumnoHorario alumnoHorario = new AlumnoHorario();
            alumnoHorario.setAlumno(ingresante);
            alumnoHorario.setCicloAcademico(cicloAcademico);
            alumnoHorario.setEstado(EstadoAlumnoHorarioEnum.PEND);
            alumnoHorario.setFechaCreacion(new Date());
            alumnoHorario.setUserCreacion(user);
            alumnoHorarioDAO.save(alumnoHorario);

            Carrera carrera = ingresante.getCarrera();
            Integer cant = mapIngresantes.get(carrera.getId());
            if (cant == null) {
                logger.debug("Carrera ubicada: {}", carrera.getNombre());
            }
            cant = (cant == null) ? 1 : cant + 1;
            mapCarreras.put(carrera.getId(), carrera);
            mapIngresantes.put(carrera.getId(), cant);
        }

        logger.debug("Existe {} carreras en el map", mapCarreras.values().size());
        logger.debug("Hay {} carreras preexistentes en el map", mapCarreraCachimbos.values().size());

        for (Map.Entry<Long, CarreraCachimbos> entry : mapCarreraCachimbos.entrySet()) {
            logger.debug("id:{} carrera:{}", entry.getKey(), entry.getValue().getCarrera().getNombre());
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }

        for (Carrera carrera : mapCarreras.values()) {
            logger.debug("Creando {} carrera-cachimbo {}", carrera.getId(), carrera.getNombre());
            Integer ingresantes = mapIngresantes.get(carrera.getId());

            CarreraCachimbos ch = mapCarreraCachimbos.get(carrera.getId());
            logger.debug("\tCarrera-cachimno {}", ch);

            if (ch == null) {
                ch = new CarreraCachimbos();
                ch.setCarrera(carrera);
                ch.setCicloAcademico(cicloAcademico);
                ch.setConHorario(0);
                ch.setHorarios(0);
                ch.setIngresantes(ingresantes);
                ch.setMatriculados(0);
                ch.setSinHorario(ingresantes);
                ch.setSuspendidos(0);
                carreraCachimbosDAO.save(ch);
                mapCarreraCachimbos.put(carrera.getId(), ch);
                logger.debug("\tCarrera-cachimno nuevo {}", ch.getId());

            } else {
                ch.setIngresantes(ingresantes + ch.getIngresantes());
                ch.setSinHorario(ingresantes + ch.getSinHorario());
                carreraCachimbosDAO.update(ch);
                logger.debug("\tCarrera-cachimno editado {}", ch.getId());
            }
        }
    }

    @Override
    public List<Alumno> allAlumnoIngresantePregradoByNameCiclo(String nombre, CicloAcademico cicloAcademico) {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return alumnoDAO.allByNameModalidadEstudioCiclo(nombre, modalidad, cicloAcademico);
    }

    @Override
    @Transactional
    public void eliminarHorarios(CicloAcademico cicloAcademico, Usuario user) {
        seccionDAO.allRegenerateReservadoByCiclo(cicloAcademico);
        seccionHorarioCachimbosDAO.deleteAllByCiclo(cicloAcademico);
        alumnoHorarioDAO.allSetHorarioNullByCiclo(cicloAcademico);
        vacanteAlumnoDAO.deleteAllByCiclo(cicloAcademico);
        horarioCachimbosDAO.deleteAllByCiclo(cicloAcademico);
        carreraCachimbosDAO.allRegenerateByCiclo(cicloAcademico);
    }

    @Override
    public List<IngresanteCantidad> allIngresanteCantidad(CicloAcademico cicloAcademico) {
        List<AlumnoHorario> alumnoHorarios = alumnoHorarioDAO.allByCicloAcademico(cicloAcademico);
        Map<String, List<AlumnoHorario>> alumnoHorariosMap = TypesUtil.convertListToMapList("estado", alumnoHorarios);
        List<IngresanteCantidad> cantidad = new ArrayList();
        for (EstadoAlumnoHorarioEnum value : EstadoAlumnoHorarioEnum.values()) {
            List<AlumnoHorario> alumnos = alumnoHorariosMap.get(value.name());
            IngresanteCantidad ingresanteCantidad = new IngresanteCantidad();
            ingresanteCantidad.setEstado(value.name());
            ingresanteCantidad.setNombre(value.getValue());
            ingresanteCantidad.setCantidad(alumnos != null ? alumnos.size() : 0);
            ingresanteCantidad.setIdgen(EstadoAlumnoHorarioEnum.valueOf(value.name()).ordinal());
            cantidad.add(ingresanteCantidad);
        }
        return cantidad;
    }

    @Override
    @Transactional
    public void deleteIngresante(AlumnoHorario alumnoHorario, CicloAcademico cicloAcademico) {
        AlumnoHorario aluHorario = alumnoHorarioDAO.find(alumnoHorario);
        Carrera carrera = aluHorario.getAlumno().getCarrera();

        if (aluHorario.getHorarioCachimbos() == null) {
            alumnoHorarioDAO.delete(aluHorario);

            CarreraCachimbos cachimbos = carreraCachimbosDAO.findByCarreraCiclo(carrera, cicloAcademico);
            cachimbos.setIngresantes(cachimbos.getIngresantes() - 1);
            cachimbos.setSinHorario(cachimbos.getSinHorario() - 1);
            carreraCachimbosDAO.update(cachimbos);
        } else {
            throw new PhobosException("No se puede eliminar por que tiene un horario asignado.");
        }
    }

    @Override
    @Transactional
    public void matricular(CicloAcademico cicloAcademico, DataSessionPivot ds) {
        List<ConfigRecorridoIngresante> configRecorridoIngresantes = configRecorridoIngresanteDAO.allByCicloAcademico(cicloAcademico);
        Map<Long, ConfigRecorridoIngresante> mapConfigRecorrido = TypesUtil.convertListToMap("tipoActividadIngresante.id", configRecorridoIngresantes);

        List<ActividadIngresante> actividadIngresantes = actividadIngresanteDAO.allByCicloAcademico(cicloAcademico);
        TipoActividadIngresante tipoActividadIngresante = tipoActividadIngresanteDAO.findCodigo(TipoActividadIngresanteEnum.MATRI);
        Map<Long, List<ActividadIngresante>> mapActividadesIngresantes = TypesUtil.convertListToMapList("recorridoIngresante.alumno.id", actividadIngresantes);
        System.out.println("Total actividades-alumnos :::: " + actividadIngresantes.size());

        int actividadesPreMatricula = cantidadActividadesPreMatricula(configRecorridoIngresantes);

        List<HorarioCachimbos> horarios = horarioCachimbosDAO.allByCiclo(cicloAcademico);
        for (HorarioCachimbos horario : horarios) {
            if (horario.getMatriculados().intValue() >= horario.getSuscritos()) {
                System.out.println("No hay matriculables en el " + horario.getCodigo());
                continue;
            }

            List<SeccionHorarioCachimbos> seccionesHorario = seccionHorarioCachimbosDAO.allByHorario(horario);
            Map<Long, Curso> mapCurso = TypesUtil.convertListToMap("seccion.grupoSeccion.curso.id", "seccion.grupoSeccion.curso", seccionesHorario);
            Map<Long, List<Seccion>> mapSeccion = TypesUtil.convertListToMapList("seccion.grupoSeccion.curso.id", "seccion", seccionesHorario);
            List<Curso> cursos = new ArrayList(mapCurso.values());

            List<AlumnoHorario> alumnosHorario = alumnoHorarioDAO.allByHorario(horario);

            for (AlumnoHorario aluHorario : alumnosHorario) {
                if (aluHorario.getEstadoEnum() == EstadoAlumnoHorarioEnum.MATR) {
                    continue;
                }

                // estados de recorrido tienen que estar activo. menos el de matricula. RecorridoAlumno - ActividadIngresante
                Alumno alumno = aluHorario.getAlumno();

                List<ActividadIngresante> actividadesAlumno = TypesUtil.getListNotNull(mapActividadesIngresantes.get(alumno.getId()));

                int cantActividadAlumnoPreMatri = cantidadActividadesPreMatriculaAlumno(actividadesAlumno, mapConfigRecorrido);

                if (cantActividadAlumnoPreMatri < actividadesPreMatricula) {
                    //System.out.println("\tNo tiene la cantidad adecuada de actividades");
                    continue;
                }

                System.out.println("Alumno :::: " + alumno.getCodigo());
                System.out.println("\tingresante con " + actividadesAlumno.size() + " actividades");
                System.out.println("\tingresante con " + cantActividadAlumnoPreMatri + " actividades pre-matricula");

                MatriculaResumen matResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);
                List<MatriculaCurso> matCursos = matriculaCursoDAO.allByMatriculaResumen(matResumen);
                List<MatriculaSeccion> matSecciones = matriculaSeccionDAO.allByMatriculaResumen(matResumen);
                Map<Long, MatriculaCurso> mapMatriCursoAlu = TypesUtil.convertListToMap("curso.id", matCursos);
                Map<Long, List<MatriculaSeccion>> mapMatriSeccAlu = TypesUtil.convertListToMapList("seccion.grupoSeccion.curso.id", matSecciones);

                matResumen.setEstadoEnum(EstadoMatriculaEnum.MAT);

                for (Curso curso : cursos) {
                    System.out.println("\tMatricula " + curso.getCodigo() + " :::: " + curso.getNombre());
                    matResumen.setCreditosMatriculados(matResumen.getCreditosMatriculados() + curso.getCreditos());
                    matResumen.setCursosMatriculados(matResumen.getCursosMatriculados() + 1);

                    MatriculaCurso matCursoAlu = mapMatriCursoAlu.get(curso.getId());
                    if (matCursoAlu == null) {
                        matCursoAlu = new MatriculaCurso();
                        matCursoAlu.setCreditos(curso.getCreditos());
                        matCursoAlu.setCreditosAprobados(0);
                        matCursoAlu.setCurso(curso);
                        matCursoAlu.setEstadoEnum(EstadoMatriculaEnum.MAT);
                        matCursoAlu.setMatriculaResumen(matResumen);
                        matCursoAlu.setNotaAcumulada("0");
                        matCursoAlu.setNotaAcumuladaFull("0");
                        matCursoAlu.setNotaAvance("0");
                        matCursoAlu.setNotaAvanceFull("0");
                        matCursoAlu.setNotaFinal("0");
                        matCursoAlu.setPorcentajeAvanceNota(0);
                        matriculaCursoDAO.save(matCursoAlu);

                    } else {
                        matCursoAlu.setEstadoEnum(EstadoMatriculaEnum.MAT);
                        matriculaCursoDAO.update(matCursoAlu);
                    }

                    List<MatriculaSeccion> matSeccionesAlu = mapMatriSeccAlu.get(curso.getId());
                    matSeccionesAlu = (matSeccionesAlu == null) ? new ArrayList() : matSeccionesAlu;
                    for (MatriculaSeccion matSeccAlu : matSeccionesAlu) {
                        matSeccAlu.setEstadoEnum(EstadoMatriculaEnum.RET);
                    }
                    List<Seccion> seccionesCurso = mapSeccion.get(curso.getId());
                    for (Seccion seccion : seccionesCurso) {
                        MatriculaSeccion matSecCur = null;
                        for (MatriculaSeccion matSeccAlu : matSeccionesAlu) {
                            if (seccion.getId().compareTo(matSeccAlu.getSeccion().getId()) == 0) {
                                matSecCur = matSeccAlu;
                                break;
                            }
                        }
                        if (matSecCur != null) {
                            matSecCur.setEstadoEnum(EstadoMatriculaEnum.RET);
                            matriculaSeccionDAO.update(matSecCur);

                        } else {
                            matSecCur = new MatriculaSeccion();
                            matSecCur.setCreditos(curso.getCreditos());
                            matSecCur.setEstadoEnum(EstadoMatriculaEnum.MAT);
                            matSecCur.setMatriculaResumen(matResumen);
                            matSecCur.setSeccion(seccion);
                            matSecCur.setUserRegistro(ds.getUsuario());
                            matSecCur.setFechaRegistro(new Date());
                            matriculaSeccionDAO.save(matSecCur);
                        }
                        seccion.setMatriculados(seccion.getMatriculados() + 1);
                        seccion.setReservados(seccion.getReservados() - 1);
                        seccionDAO.update(seccion);
                    }

                }
                aluHorario.setEstado(EstadoAlumnoHorarioEnum.MATR);
                horario.setMatriculados(horario.getMatriculados() + 1);
                matriculaResumenDAO.update(matResumen);

                ActividadIngresante actividadIngresante = new ActividadIngresante();
                actividadIngresante.setEstadoEnum(RecorridoIngresanteEstadoEnum.ACT);
                actividadIngresante.setFechaRegistro(new Date());
                actividadIngresante.setRecorridoIngresante(actividadesAlumno.get(0).getRecorridoIngresante());
                actividadIngresante.setTipoActividadIngresante(tipoActividadIngresante);
                actividadIngresante.setUserEjecucion(ds.getUsuario());
                actividadIngresanteDAO.save(actividadIngresante);

            }
            horarioCachimbosDAO.update(horario);
        }
    }

    private int cantidadActividadesPreMatriculaAlumno(
            List<ActividadIngresante> actividadesAlumno,
            Map<Long, ConfigRecorridoIngresante> mapConfigRecorrido) {

        for (ActividadIngresante actIng : actividadesAlumno) {
            TipoActividadIngresante tipo = actIng.getTipoActividadIngresante();
            ConfigRecorridoIngresante cfg = mapConfigRecorrido.get(tipo.getId());
            actIng.setOrden(cfg.getOrdenActividad());
        }

        Collections.sort(actividadesAlumno, new ActividadIngresante.CompareOrden());

        int loop = 0;
        for (ActividadIngresante actIng : actividadesAlumno) {
            if (actIng.getEstadoEnum() != RecorridoIngresanteEstadoEnum.ACT) {
                continue;
            }

            TipoActividadIngresante tipo = actIng.getTipoActividadIngresante();
            if (tipo.getCodigoEnum() == TipoActividadIngresanteEnum.MATRI) {
                break;
            }
            loop++;
        }
        return loop;

    }

    private int cantidadActividadesPreMatricula(List<ConfigRecorridoIngresante> configRecorridoIngresantes) {
        int loop = 0;
        for (ConfigRecorridoIngresante cfg : configRecorridoIngresantes) {
            if (cfg.getTipoActividadIngresante().getCodigoEnum() == TipoActividadIngresanteEnum.MATRI) {
                break;
            }
            loop++;
        }
        return loop;
    }

}

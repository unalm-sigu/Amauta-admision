package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.ingresante;

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
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CarreraCachimbos;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoVacanteEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoAlumnoHorarioEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
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
import pe.edu.lamolina.pivot.controller.academico.horariocachimbo.generar.HorarioCachimboGenerarService;
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
        if (alumnoHorario == null) {
            alumnoHorario = new AlumnoHorario();
            alumnoHorario.setAlumno(alumno);
            alumnoHorario.setCicloAcademico(cicloAcademico);
            alumnoHorario.setEstado(EstadoAlumnoHorarioEnum.PEND.name());
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
                ch.setSinHorario(ingresantesTotal);
                carreraCachimbosDAO.update(ch);
            }

        }
    }

    @Override
    @Transactional
    public void activarMatricula(AlumnoHorario alumnoHorario) {
        AlumnoHorario alumnoHorarioDb = alumnoHorarioDAO.find(alumnoHorario);
        alumnoHorarioDb.setEstado(EstadoAlumnoHorarioEnum.MATR.name());
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
        alumnoHorarioDb.setEstado(EstadoAlumnoHorarioEnum.SUSP.name());
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
        alumnoHorarioDb.setEstado(EstadoAlumnoHorarioEnum.PEND.name());
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
        List<Alumno> alumnoExclude = alumnoHorarios.stream().map(AlumnoHorario::getAlumno).collect(Collectors.toList());
        List<Alumno> alumnosIngresantes = alumnoDAO.allIngresantePregradoByCiclo(modalidad, cicloAcademico, alumnoExclude);

        if (alumnosIngresantes.isEmpty()) {
            throw new PhobosException("No existen alumnos nuevos");
        }
        logger.debug("alumnosIngresantes  {}", alumnosIngresantes.size());

        Map<Long, Carrera> mapCarreras = new LinkedHashMap();
        Map<Long, Integer> mapIngresantes = new LinkedHashMap();
        Map<Long, CarreraCachimbos> mapCarreraCachimbos = TypesUtil.convertListToMap("carrera.id", carreraCachimbos);

        for (Alumno alumnosIngresante : alumnosIngresantes) {
            AlumnoHorario alumnoHorario = new AlumnoHorario();
            alumnoHorario.setAlumno(alumnosIngresante);
            alumnoHorario.setCicloAcademico(cicloAcademico);
            alumnoHorario.setEstado(EstadoAlumnoHorarioEnum.PEND.name());
            alumnoHorario.setFechaCreacion(new Date());
            alumnoHorario.setUserCreacion(user);
            alumnoHorarioDAO.save(alumnoHorario);

            Carrera carr = alumnosIngresante.getCarrera();
            Integer cant = mapIngresantes.get(carr.getId());
            cant = (cant == null) ? 1 : cant + 1;
            mapCarreras.put(carr.getId(), carr);
            mapIngresantes.put(carr.getId(), cant);
        }

        for (Carrera carrera : mapCarreras.values()) {
            Integer ingresantes = mapIngresantes.get(carrera.getId());

            CarreraCachimbos ch = mapCarreraCachimbos.get(carrera.getId());

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
                mapCarreraCachimbos.put(ch.getId(), ch);
            } else {
                ch.setIngresantes(ingresantes + ch.getIngresantes());
                ch.setSinHorario(ingresantes + ch.getSinHorario());
                carreraCachimbosDAO.update(ch);
            }
        }
    }

    @Override
    public List<Alumno> allAlumnoIngresantePregradoByNameCiclo(String nombre, CicloAcademico cicloAcademico) {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return alumnoDAO.allByNameModalidadEstudioCiclo(nombre, modalidad, cicloAcademico);
    }

    @Override
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

}

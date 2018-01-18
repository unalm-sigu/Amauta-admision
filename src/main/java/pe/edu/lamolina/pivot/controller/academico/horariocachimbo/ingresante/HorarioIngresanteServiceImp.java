package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.ingresante;

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
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CarreraCachimbos;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EstadoAlumnoHorarioEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.horario.HorarioCachimbos;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.academico.horariocachimbo.horario.GenerarHorarioIngresanteService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoHorarioDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoCachimbosDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioCachimbosDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class HorarioIngresanteServiceImp implements HorarioIngresanteService {

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
    GenerarHorarioIngresanteService generarHorarioIngresanteService;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    CarreraCachimbosDAO carreraCachimbosDAO;

    @Override
    public List<AlumnoHorario> allAlumnoHorario(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return alumnoHorarioDAO.allByAlumnoHorario(filter, cicloAcademico);
    }

    @Override
    @Transactional
    public void addAlumno(Alumno alumno, CicloAcademico cicloAcademico) {
        logger.debug("alumno {} cicloAcademico {}", alumno.getId(), cicloAcademico.getId());
        AlumnoHorario alumnoHorario = alumnoHorarioDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        if (alumnoHorario == null) {
            alumnoHorario = new AlumnoHorario();
            alumnoHorario.setAlumno(alumno);
            alumnoHorario.setCicloAcademico(cicloAcademico);
            alumnoHorario.setEstado(EstadoAlumnoHorarioEnum.PEND.name());
            alumnoHorarioDAO.save(alumnoHorario);
        }
    }

    @Override
    @Transactional
    public void activarMatricula(AlumnoHorario alumnoHorario) {
        AlumnoHorario alumnoHorarioDb = alumnoHorarioDAO.find(alumnoHorario.getId());
        alumnoHorarioDb.setEstado(EstadoAlumnoHorarioEnum.MATR.name());
        alumnoHorarioDAO.update(alumnoHorarioDb);
    }

    @Override
    @Transactional
    public void suspenderMatricula(AlumnoHorario alumnoHorario) {
        AlumnoHorario alumnoHorarioDb = alumnoHorarioDAO.find(alumnoHorario.getId());
        alumnoHorarioDb.setEstado(EstadoAlumnoHorarioEnum.PEND.name());
        alumnoHorarioDAO.update(alumnoHorarioDb);
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
    public void retirarHorario(AlumnoHorario alumnoHorario) {
        AlumnoHorario alumnoHorarioDb = alumnoHorarioDAO.find(alumnoHorario.getId());
        if (alumnoHorarioDb == null) {
            return;
        }
        HorarioCachimbos horarioCachimbos = alumnoHorarioDb.getHorarioCachimbos();
        if (horarioCachimbos != null && horarioCachimbos.getSuscritos() > 0) {
            horarioCachimbos.setSuscritos(horarioCachimbos.getSuscritos() - 1);
            horarioCachimbosDAO.update(horarioCachimbos);
        }
        alumnoHorarioDb.setHorarioCachimbos(null);
        alumnoHorarioDAO.update(alumnoHorarioDb);
    }

    @Override
    public void buscarHorario(Alumno alumno, CicloAcademico cicloAcademico) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Alumno> allAlumnoByName(String nombre) {
        return alumnoDAO.allAlumnoByName(nombre);
    }

    @Override
    @Transactional
    public void cargarIngresantes(CicloAcademico cicloAcademico, Usuario user) {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        List<AlumnoHorario> alumnoHorarios = alumnoHorarioDAO.allByCicloAcademico(cicloAcademico);
        List<Alumno> alumnoExclude = alumnoHorarios.stream().map(AlumnoHorario::getAlumno).collect(Collectors.toList());
        List<Alumno> alumnosIngresantes = alumnoDAO.allIngresantePregradoByCiclo(modalidad, cicloAcademico, alumnoExclude);

        if (alumnosIngresantes.isEmpty()) {
            throw new PhobosException("No existen alumnos nuevos");
        }

        Map<Long, Carrera> mapCarreras = new LinkedHashMap();
        Map<Long, Integer> mapIngresantes = new LinkedHashMap();

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
            CarreraCachimbos ch = new CarreraCachimbos();
            ch.setCarrera(carrera);
            ch.setCicloAcademico(cicloAcademico);
            ch.setConHorario(0);
            ch.setHorarios(0);
            ch.setIngresantes(ingresantes);
            ch.setMatriculados(0);
            ch.setSinHorario(ingresantes);
            ch.setSuspendidos(0);
            carreraCachimbosDAO.save(ch);
        }

    }

    @Override
    public List<Alumno> allAlumnoIngresantePregradoByNameCiclo(String nombre, CicloAcademico cicloAcademico) {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return alumnoDAO.allAlumnoIngresantePregradoByNameCiclo(nombre, modalidad, cicloAcademico);
    }

}

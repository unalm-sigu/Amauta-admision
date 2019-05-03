package pe.edu.lamolina.pivot.controller.consejeria.consejeros;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.consejeria.AlumnoConsejero;
import pe.edu.lamolina.model.consejeria.Consejero;
import static pe.edu.lamolina.model.enums.EstadoEnum.INA;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoService;
import pe.edu.lamolina.pivot.controller.general.oficina.OficinaService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.pivot.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ConsejerosServiceImp implements ConsejerosService {

    @Autowired
    AlumnoDAO AlumnoDAO;
    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;
    @Autowired
    ConsejeroDAO consejeroDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    ColaboradorDAO colaboradorDAO;
    @Autowired
    DocenteDAO docenteDAO;

    @Autowired
    OficinaService oficinaService;
    @Autowired
    AlumnoService alumnoService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Colaborador findColaboradorByIdPersona(Long idPersona) {
        return colaboradorDAO.findColaboradorByIdPersona(idPersona);
    }

    @Override
    public Carrera findbByNombre(Long idcarrera) {
        return carreraDAO.find(idcarrera);
    }

    @Override
    public List<Consejero> allByCarreraDynatable(Carrera carrera, DynatableFilter filter) {
        return consejeroDAO.allByCarreraDynatable(carrera, filter);
    }

    @Override
    public Consejero finByIdPersona(Persona persona) {
        return consejeroDAO.finByIdPersona(persona);
    }

    @Override
    public List<Carrera> allCarreraByPersonaCiclo(Persona persona, CicloAcademico ciclo) {
        List<Facultad> facultades = new ArrayList();
        List<Carrera> carreras = new ArrayList();
        logger.debug("***ciclo academico {}", ciclo.getDescripcion());

        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(persona);
        logger.debug("***cantidad oficina es {}", oficinasMain.size());

        for (Oficina oficina : oficinasMain) {
            logger.debug("codigo oficina es {}", oficina.getCodigo());
            logger.debug("tipo oficina es {} ", oficina.getTipoOficina().getCodigo());

            if (oficina.getCodigoEnum() == OficinaEnum.OERA) {
                return carreraDAO.allPregradoByCicloMatriculables(ciclo);
            }
            if (oficina.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.FAC) {
                facultades.add(new Facultad(oficina.getInstanciaOficina()));
            }
            if (oficina.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.ESP) {
                carreras.add(new Carrera(oficina.getInstanciaOficina()));
            }
        }

        logger.debug("Carreras previas es {} {} {}", carreras.size());
        if (!carreras.isEmpty()) {
            List<Carrera> carrerasCiclo = carreraDAO.allByMatriculablesCicloCarreras(carreras, ciclo);
            carreras.addAll(carrerasCiclo);
        }

        if (!facultades.isEmpty()) {
            List<Carrera> carrerasFac = carreraDAO.allByMatriculablesCicloFacultades(facultades, ciclo);
            carreras.addAll(carrerasFac);
        }

        return carreraDAO.allByCarreras(carreras);
    }

    @Override
    public List<Docente> allDocenteByNombreFacultad(String nombre, Facultad facultad) {
        return docenteDAO.allByNombreFacultad(nombre, facultad);
    }

    @Override
    @Transactional
    public void updateEstado(Consejero consejero, DataSessionPivot ds) {
        Consejero cons = consejeroDAO.find(consejero.getId());
        Consejero consejeroNN = new Consejero();
        consejeroNN.setId(Constantine.ID_CONSEJERO_NN); // consejero comodin NN
        cons.setEstado(consejero.getEstado());
        int cantidadAlumnos = cons.getAlumnosActivos() + cons.getAlumnosInactivos();
        logger.debug("cantidad de alumnos  {}", cantidadAlumnos);
        if (consejero.getEstado().equals(INA.name()) && cantidadAlumnos > 0) {
            /// en caso de desactivar al consejero, los alumnos asociados seran transaladados
            /// al consejero comodin NN 
            logger.debug("intentas inhabilitar {}", consejero.getEstado());
            List<Alumno> alumnos = this.allAlumnosByConsejero(cons);
            logger.debug("cantidad a mover {}", alumnos.size());

            List<AlumnoConsejero> alumnoConsejeros = alumnoConsejeroDAO.findAlumnoConsejeroByIdConsejero(cons);
            for (AlumnoConsejero alumnoConsejero : alumnoConsejeros) {
                alumnoConsejero.setConsejero(consejeroNN);  ///asignacion consejero comodin NN
                alumnoConsejeroDAO.update(alumnoConsejero);
            }

            for (Alumno alumno : alumnos) {
                alumno.setConsejero(consejeroNN);  ///asignacion consejero comodin NN
                AlumnoDAO.update(alumno);
            }

            cons.setAlumnosActivos(0);
            cons.setAlumnosInactivos(0);
        }
        consejeroDAO.update(cons);
    }

    @Override
    @Transactional
    public void saveConsejeroByDocente(Docente docente, DataSessionPivot ds) {

        Consejero consejero = new Consejero();

        Colaborador colaborador = this.findColaboradorByIdPersona(docente.getPersona().getId());

        Carrera carrera = this.findbByNombre(docente.getCarrera().getId());

        consejero.setEstado(docente.getEstadoEnum().name());
        consejero.setFechaRegistro(new Date());
        consejero.setFechaModificacion(new Date());
        consejero.setFechaInicio(new Date());
        consejero.setUserRegistro(ds.getUsuario());
        consejero.setCarrera(carrera);
        consejero.setColaborador(colaborador);
        consejero.setAlumnosInactivos(0);
        consejero.setAlumnosActivos(0);

        consejeroDAO.save(consejero);
    }

    @Override
    public ConsejeriaEstado findConsejeroByStateAndCarrera(Long carrera) {
        return consejeroDAO.findByStateAndCarrera(carrera);
    }

    @Override
    @Transactional
    public void asignarAlumnosAleatorio(Long carrera, DataSessionPivot ds) {

        List<Alumno> alumnos = alumnoService.findAlumnoConsejeria(carrera, ds.getCicloAcademico());
        List<Consejero> consejerosDesasignar = new ArrayList();
        Consejero consejeroNN = new Consejero();
        consejeroNN.setId(Constantine.ID_CONSEJERO_NN);
        consejerosDesasignar.add(consejeroNN);
        alumnoConsejeroDAO.desasignarAlumnosConsejero(consejerosDesasignar, ds.getUsuario());

        int i = 1;
        Collections.shuffle(alumnos);
        for (Alumno alumno : alumnos) {
//            alumno.setIndex(i);
            i++;
        }
        List<Consejero> consejeros = consejeroDAO.findConsejeroByEstado(carrera);
        int cantEqvalente = alumnos.size() / consejeros.size();
        int ult = consejeros.size();

        for (int numConsejero = 1; numConsejero <= consejeros.size(); numConsejero++) {

            int limit = cantEqvalente * numConsejero; //hasta
            int offset = (numConsejero - 1) * cantEqvalente; //desde

            int limite = (numConsejero == ult ? alumnos.size() : limit);

            List<Alumno> alumos = alumnos.stream().filter((x) -> x.getIndex() > offset && x.getIndex() <= limite).collect(Collectors.toList());
            alumnoConsejeroDAO.insertAlumnoConsejero(consejeros.get(numConsejero - 1), ds.getCicloAcademico(), ds.getUsuario(), new Carrera(carrera), alumos);
            Consejero consejero = consejeros.get(numConsejero - 1);
            logger.debug("  alumnons cantidad  {} ", alumos.size());

            Long activos = consejeroDAO.findByMatriculaActivo(alumos, carrera, ds.getCicloAcademico());
            Long inactivos = consejeroDAO.findByMatriculaInactivo(alumos, carrera, ds.getCicloAcademico());

            int cantidadAct = activos.intValue();
            int cantidaIna = inactivos.intValue();

            consejero.setAlumnosActivos(cantidadAct);
            consejero.setAlumnosInactivos(cantidaIna);
            consejeroDAO.update(consejero);

            for (Alumno alumo : alumos) {
                alumo.setConsejero(consejero);
                AlumnoDAO.update(alumo);
                i++;
            }
        }
    }

    @Override
    @Transactional
    public void desasignarAlumnos(Long carrera, DataSessionPivot ds) {
        List<Consejero> consejeros = consejeroDAO.findConsejeroByEstado(carrera);
        alumnoConsejeroDAO.desasignarAlumnosConsejero(consejeros, ds.getUsuario());
        for (Consejero consejero : consejeros) {
            for (Alumno alumno : consejero.getAlumno()) {
                alumno.setConsejero(null);
                AlumnoDAO.update(alumno);
            }
            consejero.setAlumnosActivos(0);
            consejero.setAlumnosInactivos(0);
            consejeroDAO.update(consejero);
        }
    }

    @Override
    public AConsejeroEstado findAConsejadosByStateCarrera(Long carrera, DataSessionPivot ds) {
        return consejeroDAO.findAconsejadosByMatricula(carrera, ds.getCicloAcademico());
    }

    @Override
    public List<Consejero> allByCarrera(String nombre, Carrera carrera) {
        return consejeroDAO.allByNombreAndCarrera(nombre, carrera);
    }

    @Override
    public List<Alumno> allAlumnosByConsejero(Consejero consejero) {
        return consejeroDAO.allAlumnosByConsejero(consejero);
    }

}

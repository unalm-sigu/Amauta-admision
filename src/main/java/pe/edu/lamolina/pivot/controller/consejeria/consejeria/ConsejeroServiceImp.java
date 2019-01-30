package pe.edu.lamolina.pivot.controller.consejeria.consejeria;

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
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoService;
import pe.edu.lamolina.pivot.controller.general.oficina.OficinaService;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.consejeria.AlumnoConsejeroDAO;
import pe.edu.lamolina.pivot.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
public class ConsejeroServiceImp implements ConsejeroService {

    @Autowired
    DocenteDAO docenteDAO;
    @Autowired
    ConsejeroDAO consejeroDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Autowired
    OficinaService oficinaService;
    @Autowired
    AlumnoService alumnoService;

    @Autowired
    AlumnoConsejeroDAO alumnoConsejeroDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Docente> allDocenteByNombreAndCarrera(String nombre, String facultadid) {
        return docenteDAO.allByNameAndCarrera(nombre, facultadid);
    }

    @Override
    public List<Docente> allDocente() {
        return docenteDAO.all();
    }

    @Override
    public Docente findById(Long idDocente) {
        return docenteDAO.find(idDocente);
    }

    @Override
    public Carrera findCarreraByIdFacultad(Long idFaculta) {
        return carreraDAO.findCarreraByIdFacultad(idFaculta);
    }

    @Override
    public Colaborador findColaboradorByIdPersona(Long idPersona) {
        return colaboradorDAO.findColaboradorByIdPersona(idPersona);
    }

    @Override
    public List<Docente> allDocenteByCarrera(String nombre) {
        return docenteDAO.allDocenteByCarrera(nombre);
    }

    @Override
    public List<Carrera> allByCarreraByNombre(String nombre, List<Carrera> carreras) {
        return carreraDAO.allByNombreCarrera(nombre, carreras);
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
    public List<DepartamentoAcademico> allDeptByIdFacultad(String facultadid) {
        return consejeroDAO.allByIdFacultad(facultadid);
    }

    @Override
    public List<Docente> allDocenteByNombreAndCarreraAndDeparts(String nombre, List<DepartamentoAcademico> departs) {
        return consejeroDAO.allByNombreAndDeparts(nombre, departs);
    }

    @Override
    public Colaborador findColaboradorDocenteByIdPersona(Long idPersona, Long IdCargo) {
        return colaboradorDAO.findColaboradorDocenteByIdPersona(idPersona, IdCargo);
    }

    @Override
    public Consejero find(Long idConsejero) {
        return consejeroDAO.find(idConsejero);
    }

    @Override
    public Consejero finByIdPersona(Persona persona) {
        return consejeroDAO.finByIdPersona(persona);
    }

    @Override
    public List<Carrera> allCarreraByIdDocente(long idDocente) {
        return consejeroDAO.findAllCarreraByIdDocente(idDocente);
    }

    @Override
    public List<Carrera> allCarreraByPersonaCiclo(Persona persona, CicloAcademico ciclo) {
        List<Facultad> facultades = new ArrayList();
        List<Carrera> carreras = new ArrayList();

        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(persona);

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
        Consejero cons = this.find(consejero.getId());
        cons.setEstado(consejero.getEstado());
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
    public ConsejeroEstado findConsejeroByStateAndCarrera(Long carrera) {
        return consejeroDAO.findByStateAndCarrera(carrera);
    }

    @Override
    @Transactional
    public void asignarAlumnosAleatorio(Long carrera, DataSessionPivot ds) {

        List<Alumno> alumnos = alumnoService.findAlumnnoByCarrera(carrera, ds.getCicloAcademico());
        int i = 1;
        Collections.shuffle(alumnos);
        for (Alumno alumno : alumnos) {
            alumno.setIndex(i);
            i++;
        }
        List<Consejero> consejeros = consejeroDAO.findConsejeroByEstado(carrera);
        int cantEqv = alumnos.size() / consejeros.size();

        int ult = consejeros.size();
        int sum = 0;
        for (int numConsejero = 1; numConsejero <= consejeros.size(); numConsejero++) {
            sum = cantEqv;
            int offset = (numConsejero - 1) * sum;
            int a = ult == numConsejero ? alumnos.size() : sum;
            List<Alumno> alumos = alumnos.stream().filter((x) -> x.getIndex() > offset && x.getIndex() <= a).collect(Collectors.toList());
            alumnoConsejeroDAO.insertAlumnoConsejero(consejeros.get(numConsejero - 1), ds.getCicloAcademico(), ds.getUsuario(), new Carrera(carrera), alumos);
            Consejero consejero = consejeros.get(numConsejero - 1);
            consejero.setAlumnosActivos(alumos.size());
            consejeroDAO.update(consejero);
        }

    }

    @Override
    @Transactional
    public void desasignarAlumnos(Long carrera, DataSessionPivot ds) {
        List<Consejero> consejeros = consejeroDAO.findConsejeroByEstado(carrera);
        int cantidadConsejeros = consejeros.size();
        alumnoConsejeroDAO.desasignarAlumnosConsejero(consejeros, ds.getUsuario());
        for (Consejero consejero : consejeros) {
            consejero.setAlumnosActivos(0);
            consejero.setAlumnosInactivos(0);
            consejeroDAO.update(consejero);
        }

    }

    @Override
    public List<Consejero> allByCarrera(String nombre, Carrera carrera) {
        return consejeroDAO.allByNombreAndCarrera(nombre, carrera);
    }

}

package pe.edu.lamolina.pivot.controller.consejeria.consejeria;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
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
    @Transactional
    public void saveConsejero(Consejero consejero, DataSessionPivot ds) {

        consejero.setFechaRegistro(new Date());
        consejero.setFechaModificacion(new Date());
        consejero.setFechaInicio(new Date());
        consejero.setUserRegistro(ds.getUsuario());

        consejeroDAO.save(consejero);

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
    public List<Consejero> allConsejerosbyDynatableCarrera(DynatableFilter filter) {
        return consejeroDAO.allByCarreraDynatable(filter);
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
    public Consejero findByIdColaborador(Long id) {
        return consejeroDAO.finByIdColaborador(id);
    }

    @Override
    public List<Carrera> allCarreraByIdDocente(long idDocente) {
        return consejeroDAO.findAllCarreraByIdDocente(idDocente);
    }

}

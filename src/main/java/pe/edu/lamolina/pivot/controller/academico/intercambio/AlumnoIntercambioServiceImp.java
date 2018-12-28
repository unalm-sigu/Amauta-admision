package pe.edu.lamolina.pivot.controller.academico.intercambio;

import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoIntercambio;
import pe.edu.lamolina.model.academico.BecaEstudio;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.AlumnoBecadoEstadoEnum;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoIntercambioDAO;
import pe.edu.lamolina.pivot.dao.academico.BecaEstudioDAO;
import pe.edu.lamolina.pivot.dao.general.EmpresaDAO;

@Service
@Transactional(readOnly = true)
public class AlumnoIntercambioServiceImp implements AlumnoIntercambioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoIntercambioDAO alumnoIntercambioDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    BecaEstudioDAO becaEstudioDAO;

    @Autowired
    EmpresaDAO empresaDAO;

    @Override
    public List<AlumnoIntercambio> allAlumnoBecado(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return alumnoIntercambioDAO.allByDynatable(filter);
    }

    @Override
    public List<TipoDocIdentidad> allTiposDocIdentidad() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    public List<CicloAcademico> allCicloAcademico() {
        int year = new DateTime().getYear();
        int yearinit = year - 4;
        int yearend = year + 5;
        return cicloAcademicoDAO.allPregradoByRange(yearinit, yearend);
    }

    @Override
    @Transactional
    public void save(AlumnoIntercambio alumnoBecado, Usuario user) {
        alumnoBecado.setUserRegistro(user);
        alumnoBecado.setFechaRegistro(new Date());
        alumnoBecado.setEstado(AlumnoBecadoEstadoEnum.ACT.name());
        alumnoIntercambioDAO.save(alumnoBecado);
    }

    @Override
    @Transactional
    public void update(AlumnoIntercambio alumnoIntercambio) {
        AlumnoIntercambio alumnoBecadoDb = alumnoIntercambioDAO.find(alumnoIntercambio);
        alumnoBecadoDb.setAlumno(alumnoIntercambio.getAlumno());
        alumnoBecadoDb.setCicloIntercambio(alumnoIntercambio.getCicloIntercambio());
        alumnoBecadoDb.setPaisDestino(alumnoIntercambio.getPaisDestino());
        alumnoBecadoDb.setUniversidadDestino(alumnoIntercambio.getUniversidadDestino());
        alumnoBecadoDb.setFacultadDestino(alumnoIntercambio.getFacultadDestino());
        alumnoBecadoDb.setMonto(alumnoIntercambio.getMonto());
        alumnoBecadoDb.setBecaEstudio(alumnoIntercambio.getBecaEstudio());
        alumnoIntercambioDAO.update(alumnoBecadoDb);
    }

    @Override
    public List<Alumno> allAlumnoByName(String nombre) {
        return alumnoDAO.allByName(nombre);
    }

    @Override
    @Transactional
    public void delete(AlumnoIntercambio alumnoBecado) {
        alumnoIntercambioDAO.delete(alumnoBecado);
    }

    @Override
    public AlumnoIntercambio find(AlumnoIntercambio alumnoBecado) {
        return alumnoIntercambioDAO.find(alumnoBecado);
    }

    @Override
    public List<BecaEstudio> allBeca(String nombre) {
        return becaEstudioDAO.allBecaByName(this.forLike(nombre));
    }

    private String forLike(String nombre) {
        return "%" + nombre.replaceAll(" ", "%") + "%";
    }

    @Override
    @Transactional
    public BecaEstudio saveBeca(BecaEstudio becaEstudio) {
        becaEstudioDAO.save(becaEstudio);
        return becaEstudio;
    }

    @Override
    public List<Empresa> allInstituciones() {
        return empresaDAO.all();
    }

}

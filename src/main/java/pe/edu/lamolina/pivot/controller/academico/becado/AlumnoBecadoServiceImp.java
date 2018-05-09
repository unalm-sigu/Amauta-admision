package pe.edu.lamolina.pivot.controller.academico.becado;

import java.util.Calendar;
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
import pe.edu.lamolina.model.academico.AlumnoBecado;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.AlumnoBecadoEstadoEnum;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.AlumnoBecadoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;

@Service
@Transactional(readOnly = true)
public class AlumnoBecadoServiceImp implements AlumnoBecadoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoBecadoDAO alumnoBecadoDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Override
    public List<AlumnoBecado> allAlumnoBecado(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return alumnoBecadoDAO.allByDynatable(filter);
    }

    @Override
    public List<TipoDocIdentidad> allTiposDocIdentidad() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    public List<CicloAcademico> allCicloAcademico() {
        int year = new DateTime().getYear();
        int yearinit = year - 4;
        int yearend = year + 3;
        return cicloAcademicoDAO.allPregradoByRange(yearinit, yearend);
    }

    @Override
    @Transactional
    public void save(AlumnoBecado alumnoBecado, Usuario user) {
        alumnoBecado.setUserRegistro(user);
        alumnoBecado.setFechaRegistro(new Date());
        alumnoBecado.setEstado(AlumnoBecadoEstadoEnum.ACT.name());
        alumnoBecadoDAO.save(alumnoBecado);
    }

    @Override
    @Transactional
    public void update(AlumnoBecado alumnoBecado, Usuario user) {
        AlumnoBecado alumnoBecadoDb = alumnoBecadoDAO.findAlumnoBecado(alumnoBecado);
        alumnoBecadoDb.setAlumno(alumnoBecado.getAlumno());
        alumnoBecadoDb.setCicloBeca(alumnoBecado.getCicloBeca());
        alumnoBecadoDb.setPaisDestino(alumnoBecado.getPaisDestino());
        alumnoBecadoDb.setUniversidadDestino(alumnoBecado.getUniversidadDestino());
        alumnoBecadoDb.setNombreUniversidadDestino(alumnoBecado.getNombreUniversidadDestino());
        alumnoBecadoDb.setFacultadDestino(alumnoBecado.getFacultadDestino());
        alumnoBecadoDb.setMonto(alumnoBecado.getMonto());
        alumnoBecadoDAO.update(alumnoBecadoDb);
    }

    @Override
    public List<Alumno> allAlumnoByName(String nombre) {
        return alumnoDAO.allByName(nombre);
    }

    @Override
    @Transactional
    public void delete(AlumnoBecado alumnoBecado) {
        alumnoBecadoDAO.delete(alumnoBecado);
    }

    @Override
    public AlumnoBecado find(AlumnoBecado alumnoBecado) {
        return alumnoBecadoDAO.findAlumnoBecado(alumnoBecado);
    }

}

package pe.edu.lamolina.amauta.controller.posgrado.alumnotarifa;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.enums.AmbitoTarifaEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.posgrado.AlumnoTarifa;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.general.PaisDAO;
import pe.edu.lamolina.amauta.dao.posgrado.AlumnoTarifaDAO;
import pe.edu.lamolina.amauta.dao.posgrado.TarifaCarreraDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
public class AlumnoTarifaServiceImp implements AlumnoTarifaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoTarifaDAO alumnoTarifaDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    PaisDAO paisDAO;
    
    @Autowired
    TarifaCarreraDAO tarifaCarreraDAO;

    @Override
    public List<AlumnoTarifa> allAlumnoTarifa(DynatableFilter filter) {
        return alumnoTarifaDAO.allDynaTable(filter);
    }

    @Override
    @Transactional
    public void save(AlumnoTarifa alumnoTarifaForm, DataSessionPivot ds) {
        AlumnoTarifa alumnoTarifaBD = alumnoTarifaDAO.find(alumnoTarifaForm.getId());
        Assert.isTrue(alumnoTarifaBD.getEstadoEnum() == EstadoEnum.ACT, "Este registro ya fue desactivado");

        alumnoTarifaBD.setEstadoEnum(EstadoEnum.INA);
        alumnoTarifaDAO.update(alumnoTarifaBD);

        AlumnoTarifa alumnoTarifaNew = new AlumnoTarifa();
        alumnoTarifaNew.setTarifaCarrera(alumnoTarifaForm.getTarifaNueva());
        alumnoTarifaNew.setAlumno(alumnoTarifaBD.getAlumno());
        alumnoTarifaNew.setEstadoEnum(EstadoEnum.ACT);
        alumnoTarifaNew.setFechaActivacion(new Date());
        alumnoTarifaNew.setUserActivacion(ds.getUsuario());
        alumnoTarifaNew.setFechaRegistro(new Date());
        alumnoTarifaNew.setUserRegistro(ds.getUsuario());
        alumnoTarifaDAO.save(alumnoTarifaNew);
    }

    @Override
    public List<TarifaCarrera> allOtrasTarifas(Alumno alumnoForm) {
        Alumno alumnoBD = alumnoDAO.find(alumnoForm);
        Carrera carrera = alumnoBD.getCarrera();
        Pais nacionalidad = alumnoBD.getPersona().getNacionalidad();
        if (nacionalidad == null) {
            nacionalidad = paisDAO.findByCodigo("PE");
        }

        AmbitoTarifaEnum ambito = getAmbito(nacionalidad);

        AlumnoTarifa alumnoTarifaBD = alumnoTarifaDAO.findActivaByAlumno(alumnoBD);
        TarifaCarrera actual = alumnoTarifaBD == null ? null : alumnoTarifaBD.getTarifaCarrera();
        List<TarifaCarrera> tarifas = tarifaCarreraDAO.allByCarreraAmbito(carrera, ambito);
        if (tarifas.isEmpty()) {
            tarifas = tarifaCarreraDAO.allByCarreraAmbito(carrera, AmbitoTarifaEnum.NAC);
        }
        if (actual == null) {
            return tarifas;
        }

        logger.debug("actual.id={}", actual.getId());

        TarifaCarrera tarifaEquiv = null;
        for (TarifaCarrera tarifa : tarifas) {
            logger.debug("tarifa.id={}", tarifa.getId());
            if (actual.getId() == tarifa.getId().longValue()) {
                tarifaEquiv = tarifa;
                break;
            }
        }
        logger.debug("tarifas={}", tarifas.size());
        if (tarifaEquiv != null) {
            logger.debug("tarifaEquiv.id={}", tarifaEquiv.getId());
            tarifas.remove(tarifaEquiv);
            logger.debug("tarifas={}", tarifas.size());
        }
        return tarifas;

    }

    private AmbitoTarifaEnum getAmbito(Pais pais) {
        if (pais.getCodigo().equals("PE")) {
            return AmbitoTarifaEnum.NAC;
        }
        if (pais.getConvenioAndresBello() != null) {
            return AmbitoTarifaEnum.CAN;
        }
        return AmbitoTarifaEnum.EXT;
    }

}

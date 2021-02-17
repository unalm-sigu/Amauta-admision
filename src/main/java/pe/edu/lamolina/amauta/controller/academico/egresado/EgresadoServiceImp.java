package pe.edu.lamolina.amauta.controller.academico.egresado;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.academico.FacultadDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.bean.BusquedaBean;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Compania;

@Service
@Transactional(readOnly = true)
public class EgresadoServiceImp implements EgresadoService {

    @Autowired
    EgresadoDAO egresadoDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    FacultadDAO facultadDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Egresado> allEgresadoByDynatable(DynatableFilter filter, List<Carrera> carreras, String todo) {
        return egresadoDAO.allByCarrerasDynatable(filter, carreras, todo);
    }

    @Override
    public EgresadoResumen findResumenEgresado(List<Carrera> carreras, String todo) {
        return egresadoDAO.findResumenEgresado(carreras, todo);
    }

    @Override
    public List<CicloAcademico> allCiclosByNombre(String nombre, DataSessionPivot ds) {
        return cicloAcademicoDAO.allCicloByName(nombre);
    }

    @Override
    public List<Facultad> allFacultadByNombre(String nombre, DataSessionPivot ds) {

        return facultadDAO.allFacultad(nombre, new Compania(1));
    }

    @Override
    public List<Carrera> allCarreraByNombre(String nombre, DataSessionPivot ds) {
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return carreraDAO.allByNombreModalidad(nombre, modalidadEstudio);
    }

    @Override
    public String downloadReporte(BusquedaBean busquedaBean, DataSessionPivot ds) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

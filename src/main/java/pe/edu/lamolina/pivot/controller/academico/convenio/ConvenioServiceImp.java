package pe.edu.lamolina.pivot.controller.academico.convenio;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CarreraConvenio;
import pe.edu.lamolina.model.academico.ConvenioBeca;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EstadoConvenioBecaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.CarreraConvenioDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.ConvenioBecaDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.general.EmpresaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ConvenioServiceImp implements ConvenioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ConvenioBecaDAO convenioBecaDAO;

    @Autowired
    EmpresaDAO empresaDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    CarreraConvenioDAO carreraConvenioDAO;

    @Override
    @Transactional
    public void delete(ConvenioBeca convenioBeca) {
        carreraConvenioDAO.deleteByConvenioBeca(convenioBeca);
        convenioBecaDAO.delete(convenioBeca);
    }

    @Override
    @Transactional
    public void update(ConvenioBeca convenioBeca, DataSessionPivot ds) {
        ConvenioBeca convenioBecaDB = convenioBecaDAO.find(convenioBeca.getId());
        convenioBeca.setUserRegistro(convenioBecaDB.getUserRegistro());
        convenioBeca.setFechaRegistro(convenioBecaDB.getFechaRegistro());
        convenioBeca.setEstado(convenioBecaDB.getEstado());
        convenioBecaDAO.update(convenioBeca);
        carreraConvenioDAO.deleteByConvenioBeca(convenioBeca);
        List<CarreraConvenio> carreraConvenios = convenioBeca.getCarreraConvenio();
        for (CarreraConvenio carreraConvenio : carreraConvenios) {
            carreraConvenio.setConvenioBeca(convenioBeca);
            carreraConvenioDAO.save(carreraConvenio);
        }
    }

    @Override
    @Transactional
    public void save(ConvenioBeca convenioBeca, DataSessionPivot ds) {
        Usuario user = ds.getUsuario();
        convenioBeca.setUserRegistro(user);
        convenioBeca.setFechaRegistro(new Date());
        convenioBeca.setEstado(EstadoConvenioBecaEnum.PEN.name());
        convenioBecaDAO.save(convenioBeca);
        List<CarreraConvenio> carreraConvenios = convenioBeca.getCarreraConvenio();
        for (CarreraConvenio carreraConvenio : carreraConvenios) {
            carreraConvenio.setConvenioBeca(convenioBeca);
            carreraConvenioDAO.save(carreraConvenio);
        }
    }

    @Override
    public ConvenioBeca findConvenioBeca(Long idConvenioBeca) {
        ConvenioBeca convenioBeca = convenioBecaDAO.find(idConvenioBeca);
        List<CarreraConvenio> carreraConvenios = carreraConvenioDAO.allByConvenioBeca(convenioBeca);
        convenioBeca.setCarreraConvenio(carreraConvenios);
        return convenioBeca;
    }

    @Override
    public List<ConvenioBeca> allByDynatable(DynatableFilter filter) {
        return convenioBecaDAO.allByDynatable(filter);
    }

    @Override
    @Transactional
    public void saveInstitucion(Empresa institucion) {
        empresaDAO.save(institucion);
    }

    @Override
    public List<Carrera> allCarreraByName(String nombre, Compania cia) {
        List<ModalidadEstudio> modalidadEstudio = modalidadEstudioDAO.allPrePostgrado(cia);
        return carreraDAO.allCarreraByNameAndModalidad(nombre, modalidadEstudio);
    }

    @Override
    public Map<Long, List<CarreraConvenio>> allByCarreraConvenio(List<ConvenioBeca> convenios) {
        Map<Long, List<CarreraConvenio>> carreraConvenioMaps = new LinkedHashMap();
        if (convenios == null || convenios.isEmpty()) {
            return carreraConvenioMaps;
        }
        List<CarreraConvenio> carreraConvenios = carreraConvenioDAO.allByCarreraConvenio(convenios);
        carreraConvenioMaps = TypesUtil.convertListToMapList("convenioBeca.id", carreraConvenios);
        return carreraConvenioMaps;
    }

    @Override
    public List<CarreraConvenio> allCarreraConvenioByConvenioBeca(ConvenioBeca convenioBeca) {
        if (convenioBeca.getId() == null) {
            return new ArrayList();
        }
        return carreraConvenioDAO.allByConvenioBeca(convenioBeca);
    }

}

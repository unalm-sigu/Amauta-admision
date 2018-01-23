package pe.edu.lamolina.pivot.controller.academico.convenio;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.ConvenioBeca;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.ConvenioBecaDAO;
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

    @Override
    public void delete(ConvenioBeca convenioBeca) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(ConvenioBeca convenioBeca, DataSessionPivot ds) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save(ConvenioBeca convenioBeca, DataSessionPivot ds) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ConvenioBeca findConvenioBeca(Long idConvenioBeca) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ConvenioBeca> allByDynatable(DynatableFilter filter) {
        return convenioBecaDAO.allByDynatable(filter);
    }

    @Override
    public void saveInstitucion(Empresa institucion) {
        empresaDAO.save(institucion);
    }

    @Override
    public List<Carrera> allCarreraByName(String nombre, ModalidadEstudio modalidadEstudio) {
        return carreraDAO.allCarreraByName(nombre, modalidadEstudio);
    }

}

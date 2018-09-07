package pe.edu.lamolina.pivot.controller.academico.cargaadicional.factor2;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguraCargaAdicional;
import pe.edu.lamolina.model.academico.Factor2CargaAdicional;
import pe.edu.lamolina.pivot.dao.academico.ConfiguraCargaAdicionalDAO;
import pe.edu.lamolina.pivot.dao.academico.Factor2CargaAdicionalDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CargaAdicionalFactor2ServiceImp implements CargaAdicionalFactor2Service {

      @Autowired
    ConfiguraCargaAdicionalDAO configuraCargaAdicionalDAO;
    
    @Autowired
    Factor2CargaAdicionalDAO factor2CargaAdicionalDAO;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public List<Factor2CargaAdicional> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return factor2CargaAdicionalDAO.allByDynatableCicloAcademico(filter, cicloAcademico);
    }
    
    @Override
    @Transactional
    public void delete(Long id, DataSessionPivot ds) {
        factor2CargaAdicionalDAO.delete(id);
    }
    
    @Override
    public Factor2CargaAdicional find(Long id) {
        return factor2CargaAdicionalDAO.find(id);
    }
    
    @Override
    @Transactional
    public void save(Factor2CargaAdicional factor2CargaAdicional, DataSessionPivot ds) {
        factor2CargaAdicional.setCicloAcademico(ds.getCicloAcademico());
        
        factor2CargaAdicional.setUserRegistro(ds.getUsuario());
        factor2CargaAdicional.setFechaRegistro(new Date());
        
        factor2CargaAdicionalDAO.save(factor2CargaAdicional);
    }
    
    @Override
    @Transactional
    public void update(Factor2CargaAdicional factor2CargaAdicional, DataSessionPivot ds) {
        Factor2CargaAdicional factorBD = factor2CargaAdicionalDAO.find(factor2CargaAdicional.getId());
        
        factorBD.setCantidadFin(factor2CargaAdicional.getCantidadFin());
        factorBD.setCantidadInicio(factor2CargaAdicional.getCantidadInicio());
        factorBD.setFactor(factor2CargaAdicional.getFactor());
        
        factorBD.setUserRegistro(ds.getUsuario());
        factorBD.setFechaRegistro(new Date());
        
        factor2CargaAdicionalDAO.update(factorBD);
    }
    
    @Override
    public ConfiguraCargaAdicional findConfiguracionByCiclo(CicloAcademico cicloAcademico) {
        return configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
    }

}

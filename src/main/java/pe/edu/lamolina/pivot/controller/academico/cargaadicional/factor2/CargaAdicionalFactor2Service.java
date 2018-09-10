package pe.edu.lamolina.pivot.controller.academico.cargaadicional.factor2;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguraCargaAdicional;
import pe.edu.lamolina.model.academico.Factor2CargaAdicional;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CargaAdicionalFactor2Service {
    
  List<Factor2CargaAdicional> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);
    
    void save(Factor2CargaAdicional factor1CargaAdicional, DataSessionPivot ds);
    
    void update(Factor2CargaAdicional factor1CargaAdicional, DataSessionPivot ds);
    
    void delete(Long id, DataSessionPivot ds);
    
    Factor2CargaAdicional find(Long id);
    
    ConfiguraCargaAdicional findConfiguracionByCiclo(CicloAcademico cicloAcademico);
    
}

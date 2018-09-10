package pe.edu.lamolina.pivot.controller.academico.cargaadicional.factor1;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguraCargaAdicional;
import pe.edu.lamolina.model.academico.Factor1CargaAdicional;
import pe.edu.lamolina.model.rrhh.CategoriaDocente;
import pe.edu.lamolina.model.rrhh.SituacionDocente;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface CargaAdicionalFactor1Service {
    
    List<Factor1CargaAdicional> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico);

    public List<CategoriaDocente> allCategoriaDocente();

    public List<SituacionDocente> allSituacionDocente();
    
    void save(Factor1CargaAdicional factor1CargaAdicional, DataSessionPivot ds);
    
    void update(Factor1CargaAdicional factor1CargaAdicional, DataSessionPivot ds);
    
    void delete(Long id, DataSessionPivot ds);
    
    Factor1CargaAdicional find(Long id);
    
    ConfiguraCargaAdicional findConfiguracionByCiclo(CicloAcademico cicloAcademico);
    

}

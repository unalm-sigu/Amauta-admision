package pe.edu.lamolina.pivot.dao.academico;

import java.math.BigDecimal;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteCiclo;

public interface DocenteCicloDAO extends EasyDAO<DocenteCiclo> {

    List<DocenteCiclo> allByDynatableCicloAcademico(DynatableFilter filter, CicloAcademico cicloAcademico);

    void deshacerCarga(CicloAcademico cicloAcademico);
    
    void deshacerMontos(CicloAcademico cicloAcademico);
    
    void generarMontos(CicloAcademico cicloAcademico, BigDecimal rca);
    
}

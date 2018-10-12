package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.BecaEstudio;

public interface BecaEstudioDAO extends EasyDAO<BecaEstudio> {
    
    List<BecaEstudio> allDynaTable(DynatableFilter filter);
    
    List<BecaEstudio> allByNombre(List<String> nombre);
   

}

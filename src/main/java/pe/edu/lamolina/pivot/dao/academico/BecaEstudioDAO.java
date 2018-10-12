package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.BecaEstudio;

public interface BecaEstudioDAO extends EasyDAO<BecaEstudio> {
    
    List<BecaEstudio> allDynaTable(DynatableFilter filter);
    
    BecaEstudio find(BecaEstudio nombre);

    BecaEstudio findAllInfo(Long id);
    
    BecaEstudio findByInstitucioOtorga(BecaEstudio nombre);
    
    List<BecaEstudio> allByName(String nombre);
    
    void updateInstitucionOtorga(BecaEstudio nombre);

}

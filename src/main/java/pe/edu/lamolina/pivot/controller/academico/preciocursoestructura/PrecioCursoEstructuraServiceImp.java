package pe.edu.lamolina.pivot.controller.academico.preciocursoestructura;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.PrecioCursoEstructuraDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PrecioCursoEstructuraServiceImp implements PrecioCursoEstructuraService {

    @Autowired
    PrecioCursoEstructuraDAO precioCursoEstructuraDAO;
    
    @Autowired
    SeccionDAO seccionDAO;
    
    @Autowired
    CursoCicloAcademicoDAO cursoCicloAcademicoDAO;
    
    @Override
    public List<PrecioCursoEstructura> allByCicloAcademico(CicloAcademico ciclo) {
        return precioCursoEstructuraDAO.allByCiclo(ciclo);
    }

    @Override
    @Transactional
    public void saveAll(List<PrecioCursoEstructura> listForm, DataSessionPivot ds) {
        Map<Long, PrecioCursoEstructura> mapForm = listForm.stream().collect(Collectors.toMap(PrecioCursoEstructura::getId, x -> x));
        List<PrecioCursoEstructura> listBD = precioCursoEstructuraDAO.all(new ArrayList(mapForm.keySet()));
        for (PrecioCursoEstructura item : listBD) {
            PrecioCursoEstructura form = mapForm.get(item.getId());
            item.setPrecio(form.getPrecio());
            
            item.setUserPrecio(ds.getUsuario());
            item.setFechaPrecio(new Date());
            
            precioCursoEstructuraDAO.update(item);
            seccionDAO.updatePrecioByTpc(ds.getCicloAcademico(), item.getTpc(), item.getPrecio());
            cursoCicloAcademicoDAO.updatePrecioByTpc(ds.getCicloAcademico(), item.getTpc(), item.getPrecio());
        }
    }
    
}

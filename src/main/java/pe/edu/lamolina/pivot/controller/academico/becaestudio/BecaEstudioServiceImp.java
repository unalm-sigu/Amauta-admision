package pe.edu.lamolina.pivot.controller.academico.becaestudio;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.BecaEstudio;
import pe.edu.lamolina.pivot.dao.academico.BecaEstudioDAO;

@Service
public class BecaEstudioServiceImp implements BecaEstudioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BecaEstudioDAO becaestudioDAO;
    
    @Override
    public List<BecaEstudio> allBecaEstudio(DynatableFilter filter) {
        return becaestudioDAO.allDynaTable(filter);
    }

}

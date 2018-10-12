package pe.edu.lamolina.pivot.controller.academico.becaestudio;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.BecaEstudio;
import pe.edu.lamolina.pivot.dao.academico.BecaEstudioDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
public class BecaEstudioServiceImp implements BecaEstudioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BecaEstudioDAO becaestudioDAO;
    
    @Override
    public List<BecaEstudio> allByDynatable(DynatableFilter filter) {
        return becaestudioDAO.allDynaTable(filter);
    }   

    @Override
    @Transactional
    public void save(BecaEstudio becaestudio, DataSessionPivot ds) {
        becaestudioDAO.save(becaestudio);
    }

    @Override
    @Transactional
    public void update(BecaEstudio becaestudioForm, DataSessionPivot ds) {
        BecaEstudio becaestudioBD = becaestudioDAO.find(becaestudioForm.getId());
        Assert.isNotNull(becaestudioForm.getNombre(), "El nombre esta vacio");

        becaestudioBD.setNombre(becaestudioForm.getNombre());
        becaestudioBD.setInstitucionOtorga(becaestudioForm.getInstitucionOtorga());
        becaestudioDAO.update(becaestudioBD);
    }

}

package pe.edu.lamolina.amauta.controller.general.idioma;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.amauta.dao.general.IdiomaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class IdiomaServiceImp implements IdiomaService {

    @Autowired
    IdiomaDAO idiomaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Idioma> allByDynatable(DynatableFilter filter) {
        return idiomaDAO.allDynatable(filter);
    }

    @Override
    @Transactional
    public void save(Idioma idioma, DataSessionPivot ds) {
        idiomaDAO.save(idioma);
    }

    @Override
    @Transactional
    public void update(Idioma idiomaForm, DataSessionPivot ds) {
        Idioma idiomaBD = idiomaDAO.find(idiomaForm.getId());
        Assert.isNotNull(idiomaForm.getCodigo(), "El codigo esta vacio");

        idiomaBD.setCodigo(idiomaForm.getCodigo());
        idiomaBD.setNombre(idiomaForm.getNombre());
        idiomaDAO.update(idiomaBD);
    }

}

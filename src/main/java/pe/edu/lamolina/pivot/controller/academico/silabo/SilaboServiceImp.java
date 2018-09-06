package pe.edu.lamolina.pivot.controller.academico.silabo;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.SilaboCurso;
import pe.edu.lamolina.pivot.dao.academico.SilaboCursoDAO;

@Service
@Transactional
public class SilaboServiceImp implements SilaboService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SilaboCursoDAO silaboCursoDAO;

    @Override
    public List<SilaboCurso> allSilabo(DynatableFilter filter) {
        return silaboCursoDAO.allByDynatable(filter);
    }

}

package pe.edu.lamolina.amauta.controller.tramite.tramiteBachiller;

import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.dao.tramite.TramiteBachillerDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.tramite.TramiteBachiller;

@Service
@Transactional(readOnly = true)
public class TramitesBachillerServiceImp implements TramitesBachillerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DateTime today = new DateTime();

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    TramiteBachillerDAO tramiteBachillerDAO;

    @Override
    public List<TramiteBachiller> allTramitesByFilter(DynatableFilter filter, DataSessionPivot ds) {

        List<TramiteBachiller> bachillers = tramiteBachillerDAO.allByDynatable(filter, ds.getCicloAcademico());
        return bachillers;
    }

}

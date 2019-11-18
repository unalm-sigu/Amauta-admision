package pe.edu.lamolina.pivot.controller.tramite.tramiteDocumentoConsejero;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDocumentoAcademicoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class TramiteDocumentoConsejeroServiceImp implements TramiteDocumentoConsejeroService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteDocumentoAcademicoDAO tramiteDocumentoDAO;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Override
    public List<TramiteDocumentoAcademico> allTramiteDocumentoAcademico(DynatableFilter filter, DataSessionPivot ds) {

        List<Colaborador> colaboradors = colaboradorDAO.allActivosByPersona(ds.getPersona());
        return tramiteDocumentoDAO.allTramiteDocumentoAcademico(filter, colaboradors);
    }

}

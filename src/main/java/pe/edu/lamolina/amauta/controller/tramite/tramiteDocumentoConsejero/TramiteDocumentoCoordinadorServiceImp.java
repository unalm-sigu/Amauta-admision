package pe.edu.lamolina.amauta.controller.tramite.tramiteDocumentoConsejero;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDocumentoAcademicoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class TramiteDocumentoCoordinadorServiceImp implements TramiteDocumentoCoordinadorService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TramiteDocumentoAcademicoDAO tramiteDocumentoDAO;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Override
    public List<TramiteDocumentoAcademico> allTramiteDocumentoAcademico(DynatableFilter filter, DataSessionPivot ds) {
        if (!isCoordinador(ds.getRoles())) {
            return new ArrayList<>();
        }

        return tramiteDocumentoDAO.allTramiteDocumentoAcademico(filter, ds.getPersona());
    }

    public Boolean isCoordinador(List<Rol> roles) {
        Boolean visible = false;
        for (Rol rol : roles) {
            if (rol.getCodigoEnum() == RolEnum.COORD_ESP_EPG) {
                visible = true;
            }
        }
        return visible;
    }

}

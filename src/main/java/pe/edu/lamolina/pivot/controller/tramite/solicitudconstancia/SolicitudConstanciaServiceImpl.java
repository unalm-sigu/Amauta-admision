package pe.edu.lamolina.pivot.controller.tramite.solicitudconstancia;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDocumentoAcademicoDAO;

@Service
@Transactional(readOnly = true)
public class SolicitudConstanciaServiceImpl implements SolicitudConstanciaService {

    @Autowired
    TramiteDocumentoAcademicoDAO tramiteDocumentoAcademicoDAO;

    @Override
    public TramiteDocumentoAcademico findById(TramiteDocumentoAcademico tramiteDocumentoAcademico) {
        return tramiteDocumentoAcademicoDAO.find(tramiteDocumentoAcademico.getId());

    }

    @Override
    public List<TramiteDocumentoAcademico> all() {
        return tramiteDocumentoAcademicoDAO.all();
    }

    @Override
    public void save(TramiteDocumentoAcademico tramiteDocumentoAcademico, Usuario usuario) {
        tramiteDocumentoAcademicoDAO.save(tramiteDocumentoAcademico);
    }

    @Override
    public void update(TramiteDocumentoAcademico tramiteDocumentoAcademico, Usuario usuario) {
        tramiteDocumentoAcademicoDAO.update(tramiteDocumentoAcademico);
    }

}

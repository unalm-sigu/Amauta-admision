package pe.edu.lamolina.pivot.controller.tramite;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.SolicitudConstanciaDAO;

@Service
@Transactional(readOnly = true)
public class SolicitudConstanciaServiceImpl implements SolicitudConstanciaService {

    @Autowired
    SolicitudConstanciaDAO solicitudConstanciaDAO;

    @Override
    public TramiteDocumentoAcademico findById(TramiteDocumentoAcademico tramiteDocumentoAcademico) {
        return solicitudConstanciaDAO.find(tramiteDocumentoAcademico.getId());

    }

    @Override
    public List<TramiteDocumentoAcademico> all() {
        return solicitudConstanciaDAO.all();
    }

    @Override
    public void save(TramiteDocumentoAcademico tramiteDocumentoAcademico, Usuario usuario) {
        solicitudConstanciaDAO.save(tramiteDocumentoAcademico);
    }

    @Override
    public void update(TramiteDocumentoAcademico tramiteDocumentoAcademico, Usuario usuario) {
        solicitudConstanciaDAO.update(tramiteDocumentoAcademico);
    }

}

package pe.edu.lamolina.pivot.controller.tramite;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.TipoConstanciaDAO;

@Service
@Transactional(readOnly = true)
public class TipoConstanciaServiceImpl implements TipoConstanciaService {

    @Autowired
    TipoConstanciaDAO tipoConstanciaDAO;

    @Override
    @Transactional
    public void update(TipoDocumentoAcademico tipoDocumentoAcademico, Usuario usuario) {
        tipoConstanciaDAO.update(tipoDocumentoAcademico);
    }

    @Override
    @Transactional
    public void save(TipoDocumentoAcademico tramiteDocumentoAcademico, Usuario usuario) {
        tipoConstanciaDAO.save(tramiteDocumentoAcademico);
    }

    @Override
    public List<TipoDocumentoAcademico> all(DynatableFilter filter) {
        return tipoConstanciaDAO.allDynatable(filter);
    }

    @Override
    public TipoDocumentoAcademico findById(TipoDocumentoAcademico tipoDocumentoAcademico) {
        return tipoConstanciaDAO.find(tipoDocumentoAcademico.getId());
    }

    @Override
    public List<TipoDocumentoAcademico> all() {
       return tipoConstanciaDAO.all();
    }

}

package pe.edu.lamolina.pivot.controller.tramite.tipoConstancia;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoOficinaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoConstanciaDAO;

@Service
@Transactional(readOnly = true)
public class TipoConstanciaServiceImpl implements TipoConstanciaService {

    @Autowired
    TipoConstanciaDAO tipoConstanciaDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    TipoOficinaDAO tipoOficinaDAO;

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

    @Override
    @Transactional
    public void delete(TipoDocumentoAcademico tipoDocumento) {
        tipoConstanciaDAO.delete(tipoDocumento);
    }

    @Override
    public List<Oficina> allOficina(String nombre) {
        return oficinaDAO.allByName(nombre);
    }

    @Override
    public List<TipoOficina> allTipoOficina(String nombre) {
        return tipoOficinaDAO.allByName(nombre);
    }

}

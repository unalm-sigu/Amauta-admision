package pe.edu.lamolina.amauta.controller.tramite.costo;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.amauta.dao.general.IdiomaDAO;
import pe.edu.lamolina.amauta.dao.tramite.CostoDocumentoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CostoDocumentoServiceImpl implements CostoDocumentoService {

    @Autowired
    CostoDocumentoDAO costoDocumentoDAO;

    @Autowired
    IdiomaDAO idiomaDAO;

    @Override
    public PrecioDocumento findById(PrecioDocumento precioDocumento) {
        return costoDocumentoDAO.find(precioDocumento.getId());

    }

    @Override
    public List<PrecioDocumento> all(DynatableFilter filter) {
        return costoDocumentoDAO.allDynatable(filter);
    }

    @Override
    @Transactional
    public void save(PrecioDocumento precioDocumento, DataSessionPivot ds) {
        PrecioDocumento precioDB = costoDocumentoDAO.findTipoDocAndIdioma(precioDocumento.getTipoDocumento(),precioDocumento.getIdioma());
        Assert.isNull(precioDB, "Existe precio para " + precioDocumento.getTipoDocumento().getNombre());
        precioDocumento.setFechaRegistro(new Date());
        precioDocumento.setUserRegistro(ds.getUsuario());
        precioDocumento.setCuentaBancaria(new CuentaBancaria(6));
        costoDocumentoDAO.save(precioDocumento);
    }

    @Override
    @Transactional
    public void update(PrecioDocumento precioDocumento, DataSessionPivot ds) {
        PrecioDocumento documento = costoDocumentoDAO.find(precioDocumento.getId());
        documento.setPrecio(precioDocumento.getPrecio());
        costoDocumentoDAO.update(documento);
    }

    @Override
    public List<Idioma> allIdioma() {
        return idiomaDAO.allInglesAndEspañol();
    }

}

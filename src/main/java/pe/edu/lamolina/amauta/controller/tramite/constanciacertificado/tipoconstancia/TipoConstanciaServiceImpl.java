package pe.edu.lamolina.amauta.controller.tramite.constanciacertificado.tipoconstancia;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.AccionTramiteDocumento;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.amauta.dao.general.ConfiguracionFirmaDocumentoDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.general.TipoOficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.AccionTramiteDocumentoDAO;
import pe.edu.lamolina.amauta.dao.tramite.PrecioDocumentoDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoAcademicoDAO;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;

@Service
@Transactional(readOnly = true)
public class TipoConstanciaServiceImpl implements TipoConstanciaService {

    @Autowired
    TipoDocumentoAcademicoDAO tipoDocumentoAcademicoDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    TipoOficinaDAO tipoOficinaDAO;

    @Autowired
    ConfiguracionFirmaDocumentoDAO configuracionFirmaDocumentoDAO;

    @Autowired
    AccionTramiteDocumentoDAO accionTramiteDocumentoDAO;

    @Autowired
    PrecioDocumentoDAO precioDocumentoDAO;

    @Override
    @Transactional
    public void save(TipoDocumentoAcademico tramiteDocumentoAcademico, Usuario usuario) {

        tramiteDocumentoAcademico.setCodigo(tramiteDocumentoAcademico.getTipo() + System.currentTimeMillis());

        TipoDocumentoAcademico tramiteDocumentoAcademicoCodigo = tipoDocumentoAcademicoDAO.findByCodigo(tramiteDocumentoAcademico.getCodigo());

        while (tramiteDocumentoAcademicoCodigo != null) {
            tramiteDocumentoAcademico.setCodigo(tramiteDocumentoAcademico.getTipo() + System.currentTimeMillis());
            tramiteDocumentoAcademicoCodigo = tipoDocumentoAcademicoDAO.findByCodigo(tramiteDocumentoAcademico.getCodigo());
        }

        Oficina oficina = oficinaDAO.findByCode(OficinaEnum.OERA.name());
        if (tramiteDocumentoAcademico.getCostoCiclo() == null) {
            tramiteDocumentoAcademico.setCostoCiclo(0L);
        }
        if (tramiteDocumentoAcademico.getPlazoDiasPago() == null) {
            tramiteDocumentoAcademico.setPlazoDiasPago(0);
        }
        if (tramiteDocumentoAcademico.getRequiereFoto() == null) {
            tramiteDocumentoAcademico.setRequiereFoto(0);
        }
        if (tramiteDocumentoAcademico.getRequiereEgresado() == null) {
            tramiteDocumentoAcademico.setRequiereEgresado(0);
        }
        if (tramiteDocumentoAcademico.getRequierePosgrado() == null) {
            tramiteDocumentoAcademico.setRequierePosgrado(0);
        }
        if (tramiteDocumentoAcademico.getRequiereEspecial() == null) {
            tramiteDocumentoAcademico.setRequiereEspecial(0);
        }

        if (tramiteDocumentoAcademico.getRequierePregrado() == null) {
            tramiteDocumentoAcademico.setRequierePregrado(0);
        }

        if (tramiteDocumentoAcademico.getConfigurado() == null) {
            tramiteDocumentoAcademico.setConfigurado(0L);
        }

        if (tramiteDocumentoAcademico.getDescargable() == null) {
            tramiteDocumentoAcademico.setDescargable(false);
        }

        tramiteDocumentoAcademico.setOficinaEmisora(oficina);
        tipoDocumentoAcademicoDAO.save(tramiteDocumentoAcademico);
    }

    @Override
    @Transactional
    public void update(TipoDocumentoAcademico tramiteDocumentoAcademicoForm, Usuario usuario) {

        if (tramiteDocumentoAcademicoForm.getCostoCiclo() == null) {
            tramiteDocumentoAcademicoForm.setCostoCiclo(0L);
        }
        if (tramiteDocumentoAcademicoForm.getPlazoDiasPago() == null) {
            tramiteDocumentoAcademicoForm.setPlazoDiasPago(0);
        }
        if (tramiteDocumentoAcademicoForm.getRequiereFoto() == null) {
            tramiteDocumentoAcademicoForm.setRequiereFoto(0);
        }
        if (tramiteDocumentoAcademicoForm.getRequiereEgresado() == null) {
            tramiteDocumentoAcademicoForm.setRequiereEgresado(0);
        }
        if (tramiteDocumentoAcademicoForm.getRequierePosgrado() == null) {
            tramiteDocumentoAcademicoForm.setRequierePosgrado(0);
        }
        if (tramiteDocumentoAcademicoForm.getRequierePregrado() == null) {
            tramiteDocumentoAcademicoForm.setRequierePregrado(0);
        }
        if (tramiteDocumentoAcademicoForm.getConfigurado() == null) {
            tramiteDocumentoAcademicoForm.setConfigurado(0L);
        }

        tipoDocumentoAcademicoDAO.updateColumns(tramiteDocumentoAcademicoForm,
                "costoCiclo",
                "requiereFoto",
                "requiereEgresado",
                "requierePosgrado",
                "requierePregrado",
                "configurado",
                "nombre",
                "tipo");

    }

    @Override
    public List<TipoDocumentoAcademico> allDynatable(DynatableFilter filter) {
        return tipoDocumentoAcademicoDAO.allDynatable(filter);
    }

    @Override
    public TipoDocumentoAcademico findById(TipoDocumentoAcademico tipoDocumentoAcademico) {
        return tipoDocumentoAcademicoDAO.find(tipoDocumentoAcademico.getId());
    }

    @Override
    public List<TipoDocumentoAcademico> all() {
        return tipoDocumentoAcademicoDAO.allTipoDocumento();
    }

    @Override
    @Transactional
    public void delete(TipoDocumentoAcademico tipoDocumento) {
        configuracionFirmaDocumentoDAO.deleteByTipoDocumentoAcademicos(tipoDocumento);
        List<AccionTramiteDocumento> accionTramiteDocumentos = accionTramiteDocumentoDAO.allByTipoDocumento(tipoDocumento);
        for (AccionTramiteDocumento accionTramiteDocumento : accionTramiteDocumentos) {
            accionTramiteDocumentoDAO.delete(accionTramiteDocumento);
        }
        List<PrecioDocumento> precioDocumentos = precioDocumentoDAO.allByTipoDocumentoAcademico(tipoDocumento);
        for (PrecioDocumento precioDocumento : precioDocumentos) {
            precioDocumentoDAO.delete(precioDocumento);
        }
        TipoDocumentoAcademico tipoDocumentoAcademicoDB = tipoDocumentoAcademicoDAO.find(tipoDocumento);
        tipoDocumentoAcademicoDAO.delete(tipoDocumentoAcademicoDB);
    }

    @Override
    public List<Oficina> allOficina(String nombre) {
        return oficinaDAO.allByName(nombre);
    }

    @Override
    public List<TipoOficina> allTipoOficina(String nombre) {
        return tipoOficinaDAO.allByName(nombre);
    }

    @Override
    public TipoDocumentoAcademico findTipoDocumentoAcademico(TipoDocumentoAcademico tipoDocumento) {
        return tipoDocumentoAcademicoDAO.find(tipoDocumento);
    }

    @Override
    public List<Oficina> allOficinaEmisora() {
        return oficinaDAO.allOficinaEmisora();
    }

}

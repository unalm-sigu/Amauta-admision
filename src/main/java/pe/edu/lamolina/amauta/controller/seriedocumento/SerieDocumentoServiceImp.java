package pe.edu.lamolina.amauta.controller.seriedocumento;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.tramite.SerieDocumentoDAO;

@Service
@Transactional(readOnly = false)
public class SerieDocumentoServiceImp implements SerieDocumentoService {

    @Autowired
    SerieDocumentoDAO serieDocumentoDAO;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.MANDATORY)
    public SerieDocumento getCorrelativo(TipoDocumentoCompania tipo, Long nroSerie, Usuario user) {

        SerieDocumento serie = serieDocumentoDAO.findCorrelativo(tipo, nroSerie.toString());

        if (serie != null) {
            serie = serieDocumentoDAO.findLock(serie.getId());

            if (serie.getEstado().equals("ACT")) {
                SerieDocumento serieDoc = new SerieDocumento(tipo, nroSerie.toString());
                serieDoc.setNumeroDocumento((Long.valueOf(serie.getNumeroDocumento()) + 1L) + "");

                serie.setNumeroDocumento((Long.valueOf(serie.getNumeroDocumento()) + 1L) + "");
                serieDocumentoDAO.update(serie);

                return serieDoc;
            }

            return null;

        } else {
            serie = new SerieDocumento(tipo, nroSerie.toString());
            serie.setNumeroDocumento("1");
            serie.setEstado("ACT");
            serie.setFechaRegistro(new Date());
            serie.setUserRegistro(user);
            serieDocumentoDAO.save(serie);

            return serie;
        }
    }

//    @Override
//    public SerieDocumento getCorrelativoConstanciaCertificado(TipoDocumentoCompania tipo, Long nroSerie, Usuario user, Oficina oficina) {
//        SerieDocumento serie = serieDocumentoDAO.findCorrelativoOficina(tipo, nroSerie.toString(),oficina);
//
//        if (serie != null) {
//            serie = serieDocumentoDAO.findLock(serie.getId());
//
//            if (serie.getEstado().equals("ACT")) {
//                SerieDocumento serieDoc = new SerieDocumento(tipo, nroSerie.toString());
//                serieDoc.setNumeroDocumento((Long.valueOf(serie.getNumeroDocumento()) + 1L) + "");
//
//                serie.setNumeroDocumento((Long.valueOf(serie.getNumeroDocumento()) + 1L) + "");
//                serieDocumentoDAO.update(serie);
//
//                return serieDoc;
//            }
//
//            return null;
//
//        } else {
//            serie = new SerieDocumento(tipo, nroSerie.toString());
//            serie.setNumeroDocumento("1");
//            serie.setEstado("ACT");
//            serie.setFechaRegistro(new Date());
//            serie.setUserRegistro(user);
//            serie.setOficina(oficina);
//            serieDocumentoDAO.save(serie);
//
//            return serie;
//        }
//    }

}

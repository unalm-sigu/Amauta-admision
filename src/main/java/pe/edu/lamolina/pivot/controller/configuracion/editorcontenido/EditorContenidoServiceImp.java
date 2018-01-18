package pe.edu.lamolina.pivot.controller.configuracion.editorcontenido;

import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.zelper.aws.S3Service;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.ContenidoVariable;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.config.DespliegueConfig;
import pe.edu.lamolina.pivot.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.pivot.dao.general.ContenidoVariableDAO;
import pe.edu.lamolina.pivot.dao.seguridad.SistemaDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.S3_PUBLIC_DIR;
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.S3_TMP;

@Service
@Transactional(readOnly = true)
public class EditorContenidoServiceImp implements EditorContenidoService {

    @Autowired
    ContenidoCartaDAO contenidoCartaDAO;
    @Autowired
    ContenidoVariableDAO contenidoVariableDAO;
    @Autowired
    S3Service s3Service;
    @Autowired
    DespliegueConfig despliegueConfig;
    @Autowired
    SistemaDAO sistemaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ContenidoCarta findContenidoCartaById(Long idContenido) {
        return contenidoCartaDAO.find(idContenido);
    }

    @Override
    @Transactional
    public void updateContenido(Long idContenido, String contenido, Long idSistema) {
        ContenidoCarta contenidoCarta = contenidoCartaDAO.find(idContenido);
        contenidoCarta.setContenido(contenido);
        contenidoCartaDAO.update(contenidoCarta);
    }

    @Override
    public List<ContenidoCarta> allContenidoCartaByDynaTable(DynatableFilter filter) {
        return contenidoCartaDAO.allByDynaTable(filter);
    }

    @Override
    @Transactional
    public void save(ContenidoCarta contenido) {
        if (contenido.getId() == null) {
            contenidoCartaDAO.save(contenido);
        } else {
            ContenidoCarta contenidoCarta = contenidoCartaDAO.find(contenido.getId());
            contenidoCarta.setCodigo(contenido.getCodigo());
            contenidoCarta.setNombre(contenido.getNombre());
            contenidoCartaDAO.update(contenido);
        }
    }

    @Override
    public List<ContenidoVariable> allVariablesByContenido(Long idContenido) {
        return contenidoVariableDAO.allByContenidoId(idContenido);
    }

    @Override
    @Transactional
    public void updateImgUrl(Long idContenido, String fileName) {

        ContenidoCarta contenidoCarta = contenidoCartaDAO.find(idContenido);

        if (contenidoCarta == null) {
            return;
        }

        String absoluteName = Constantine.TMP_DIR + fileName;
        File nuevo = new File(absoluteName);

        if (despliegueConfig.getS3() && nuevo.exists()) {
            logger.debug("TRYIN' TO UPLOAD {} TO S3", fileName);
            this.uploadS3(Constantine.S3_PUBLIC_DIR, Constantine.TMP_DIR, fileName, true);
            absoluteName = Constantine.S3_LINK + S3_PUBLIC_DIR + fileName;
            logger.debug("LINK ? {}", absoluteName);
        } else {
            this.uploadS3(Constantine.S3_TMP, Constantine.TMP_DIR, fileName, true);
            absoluteName = Constantine.S3_LINK + S3_TMP + fileName;
            logger.debug("LINK ? {}", absoluteName);
        }

        contenidoCarta.setImgUrl(absoluteName);
        contenidoCartaDAO.update(contenidoCarta);
    }

    private void uploadS3(String remoteDirectory, String localDirectory, String fileName, Boolean publico) {
        s3Service.uploadFileSync(Constantine.S3_DIR, remoteDirectory, localDirectory, fileName, publico);
    }

    @Override
    public List<Sistema> allSistema() {
        return sistemaDAO.allSistema();
    }

}

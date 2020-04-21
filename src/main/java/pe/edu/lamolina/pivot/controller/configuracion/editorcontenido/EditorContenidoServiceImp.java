package pe.edu.lamolina.pivot.controller.configuracion.editorcontenido;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.ContenidoCartaVariable;
import pe.edu.lamolina.model.inscripcion.ContenidoVariable;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.pivot.config.DespliegueConfig;
import pe.edu.lamolina.pivot.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.pivot.dao.general.ContenidoVariableDAO;
import pe.edu.lamolina.pivot.dao.seguridad.SistemaDAO;
import pe.edu.lamolina.pivot.dao.sip.ContenidoCartaVariableDAO;
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
    ContenidoCartaVariableDAO contenidoCartaVariableDAO;
    @Autowired
    SistemaDAO sistemaDAO;

    @Autowired
    StorageService swiftService;
    @Autowired
    DespliegueConfig despliegueConfig;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ContenidoCarta findContenidoCartaById(Long idContenido) {
        return contenidoCartaDAO.find(idContenido);
    }

    @Override
    public ContenidoCarta findSoloContenidoCartaById(Long idContenido) {
        ContenidoCarta conteBD = contenidoCartaDAO.find(idContenido);
        String html = conteBD.getContenido();
        List<ContenidoCartaVariable> vars = contenidoCartaVariableDAO.allByIdContenido(idContenido);
        for (ContenidoCartaVariable var : vars) {
            while (html.indexOf(var.getContenidoVariable().getCodigo()) > -1) {
                html = html.replace(var.getContenidoVariable().getCodigo(), var.getEjemplo());
            }
        }

        ContenidoCarta conteForm = new ContenidoCarta();
        conteForm.setContenido(html);

        return conteForm;
    }

    @Override
    @Transactional
    public void updateContenido(ContenidoCarta contenidoCarta) {
        ContenidoCarta contenidoCartaBD = contenidoCartaDAO.find(contenidoCarta.getId());
        contenidoCartaBD.setContenido(contenidoCarta.getContenido());
        contenidoCartaDAO.update(contenidoCartaBD);
    }

    @Override
    public List<ContenidoCarta> allContenidoCartaByDynaTable(DynatableFilter filter) {
        List<Sistema> sistemas = sistemaDAO.allByCodes(Arrays.asList("AMAUTA", "MAIPI", "BIEN", "MAT"));
        return contenidoCartaDAO.allByDynaTableBySistema(filter, sistemas);
    }

    @Override
    @Transactional
    public void save(ContenidoCarta contenido) {
        if (contenido.getId() == null) {
            ContenidoCarta contenidoNew = new ContenidoCarta();
            contenidoNew.setCodigo(contenido.getCodigo());
            contenidoNew.setNombre(contenido.getNombre());
            contenidoCartaDAO.save(contenidoNew);
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
    public List<ContenidoCartaVariable> allVariablesCartaByContenido(Long idContenido) {
        return contenidoCartaVariableDAO.allByIdContenido(idContenido);
    }

    @Override
    @Transactional
    public void updateImgUrl(Long idContenido, String fileName) {

        ContenidoCarta contenidoCarta = contenidoCartaDAO.find(idContenido);

        if (contenidoCarta == null) {
            return;
        }

        String absoluteName = GlobalConstantine.TMP_DIR + fileName;
        File nuevo = new File(absoluteName);

        if (despliegueConfig.getStorage() && nuevo.exists()) {
            logger.debug("TRYIN' TO UPLOAD {} TO S3", fileName);
            this.uploadS3(Constantine.S3_PUBLIC_DIR, GlobalConstantine.TMP_DIR, fileName, true);
            absoluteName = Constantine.S3_LINK + S3_PUBLIC_DIR + fileName;
            logger.debug("LINK ? {}", absoluteName);
        } else {
            this.uploadS3(Constantine.S3_TMP, GlobalConstantine.TMP_DIR, fileName, true);
            absoluteName = Constantine.S3_LINK + S3_TMP + fileName;
            logger.debug("LINK ? {}", absoluteName);
        }

        contenidoCarta.setImgUrl(absoluteName);
        contenidoCartaDAO.update(contenidoCarta);
    }

    private void uploadS3(String remoteDirectory, String localDirectory, String fileName, Boolean publico) {
        swiftService.uploadFileSync(Constantine.S3_DIR, remoteDirectory, localDirectory, fileName, publico);
    }

    @Override
    public List<Sistema> allSistema() {
        return sistemaDAO.all();
    }

    @Override
    public List<ContenidoVariable> allVariables() {
        return contenidoVariableDAO.all();
    }

    @Override
    @Transactional
    public void addVariable(ContenidoCartaVariable contVariableForm, Long idContenido) {
        ContenidoCarta conteBD = contenidoCartaDAO.find(idContenido);
        List<ContenidoCartaVariable> vars = contenidoCartaVariableDAO.allByIdContenido(idContenido);
        for (ContenidoCartaVariable var : vars) {
            if (contVariableForm.getContenidoVariable().getId() == var.getContenidoVariable().getId().longValue()) {
                throw new PhobosException("Esta variable ya existe en este contenido");
            }
        }

        ContenidoCartaVariable newVar = new ContenidoCartaVariable();
        newVar.setContenidoCarta(conteBD);
        newVar.setContenidoVariable(contVariableForm.getContenidoVariable());
        newVar.setEjemplo(contVariableForm.getEjemplo());
        contenidoCartaVariableDAO.save(newVar);

    }

    @Override
    @Transactional
    public void deleteVariable(Long idContVariable) {
        contenidoCartaVariableDAO.delete(idContVariable);
    }

    @Override
    @Transactional
    public void updateContVariable(ContenidoCartaVariable contVariable) {
        contenidoCartaVariableDAO.update(contVariable);
    }

}

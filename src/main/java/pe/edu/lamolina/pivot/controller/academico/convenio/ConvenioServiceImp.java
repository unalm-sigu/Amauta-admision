package pe.edu.lamolina.pivot.controller.academico.convenio;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CarreraConvenio;
import pe.edu.lamolina.model.academico.ConvenioBeca;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EstadoConvenioBecaEnum;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.CarreraConvenioDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.ConvenioBecaDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.general.EmpresaDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class ConvenioServiceImp implements ConvenioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ConvenioBecaDAO convenioBecaDAO;

    @Autowired
    EmpresaDAO empresaDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    CarreraConvenioDAO carreraConvenioDAO;

    @Autowired
    StorageService swiftService;

    @Override
    @Transactional
    public void delete(ConvenioBeca convenioBeca) {
        carreraConvenioDAO.deleteByConvenioBeca(convenioBeca);
        convenioBecaDAO.delete(convenioBeca);
    }

    @Override
    @Transactional
    public void update(ConvenioBeca convenioBeca, DataSessionPivot ds) {
        ConvenioBeca convenioBecaDB = convenioBecaDAO.find(convenioBeca.getId());
        convenioBeca.setUserRegistro(convenioBecaDB.getUserRegistro());
        convenioBeca.setFechaRegistro(convenioBecaDB.getFechaRegistro());
        convenioBeca.setEstado(convenioBecaDB.getEstado());
        this.saveArchivoS3(convenioBeca.getRutaDocumento());
        this.deleteArchivoS3(convenioBecaDB, convenioBeca);
        convenioBecaDAO.update(convenioBeca);
        carreraConvenioDAO.deleteByConvenioBeca(convenioBeca);
        List<CarreraConvenio> carreraConvenios = convenioBeca.getCarreraConvenio();
        for (CarreraConvenio carreraConvenio : carreraConvenios) {
            carreraConvenio.setConvenioBeca(convenioBeca);
            carreraConvenioDAO.save(carreraConvenio);
        }
    }

    @Override
    @Transactional
    public void save(ConvenioBeca convenioBeca, DataSessionPivot ds) {
        Usuario user = ds.getUsuario();
        convenioBeca.setUserRegistro(user);
        convenioBeca.setFechaRegistro(new Date());
        convenioBeca.setEstado(EstadoConvenioBecaEnum.PEN.name());
        this.saveArchivoS3(convenioBeca.getRutaDocumento());
        convenioBecaDAO.save(convenioBeca);
        List<CarreraConvenio> carreraConvenios = convenioBeca.getCarreraConvenio();
        for (CarreraConvenio carreraConvenio : carreraConvenios) {
            carreraConvenio.setConvenioBeca(convenioBeca);
            carreraConvenioDAO.save(carreraConvenio);
        }
    }

    @Override
    public ConvenioBeca findConvenioBeca(Long idConvenioBeca) {
        ConvenioBeca convenioBeca = convenioBecaDAO.find(idConvenioBeca);
        List<CarreraConvenio> carreraConvenios = carreraConvenioDAO.allByConvenioBeca(convenioBeca);
        convenioBeca.setCarreraConvenio(carreraConvenios);
        return convenioBeca;
    }

    @Override
    public List<ConvenioBeca> allByDynatable(DynatableFilter filter) {
        return convenioBecaDAO.allByDynatable(filter);
    }

    @Override
    @Transactional
    public void saveInstitucion(Empresa institucion) {
        empresaDAO.save(institucion);
    }

    @Override
    public List<Carrera> allCarreraByName(String nombre, Compania cia) {
        List<ModalidadEstudio> modalidadEstudio = modalidadEstudioDAO.allPrePostgrado(cia);
        return carreraDAO.allByNombreModalidad(nombre, modalidadEstudio);
    }

    @Override
    public Map<Long, List<CarreraConvenio>> allByCarreraConvenio(List<ConvenioBeca> convenios) {
        Map<Long, List<CarreraConvenio>> carreraConvenioMaps = new LinkedHashMap();
        if (convenios == null || convenios.isEmpty()) {
            return carreraConvenioMaps;
        }
        List<CarreraConvenio> carreraConvenios = carreraConvenioDAO.allByCarreraConvenio(convenios);
        carreraConvenioMaps = TypesUtil.convertListToMapList("convenioBeca.id", carreraConvenios);
        return carreraConvenioMaps;
    }

    @Override
    public List<CarreraConvenio> allCarreraConvenioByConvenioBeca(ConvenioBeca convenioBeca) {
        if (convenioBeca.getId() == null) {
            return new ArrayList();
        }
        return carreraConvenioDAO.allByConvenioBeca(convenioBeca);
    }

    @Override
    public String getCleanName(String originalFilename) {
        String nameOriginal = FilenameUtils.removeExtension(originalFilename);
        String nameOriginal1 = nameOriginal.replaceAll("[\\-]", " ");
        String nameOriginal2 = TypesUtil.getClean(nameOriginal1);
        String nameOriginal3 = nameOriginal2.replaceAll("[^a-zA-Z0-9\\s]", "");
        String nameOriginal4 = nameOriginal3.replaceAll("[\\s+]", "-");
        Date date = new Date();
        String formattedDate = new SimpleDateFormat("dd-MM-yyyy-hh.mm.SSSSSS-").format(date);
        return formattedDate + nameOriginal4 + ".pdf";
    }

    private void saveArchivoS3(String rutaDocumento) {
        File file = new File(GlobalConstantine.TMP_DIR + rutaDocumento);
        logger.debug("el archivo {} existe {} ", (GlobalConstantine.TMP_DIR + rutaDocumento), (file.exists()));
        if (file.exists()) {
            swiftService.uploadFile(Constantine.S3_DIR, Constantine.S3_DIR_CONVENIO, GlobalConstantine.TMP_DIR, rutaDocumento, true);
        }
    }

    private void deleteArchivoS3(ConvenioBeca convenioBecaDB, ConvenioBeca convenioBeca) {
        boolean requiereDelete = convenioBecaDB.getRutaDocumento().equalsIgnoreCase(convenioBeca.getRutaDocumento());
        if (!requiereDelete) {
            swiftService.deleteFile(Constantine.S3_DIR, Constantine.S3_DIR_CONVENIO, convenioBecaDB.getRutaDocumento());
        }
    }

    @Override
    @Transactional
    public void changeEstado(Long id) {
        ConvenioBeca beca = convenioBecaDAO.find(id);

        if (beca.getEstado().equals(EstadoConvenioBecaEnum.ACT.name())) {
            beca.setEstado(EstadoConvenioBecaEnum.DES.name());
        } else {
            beca.setEstado(EstadoConvenioBecaEnum.ACT.name());
        }

        convenioBecaDAO.update(beca);
    }

}

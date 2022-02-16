package pe.edu.lamolina.amauta.controller.academico.silabo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.SilaboCurso;
import pe.edu.lamolina.model.enums.SilaboCursoEstadoEnum;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.controller.comun.s3.UploadFileS3;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.SilaboCursoDAO;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Compania;

@Slf4j
@Service
@Transactional
public class SilaboServiceImp implements SilaboService {

    @Autowired
    SilaboCursoDAO silaboCursoDAO;

    @Autowired
    DespliegueConfig despliegueConfig;

    @Autowired
    StorageService swiftService;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    UploadFileS3 uploadFileS3;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Override
    public List<SilaboCurso> allSilabo(DynatableFilter filter) {
        return silaboCursoDAO.allByDynatable(filter);
    }

    @Override
    @Transactional
    public void save(SilaboCurso silabo) {

        if (silabo.getFileUpdated() != null) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy-HHmm");

            String fileName = "Silabo-" + silabo.getCurso().getCodigo() + "-" + sdf.format(new Date()) + ".pdf";

            FileHelper.renameFile(GlobalConstantine.TMP_DIR + silabo.getRutaDocumento(), GlobalConstantine.TMP_DIR + fileName);

            uploadFileS3.uploadSync(AcademicoConstantine.S3_DIR_SILABUS, GlobalConstantine.TMP_DIR, fileName, true);

            String path = uploadFileS3.getPathFile(AcademicoConstantine.S3_DIR_SILABUS, fileName);

            silabo.setRutaDocumento(path);

        }

        if (silabo.getId() == null) {
            silabo.setEstadoEnum(SilaboCursoEstadoEnum.CRE);
            silabo.setFechaRegistro(new Date());
            silaboCursoDAO.save(silabo);
        } else {
            silaboCursoDAO.updateColumns(silabo,"rutaDocumento","departamentoAcademico","curso");
        }
    }

    @Override
    @Transactional
    public void delete(SilaboCurso silabo) {
        silaboCursoDAO.delete(silabo.getId());
    }

    @Override
    @Transactional
    public String revision(SilaboCurso silabo) {
        SilaboCurso silaboDB = silaboCursoDAO.find(silabo.getId());
        String template = "El silabo ha cambiado su estado a %s";
        if (silaboDB == null) {
            throw new PhobosException("El silabo no existe");
        }
        if (silaboDB.getEstadoEnum() == SilaboCursoEstadoEnum.VEN) {
            throw new PhobosException("Este silabo no puede ser modificado");
        }

        if (!correcto(silaboDB.getEstadoEnum(), silabo.getEstadoEnum())) {
            throw new PhobosException("Ocurrió un error en su petición");
        }

        SilaboCursoEstadoEnum sEnum = SilaboCursoEstadoEnum.valueOf(silabo.getEstado());
        String msg = String.format(template, sEnum.getValue());
        silaboDB.setEstadoEnum(silabo.getEstadoEnum());

        if (silaboDB.getCicloVigenciaFin() != null && silabo.getEstadoEnum() == SilaboCursoEstadoEnum.ACT) {
            msg = String.format(template, SilaboCursoEstadoEnum.VEN.getValue());
            silaboDB.setEstadoEnum(SilaboCursoEstadoEnum.VEN);
        }
        silaboCursoDAO.update(silaboDB);
        return msg;
    }

    private Boolean correcto(SilaboCursoEstadoEnum viejo, SilaboCursoEstadoEnum nuevo) {

        if (nuevo == SilaboCursoEstadoEnum.ACT && viejo != SilaboCursoEstadoEnum.PEND) {
            return false;
        }

        if (nuevo == SilaboCursoEstadoEnum.VEN && viejo != SilaboCursoEstadoEnum.REV) {
            return false;
        }

        return true;
    }

    @Override
    public List<Curso> allCursoByModalidadEstudioNombre(String nombre, ModalidadEstudioEnum modalidadEstudioEnum) {
        return cursoDAO.allByModalidadEstudioNombre(modalidadEstudioEnum, nombre);
    }

    @Override
    public List<DepartamentoAcademico> allDepartamentoMod(String nombre, Compania compania) {
        return departamentoAcademicoDAO.allDepartemento(this.forLike(nombre), compania);
    }

    private String forLike(String nombre) {
        return "%" + nombre.replaceAll(" ", "%") + "%";
    }

}

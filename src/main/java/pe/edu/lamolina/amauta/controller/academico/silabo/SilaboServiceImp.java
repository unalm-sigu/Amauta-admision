package pe.edu.lamolina.amauta.controller.academico.silabo;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.comun.archivo.ArchivoService;
import pe.edu.lamolina.model.academico.SilaboCurso;
import pe.edu.lamolina.model.enums.SilaboCursoEstadoEnum;
import pe.edu.lamolina.amauta.controller.comun.s3.UploadFileS3;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.SilaboCursoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import static pe.edu.lamolina.model.constantines.AcademicoConstantine.S3_DIR_SILABUS;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.TMP_DIR;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Compania;

@Slf4j
@Service
@Transactional
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
public class SilaboServiceImp implements SilaboService {

    private final SilaboCursoDAO silaboCursoDAO;

    private final CursoDAO cursoDAO;

    private final UploadFileS3 uploadFileS3;

    private final DepartamentoAcademicoDAO departamentoAcademicoDAO;

    private final CicloAcademicoDAO cicloAcademicoDAO;

    private final ArchivoService archivoService;

    @Override
    public List<SilaboCurso> allSilabo(DynatableFilter filter) {
        return silaboCursoDAO.allByDynatable(filter);
    }

    @Override
    @Transactional
    public void save(SilaboCurso silabo) {

        if (silabo.getFileUpdated() != null) {

            log.debug("FilenameUtils {}", FilenameUtils.getName(silabo.getRutaDocumento()));

            String fileName = new StringJoiner("")
                    .add("SILABUS")
                    .add(TypesUtil.toMD5(FilenameUtils.getName(silabo.getRutaDocumento())))
                    .add(silabo.getRutaDocumento())
                    .toString();

            FileHelper.renameFile(TMP_DIR + silabo.getRutaDocumento(), TMP_DIR + fileName);

            uploadFileS3.uploadSync(S3_DIR_SILABUS, TMP_DIR, fileName, true);

            String path = uploadFileS3.getPathFile(S3_DIR_SILABUS, fileName);

            silabo.setRutaDocumento(path);

        }

        if (silabo.getId() == null) {

            silabo.setEstadoEnum(SilaboCursoEstadoEnum.CRE);
            silabo.setFechaRegistro(new Date());
            silaboCursoDAO.save(silabo);

        } else {

            silaboCursoDAO.updateColumns(silabo,
                    "rutaDocumento",
                    "departamentoAcademico",
                    "curso",
                    "cicloVigenciaInicio");

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

    @Override
    public List<CicloAcademico> allCiclo(DataSessionPivot ds) {
        CicloAcademico ca = ds.getCicloAcademico();
        int rango = 20;
        return cicloAcademicoDAO.allPregradoFuturosByRange(ca.getYear() - rango, ca.getYear() + 3);
    }

    @Override
    public void downloadZip(ArrayList<Long> silabus, HttpServletResponse response) {
        List<File> attachment = new ArrayList<>();
        List<SilaboCurso> silaboCursos = silaboCursoDAO.allByIds(silabus);
        String tmpFolder = TMP_DIR + "down" + System.currentTimeMillis() + "/";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy-HHmmss");
        new File(tmpFolder).mkdirs();
        for (SilaboCurso silaboCurso : silaboCursos) {
            log.debug("star download silabu {}", silaboCurso.getId());
            if (StringUtils.isBlank(silaboCurso.getRutaDocumento())) {
                log.debug("File silabu {} lose", silaboCurso.getId());
                continue;
            }
            log.debug("{}", silaboCurso.getRutaDocumento());

            String uniqueName = new StringJoiner("-").add("Silabus")
                    .add(silaboCurso.getCurso().getCodigo())
                    .add(TypesUtil.getClean(silaboCurso.getCurso().getNombre()))
                    .add(sdf.format(silaboCurso.getFechaRegistro())
                            + "." + FilenameUtils.getExtension(silaboCurso.getRutaDocumento()))
                    .toString();

            String fileName = tmpFolder + uniqueName;
            File fileDowload = new File(fileName);
            try {
                FileUtils.copyURLToFile(new URL(silaboCurso.getRutaDocumento()), fileDowload);
            } catch (MalformedURLException ex) {
                log.debug("MalformedURLException {}", silaboCurso.getRutaDocumento());
                continue;
            } catch (IOException ex) {
                log.debug("IOException {}", silaboCurso.getRutaDocumento());
                continue;
            }
            log.debug("end download silabu {}", silaboCurso.getId());
            attachment.add(fileDowload);
        }
        if (attachment.isEmpty()) {
            throw new PhobosException("No se ha encontrado ningún archivo en el servidor");
        }
        String fileCompress = "compress" + System.currentTimeMillis() + ".zip";
        try {
            new ZipFile(TMP_DIR + fileCompress).addFiles(attachment);
        } catch (ZipException ex) {
            throw new PhobosException("No se ha encontrado ningún archivo en el servidor");
        }
        archivoService.downloadTemp(fileCompress, "Silabus.zip", response);
    }

    @Override
    public List<DepartamentoAcademico> allDepartamento() {
        return departamentoAcademicoDAO.allActivos();
    }

}

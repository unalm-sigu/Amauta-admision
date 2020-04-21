package pe.edu.lamolina.amauta.controller.academico.silabo;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.SilaboCurso;
import pe.edu.lamolina.model.enums.SilaboCursoEstadoEnum;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.dao.academico.SilaboCursoDAO;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;

@Service
@Transactional
public class SilaboServiceImp implements SilaboService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SilaboCursoDAO silaboCursoDAO;
    @Autowired
    DespliegueConfig despliegueConfig;
    @Autowired
    StorageService swiftService;

    @Override
    public List<SilaboCurso> allSilabo(DynatableFilter filter) {
        return silaboCursoDAO.allByDynatable(filter);
    }

    @Override
    @Transactional
    public void save(SilaboCurso silabo) {

        if (silabo.getCicloVigenciaFin() != null && silabo.getCicloVigenciaInicio().getCodigoInt() >= silabo.getCicloVigenciaFin().getCodigoInt()) {
            throw new PhobosException("El ciclo de inicio no debe ser mayor al ciclo final");
        }

        List<SilaboCurso> silabos = silaboCursoDAO.allParents();
        for (SilaboCurso silab : silabos) {
            if (silabo.getCurso().getId() != silab.getCurso().getId().longValue()) {
                continue;
            }
            Integer minDB = silab.getCicloVigenciaInicio().getCodigoInt();
            Integer maxDB = silab.getCicloVigenciaFin() != null ? silab.getCicloVigenciaFin().getCodigoInt() : null;
            Integer min = silabo.getCicloVigenciaInicio().getCodigoInt();
            Integer max = silabo.getCicloVigenciaFin() != null ? silabo.getCicloVigenciaFin().getCodigoInt() : null;

            if (maxDB == null) {
                if (max == null) {
                    throw new PhobosException("Ya hay un silabo vigente.");
                }
                if (max >= minDB) {
                    throw new PhobosException("Ya hay un silabo vigente dentro de estos ciclos");
                }
            } else {
                if (min >= minDB && min <= maxDB) {
                    throw new PhobosException("El ciclo inicial se encuentra dentro del rango de un silabo existente");
                }
                if (max >= minDB && max <= maxDB) {
                    throw new PhobosException("El ciclo final se encuentra dentro del rango de un silabo existente");
                }
            }
        }

        if (silabo.getFileUpdated() != null) {
            String fileName = "Silabo-" + silabo.getCurso().getCodigo() + "-" + silabo.getCicloVigenciaInicio().getCodigo() + ".pdf";

            try {
                FileHelper.deleteFromDisk(GlobalConstantine.TMP_DIR + fileName);
            } catch (Exception e) {
                logger.debug("ELIMINAR ARCHIVO {} {}", fileName, e.getLocalizedMessage());
            }

            FileHelper.renameFile(GlobalConstantine.TMP_DIR + silabo.getRutaDocumento(), GlobalConstantine.TMP_DIR + fileName);

            if (despliegueConfig.getStorage()) {
                swiftService.uploadFileSync(AcademicoConstantine.S3_BUCKET_ACADEMICO, AcademicoConstantine.S3_DIR_SILABUS, GlobalConstantine.TMP_DIR, fileName, true);
                String s3Link = AcademicoConstantine.S3_URL_ACADEMICO + AcademicoConstantine.S3_DIR_SILABUS + fileName;
                silabo.setRutaDocumento(s3Link);
            } else {
                String ruta = "/comun/archivo/downloadTemp/" + fileName;
                silabo.setRutaDocumento(ruta);
            }
        }

        if (silabo.getId() == null) {
            silabo.setEstadoEnum(SilaboCursoEstadoEnum.CRE);
            silabo.setFechaRegistro(new Date());
            silaboCursoDAO.save(silabo);
        } else {
            SilaboCurso silaboDB = silaboCursoDAO.find(silabo.getId());
            if (silaboDB == null) {
                throw new PhobosException("El silabo no existe");
            }
            silabo.setUserRegistro(silaboDB.getUserRegistro());
            silabo.setFechaRegistro(silaboDB.getFechaRegistro());
            silaboCursoDAO.update(silabo);
        }
    }

    @Override
    @Transactional
    public void delete(SilaboCurso silabo) {
        silaboCursoDAO.delete(silabo);
    }

    @Override
    @Transactional
    public void revision(SilaboCurso silabo, JsonResponse response) {
        SilaboCurso silaboDB = silaboCursoDAO.find(silabo.getId());
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
        response.setMessage("El silabo ha cambiado su estado a " + sEnum.getValue());
        silaboDB.setEstadoEnum(silabo.getEstadoEnum());

        if (silaboDB.getCicloVigenciaFin() != null && silabo.getEstadoEnum() == SilaboCursoEstadoEnum.ACT) {
            response.setMessage("El silabo ha cambiado su estado a " + SilaboCursoEstadoEnum.VEN.getValue());
            silaboDB.setEstadoEnum(SilaboCursoEstadoEnum.VEN);
        }
        silaboCursoDAO.update(silaboDB);

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

}

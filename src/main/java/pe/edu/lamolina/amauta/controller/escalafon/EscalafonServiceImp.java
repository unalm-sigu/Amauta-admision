package pe.edu.lamolina.amauta.controller.escalafon;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.comun.s3.UploadFileS3;
import pe.edu.lamolina.amauta.dao.escalafon.AcademicoEscalafonDAO;
import pe.edu.lamolina.amauta.dao.escalafon.AreaInvestigacionDAO;
import pe.edu.lamolina.amauta.dao.escalafon.DistincionEscalfonDAO;
import pe.edu.lamolina.amauta.dao.escalafon.EscalafonDAO;
import pe.edu.lamolina.amauta.dao.escalafon.ExperienciaAsesorDAO;
import pe.edu.lamolina.amauta.dao.escalafon.ExperienciaEscalafonDAO;
import pe.edu.lamolina.amauta.dao.escalafon.IdiomaEscalafonDAO;
import pe.edu.lamolina.amauta.dao.escalafon.InvestigacionEscalafonDAO;
import pe.edu.lamolina.amauta.dao.escalafon.ProduccionEscalafonDAO;
import pe.edu.lamolina.amauta.dao.general.IdiomaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.escalafon.AcademicoEscalafon;
import pe.edu.lamolina.model.escalafon.AreaInvestigacion;
import pe.edu.lamolina.model.escalafon.DistincionEscalafon;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.escalafon.EscalafonConfirmBean;
import pe.edu.lamolina.model.escalafon.ExperienciaAsesor;
import pe.edu.lamolina.model.escalafon.ExperienciaEscalafon;
import pe.edu.lamolina.model.escalafon.IdiomaEscalafon;
import pe.edu.lamolina.model.escalafon.InvestigacionEscalafon;
import pe.edu.lamolina.model.escalafon.ProduccionEscalafon;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.seguridad.Usuario;

@Service
@Transactional
public class EscalafonServiceImp implements EscalafonService {

    @Autowired
    EscalafonDAO escalafonDAO;

    @Autowired
    IdiomaEscalafonDAO idiomaEscalafonDAO;

    @Autowired
    DistincionEscalfonDAO distincionEscalafonDAO;

    @Autowired
    IdiomaDAO idiomaDAO;

    @Autowired
    AcademicoEscalafonDAO academicoEscalafonDAO;

    @Autowired
    ExperienciaEscalafonDAO experienciaEscalafonDAO;

    @Autowired
    ExperienciaAsesorDAO experienciaAsesorDAO;

    @Autowired
    InvestigacionEscalafonDAO investigacionEscalafonDAO;

    @Autowired
    ProduccionEscalafonDAO produccionEscalafonDAO;

    @Autowired
    AreaInvestigacionDAO areaInvestigacionDAO;

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    UploadFileS3 uploadFileS3;

    @Override
    public List<Escalafon> allDynatable(DynatableFilter filter) {
        return escalafonDAO.allDynatableFilter(filter);
    }

    @Override
    public Escalafon save(Escalafon escalafon, Usuario usuario) {
        Escalafon escalafonBD = escalafonDAO.findByPersona(escalafon.getPersona());
        if (escalafonBD != null) {
            return escalafonBD;
        } else {
            escalafon.setUsuarioRegistro(usuario.getId());
            escalafon.setFechaCreacion(new Date());
            escalafonDAO.save(escalafon);
            return null;
        }
    }

    @Override
    public void eliminar(Escalafon escalafon) {
        Long idEscalafon = escalafon.getId();
        List<IdiomaEscalafon> listIdioma = idiomaEscalafonDAO.allByEscalafon(escalafon);
        if (!listIdioma.isEmpty()) {
            for (IdiomaEscalafon idiomaEscalafon : listIdioma) {
                idiomaEscalafonDAO.delete(idiomaEscalafon);
            }
        }

        List<DistincionEscalafon> listDistincion = distincionEscalafonDAO.allByEscalafon(escalafon);
        if (!listDistincion.isEmpty()) {
            for (DistincionEscalafon distincionEscalafon : listDistincion) {
                distincionEscalafonDAO.delete(distincionEscalafon);
            }
        }

        List<AcademicoEscalafon> listAcademico = academicoEscalafonDAO.allByEscalafon(escalafon);
        if (!listAcademico.isEmpty()) {
            for (AcademicoEscalafon academicoEscalafon : listAcademico) {
                academicoEscalafonDAO.delete(academicoEscalafon);
            }
        }

        List<ExperienciaEscalafon> listExperiencia = experienciaEscalafonDAO.allByEscalafon(escalafon);
        if (!listExperiencia.isEmpty()) {
            for (ExperienciaEscalafon experienciaEscalafon : listExperiencia) {
                experienciaEscalafonDAO.delete(experienciaEscalafon);
            }
        }

        List<ExperienciaAsesor> listExperienciaAsesor = experienciaAsesorDAO.allByEscalafon(escalafon);
        if (!listExperienciaAsesor.isEmpty()) {
            for (ExperienciaAsesor experienciaAsesor : listExperienciaAsesor) {
                experienciaAsesorDAO.delete(experienciaAsesor);
            }
        }

        List<InvestigacionEscalafon> listInvestigacion = investigacionEscalafonDAO.allByEscalafon(escalafon);
        if (!listInvestigacion.isEmpty()) {
            for (InvestigacionEscalafon investigacion : listInvestigacion) {
                investigacionEscalafonDAO.delete(investigacion);
            }
        }

        List<ProduccionEscalafon> listProduccion = produccionEscalafonDAO.allByEscalafon(escalafon);
        if (!listProduccion.isEmpty()) {
            for (ProduccionEscalafon produccion : listProduccion) {
                produccionEscalafonDAO.delete(produccion);
            }
        }
        escalafonDAO.delete(idEscalafon);
    }

    @Override
    public Escalafon loadEscalafon(Long idEscalafon) {
        Escalafon escalafon = escalafonDAO.find(idEscalafon);

        escalafon.setIdiomaEscalafon(idiomaEscalafonDAO.allByEscalafon(escalafon));
        escalafon.setDistincionEscalafon(distincionEscalafonDAO.allByEscalafon(escalafon));
        escalafon.setAcademicoEscalafon(academicoEscalafonDAO.allByEscalafon(escalafon));
        escalafon.setExperienciaEscalafon(experienciaEscalafonDAO.allByEscalafon(escalafon));
        escalafon.setExperienciaAsesor(experienciaAsesorDAO.allByEscalafon(escalafon));
        escalafon.setInvestigacionEscalafon(investigacionEscalafonDAO.allByEscalafon(escalafon));
        escalafon.setProduccionEscalafon(produccionEscalafonDAO.allByEscalafon(escalafon));

        return escalafon;
    }

    @Override
    public List<Idioma> allIdioma() {
        return idiomaDAO.all();
    }

    @Override
    public List<AreaInvestigacion> allAreaInvestigacion() {
        return areaInvestigacionDAO.all();
    }

    @Override
    public Escalafon updateGeneral(Escalafon escalafonForm, Usuario usuario) {
        String urlArchivoForm = escalafonForm.getArchivoCurriculum();
        Boolean isChange = false;
        Escalafon escalafonBD = escalafonDAO.find(escalafonForm.getId());

        if (urlArchivoForm != null && !urlArchivoForm.equals(escalafonBD.getArchivoCurriculum())) {
            File file = new File(GlobalConstantine.TMP_DIR + urlArchivoForm);
            System.out.println("el archivo {} existe {} " + (GlobalConstantine.TMP_DIR + urlArchivoForm) + (file.exists()));
            Assert.isTrue(file.exists(), "No existe el archivo en el servidor");
            uploadFileS3.uploadSync(AcademicoConstantine.S3_ESCALAFON_CURRICULUM, GlobalConstantine.TMP_DIR, urlArchivoForm, true);
            String path = uploadFileS3.getPathFile(AcademicoConstantine.S3_ESCALAFON_CURRICULUM, urlArchivoForm);  /// ENUM FALTA DEFINIR EL DIRECTORIO
            escalafonBD.setArchivoCurriculum(path);
            isChange = true;
        }

        escalafonBD.setPersona(escalafonForm.getPersona());
        escalafonBD.setPaisNacimiento(escalafonForm.getPaisNacimiento());
        escalafonBD.setEmailPersonal(escalafonForm.getEmailPersonal());
        escalafonBD.setCelular(escalafonForm.getCelular());
        escalafonBD.setCodigoDina(escalafonForm.getCodigoDina());
        escalafonBD.setCodigoOrcid(escalafonForm.getCodigoOrcid());
        escalafonBD.setCodigoScopus(escalafonForm.getCodigoScopus());
        escalafonBD.setWebsite(escalafonForm.getWebsite());
        escalafonBD.setResumen(escalafonForm.getResumen());
        escalafonBD.setUsuarioActualizacion(usuario.getId());
        escalafonBD.setFechaActualizacion(new Date());
        escalafonDAO.update(escalafonBD);

        if (isChange) {
            uploadFileS3.deleteFile(urlArchivoForm);
        }
        return escalafonBD;
    }

    @Override
    public Escalafon findEscalafon(Escalafon escalafon) {
        Escalafon escalafonBD = escalafonDAO.find(escalafon);
        escalafonBD.setPersona(personaDAO.find(escalafonBD.getPersona().getId()));
        return escalafonBD;
    }

    @Override
    public void confirmarEscalafon(EscalafonConfirmBean escalafonConfirmBean) {
        Arrays.asList("DistincionEsc", "AcademicoEsc", "ExperienciaEsc", "ExperienciaAsesorEsc", "InvestigacionEsc", "ProduccionEsc");
        switch (escalafonConfirmBean.getTipo()) {
            case "DistincionEsc":
                DistincionEscalafon itemDis = distincionEscalafonDAO.find(escalafonConfirmBean.getInstancia());
                itemDis.setConfirmado(Boolean.TRUE);
                itemDis.setNotaConfirmacion(escalafonConfirmBean.getNotaConfirm());
                distincionEscalafonDAO.update(itemDis);
                break;
            case "AcademicoEsc":
                AcademicoEscalafon itemAca = academicoEscalafonDAO.find(escalafonConfirmBean.getInstancia());
                itemAca.setConfirmado(Boolean.TRUE);
                itemAca.setNotaConfirmacion(escalafonConfirmBean.getNotaConfirm());
                academicoEscalafonDAO.update(itemAca);
                break;
            case "ExperienciaEsc":
                ExperienciaEscalafon itemExp = experienciaEscalafonDAO.find(escalafonConfirmBean.getInstancia());
                itemExp.setConfirmado(Boolean.TRUE);
                itemExp.setNotaConfirmacion(escalafonConfirmBean.getNotaConfirm());
                experienciaEscalafonDAO.update(itemExp);
                break;
            case "ExperienciaAsesorEsc":
                ExperienciaAsesor itemAse = experienciaAsesorDAO.find(escalafonConfirmBean.getInstancia());
                itemAse.setConfirmado(Boolean.TRUE);
                itemAse.setNotaConfirmacion(escalafonConfirmBean.getNotaConfirm());
                experienciaAsesorDAO.update(itemAse);
                break;
            case "InvestigacionEsc":
                InvestigacionEscalafon itemInv = investigacionEscalafonDAO.find(escalafonConfirmBean.getInstancia());
                itemInv.setConfirmado(Boolean.TRUE);
                itemInv.setNotaConfirmacion(escalafonConfirmBean.getNotaConfirm());
                investigacionEscalafonDAO.update(itemInv);
                break;
            case "ProduccionEsc":
                ProduccionEscalafon itemPro = produccionEscalafonDAO.find(escalafonConfirmBean.getInstancia());
                itemPro.setConfirmado(Boolean.TRUE);
                itemPro.setNotaConfirmacion(escalafonConfirmBean.getNotaConfirm());
                produccionEscalafonDAO.update(itemPro);
                break;
            default:
                break;
        }
    }

    @Override
    public void verificarFecha(Date fechaInicio, Date fechaFinal) {
        if (fechaFinal.before(fechaInicio)) {
            throw new PhobosException("Ingrese las fechas de manera correcta");
        }
    }

}

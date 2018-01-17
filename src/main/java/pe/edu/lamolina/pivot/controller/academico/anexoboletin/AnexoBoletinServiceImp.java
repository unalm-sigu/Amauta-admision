package pe.edu.lamolina.pivot.controller.academico.anexoboletin;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;

@Service
@Transactional(readOnly = true)
public class AnexoBoletinServiceImp implements AnexoBoletinService {

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Override
    public List<AnexoBoletin> allByDynatable(DynatableFilter filter) {
        return anexoBoletinDAO.allByDynatable(filter);
    }

    @Override
    public List<AnexoBoletin> allAnexosSuperiores() {
        return anexoBoletinDAO.allAnexosSuperiores();
    }

    @Override
    public List<DepartamentoAcademico> allDptosByNombre(String nombre) {
        return departamentoAcademicoDAO.allDepartamentos(this.forLike(nombre));
    }

    @Override
    public List<Carrera> allCarrerasByNombre(String nombre) {
        return carreraDAO.allByNombre(this.forLike(nombre));
    }

    private String forLike(String nombre) {
        return "%" + nombre.replaceAll(" ", "%") + "%";
    }

    @Override
    @Transactional
    public void save(AnexoBoletin anexo, Usuario usuario) {
        ObjectUtil.eliminarAttrSinId(anexo, "departamentoAcademico");
        ObjectUtil.eliminarAttrSinId(anexo, "carrera");
        if (anexo.getId() == null) {
            anexo.setCodigo("COD001");
            anexo.setEstado(EstadoEnum.CRE.name());
            anexoBoletinDAO.save(anexo);
        } else {
            AnexoBoletin anexoBD = anexoBoletinDAO.find(anexo.getId());
            anexoBD.setNombre(anexo.getNombre());
            anexoBD.setDepartamentoAcademico(anexo.getDepartamentoAcademico());
            anexoBD.setCarrera(anexo.getCarrera());
            anexoBoletinDAO.update(anexoBD);
        }
    }

    @Override
    public AnexoBoletin find(Long id) {
        return anexoBoletinDAO.find(id);
    }

    @Override
    @Transactional
    public void cambiarEstado(AnexoBoletin anexo) {
        AnexoBoletin anexoBD = anexoBoletinDAO.find(anexo.getId());

        if (anexoBD.getEstado().equals(EstadoEnum.ACT.name())) {
            anexoBD.setEstado(EstadoEnum.INA.name());
            anexoBD.setMotivoAnulacion(anexo.getMotivoAnulacion());
            anexoBD.setFechaAnulacion(new Date());
        } else {
            anexoBD.setEstado(EstadoEnum.ACT.name());
        }
        anexoBoletinDAO.update(anexoBD);
    }

    @Override
    public AnexoResumen resumen() {
        return anexoBoletinDAO.resumen();
    }

}

package pe.edu.lamolina.pivot.controller.posgrado.tarifa;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.pivot.dao.posgrado.TarifaCarreraDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class TarifaServiceImp implements TarifaService {

    @Autowired
    TarifaCarreraDAO tarifaCarreraDAO;

    @Override
    @Transactional
    public void save(TarifaCarrera tarifaCarrera, DataSessionPivot ds) {
        tarifaCarrera.setUserRegistro(ds.getUsuario());
        tarifaCarrera.setEstado(EstadoEnum.CRE.name());
        tarifaCarrera.setFechaRegistro(new Date());
        tarifaCarreraDAO.save(tarifaCarrera);
    }

    @Override
    @Transactional
    public void clonar(TarifaCarrera tarifaCarrera, DataSessionPivot ds) {
        TarifaCarrera clon = new TarifaCarrera();
        clon.setUserRegistro(ds.getUsuario());
        clon.setEstado(EstadoEnum.CRE.name());
        clon.setFechaRegistro(new Date());
        tarifaCarreraDAO.save(clon);
    }

    @Override
    public TarifaCarrera find(TarifaCarrera tarifaCarrera) {
        return tarifaCarreraDAO.find(tarifaCarrera.getId());
    }

    @Override
    @Transactional
    public void update(TarifaCarrera tarifaCarrera, DataSessionPivot ds) {
        tarifaCarreraDAO.update(tarifaCarrera);
    }

    @Override
    @Transactional
    public void eliminar(TarifaCarrera tarifaCarrera, DataSessionPivot ds) {
        Assert.isTrue(tarifaCarrera.getEstado().equals(EstadoEnum.CRE.name()), "Solo se pueden elimintar tarifas en estado creado");
        tarifaCarreraDAO.delete(tarifaCarrera);
    }

    @Override
    @Transactional
    public void activar(TarifaCarrera tarifaCarrera, DataSessionPivot ds) {
        TarifaCarrera tarifaBD = tarifaCarreraDAO.find(tarifaCarrera.getId());
        tarifaBD.setUserActivacion(ds.getUsuario());
        tarifaBD.setFechaActivacion(new Date());
        tarifaCarreraDAO.update(tarifaBD);
    }

    @Override
    public List<TarifaCarrera> allByDynatableCicloAcademicor(DynatableFilter filter, CicloAcademico ds) {
        return tarifaCarreraDAO.allByDynatableCiclo(filter, ds);
    }

}

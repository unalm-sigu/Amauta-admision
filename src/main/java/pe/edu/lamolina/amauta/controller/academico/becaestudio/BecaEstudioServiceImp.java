package pe.edu.lamolina.amauta.controller.academico.becaestudio;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.BecaEstudio;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.TipoDocIdentidadEnum;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.amauta.dao.academico.BecaEstudioDAO;
import pe.edu.lamolina.amauta.dao.general.EmpresaDAO;
import pe.edu.lamolina.amauta.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
public class BecaEstudioServiceImp implements BecaEstudioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BecaEstudioDAO becaestudioDAO;
    @Autowired
    EmpresaDAO empresaDAO;
    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;

    @Override
    public List<BecaEstudio> allByDynatable(DynatableFilter filter) {
        return becaestudioDAO.allDynaTable(filter);
    }

    @Override
    @Transactional
    public void save(BecaEstudio becaestudio, DataSessionPivot ds) {
        becaestudioDAO.save(becaestudio);
    }

    @Override
    @Transactional
    public void update(BecaEstudio becaestudioForm, DataSessionPivot ds) {
        BecaEstudio becaestudioBD = becaestudioDAO.find(becaestudioForm.getId());
        Assert.isNotNull(becaestudioForm.getNombre(), "El nombre esta vacio");

        becaestudioBD.setNombre(becaestudioForm.getNombre());
        becaestudioBD.setInstitucion(becaestudioForm.getInstitucion());
        becaestudioDAO.update(becaestudioBD);
    }

    @Override
    @Transactional
    public Empresa saveInstitucion(Empresa insticion) {
        TipoDocIdentidad doc = tipoDocIdentidadDAO.findBySimbolo(TipoDocIdentidadEnum.RUC.name());

        insticion.setTipoDocIdentidad(doc);
        insticion.setPaisUbicacion(new Pais(GlobalConstantine.ID_PERU));
        empresaDAO.save(insticion);
        return insticion;
    }

    @Override
    @Transactional
    public void delete(BecaEstudio becaestudio, DataSessionPivot ds) {
        becaestudioDAO.delete(becaestudio);
    }

    @Override
    public List<Empresa> allInstituciones() {
        return empresaDAO.all();
    }

}

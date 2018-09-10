package pe.edu.lamolina.pivot.controller.academico.cargaadicional.factor1;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfiguraCargaAdicional;
import pe.edu.lamolina.model.academico.Factor1CargaAdicional;
import pe.edu.lamolina.model.rrhh.CategoriaDocente;
import pe.edu.lamolina.model.rrhh.SituacionDocente;
import pe.edu.lamolina.pivot.dao.academico.ConfiguraCargaAdicionalDAO;
import pe.edu.lamolina.pivot.dao.academico.Factor1CargaAdicionalDAO;
import pe.edu.lamolina.pivot.dao.rrhh.CategoriaDocenteDAO;
import pe.edu.lamolina.pivot.dao.rrhh.SituacionDocenteDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CargaAdicionalFactor1ServiceImp implements CargaAdicionalFactor1Service {

    @Autowired
    ConfiguraCargaAdicionalDAO configuraCargaAdicionalDAO;

    @Autowired
    Factor1CargaAdicionalDAO factor1CargaAdicionalDAO;

    @Autowired
    CategoriaDocenteDAO categoriaDocenteDAO;

    @Autowired
    SituacionDocenteDAO situacionDocenteDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Factor1CargaAdicional> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return factor1CargaAdicionalDAO.allByDynatableCicloAcademico(filter, cicloAcademico);
    }

    @Override
    public List<CategoriaDocente> allCategoriaDocente() {
        return categoriaDocenteDAO.all();
    }

    @Override
    public List<SituacionDocente> allSituacionDocente() {
        return situacionDocenteDAO.all();
    }

    @Override
    @Transactional
    public void delete(Long id, DataSessionPivot ds) {
        factor1CargaAdicionalDAO.delete(id);
    }

    @Override
    public Factor1CargaAdicional find(Long id) {
        return factor1CargaAdicionalDAO.find(id);
    }

    @Override
    @Transactional
    public void save(Factor1CargaAdicional factor, DataSessionPivot ds) {
        Factor1CargaAdicional factorBD = factor1CargaAdicionalDAO.findByCategoriaSituacionCicloAcademico(factor.getCategoriaDocente(), factor.getSituacionDocente(), ds.getCicloAcademico());

        if (factorBD != null) {
            factorBD.setFactor(factor.getFactor());
            factorBD.setCreditosMinimo(factor.getCreditosMinimo());
            factorBD.setUserRegistro(ds.getUsuario());
            factorBD.setFechaRegistro(new Date());
            factor1CargaAdicionalDAO.update(factorBD);
        } else {
            factor.setCicloAcademico(ds.getCicloAcademico());
            factor.setUserRegistro(ds.getUsuario());
            factor.setFechaRegistro(new Date());
            factor1CargaAdicionalDAO.save(factor);
        }
        
    }

    @Override
    @Transactional
    public void update(Factor1CargaAdicional factor1CargaAdicional, DataSessionPivot ds) {
        Factor1CargaAdicional factorBD = factor1CargaAdicionalDAO.find(factor1CargaAdicional.getId());
        factorBD.setCategoriaDocente(factor1CargaAdicional.getCategoriaDocente());
        factorBD.setCreditosMinimo(factor1CargaAdicional.getCreditosMinimo());
        factorBD.setFactor(factor1CargaAdicional.getFactor());
        factorBD.setSituacionDocente(factor1CargaAdicional.getSituacionDocente());

        factorBD.setUserRegistro(ds.getUsuario());
        factorBD.setFechaRegistro(new Date());

        factor1CargaAdicionalDAO.update(factorBD);
    }

    @Override
    public ConfiguraCargaAdicional findConfiguracionByCiclo(CicloAcademico cicloAcademico) {
        return configuraCargaAdicionalDAO.findByCicloAcademico(cicloAcademico);
    }

}

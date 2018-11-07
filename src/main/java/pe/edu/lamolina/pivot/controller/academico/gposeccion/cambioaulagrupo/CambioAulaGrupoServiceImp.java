package pe.edu.lamolina.pivot.controller.academico.gposeccion.cambioaulagrupo;

import java.util.Date;
import java.util.List;
import static org.apache.commons.math3.stat.inference.TestUtils.t;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.CambioAulaGrupo;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AmpliacionVacanteEstadoEnum;
import pe.edu.lamolina.model.enums.CambioAulaGrupoEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.general.oficina.OficinaService;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.CambioAulaGrupoDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CambioAulaGrupoServiceImp implements CambioAulaGrupoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    CambioAulaGrupoDAO cambioAulaGrupoDAO;

    @Autowired
    OficinaService oficinaService;
        
    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Override
    public List<CambioAulaGrupo> allAulaGrupos(Seccion seccion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional
    public void saveCambioAulaGrupo(CambioAulaGrupo cambioAulaGrupo, DataSessionPivot ds) {

        Persona persona = ds.getPersona();
        Oficina oficinaMain = cambioAulaGrupo.getOficina();
        Oficina oficinaReal = oficinaService.findOficinaHija(persona, oficinaMain);
        Assert.isNotNull(oficinaReal, "Usted no se encuentra activo en la oficina " + oficinaMain.getNombre());

        Colaborador colaborador = colaboradorDAO.findActivoByPersonaOficina(oficinaReal, persona);
        Assert.isNotNull(colaborador, "Usted no se encuentra activo en la oficina " + oficinaReal.getNombre());

        cambioAulaGrupo.setColaborador(colaborador);
        
        cambioAulaGrupo.setFechaModificacion(new Date());
        cambioAulaGrupo.setUserModificacion(ds.getUsuario());
        cambioAulaGrupo.setFechaRegistro(new Date());
        cambioAulaGrupo.setFechaSolicitud(new Date());
        cambioAulaGrupo.setUserRegistro(ds.getUsuario());
        cambioAulaGrupo.setEstadoEnum(CambioAulaGrupoEstadoEnum.ACEPTADO);

        cambioAulaGrupoDAO.save(cambioAulaGrupo);
    }

}

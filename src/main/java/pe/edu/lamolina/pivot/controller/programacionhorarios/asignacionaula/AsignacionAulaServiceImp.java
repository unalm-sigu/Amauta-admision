package pe.edu.lamolina.pivot.controller.programacionhorarios.asignacionaula;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.AsignacionAula;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.dao.academico.AsignacionAulaDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = false)
public class AsignacionAulaServiceImp implements AsignacionAulaService {

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AsignacionAulaDAO asignacionAulaDAO;

    @Override
    public CicloAcademico findCiclo(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.find(cicloAcademico);
    }

    @Override
    public AsignacionAula findAsignacionAulaByCiclo(CicloAcademico cicloAcademico) {
        return asignacionAulaDAO.findByCiclo(cicloAcademico);
    }

    @Override
    public void procesarAsignacionAulas(AsignacionAula asignacionAula, DataSessionPivot ds) {
        
    } 

}

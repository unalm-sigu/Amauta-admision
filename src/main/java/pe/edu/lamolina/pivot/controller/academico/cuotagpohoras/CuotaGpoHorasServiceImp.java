package pe.edu.lamolina.pivot.controller.academico.cuotagpohoras;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.FrenchMethod;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.AlumnoCuotaEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.posgrado.AlumnoConceptoMatricula;
import pe.edu.lamolina.model.posgrado.AlumnoCuotaMatricula;
import pe.edu.lamolina.model.posgrado.AlumnoResumenCuotas;
import pe.edu.lamolina.model.posgrado.ConceptoPosgrado;
import pe.edu.lamolina.model.posgrado.TarifaCarrera;
import pe.edu.lamolina.model.posgrado.TarifaConcepto;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoConceptoMatriculaDAO;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoCuotaMatriculaDAO;
import pe.edu.lamolina.pivot.dao.posgrado.AlumnoResumenCuotasDAO;
import pe.edu.lamolina.pivot.dao.posgrado.ConceptoPosgradoDAO;
import pe.edu.lamolina.pivot.dao.posgrado.TarifaCarreraDAO;
import pe.edu.lamolina.pivot.dao.posgrado.TarifaConceptoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CuotaGpoHorasServiceImp implements CuotaGpoHorasService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;

   /*
    @Override
    public List<Alumno> allAlumnosPosgrado(DynatableFilter filter, CicloAcademico cicloAcademico) {
        List<String> modalidadesEstudios = new ArrayList<>();
        modalidadesEstudios.add(ModalidadEstudioEnum.EPG.name());
        modalidadesEstudios.add(ModalidadEstudioEnum.ESP.name());
        return alumnoDAO.allByModalidadesDynatable(filter, cicloAcademico, modalidadesEstudios);
    }*/

}

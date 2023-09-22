package pe.edu.lamolina.amauta.controller.programacionhorarios.reporte;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.FacultadDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ReporteProgramacionServiceImp implements ReporteProgramacionService {

    private final CarreraDAO carreraDAO;
    private final FacultadDAO facultadDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final AlumnoCicloDAO alumnoCicloDAO;
    private final ModalidadEstudioDAO modalidadEstudioDAO;

    @Override
    public List<MatriculaPreBean> allMatriculaPregrado(CicloAcademico cicloAcademico, String facultad) {
        List<MatriculaPreBean> matriculaPreBean = new ArrayList<>();
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        matriculaPreBean = alumnoCicloDAO.allmatriculadosPregrado(cicloAcademico, modalidadEstudio, facultad);

        return matriculaPreBean;
    }

    @Override
    public List<Carrera> allCarrera() {
        return carreraDAO.allCarrera();
    }

    @Override
    public List<Facultad> allFacultadesPre() {
        return facultadDAO.allFacultadesPre();
    }

    @Override
    public CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico) {
        return cicloAcademicoDAO.find(cicloAcademico);
    }
    
    @Override
    public List<Carrera> searchAllCarrera(String nombre) {
        return carreraDAO.searchByNombre(nombre);
    }

}

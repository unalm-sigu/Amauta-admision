package pe.edu.lamolina.amauta.controller.programacionhorarios.reporte;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
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
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.seguridad.Rol;

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
    public List<Facultad> allFacultadesPre(DataSessionPivot ds) {

        List<Facultad> facultades = new ArrayList<>();
        List<RolEnum> rolesEnum = ds.getRoles().stream().map(x -> x.getCodigoEnum()).collect(Collectors.toList());
        List<String> rolesCodigo = ds.getRoles().stream().map(x -> x.getCodigo()).collect(Collectors.toList());

        if (rolesEnum.contains(RolEnum.IOREA)) {
            return facultadDAO.allNormal();
        }

        if (rolesCodigo.contains("REPORT_PROGRAM_FACU")) {//TMP, ponerlo en ENUM 
            return facultadDAO.allFacultadesPre(ds.getFacultades());
        }

        return facultades;
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

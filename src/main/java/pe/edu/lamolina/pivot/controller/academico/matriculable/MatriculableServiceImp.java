package pe.edu.lamolina.pivot.controller.academico.matriculable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_1;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_2U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_3U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_4U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_5;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_6U;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_9;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_EM;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_N;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_TU;
import pe.edu.lamolina.pivot.controller.academico.alumno.AlumnoResumen;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class MatriculableServiceImp implements MatriculableService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;

    @Override
    public AlumnoResumen allResumenAlumnosByCicloRol(CicloAcademico cicloAcademico, String codigo, List<Long> filtros) {
        return matriculaResumenDAO.findResumenByCicloRolDynateable(cicloAcademico, codigo, filtros);
    }

    @Override
    public List<MatriculaResumen> allAlumnosByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, String codigo, List<Long> filtros) {
        return matriculaResumenDAO.allByCicloRolDynatable(filter, cicloAcademico, codigo, filtros);
    }

    @Override
    public MatriculableResumen findResumenByCiclo(CicloAcademico cicloAcademico) {
        return alumnoDAO.findResumenByCiclo(cicloAcademico);
    }

    @Override
    @Transactional
    public List<ModalidadEstudio> allModalidadEstudioByCodigos(List<String> codigos) {
        return modalidadEstudioDAO.allByCodigos(codigos);
    }

    @Override
    public void generar(CicloAcademico ciclo, DataSessionPivot ds) {

        ModalidadEstudio pre = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        ModalidadEstudio epg = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.EPG);

        List<SituacionAcademica> situacionesPregrado = situacionAcademicaDAO.allByCodes(
                Arrays.asList(S_N, S_1, S_2, S_3, S_5, S_8, S_9, S_EM, S_3U, S_2U, S_4U, S_6U, S_TU));
        List<SituacionAcademica> situacionesPosgrado = situacionAcademicaDAO.allByCodes(
                Arrays.asList(S_N, S_1, S_2, S_3, S_5, S_EM));

        List<Alumno> pregrados = alumnoDAO.allBySituaciones(pre, situacionesPregrado);
        List<Alumno> posgrados = alumnoDAO.allBySituaciones(epg, situacionesPosgrado);

        List<MatriculaResumen> matriculables = matriculaResumenDAO.allByCiclo(ciclo);
        Map<String, MatriculaResumen> mapMatriculables = TypesUtil.convertListToMap("alumno.codigo", matriculables);

        for (Alumno alumno : pregrados) {
            MatriculaResumen matri = mapMatriculables.get(alumno.getCodigo());
            if (matri != null) {
                continue;
            }

            matri = new MatriculaResumen();
            matri.setAlumno(alumno);
            matri.setCicloAcademico(ciclo);
            matri.setSituacionInicio(alumno.getSituacionAcademica());

            matri.setCreditosMatriculados(0);
            matri.setCreditosRetirados(0);
            matri.setCursosMatriculados(0);
            matri.setCursosRetirados(0);
            matri.setPorcentajeAvance(0);
            matri.setNotaAcumulada("0");
            matri.setNotaAvance("0");
            matri.setNotaFinal("0");
            matri.setEstadoEnum(EstadoMatriculaEnum.NMAT);
            matriculaResumenDAO.save(matri);
        }

        for (Alumno alumno : posgrados) {
            MatriculaResumen matri = mapMatriculables.get(alumno.getCodigo());
            if (matri != null) {
                continue;
            }

            matri = new MatriculaResumen();
            matri.setAlumno(alumno);
            matri.setCicloAcademico(ciclo);
            matri.setSituacionInicio(alumno.getSituacionAcademica());

            matri.setCreditosMatriculados(0);
            matri.setCreditosRetirados(0);
            matri.setCursosMatriculados(0);
            matri.setCursosRetirados(0);
            matri.setPorcentajeAvance(0);
            matri.setNotaAcumulada("0");
            matri.setNotaAvance("0");
            matri.setNotaFinal("0");
            matri.setEstadoEnum(EstadoMatriculaEnum.NMAT);
            matriculaResumenDAO.save(matri);
        }

    }

}

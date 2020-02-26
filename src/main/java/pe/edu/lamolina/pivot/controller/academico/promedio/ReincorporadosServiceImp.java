package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;

@Service
@Transactional(readOnly = true)
public class ReincorporadosServiceImp implements ReincorporadosService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Override
    public List<Reincorporacion> allReincorporacionesByCicloActivo(List<Alumno> alumnos, List<CicloAcademico> ciclosActivos) {
        CicloAcademico cicloActivoPregrado = ciclosActivos.stream()
                .filter(x -> x.getModalidadEstudio().getCodigoEnum().equals(ModalidadEstudioEnum.PRE))
                .findFirst().orElse(null);

        CicloAcademico cicloActivoPosgrado = ciclosActivos.stream()
                .filter(x -> x.getModalidadEstudio().getCodigoEnum().equals(ModalidadEstudioEnum.EPG))
                .findFirst().orElse(null);

        List<Alumno> alumnosPregrados = alumnos.stream().filter(x -> x.isPregrado()).collect(Collectors.toList());
        List<Alumno> alumnosPosgrados = alumnos.stream().filter(x -> x.isPostgrado()).collect(Collectors.toList());

        List<Reincorporacion> reincorporacionesPregradoAntes = reincorporacionDAO.allAceptadosByAlumnosSinCiclo(alumnosPregrados, cicloActivoPregrado);
        List<Reincorporacion> reincorporacionesPregradoActivo = reincorporacionDAO.allAceptadasPendientesByAlumnosCiclo(alumnosPregrados, cicloActivoPregrado);

        List<Reincorporacion> reincorporacionesPosgradoAntes = reincorporacionDAO.allAceptadosByAlumnosSinCiclo(alumnosPosgrados, cicloActivoPosgrado);
        List<Reincorporacion> reincorporacionesPosgradoActivo = reincorporacionDAO.allAceptadasPendientesByAlumnosCiclo(alumnosPosgrados, cicloActivoPosgrado);

        reincorporacionesPregradoAntes.addAll(reincorporacionesPregradoActivo);
        reincorporacionesPregradoAntes.addAll(reincorporacionesPosgradoAntes);
        reincorporacionesPregradoAntes.addAll(reincorporacionesPosgradoActivo);

        return reincorporacionesPregradoAntes;
    }

    @Override
    public List<Reincorporacion> allReincorporacionesByAlumno(Alumno alumno, CicloAcademico cicloActivo) {
        List<Reincorporacion> reincorporacionesAceptadasByAlumno = reincorporacionDAO.allAceptadasByAlumnoSinCiclo(alumno, cicloActivo);
        List<Reincorporacion> reincorporacionesByAlumnoCiclo = reincorporacionDAO.allAceptadasPendientesByAlumnoCiclo(alumno, cicloActivo);
        reincorporacionesAceptadasByAlumno.addAll(reincorporacionesByAlumnoCiclo);

        return reincorporacionesAceptadasByAlumno;
    }

}

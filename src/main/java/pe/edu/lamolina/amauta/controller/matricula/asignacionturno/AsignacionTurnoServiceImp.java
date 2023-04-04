package pe.edu.lamolina.amauta.controller.matricula.asignacionturno;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.edu.lamolina.amauta.controller.academico.evento.EventoCicloAcademicoService;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.TurnoAtencionDAO;
import pe.edu.lamolina.amauta.dao.matricula.MatriculaTurnoDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.TurnoAtencion;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.matricula.MatriculaTurno;
import pe.edu.lamolina.model.seguridad.Usuario;


@Service
public class AsignacionTurnoServiceImp implements AsignacionTurnoService {

    @Autowired
    MatriculaTurnoDAO matriculaTurnoDAO;
    
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    
    @Autowired
    TurnoAtencionDAO turnoAtencionDAO;
    
    @Autowired
    EventoCicloAcademicoService eventoCicloAcademicoService;

    
    @Override
    public DynatableResponse findAllMatriculaTurnoByCiclo(DynatableFilter filter, HttpSession httpSession) {
        DataSessionPivot dataSessionPivot = dataSessionPivot(httpSession);
        CicloAcademico cicloAcademico = dataSessionPivot.getCicloAcademico();
        List<MatriculaTurno> matriculaTurnos = matriculaTurnoDAO.findAllMatriculaTurnoByCiclo(filter, cicloAcademico);        
        Map<MatriculaResumen, List<MatriculaTurno>> mapMatriculaTurno = matriculaTurnos.stream().collect(Collectors.groupingBy(x -> x.getMatriculaResumen()));                
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        mapMatriculaTurno.forEach((key, values) -> {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            ObjectNode objMatriculaResumen = JaneHelper.from(key)
                    .only("id, estado, estadoEnum")
                    .join("alumno", "id, codigo, pregrado")
                    .join("alumno.modalidadEstudio", "id, nombre")
                    .join("alumno.carrera", "id, nombre, tipoEnum, tipo")
                    .join("alumno.carrera.facultad", "id, nombre")
                    .join("alumno.persona", "id, paterno, materno, nombres, nombreCompleto, numeroDocIdentidad, tipoFoto, rutaFoto")
                    .join("alumno.persona.tipoDocumento", "id, simbolo")
                    .json();
            values.forEach(value -> {
                ObjectNode objMatriculaTurno = JaneHelper.from(value)
                        .join("turnoAtencion")
                        .join("eventoAcademico")
                        .json();
                array.add(objMatriculaTurno);
            });
            objMatriculaResumen.set("turnosMatricula", array);
            arrayNode.add(objMatriculaResumen);
        });
        DynatableResponse json = new DynatableResponse();
        json.setData(arrayNode);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());
        return json;
    }

    @Override
    public boolean nuevoTurno(MatriculaTurno matriculaTurnoForm, HttpSession httpSession) {
        DataSessionPivot dataSessionPivot = dataSessionPivot(httpSession);
        CicloAcademico cicloAcademico = dataSessionPivot.getCicloAcademico();
        Usuario usuario = dataSessionPivot.getUsuario();
        EventoCicloAcademico eventoCicloAcademico = eventoCicloAcademicoService.findByCicloAndEvento(cicloAcademico, EventoAcademicoEnum.MAT_REG);
        boolean seRegistro = false;
        if (!Optional.ofNullable(matriculaTurnoForm.getId()).isPresent()) {
            MatriculaResumen matriculaResumenDB = matriculaResumenDAO.find(matriculaTurnoForm.getMatriculaResumen().getId()); 
            MatriculaTurno matriculaTurnoDB = matriculaTurnoDAO.findMatriculaTurnoByTurnoAtencion(matriculaTurnoForm.getTurnoAtencion());
            if(Objects.isNull(matriculaTurnoDB)) {
                matriculaTurnoDB = new MatriculaTurno();
                matriculaTurnoDB.setFechaRegistro(new Date());
                matriculaTurnoDB.setMatriculaResumen(matriculaResumenDB);
                matriculaTurnoDB.setMotivo(matriculaTurnoForm.getMotivo());
                matriculaTurnoDB.setTurnoAtencion(matriculaTurnoForm.getTurnoAtencion());
                matriculaTurnoDB.setEventoAcademico(eventoCicloAcademico.getEventoAcademico());
                matriculaTurnoDB.setVecesIngreso(0);
                matriculaTurnoDB.setUserRegistro(usuario);
                matriculaTurnoDAO.save(matriculaTurnoDB);                
                seRegistro = true;
            }
        }        
        return seRegistro;
    }

    @Override
    public ArrayNode findAllTurnoAtencionByAlumno() {
        Date hoy = new Date();
        List<TurnoAtencion> turnosHoy = turnoAtencionDAO.findAllTurnoAtencionByFecha(hoy);
        ArrayNode array = JaneHelper
                .from(turnosHoy)
                .array();
        return array;
    }
    
    private DataSessionPivot dataSessionPivot(HttpSession httpSession) {
        DataSessionPivot dataSessionPivot = (DataSessionPivot) httpSession.getAttribute(GlobalConstantine.SESSION_USUARIO);
        return dataSessionPivot;
    }   

}

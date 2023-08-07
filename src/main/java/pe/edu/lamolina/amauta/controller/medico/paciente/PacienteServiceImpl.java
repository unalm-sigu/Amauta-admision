package pe.edu.lamolina.amauta.controller.medico.paciente;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.amauta.controller.responserest.ResponseRestService;
import pe.edu.lamolina.amauta.dao.medico.PacienteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.medico.Paciente;
import pe.edu.lamolina.model.seguridad.TokenIngresante;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class PacienteServiceImpl implements PacienteService {

    private final PacienteDAO pacienteDAO;
    private final ResponseRestService responseRestService;

    @Override
    public Paciente findPaciente(Persona persona, DataSessionPivot ds) {
        Paciente paciente = pacienteDAO.findByPersona(persona);
        if (paciente != null) {
            return paciente;
        }

        TokenIngresante token = responseRestService.createToken(ds);
        JsonResponse response = responseRestService.crearPaciente(persona, ds, token);
        Assert.isTrue(response.getSuccess(), response.getMessage());
        
        return pacienteDAO.findByPersona(persona);
    }

}

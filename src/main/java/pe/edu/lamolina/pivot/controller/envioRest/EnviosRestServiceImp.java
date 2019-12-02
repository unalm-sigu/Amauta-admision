package pe.edu.lamolina.pivot.controller.envioRest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo.ResponseRestService;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class EnviosRestServiceImp implements EnviosRestService {

    @Autowired
    ResponseRestService responseRestService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void modificarDescuento(Seccion seccion, DataSessionPivot ds) {
        responseRestService.createToken(ds);
      
        JsonResponse jsonResponse = responseRestService.modificarDescuento(seccion, ds);

        Assert.isTrue(jsonResponse.getSuccess(), "Se produjo un error al agregar aportes. Comuniquese con mesa de ayuda.");

    }
}

package pe.edu.lamolina.pivot.controller.soporte;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Soporte;
import pe.edu.lamolina.model.enums.SoporteEstadoEnum;
import pe.edu.lamolina.pivot.dao.academico.SoporteDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class SoporteServiceImp implements SoporteService {

    @Autowired
    SoporteDAO soporteDAO;

    @Override
    @Transactional
    public void responder(Soporte soporte, DataSessionPivot ds) {

        soporte.setEstadoEnum(SoporteEstadoEnum.ATEN);
        soporte.setFechaAtencion(new Date());
        soporte.setUserAtencion(ds.getUsuario());
        soporteDAO.updateColumns(soporte, "estado", "fechaAtencion", "userAtencion", "respuesta");
    }

    @Override
    public List<Soporte> list(DynatableFilter filter) {

        return soporteDAO.allDyanatable(filter);
    }

}

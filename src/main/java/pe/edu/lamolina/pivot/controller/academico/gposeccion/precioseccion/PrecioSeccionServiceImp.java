package pe.edu.lamolina.pivot.controller.academico.gposeccion.precioseccion;

import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PrecioSeccionServiceImp implements PrecioSeccionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;

    @Override
    public void savePrecioSeccion(Seccion precioSeccion, DataSessionPivot ds) {
        precioSeccion.setUserPrecio(ds.getUsuario());
        precioSeccion.setFechaPrecio(new Date());
        seccionDAO.updatePrecioBySeccion(precioSeccion);
    }

}

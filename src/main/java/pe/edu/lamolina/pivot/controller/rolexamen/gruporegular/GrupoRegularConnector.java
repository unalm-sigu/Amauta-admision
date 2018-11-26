package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface GrupoRegularConnector {

    void savedLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular);

    void crearLetraGrupoRegularByLetra(
            LetraGrupoRegular letraGrupoRegular,
            Map<String, List<Seccion>> grupoHorasLetraMap,
            List<Seccion> seccionesEspeciales,
            DateTime today,
            Usuario usuario);

    boolean procesarSeccionesByLetra(
            LetraGrupoRegular letraGrupoRegular, Seccion seccion,
            List<Seccion> seccionesByLetra,
            Usuario usuario, DateTime today);

}

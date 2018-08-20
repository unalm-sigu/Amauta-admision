package pe.edu.lamolina.pivot.controller.mensajeria.mensajeintranet;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoAlumno;
import pe.edu.lamolina.model.academico.MensajeIntranet;
import pe.edu.lamolina.model.academico.TipoMensajeIntranet;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface MensajeriaIntranetService {

    List<GrupoAlumno> allGruposAlumnos();

    List<TipoMensajeIntranet> allTiposMensajes();

    List<MensajeIntranet> allByDynatble(DynatableFilter filter);

    void saveMensajeria(MensajeIntranet mensajeria, CicloAcademico cicloAcademico, Usuario usuario);

    void updateMensajeria(MensajeIntranet mensajeria, CicloAcademico cicloAcademico, Usuario usuario);

    void eliminar(MensajeIntranet mensajeria);

    MensajeIntranet findMensajeria(Long id);

}

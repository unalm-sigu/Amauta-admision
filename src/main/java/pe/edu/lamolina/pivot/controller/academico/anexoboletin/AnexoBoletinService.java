package pe.edu.lamolina.pivot.controller.academico.anexoboletin;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.AnexoBoletin;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.pivot.model.seguridad.Usuario;

public interface AnexoBoletinService {

    List<AnexoBoletin> allByDynatable(DynatableFilter filter);

    List<AnexoBoletin> allAnexosSuperiores();

    List<DepartamentoAcademico> allDptosByNombre(String nombre);

    List<Carrera> allCarrerasByNombre(String nombre);

    void save(AnexoBoletin anexo, Usuario usuario);

    AnexoBoletin find(Long id);

    void cambiarEstado(AnexoBoletin anexo);
    
    AnexoResumen resumen();

}

package pe.edu.lamolina.pivot.controller.academico.anexoboletin;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface AnexoBoletinService {

    List<AnexoBoletin> allByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    List<AnexoBoletin> allAnexosSuperiores();

    void save(AnexoBoletin anexo, Usuario usuario);

    AnexoBoletin find(Long id);

    void cambiarEstado(AnexoBoletin anexo, String accion);

    AnexoResumen resumen();

    void cambiarOrden(AnexoBoletin anexoBoletin, String direccion);

    List<CicloAcademico> allCiclosByNombre(String nombre);

    CicloAcademico findCiclo(CicloAcademico ciclo);

    List<DepartamentoAcademico> allDepartamentosAcademicos();

    List<Carrera> allCarrerasPosgrado();

}

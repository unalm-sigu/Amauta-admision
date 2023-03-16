package pe.edu.lamolina.amauta.controller.academico.ciclo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Compania;

public interface CicloAcademicoService {

    List<CicloAcademico> allCicloAcademico(Integer maxResultado);

    CicloAcademico getCicloAcademico(Long cicloAcademico);

    void delete(CicloAcademico cicloAcademico);

    void save(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void update(CicloAcademico cicloAcademico, DataSessionPivot ds);

    CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico);

    List<CicloAcademico> allByDynatable(DynatableFilter filter);

    List<ModalidadEstudio> allPrePostgrado(Compania cia);

    void cerrar(CicloAcademico cicloAcademico);

    void anular(CicloAcademico cicloAcademico);

    void activar(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void desactivar(CicloAcademico cicloAcademico);

    void pendiente(CicloAcademico cicloAcademico);

    List<MargenYear> allMargenesByYearModalidad(Integer year, ModalidadEstudio modalidad);

    ModalidadEstudio findModalidadEstudio(ModalidadEstudio modalidadEstudio);

    void configurar(CicloAcademico cicloAcademico);

    void changeVisiblelogin(CicloAcademico cicloAcademico);

    List<Alumno> ejecutarTramiteAcademicos(CicloAcademico cicloAcademico, DataSessionPivot ds);

}

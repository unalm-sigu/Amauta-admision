package pe.edu.lamolina.pivot.controller.academico.ciclo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface CicloAcademicoService {

    List<CicloAcademico> allCicloAcademico(Integer maxResultado);

    CicloAcademico getCicloAcademico(Long cicloAcademico);

    void delete(CicloAcademico cicloAcademico);

    void save(CicloAcademico cicloAcademico, Usuario usuario);

    void update(CicloAcademico cicloAcademico, Usuario usuario);

    CicloAcademico findCicloAcademico(CicloAcademico cicloAcademico);

    List<CicloAcademico> allByDynatable(DynatableFilter filter);

    List<ModalidadEstudio> allPrePostgrado(Compania cia);

    void cerrar(CicloAcademico cicloAcademico);

    void anular(CicloAcademico cicloAcademico);

    void activar(CicloAcademico cicloAcademico);

    void desactivar(CicloAcademico cicloAcademico);

    public void pendiente(CicloAcademico cicloAcademico);

}

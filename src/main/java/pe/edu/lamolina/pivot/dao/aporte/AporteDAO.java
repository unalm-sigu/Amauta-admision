package pe.edu.lamolina.pivot.dao.aporte;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.enums.AportesEnum;

public interface AporteDAO extends EasyDAO<Aporte> {

    List<Aporte> allAporte();

    List<Aporte> allByNombre(List<String> aporteName);

    List<Aporte> allByDynatable(DynatableFilter filter);

    List<Aporte> allByCodigoCicloAcademico(String codigoCicloAcademico);

    List<Aporte> allActivoByModalidadEstudio(ModalidadEstudio modalidadEstudio);

    Aporte findMaximoCodigo();

    Aporte findByNombre(String aporteName);

    Aporte findByCode(AportesEnum codeEnum);

    List<Aporte> allByCodesEnum(List<AportesEnum> codesEnum);

}

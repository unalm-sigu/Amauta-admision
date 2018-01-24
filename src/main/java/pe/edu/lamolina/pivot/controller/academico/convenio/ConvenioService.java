package pe.edu.lamolina.pivot.controller.academico.convenio;

import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CarreraConvenio;
import pe.edu.lamolina.model.academico.ConvenioBeca;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ConvenioService {

    void delete(ConvenioBeca convenioBeca);

    void update(ConvenioBeca convenioBeca, DataSessionPivot ds);

    void save(ConvenioBeca convenioBeca, DataSessionPivot ds);

    ConvenioBeca findConvenioBeca(Long idConvenioBeca);

    List<ConvenioBeca> allByDynatable(DynatableFilter filter);

    void saveInstitucion(Empresa institucion);

    List<Carrera> allCarreraByName(String nombre, Compania cia);

    Map<Long, List<CarreraConvenio>> allByCarreraConvenio(List<ConvenioBeca> convenios);

    List<CarreraConvenio> allCarreraConvenioByConvenioBeca(ConvenioBeca convenioBeca);

}

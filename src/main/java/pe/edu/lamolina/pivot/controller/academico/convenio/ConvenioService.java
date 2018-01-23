package pe.edu.lamolina.pivot.controller.academico.convenio;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.ConvenioBeca;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ConvenioService {

    void delete(ConvenioBeca convenioBeca);

    void update(ConvenioBeca convenioBeca, DataSessionPivot ds);

    void save(ConvenioBeca convenioBeca, DataSessionPivot ds);

    ConvenioBeca findConvenioBeca(Long idConvenioBeca);

    List<ConvenioBeca> allByDynatable(DynatableFilter filter);

    void saveInstitucion(Empresa institucion);

    public List<Carrera> allCarreraByName(String nombre, ModalidadEstudio modalidadEstudio);

}

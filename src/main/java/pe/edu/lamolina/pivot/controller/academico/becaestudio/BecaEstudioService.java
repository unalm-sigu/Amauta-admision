package pe.edu.lamolina.pivot.controller.academico.becaestudio;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.BecaEstudio;
import pe.edu.lamolina.model.general.Empresa;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface BecaEstudioService {

    List<BecaEstudio> allByDynatable(DynatableFilter filter);

    public void save(BecaEstudio becaestudio, DataSessionPivot ds);

    public void update(BecaEstudio becaestudio, DataSessionPivot ds);

    Empresa saveInstitucion(Empresa insticion);

    void delete(BecaEstudio becaestudio, DataSessionPivot ds);

    List<Empresa> allInstituciones();

}

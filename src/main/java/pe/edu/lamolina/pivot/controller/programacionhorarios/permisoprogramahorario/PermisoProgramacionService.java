package pe.edu.lamolina.pivot.controller.programacionhorarios.permisoprogramahorario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.bean.ColaboradorAnexoBean;
import pe.edu.lamolina.model.permisoprogramacion.PermisoProgramacion;
import pe.edu.lamolina.model.permisoprogramacion.PermisosProgramacionHorarios;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface PermisoProgramacionService {

    List<ColaboradorAnexoBean> allPermisos(DynatableFilter filter);

    public void save(ColaboradorAnexoBean colaboradorAnexoBean, DataSessionPivot ds);

    public void update(ColaboradorAnexoBean colaboradorAnexoBean, DataSessionPivot ds);

    public List<PermisoProgramacion> allPermisosPrograma();

    public List<AnexoBoletin> allAnexoBoletin();

    public void savepermiso(ColaboradorAnexoBean colaboradorAnexo, DataSessionPivot ds);

    public void updatepermiso(ColaboradorAnexoBean colaboradorAnexo, DataSessionPivot ds);

}

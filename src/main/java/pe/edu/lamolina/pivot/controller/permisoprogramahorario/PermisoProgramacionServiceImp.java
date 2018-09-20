package pe.edu.lamolina.pivot.controller.permisoprogramahorario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.bean.ColaboradorAnexoBean;
import static pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum.CURSO;
import static pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum.DOCENTE;
import static pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum.GPOSECC;
import static pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum.SECCION;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.FuncionColaborador;
import pe.edu.lamolina.model.permisoprogramacion.ColaboradorAnexo;
import pe.edu.lamolina.model.permisoprogramacion.PermisosProgramacionHorarios;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.general.FuncionColaboradorDAO;
import pe.edu.lamolina.pivot.dao.interceptor.LoggerPermisoProgramacionDAO;
import pe.edu.lamolina.pivot.dao.permisoprogramacion.ColaboradorAnexoDAO;
import pe.edu.lamolina.pivot.dao.permisoprogramacion.PermisoProgramacionDAO;
import pe.edu.lamolina.pivot.dao.permisoprogramacion.PermisoProgramacionHorariosDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PermisoProgramacionServiceImp implements PermisoProgramacionService {

    @Autowired
    PermisoProgramacionHorariosDAO permisoProgramacionHorariosDAO;

    @Autowired
    LoggerPermisoProgramacionDAO loggerPermisoProgramacionDAO;

    @Autowired
    FuncionColaboradorDAO funcionColaboradorDAO;

    @Autowired
    PermisoProgramacionDAO permisoProgramacionDAO;

    @Autowired
    ColaboradorAnexoDAO colaboradorAnexoDAO;

    @Override
    public List<ColaboradorAnexoBean> allPermisos(DynatableFilter filter) {
        List<FuncionColaborador> funcionesCola = funcionColaboradorDAO.allColaboradorEditor(filter);
        ArrayList<Colaborador> colaboradores = new ArrayList();
        for (FuncionColaborador funcionColaborador : funcionesCola) {
            colaboradores.add(funcionColaborador.getColaborador());
        }
        List<PermisosProgramacionHorarios> perColaboradorAnexo = permisoProgramacionHorariosDAO.allPermisos(colaboradores);
        Map<String, List<PermisosProgramacionHorarios>> mapPermisos = TypesUtil.convertListToMapList("key", perColaboradorAnexo);
        Map<Long, List<ColaboradorAnexo>> mapColAnexo = TypesUtil.convertListToMapList("colaboradorAnexo.colaborador.id", "colaboradorAnexo", perColaboradorAnexo);
        List<ColaboradorAnexoBean> anexoBeans = new ArrayList<>();
        for (Colaborador colaborador : colaboradores) {
            List<ColaboradorAnexo> colaboradorAnexo = mapColAnexo.get(colaborador.getId());
            for (ColaboradorAnexo item : colaboradorAnexo) {
                ColaboradorAnexoBean anexoBean = new ColaboradorAnexoBean();
                anexoBean.setColaborador(colaborador);
                anexoBean.setAnexoBoletin(item.getAnexoBoletin());
                anexoBean.setPermisosCurso(mapPermisos.get(item.getId() + CURSO.toString()));
                anexoBean.setPermisosDocente(mapPermisos.get(item.getId() + DOCENTE.toString()));
                anexoBean.setPermisosGpoSec(mapPermisos.get(item.getId() + GPOSECC.toString()));
                anexoBean.setPermisosSecc(mapPermisos.get(item.getId() + SECCION.toString()));
                anexoBeans.add(anexoBean);
            }

        }

        return anexoBeans;
    }

    @Override
    @Transactional
    public void save(Colaborador colaboradorForm, DataSessionPivot ds) {
        Usuario usuario = ds.getUsuario();

//        for (PermisosProgramacionHorarios permisosHorariosForm : colaboradorForm.getPermisosProgramacionHorarioses()) {
//
//            permisosHorariosForm.setEstado(ACT.name());
//            permisosHorariosForm.setFechaRegistro(new Date());
//            permisosHorariosForm.setUserRegistro(usuario);
//            permisoProgramacionHorariosDAO.save(permisosHorariosForm);
//            
//            ColaboradorAnexo colaboradorAnexo = new ColaboradorAnexo();
//            
//            LoggerPermisoProgramacion loggerPermisoProgramacion = new LoggerPermisoProgramacion();
//            loggerPermisoProgramacion.setAnexoBoletin(colaboradorAnexo.getAnexoBoletin());
//            loggerPermisoProgramacion.setColaborador(colaboradorAnexo.getColaborador());
//            loggerPermisoProgramacion.setFechaPermiso(new Date());
//            loggerPermisoProgramacion.setPermisoProgramacion(permisosHorariosForm.getPermisoProgracion());
//            loggerPermisoProgramacion.setPuedeAgregar(permisosHorariosForm.getPuedeAgregar());
//            loggerPermisoProgramacion.setPuedeEliminar(permisosHorariosForm.getPuedeEliminar());
//            loggerPermisoProgramacion.setPuedeModificar(permisosHorariosForm.getPuedeEliminar());
//            loggerPermisoProgramacion.setUserPermiso(usuario);
//            loggerPermisoProgramacionDAO.save(loggerPermisoProgramacion);
//        }
    }

    @Override
    public void update(Colaborador colaboradorForm, DataSessionPivot ds) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

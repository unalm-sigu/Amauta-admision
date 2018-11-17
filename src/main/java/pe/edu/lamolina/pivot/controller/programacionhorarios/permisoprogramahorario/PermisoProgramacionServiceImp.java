package pe.edu.lamolina.pivot.controller.programacionhorarios.permisoprogramahorario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.bean.ColaboradorAnexoBean;
import pe.edu.lamolina.model.enums.EstadoEnum;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import pe.edu.lamolina.model.enums.PermisoProgramacionHorarioEstadoEnum;
import static pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum.CURSO;
import static pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum.DOCENTE;
import static pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum.GPOSECC;
import static pe.edu.lamolina.model.enums.PermisoProgramacionNivelEnum.SECCION;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.FuncionColaborador;
import pe.edu.lamolina.model.permisoprogramacion.ColaboradorAnexo;
import pe.edu.lamolina.model.permisoprogramacion.PermisoProgramacion;
import pe.edu.lamolina.model.permisoprogramacion.PermisosProgramacionHorarios;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
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
    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Override
    public List<ColaboradorAnexoBean> allPermisos(DynatableFilter filter) {
        List<FuncionColaborador> funcionesCola = funcionColaboradorDAO.allColaboradorEditor(filter);
        ArrayList<Colaborador> colaboradores = new ArrayList();
        for (FuncionColaborador funcionColaborador : funcionesCola) {
            colaboradores.add(funcionColaborador.getColaborador());
        }
        List<ColaboradorAnexo> colaboradorAn = colaboradorAnexoDAO.allByColaboradores(colaboradores);
        List<PermisosProgramacionHorarios> perColaboradorAnexo = permisoProgramacionHorariosDAO.allPermisos(colaboradores);
        Map<String, List<PermisosProgramacionHorarios>> mapPermisos = TypesUtil.convertListToMapList("key", perColaboradorAnexo);
        Map<Long, List<ColaboradorAnexo>> mapColAnexo = TypesUtil.convertListToMapList("colaborador.id", colaboradorAn);
        List<ColaboradorAnexoBean> anexoBeans = new ArrayList<>();
        for (Colaborador colaborador : colaboradores) {
            List<ColaboradorAnexo> colaboradorAnexo = mapColAnexo.get(colaborador.getId());
            if (colaboradorAnexo != null) {
                for (ColaboradorAnexo item : colaboradorAnexo) {
                    ColaboradorAnexoBean anexoBean = new ColaboradorAnexoBean();
                    anexoBean.setId(item.getId());
                    anexoBean.setColaborador(colaborador);
                    anexoBean.setAnexoBoletin(item.getAnexoBoletin());
                    List<PermisosProgramacionHorarios> permisos = mapPermisos.get(item.getId() + "-" + CURSO.name());
                    anexoBean.setPermisosCurso(permisos != null ? permisos : new ArrayList<>());
                    permisos = mapPermisos.get(item.getId() + "-" + DOCENTE.name());
                    anexoBean.setPermisosDocente(permisos != null ? permisos : new ArrayList<>());
                    permisos = mapPermisos.get(item.getId() + "-" + GPOSECC.name());
                    anexoBean.setPermisosGpoSec(permisos != null ? permisos : new ArrayList<>());
                    permisos = mapPermisos.get(item.getId() + "-" + SECCION.name());
                    anexoBean.setPermisosSecc(permisos != null ? permisos : new ArrayList<>());
                    anexoBeans.add(anexoBean);
                }
            } else {
                ColaboradorAnexoBean anexoBean = new ColaboradorAnexoBean();
                anexoBean.setColaborador(colaborador);
                anexoBean.setAnexoBoletin(new AnexoBoletin());
                anexoBean.setPermisosCurso(new ArrayList<>());
                anexoBean.setPermisosDocente(new ArrayList<>());
                anexoBean.setPermisosGpoSec(new ArrayList<>());
                anexoBean.setPermisosSecc(new ArrayList<>());
                anexoBeans.add(anexoBean);
            }
        }

        return anexoBeans;
    }

    @Override
    @Transactional
    public void save(ColaboradorAnexoBean colaboradorAnexoForm, DataSessionPivot ds) {
        Usuario usuario = ds.getUsuario();
        ColaboradorAnexo colaboradorAnexo = null;
        if (colaboradorAnexoForm.getId() != null) {
            colaboradorAnexo = colaboradorAnexoDAO.find(colaboradorAnexoForm.getId());
        }
        if (colaboradorAnexo == null) {
            colaboradorAnexo = new ColaboradorAnexo();
            colaboradorAnexo.setAnexoBoletin(colaboradorAnexoForm.getAnexoBoletin());
            colaboradorAnexo.setColaborador(colaboradorAnexoForm.getColaborador());
            colaboradorAnexo.setEstado(EstadoEnum.ACT.name());
            colaboradorAnexo.setFechaRegistro(new Date());
            colaboradorAnexo.setUserRegistro(ds.getUsuario());
            colaboradorAnexoDAO.save(colaboradorAnexo);
        }
        for (PermisosProgramacionHorarios permisosHorariosForm : colaboradorAnexoForm.getPermisosProgramacionHorarios()) {
            PermisosProgramacionHorarios programacionHo = permisoProgramacionHorariosDAO.findByColaborador(colaboradorAnexo.getId(), permisosHorariosForm.getPermisoProgramacion());
            PermisosProgramacionHorarios programacionHorarios = programacionHo == null ? new PermisosProgramacionHorarios() : programacionHo;
            programacionHorarios.setColaboradorAnexo(colaboradorAnexo);
            programacionHorarios.setEstado(ACT.name());
            programacionHorarios.setFechaRegistro(new Date());
            programacionHorarios.setUserRegistro(usuario);
            programacionHorarios.setPermisoProgramacion(permisosHorariosForm.getPermisoProgramacion());
            programacionHorarios.setPuedeAgregar(permisosHorariosForm.getPuedeAgregar());
            programacionHorarios.setPuedeEliminar(permisosHorariosForm.getPuedeEliminar());
            programacionHorarios.setPuedeModificar(permisosHorariosForm.getPuedeModificar());

            permisoProgramacionHorariosDAO.save(programacionHorarios);

//            LoggerPermisoProgramacion loggerPermisoProgramacion = new LoggerPermisoProgramacion();
//            loggerPermisoProgramacion.setAnexoBoletin(colaboradorAnexoForm.getAnexoBoletin());
//            loggerPermisoProgramacion.setColaborador(colaboradorAnexoForm.getColaborador());
//            loggerPermisoProgramacion.setFechaPermiso(new Date());
//            loggerPermisoProgramacion.setPermisoProgramacion(permisosHorariosForm.getPermisoProgramacion());
//            loggerPermisoProgramacion.setPuedeAgregar(permisosHorariosForm.getPuedeAgregar());
//            loggerPermisoProgramacion.setPuedeEliminar(permisosHorariosForm.getPuedeEliminar());
//            loggerPermisoProgramacion.setPuedeModificar(permisosHorariosForm.getPuedeEliminar());
//            loggerPermisoProgramacion.setUserPermiso(usuario);
//            loggerPermisoProgramacionDAO.save(loggerPermisoProgramacion);
        }

    }

    @Override
    @Transactional
    public void update(ColaboradorAnexoBean anexoBean, DataSessionPivot ds) {
        PermisosProgramacionHorarios permisosPrograma = permisoProgramacionHorariosDAO.find(anexoBean.getIdPermiso());
        permisosPrograma.setEstadoEnum(PermisoProgramacionHorarioEstadoEnum.INA);
        permisoProgramacionHorariosDAO.update(permisosPrograma);
    }

    @Override
    public List<PermisoProgramacion> allPermisosPrograma() {
        return permisoProgramacionDAO.allPermisos();
    }

    @Override
    public List<AnexoBoletin> allAnexoBoletin() {
        return anexoBoletinDAO.all();
    }

    @Override
    @Transactional
    public void savepermiso(ColaboradorAnexoBean colaboradorAnexoForm, DataSessionPivot ds) {
        Usuario usuario = ds.getUsuario();

        ColaboradorAnexo colaboradorAnexo = new ColaboradorAnexo();
        colaboradorAnexo.setAnexoBoletin(colaboradorAnexoForm.getAnexoBoletin());
        colaboradorAnexo.setColaborador(colaboradorAnexoForm.getColaborador());
        colaboradorAnexo.setEstado(EstadoEnum.ACT.name());
        colaboradorAnexo.setFechaRegistro(new Date());
        colaboradorAnexo.setUserRegistro(ds.getUsuario());
        colaboradorAnexoDAO.save(colaboradorAnexo);

        for (PermisosProgramacionHorarios permisosHorariosForm : colaboradorAnexoForm.getPermisosProgramacionHorarios()) {
            PermisosProgramacionHorarios programacionHo = permisoProgramacionHorariosDAO.findByColaborador(colaboradorAnexo.getId(), permisosHorariosForm.getPermisoProgramacion());
            PermisosProgramacionHorarios programacionHorarios = programacionHo == null ? new PermisosProgramacionHorarios() : programacionHo;
            programacionHorarios.setColaboradorAnexo(colaboradorAnexo);
            programacionHorarios.setEstado(ACT.name());
            programacionHorarios.setFechaRegistro(new Date());
            programacionHorarios.setUserRegistro(usuario);
            programacionHorarios.setPermisoProgramacion(permisosHorariosForm.getPermisoProgramacion());
            programacionHorarios.setPuedeAgregar(0);
            programacionHorarios.setPuedeEliminar(0);
            programacionHorarios.setPuedeModificar(0);
            if (permisosHorariosForm.getPuedeAgregar() != null) {
                programacionHorarios.setPuedeAgregar(1);
            }
            if (permisosHorariosForm.getPuedeEliminar() != null) {
                programacionHorarios.setPuedeEliminar(1);
            }
            if (permisosHorariosForm.getPuedeModificar() != null) {
                programacionHorarios.setPuedeModificar(1);
            }
            permisoProgramacionHorariosDAO.save(programacionHorarios);

//            LoggerPermisoProgramacion loggerPermisoProgramacion = new LoggerPermisoProgramacion();
//            loggerPermisoProgramacion.setAnexoBoletin(colaboradorAnexoForm.getAnexoBoletin());
//            loggerPermisoProgramacion.setColaborador(colaboradorAnexoForm.getColaborador());
//            loggerPermisoProgramacion.setFechaPermiso(new Date());
//            loggerPermisoProgramacion.setPermisoProgramacion(permisosHorariosForm.getPermisoProgramacion());
//            loggerPermisoProgramacion.setPuedeAgregar(permisosHorariosForm.getPuedeAgregar());
//            loggerPermisoProgramacion.setPuedeEliminar(permisosHorariosForm.getPuedeEliminar());
//            loggerPermisoProgramacion.setPuedeModificar(permisosHorariosForm.getPuedeEliminar());
//            loggerPermisoProgramacion.setUserPermiso(usuario);
//            loggerPermisoProgramacionDAO.save(loggerPermisoProgramacion);
        }

    }

    @Override
    @Transactional
    public void updatepermiso(ColaboradorAnexoBean colaboradorAnexoForm, DataSessionPivot ds) {
        Usuario usuario = ds.getUsuario();
        PermisosProgramacionHorarios permisosPrograma = permisoProgramacionHorariosDAO.find(colaboradorAnexoForm.getIdPermiso());
        ColaboradorAnexo colaboradorAnexo = colaboradorAnexoDAO.findColaborador(colaboradorAnexoForm.getColaborador(),colaboradorAnexoForm.getAnexoBoletin());
        for (PermisosProgramacionHorarios permisosProgramacionForm : colaboradorAnexoForm.getPermisosProgramacionHorarios()) {
            permisosPrograma.setColaboradorAnexo(colaboradorAnexo);
            permisosPrograma.setPuedeAgregar(permisosProgramacionForm.getPuedeAgregar());
            permisosPrograma.setPuedeEliminar(permisosProgramacionForm.getPuedeEliminar());
            permisosPrograma.setPuedeModificar(permisosProgramacionForm.getPuedeModificar());
            permisoProgramacionHorariosDAO.update(permisosPrograma);
        }
    }
}

package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.precioseccion;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.SituacionDocenteEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.TipoCarpetaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class PrecioSeccionServiceImp implements PrecioSeccionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    TipoCarpetaDAO tipoCarpetaDAO;

    @Autowired
    CursoCicloAcademicoDAO cursoCicloAcademicoDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Override
    public void savePrecioSeccion(Seccion seccionForm, DataSessionPivot ds) {

        Seccion seccionBD = seccionDAO.find(seccionForm);

        Curso curso = seccionBD.getGrupoSeccion().getCurso();

        CicloAcademico ciclo = seccionBD.getGrupoSeccion().getCicloAcademico();

        Assert.isTrue(ciclo.getTipoEnum() == TipoCicloEnum.NIV, "Sólo se aplica en ciclos de nivelación");

        CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(curso, ciclo);

        BigDecimal total = cursoCiclo.getPrecio().add(cursoCiclo.getPrecioAdicional());

        if (total.compareTo(seccionForm.getPrecio()) == 0) {
            seccionForm.setPrecioPersonalizado(Boolean.FALSE);
            seccionForm.setUserPrecio(null);
            seccionForm.setFechaPrecio(null);
        } else {
            if (seccionBD.getPrecio().compareTo(seccionForm.getPrecio()) != 0) {
                seccionForm.setPrecioPersonalizado(Boolean.TRUE);
                seccionForm.setUserPrecio(ds.getUsuario());
                seccionForm.setFechaPrecio(new Date());
            } else {
                return;
            }
        }
        seccionDAO.updatePrecioBySeccion(seccionForm);
    }

    @Override
    @Transactional
    public void asignarHorasAdicionales(Seccion seccionForm, DataSessionPivot ds) {
        Seccion seccion = seccionDAO.find(seccionForm);
        seccion.setHorasAdicionales(seccionForm.getHorasAdicionales());
        seccionDAO.update(seccion);
    }

    @Override
    public List<TipoCarpeta> allTipoCarpetaByNombre(String nombre) {
        return tipoCarpetaDAO.allByNombre(nombre);
    }

    @Override
    @Transactional
    public void saveTipoCarpetaSeccion(Seccion seccionForm, DataSessionPivot ds) {
        Seccion seccion = seccionDAO.find(seccionForm);
        ObjectUtil.eliminarAttrSinId(seccionForm, "tipoCarpeta");
        seccion.setTipoCarpeta(seccionForm.getTipoCarpeta());
        seccionDAO.update(seccion);
    }

    @Override
    @Transactional
    public TipoCarpeta findTipoCarpetaSeccion(Seccion seccionForm) {

        Seccion seccion = seccionDAO.find(seccionForm);

        TipoCarpeta tipoCarpeta = seccion.getTipoCarpeta();

        if (tipoCarpeta != null) {
            return tipoCarpeta;
        }

        CicloAcademico cicloAcademico = seccion.getGrupoSeccion().getCicloAcademico();
        logger.debug("cicloAcademico {}", cicloAcademico != null ? cicloAcademico.getId() : 0);

        Curso curso = seccion.getGrupoSeccion().getCurso();
        logger.debug("curso {}", curso != null ? curso.getId() : 0);

        TipoSeccionEnum tipoSeccionEnum = seccion.getTipoSeccionEnum();
        logger.debug("tipoSeccionEnum {}", tipoSeccionEnum != null ? tipoSeccionEnum.name() : "");

        CursoCicloAcademico cursoCicloAcademico = cursoCicloAcademicoDAO.findByCursoCiclo(curso, cicloAcademico);
        logger.debug("cursoCicloAcademico {}", cursoCicloAcademico != null ? cursoCicloAcademico.getId() : 0);

        if (tipoSeccionEnum == TipoSeccionEnum.PCUR || tipoSeccionEnum == TipoSeccionEnum.PRA) {

            tipoCarpeta = cursoCicloAcademico != null ? cursoCicloAcademico.getTipoCarpetaPractica() : null;
            tipoCarpeta = tipoCarpeta != null ? tipoCarpeta : curso.getTipoCarpetaPractica();

        } else if (tipoSeccionEnum == TipoSeccionEnum.TCUR || tipoSeccionEnum == TipoSeccionEnum.TEO) {

            tipoCarpeta = cursoCicloAcademico != null ? cursoCicloAcademico.getTipoCarpetaTeoria() : null;
            tipoCarpeta = tipoCarpeta != null ? tipoCarpeta : curso.getTipoCarpetaTeoria();

        }

        if (tipoCarpeta != null && seccion.getTipoCarpeta() == null) {
            seccion.setTipoCarpeta(tipoCarpeta);
            seccionDAO.update(seccion);
        }

        return tipoCarpeta;
    }

    @Override
    @Transactional
    public void asignarGrupoSeccionModular(GrupoSeccion grupoSeccionForm, DataSessionPivot ds) {

        GrupoSeccion grupoSeccion = grupoSeccionDAO.find(grupoSeccionForm.getId());

        if (!grupoSeccionForm.isTipoDictadoCheck()) {

            grupoSeccion.setFechaFinModular(null);
            grupoSeccion.setFechaInicioModular(null);
            grupoSeccion.setTipoDictado(TipoDictadoGrupoSeccionEnum.SEM.name());
            grupoSeccionDAO.update(grupoSeccion);
            this.regenerarFechas(grupoSeccion);
            return;

        }

        if (grupoSeccionForm.getFechaFinModular() == null || grupoSeccionForm.getFechaInicioModular() == null) {
            throw new PhobosException("Las fechas no son validas");
        }

        if (grupoSeccionForm.getFechaInicioModular().after(grupoSeccionForm.getFechaFinModular())) {
            throw new PhobosException("Las fechas no son validas");
        }

        grupoSeccion.setFechaFinModular(grupoSeccionForm.getFechaFinModular());
        grupoSeccion.setFechaInicioModular(grupoSeccionForm.getFechaInicioModular());
        grupoSeccion.setTipoDictado(TipoDictadoGrupoSeccionEnum.MOD.name());

        grupoSeccionDAO.update(grupoSeccion);
        this.reordenarFechas(grupoSeccion);

    }

    private void regenerarFechas(GrupoSeccion grupoSeccion) {

    }

    private void reordenarFechas(GrupoSeccion grupoSeccion) {

        List<HorarioSeccion> horariosSeccions = horarioSeccionDAO.allByGrupoSeccion(grupoSeccion);

        for (HorarioSeccion horariosSeccion : horariosSeccions) {
            horariosSeccion.setFechaInicio(grupoSeccion.getFechaInicioModular());
            horariosSeccion.setFechaFin(grupoSeccion.getFechaFinModular());
            horarioSeccionDAO.update(horariosSeccion);
        }

        List<DocenteSeccion> docenteSecciones = docenteSeccionDAO.allByGrupoSeccion(grupoSeccion);

        for (DocenteSeccion docenteSeccione : docenteSecciones) {

            docenteSeccione.setFechaInicio(grupoSeccion.getFechaInicioModular());
            docenteSeccione.setFechaFin(grupoSeccion.getFechaFinModular());
            docenteSeccionDAO.update(docenteSeccione);

            Seccion seccion = docenteSeccione.getSeccion();
            seccion.setSituacionDocente(SituacionDocenteEnum.ERR.name());
            seccionDAO.update(seccion);

        }

        List<Seccion> secciones = seccionDAO.allActivosByGpoSeccion(grupoSeccion);
        
        Map<Long, Aula> aulasMAp = TypesUtil.convertListToMap("aula.id", "aula", secciones);
        
        List<Aula> aulas = aulasMAp.values().stream().collect(Collectors.toList());
        
        List<HorarioAula> horarioAulas = horarioAulaDAO.allByAulas(aulas);
        
        List<HorarioAula> horarioAulasCruce = horarioAulaDAO.allByFechas(grupoSeccion.getFechaInicioModular(),grupoSeccion.getFechaFinModular());

        for (HorarioAula horarioAula : horarioAulas) {
            
            if(horarioAula.getAula().getPermiteCruce()==1){
                
                horarioAula.setFechaFin(grupoSeccion.getFechaFinModular());
                horarioAula.setFechaInicio(grupoSeccion.getFechaInicioModular());
                horarioAulaDAO.update(horarioAula);
                
            }else{
                
                
            
            }
        }

    }

}

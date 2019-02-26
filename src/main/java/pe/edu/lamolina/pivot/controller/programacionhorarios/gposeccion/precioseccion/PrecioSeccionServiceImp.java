package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.precioseccion;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.pivot.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.TipoCarpetaDAO;
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

}

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
    public TipoCarpeta findTipoCarpetaSeccion(Seccion seccionForm) {
        TipoCarpeta tipo = tipoCarpetaDAO.findTipoCarpetaBySeccion(seccionForm);
        return tipo;
    }

}

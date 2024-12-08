package pe.edu.lamolina.amauta.controller.nivelacioneegg.asistencianivelacion;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.AsistenciaNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.TemaAsistenciaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import static pe.edu.lamolina.model.enums.dictadoclases.AsistenciaClasesEstadoEnum.ASISTIO;
import pe.edu.lamolina.model.nivelacioneegg.AsistenciaNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.TemaAsistencia;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class AsistenciaNivelacionServiceImpl implements AsistenciaNivelacionService {

    private final AsistenciaNivelacionDAO asistenciaNivelacionDAO;
    private final TemaAsistenciaDAO temaAsistenciaDAO;

    @Override
    public TemaAsistencia findLeccion(TemaAsistencia form, Docente docenteForm, CicloAcademico cicloForm) {
        TemaAsistencia leccion = temaAsistenciaDAO.find(form.getId());
        Assert.isNotNull(leccion, "No existe la lección solicitada");

        CursoNivelacion seccion = leccion.getCursoNivelacion();
        Assert.isNotNull(seccion, "No existe la sección solicitada");

        Docente docente = seccion.getDocente();
        Assert.isTrue(docente.getId().equals(docenteForm.getId()), "Esta sección no corresponde al docente");

        CicloAcademico ciclo = seccion.getCursoCiclo().getCicloAcademico();
        Assert.isTrue(ciclo.getId().equals(cicloForm.getId()), "Esta sección no corresponde al ciclo actual");

        return leccion;
    }

    @Override
    public List<AsistenciaNivelacion> allInscritos(DynatableFilter filter, TemaAsistencia leccion) {
        return asistenciaNivelacionDAO.allLeccionByDynatable(filter, leccion);
    }

    @Override
    @Transactional
    public void marcarAsistencia(AsistenciaNivelacion form, Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {
        Assert.isNotNull(form.getEstado(), "No ha indicado el estado de la asistencia");
        AsistenciaNivelacion asistencia = asistenciaNivelacionDAO.find(form.getId());
        Assert.isNotNull(asistencia, "No existe el registro que desea modificar");
        Assert.isFalse(form.getEstadoEnum() == asistencia.getEstadoEnum(), "No ha modificado el estado de la asistencia");

        TemaAsistencia leccion = this.findLeccion(asistencia.getTemaAsistencia(), docente, ciclo);
        if (form.getEstadoEnum() == ASISTIO) {
            leccion.setAsistentes(leccion.getAsistentes() + 1);
            leccion.setFaltantes(leccion.getFaltantes() - 1);
        } else {
            leccion.setAsistentes(leccion.getAsistentes() - 1);
            leccion.setFaltantes(leccion.getFaltantes() + 1);
        }
        temaAsistenciaDAO.update(leccion);

        asistencia.setEstadoEnum(form.getEstadoEnum());
        asistencia.setUserRegistro(ds.getUsuario());
        asistencia.setFechaRegistro(new Date());
        asistenciaNivelacionDAO.update(asistencia);
    }

}

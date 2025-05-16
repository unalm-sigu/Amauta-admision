package pe.edu.lamolina.amauta.controller.programacionhorarios.resumen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.*;
import pe.edu.lamolina.model.academico.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ResumenProgramacionServiceImp implements ResumenProgramacionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Override
    public DepartamentoAcademico findDepartamento(Long idDepartamentoAcad) {
        return departamentoAcademicoDAO.find(idDepartamentoAcad);
    }

    @Override
    public AnexoBoletin findAnexoBoletin(Long idAnexoBoletin) {
        return anexoBoletinDAO.find(idAnexoBoletin);
    }

    @Override
    public List<DepartamentoAcademico> allDepartamentosAcademicos() {
        return departamentoAcademicoDAO.all();
    }

    @Override
    public List<DepartamentoCursosProgramadosDTO> countGroupsByFilter(List<Long> departamentos, CicloAcademico cicloAcademico, AnexoBoletin departamentoAcademico) {
        return anexoBoletinDAO.allCursosProgramadosByAnexo(departamentos, cicloAcademico, departamentoAcademico);
    }

    @Override
    public List<AnexoBoletin> allActiveAnexos(DynatableFilter filter, CicloAcademico cicloAcademico) {
        return anexoBoletinDAO.allHijosByDynatable(filter,cicloAcademico);
    }

    @Override
    public List<DepartamentoCursosProgramadosDTO> obtenerEstadisticas() {
        List<DepartamentoCursosProgramadosDTO> lista = new ArrayList<>();
        lista.add(new DepartamentoCursosProgramadosDTO(12L,15L,88L,88L,12L,15L,88L,88L,12L,15L,88L,88L));
        return lista;
    }

    @Override
    public List<GrupoSeccion> allGrupoSeccionByFilterDyna(CicloAcademico cicloAcademico, AnexoBoletin anexoBoletin, DynatableFilter dynatableFilter) {
        List<GrupoSeccion> gpoSecciones = grupoSeccionDAO.allByDynatableCicloAnexo(cicloAcademico, anexoBoletin, dynatableFilter);
        List<Seccion> secciones = seccionDAO.allActivosByGposSeccion(gpoSecciones);
        List<DocenteSeccion> profeSecciones = docenteSeccionDAO.allActivosBySecciones(secciones);

        Map<Long, List<Seccion>> mapSecciones = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);
        Map<Long, List<DocenteSeccion>> mapDocentes = TypesUtil.convertListToMapList("seccion.id", profeSecciones);

        for (GrupoSeccion gpoSecc : gpoSecciones) {
            List<Seccion> seccionesGpo = TypesUtil.getListNotNull(mapSecciones.get(gpoSecc.getId()));
            gpoSecc.setSecciones(seccionesGpo);
            for (Seccion seccion : seccionesGpo) {
                List<DocenteSeccion> profesBySeccion = TypesUtil.getListNotNull(mapDocentes.get(seccion.getId()));
                List<DocenteSeccion> profesSeccFinal = new ArrayList();
                for (DocenteSeccion profeSecc : profesBySeccion) {
                    if (profeSecc.getDocente().getPersona() != null) {
                        profesSeccFinal.add(profeSecc);
                        profeSecc.setSeccion(seccion);
                    }
                }
                seccion.setDocenteSeccion(profesSeccFinal);
                seccion.setGrupoSeccion(gpoSecc);
            }
        }

        return gpoSecciones;
    }
}

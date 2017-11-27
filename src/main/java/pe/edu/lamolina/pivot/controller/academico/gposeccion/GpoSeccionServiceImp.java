package pe.edu.lamolina.pivot.controller.academico.gposeccion;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.pivot.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.model.academico.AnexoBoletin;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;

@Service
@Transactional(readOnly = true)
public class GpoSeccionServiceImp implements GpoSeccionService {

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    AnexoBoletinDAO anexoBoletinDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Override
    public List<GrupoSeccion> allByDynatable(DynatableFilter filter, CicloAcademico cicloAcademico) {
        List<GrupoSeccion> gsecciones = grupoSeccionDAO.allByDynatable(filter, cicloAcademico);
        List<Seccion> secciones = seccionDAO.allActivosByGposSeccion(gsecciones);

        Map<Long, List<Seccion>> mapSecciones = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);
        for (GrupoSeccion gseccion : gsecciones) {
            gseccion.setSecciones(mapSecciones.get(gseccion.getId()));
        }

        List<DocenteSeccion> docenteSeccion = docenteSeccionDAO.allActivosBySecciones(secciones);

        Map<Long, List<DocenteSeccion>> mapDocSeccion = TypesUtil.convertListToMapList("seccion.id", docenteSeccion);

        for (Seccion seccion : secciones) {
            seccion.setDocenteSeccion(mapDocSeccion.get(seccion.getId()));
        }

        return gsecciones;
    }

    @Override
    public void saveGpoSeccionHeader(GrupoSeccion grupoSeccion) {
        GrupoSeccion lastGrupoSeccion = grupoSeccionDAO.findLast();
        String codigo = generateCodigo(lastGrupoSeccion.getCodigo());
    }

    public static String generateCodigo(String codigo) {
        if (StringUtils.isBlank(codigo)) {
            return "001";
        }
        String letterPart = codigo.substring(0, 1);
        Integer numericPart = Integer.parseInt(codigo.substring(1, 3));

        if (numericPart == 99) {
            if (StringUtils.isNumeric(letterPart)) {
                Integer letterPartInt = Integer.parseInt(letterPart);
                if (letterPartInt < 9) {
                    letterPartInt++;
                    letterPart = letterPartInt + "";
                } else {
                    letterPart = "A";
                }
            } else {
                int charValue = letterPart.charAt(0);
                letterPart = String.valueOf((char) (charValue + 1));
            }
            numericPart = 0;
        }
        numericPart++;
        return letterPart + String.format("%02d", numericPart);
    }

    @Override
    public List<AnexoBoletin> allAnexosSuperiores() {
        return anexoBoletinDAO.allAnexosSuperiores();
    }

    @Override
    public GpoSeccionResumen resumen() {
        return grupoSeccionDAO.resumen();
    }

    @Override
    public List<Curso> allCursosForProgramacion(String nomString) {
        return cursoDAO.allForProgramacion(nomString);
    }

    @Override
    public List<AnexoBoletin> allAnexoBoletionHijos() {
        return anexoBoletinDAO.allAnexosHijos();
    }

    @Override
    public AnexoBoletin findAnexoBoletin(Long idAnexoBoletin) {
        return anexoBoletinDAO.find(idAnexoBoletin);
    }

    @Override
    public Curso findCurso(Long id) {
        return cursoDAO.find(id);
    }

}

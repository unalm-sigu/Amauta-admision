package pe.edu.lamolina.pivot.controller.academico.infoacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.TipoCursoCurriculaEnum;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;

@Service
@Transactional(readOnly = true)
public class infoAcademicoServiceImpl implements infoAcademicoService {

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Override
    public ObjectNode allAlumnosByCiclo(Alumno alumno, Long numeroCiclo) {
        ArrayNode lstCiclos = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode lstCursos = new ArrayNode(JsonNodeFactory.instance);
        ObjectNode objNodeCursos = new ObjectNode(JsonNodeFactory.instance);
        List<AlumnoCursoCurricula> lst = alumnoCursoCurriculaDAO.allByAlumno(alumno, numeroCiclo);
        if (numeroCiclo == 1l) {
            List<AlumnoCursoCurricula> ciclosAlumno = alumnoCursoCurriculaDAO.allCiclosAlumno(alumno);

            Map<Integer, Long> counters = ciclosAlumno.stream()
                    .collect(Collectors.groupingBy(c -> c.getNumeroCiclo(),
                            Collectors.counting()));

            for (Map.Entry<Integer, Long> entry : counters.entrySet()) {
                ObjectNode objCiclo = new ObjectNode(JsonNodeFactory.instance);
                objCiclo.put("numeroRoman", NumberFormat.roman(entry.getKey()));
                objCiclo.put("cantidad", "(" + entry.getValue() + ")");
                objCiclo.put("numero", entry.getKey());
                lstCiclos.add(objCiclo);
            }

            objNodeCursos.put("ciclos", lstCiclos);
        }

        for (AlumnoCursoCurricula alumnoCursoCurricula : lst) {
            ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);
            objNode.put("numeroCiclo", alumnoCursoCurricula.getNumeroCiclo());
            objNode.put("estado", CursoCurriculaEstadoEnum.getNombreAndName(alumnoCursoCurricula.getEstado()));
            objNode.put("codigo", alumnoCursoCurricula.getCurso().getCodigo());
            objNode.put("codigoAnterior", alumnoCursoCurricula.getCurso().getCodigoAnterior1());
            objNode.put("tipoCurso", alumnoCursoCurricula.getCurso() == null ? "" : TipoCursoCurriculaEnum.getNombre(alumnoCursoCurricula.getCursoCurricula().getTipoCursoCurricula().getNombre()));
            objNode.put("vecesCursado", alumnoCursoCurricula.getVecesCursado());
            objNode.put("nombre", alumnoCursoCurricula.getCurso().getNombre());
            objNode.put("nota", alumnoCursoCurricula.getNota());
            objNode.put("creditos", alumnoCursoCurricula.getCreditos());
            objNode.put("descripcion", alumnoCursoCurricula.getCicloAprobado() == null ? "" : alumnoCursoCurricula.getCicloAprobado().getDescripcion());
            lstCursos.add(objNode);
        }
        objNodeCursos.put("cursos", lstCursos);

        return objNodeCursos;
    }

}

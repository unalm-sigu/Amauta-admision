package pe.edu.lamolina.amauta.controller.academico.silabo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.SilaboCurso;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.general.Compania;

public interface SilaboService {

    List<SilaboCurso> allSilabo(DynatableFilter filter);

    void save(SilaboCurso silabo);

    void delete(SilaboCurso silabo);

    String revision(SilaboCurso silabo);

    public List<Curso> allCursoByModalidadEstudioNombre(String nombre, ModalidadEstudioEnum modalidadEstudioEnum);

    public List<DepartamentoAcademico> allDepartamentoMod(String nombre, Compania compania);

}

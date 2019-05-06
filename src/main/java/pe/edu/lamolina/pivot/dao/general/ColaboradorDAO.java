package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.pivot.controller.general.oficina.colaborador.ResumenColaborador;

public interface ColaboradorDAO extends EasyDAO<Colaborador> {

    List<Colaborador> allByOficinas(List<Oficina> oficinas);

    List<Colaborador> allByOficina(Oficina oficina);

    List<Colaborador> allActivosByPersona(Persona persona);

    List<Colaborador> allActivosByOficina(Oficina oficinaBD);

    ResumenColaborador countByOficinas(List<Oficina> oficina);

    List<Colaborador> allDynatableByOficina(DynatableFilter filter, List<Oficina> oficinas);

    Colaborador findMaxCodigo();

    Colaborador find(Colaborador colaborador);

    Colaborador findActivoByPersonaOficina(Oficina oficina, Persona persona);

    List<Colaborador> allByName(String nombre);

    Colaborador findByPersonaAndEstado(Persona persona);

    Colaborador findDocenteActivoByPersonaDptoAcademico(Persona persona, DepartamentoAcademico departamento);

}

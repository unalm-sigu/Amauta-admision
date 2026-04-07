<template>
  <div>
    <header class="header b-b padder-lg">

      <div class="pull-right m-t-sm">
        <div class="dropdown">
          <a v-bind:href="origen" class="btn btn-default">Regresar</a>
          <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">
            Acciones <span class="caret"></span>
          </button>

          <ul class="dropdown-menu dropdown-menu-right">
            <li><a v-on:click.prevent="addAlumnoBorrado" class="dropdown-item pointer">Nuevo Registro</a></li>
          </ul>
        </div>
      </div>

      <h2 class="m-b-xs"> Alumnos borrados historial</h2>
    </header>

    <section class="wrapper-lg">
      <section class="panel m-b-md">
        <section class="panel-body">

          <raptor-table v-bind:url="raptorURL"
                        ref="raptorAlumnosBorrados">
            <template scope="props">
              <table class="table table-striped">
                <thead class="panel panel-heading">
                <tr>
                  <th class="v-middle text-center" colspan="2">Alumno</th>
                  <th class="v-middle "></th>
                  <th class="v-middle ">Ciclo Afectado</th>
                  <th class="v-middle text-center wd-15">Motivo</th>
                  <th class="v-middle text-center wd-15">Usuario Registro</th>
                  <th class="wd-1">Detalle</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for=" item  in props.data">
                  <td class="v-middle">
                    <template>
                      <foto-persona
                          v-bind:persona="item.alumno.persona"
                          v-bind:modalidad="item.alumno.modalidadEstudio">
                      </foto-persona>
                    </template>
                  </td>

                  <td class="v-middle">
                    <template>
                      <info-alumno
                          v-bind:alumno="item.alumno"
                          v-bind:persona="item.alumno.persona">
                      </info-alumno>
                    </template>
                  </td>

                  <td class="v-middle">
                  </td>
                  <td class="v-middle">
                  {{ item.cicloAfectado.descripcion}}
                  </td>
                  <td class="v-middle">
                    {{item.motivo}}
                  </td>

                  <td class="v-middle text-center bold">
                      {{ item.userRegistra.persona.apellidosNombres }}
                  </td>

                  <td class="v-middle text-center">
                    <a v-on:click.prevent="histoEliminada(item.alumno.id, item.cicloAfectado.id)" class="pointer"><i class="fa fa-eye fa-2x text-danger" aria-hidden="true"></i></a>
                  </td>
                </tr>
                </tbody>
              </table>
            </template>
          </raptor-table>

        </section>
      </section>

    </section>

    <modal-add-alumno-borrado ref="modalAddAlumnoBorrado"></modal-add-alumno-borrado>
    <modal-historial-eliminada ref="modalHistorialBorrada"></modal-historial-eliminada>


  </div>
</template>

<script>
Vue.component("multiselect", window.VueMultiselect.default);

const FotoPersona = httpVueLoader('/app/_componentes/FotoPersonaAlumno.vue');
const InfoAlumno = httpVueLoader('/app/academico/registroborrado/AlumnoComponent.vue');
const ModalAddAlumnoBorrado = httpVueLoader('./ModalAddAlumnoBorrado.vue');
const ModalHistorialEliminada = httpVueLoader('./ModalHistorialEliminada.vue');

module.exports = {
  components :{
    FotoPersona, InfoAlumno, ModalAddAlumnoBorrado,ModalHistorialEliminada
  },
  data() {
    return {
      pagination: {'total-items': 0, 'items-per-page': 1000, 'max-size': 3, 'boundary-link-numbers': true},
      raptorURL: `/${rutaModulo}/list`
    };
  },
  mounted() {},
  computed: {},
  methods: {
    addAlumnoBorrado() {
      this.$refs.modalAddAlumnoBorrado.open(this.$refs.raptorAlumnosBorrados);
    },
    histoEliminada(idAlumno,idCicloEstudiado) {
      let infoAlumno = {idAlumno:idAlumno, idCicloEstudiado:idCicloEstudiado};
      this.$refs.modalHistorialBorrada.open(infoAlumno);
    },


  }

}


</script>

<style>

</style>
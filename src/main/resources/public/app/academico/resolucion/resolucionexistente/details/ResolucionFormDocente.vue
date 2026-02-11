<template>
  <div>

    <h4 class="text-primary m-b-lg"> Trámites {{resolucion.tipoResolucion.nombre}}</h4>

    <resolucion-form-filter v-bind:callfilter="applyFilter"  v-if="!isEdicion"></resolucion-form-filter>

    <table class="table table-striped">
      <thead>
      <tr>
        <th class="col-sm-9 text-center" >Persona</th>
        <th class="col-sm-1 text-center" >Seleccionado</th>
<!--        <th class="col-sm-1 text-center"></th>-->
      </tr>
      </thead>
      <tbody>

      <tr v-for="(docenteRes , index) in resolucion.docenteResolucion">
        <td class="v-middle text-center">
          <div class="form-group">
            <div class="col-md-12">
              <multiselect v-model="docenteRes.docente"
                           v-bind:options='docentes'
                           v-on:search-change="searchAlumno"
                           track-by='id'
                           v-bind:show-labels="false"
                           v-bind:allow-empty="false"
                           deselect-label="No se puede eliminar este valor"
                           v-bind:internal-search='false'
                           placeholder=" "
                           v-bind:disabled="isEdicion &amp;&amp; sancionDsc.id != null"
              >
                <template slot="singleLabel" slot-scope="props">
                  <span class="">{{props.option.codigo}} - {{ props.option.persona.apellidosNombres }}</span>
                </template>
                <template slot="option" slot-scope="props">
                  <div class="option__desc">
                    <span class="option__title block bold">{{ props.option.codigo }} - {{ props.option.persona.nombreCompleto }} </span>
                    <span class="option__small">{{ props.option.persona.tipoDocumento.simbolo }} - {{ props.option.persona.numeroDocIdentidad }}</span>
                  </div>
                </template>
              </multiselect>
              <input v-model="docenteRes.docente" required="true" type="text" class="hide"/>
            </div>
          </div>
        </td>
        <td class="v-middle">
          <label class="switch">
            <input type="checkbox"
                   v-model="docenteRes.seleccionado"
                   v-bind:disabled="isEdicion &amp;&amp; docenteRes.id !=null"/>
            <span class="slider round"></span>
          </label>
        </td>
<!--        <td class="v-middle">-->
<!--          <button type="button"  v-on:click.prevent="anularSancion(index)" class="btn btn-danger" v-bind:disabled="isEdicion && sancionDsc.id != null && !resolucion.tipoResolucion.isTramiteSancionDisciplina">-->
<!--            <i class="fa fa-trash-o " aria-hidden="true"></i>-->
<!--          </button>-->
<!--        </td>-->
      </tr>


      </tbody>
    </table>

  </div>
</template>

<script>
const ResolucionFormFilter = httpVueLoader('/app/academico/resolucion/resolucionexistente/ResolucionFormFilter.vue');
module.exports = {
  mixins: [AppliedFilter, VueLoader],
  components: {
    resolucionFormFilter: ResolucionFormFilter,
  },
  props: {
    resolucion: {type: Object, default: {}},
  },
  model: {
    prop: 'resolucion',
    event: 'change'
  },
  data() {
    return {
      docentes: [],
      isEdicion: IS_EDICION,
      isAnular: IS_ANULAR,
      visualizarSoloSeleccionados:true,
      filterFacultad:true,
    };
  },
  mounted: function () {
    let $vue = this;
    if (!$vue.isEdicion) {
      if ($vue.resolucion.tipoResolucion.id === 112) {
        $vue.allDocenteResolucionConsejo();
      } else {
        $vue.allDocenteResolucionFacultad();
      }
    }
  },
  methods: {
    anularSancion(index) {
      let $vue = this;
      let sancionDsc = $vue.resolucion.sancionDisciplina[index];

      if (sancionDsc.id != null) {

        bootbox.confirm({
          message: "¿Está seguro de que desea anular esta sanción disciplinaria? Esta acción no se puede deshacer.",
          buttons: {
            confirm: {label: 'Sí, anular', className: "btn-danger"},
            cancel: {label: 'Cancelar', className: "btn-link"}
          },
          callback: function (result) {

            if (result) {
              $vue.showLoader("Anulando sanción...");

              AXIOS.delete(APP.url(`academico/resolucion/existentes/anularSancion/${sancionDsc.id}`))
                  .then(({data}) => {
                    $vue.hideLoader();

                    if (data.success) {
                      $vue.resolucion.sancionDisciplina.splice(index, 1);
                      $vue.$forceUpdate();

                      notify(data.message, "success");


                    } else {
                      notify("Error al anular la sanción: " + data.message, "error");
                    }
                  })
                  .catch((error) => {
                    $vue.hideLoader();
                    console.error('Error al anular sanción:', error);
                    notify("Error al anular la sanción. Por favor, inténtelo nuevamente.", "error");
                  });
            }
          }
        });

      }
    },
    searchAlumno(nombre) {

      let $vue = this;
      if ($vue.resolucion.oficina == null) {
        notify("Seleccione una oficina.");
        return;
      }

      AXIOS.get(APP.url("academico/resolucion/existentes/findAlumnoSancionados"),
          {params: {nombre: nombre, instanciaOficina: $vue.resolucion.oficina.id}})
          .then(({data}) => {
            if (data.success) {
              $vue.docentes = data.data;
            }
          });

    },
    allDocenteResolucionConsejo() {
      let $vue = this;
      $vue.showLoader("Espere un momento por favor");
      axios_.get(APP.url("academico/resolucion/existentes/allDocenteResolucionConsejo"))
          .then(({data}) => {
            $vue.resolucion.docenteResolucion = data;
            $vue.hideLoader();
            $vue.$forceUpdate();
          }, () => {
            $vue.hideLoader();
          });
    },
    allDocenteResolucionFacultad(){
      let $vue = this;
      $vue.showLoader("Espere un momento por favor");
      axios_.get(APP.url("academico/resolucion/existentes/allDocenteResolucionFacultad"))
          .then(({data}) => {
            $vue.resolucion.docenteResolucion = data;
            $vue.hideLoader();
            $vue.$forceUpdate();
          }, () => {
            $vue.hideLoader();
          });
    }
  }
};
</script>
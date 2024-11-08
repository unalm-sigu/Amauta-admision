<template>
  <div>

        <raptor-table v-bind:url="URL_BECAS_PRONABEC" ref="becaLoad">
          <template scope="props" >
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th class="col-md-4" colspan="2"></th>
                  <th class="col-md-2 text-center">Tipo Beca</th>
                  <th class="col-md-1 text-center v-middle">Año Convocatoria</th>
                  <th class="col-md-2 text-center v-middle">Fecha Inicio</th>
                  <th class="col-md-2 text-center v-middle">Fecha Fin</th>
                  <th class="col-md-1 text-center v-middle">Estado</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
              <tr  v-for="item in props.data" >
                  <td class="v-middle">
                    <div class="pull-left">
                      <img class="img-foto-tempo img-responsive img-thumbnail img-circle" v-bind:src="item.rutaFoto" />
                    </div>
                  </td>
                  <td class="v-middle">
                    <a  class="block text-primary bold h5 m-b-xs m-t-xs" >
                      {{item.nombre}}
                    </a>
                    <small class="block">
                        DNI:  <b>{{item.nroDocumento}}</b>
                    </small>
                    <span class="block">
                                                    Correo institucional:  <b>{{item.emailEmpresa}}</b>
                                                </span>
                    <span class="block">
                                                    Correo personal:  <b>{{item.email}}</b>
                                                </span>
                  </td>

                  <td class="v-middle text-center" >
                    <span >{{item.tipoBeca}}</span>
                  </td>

                  <td class="v-middle text-center" >
                    <span >{{item.yearConvocatoria}}</span>
                  </td>

                  <td class="v-middle text-center" >
                    <span>{{item.fechaInicio}}</span>
                  </td>

                  <td class="text-center v-middle" >
                    <span >{{item.fechaFin}}</span>
                  </td>
                <td class="text-center v-middle" >
                  <span :class="{
                    'label': true,
                    'label-success': item.estado === 'ACT',
                    'label-danger': item.estado === 'ANU'
                  }">
                    {{ item.estado === 'ANU' ? 'Anulado' : item.estado === 'ACT' ? 'Activo' : item.estado }}
                  </span>
                </td>
                  <td class="v-middle">
                    <div class="actions">
                      <a class="dropdown-toggle" href="#" data-toggle="dropdown"><i class="fa fa-cog"></i></a>
                      <ul class="dropdown-menu pull-right">
                        <li><a href="#" v-on:click.prevent="openEliminar(item)" class="text-danger"><i class="fa fa-trash" style="color: #ff0000;"></i> Eliminar Becado</a></li>
                        <li class="divider"></li>
                        <li><a href="#" v-on:click.prevent="openEditar(item)"><i class="fa fa-pencil text-warning"></i> Editar Becario</a></li>
                        <li v-if="item.estado !== 'ANU'"><a href="#" v-on:click.prevent="anular(item)"><i class="fa fa-ban text-secondary"></i> Anular Becado</a></li>
                        <li><a href="#" v-on:click.prevent="verHistorial(item)"><i class="fa fa-history text-info"></i> Historial Becas</a></li>
                      </ul>
                    </div>
                  </td>

              </tr>
              </tbody>
            </table>
          </template>
        </raptor-table>
        <modal-vik ref="modalAnular"
                   v-bind="modalAnular"
                   v-bind:okaction="eliminarBecado">

          <div slot="body">
            <form id="formAnular" data-parsley-validate="true" v-if="becadoAnular.id != null">

              <div class="form-group" >
                <table>
                  <tr >
                    <td class="v-middle col-md-4" >
                      <img
                           class="img-foto-tempo img-responsive img-thumbnail img-circle"
                           v-bind:src="becadoAnular.rutaFoto" />
                    </td>
                    <td  class="v-middle col-md-4" >
                      <a  class="block text-primary bold h5 m-b-xs m-t-xs" >
                        {{becadoAnular.nombre}}
                      </a>
                      <span class="block">
                                Matrícula: {{becadoAnular.nroDocumento}}
                            </span>
                      <span class="block">Tipo beca: {{becadoAnular.tipoBeca}}</span>
                    </td>
                  </tr>
                </table>
              </div>
            </form>
          </div>
        </modal-vik>
        <modal-vik ref="modalEditarBecc" v-bind="modalEditarBecc" v-bind:okaction="updateBecado">

          <div slot="body">
            <form ref="formEditar" data-parsley-validate="true">
              <div class="form-group">
                <label> Alumno </label>
                <input class="form-control col-md-4 m-b-xs" v-model="becadoEditar.nombre" required="" readonly/>
              </div>
              <div class="form-group">
                <label> Tipo de beca </label>
                <multiselect v-model="becadoEditar.tipoBeca"
                             v-bind:options='tipoBeca'
                             v-bind:internal-search="true"
                             label='nombre'
                             track-by='id'
                             placeholder="Seleccione un ciclo">
                </multiselect>
                <input type="hidden" v-model="becadoEditar.tipoBeca" required=""/>
              </div>
              <div class="form-group">
                <label> Año de convocatoria </label>
                <div>
                  <input type="number" class="form-control col-md-4 m-b-xs" minlength="4" placeholder="Ingrese el año ej. 2024" v-model="becadoEditar.yearConvocatoria" required="true"/>
                </div>

              </div>
              <div class="form-group">
                <label>Fecha de inicio</label>
                <div class="input-group date">
                  <date-picker v-model="becadoEditar.fechaInicio"
                               required="true"
                               v-bind:config="configDate"
                               data-parsley-id="2"
                               v-bind:wrap="true" >
                  </date-picker>
                  <div class="input-group-addon">
                    <span class="fa fa-calendar"></span>
                  </div>
                </div>
              </div>
              <div class="form-group">
                <label>Fecha fin</label>
                <div class="input-group date">
                  <date-picker v-model="becadoEditar.fechaFin"
                               required="true"
                               v-bind:config="configDate"
                               data-parsley-id="2"
                               v-bind:wrap="true" >
                  </date-picker>
                  <div class="input-group-addon">
                    <span class="fa fa-calendar"></span>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </modal-vik>
        <modal-vik ref="modalHistorial"
                   v-bind="modalHistorial">

          <div slot="body">
            <div class="panel padder-v">
              <table class="table table-striped table-hover">
                <thead>
                <tr>
                  <th class="col-xs-7">Tipo Beca</th>
                  <th class="col-xs-1  text-center">Año Convocatoria</th>
                  <th class="col-xs-2  text-center">Fecha Inicio</th>
                  <th class="col-xs-2  text-center">Fecha Fin</th>

                </tr>
                </thead>

                <tbody>
                <tr v-for="alumno in becadoHistorial">

                  <td class="v-middle">
                    <p class="text-primary text-sm">{{alumno.tipoBeca}}</p>
                  </td>

                  <td class="v-middle  text-center">
                    <span class="label label-default" >{{alumno.yearConvocatoria}}</span>
                  </td>
                  <td class="v-middle  text-center">
                    <span class="text-success text-sm" >{{alumno.fechaInicio}}</span>
                  </td>
                  <td class="v-middle  text-center">
                    <span class="text-danger text-sm" >{{alumno.fechaFin}}</span>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </modal-vik>

  </div>
</template>
<script>
module.exports={
  components:{
    RaptorTable: use("/_vue/modules/RaptorTable.vue")
  },
  data() {
    return {
      tipoBeca: JSON.parse(tipoBecaJson),
      URL_BECAS_PRONABEC: APP.url('academico/becaspronabec/list'),
      modalAnular: VUE_MODAL.structFormAjax({
        id: 'modalAnular',
        header: true,
        title: 'Eliminar Becado',
        okbtn: "Eliminar Becado",
        showaccept: true
      }),
      modalEditarBecc: VUE_MODAL.structFormAjax({
        id: 'modalEditarBecc',
        header: true,
        title: 'Editar Becado',
        okbtn: "Guardar Becado",
        showaccept: true
      }),
      modalHistorial: VUE_MODAL.structFormAjax({
        id: 'modalHistorial',
        header: true,
        title: 'Historial Becado',
        cancelbtn: 'Aceptar',
        showaccept: false
      }),
      becadoEditar:{},
      becadoAnular:{},
      becadoHistorial:{},
      configDate: {
        format: 'DD/MM/YYYY',
        useCurrent: false
      },
    };
  },
  mounted() {
  },
  methods: {
    openEliminar(item) {
      let $vue = this;
      $vue.becadoAnular = JSON.parse(JSON.stringify(item));
      $vue.$refs.modalAnular.open();
    },
    eliminarBecado() {
      let $vue = this;
      var form = $("#formAnular");

      if (!form.parsley().validate()) {
        return;
      }

      bootbox.confirm({
        message: '¿Está seguro que desea eliminar al becario?',
        buttons: {
          confirm: {label: 'Si, eliminar', className: 'btn-danger'},
          cancel: {label: 'Cancelar', className: 'btn-link'}
        },
        callback: function (result) {
          if (result) {
            axios.post(APP.url('academico/becaspronabec/eliminar'), null, {
              params: {
                id: $vue.becadoAnular.id
              },
              headers: {
                'Content-Type': 'application/json'
              }
            })
                .then(response => {
                  if (response.data.success) {
                    notify(response.data.message, "info");
                    $vue.$refs.becaLoad.loadRemoteData();
                    $vue.$refs.modalAnular.close();
                  } else {
                    notify(response.data.message, "error");
                  }
                })
                .catch(() => {
                  notify(Messages.errorComunicacion, "error");
                });
          }
        }
      });
    },
    anular(item) {
      let $vue = this;
      console.log(item);

      swal('¿El alumno perdió la beca?', {
        icon: "warning",
        closeOnClickOutside: false,
        closeOnEsc: false,
        dangerMode: true,
        buttons: {
          cancel: { text: "Cancelar", closeModal: true, visible: true },
          confirm: { text: "Sí, Anular", closeModal: false }
        }
      }).then((value) => {
        if (value !== true) {
          return;
        }

        // Enviar el id como parámetro en la URL
        axios.post(APP.url('academico/becaspronabec/anular'), null, {
          params: {
            id: item.id  // Aquí se pasa el id del alumno como parámetro
          },
          headers: {
            'Content-Type': 'application/json'
          }
        })
            .then(({data}) => {
              // Verificar si 'data' tiene un mensaje
              if (data && data.message) {
                // Si el servidor devuelve un mensaje de éxito, recargamos los datos y mostramos el mensaje
                $vue.$refs.becaLoad.loadRemoteData();
                return swal({ text: data.message, icon: "success", button: false, timer: 1700 });
              } else {
                // Si no hay un mensaje definido, mostrar un mensaje por defecto
                return swal({ text: "Operación realizada con éxito", icon: "success", button: false, timer: 1700 });
              }
            })
            .catch(() => {
              // Si hubo un error, mostramos un mensaje de error
              return swal(APP.errorComunicacion, "error");
            });
      }).catch(err => {
        if (err) {
          swal(APP.errorComunicacion, "error");
        } else {
          swal.stopLoading();
          swal.close();
        }
      });

    },
    openEditar(item) {
      let $vue = this;
      $vue.becadoEditar = JSON.parse(JSON.stringify(item));
      const becaSeleccionada = $vue.tipoBeca.find(beca => beca.nombre === $vue.becadoEditar.tipoBeca);
      if (becaSeleccionada) {
        $vue.becadoEditar.tipoBeca = becaSeleccionada;
      } else {
        $vue.becadoEditar.tipoBeca = null;
      }
      $vue.$refs.modalEditarBecc.open();
    },
    updateBecado() {
      let $vue = this;
      if (!$($vue.$refs.formEditar).parsley().validate()) {
        return;
      }
      axios.post(APP.url('academico/becaspronabec/updateBeca'), $vue.becadoEditar)
          .then(({data}) => {
            notify(data.message, "info");
            $vue.$refs.modalEditarBecc.close();
            $vue.$refs.becaLoad.loadRemoteData();
          }, () => {
            notify(data.message, "error");
            $vue.$refs.modalEditarBecc.close();
          });

    },
    refreshTable() {
      this.$refs.becaLoad.loadRemoteData(); // Refresca la tabla
    },
    verHistorial(item) {
      var vue = this;
      console.log(item)
      $.ajax({
        method: 'POST',
        url: APP.url("academico/becaspronabec/historial"),
        data: {id: item.nroDocumento},
        success: function(response) {
          if (response.success) {
            vue.becadoHistorial = response.data;
          } else {
            notify(response.message, 'error');
          }
        },
        error: function() {
          notify(Messages.errorComunicacion, "error");
        }
      });
      this.$refs.modalHistorial.open();

    }
  }
};
</script>
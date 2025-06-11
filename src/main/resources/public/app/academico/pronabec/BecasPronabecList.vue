<template>
  <div>

        <raptor-table v-bind:url="URL_BECAS_PRONABEC" ref="becaLoad">
          <template scope="props" >
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th class="col-md-4" colspan="2"></th>
                  <th class="col-md-2 text-center">Beca</th>
                  <th class="col-md-2 text-center v-middle">Fecha</th>
                  <th class="col-md-2 text-center v-middle">Estado</th>
                  <th class="col-md-2 text-center v-middle" >Modifica</th>
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
                  <a class="block text-primary bold h5 m-b-xs m-t-xs">
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
                  <span class="block">
                      Convocatoria: <span class="label label-success">{{item.yearConvocatoria}}</span>
                  </span>
                </td>

                  <td class="v-middle text-center" >
                    <span > Tipo: <b>{{item.tipoBeca}}</b> </span>
                    <br/>
                    <span> Carrera:  <b>{{item.carrera}}</b> </span>
                  </td>
                  <td class="v-middle text-center">
                    <span>
                      Inicio: <b>{{item.fechaInicio}}</b>
                    </span>
                    <br/>
                    <span>
                      Fin: <b>{{item.fechaFin}}</b>
                    </span>
                  </td>
                <td class="v-middle" >
                  <small style="display: block; text-align: center;">
                    <span :class="{
                      'label label-success': item.estado === 'ACTIVO',
                      'label label-danger': item.estado === 'INACTIVO',
                      'label label-warning': item.estado !== 'ACTIVO' && item.estado !== 'INACTIVO'
                    }">
                      {{ item.estado }}
                    </span>
                  </small>
                  <div v-if="item.rutaUrl" class="block text-muted">
                    <a v-bind:href="item.rutaUrl" target="_blank" >
                      <i class="fa fa-file-pdf-o text-danger"></i>
                      Ver documento
                    </a>
                  </div>
                  <small class="block">
                    Condicion:  <b>{{item.condicion}}</b>
                  </small>
                  <small class="block">
                    Situacion:  <b>{{item.situacion}}</b>
                  </small>
                </td>
                <td class="v-middle">
                  <span class="block small"> <b>{{item.modificador}}</b> </span>
                  <span class="small"> {{item.fechaModificacion}} </span>
                </td>
                  <td class="v-middle">
                    <div class="actions">
                      <a class="dropdown-toggle" href="#" data-toggle="dropdown"><i class="fa fa-cog"></i></a>
                      <ul class="dropdown-menu pull-right">
                        <li><a href="#" v-on:click.prevent="openEliminar(item)" class="text-danger"><i class="fa fa-trash" style="color: #ff0000;"></i> Eliminar Becado</a></li>
                        <li class="divider"></li>
                        <li><a href="#" v-on:click.prevent="openEditar(item)"><i class="fa fa-pencil text-warning"></i> Editar Becario</a></li>
<!--                        <li v-if="item.estado !== 'INACTIVO'"><a href="#" v-on:click.prevent="anular(item)"><i class="fa fa-ban text-secondary"></i> Desactivar Beca</a></li>-->
                        <li><a href="#" v-on:click.prevent="loadModalCondicion(item)" > <i class="fa fa-chain-broken text-warning"></i> Cambiar condicion beca</a></li>
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
                <label> Carrera Universitaria </label>
                <div>
                  <input type="text" class="form-control col-md-4 m-b-xs" v-model="becadoEditar.carrera" required="true"/>
                </div>

              </div>
              <div class="form-group">
                <label> Condición </label>
                <div>
                  <input type="text" class="form-control col-md-4 m-b-xs" v-model="becadoEditar.condicion" required="true"/>
                </div>
              </div>
              <div class="form-group">
                <label> Situación </label>
                <div>
                  <input type="text" class="form-control col-md-4 m-b-xs" v-model="becadoEditar.situacion" required="true"/>
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
        <modal-vik ref="modalCondicion" v-bind="modalCondicion" v-bind:okaction="saveCondicion" v-bind:showaccept="false"
                   modalsize="modal-lg">
          <div slot="body">
            <form id="frmResolucion">
              <div class="row">
                <div class="col-lg-6">
                  <label>Condicion</label>
                  <div class="form-group">
                    <textarea class="form-control" rows="4" readonly style="resize: none; background-color: #f8f9fa; word-wrap: break-word;">{{ becadoCondicion.condicion }}</textarea>
                  </div>
                </div>
                <div class="col-lg-6">
                  <label>Situacion</label>
                  <div class="form-group">
                    <textarea class="form-control" rows="4" readonly style="resize: none; background-color: #f8f9fa; word-wrap: break-word;">{{ becadoCondicion.situacion }}</textarea>
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="col-lg-6">
                  <label>Becario</label>
                  <div class="form-group">
                    <span class="form-control block" v-text="becadoCondicion.nombre" style="word-wrap: break-word; white-space: normal; height: auto; min-height: 34px;"/>
                  </div>
                </div>
                <div class="col-lg-6">
                  <label>Tipo Beca</label>
                  <div class="form-group">
                    <span class="form-control block" v-text="becadoCondicion.tipoBeca" style="resize: none;"></span>
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="col-lg-12">
                  <label>Archivo</label>
                  <div class="form-group">
                    <file-upload
                        extensions="gif,jpg,jpeg,png,pdf"
                        accept="image/png,image/gif,image/jpeg,application/pdf"
                        post-action="/academico/becaspronabec/addFile"
                        v-bind:data="{becadoCondicionId:becadoCondicion.id}"
                        v-model="files"
                        v-bind:multiple="false"
                        v-on:input-filter="inputFilter"
                        v-on:input-file="inputFile"
                        required="true"
                        ref="upload">
                        <button type="button"
                                class="btn btn-primary m-b-md"
                                v-if="!$refs.upload || !$refs.upload.active"
                                v-on:click="$refs.upload.active = true">
                          <i class="fa fa-cloud-upload"></i> &nbsp;Subir Archivo
                        </button>
                    </file-upload>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </modal-vik>

  </div>
</template>
<script>
Vue.component('file-upload', VueUploadComponent);
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
      modalCondicion: VUE_MODAL.structFormAjax({
        id: 'modalCondicion',
        header: true,
        title: 'Cambiar condicion',
        modalsize: 'modal-lg',
        okbtn: "Aceptar",
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
      becadoCondicion: {},
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
              if (data && data.message) {
                $vue.$refs.becaLoad.loadRemoteData();
                return swal({ text: data.message, icon: "success", button: false, timer: 1700 });
              } else {
                return swal({ text: "Operación realizada con éxito", icon: "success", button: false, timer: 1700 });
              }
            })
            .catch(() => {
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
      this.$refs.becaLoad.loadRemoteData();
    },
    verHistorial(item) {
      var vue = this;
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

    },
    loadModalCondicion(item){
      let $vue = this;
      this.becadoCondicion = JSON.parse(JSON.stringify(item));
      this.$refs.modalCondicion.open();
    },
    saveCondicion(){
      let $vue = this;
      var form = $("#frmResolucion");
    },
    inputFile(newFile, oldFile) {
      let $vue = this;
      MODAL.showWait("Espere un momento por favor");
      if (newFile && oldFile) {
        // update
        if (newFile.active && !oldFile.active) {
          // beforeSend
          // min size
          if (newFile.size >= 0 && this.minSize > 0 && newFile.size < this.minSize) {
            this.$refs.upload.update(newFile, {error: 'size'})
          }
        }
        if (newFile.progress !== oldFile.progress) {

          // progress
        }
        if (newFile.error && !oldFile.error) {
        }
        if (newFile.success && !oldFile.success) {
          //  $vue.producto.productoImagen.splice(0, 0, newFile.response.data)
        }
      }
      if (!newFile && oldFile) {
        if (oldFile.success && oldFile.response.id) {
        }
      }
      // Automatically activate upload
      if (Boolean(newFile) !== Boolean(oldFile) || oldFile.error !== newFile.error) {
        if (!this.$refs.upload.active) {
          //console.log('subiendo')
          this.$refs.upload.active = true
        } else {
          //console.log("FIN?")
        }
      }

      if ($vue.$refs.upload.uploaded) {
        if ($vue.files.length > 0) {
          //  $vue.reloadProducto();x
          $vue.becadoCondicion.rutaUrl = $vue.files[0].response.data;
        }
        if ($vue.$refs.upload.clear()) {
          //   console.log("reiniciar img 2")
        }
      }

      if (newFile && oldFile && !newFile.active && oldFile.active) {
        // Get response data
        if (newFile.xhr) {
          //  Get the response status code
          if (newFile.xhr.status == 200) {
            notify(newFile.response.message, "info");
            $vue.$refs.becaLoad.loadRemoteData();
          } else {
            notify(newFile.response.message, "error");
          }
          $vue.$refs.modalCondicion.close();
          MODAL.hideWait();
        } else {
          notify(response.message, "error");
        }
      }
    },
    inputFilter(newFile, oldFile, prevent) {
      if (newFile && !oldFile) {
        if (!/\.(gif|jpg|jpeg|png|pdf)$/i.test(newFile.name)) {
          swal(
              'Oops...',
              'Este archivo no esta permitido!',
              'error'
          )
          return prevent();
        }
      }
      if (newFile && (!oldFile || newFile.file !== oldFile.file)) {
        newFile.url = ''
        let URL = window.URL || window.webkitURL
        if (URL && URL.createObjectURL) {
          newFile.url = URL.createObjectURL(newFile.file)
        }
      }
    },
  }
};
</script>
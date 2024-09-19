<template>
  <div>

    <section class="panel">
      <section class="panel-body">
        <raptor-table v-bind:url="URL_BECAS_PRONABEC" v-bind:preload="true" ref="load">
          <template scope="props" >
            <table class="table table-striped table-hover">
              <thead>
              <tr>
                <th class="col-md-4" colspan="2"></th><!--  //-->
                <th class="col-md-3 text-center">Estado</th>
                <th class="col-md-3 text-center v-middle">Motivo</th>
                <th class="col-md-1 text-center v-middle">Ciclo</th>
                <th class="col-md-1 text-center v-middle">Monto</th>
                <th></th>
              </tr>
              </thead>
              <tbody>
              <template  v-for="item in props.data" >
                <tr v-for="(deudas,idx) in item.alumnoOmisoEleccions">
                  <td class="v-middle" v-bind:rowspan="getRows(item)" v-if="idx == 0">
                    <div class="pull-left">
                      <div v-if="item.persona.tipoFoto=='POSTUL' "
                           class="img-responsive img-thumbnail img-circle div-foto-list">
                        <img class="img-foto-list" v-bind:src="item.persona.rutaFoto" />
                      </div>

                      <img v-else-if="item.persona.tipoFoto=='COMUN' "
                           class="img-responsive img-thumbnail img-circle div-foto-alumno-list"
                           v-bind:src="item.persona.rutaFoto" />

                      <img v-else=""
                           class="img-foto-tempo img-responsive img-thumbnail img-circle"
                           v-bind:src="item.persona.rutaFoto" />
                    </div>
                  </td>
                  <td class="v-middle" v-bind:rowspan="getRows(item)" v-if="idx == 0">
                    <a  class="block text-primary bold h5 m-b-xs m-t-xs" >
                      {{item.persona.nombreCompleto}}
                    </a>
                    <span class="block">
                                                    Matrícula: <b>{{item.codigo}}</b>
                                                </span>
                    <span class="block">
                                                    Modalidad: <b> {{item.modalidadEstudio.nombre}}</b>
                                                </span>
                    <small v-if="item.persona.numeroDocIdentidad != '' " class="block bold">
                      {{item.persona.tipoDocumento.simbolo}} {{item.persona.numeroDocIdentidad}}
                    </small>
                    <span class="block">
                                                    Correo institucional:  <b>{{item.persona.emailCompania}}</b>
                                                </span>
                    <span class="block">
                                                    Correo personal:  <b>{{item.persona.email}}</b>
                                                </span>
                  </td>

                  <td class="v-middle text-center" >
                    <span class="label " v-bind:class="getClass(deudas.estadoEnum.name)" v-text="deudas.estadoEnum.value"></span>
                  </td>

                  <td class="v-middle text-center" >
                    <span v-text="deudas.motivoEnum.value"></span>
                  </td>

                  <td class="v-middle text-center" >
                    <span v-text="deudas.cicloAcademico.descripcion"></span>
                  </td>

                  <td class="text-center v-middle" >
                    <span v-text="commas(deudas.multa)"></span>
                  </td>
                  <td class="v-middle" v-bind:rowspan="getRows(item)" v-if="idx == 0">
                    <div class="actions">
                      <a class="dropdown-toggle" href="#" data-toggle="dropdown"><i class="fa fa-cog"></i></a>
                      <ul class="dropdown-menu pull-right">
                        <li><a href="#" v-on:click.prevent="openAnular(item)">Anular Deuda</a></li>
                        <li><a href="#" v-on:click.prevent="verAportes(item)">Ver aportes</a></li>
                        <li><a href="#" v-on:click.prevent="verBoletas(item)">Ver boletas</a></li>
                      </ul>
                    </div>
                  </td>
                </tr>

              </template>
              </tbody>
            </table>
          </template>
        </raptor-table>
      </section>
    </section>

  </div>
</template>

<script>
module.exports = {
  data() {
    return {
      URL_BECAS_PRONABEC: APP.url('academico/matricula/bloqueo/all'),
    };
  },
  mounted: function () {
    let $vue = this;
  },
  methods: {
    update(item) {
      this.$parent.update({...item});
    },
    eliminar(item) {
      let $vue = this;
      swal('¿Seguro que desea eliminar el registro?', {
        icon: "warning",
        closeOnClickOutside: false,
        closeOnEsc: false,
        dangerMode: true,
        buttons: {
          cancel: {text: "Cancelar", closeModal: true, visible: true},
          confirm: {text: "Sí, Eliminar", closeModal: false}
        }
      }).then((value) => {
        if (value != true) {
          return;
        }
        axios_.delete("/academico/matricula/bloqueo/" + item.id)
            .then(({data}) => {
              $vue.reload();
              notify(data, 'info');
              return swal({text: data, icon: "success", button: false, timer: 1000});
            }, () => {
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
    reload() {
      this.$refs.dynatable.repreload();
    }
  }
};
</script>
<script>
// DDDDSSSSSS
module.exports={
  data() {
    return {
      alumnosURL: APP.url(`${rutaModulo}/list`),
      ciclos: JSON.parse(cicloJson),
      motivos: JSON.parse(motivosJson),
      modalOmisoEleccion: this.createModal('Agregar Deuda', 'Guardar'),
      modalAnular: this.createModal('Anular deuda', 'Anular deuda'),
      modalLoadModal: this.createModal('Cargar Deuda', 'Cargar'),
      isLoading: false,
      alumnos: [],
      alumnoOmisoEleccion: {},
      omisionAnular: {},
      resumenModal: {},
      resumenes: [],
      alumno: {}
    };
  },
  mounted() {
    $(".numeric").numeric({ negative: false });
  },
  methods: {
    createModal(title, okbtn) {
      return {
        id: title.replace(/\s+/g, '') + 'Modal',
        header: true,
        title,
        okbtn,
        showaccept: true
      };
    },
    customLabel({ persona, codigo }) {
      return `${persona.nombreCompleto} — ${codigo}`;
    },
    openNuevo() {
      this.alumnoOmisoEleccion = {};
      this.$refs.modalOmisoEleccion.open();
    },
    save() {
      const form = $("#formNuevo");
      if (!form.parsley().validate()) return;

      MODAL.showWait("Espere un momento por favor");
      this.alumnoOmisoEleccion.motivo = this.alumnoOmisoEleccion.motivo.name;

      axios_.post(APP.url(`${rutaModulo}/saveOmision`), this.alumnoOmisoEleccion)
          .then(response => {
            if (response.data.success) {
              this.$refs.load.loadRemoteData();
              notify(response.data.message, "success");
            } else {
              notify(response.data.message, "error");
            }
            this.$refs.modalOmisoEleccion.close();
            MODAL.hideWait();
          })
          .catch(() => {
            this.$refs.modalOmisoEleccion.close();
            notify(Messages.errorComunicacion, "error");
          });
    },
    openAnular(item) {
      this.omisionAnular = JSON.parse(JSON.stringify(item));
      this.$refs.modalAnular.open();
    },
    saveAnular() {
      const form = $("#formAnular");
      if (!form.parsley().validate()) return;

      const selectedCount = this.omisionAnular.alumnoOmisoEleccions.filter(item => item.seleccionado).length;

      if (selectedCount === 0) {
        notify("No ha seleccionado que multas van a ser anuladas", "error");
        return;
      }

      bootbox.confirm({
        message: '¿Seguro que desea anular las deudas?',
        buttons: {
          confirm: { label: 'Si, anular', className: "btn-danger" },
          cancel: { label: 'Cancelar', className: "btn-link" }
        },
        callback: (result) => {
          if (result) {
            this.$refs.modalAnular.beginProcessing();
            axios_.post(APP.url(`${rutaModulo}/anularOmision`), this.omisionAnular)
                .then(response => {
                  this.$refs.modalAnular.confirmReaction(response.data.success);
                  if (response.data.success) {
                    this.$refs.load.loadRemoteData();
                    notify(response.data.message, "success");
                  } else {
                    notify(response.data.message, "error");
                  }
                })
                .catch(() => {
                  this.$refs.modalAnular.confirmReaction(false);
                  notify(Messages.errorComunicacion, "error");
                });
          }
        }
      });
    },
    loadAlumno(nombre) {
      if (!nombre) return;

      this.isLoading = true;

      axios_.post(APP.url(`${rutaModulo}/allAlumnoByNombre`), { nombre })
          .then(response => {
            if (response.data.success) {
              this.alumnos = response.data.data;
            }
            this.isLoading = false;
          })
          .catch(() => {
            this.isLoading = false;
          });
    },
    verAportes(item) {
      this.alumno = { ...item };
      this.resumenModal = {};
      this.$refs.modalAporteAlumno.open();
      this.$refs.modalAporteAlumno.showWait("Cargando aportes");

      axios_.post(APP.url(`${rutaModulo}/getInfoAportes/${item.id}`))
          .then(response => {
            this.$refs.modalAporteAlumno.hideWait();
            if (response.data.success) {
              this.resumenes = response.data.data;
            } else {
              this.$refs.modalAporteAlumno.close();
              notify(response.data.message, "error");
            }
          })
          .catch(() => {
            this.$refs.modalAporteAlumno.close();
            notify(Messages.errorComunicacion, "error");
          });
    },
    verBoletas(item) {
      this.resumenModal = {};
      this.$refs.modalBoletaAlumno.open();
      this.$refs.modalBoletaAlumno.showWait("Buscando boletas..");

      axios_.post(APP.url(`${rutaModulo}/findBoleta/${item.id}`))
          .then(response => {
            this.$refs.modalBoletaAlumno.hideWait();
            if (response.data.success) {
              if (response.data.data.boletas.length === 0) {
                this.$refs.modalBoletaAlumno.close();
                notify("No existen boletas generadas para este alumno", "warning");
                return;
              }
              this.resumenModal = response.data.data;
            } else {
              this.$refs.modalBoletaAlumno.close();
              notify(response.data.message, "error");
            }
          })
          .catch(() => {
            this.$refs.modalBoletaAlumno.close();
            notify(Messages.errorComunicacion, "error");
          });
    },
    commas(n) {
      return Number(n).toLocaleString('en', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    }
  }
};
</script>
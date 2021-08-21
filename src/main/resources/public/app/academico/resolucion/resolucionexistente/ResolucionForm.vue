<template>
    <div>

        <form id="form" data-parsley-validate="true" >

            <section class="panel m-b-xs">
                <section class="panel-body">

                    <resolucion-form-header></resolucion-form-header>

                </section>
            </section>

            <section class="panel m-b-xs">
                <section  class="panel-body">

                    <div v-if="resolucion.tipoResolucion">

                        <div v-if="resolucion.tipoResolucion.isTramiteBachiller">
                            <resolucion-form-details-bachiller></resolucion-form-details-bachiller>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isCambioNota">
                            <resolucion-form-details-cambio-nota></resolucion-form-details-cambio-nota>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isCambioPlanCurricular">
                            <resolucion-form-details-cambio-plan-curricular></resolucion-form-details-cambio-plan-curricular>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isCursoDirigido">
                            <resolucion-form-details-curso-dirigido></resolucion-form-details-curso-dirigido>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isNotaBaja">
                            <resolucion-form-details-nota-mas-baja></resolucion-form-details-nota-mas-baja>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isTramitePracticas">
                            <resolucion-form-details-practicas-preprofesionales></resolucion-form-details-practicas-preprofesionales>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isReadmision">
                            <resolucion-form-details-readmision></resolucion-form-details-readmision>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isReincorporacion">
                            <resolucion-form-details-reincorporacion></resolucion-form-details-reincorporacion>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isRetiroCiclo">
                            <resolucion-form-details-retiro-ciclo></resolucion-form-details-retiro-ciclo>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isTramiteTitulo">
                            <resolucion-form-details-titulo></resolucion-form-details-titulo>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isTrasladoInterno">
                            <resolucion-form-details-traslado></resolucion-form-details-traslado>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isTrasladoExterno">
                            <resolucion-form-details-traslado></resolucion-form-details-traslado>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isIntercambioEstudiantil">
                            <resolucion-form-details-traslado></resolucion-form-details-traslado>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isIngresoFisicoHistorial">
                            <resolucion-form-details-traslado></resolucion-form-details-traslado>
                        </div>

                    </div>


                    <button v-if="resolucion.id" type="button" v-on:click="update" class="btn btn-primary pull-left m-t-md">
                        <span><i class="fa fa-floppy-o" aria-hidden="true"></i></span>
                        Actualizar
                    </button>

                    <button  v-else="" type="button" v-on:click="save" class="btn btn-primary pull-left m-t-md">
                        <span><i class="fa fa-floppy-o" aria-hidden="true"></i></span>
                        Guardar
                    </button>


                </section>
            </section>

        </form>


        <modal-simple ref="modalError" v-bind:showaccept="false" >
            <div slot="header">
                <p class="h4 text-primary">LOS SIGUIENTES ALUMNO NO PUDIERON SER MATRICULADOS</p>
            </div>
            <div slot="body">

                <div class="row m-b-md" v-for="error in errores">
                    <b>- <span v-text="error"></span></b>
                </div>

            </div>
        </modal-simple>


    </div>
</template>

<script>
    const ResolucionFormHeader = httpVueLoader('/app/academico/resolucion/resolucionexistente/ResolucionFormHeader.vue');

    const ResolucionFormDetailsBachiller = httpVueLoader('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsBachiller.vue');
    const ResolucionFormDetailsCambioNota = httpVueLoader('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsCambioNota.vue');
    const ResolucionFormDetailsCambioPlanCurricular = httpVueLoader('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsCambioPlanCurricular.vue');
    const ResolucionFormDetailsCursoDirigido = httpVueLoader('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsCursoDirigido.vue');
    const ResolucionFormDetailsNotaMasBaja = httpVueLoader('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsNotaMasBaja.vue');
    const ResolucionFormDetailsPracticasPreprofesionales = httpVueLoader('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsPracticasPreprofesionales.vue');
    const ResolucionFormDetailsReadmision = httpVueLoader('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsReadmision.vue');
    const ResolucionFormDetailsReincorporacion = httpVueLoader('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsReincorporacion.vue');
    const ResolucionFormDetailsRetiroCiclo = httpVueLoader('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsRetiroCiclo.vue');
    const ResolucionFormDetailsTitulo = httpVueLoader('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsTitulo.vue');
    const ResolucionFormDetailsTraslado = httpVueLoader('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsTraslado.vue');

    module.exports = {
        components: {
            resolucionFormHeader: ResolucionFormHeader,
            resolucionFormDetailsBachiller: ResolucionFormDetailsBachiller,
            resolucionFormDetailsCambioNota: ResolucionFormDetailsCambioNota,
            resolucionFormDetailsCambioPlanCurricular: ResolucionFormDetailsCambioPlanCurricular,
            resolucionFormDetailsCursoDirigido: ResolucionFormDetailsCursoDirigido,
            resolucionFormDetailsNotaMasBaja: ResolucionFormDetailsNotaMasBaja,
            resolucionFormDetailsPracticasPreprofesionales: ResolucionFormDetailsPracticasPreprofesionales,
            resolucionFormDetailsReadmision: ResolucionFormDetailsReadmision,
            resolucionFormDetailsReincorporacion: ResolucionFormDetailsReincorporacion,
            resolucionFormDetailsRetiroCiclo: ResolucionFormDetailsRetiroCiclo,
            resolucionFormDetailsTitulo: ResolucionFormDetailsTitulo,
            resolucionFormDetailsTraslado: ResolucionFormDetailsTraslado,
        },
        mixins: [VueLoader],
        data() {
            return {
                errores: []
            };
        },
        computed: {
            ...Vuex.mapState(["resolucion", "isEdicion"])
        },
        mounted: function () {
            let $vue = this;
            if ($vue.resolucion.id) {
                $vue.isEdicion = true;
            }
        },
        methods: {
            save() {

                let $vue = this;
                var valid = $('#form').parsley().validate();
                if (!valid) {
                    return;
                }

                $vue.showLoader("Espere un momento por favor");
                $vue.errores = [];

                AXIOS.post(APP.url("academico/resolucion/existentes/save"), $vue.resolucion)
                        .then(({data}) => {
                            $vue.resolucion.tramiteTitulos = data.data;
                            if (data.success && data.data.length == 0) {

                                $vue.$store.dispatch('newResolucion');

                                notify(data.message, 'info');

                            } else {

                                if (data.data != null && data.data.length > 0) {
                                    $vue.errores = data.data;
                                    $vue.$refs.modalError.open();
                                    notify("Algunos alumnos no pudieron ser matriculados.", 'error');
                                } else {
                                    notify(data.message, 'error');
                                }
                            }
                            $vue.hideLoader();
                        }, () => {
                            $vue.hideLoader();
                            notify(Messages.errorComunicacion, "error");
                        });


            },
            update() {

                let $vue = this;
                var valid = $('#form').parsley().validate();

                if (!valid) {
                    return;
                }

                bootbox.confirm({
                    message: '¿Seguro que desea actualizar la resolución? ',
                    buttons: {
                        confirm: {label: 'Sí, aceptar', className: "btn-warning"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {

                            $vue.showLoader("Espere un momento por favor");
                            $vue.errores = [];

                            AXIOS.post(APP.url('academico/resolucion/existentes/update'), $vue.resolucion)
                                    .then(({data}) => {
                                        if (data.success && data.data.length == 0) {

                                            notify(data.message, 'info');
                                            location.href = APP.url('academico/resolucion');

                                        } else {

                                            if (data.data != null && data.data.length > 0) {
                                                $vue.errores = data.data;
                                                $vue.$refs.modalError.open();
                                                notify("Algunos alumnos no pudieron ser matriculados.", 'error');
                                            } else {
                                                notify(data.message, 'error');
                                            }
                                        }
                                        $vue.hideLoader();
                                    }, () => {
                                        $vue.hideLoader();
                                        notify(Messages.errorComunicacion, "error");
                                    });

                        }
                    }
                });

            },
        }
    };
</script>
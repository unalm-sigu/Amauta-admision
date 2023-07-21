<template>
    <div>

        <form id="form" data-parsley-validate="true" >

            <section class="panel m-b-xs">
                <section class="panel-body">

                    <resolucion-form-header v-bind:resolucion="resolucion"></resolucion-form-header>    </section>
            </section>

            <section class="panel m-b-xs">
                <section  class="panel-body">

                    <div v-if="resolucion.tipoResolucion">

                        <div v-if="resolucion.tipoResolucion.isTramiteBachiller || resolucion.tipoResolucion.isTramiteBachillerFacultad">
                            <resolucion-form-details-bachiller v-model="resolucion"></resolucion-form-details-bachiller>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isCambioNota">
                            <resolucion-form-details-cambio-nota v-model="resolucion"></resolucion-form-details-cambio-nota>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isCambioPlanCurricular">
                            <resolucion-form-details-cambio-plan-curricular v-model="resolucion"></resolucion-form-details-cambio-plan-curricular>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isCursoDirigido">
                            <resolucion-form-details-curso-dirigido v-model="resolucion"></resolucion-form-details-curso-dirigido>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isNotaBaja">
                            <resolucion-form-details-nota-mas-baja v-model="resolucion"></resolucion-form-details-nota-mas-baja>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isTramitePracticas">
                            <resolucion-form-details-practicas-preprofesionales v-model="resolucion"></resolucion-form-details-practicas-preprofesionales>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isReadmision">
                            <resolucion-form-details-readmision v-model="resolucion"></resolucion-form-details-readmision>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isReincorporacion">
                            <resolucion-form-details-reincorporacion v-model="resolucion"></resolucion-form-details-reincorporacion>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isRetiroCiclo">
                            <resolucion-form-details-retiro-ciclo v-model="resolucion"></resolucion-form-details-retiro-ciclo>
                        </div>
                        <div v-if="resolucion.tipoResolucion.isTramiteTitulo">
                            <resolucion-form-details-titulo v-model="resolucion"></resolucion-form-details-titulo>
                        </div>
                        
                        <div v-if="resolucion.tipoResolucion.isTramiteTituloFacultad">
                            <resolucion-form-details-titulo v-model="resolucion"></resolucion-form-details-titulo>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isTramiteTituloResolucion">
                            <resolucion-form-details-titulo v-model="resolucion"></resolucion-form-details-titulo>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isTrasladoInterno">
                            <resolucion-form-details-traslado-interno v-model="resolucion"></resolucion-form-details-traslado-interno>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isTrasladoExterno">
                            <resolucion-form-details-traslado v-model="resolucion"></resolucion-form-details-traslado>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isIntercambioEstudiantil">
                            <resolucion-form-details-traslado v-model="resolucion"></resolucion-form-details-traslado>
                        </div>

                        <div v-if="resolucion.tipoResolucion.isIngresoFisicoHistorial">
                            <resolucion-form-details-traslado v-model="resolucion"></resolucion-form-details-traslado>
                        </div>

                    </div>



                    <button v-if="resolucion.id && visible == false" type="button" v-on:click="update" class="btn btn-primary pull-left m-t-md">
                        <span><i class="fa fa-floppy-o" aria-hidden="true"></i></span>
                        Actualizar
                    </button>
                    <button v-else-if="resolucion.id && visible == true" type="button" v-on:click="update" class="btn btn-primary pull-left m-t-md">
                        <span><i class="fa fa-floppy-o" aria-hidden="true"></i></span>
                        Actualizar
                    </button>

                    <button  v-if="resolucion.id == null" type="button" v-on:click="save" class="btn btn-primary pull-left m-t-md">
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
    const ResolucionFormHeader = use('/app/academico/resolucion/resolucionexistente/ResolucionFormHeader.vue');

    const ResolucionFormDetailsBachiller = use('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsBachiller.vue');
    const ResolucionFormDetailsCambioNota = use('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsCambioNota.vue');
    const ResolucionFormDetailsCambioPlanCurricular = use('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsCambioPlanCurricular.vue');
    const ResolucionFormDetailsCursoDirigido = use('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsCursoDirigido.vue');
    const ResolucionFormDetailsNotaMasBaja = use('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsNotaMasBaja.vue');
    const ResolucionFormDetailsPracticasPreprofesionales = use('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsPracticasPreprofesionales.vue');
    const ResolucionFormDetailsReadmision = use('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsReadmision.vue');
    const ResolucionFormDetailsReincorporacion = use('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsReincorporacion.vue');
    const ResolucionFormDetailsRetiroCiclo = use('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsRetiroCiclo.vue');
    const ResolucionFormDetailsTitulo = use('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsTitulo.vue');
    const ResolucionFormDetailsTraslado = use('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsTraslado.vue');
    const ResolucionFormDetailsTrasladoInterno = use('/app/academico/resolucion/resolucionexistente/details/ResolucionFormDetailsTrasladoInterno.vue');
    const ModalSimple = use("/_vue/modules/ModalSimple.vue");
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
            resolucionFormDetailsTrasladoInterno: ResolucionFormDetailsTrasladoInterno,
            modalSimple: ModalSimple,
        },
        mixins: [VueLoader],
        data() {
            return {
                errores: [],
                visible: IS_EDICION,
                resolucion: IS_EDICION ? JSON.parse(resolucionJson) : {},
                resolucionNew: {
                    reincorporaciones: [],
                    retiroCiclo: [],
                    cambioNota: [],
                    cursoDirigido: [],
                    tramiteTraslado: [],
                    cambioNotaMasBajas: [],
                    tramiteBachiller: [],
                    tramiteTitulos: [],
                    tramitePracticasPreProfesionales: [],
                    readmisiones: [],
                    cambioPlanCurriculares: [],
                }
            };
        },
        mounted: function () {
            let $vue = this;
            if (IS_EDICION || IS_ANULAR) {
                $vue.resolucion = JSON.parse(resolucionJson);
            } else {
                $vue.resolucion = {...$vue.resolucionNew};
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

                axios_.post(APP.url("academico/resolucion/existentes/save"), $vue.resolucion)
                        .then(({data}) => {

                            if (data.success) {

                                $vue.resolucion = {... $vue.resolucionNew};
                                notify(data.message, 'info');

                            } else {

                                if (data.message.substring(0, 32) === 'Ya fue registrado una resolución') {
                                    notify(data.message, 'error');
                                } else {
                                    $vue.errores = data.data;
                                    $vue.$refs.modalError.open();
                                    notify("Algunos alumnos no pudieron ser matriculados.", 'error');
                                }

                            }

                            $vue.hideLoader();

                        }, () => $vue.hideLoader());

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

                            axios_.post(APP.url('academico/resolucion/existentes/update'), $vue.resolucion)
                                    .then(({data}) => {

                                        if (data.success) {

                                            notify(data.message, 'info');
                                            setTimeout(() => {
                                                location.href = APP.url('academico/resolucion');
                                            }, 500)

                                        } else {

                                            $vue.errores = data.data;
                                            $vue.$refs.modalError.open();
                                            notify("Algunos alumnos no pudieron ser matriculados.", 'error');

                                        }

                                        $vue.hideLoader();

                                    }, () => $vue.hideLoader());

                        }
                    }
                });

            },
            loadTramiteBachiller(data) {
                let $vue = this;
                $vue.resolucion.tramiteBachiller = data;
                $vue.$forceUpdate()
            }
        }
    };
</script>
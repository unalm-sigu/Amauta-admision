<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="btn-group pull-right">
                <a class="btn btn-default dropdown-toggle pull-right" v-bind:href="origen"> Regresar</a>
            </div>

            <h2> Informe final de tutoría {{ciclo.descripcion}}</h2>

        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-md">
                <section class="panel-body">

                    <template v-if="tienePermiso">

                        <div class="row">
                            <div class="col-md-2">
                                <foto-persona v-bind:persona="consejero.colaborador.persona"></foto-persona>
                            </div>
                            <div class="col-md-10">
                                <h4 class="bold m-t-sm m-b-xs">
                                    <span class="text-primary">Tutor</span>
                                </h4>
                                <h3 class="bold m-t-xs">
                                    {{consejero.colaborador.persona.apellidosNombres}}
                                </h3>
                                <div class="h4 m-t-xs">
                                    <b>Especialidad:</b> {{consejero.carrera.nombre}}
                                </div>
                                <div class="h4 m-t-xs">
                                    <b>Facultad:</b> {{consejero.carrera.facultad.nombre}}
                                </div>
                            </div>
                        </div>

                    </template>

                    <div v-else="" class="alert alert-danger">
                        <h3>Usted no tiene permiso de acceder a esta información</h3>
                    </div>

                </section>
            </section>

            <section class="panel m-b-md" v-if="tienePermiso">
                <section class="panel-body">

                    <template v-if="informe">
                        <template v-if="informe.id">
                            <template v-if="informe.numero">
                                <h3 class="text-primary"> Informe Nº {{informe.serie}}-{{informe.numero}}</h3>
                                <div class="block">
                                    <b>Fecha emisión:</b> {{informe.fechaEmision.split(' ')[0]}}
                                </div>
                            </template>

                            <template v-else="">
                                <h4 class="text-danger"> Informe sin numeración</h4>
                                <button v-if="esConsejero && informe.estado == 'PEN' "
                                        v-on:click.prevent="enviarInforme" class="btn btn-danger">
                                    Enviar informe
                                </button>
                            </template>

                            <!-- Nav tabs -->
                            <ul class="nav nav-tabs m-t-lg" role="tablist">
                                <li role="presentation" class="active"><a href="#actividades" aria-controls="actividades" role="tab" data-toggle="tab">Actividades</a></li>
                                <li role="presentation"><a href="#dificultades" aria-controls="dificultades" role="tab" data-toggle="tab">Dificultades</a></li>
                                <li role="presentation"><a href="#sugerencias" aria-controls="sugerencias" role="tab" data-toggle="tab">Sugerencias</a></li>
                                <li role="presentation"><a href="#conclusiones" aria-controls="conclusiones" role="tab" data-toggle="tab">Conclusiones</a></li>
                            </ul>

                            <!-- Tab panes -->
                            <div class="tab-content">
                                <div role="tabpanel" class="tab-pane active" id="actividades">
                                    <partes-informe v-bind:informe="informe"></partes-informe>
                                </div>
                                <div role="tabpanel" class="tab-pane " id="dificultades">
                                    <dificultades-informe v-bind:informe="informe"></dificultades-informe>
                                </div>
                                <div role="tabpanel" class="tab-pane " id="sugerencias">
                                    <sugerencias-informe v-bind:informe="informe"></sugerencias-informe>
                                </div>
                                <div role="tabpanel" class="tab-pane " id="conclusiones">
                                    <conclusiones-informe v-bind:informe="informe"></conclusiones-informe>
                                </div>
                            </div>

                        </template>

                        <div v-else="" class="alert alert-danger">
                            <h3>{{informe.comentarioInforme}}</h3>
                        </div>
                    </template>

                </section>
            </section>

        </section>

        <modal-confirm ref="modalConfirm"></modal-confirm>
        <modal-info ref="modalInfo"></modal-info>
    </div>

</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component('date-picker', window.VueBootstrapDatetimePicker);

    const FotoPersona = httpVueLoader('/app/_componentes/FotoPersonaAlumno.vue');
    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');
    const PartesInforme = httpVueLoader('./PartesInforme.vue');
    const DificultadesInforme = httpVueLoader('./DificultadesInforme.vue');
    const SugerenciasInforme = httpVueLoader('./SugerenciasInforme.vue');
    const ConclusionesInforme = httpVueLoader('./ConclusionesInforme.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo, FotoPersona,
            PartesInforme, DificultadesInforme, SugerenciasInforme, ConclusionesInforme
        },
        data() {
            return {
                ciclo: JSON.parse(cicloJson),
                consejero: JSON.parse(consejeroJson),
                origen: origen,
                tienePermiso: tienePermiso,
                esConsejero: esConsejero,
                informe: null,
                idModalConfirm: "id-modal-confirm-informe-tutor",
                idModalInfo: "id-modal-info-informe-tutor"
            };
        },
        mounted() {
            this.loadInforme();
        },
        computed: {
        },
        methods: {
            loadInforme() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/${this.consejero.id}/findInforme`
                })).then((resp) => this.informe = resp.data.data);
            },
            enviarInforme() {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea enviar el informe? <br/> Una vez enviado, ya no podrá modificarlo.`,
                    okbtn: "Si, enviar",
                    okclass: "btn-primary",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/enviarInforme`,
                            modal: this.$refs.modalConfirm.getModal(),
                            body: {id: this.informe.id}
                        })).then(() => this.loadInforme());
                    }
                });

                this.$refs.modalConfirm.open(config);
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>
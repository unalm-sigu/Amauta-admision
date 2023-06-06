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
                                <div class="block m-b m-t-xs">
                                    <span class="label font-estado" v-bind:class="classEstado()"> {{estadoInforme()}} </span>
                                </div>
                                <div class="block">
                                    <b>Fecha emisión:</b> {{informe.fechaEmision.split(' ')[0]}}
                                </div>
                                <div class="block" v-if="informe.estado == 'OBS' ">
                                    <b>Fecha observación:</b> {{informe.fechaModificacion.split(' ')[0]}}
                                </div>

                                <template v-if="informe.estado == 'ACP' ">
                                    <div class="block">
                                        <b>Fecha aprobación:</b> {{informe.fechaModificacion.split(' ')[0]}}
                                    </div>
                                    <div class="block">
                                        <b>Aprobado por:</b> {{informe.userAceptacion.persona.nombreConTitulo}}
                                    </div>
                                </template>

                                <div v-if="esCoordinador && informe.estado == 'OBS' " class="row">
                                    <div class="col-md-6">
                                        <div class="alert alert-danger m-t">
                                            <h4>Observaciones:</h4>
                                            <p>{{informe.observaciones}}</p>
                                        </div>
                                    </div>
                                </div>
                            </template>

                            <template v-else="">
                                <h4 class="text-danger"> Informe sin numeración</h4>
                            </template>

                            <div class="row">
                                <div class="col-md-10">
                                    <step-progress v-bind:steps="pasosInforme"></step-progress>
                                </div>
                            </div>

                            <br/>
                            <div class="block panel-steps m-t m-b">
                                <div class="col-md-10">
                                    <button v-on:click.prevent="previousStep" v-bind:disabled="pasoActivo == 1" class="btn btn-success">Anterior</button>
                                    <button v-on:click.prevent="nextStep" v-bind:disabled="pasoActivo == pasosInforme.length " class="btn btn-success">Siguiente</button>
                                </div>
                            </div>

                            <template v-if="pasoActivo == 1 ">
                                <partes-informe v-bind:informe="informe" ref="partesInforme"></partes-informe>
                            </template>
                            <template v-if="pasoActivo == 2 ">
                                <dificultades-informe v-bind:informe="informe" ref="dificultadesInforme"></dificultades-informe>
                            </template>
                            <template v-if="pasoActivo == 3 ">
                                <sugerencias-informe v-bind:informe="informe" ref="sugerenciasInforme"></sugerencias-informe>
                            </template>
                            <template v-if="pasoActivo == 4 ">
                                <conclusiones-informe v-bind:informe="informe" ref="conclusionesInforme"></conclusiones-informe>
                            </template>
                            <template v-if="pasoActivo == 5 ">
                                <enviar-informe v-bind:informe="informe" ref="enviarInforme"></enviar-informe>
                            </template>

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
    const StepProgress = httpVueLoader('/app/_componentes/StepProgress.vue');
    const PartesInforme = httpVueLoader('./PartesInforme.vue');
    const DificultadesInforme = httpVueLoader('./DificultadesInforme.vue');
    const SugerenciasInforme = httpVueLoader('./SugerenciasInforme.vue');
    const ConclusionesInforme = httpVueLoader('./ConclusionesInforme.vue');
    const EnviarInforme = httpVueLoader('./EnviarInforme.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo, FotoPersona,
            PartesInforme, DificultadesInforme, SugerenciasInforme,
            ConclusionesInforme, EnviarInforme, StepProgress
        },
        data() {
            return {
                ciclo: JSON.parse(cicloJson),
                consejero: JSON.parse(consejeroJson),
                origen: origen,
                tienePermiso: tienePermiso,
                esConsejero: esConsejero,
                esCoordinador: esCoordinador,
                informe: null,
                idModalConfirm: "id-modal-confirm-informe-tutor",
                idModalInfo: "id-modal-info-informe-tutor",
                pasoActivo: 1,
                pasosInforme: []
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
                })).then((resp) => {
                    this.informe = resp.data.data;
                    this.configurarPasos();
                    setTimeout(() => this.iniciarPasoActivo(), 400);
                });
            },
            configurarPasos() {
                if (this.esConsejero && ['PEN', 'OBS'].includes(this.informe.estado)) {
                    if (this.pasosInforme.length === 5) {
                        return;
                    }
                    this.pasosInforme = [
                        {id: 1, active: true, locked: false, title: "Actividades"},
                        {id: 2, active: false, locked: false, title: "Dificultades"},
                        {id: 3, active: false, locked: false, title: "Sugerencias"},
                        {id: 4, active: false, locked: false, title: "Conclusiones"},
                        {id: 5, active: false, locked: false, title: "Enviar informe"}
                    ];
                } else if (this.esCoordinador && ['ACT'].includes(this.informe.estado)) {
                    this.pasosInforme = [
                        {id: 1, active: true, locked: false, title: "Actividades"},
                        {id: 2, active: false, locked: false, title: "Dificultades"},
                        {id: 3, active: false, locked: false, title: "Sugerencias"},
                        {id: 4, active: false, locked: false, title: "Conclusiones"},
                        {id: 5, active: false, locked: false, title: "Responder"}
                    ];
                } else {
                    if (this.pasosInforme.length === 4) {
                        return;
                    }
                    this.pasosInforme = [
                        {id: 1, active: true, locked: false, title: "Actividades"},
                        {id: 2, active: false, locked: false, title: "Dificultades"},
                        {id: 3, active: false, locked: false, title: "Sugerencias"},
                        {id: 4, active: false, locked: false, title: "Conclusiones"}
                    ];
                }
            },
            estadoInforme() {
                if (this.informe.estado === 'ACT') {
                    return 'Enviado al Coordinador de Tutoría';
                } else if (this.informe.estado === 'PEN') {
                    return 'En construcción';
                } else if (this.informe.estado === 'OBS') {
                    return 'Observado por el Coordinador de Tutoría';
                } else if (this.informe.estado === 'ACP') {
                    return 'Aprobado';
                }
            },
            classEstado() {
                if (this.informe.estado === 'ACT') {
                    return 'label-primary';
                } else if (this.informe.estado === 'PEN') {
                    return 'label-default';
                } else if (this.informe.estado === 'OBS') {
                    return 'label-danger';
                } else if (this.informe.estado === 'ACP') {
                    return 'label-success';
                }
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
            choiseStep(step) {
                let posible = step.id;
                for (let idx in this.pasosInforme) {
                    let paso = this.pasosInforme[idx];
                    paso.active = paso.id <= posible;
                    if (paso.id == posible) {
                        this.pasoActivo = paso.id;
                        this.iniciarPasoActivo();
                    }
                    if (!paso.active) {
                        paso.locked = false;
                    }
                }
            },
            nextStep() {
                let posible = this.pasoActivo + 1;
                if (!this.verificarPasoActivo()) {
                    return;
                }

                for (let idx in this.pasosInforme) {
                    let paso = this.pasosInforme[idx];
                    paso.active = paso.id <= posible;
                    if (paso.id == posible) {
                        this.pasoActivo = paso.id;
                        this.iniciarPasoActivo();
                    }
                }
            },
            previousStep() {
                let posible = this.pasoActivo - 1;
                if (posible <= 0) {
                    return;
                }

                for (let idx in this.pasosInforme) {
                    let paso = this.pasosInforme[idx];
                    paso.active = paso.id <= posible;
                    if (paso.id == posible) {
                        this.pasoActivo = paso.id;
                        this.iniciarPasoActivo();
                    }
                    if (!paso.active) {
                        paso.locked = false;
                    }
                }
            },
            verificarPasoActivo() {
                if (!['PEN', 'OBS'].includes(this.informe.estado)) {
                    return true;
                }

                let paso = this.pasosInforme.find(e => e.id === this.pasoActivo);
                if (this.pasoActivo === 1) {
                    let ok = this.$refs.partesInforme.validar();
                    paso.locked = !ok;
                    return ok;

                } else if (this.pasoActivo === 2) {
                    let ok = this.$refs.dificultadesInforme.validar();
                    paso.locked = !ok;
                    return ok;

                } else if (this.pasoActivo === 3) {
                    let ok = this.$refs.sugerenciasInforme.validar();
                    paso.locked = !ok;
                    return ok;

                } else if (this.pasoActivo === 4) {
                    let ok = this.$refs.conclusionesInforme.validar();
                    paso.locked = !ok;
                    return ok;
                }

                return false;
            },
            iniciarPasoActivo() {

                let paso = this.pasosInforme.find(e => e.id === this.pasoActivo);
                setTimeout(() => {
                    if (this.pasoActivo === 1) {
                        this.$refs.partesInforme.iniciar();
                    } else if (this.pasoActivo === 2) {
                        this.$refs.dificultadesInforme.iniciar();
                    } else if (this.pasoActivo === 3) {
                        this.$refs.sugerenciasInforme.iniciar();
                    } else if (this.pasoActivo === 4) {
                        this.$refs.conclusionesInforme.iniciar();
                    }
                }, 400);
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>
<style>
    .font-estado {
        font-size: 13px;
    }
</style>
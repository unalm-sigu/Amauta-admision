<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="btn-group pull-right">
                <a class="btn btn-default dropdown-toggle pull-right" v-bind:href="origen"> Regresar</a>
            </div>

            <h2>Plan de intervención tutorial {{ciclo.descripcion}}</h2>

        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-md">
                <section class="panel-body">

                    <template v-if="tienePermiso">
                        <div class="row">
                            <div class="col-md-2">
                                <foto-persona v-bind:persona="alumno.persona"></foto-persona>
                            </div>
                            <div class="col-md-10">
                                <div class="row">
                                    <div class="col-md-6">
                                        <info-alumno v-bind:persona="alumno.persona" v-bind:alumno="alumno"></info-alumno>
                                    </div>
                                    <div class="col-md-6">
                                        <div v-if="alumno.persona.emailCompania" class="block m-b-xs">
                                            <i class="fa fa-envelope" aria-hidden="true"></i>
                                            {{alumno.persona.emailCompania}}
                                        </div>
                                        <div v-if="alumno.persona.email" class="block m-b-xs">
                                            <i class="fa fa-envelope-o" aria-hidden="true"></i>
                                            {{alumno.persona.email}}
                                        </div>
                                        <div v-if="alumno.persona.celular" class="block m-b-xs">
                                            <i class="fa fa-phone" aria-hidden="true"></i>
                                            {{alumno.persona.celular}}
                                        </div>
                                        <div v-if="alumno.persona.telefono" class="block m-b-xs">
                                            <i class="fa fa-volume-control-phone" aria-hidden="true"></i>
                                            {{alumno.persona.telefono}}
                                        </div>
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="col-md-12">
                                        <h3 class="bold m-t-sm">
                                            <span class="text-primary">Tutor:</span>
                                            {{consejero.colaborador.persona.apellidosNombres}}
                                        </h3>
                                    </div>
                                </div>
                            </div>


                        </div>
                    </template>

                    <div v-else="" class="alert alert-danger">
                        <h3 v-if="carrerasDiferentes">La especialidad del alumno es {{alumno.carrera.nombre}}, sin embargo la del tutor es {{consejero.carrera.nombre}}</h3>
                        <h3 v-else="">Usted no es el consejero del alumno seleccionado</h3>
                    </div>

                </section>
            </section>

            <section class="panel m-b-md" v-if="tienePermiso">
                <section class="panel-body">
                    <div>
                        <!-- Nav tabs -->
                        <ul class="nav nav-tabs" role="tablist">
                            <li role="presentation" class="active"><a href="#carac" aria-controls="carac" role="tab" data-toggle="tab">Caracterización</a></li>
                            <li role="presentation"><a href="#empatia" aria-controls="empatia" role="tab" data-toggle="tab">Mapa de empatía</a></li>
                            <li role="presentation"><a href="#plan" aria-controls="plan" role="tab" data-toggle="tab">Plan de intervención</a></li>
                        </ul>

                        <!-- Tab panes -->
                        <div class="tab-content">
                            <div role="tabpanel" class="tab-pane active" id="carac">
                                <tab-caracteristica v-bind:alumno="alumno"
                                                    v-bind:tipos="tipos"
                                                    v-bind:cualidades="cualidades"
                                                    ref="tabCaracteristica"></tab-caracteristica>
                            </div>
                            <div role="tabpanel" class="tab-pane " id="empatia">
                                <tab-mapa-empatia v-bind:alumno="alumno"
                                                  v-bind:tipos="tipos"
                                                  v-bind:cualidades="cualidades"
                                                  ref="tabMapaEmpatia"></tab-mapa-empatia>
                            </div>
                            <div role="tabpanel" class="tab-pane " id="plan">
                                <tab-plan-intervencion v-bind:alumno="alumno"></tab-plan-intervencion>
                            </div>
                        </div>

                    </div>
                </section>
            </section>
        </section>

        <modal-confirm ref="modalConfirm"></modal-confirm>
        <modal-info ref="modalInfo"></modal-info>
    </div>

</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component("date-picker", window.VueBootstrapDatetimePicker.default);

    const FotoPersona = httpVueLoader('/app/_componentes/FotoPersonaAlumno.vue');
    const InfoAlumno = httpVueLoader('/app/_componentes/InfoAlumno.vue');
    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');
    const TabCaracteristica = httpVueLoader('./TabCaracteristica.vue');
    const TabMapaEmpatia = httpVueLoader('./TabMapaEmpatia.vue');
    const TabPlanIntervencion = httpVueLoader('./TabPlanIntervencion.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo, FotoPersona, InfoAlumno,
            TabCaracteristica, TabMapaEmpatia, TabPlanIntervencion
        },
        data() {
            return {
                ciclo: JSON.parse(cicloJson),
                alumno: JSON.parse(alumnoJson),
                consejero: JSON.parse(consejeroJson),
                alumnoConsejero: JSON.parse(alumnoConsejeroJson),
                origen: origen,
                tienePermiso: tienePermiso,
                esConsejero: esConsejero,
                tipos: [],
                cualidades: [],
                configDate: {
                    format: 'DD/MM/YYYY',
                    locale: 'es'
                }
            };
        },
        mounted() {
            if (this.consejero.id && this.alumnoConsejero.id) {
                this.loadTiposCualidades();
                this.loadCualidadesAlumno();
            }
        },
        computed: {
        },
        methods: {
            loadTiposCualidades() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allTiposCualidades`
                })).then((resp) => this.tipos = resp.data.data);
            },
            loadCualidadesAlumno() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allCualidadesAlumno`,
                    body: {id: this.alumno.id}
                })).then((resp) => {
                    this.cualidades = resp.data.data;
                    this.$refs.tabCaracteristica.revisarDatos(this.cualidades.length);
                    this.$refs.tabMapaEmpatia.revisarDatos(this.cualidades.length);
                });
            },
            carrerasDiferentes() {
                if (!this.alumno.carrera) {
                    return false;
                }
                if (!this.alumno.carrera.codigo) {
                    return false;
                }
                if (!this.consejero.carrera) {
                    return false;
                }
                if (!this.consejero.carrera.codigo) {
                    return false;
                }

                return this.alumno.carrera.codigo !== this.consejero.carrera.codigo;
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>
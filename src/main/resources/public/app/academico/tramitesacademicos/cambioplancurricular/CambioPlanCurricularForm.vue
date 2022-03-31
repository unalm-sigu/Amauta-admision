<template>
    <div>

        <modal-simple ref="modal_cambio_plan_curricular"
                      v-bind:okaction="save" >

            <div slot="header">
                <h4 class="modal-title">Nuevo Trámite Cambio de Plan Curricular</h4>
            </div>

            <div slot="body">
                <form data-parsley-validate="true">

                    <div class="form-group">
                        <label>Alumno</label>

                        <multiselect
                            v-model="cambio.alumno"
                            v-bind:options="alumnos"
                            v-bind:allow-empty="true"
                            v-on:search-change="searchAlumno"
                            v-on:input="changeAlumno"
                            track-by="id"
                            placeholder=" "
                            label='nombre'
                            v-bind:internal-search="false"
                            v-bind:hide-selected="false"
                            v-bind:showNoOptions="true"
                            v-bind:show-labels="false">

                            <template slot="singleLabel" slot-scope="props">
                                <span class="">{{ props.option.persona.nombreCompleto }}</span>
                                <span class="">{{ props.option.persona.tipoDocumento.simbolo }} - {{ props.option.persona.numeroDocIdentidad }}</span>
                            </template>

                            <template slot="option" slot-scope="props">
                                <span class="block bold">{{ props.option.persona.nombreCompleto }} </span>
                                <span class="text-xs">{{ props.option.persona.tipoDocumento.simbolo }} - {{ props.option.persona.numeroDocIdentidad }}</span>
                            </template>

                            <template slot="noOptions">&nbsp</template>
                            <template slot="noResult">&nbsp</template>

                        </multiselect>

                        <input v-model="cambio.alumno" required="true" type="text" class="hide"/>

                    </div>

                    <div class="form-group">

                        <label>Plan Curricular Origen</label>

                        <p v-if="cambio.planCurricularOrigen" v-text="cambio.planCurricularOrigen.cicloInicioVigencia.descripcion" required="true" type="text" ></p>

                    </div>

                    <div class="form-group">
                        <label>Plan Curricular Destino</label>

                        <multiselect
                            v-model="cambio.planCurricularDestino"
                            v-bind:options="destinos"
                            v-bind:allow-empty="true"
                            track-by="id"
                            placeholder=" "
                            label='descripcion'
                            v-bind:internal-search="false"
                            v-bind:hide-selected="false"
                            v-bind:showNoOptions="true"
                            v-bind:show-labels="false">

                            <template slot="singleLabel" slot-scope="props">
                                <span class="">{{ props.option.cicloInicioVigencia.descripcion }}</span>
                            </template>

                            <template slot="option" slot-scope="props">
                                <span class="">{{ props.option.cicloInicioVigencia.descripcion }} </span>
                            </template>

                            <template slot="noOptions">&nbsp</template>
                            <template slot="noResult">&nbsp</template>

                        </multiselect>

                        <input v-model="cambio.planCurricularDestino" required="true" type="text" class="hide"/>

                    </div>

                    <div class="form-group">
                        <label>Motivo</label>
                        <textarea class="form-control" v-model="cambio.motivo" required="true"></textarea>
                    </div>

            </div>
        </modal-simple> 

    </div>
</template>

<script>
    module.exports = {
        components: {
            ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        },
        data() {
            return {
                cambio: {},
                destinos: [],
                ciclo: {},
                alumnos: [],
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            open() {
                let $vue = this;
                $vue.cambio = {};
                $vue.$refs.modal_cambio_plan_curricular.open();
            },
            save() {
                let $vue = this;
                axios.post(APP.url("academico/tramiteacademico/cambioplancurricular"), $vue.cambio)
                        .then(({data}) => {

                            if (data.success) {
                                $vue.$parent.$parent.$refs.cambioplancurricularlist.recargar();
                                notify(data.message, "success");
                            } else {
                                notify(data.message, "error");
                            }

                            $vue.$refs.modal_cambio_plan_curricular.close();

                        }, error => {

                            $vue.$refs.modal_cambio_plan_curricular.stop();
                            notify(Messages.errorComunicacion, "error");
                        });
            },
            searchAlumno(nombre) {

                let $vue = this;

                if (nombre) {

                    axios.get(APP.url("academico/tramiteacademico/cambioplancurricular/searchAlumno"), {params: {nombre: nombre}})
                            .then(({data}) => {

                                if (data.success) {
                                    $vue.alumnos = data.data;
                            }

                            });

                }
            },
            changeAlumno(alumno) {

                let $vue = this;

                Vue.set($vue.cambio, "planCurricularOrigen", );
                Vue.set($vue.cambio, "planCurricularDestino", );
                $vue.destinos = [];

                if (alumno) {

                    axios.get(APP.url("academico/tramiteacademico/cambioplancurricular/searchPlanCurricular/" + alumno.id))
                            .then(({data}) => {

                                if (data.success) {

                                    $vue.cambio.planCurricularOrigen = data.data.planCurricularOrigen;
                                    $vue.destinos = data.data.destinos;

                            }

                            });

                }
            }
        }
    };
</script>
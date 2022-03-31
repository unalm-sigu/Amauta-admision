<template>
    <div>

        <modal-simple ref="modalEditarEncuesta"
                      modalsize="modal-lg"
                      v-bind:okaction="save">
            <div slot="header">
                <p class="h4 text-primary">Edicion Periodo Encuesta</p>
            </div>
            <div slot="body">

                <div class="panel-body">
                    Fechas en que se efectuarán las encuestas para secciones con un solo docente
                    <table class="table table-condensed m-t">
                        <thead>
                            <tr>
                                <th class="col-md-4 text-center">Modalidad</th>
                                <th class="col-md-4 text-center">Fecha inicio</th>
                                <th class="col-md-4 text-center">Fecha fin</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(periodo,idx) in periodosEncuesta">
                                <td  class="v-middle">
                                    <div>
                                        <multiselect v-model='periodo.modalidadEstudio'               
                                                     v-bind:options='modalidadesEstudios'
                                                     label='nombre'
                                                     v-bind:limit='15'
                                                     :show-labels="false"
                                                     v-bind:disabled="periodo.id"
                                                     track-by='id'>
                                        </multiselect>
                                    </div>
                                </td>
                                <td  class="v-middle">
                                    <div class="input-group">
                                        <date-picker 
                                            v-bind:disabled="periodo.id"
                                            v-bind:config="configDate" 
                                            v-bind:wrap="false" 
                                            v-model="periodo.fechaInicio"></date-picker>
                                        <div class="input-group-addon">
                                            <span class="fa fa-calendar"></span>
                                        </div>
                                    </div>
                                </td>
                                <td  class="v-middle">
                                    <div class="input-group">
                                        <date-picker 
                                            v-bind:disabled="periodo.id"
                                            v-bind:config="configDate" 
                                            v-bind:wrap="false" 
                                            v-model="periodo.fechaFin"></date-picker>
                                        <div class="input-group-addon">
                                            <span class="fa fa-calendar"></span>
                                        </div>
                                    </div>
                                </td>
                                <td  class="v-middle">

                                    <span class="pointer" 
                                          v-if="!periodo.id"
                                          v-on:click="removePeriodo(idx)">
                                        <i class="fa fa-2x fa-trash-o text-danger"></i>
                                    </span>

                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <div class="block text-center">
                        <a class="btn btn-sm btn-primary pointer" v-on:click="addPeriodoEncuesta">Agregar periodo</a>
                    </div>
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
        data: function () {
            return {
                periodosEncuesta: [],
                encuestaEstudiantil: {},
                modalidadesEstudios: JSON.parse(modalidadEstudiosJson),
                configDate: {
                    format: "DD/MM/YYYY",
                    useCurrent: false
                },
            }
        },
        methods: {
            open() {
                let $vue = this;
                axios_.get(APP.url('academico/encuestaestudiantil/docente/resumenPeriodo'))
                        .then(({data}) => {
                            $vue.encuestaEstudiantil = data;
                            $vue.periodosEncuesta = data.periodosEncuesta;
                            $vue.$refs.modalEditarEncuesta.open();
                        }, () => {
                        });
            },
            save() {
                var $vue = this;
                $vue.encuestaEstudiantil.periodosEncuesta = $vue.periodosEncuesta;
                axios_.post(APP.url('academico/encuestaestudiantil/docente/updatePeriodo'), $vue.encuestaEstudiantil)
                        .then(({data}) => {
                            $vue.$refs.modalEditarEncuesta.close();
                            notify(data, 'success');
                        }, () => {
                            $vue.$refs.modalEditarEncuesta.stop();
                        });
            },
            addPeriodoEncuesta() {
                var $vue = this;
                if ($vue.periodosEncuesta.length < 2) {
                    $vue.periodosEncuesta.push({});
                    return
                }
                notify("Solo puede agregar mas de 2 periodos", "error");
            },
            removePeriodo(i) {
                var vue = this;
                vue.periodosEncuesta.splice(i, 1);
            },
        }
    };
</script>
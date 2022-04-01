<template>
    <div>

        <modal-simple ref="modalForm"
                      v-bind:okaction="save">

            <div slot="header">
                <h4 class="modal-title" id="myModalLabel"> Bloqueo Matricula Alumno </h4>
            </div>

            <div slot="body">

                <form class="form" id="form" data-parsley-validate="true">

                    <div class='form-group'>

                        <label class="">Ciclo Aplica</label>

                        <multiselect
                            v-model="bloqueo.cicloAplica"
                            v-bind:options="ciclos"
                            v-bind:allow-empty="true"
                            track-by="id"
                            placeholder=" "
                            label='descripcion'
                            v-bind:internal-search="false"
                            v-bind:hide-selected="false"
                            v-bind:showNoOptions="false"
                            v-bind:show-labels="false">

                            <template slot="singleLabel" slot-scope="props">
                                {{ props.option.descripcion }}
                            </template>

                            <template slot="option" slot-scope="props">
                                {{ props.option.descripcion }}
                            </template>

                            <template slot="noOptions">&nbsp;</template>
                            <template slot="noResult">&nbsp;</template>

                        </multiselect>

                    </div>

                    <div class='form-group'>

                        <label class="">Carrera</label>

                        <multiselect
                            v-model="bloqueo.carrera"
                            v-bind:options="carreras"
                            v-bind:allow-empty="true"
                            track-by="id"
                            placeholder=" "
                            label='nombre'
                            v-bind:internal-search="false"
                            v-bind:hide-selected="false"
                            v-bind:showNoOptions="false"
                            v-bind:show-labels="false">

                            <template slot="singleLabel" slot-scope="props">
                                {{ props.option.nombre }}
                            </template>

                            <template slot="option" slot-scope="props">
                                {{ props.option.nombre }}
                            </template>

                            <template slot="noOptions">&nbsp;</template>
                            <template slot="noResult">&nbsp;</template>

                        </multiselect>

                    </div>

                    <div class='form-group'>

                        <label class="">Situación Académica</label>

                        <multiselect
                            v-bind:multiple="true" 
                            v-model="bloqueo.situacionAcademicas"
                            v-bind:options="situaciones"
                            v-bind:allow-empty="true"
                            track-by="id"
                            placeholder=" "
                            label='nombre'
                            v-bind:internal-search="false"
                            v-bind:hide-selected="false"
                            v-bind:showNoOptions="false"
                            v-bind:show-labels="false">

                            <template slot="singleLabel" slot-scope="props">
                                {{ props.option.nombre }}
                            </template>

                            <template slot="option" slot-scope="props">
                                {{ props.option.nombre }}
                            </template>

                            <template slot="noOptions">&nbsp;</template>
                            <template slot="noResult">&nbsp;</template>

                        </multiselect>

                    </div>

                </form>

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
                bloqueo: {},
                ciclos: [],
                situaciones: [],
                carreras: []
            };
        },
        mounted: function () {
            let $vue = this;
            $vue.allCiclo();
            $vue.allSituacion();
            $vue.allCarrera();
        },
        methods: {
            save() {
                let $vue = this;
                if ($vue.bloqueo.id) {
                    $vue.actualizar();
                    return;
                }
                $vue.guardar();
            },
            actualizar() {
                let $vue = this;
                axios_.put('/academico/matricula/bloqueo', $vue.bloqueo).then(({data}) => {
                    $vue.$refs.modalForm.close();
                    notify(data, 'info');
                    $vue.$parent.reload();
                }, () => {
                    $vue.$refs.modalForm.stop();
                });
            },
            guardar() {
                let $vue = this;
                axios_.post('/academico/matricula/bloqueo', $vue.bloqueo).then(({data}) => {
                    $vue.$refs.modalForm.close();
                    notify(data, 'info');
                    $vue.$parent.reload();
                }, () => {
                    $vue.$refs.modalForm.stop();
                });
            },
            nuevo() {
                let $vue = this;
                $vue.bloqueo = {}
                $vue.$refs.modalForm.open();
            },
            update(item) {
                let $vue = this;
                axios_.get('/academico/matricula/bloqueo/' + item.id).then(({data}) => {
                    $vue.bloqueo = data
                    $vue.$refs.modalForm.open();
                }, () => {
                });
            },
            allCiclo() {
                let $vue = this;
                axios_.get('/academico/matricula/bloqueo/allCiclo').then(({data}) => {
                    $vue.ciclos = data;
                }, () => {
                });
            },
            allSituacion() {
                let $vue = this;
                axios_.get('/academico/matricula/bloqueo/allSituacion').then(({data}) => {
                    $vue.situaciones = data;
                }, () => {
                });
            },
            allCarrera() {
                let $vue = this;
                axios_.get('/academico/matricula/bloqueo/allCarrera').then(({data}) => {
                    $vue.carreras = data;
                }, () => {
                });
            }
        }
    };
</script>
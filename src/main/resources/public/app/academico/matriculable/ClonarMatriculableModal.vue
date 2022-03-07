<template>
    <div>

        <modal-simple ref="modalClonarMatriculables"
                      v-bind:okaction="clonar">
            <div slot="header">
                Clonar Ciclo Nivelación
            </div>
            <div slot="body">

                <form data-parsley-validate="true">

                    <div class="form-group">
                        <label>Ciclo Origen</label>

                        <multiselect
                            v-model="dto.cicloOrigen"
                            v-bind:options="ciclosAcademicos"
                            v-bind:allow-empty="true"
                            track-by="id"
                            placeholder=" "
                            label='descripcion'
                            v-bind:internal-search="true"
                            v-bind:hide-selected="false"
                            v-bind:showNoOptions="true"
                            v-bind:show-labels="false">

                            <template slot="singleLabel" slot-scope="props">
                                <span class="option__title">
                                    {{ props.option.descripcion }}
                                </span>
                            </template>

                            <template slot="option" slot-scope="props">
                                <span class="option_title">
                                    {{props.option.descripcion}}
                                </span> 
                            </template>

                            <template slot="noOptions">&nbsp;</template>
                            <template slot="noResult">&nbsp;</template>

                        </multiselect>

                        <input v-model="dto.cicloOrigen" required="true" type="text" class="hide"/>

                    </div>

                    <div class="form-group">
                        <label>Ciclo Destino</label>

                        <multiselect
                            v-model="dto.cicloDestino"
                            v-bind:options="ciclosAcademicos"
                            v-bind:allow-empty="true"
                            track-by="id"
                            placeholder=" "
                            label='descripcion'
                            v-bind:internal-search="true"
                            v-bind:hide-selected="false"
                            v-bind:showNoOptions="true"
                            v-bind:show-labels="false">

                            <template slot="singleLabel" slot-scope="props">
                                <span class="option__title">
                                    {{ props.option.descripcion }}
                                </span>
                            </template>

                            <template slot="option" slot-scope="props">
                                <span class="option_title">
                                    {{props.option.descripcion}}
                                </span> 
                            </template>

                            <template slot="noOptions">&nbsp;</template>
                            <template slot="noResult">&nbsp;</template>

                        </multiselect>

                        <input v-model="dto.cicloDestino" required="true" type="text" class="hide"/>

                    </div>

                </form>

            </div>
        </modal-simple>

    </div>
</template>
<script>
    module.exports = {
        data() {
            return {
                ciclosAcademicos: JSON.parse(ciclos),
                carrera: null,
                dto: {}
            };
        },
        mounted: function () {

        },
        methods: {
            open() {
                let $vue = this;
                $vue.$refs.modalClonarMatriculables.open();
            },
            clonar() {
                let $vue = this;
                axios_.post(APP.url('academico/matriculable/nivelacion/clonar'), $vue.dto)
                        .then(({data}) => {
                            notify(data);
                            $vue.$refs.modalClonarMatriculables.close();
                            $vue.$parent.$refs.load.loadRemoteData();
                            $vue.$parent.getResumen();
                        }, () => $vue.$refs.modalClonarMatriculables.stop());
            }
        }
    };
</script>
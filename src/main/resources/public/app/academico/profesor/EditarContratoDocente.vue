<template>
    <div>

        <modal-simple ref="modalEditarContratoDocente"
                      okbtn="Actualizar"
                      v-bind:okaction="saveContratoDocente">
            <div slot="header">
                <p class="h4 text-primary">Editar Contrato Docente</p>
            </div>
            <div slot="body">

                <form ref="formContrato" data-parsley-validate="true" >
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label> Categoría </label>

                                <multiselect
                                    v-model="contrato.categoria"
                                    v-bind:options="categorias"
                                    v-bind:allow-empty="true"
                                    track-by="id"
                                    placeholder=" "
                                    label='nombre'
                                    v-bind:internal-search="true"
                                    v-bind:hide-selected="true"
                                    v-bind:showNoOptions="true"
                                    v-bind:show-labels="false">

                                    <template slot="singleLabel" slot-scope="props">
                                        <span class="option__title">
                                            {{ props.option.nombre }}
                                        </span>
                                    </template>

                                    <template slot="option" slot-scope="props">
                                        <span class="option_title">
                                            {{props.option.nombre}}
                                        </span> 
                                    </template>

                                    <template slot="noOptions">&nbsp</template>
                                    <template slot="noResult">&nbsp</template>

                                </multiselect>

                                <input type="text" class="hide"      v-model="contrato.categoria" required="true"/>

                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label> Dedicación </label>


                                <multiselect
                                    v-model="contrato.dedicacion"
                                    v-bind:options="dedicaciones"
                                    v-bind:allow-empty="true"
                                    track-by="id"
                                    placeholder=" "
                                    label='nombre'
                                    v-bind:internal-search="true"
                                    v-bind:hide-selected="true"
                                    v-bind:showNoOptions="true"
                                    v-bind:show-labels="false">

                                    <template slot="singleLabel" slot-scope="props">
                                        <span class="option__title">
                                            {{ props.option.nombre }}
                                        </span>
                                    </template>

                                    <template slot="option" slot-scope="props">
                                        <span class="option_title">
                                            {{props.option.nombre}}
                                        </span> 
                                    </template>

                                    <template slot="noOptions">&nbsp</template>
                                    <template slot="noResult">&nbsp</template>

                                </multiselect>

                                <input type="text" class="hide" v-model="contrato.dedicacion" required="true"/>

                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label> Situación </label>


                                <multiselect
                                    v-model="contrato.situacion"
                                    v-bind:options="situaciones"
                                    v-bind:allow-empty="true"
                                    track-by="id"
                                    placeholder=" "
                                    label='nombre'
                                    v-bind:internal-search="true"
                                    v-bind:hide-selected="true"
                                    v-bind:showNoOptions="true"
                                    v-bind:show-labels="false">

                                    <template slot="singleLabel" slot-scope="props">
                                        <span class="option__title">
                                            {{ props.option.nombre }}
                                        </span>
                                    </template>

                                    <template slot="option" slot-scope="props">
                                        <span class="option_title">
                                            {{props.option.nombre}}
                                        </span> 
                                    </template>

                                    <template slot="noOptions">&nbsp</template>
                                    <template slot="noResult">&nbsp</template>

                                </multiselect>

                                <input type="text" class="hide" v-model="contrato.situacion" required="true"/>

                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label> Ciclo Inicio </label>

                                <multiselect
                                    v-model="contrato.cicloInicioContrato"
                                    v-bind:options="ciclos"
                                    v-bind:allow-empty="true"
                                    track-by="id"
                                    placeholder=" "
                                    label='descripcion'
                                    v-bind:internal-search="true"
                                    v-bind:hide-selected="true"
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

                                    <template slot="noOptions">&nbsp</template>
                                    <template slot="noResult">&nbsp</template>

                                </multiselect>

                                <input type="text" class="hide"  v-model="contrato.cicloInicioContrato" required="true"/>


                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label> Ciclo Fin </label>


                                <multiselect
                                    v-model="contrato.cicloFinContrato"
                                    v-bind:options="ciclos"
                                    v-bind:allow-empty="true"
                                    track-by="id"
                                    placeholder=" "
                                    label='descripcion'
                                    v-bind:internal-search="true"
                                    v-bind:hide-selected="true"
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

                                    <template slot="noOptions">&nbsp</template>
                                    <template slot="noResult">&nbsp</template>

                                </multiselect>


                            </div>
                        </div>
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
                contrato: {},
                ciclos: [],
                categorias: [],
                situaciones: [],
                dedicaciones: []
            };
        },
        mounted: function () {
            this.allData();
        },
        methods: {
            open(contrato) {
                let $vue = this;
                $vue.contrato = {...contrato}
                Vue.delete($vue.contrato, 'estadoEnum');
                Vue.delete($vue.contrato, 'resolucionFacultad');
                Vue.delete($vue.contrato, 'resolucionConsejo');
                $vue.$refs.modalEditarContratoDocente.open();
            },
            saveContratoDocente() {
                let $vue = this;
                if (!$($vue.$refs.formContrato).parsley().validate()) {
                    return;
                }
                axios_.post("/academico/profesor/contrato/update/profesor", $vue.contrato)
                        .then(({data}) => {
                            notify(data, 'info');
                            $vue.$refs.modalEditarContratoDocente.close();
                            $vue.$parent.$refs.raptorContratos.loadRemoteData();
                        }, () => {
                            $vue.$refs.modalEditarContratoDocente.stop();
                        });
            },
            allData() {
                let $vue = this;
                axios_.get(`/academico/profesor/all/data/contrato`)
                        .then(({data}) => {
                            $vue.ciclos = data.ciclos;
                            $vue.categorias = data.categorias;
                            $vue.situaciones = data.situaciones;
                            $vue.dedicaciones = data.dedicaciones;
                        }, () => {
                        });
            }
        }
    };
</script>
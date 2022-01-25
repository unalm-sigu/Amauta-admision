<template>
    <div>

        <modal-simple ref="modalClonarConsejeros"
                      v-bind:okaction="clonarConsejeros">

            <div slot="header">
                <h4 class="modal-title">Clonación</h4>
            </div>

            <div slot="body">

                <form data-parsley-validate="true" method="POST" >

                    <div class="form-group">
                        <label>Modelo</label>
                        <multiselect
                            v-model="clonarConsejero.modelo"
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

                            <template slot="noOptions">&nbsp;</template>
                            <template slot="noResult">&nbsp;</template>

                        </multiselect>

                        <input type="text" class="hide"      v-model="clonarConsejero.modelo" required="true"/>

                    </div>

                    <div class="form-group">
                        <label>Destino</label>
                        <multiselect
                            v-model="clonarConsejero.destino"
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

                            <template slot="noOptions">&nbsp;</template>
                            <template slot="noResult">&nbsp;</template>

                        </multiselect>

                        <input type="text" class="hide"      v-model="clonarConsejero.destino" required="true"/>

                    </div>

                </form>


            </div>
        </modal-simple>


    </div>
</template>

<script>

    module.exports = {
        components: {
        },
        data() {
            return {
                ciclos: [],
                clonarConsejero: {}
            };
        },
        mounted: function () {
            let $vue = this;
            $vue.allCiclo();
        },
        methods: {
            open() {
                let $vue = this;
                $vue.clonarConsejero = {};
                $vue.$refs.modalClonarConsejeros.open();
            },
            clonarConsejeros() {
                let $vue = this;
                axios_.post(APP.url('consejeria/administracion/clonar'), $vue.clonarConsejero).then(({data}) => {
                    notify(data, "info");
                    $vue.$refs.modalClonarConsejeros.close();
                }, () => {
                    $vue.$refs.modalClonarConsejeros.stop();
                });
            },
            allCiclo() {
                let $vue = this;
                axios_.get(APP.url('consejeria/administracion/ciclo/all')).then(({data}) => {
                    $vue.ciclos = data
                }, () => {
                });
            }
        }
    };
</script>
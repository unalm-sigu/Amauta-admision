<template>
    <div>

        <modal-simple ref="modalCopiarContratoDocente"
                      v-bind:okaction="copiarContratoDocente">
            <div slot="header">
                <p class="h4 text-primary">Copiar Contrato Docente</p>
            </div>
            <div slot="body">

                <form data-parsley-validate="true">

                    <div class="form-group">
                        <label class="bold">Ciclo Académico Origen</label>

                        <multiselect v-model='cicloOrigen'
                                     label='descripcion'
                                     placeholder=" "
                                     deselect-label=" "
                                     select-label=" "
                                     track-by='id'
                                     v-bind:allow-empty="true"
                                     v-bind:options='ciclosOrigen'>
                        </multiselect>

                        <input required="true" class="hide" type="text" v-model="cicloOrigen" />


                    </div>

                    <div class="form-group">
                        <label class="bold">Ciclo Académico Destino</label>

                        <multiselect v-model='cicloDestino'
                                     label='descripcion'
                                     placeholder=" "
                                     deselect-label=" "
                                     select-label=" "
                                     track-by='id'
                                     v-bind:allow-empty="true"
                                     v-bind:options='ciclos'>
                        </multiselect>

                        <input required="true" class="hide" type="text" v-model="cicloDestino" />

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
                ciclos: [],
                ciclosOrigen: [],
                cicloOrigen: null,
                cicloDestino: null,
            };
        },
        mounted: function () {
            this.allCiclo();
        },
        methods: {
            open() {
                let vue = this;
                vue.cicloOrigen = null;
                vue.$refs.modalCopiarContratoDocente.open();
            },
            copiarContratoDocente() {
                let $vue = this;
                if ($vue.cicloOrigen.codigo >= $vue.cicloDestino.codigo) {
                    notify("El ciclo de origen no debe ser mayor o igual al ciclo de destino.");
                    $vue.$refs.modalCopiarContratoDocente.stop();
                    return;
                }
                axios_.get(`/academico/profesor/generar/${$vue.cicloOrigen.id}/${$vue.cicloDestino.id}`)
                        .then(({data}) => {
                            notify(data, 'info')
                            $vue.$refs.modalCopiarContratoDocente.close();
                        }, () => {
                            $vue.$refs.modalCopiarContratoDocente.stop();
                        });
            },
            allCiclo() {
                let $vue = this;
                axios_.get("/academico/profesor/all/data/contrato")
                        .then(({data}) => {
                            $vue.ciclosOrigen = data.ciclosOrigen;
                            $vue.ciclos = data.ciclos;
                        }, () => {
                        });
            }
        }
    };
</script>
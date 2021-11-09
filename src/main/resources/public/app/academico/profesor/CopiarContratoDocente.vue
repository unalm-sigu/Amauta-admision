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
                                     v-bind:options='ciclos'>
                        </multiselect>

                        <input required="true" class="hide" type="text" v-model="cicloOrigen" />


                    </div>

                    <div class="form-group">
                        <label class="bold">Ciclo Académico Destino</label>

                        <p class="form-control-static" >{{CICLO_ACADEMICO_DESCRIPCION}}</p>

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
                cicloOrigen: null,
                CICLO_ACADEMICO_DESCRIPCION: CICLO_ACADEMICO_DESCRIPCION
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
                axios_.post("/academico/profesor/generar/general", $vue.cicloOrigen)
                        .then(({data}) => {
                            notify(data, 'info')
                            $vue.$refs.modalCopiarContratoDocente.close();
                        }, () => {
                            $vue.$refs.modalCopiarContratoDocente.stop();
                        });
            },
            allCiclo() {
                let $vue = this;
                axios_.get("/academico/profesor/all/ciclo/contrato")
                        .then(({data}) => {
                            $vue.ciclos=data;
                        }, () => {
                        });
            }
        }
    };
</script>
<template>
    <div>

        <modal-simple ref="modalReporte"
                      v-bind:okaction="downloadReporte">
            <div slot="header">
                <p class="h4 text-primary">Reporte Entrega Materiales</p>
            </div>
            <div slot="body">
                <div class="form-group m-b-xs">
                    <label class="bold">Facultad</label>

                    <multiselect v-model='facultad'
                                 label='nombre'
                                 placeholder="Todas las facultades"
                                 deselect-label=""
                                 select-label=""
                                 track-by='id'
                                 v-bind:allow-empty="true"
                                 v-bind:options='facultades'>
                    </multiselect>

                </div>
            </div>
        </modal-simple>

    </div>
</template>

<script>
    module.exports = {
        data() {
            return {
                facultades: JSON.parse(jFacultades),
                facultad: null
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            open() {
                let vue = this;
                vue.facultad = null
                vue.$refs.modalReporte.open();
            },
            downloadReporte() {
                let vue = this;
                let data = {};
                if (vue.facultad != null) {
                    data = {params: {facultad: vue.facultad.id}};
                }

                axios_blob.get("/academico/profesor/reporteEntregaMateriales", data)
                        .then(response => {
                            UTIL_BLOB.save(response);
                            vue.$refs.modalReporte.close();
                        }, () => {
                            vue.$refs.modalReporte.stop();
                            notify(Messages.errorComunicacion, 'error')
                        });
            },
        }
    };
</script>
<template>
    <div>

        <section class="panel">

            <section class="panel-body m-t-sm">

                <h3 class="text-primary">Subir Fotos</h3><!-- comment -->

                <div class="row">
                    <div class="col-md-4">
                        <div class="form-group">
                            <label>Archivo</label>
                            <p v-on:click.prevent="triggerFileUpload" class="form-control pointer text-primary">
                                <span v-if="processUpload"><i class="fa fa-spinner fa-spin"></i></span> 
                                <span v-if="fileSuccesUpload" class="text-success"><i class="fa fa-check"></i> </span>
                                {{filename}} 
                            </p>

                            <input class="hide" placeholder="Selecione un archivo" v-on:change="uploadFoto" type="file" accept=".zip" ref="inputFile" />

                        </div>

                    </div>
                </div>

                <div class="row">
                    <div class="col-md-4">
                        <div class="form-group">
                            <button class="btn btn-info btn-sm"
                                    v-on:click.prevent="procesarFoto"
                                    v-bind:disabled='procesandoUpload' >
                                <span v-if="loaderUpload"><i class="fa fa-spinner fa-spin"></i></span>
                                Procesar Fotos
                            </button>
                        </div>
                    </div>
                </div>

                <div class="col-md-10 col-md-offset-0">

                    <div class="col-xs-12 m-t-xl">

                        <div>
                            <p class="pull-right"> <span v-text='info.perAvance'></span>%</p>
                            <p>Fotos ( <span v-text='info.avance'></span> / <span v-text='info.cantidadTotal'></span>  )</p>
                        </div>

                        <vue-simple-progress size="medium"  v-bind:val="info.perAvance" v-bind:text="info.perAvance"></vue-simple-progress>

                    </div>

                </div>

            </section>
        </section>

    </div>
</template>

<script>

    module.exports = {
        data() {
            return {
                info: {},
                filename: 'Seleccione ...',
                fileSuccesUpload: false,
                processUpload: false,
                loaderUpload: false,
                procesandoUpload: false,
            };
        },
        methods: {
            triggerFileUpload() {
                let $vue = this;
                $vue.$refs.inputFile.click();
            },
            uploadFoto() {
                let $vue = this;
                $vue.fileSuccesUpload = false;
                $vue.processUpload = true;
                $vue.procesandoUpload = true;
                $vue.filename = $vue.$refs.inputFile.files[0].name;
                let formData = new FormData();
                formData.append('file', $vue.$refs.inputFile.files[0]);
                AXIOS.post('/comun/archivo/upload', formData)
                        .then(response => {
                            console.log(response);
                            $vue.fileSuccesUpload = true;
                            $vue.processUpload = false;
                            $vue.procesandoUpload = false;
                        }, () => {
                            $vue.fileSuccesUpload = false;
                            $vue.processUpload = false;
                            $vue.procesandoUpload = false;
                        });
            },
            procesarFoto() {
                let $vue = this;
            }
        },
    };
</script>
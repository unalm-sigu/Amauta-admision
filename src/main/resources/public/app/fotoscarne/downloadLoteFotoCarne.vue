<template>
    <div>

        <section class="panel">

            <section class="panel-body m-t-sm">

                <h3 class="text-primary">Descargar Fotos en Lote</h3>

                <form id="formDownloadLoteFoto" data-parsley-validate="true" method="POST">

                    <div class="row">
                        <div class="col-md-4">
                            <div class="form-group">
                                <label>Alumnos</label>

                                <input class="form-control" v-model='codigos' required="true"/>
                                
                            </div>
                        </div>
                    </div>


                </form>


                <div class="row">
                    <div class="col-md-4">
                        <div class="form-group">
                            <button class="btn btn-success"
                                    v-on:click.prevent="descargarFoto"
                                    v-bind:disabled='procesando' >
                                <span v-if="procesando"><i class="fa fa-spinner fa-spin"></i></span>
                                  Descargar Archivo
                            </button>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-xs-12">

                        <div class="h4">
                            <p class="pull-right"> <span v-text='info.perAvance'></span>%</p>
                            <p>Fotos ( <span v-text='info.avance'></span> / <span v-text='info.total'></span>  )</p>
                        </div>

                        <vue-simple-progress size="large"  v-bind:val="info.perAvance" v-bind:text="info.perAvance"></vue-simple-progress>

                    </div>
                </div>


                <div v-if="info.pathFile" class="row" style="margin-top:30px;">
                    <div class="col-xs-12">
                        <table class="table table-streped">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Error</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="(error, i) in info.errores" v-bind:key="i">
                                    <td>{{i+1}}</td>
                                    <td>{{error.mensaje}}</td>
                                </tr>
                            </tbody>
                        </table>
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
                carrera: null,
                carreras: [],
                modalidad: '',
                modalidades: JSON.parse(MODALIDADES_JSON),
                info: {perAvance: 0},
                procesando: true
            };
        },
        mounted: function () {
            let $vue = this;
            $vue.obtenerInfo();
        },
        methods: {
            descargarFoto() {
                let $vue = this;
                console.log($("#formDownloadLoteFoto").parsley().validate());
                if ($("#formDownloadLoteFoto").parsley().validate() != true) {
                    return;
                }
                $vue.procesando = true;
                axios_blob.post(APP.url('fotos/carne/descargarLote'),{codigos:$vue.codigos})
                        .then(response => {
                            UTIL_BLOB.save(response);
                            $vue.procesando = false;
                        }, () => {
                            $vue.procesando = false;
                            notify(Messages.errorComunicacion, 'error')
                        });
            },
            obtenerInfo() {
                let $vue = this;
                axios.get(APP.url('fotos/carne/infoDownLote'))
                        .then(response => {
                            $vue.info = response.data;
                              $vue.procesando= $vue.info.isIniciado;
                            setTimeout($vue.obtenerInfo, 3000);
                        }, () => {
                            notify(response.message, "error");
                        });
            }
        }
    };
</script>
<template>
    <div>

        <section class="panel">

            <section class="panel-body m-t-sm">

                <h3 class="text-primary">Descargar Fotos</h3>

                <form id="formDownloadFoto" data-parsley-validate="true" method="POST">

                    <div class="row">
                        <div class="col-md-4">

                            <div class="form-group">
                                <label>Modalidades</label>
                                <multiselect v-model='modalidad'
                                             v-bind:options='modalidades'
                                             v-bind:allow-empty="false"
                                             label='nombre'
                                             placeholder=" "
                                             select-label=" "
                                             track-by='id'
                                             v-on:input="carrerasByCarrera(modalidad.codigo)">
                                </multiselect>
                                <input class="hide" v-model='modalidad' required="true"/>
                            </div>

                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-4">
                            <div class="form-group">
                                <label>Carreras</label>
                                <multiselect v-model='carrera'
                                             v-bind:options='carreras'
                                             v-bind:allow-empty="true"
                                             label='nombre'
                                             placeholder=" "
                                             select-label=" "
                                             track-by='id'>
                                </multiselect>
                                <input class="hide" v-model='carrera' required="true"/>
                            </div>
                        </div>
                    </div>


                </form>


                <div class="row">
                    <div class="col-md-4">
                        <div class="form-group">
                            <button class="btn btn-success"
                                    v-on:click.prevent="compilarFoto"
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
            compilarFoto() {
                let $vue = this;
                console.log($("#formDownloadFoto").parsley().validate());
                if ($("#formDownloadFoto").parsley().validate() != true) {
                    return;
                }
                $vue.procesando = true;
                axios.get(APP.url('fotos/carne/compilarInformacion/' + $vue.carrera.codigo))
                        .then(() => {
                            $vue.descargarFoto();
                        }, () => {
                            notify(response.message, "error");
                        });
            },
            descargarFoto() {
                let $vue = this;
                $vue.procesando = true;
                axios_blob.get(APP.url('fotos/carne/descargarFotos'))
                        .then(response => {
                            UTIL_BLOB.save(response);
                            $vue.procesando = false;
                        }, () => {
                            $vue.procesando = false;
                            notify(Messages.errorComunicacion, 'error')
                        });
            },
            carrerasByCarrera(filtroModalidad) {
                let $vue = this;
                $vue.carrera = null;
                axios.get('/fotos/carne/allCarreraByModalidad/' + filtroModalidad)
                        .then(response => {
                            if (response.data.success) {
                                $vue.carreras = response.data.data;
                            }
                        }, () => {
                            notify(response.message, "error");
                        });
            },
            obtenerInfo() {
                let $vue = this;
                axios.get(APP.url('fotos/carne/infoDown'))
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
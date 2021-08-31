<template>
    <div>

        <modal-simple ref="modalCostoDocumentoForm"
                      v-bind:okaction="saveCostoDocumento">

            <div slot="header">
                <h4 class="modal-title" id="myModalLabel"> Costo Documento </h4>
            </div>

            <div slot="body">

                <form id="formConfig">

                    <div class="col-md-offset-1 ">
                        <div class='form-group row'>
                            <label class="col-sm-3 control-label">Tipo constancia</label>
                            <div class="col-sm-8">
                                <div class="form-group" v-if="costoDocumento.id">   
                                    <span class="form-control ">
                                        {{costoDocumento.tipoDocumento.tipo.value}}
                                        {{costoDocumento.tipoDocumento != undefined ? costoDocumento.tipoDocumento.nombre : ''}}
                                    </span>
                                </div>
                                <div class="form-group" v-else="">                                
                                    <multiselect v-model="costoDocumento.tipoDocumento" 
                                                 v-bind:options='tipoConstancia'
                                                 label='nombre'
                                                 track-by='id'
                                                 v-bind:searchable="true" 
                                                 v-bind:allow-empty="false"
                                                 placeholder="Seleccione un tipo de constancia"
                                                 v-bind:close-on-select="true"
                                                 v-bind:preserve-search="true"
                                                 v-bind:custom-label="nameWithCodeEspecial" 
                                                 class="tipoDocumento"
                                                 >
                                        <template slot="option" slot-scope="props">    
                                            <div class="option__desc">
                                                <div >
                                                    <span class="bold  option__small">
                                                        {{ props.option.nombre }}
                                                    </span>
                                                </div>                                                       
                                            </div>
                                        </template>
                                    </multiselect>
                                </div>

                            </div>  
                        </div> 

                        <div class='form-group row'>
                            <label class="col-sm-3 control-label">Idioma</label>
                            <div class="col-sm-8">

                                <div class="form-group" v-if="costoDocumento.id">   
                                    <span v-text="costoDocumento.idioma != undefined ? costoDocumento.idioma.nombre : ''" class="form-control "></span>
                                </div>
                                <div class="form-group" v-else="">                                
                                    <multiselect v-model="costoDocumento.idioma" 
                                                 v-bind:options='idiomas'
                                                 label='nombre'
                                                 track-by='id'
                                                 placeholder="Seleccione un tipo de constancia"
                                                 >
                                    </multiselect>

                                </div>

                            </div>  
                        </div>   

                        <div class='form-group row'>
                            <label class="col-sm-3 control-label">Precio</label>
                            <div class="col-sm-8">
                                <input  required="true" v-model="costoDocumento.precio" type="text" class="form-control numerico " placeholder="Ingrese un precio"/>
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
        computed: {
            ...Vuex.mapState(["costoDocumento"])
        },
        data() {
            return {
                tipoConstancia: JSON.parse(tipoDocumentoJson),
                idiomas: JSON.parse(idiomasJson),
                isNew: true,
                isOld: false,
            };
        },
        mounted: function () {
            let $vue = this;
            $('.numerico').numeric();
        },
        methods: {
            saveCostoDocumento() {

                let $vue = this;

                $(".mx-input").attr("required", true);

                if (!$("#formConfig").parsley().validate()) {
                    $vue.$refs.modalCostoDocumentoForm.stop();
                    return;
                }

                $vue.costoDocumento.tipoDocumento.tipo = $vue.costoDocumento.tipoDocumento.tipo.name;
                $vue.costoDocumento.tipoDocumento.costoCiclo = $vue.costoDocumento.tipoDocumento.costoCiclo == true ? 1 : 0;

                let url = APP.url('tramite/costodocumento/save');

                if ($vue.costoDocumento.id) {
                    url = APP.url('tramite/costodocumento/update');
                }

                $.ajax({
                    method: 'POST',
                    url: url,
                    contentType: "application/json",
                    data: JSON.stringify($vue.costoDocumento),
                    success: function (response) {

                        if (response.success) {

                            $vue.$refs.modalCostoDocumentoForm.close();
                            $vue.$parent.reload();
                            notify(response.message, 'info');

                        } else {

                            $vue.$refs.modalCostoDocumentoForm.stop();
                            notify(response.message, 'error');

                        }
                    }
                });

            },
            open() {
                let $vue = this;
                $vue.$refs.modalCostoDocumentoForm.open();
            }
        }
    };
</script>
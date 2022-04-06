<template>
    <div>

        <modal-simple ref="modalCostoDocumentoForm"
                      v-bind:okaction="saveCostoDocumento">

            <div slot="header">
                <h4 class="modal-title" id="myModalLabel"> Costo Documento </h4>
            </div>

            <div slot="body">

                <form id="formConfig"  data-parsley-validate="true" >

                    <div class="col-md-offset-1 ">
                        <div class='form-group row'>
                            <label class="col-sm-3 control-label">Tipo constancia</label>
                            <div class="col-sm-8">
                                <div class="form-group" v-if="costoDocumento.id">   
                                    <span v-if="costoDocumento.tipoDocumento" class="form-control ">
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
                                                 placeholder=" "
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

                                    <input  required="true" v-model="costoDocumento.tipoDocumento" 
                                            type="text" class="hide"  />
                                </div>

                            </div>  
                        </div> 

                        <div class='form-group row'>
                            <label class="col-sm-3 control-label">Idioma</label>
                            <div class="col-sm-8">

                                <div class="form-group" v-if="costoDocumento.id">   
                                    <span v-if="costoDocumento.idioma " v-text="costoDocumento.idioma.nombre" class="form-control "></span>
                                </div>
                                <div class="form-group" v-else="">                                
                                    <multiselect v-model="costoDocumento.idioma" 
                                                 v-bind:options='idiomas'
                                                 label='nombre'
                                                 track-by='id'
                                                 placeholder=" "
                                                 >
                                    </multiselect>

                                    <input  required="true" v-model="costoDocumento.idioma" 
                                            type="text" class="hide"  />

                                </div>

                            </div>  
                        </div>   

                        <div class='form-group row'>
                            <label class="col-sm-3 control-label">Precio</label>
                            <div class="col-sm-8">
                                <input  required="true" v-model="costoDocumento.precio" 
                                        type="text" class="form-control numerico " 
                                        />
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
        components: {
            ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        },
        data() {
            return {
                tipoConstancia: JSON.parse(tipoDocumentoJson),
                idiomas: JSON.parse(idiomasJson),
                isNew: true,
                isOld: false,
                costoDocumento: {}
            };
        },
        mounted: function () {
            let $vue = this;
            $('.numerico').numeric();
        },
        methods: {
            saveCostoDocumento() {

                let $vue = this;

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
            },
            setCostoDocumento(costoDocumento) {
                let $vue = this;
                $vue.costoDocumento = costoDocumento;
            }
        }
    };
</script>
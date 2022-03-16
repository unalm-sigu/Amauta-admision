<template>
    <div>

        <modal-simple ref="modalAddTipoConstancia"
                      v-bind="addTipoConstanciaModal"
                      okbtn='Agregar Tipo'
                      v-bind:okaction="save">

            <div slot="header" >
                <h4 class="modal-title">Nuevo Tipo Documento Académico</h4>
            </div>

            <div slot="body" >

                <form id="formTipoConstancia" class="form-horizontal"  data-parsley-validate="true">

                    <div class='form-group row'>
                        <label class="col-sm-3 control-label">Nombre</label>
                        <div class="col-sm-6">
                            <input name="nombre"  required="true" v-model="tipoConstancia.nombre" type="text" class="form-control " />
                        </div>
                    </div>

                    <div class='form-group row'>
                        <label class="col-sm-3 control-label">Código</label>
                        <div class="col-sm-6">
                            <input name="codigo"  required="true" v-model="tipoConstancia.codigo" type="text" class="form-control " />
                        </div>
                    </div>

                    <div class='form-group'>
                        <label class="col-sm-3 control-label">Tipo</label>
                        <div class="col-sm-6">
                            <select name="tipo" class="form-control"   required="true"  v-model="tipoConstancia.tipo">
                                <option v-for="tipo in tipos"   v-bind:value="tipo.name" v-text="tipo.value"  ></option>
                            </select>
                        </div>
                    </div>


                    <div class='form-group' >
                        <label class="col-sm-3 control-label">Costo Ciclo</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" name="costoCiclo" value="1" true-value="1" false-value="0"  v-model="tipoConstancia.costoCiclo" ></input>
                                <span></span>
                            </label>
                        </div>
                    </div>

                    <div class='form-group' >
                        <label class="col-sm-3 control-label">Requiere Foto</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" name="requiereFoto" value="1" true-value="1" false-value="0"  v-model="tipoConstancia.requiereFoto" ></input>
                                <span></span>
                            </label>
                        </div>
                    </div>

                    <div class='form-group' >
                        <label class="col-sm-3 control-label">Solo Pregrado</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" name="requierePregrado" value="1" true-value="1" false-value="0"  v-model="tipoConstancia.requierePregrado" ></input>
                                <span></span>
                            </label>
                        </div>
                    </div>

                    <div class='form-group' >
                        <label class="col-sm-3 control-label">Solo Posgrado</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" name="requierePosgrado" value="1" true-value="1" false-value="0"  v-model="tipoConstancia.requierePosgrado" ></input>
                                <span></span>
                            </label>
                        </div>
                    </div>

                    <div class='form-group' >
                        <label class="col-sm-3 control-label">Solo Egresado</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" name="requiereEgresado" value="1" true-value="1" false-value="0"  v-model="tipoConstancia.requiereEgresado" ></input>
                                <span></span>
                            </label>
                        </div>
                    </div>

                    <div class='form-group' >
                        <label class="col-sm-3 control-label">Configurado</label>
                        <div class="col-sm-9">
                            <label class="switch">
                                <input type="checkbox" name="requiereEgresado" value="1" true-value="1" false-value="0"  v-model="tipoConstancia.configurado" ></input>
                                <span></span>
                            </label>
                        </div>
                    </div>

                    <!--                    <div class='form-group' >
                                            <label class="col-sm-3 control-label">Oficina Emisora</label>
                                            <div class="col-sm-9">
                                                <multiselect
                                                    v-model="tipoConstancia.oficinaEmisora"
                                                    v-bind:options="oficinas"
                                                    v-bind:allow-empty="true"
                                                    track-by="id"
                                                    placeholder=" "
                                                    label='nombre'
                                                    v-bind:internal-search="true"
                                                    v-bind:hide-selected="false"
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
                    
                                                    <template slot="noOptions">&nbsp;</template>
                                                    <template slot="noResult">&nbsp;</template>
                    
                                                </multiselect>
                                            </div>
                                        </div>-->

                </form>
            </div>
        </modal-simple>


    </div>
</template>

<script>
    module.exports = {
        data() {
            return {
                tipos: JSON.parse(tiposJson),
                tipoConstancia: {tipo: {}},
            };
        },
        methods: {
            updateTipo: function (tipoConstancia) {
                let $vue = this;
                $vue.tipoConstancia = {tipo: {}};
                $("#formTipoConstancia").parsley().destroy();
                axios_.post(APP.url('tramite/tipoconstancia/find/' + tipoConstancia.id))
                        .then(({data}) => {
                            $vue.tipoConstancia = data;
                            $vue.$refs.modalAddTipoConstancia.open();
                        }, () => null);
            },
            nuevo: function () {
                let $vue = this;
                $vue.tipoConstancia = {tipo: {}};
                $vue.$refs.modalAddTipoConstancia.open();
            },
            save: function (e) {
                let $vue = this;
                axios_.post(APP.url('tramite/tipoconstancia/save'), $vue.tipoConstancia)
                        .then(({data}) => {
                            console.log('save')
                            $global.$emit("RELOAD");
                            notify(data, 'info');
                            $vue.$refs.modalAddTipoConstancia.close();
                        }, () => {
                            $vue.$refs.modalAddTipoConstancia.stop();
                        });
            },
        }
    };
</script>
<template>
    <div>

        <div class="row">
            <div class="col-lg-6">
                <label>Facultad</label>
                <div class="form-group">
                    <multiselect 
                        v-model="resolucion.oficina" 
                        v-bind:options="oficinas"
                        placeholder=" "
                        label="nombre"
                        v-bind:allow-empty="false"
                        track-by="id" 
                        required="true"
                        v-bind:showNoOptions="true"
                        v-bind:show-labels="false"
                        v-on:select="oficinaSelect" >

                        <template slot="singleLabel" slot-scope="props">
                            <span class="option__title">
                                {{props.option.nombre}}
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

                    <input v-model="resolucion.oficina" required="true" type="text" class="hide"/>

                </div>
            </div> 
            <div class="col-lg-6">
                <div class="form-group">
                    <label>Fecha Resolución</label>
                    <div class="input-group date">
                        <date-picker v-model="resolucion.fecha" 
                                     required="true" 
                                     v-bind:config="configDate" 
                                     data-parsley-id="2" 
                                     v-bind:wrap="true" >
                        </date-picker>  
                        <div class="input-group-addon">
                            <span class="fa fa-calendar"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-lg-6">
                <label>Serie</label>
                <div class="form-group">
                    <input type="text"
                           v-model="resolucion.serie"
                           maxlength="10"
                           class="form-control numerico" 
                           required="true"></input>
                </div>
            </div>
            <div class="col-lg-6">
                <label>Número</label>
                <div class="form-group">
                    <input type="text"
                           maxlength="10"
                           v-model="resolucion.numero"
                           class="form-control numerico" 
                           required="true"> </input>
                </div>
            </div>
        </div>
        <div class="row">
            
            <div class="col-lg-6">
                <label>Tipo Resolución</label>
                <div class="form-group">
                    <multiselect 
                        v-model="resolucion.tipoResolucion" 
                        v-bind:options="tiposResolucion"
                        placeholder=" "
                        label="nombre"
                        v-bind:allow-empty="false"
                        deselect-label="No se puede eliminar este valor"
                        track-by="id" 
                        required="true"
                        v-on:select="tipoResolucionSelect"
                        v-bind:disabled="isEdicion"
                        >
                    </multiselect>
                    <input v-model="resolucion.tipoResolucion" required="true" type="text" class="hide"/>
                </div>
            </div> 

            <div class="col-lg-6" v-if="isCambioNota||isCursoDirigido">
                <label>Ciclo 
                    <span v-if='isRetiroCiclo'> a retirar</span>
                    <span v-if='isReincorporacion'> a reincorporar</span>
                </label>
                <div class="form-group">
                    <multiselect 
                        v-model="resolucion.cicloAplica" 
                        v-bind:options="ciclos"
                        placeholder=" "
                        label="descripcion"
                        track-by="id" 
                        required="true"
                        v-bind:allow-empty="true"
                        deselect-label="No se puede eliminar este valor" 
                        v-bind:disabled="isEdicion">
                    </multiselect>
                    <input v-model="resolucion.cicloAplica" required="true" type="text" class="hide"/>
                </div>
            </div>

        </div>

    </div>
</template>

<script>
    module.exports = {
        data() {
            return {
                oficinas: JSON.parse(oficinasJson),
                ciclos: JSON.parse(ciclosJson),
                tiposResolucion: JSON.parse(tiposResolucionJson),
                carreras: JSON.parse(carrerasJson),
                configDate: {
                    format: 'DD/MM/YYYY',
                    useCurrent: false
                },
            };
        },
        computed: {
            ...Vuex.mapState(["resolucion"])
        },
        mounted: function () {
            let $vue = this;
            $(".numerico").numeric({negative: false});
        },
        methods: {
            oficinaSelect(ofi) {
                let $vue = this;
                if ($vue.resolucion.oficina != null) {
                    if (ofi.id != $vue.resolucion.oficina.id) {
                        $vue.resolucion.reincorporaciones = [];
                        $vue.alumnos = [];
                    }
                }
            },
            tipoResolucionSelect() {

            },
        }
    };
</script>
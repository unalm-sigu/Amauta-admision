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
                        v-bind:show-labels="false" >

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
                        >
                    </multiselect>
<!--                                            v-bind:disabled="isEdicion"-->
                    <input v-model="resolucion.tipoResolucion" required="true" type="text" class="hide"/>
                </div>
            </div> 

            <div v-if="resolucion.tipoResolucion">
                <div class="col-lg-6" 
                     v-if="!(resolucion.tipoResolucion.isTrasladoExterno||
                     resolucion.tipoResolucion.isIngresoFisicoHistorial||
                     resolucion.tipoResolucion.isTramiteBachiller||
                     resolucion.tipoResolucion.isTramiteTitulo||
                     resolucion.tipoResolucion.isCursoDirigido||
                     resolucion.tipoResolucion.isIngresoFisicoHistorial||
                     resolucion.tipoResolucion.isTramitePracticas||
                     resolucion.tipoResolucion.isAlumRenunciante||
                     resolucion.tipoResolucion.isAlumRenuncianteCarrera||
                     resolucion.tipoResolucion.isTramiteBachillerFacultad||
                     resolucion.tipoResolucion.isTramiteTituloFacultad)">
                    <label>
                        Ciclo Aplica
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

    </div>
</template>

<script>
    module.exports = {
        props: {
            resolucion: {type: Object, default: {}},
        },
        data() {
            return {
                oficinas: JSON.parse(oficinasJson),
                ciclos: JSON.parse(ciclosJson),
                isEdicion: IS_EDICION,
                tiposResolucion: JSON.parse(tiposResolucionJson),
                configDate: {
                    format: 'DD/MM/YYYY',
                    useCurrent: false
                },
            };
        },
        mounted: function () {
            let $vue = this;
            $(".numerico").numeric({negative: false});
        },
        methods: {
            oficinaSelect(ofi) {
                let $vue = this;
                if ($vue.resolucion.oficina) {
                    if (ofi.id != $vue.resolucion.oficina.id) {
                        $vue.resolucion.reincorporaciones = [];
                    }
                }
            },
        }
    };
</script>
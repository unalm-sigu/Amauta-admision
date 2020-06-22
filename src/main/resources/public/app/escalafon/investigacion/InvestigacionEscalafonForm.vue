<template>
    <modal-vik  v-bind:showaccept="true" id="investigacionEscalafonModal" ref="investigacionEscalafonModal" v-bind:okaction="save">
        <template v-slot:body>
            <form id="form-validar-investigacion-escalafon">
                <div class="form-group">
                    <label>Título</label>
                    <textarea type="text" required="true" class="form-control" rows="2" v-model="investigacionEscalafon.titulo" ></textarea>
                </div>
                <div class="form-group">
                    <label>Investigadores</label>
                    <textarea type="text" required="true" class="form-control" rows="2" v-model="investigacionEscalafon.investigadores" ></textarea>
                </div>
                <div class="form-group">
                    <label>Área de Investigación</label>
                    <multiselect  
                        v-model="investigacionEscalafon.area"
                        label='descripcion'
                        track-by='id'
                        v-bind:options='listAreaInvestigacion'
                        placeholder="Seleccione el área"
                        v-bind:allow-empty="false"
                        v-bind:show-labels="false"
                        v-bind:hide-selected="false">              
                        <template slot="noOptions">La lista se encuentra vacía</template>
                        <template slot="noResult">No se encontraron resultados</template>
                    </multiselect>                 
                    <input type="text" required="true" class="hide" v-model="investigacionEscalafon.area"/>      
                </div>
                <div class="form-group">
                    <label>Repositorio</label>
                    <input type="text" class="form-control" v-model="investigacionEscalafon.urlRepositorio"/>
                </div>
                <div class="form-group">
                    <label>Fecha Inicio (día/mes/año)</label> PENDIENTE
                    <!--                    <div class="input-group">
                                            <date-picker 
                                                style="height: 40px;"
                                                v-bind:config="configDate"
                                                class="float-left"
                                                v-model="investigacionEscalafon.fechaInicio">
                                            </date-picker>                  
                                            <div class="input-group-append">
                                                <span class="input-group-text align-middle">
                                                    <i class="fas fa-calendar-alt"></i>
                                                </span>
                                            </div>
                                        </div>-->
                </div>
                <div class="form-group">
                    <label>Fecha Fin (día/mes/año)</label> PENDIENTE
                    <!--                    <div class="input-group">
                                            <date-picker 
                                                style="height: 40px;"
                                                v-bind:config="configDate"
                                                class="float-left"
                                                v-model="investigacionEscalafon.fechaFin">
                                            </date-picker>                  
                                            <div class="input-group-append">
                                                <span class="input-group-text align-middle">
                                                    <i class="fas fa-calendar-alt"></i>
                                                </span>
                                            </div>
                                        </div>-->
                </div>
            </form>
        </template>
    </modal-vik>
</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component('date-picker', VueBootstrapDatetimePicker.default);

    
    module.exports = {
       
        data() {
            return{
                investigacionEscalafon: {pais: null},
                listAreaInvestigacion: JSON.parse(listAreaInvestigacionJson),
                rutaModulo: "/escalafon/investigacion",
                configDate: CONFIG_DATE
            };
        },
        computed: {
            escalafon() {
                return this.$store.state.escalafon;
            }
        },
        methods: {
            open(item) {
                let $vue = this;
                $vue.investigacionEscalafon = {escalafon: {id: $vue.escalafon.id}, area: null};
                if (item.id != null) {
                    $vue.investigacionEscalafon = {...item};
                }
                $vue.$refs.investigacionEscalafonModal.open();
            },
            save() {
                let $vue = this;
                if (!$("#form-validar-investigacion-escalafon").parsley().validate()) {
                    return;
                }
                axios.post($vue.rutaModulo + "/save", $vue.investigacionEscalafon)
                        .then(function (response) {
                            if (response.data.success) {
                                notify(response.data.message, "success");
                                $vue.$parent.loadList();
                                $vue.$refs.investigacionEscalafonModal.close();
                            } else {
                                notify(response.data.message, "warning");
                            }
                        })
                        .catch(function (error) {
                            notify(error.errorComunicacion, "error");
                        });
            }
        }
    };
</script>

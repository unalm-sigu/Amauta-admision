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
                    <label>Fecha Inicio (día/mes/año)</label>
                    <div class="input-group date">
                        <input type="date"
                               id="fechaInicio"
                               class="form-control"
                               v-on:input="getFormatFecha"
                               required="true" />
                        <span class="input-group-addon">
                            <i class="fa fa-calendar" aria-hidden="true"></i>
                        </span>
                    </div>
                </div>
                <div class="form-group">
                    <label>Fecha Fin (día/mes/año)</label>
                    <div class="input-group date">
                        <input type="date"
                               id="fechaFin"
                               class="form-control"
                               v-on:input="getFormatFecha"
                               required="true" />
                        <span class="input-group-addon">
                            <i class="fa fa-calendar" aria-hidden="true"></i>
                        </span>
                    </div>
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
            setFechaInput(fechaParam, fechaModel) {
                if (fechaParam == null) {
                    return;
                }
                let day = fechaParam.substr(0, 2);
                let mount = fechaParam.substr(3, 2);
                let year = fechaParam.substr(6, 5);
                document.getElementById(fechaModel).value = year + "-" + mount + "-" + day;
            },
            getFormatFecha() {
                let fechaInicioMoment = moment($('#fechaInicio').val());
                let fechaFinMoment = moment($('#fechaFin').val());
                this.investigacionEscalafon.fechaInicio = fechaInicioMoment.format("DD/MM/YYYY");
                this.investigacionEscalafon.fechaFin = fechaFinMoment.format("DD/MM/YYYY");
            },
            open(item) {
                let $vue = this;
                $('#form-validar-investigacion-escalafon').parsley().destroy();
                document.getElementById('fechaInicio').value = null;
                document.getElementById('fechaFin').value = null;
                $vue.investigacionEscalafon = {escalafon: {id: $vue.escalafon.id}, area: null};
                if (item.id != null) {
                    $vue.investigacionEscalafon = {...item};
                    $vue.setFechaInput($vue.investigacionEscalafon.fechaInicio, "fechaInicio");
                    $vue.setFechaInput($vue.investigacionEscalafon.fechaFin, "fechaFin");
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

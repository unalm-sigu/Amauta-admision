<template>
    <modal-vik  v-bind:showaccept="true" id="experienciaEscalafonModal" ref="experienciaEscalafonModal" v-bind:okaction="save">
        <template v-slot:body>
            <form id="form-validar-experiencia-escalafon">
                <div class="form-group">
                    <label>Tipo Experiencia</label>
                    <div class="col-md-12">
                        <div class="col-sm-5">
                            <label class="radio inline">
                                <input type="radio" required="true" value="DOCENTE" v-model="experienciaEscalafon.tipo"/> Docente
                            </label>
                        </div>
                        <div class="col-sm-5">
                            <label class="radio inline">
                                <input type="radio" required="true" value="EXPERIENCIA" v-model="experienciaEscalafon.tipo"/> Experiencia Normal
                            </label>
                        </div>
                    </div>
                    <input type="text" required="true" class="hide" v-model="experienciaEscalafon.tipo"/>  
                </div>
                <div class="form-group" v-if="experienciaEscalafon.tipo == 'DOCENTE'">
                    <label>Universidad</label>
                    <multiselect  
                        v-model="experienciaEscalafon.universidad"
                        label='nombre'
                        track-by='id'
                        v-bind:options='listUniversidad'
                        placeholder="Seleccione la universidad"
                        v-on:search-change="searchUniversidad"
                        v-bind:allow-empty="false"
                        v-bind:show-labels="false"
                        v-bind:hide-selected="false">              
                        <template slot="noOptions">La lista se encuentra vacía</template>
                        <template slot="noResult">No se encontraron resultados</template>
                    </multiselect>                 
                    <input type="text" required="true" class="hide" v-model="experienciaEscalafon.universidad"/>  
                </div>
                <div class="form-group" v-if="experienciaEscalafon.tipo == 'EXPERIENCIA'">
                    <label>Institución</label>
                    <input type="text" class="form-control"  required="true" v-model="experienciaEscalafon.institucion"/>
                </div>
                <div class="form-group" v-if="experienciaEscalafon.tipo == 'DOCENTE'">
                    <label>Tipo Docente</label>
                    <multiselect
                        v-model="experienciaEscalafon.tipoDocente"
                        placeholder="Seleccionar el tipo de docente"
                        v-bind:options="listTipoDocenteEnum"
                        track-by="name"
                        label="descripcion"
                        v-bind:show-labels="false"
                        v-bind:allow-empty="false">                                 
                    </multiselect>       
                    <input type="text" required="true" class="hide" v-model="experienciaEscalafon.tipoDocente"/>         
                </div>
                <div class="form-group">
                    <label>Cargo</label>
                    <input type="text" class="form-control"  required="true" v-model="experienciaEscalafon.cargo"/>
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
                listTipoDocenteEnum: JSON.parse(listTipoDocenteEnumJson),
                experienciaEscalafon: {universidad: null},
                listUniversidad: [],
                rutaModulo: "/escalafon/experiencia",
                configDate: CONFIG_DATE
            };
        },
        computed: {
            escalafon() {
                return this.$store.state.escalafon;
            }
        },
        mounted() {
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
                this.experienciaEscalafon.fechaInicio = fechaInicioMoment.format("DD/MM/YYYY");
                this.experienciaEscalafon.fechaFin = fechaFinMoment.format("DD/MM/YYYY");
            },
            open(item) {
                let $vue = this;
                $('#form-validar-experiencia-escalafon').parsley().destroy();
                document.getElementById('fechaInicio').value = null;
                document.getElementById('fechaFin').value = null;
                $vue.experienciaEscalafon = {escalafon: {id: $vue.escalafon.id}, universidad: null, tipoDocente: null};
                if (item.id != null) {
                    $vue.experienciaEscalafon = {...item};
                    if ($vue.experienciaEscalafon.universidad.id == null) {
                        $vue.experienciaEscalafon.universidad = null;
                    } else {
                        $vue.experienciaEscalafon.tipoDocente = $vue.listTipoDocenteEnum.find(item => item.name == $vue.experienciaEscalafon.tipoDocente);
                    }
                    $vue.setFechaInput($vue.experienciaEscalafon.fechaInicio, "fechaInicio");
                    $vue.setFechaInput($vue.experienciaEscalafon.fechaFin, "fechaFin");
                }
                $vue.$refs.experienciaEscalafonModal.open();
            },
            searchUniversidad(nombre) {
                let $vue = this;
                if (nombre == null || nombre.trim().length == 0) {
                    return;
                }
                $vue.listUniversidad = [];
                axios.get("/comun/buscar/allUniversidad", {params: {nombre: nombre}})
                        .then(response => {
                            $vue.listUniversidad = response.data.data;
                        });
            },
            save() {
                let $vue = this;
                if (!$("#form-validar-experiencia-escalafon").parsley().validate()) {
                    return;
                }
                let item = Object.assign({}, $vue.experienciaEscalafon);
                if (item.tipo == "DOCENTE") {
                    item.tipoDocente = item.tipoDocente.name;
                } else {
                    item.tipoDocente = null;
                }
                axios.post($vue.rutaModulo + "/save", item)
                        .then(function (response) {
                            if (response.data.success) {
                                notify(response.data.message, "success");
                                $vue.$parent.loadList();
                                $vue.$refs.experienciaEscalafonModal.close();
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

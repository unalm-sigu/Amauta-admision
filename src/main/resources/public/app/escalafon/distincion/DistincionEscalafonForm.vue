<template>
    <modal-vik  v-bind:showaccept="true" id="distincionEscalafonModal" ref="distincionEscalafonModal" v-bind:okaction="save">
        <template v-slot:body>
            <form id="form-validar-distincion-escalafon">
                <div class="form-group">
                    <label>Distinción</label>
                    <textarea type="text" required="true" class="form-control" rows="2" v-model="distincionEscalafon.titulo" ></textarea>
                </div>
                <div class="form-group">
                    <label>País</label>
                    <multiselect  
                        v-model="distincionEscalafon.pais"
                        label='nombre'
                        track-by='id'
                        v-bind:options='listPais'
                        placeholder="Seleccione el país"
                        v-on:search-change="searchPais"
                        v-bind:allow-empty="false"
                        v-bind:show-labels="false"
                        v-bind:hide-selected="false">              
                        <template slot="noOptions">La lista se encuentra vacía</template>
                        <template slot="noResult">No se encontraron resultados</template>
                    </multiselect>                 
                    <input type="text" required="true" class="hide" v-model="distincionEscalafon.pais"/>      
                </div>
                <div class="form-group">
                    <label>Descripción</label>
                    <textarea type="text" required="true" class="form-control" rows="5" v-model="distincionEscalafon.descripcion" ></textarea>
                </div>
                <div class="form-group">
                    <label>Fecha Premio (día/mes/año)</label>
                    <div class="input-group date">
                        <input type="date"
                               id="fechaPremio"
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
                distincionEscalafon: {pais: null},
                listPais: [],
                rutaModulo: "/escalafon/distincion",
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
                let fechaPremioMoment = moment($('#fechaPremio').val());
                this.distincionEscalafon.fechaPremio = fechaPremioMoment.format("DD/MM/YYYY");
            },
            open(item) {
                let $vue = this;
                $('#form-validar-distincion-escalafon').parsley().destroy();
                document.getElementById('fechaPremio').value = null;
                $vue.distincionEscalafon = {escalafon: {id: $vue.escalafon.id}, pais: null};
                if (item.id != null) {
                    $vue.distincionEscalafon = {...item};
                    $vue.setFechaInput($vue.distincionEscalafon.fechaPremio, "fechaPremio");
                }
                $vue.$refs.distincionEscalafonModal.open();
            },
            searchPais(nombre) {
                let $vue = this;
                if (nombre == null || nombre.trim().length == 0) {
                    return;
                }
                $vue.listPais = [];
                axios.get("/comun/buscar/allPaises", {params: {nombre: nombre}})
                        .then(response => {
                            $vue.listPais = response.data.data;
                        });
            },
            save() {
                let $vue = this;
                if (!$("#form-validar-distincion-escalafon").parsley().validate()) {
                    return;
                }
                axios.post($vue.rutaModulo + "/save", $vue.distincionEscalafon)
                        .then(function (response) {
                            if (response.data.success) {
                                notify(response.data.message, "success");
                                $vue.$parent.loadList();
                                $vue.$refs.distincionEscalafonModal.close();
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

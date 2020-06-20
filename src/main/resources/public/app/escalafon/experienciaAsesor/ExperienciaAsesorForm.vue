<template>
    <modal-vik id="experienciaAsesorModal" ref="experienciaAsesorModal" v-bind:okaction="save">
        <template v-slot:body>
            <form id="form-validar-experiencia-asesor">
                <div class="form-group">
                    <label>Universidad</label>
                    <multiselect  
                        v-model="experienciaAsesor.universidad"
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
                    <input type="text" required="true" class="hide" v-model="experienciaAsesor.universidad"/>  
                </div> 
                <div class="form-group">
                    <label>Tipo Tesis</label>
                    <multiselect
                        v-model="experienciaAsesor.tipoTesis"
                        placeholder="Seleccionar el tipo"
                        v-bind:options="listGradoEnum"
                        track-by="name"
                        label="descripcion"
                        v-bind:show-labels="false"
                        v-bind:allow-empty="false">                                 
                    </multiselect>       
                    <input type="text" required="true" class="hide" v-model="experienciaAsesor.tipoTesis"/>
                </div>
                <div class="form-group">
                    <label>Tesista</label>
                    <input type="text" class="form-control"  required="true" v-model="experienciaAsesor.tesista"/>
                </div>
                <div class="form-group">
                    <label>Repositorio</label>
                    <input type="text" class="form-control" v-model="experienciaAsesor.urlRepositorio"/>
                </div>
                <div class="form-group">
                    <label>Fecha Aceptación (día/mes/año)</label> PENDIENTE
                    <div class="input-group">
                        <date-picker 
                            style="height: 40px;"
                            v-bind:config="configDate"
                            class="float-left"
                            v-model="experienciaAsesor.fechaAceptacion">
                        </date-picker>                  
                        <div class="input-group-append">
                            <span class="input-group-text align-middle">
                                <i class="fas fa-calendar-alt"></i>
                            </span>
                        </div>
                    </div>
                </div>
            </form>
        </template>
    </modal-vik>
</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component('date-picker', VueBootstrapDatetimePicker.default);

    const ModalVik = httpVueLoader('/_vue/modules/ModalVik.vue');
    module.exports = {
        components: {ModalVik},
        data() {
            return{
                listGradoEnum: JSON.parse(listGradoEscalafonEnumJson),
                experienciaAsesor: {universidad: null},
                listUniversidad: [],
                rutaModulo: "/escalafon/experienciaAsesor",
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
            open(item) {
                let $vue = this;
                $vue.experienciaAsesor = {escalafon: {id: $vue.escalafon.id}, universidad: null};
                if (item.id != null) {
                    $vue.experienciaAsesor = {...item};
                    $vue.experienciaAsesor.tipoTesis = $vue.listGradoEnum.find(item => item.name == $vue.experienciaAsesor.tipoTesis);
                }
                $vue.$refs.experienciaAsesorModal.open();
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
                if (!$("#form-validar-experiencia-asesor").parsley().validate()) {
                    return;
                }
                let item = Object.assign({}, $vue.experienciaAsesor);
                item.tipoTesis = item.tipoTesis.name;
                axios.post($vue.rutaModulo + "/save", item)
                        .then(function (response) {
                            if (response.data.success) {
                                notify(response.data.message, "success");
                                $vue.$parent.loadList();
                                $vue.$refs.experienciaAsesorModal.close();
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

<template>
    <modal-vik id="academicoEscalafonModal" ref="academicoEscalafonModal" v-bind:okaction="save">
        <template v-slot:body>
            <form id="form-validar-academico-escalafon">
                <div class="form-group">
                    <label>País</label>
                    <multiselect  
                        v-model="academicoEscalafon.pais"
                        label='nombre'
                        track-by='id'
                        v-bind:options='listPais'
                        placeholder="Seleccione el país"
                        v-on:search-change="searchPais"
                        v-bind:allow-empty="false"
                        v-bind:show-labels="false"
                        v-bind:hide-selected="false"
                        v-on:input='changePais'>              
                        <template slot="noOptions">La lista se encuentra vacía</template>
                        <template slot="noResult">No se encontraron resultados</template>
                    </multiselect>                 
                    <input type="text" required="true" class="hide" v-model="academicoEscalafon.pais"/>      
                </div>
                <div class="form-group" v-if="academicoEscalafon.pais != null &AMP;&AMP; academicoEscalafon.pais.nombre == 'Perú'">
                    <label>Universidad</label>
                    <multiselect  
                        v-model="academicoEscalafon.universidad"
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
                    <input type="text" required="true" class="hide" v-model="academicoEscalafon.universidad"/>  
                </div>
                <div class="form-group" v-if="academicoEscalafon.pais != null &AMP;&AMP; academicoEscalafon.pais.nombre != 'Perú'">
                    <label>Institución</label>
                    <input type="text" class="form-control"  required="true" v-model="academicoEscalafon.institucion"/>
                </div>
                <div class="form-group">
                    <label>Grado</label>
                    <multiselect
                        v-model="academicoEscalafon.grado"
                        placeholder="Seleccionar el grado"
                        v-bind:options="listGradoEnum"
                        track-by="name"
                        label="descripcion"
                        v-bind:show-labels="false"
                        v-bind:allow-empty="false">                                 
                    </multiselect>       
                    <input type="text" required="true" class="hide" v-model="academicoEscalafon.grado"/>         
                </div>
                <div class="form-group">
                    <label>Fecha Inicio (día/mes/año)</label> PENDIENTE
                    <!--                    <div class="input-group">
                                            <date-picker 
                                                style="height: 40px;"
                                                v-bind:config="configDate"
                                                class="float-left"
                                                v-model="academicoEscalafon.fechaInicio">
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
                                                v-model="academicoEscalafon.fechaFin">
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

    const ModalVik = httpVueLoader('/_vue/modules/ModalVik.vue');
    module.exports = {
        components: {ModalVik},
        data() {
            return{
                listGradoEnum: JSON.parse(listGradoEscalafonEnumJson),
                academicoEscalafon: {pais: null, universidad: null},
                listPais: [],
                listUniversidad: [],
                rutaModulo: "/escalafon/academico",
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
                $vue.academicoEscalafon = {escalafon: {id: $vue.escalafon.id}, pais: null, universidad: null};
                if (item.id != null) {
                    $vue.academicoEscalafon = {...item};
                    $vue.academicoEscalafon.grado = $vue.listGradoEnum.find(item => item.name == $vue.academicoEscalafon.grado);
                    if ($vue.academicoEscalafon.universidad.id == null) {
                        $vue.academicoEscalafon.universidad = null;
                    }
                }
                $vue.$refs.academicoEscalafonModal.open();
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
            changePais() {
                this.academicoEscalafon.universidad = null;
                this.academicoEscalafon.institucion = null;
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
                if (!$("#form-validar-academico-escalafon").parsley().validate()) {
                    return;
                }
                let item = Object.assign({}, $vue.academicoEscalafon);
                item.grado = item.grado.name;
                axios.post($vue.rutaModulo + "/save", item)
                        .then(function (response) {
                            if (response.data.success) {
                                notify(response.data.message, "success");
                                $vue.$parent.loadList();
                                $vue.$refs.academicoEscalafonModal.close();
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

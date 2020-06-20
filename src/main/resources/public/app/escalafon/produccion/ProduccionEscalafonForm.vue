<template>
    <modal-vik id="produccionModal" ref="produccionModal" v-bind:okaction="save">
        <template v-slot:body>
            <form id="form-validar-produccion-escalafon">
                <div class="form-group">
                    <label>Tipo Experiencia</label>
                    <div class="col-md-12">
                        <div class="col-sm-5">
                            <label class="radio inline">
                                <input type="radio" required="true" value="CIENTIFICA" v-model="produccionEscalafon.tipo"/> Científica
                            </label>
                        </div>
                        <div class="col-sm-5">
                            <label class="radio inline">
                                <input type="radio" required="true" value="OTROS" v-model="produccionEscalafon.tipo"/> Otros
                            </label>
                        </div>
                    </div>
                    <input type="text" required="true" class="hide" v-model="produccionEscalafon.tipo"/>                
                </div>
                <div class="form-group">
                    <label>Título</label>
                    <textarea type="text" required="true" class="form-control" rows="2" v-model="produccionEscalafon.titulo" ></textarea>
                </div>
                <div class="form-group" v-if='produccionEscalafon.tipo == "CIENTIFICA"'>
                    <label>Sub Tipo Producción</label>
                    <multiselect  
                        v-model="produccionEscalafon.subTipo"
                        placeholder="Seleccione el sub tipo"
                        v-bind:options="listSubTipoEnum"
                        v-bind:show-labels="false"
                        v-bind:allow-empty="false">                                 
                    </multiselect>                 
                    <input type="text" required="true" class="hide" v-model="produccionEscalafon.subTipo"/>                
                </div>
                <div class="form-group">
                    <label>Título Fuente</label>
                    <textarea type="text" required="true" class="form-control" rows="2" v-model="produccionEscalafon.tituloFuente" ></textarea>
                </div>
                <div class="form-group">
                    <label>Autores</label>
                    <textarea type="text" required="true" class="form-control" rows="2" v-model="produccionEscalafon.autores" ></textarea>
                </div>
                <div class="form-group">
                    <label>Año de Producción</label>
                    <input type="number" min="1" id="numero" maxlength="4" required="true" class="form-control sin-espacios numerico" v-model="produccionEscalafon.anioProduccion">
                </div>
                <div class="form-group">
                    <label>Repositorio</label>
                    <input type="text" class="form-control" v-model="produccionEscalafon.urlRepositorio"/>
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
                listSubTipoEnum: ["Artículo", "Conferencia", "Libro", "Tesis"],
                listTipoProduccionEnum: JSON.parse(listTipoProduccionEnumJson),
                produccionEscalafon: {},
                rutaModulo: "/escalafon/produccion",
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
                $vue.produccionEscalafon = {escalafon: {id: $vue.escalafon.id}, subTipo: null};
                if (item.id != null) {
                    $vue.produccionEscalafon = {...item};
                    if ($vue.produccionEscalafon.tipo == "CIENTIFICA") {
                        $vue.produccionEscalafon.subTipo = $vue.listSubTipoEnum.find(item => item.name == $vue.produccionEscalafon.subTipo);
                    }
                }
                $vue.$refs.produccionModal.open();
            },
            save() {
                let $vue = this;
                if (!$("#form-validar-produccion-escalafon").parsley().validate()) {
                    return;
                }
                let item = Object.assign({}, $vue.produccionEscalafon);
                if (item.tipo == "CIENTIFICA") {
                    item.subTipo = item.subTipo.name;
                } else {
                    item.subTipo = null;
                }
                axios.post($vue.rutaModulo + "/save", item)
                        .then(function (response) {
                            if (response.data.success) {
                                notify(response.data.message, "success");
                                $vue.$parent.loadList();
                                $vue.$refs.produccionModal.close();
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

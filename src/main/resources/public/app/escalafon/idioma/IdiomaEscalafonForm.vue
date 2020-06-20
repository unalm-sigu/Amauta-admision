<template>
    <modal-vik id="idiomaEscalaModal" ref="idiomaEscalaModal" v-bind:okaction="save">
        <template v-slot:body>
            <form id="form-validar-idioma-escalafon">
                <div class="form-group">
                    <label>Idioma</label>
                    <multiselect  
                        v-model="idiomaEscalafon.idioma"
                        label="nombre"
                        placeholder="Seleccione el idioma"
                        track-by="id"
                        v-bind:options="listIdioma"
                        v-bind:show-labels="false"
                        v-bind:allow-empty="false">                                 
                    </multiselect>
                    <input type="text" required="true" class="hide" v-model="idiomaEscalafon.idioma"/>
                </div>
                <div class="form-group" v-if="idiomaEscalafon.idioma != null &amp;&amp; idiomaEscalafon.idioma.nombre == 'Otros'">
                    <label>Otro Idioma</label>
                    <input v-model="idiomaEscalafon.idiomaOtro" type="text" class="form-control" required>
                </div>
                <div class="form-group">
                    <label>Materno</label>
                    <input type="checkbox" v-model="idiomaEscalafon.lenguaMaterna" v-on:click="selectMaterno" />
                </div>
                <div class="form-group">
                    <label>Nivel Conversación</label>
                    <multiselect  
                        v-bind:disabled="idiomaEscalafon.lenguaMaterna"
                        v-model="idiomaEscalafon.conversacion"
                        placeholder="Nivel"
                        v-bind:options="listNivel"
                        v-bind:show-labels="false"
                        v-bind:allow-empty="false">                                 
                    </multiselect>                 
                    <input type="text" required="true" class="hide" v-model="idiomaEscalafon.conversacion"/>
                </div>
                <div class="form-group">
                    <label>Nivel Lectura</label>
                    <multiselect  
                        v-bind:disabled="idiomaEscalafon.lenguaMaterna"
                        v-model="idiomaEscalafon.lectura"
                        placeholder="Nivel"
                        v-bind:options="listNivel"
                        v-bind:show-labels="false"
                        v-bind:allow-empty="false">                                 
                    </multiselect>       
                    <input type="text" required="true" class="hide" v-model="idiomaEscalafon.lectura"/>
                </div>
                <div class="form-group">
                    <label>Nivel Escritura</label>
                    <multiselect  
                        v-bind:disabled="idiomaEscalafon.lenguaMaterna"
                        v-model="idiomaEscalafon.escritura"
                        placeholder="Nivel"
                        v-bind:options="listNivel"
                        v-bind:show-labels="false"
                        v-bind:allow-empty="false">                                 
                    </multiselect>
                    <input type="text" required="true" class="hide" v-model="idiomaEscalafon.escritura"/>
                </div>
            </form>
        </template>
    </modal-vik>
</template>
<script>
    const ModalVik = httpVueLoader('/_vue/modules/ModalVik.vue');
    module.exports = {
        components: {ModalVik},
        data() {
            return{
                listIdioma: JSON.parse(listIdiomaJson),
                listNivel: JSON.parse(listNivelEnumJson),
                idiomaEscalafon: {idioma: null, lenguaMaterna: false}
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
                $vue.idiomaEscalafon = {escalafon: {id: $vue.escalafon.id}, idioma: null, lenguaMaterna: false};
                if (item.id != null) {
                    $vue.idiomaEscalafon = {...item};
                }
                $vue.$refs.idiomaEscalaModal.open();
            },
            isIdiomaRegistrado() {
                let $vue = this;
                let isFound = $vue.escalafon.idiomaEscalafon.some(item => item.idioma.id == $vue.idiomaEscalafon.idioma.id);
                return isFound;
            },
            isDuplicado() {
                let $vue = this;
                let isEqual = false;
                if ($vue.isIdiomaRegistrado() && $vue.idiomaEscalafon.id == null) {
                    isEqual = true;
                }
                $vue.escalafon.idiomaEscalafon.forEach(function (item) {
                    if (item.id != $vue.idiomaEscalafon.id && item.idioma.id == $vue.idiomaEscalafon.idioma.id) {
                        isEqual = true;
                    }
                });
                return isEqual;
            },
            selectMaterno() {
                let $vue = this;
                if (!$vue.idiomaEscalafon.lenguaMaterna) {
                    $vue.idiomaEscalafon.conversacion = "Avanzado";
                    $vue.idiomaEscalafon.escritura = "Avanzado";
                    $vue.idiomaEscalafon.lectura = "Avanzado";
                } else {
                    $vue.idiomaEscalafon.conversacion = null;
                    $vue.idiomaEscalafon.escritura = null;
                    $vue.idiomaEscalafon.lectura = null;
                }
            },
            save() {
                let $vue = this;
                if (!$("#form-validar-idioma-escalafon").parsley().validate()) {
                    return;
                }
                if ($vue.isDuplicado()) {
                    notify("El idioma seleccionado ya se encuentra registrado", "warning");
                    return;
                }
                axios.post("/escalafon/idioma/save", $vue.idiomaEscalafon)
                        .then(function (response) {
                            if (response.data.success) {
                                notify(response.data.message, "success");
                                $vue.$parent.loadList();
                                $vue.$refs.idiomaEscalaModal.close();
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

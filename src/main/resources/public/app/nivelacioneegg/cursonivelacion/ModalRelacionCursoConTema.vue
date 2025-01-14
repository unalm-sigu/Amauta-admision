<template>
    <modal-vik ref="modalRelacionCursoConTema"
               v-bind="modalRelacionCursoConTema"
               v-bind:okaction="saveRelacion">
        <div slot="body">

            <h4 class="text-primary block m-b-lg">{{title}}</h4>

            <form v-bind:id="form" data-parsley-validate="">
                <template>
                    <div class="form-group has-success">
                        <label class="control-label" for="inputSuccess1"><strong>Lista de Temas relacionadas</strong></label>
                        <hr/>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <div class="has-success" v-for="(tema,index) in temas" :key="tema.id">
                                    <div class="checkbox" v-if="index < 6">
                                        <label>
                                            <input type="checkbox" id="checkboxSuccess" :value="tema.id" v-model="seleccionados">
                                            {{tema.nombre}}
                                        </label>
                                    </div>

                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <div class="has-success" v-for="(tema,index) in temas" :key="tema.id">
                                    <div class="checkbox" v-if="index > 5">
                                        <label>
                                            <input type="checkbox" id="checkboxSuccess" :value="tema.id" v-model="seleccionados">
                                            {{tema.nombre}}
                                        </label>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                </template>
            </form>
        </div>
    </modal-vik>
</template>

<script>

    module.exports = {

        data() {
            return {
                form: "id-form-relacion",
                title: "",
                curso: {id: null, nombre: ''},
                temas: [],
                cursoListTemas: {curso: {id: '', nombre: ''}, ids: []},
                seleccionados: [],
                tema: {id: null, codigo: '', nombre: ''},
                raptor: null,
                modalRelacionCursoConTema: VUE_MODAL.structFormAjax({
                    id: "id-modal-relacion-curso-con-tema",
                    okbtn: "Guardar",
                    okclass: "btn-primary"
                })
            };
        },
        mounted() {

        },
        methods: {

            abrirModalRelacion(item, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();
                this.cursoListTemas.curso = item;

                this.title = item.codigo + " " + item.nombre;
                this.$refs.modalRelacionCursoConTema.open();
                this.raptor = raptor;

                let $vue = this;
                $vue.getTemas();
                $vue.getCursoTemasSeleccionados();
            },

            getTemas() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allTemas`
                })).then((resp) => this.temas = resp.data.data);
            },
            getCursoTemasSeleccionados() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/getCursoTemas`,
                    modal: this.$refs.modalRelacionCursoConTema,
                    body: this.cursoListTemas
                })).then((resp) => this.seleccionados = resp.data.data.ids);
            },
            saveRelacion() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                this.cursoListTemas.ids = this.seleccionados;

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/saveRelacion`,
                    modal: this.$refs.modalRelacionCursoConTema,
                    raptor: this.raptor,
                    body: this.cursoListTemas
                }));
            }

        }
    };
</script>
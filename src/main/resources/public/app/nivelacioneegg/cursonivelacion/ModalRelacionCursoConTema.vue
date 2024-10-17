<template>
    <modal-vik ref="modalRelacionCursoConTema"
               v-bind="modalRelacionCursoConTema"
               v-bind:okaction="saveRelacion">
        <div slot="body">

            <h4 class="text-primary block m-b-lg">{{title}}</h4>

            <form v-bind:id="form" data-parsley-validate="">
                <template>
                    <div class="form-group has-success">
                        <label class="control-label" for="inputSuccess1">Lista de Temas</label>
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

                this.curso = {id: '', nombre: ''};
                this.title = "Relacionar el curso " + item.codigo + " " + item.nombre + " con temas";
                this.$refs.modalRelacionCursoConTema.open();
                this.raptor = raptor;

                let $vue = this;
                $vue.temas = [
                    {id: 1, codigo: '', nombre: 'Tema 1'},
                    {id: 2, codigo: '', nombre: 'Tema 2'},
                    {id: 3, codigo: '', nombre: 'Tema 3'},
                    {id: 4, codigo: '', nombre: 'Tema 4'},
                    {id: 5, codigo: '', nombre: 'Tema 5'},
                    {id: 6, codigo: '', nombre: 'Tema 6'},
                    {id: 7, codigo: '', nombre: 'Tema 7'},
                    {id: 8, codigo: '', nombre: 'Tema 8'},
                    {id: 9, codigo: '', nombre: 'Tema 9'},
                    {id: 10, codigo: '', nombre: 'Tema 10'},
                    {id: 11, codigo: '', nombre: 'Tema 11'},
                    {id: 12, codigo: '', nombre: 'Tema 12'},
                ];
                console.log($vue.temas);

            },
            getTemas() {


            },
            editar(item, raptor) {
//                var form = $("#" + this.form);
//                form.parsley().destroy();
//
//                this.curso = JSON.parse(JSON.stringify(item));
//                this.title = "Editar Curso Nivelación";
//                this.raptor = raptor;
//                this.$refs.modalCurso.open();
//                this.$refs.modalCurso.okbtn = "Actualizar";
            },

            saveRelacion() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }
                console.dir(this.seleccionados);
//                myUtils.axios(VUE_AXIOS.structModalClose({
//                    url: `/${rutaModulo}/save`,
//                    modal: this.$refs.modalRelacionCursoConTema,
//                    raptor: this.raptor,
//                    body: this.curso
//                }));
            }

        }
    };
</script>
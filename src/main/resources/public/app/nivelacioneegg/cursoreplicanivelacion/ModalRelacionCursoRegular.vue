<template>
    <modal-vik ref="modalRelacionCursoRegular"
               v-bind="modalRelacionCursoRegular"
               v-bind:okaction="saveRelacionCurso">
        <div slot="body">

            <h4 class="text-primary block m-b-lg">Lista de cursos regulares</h4>

            <form v-bind:id="form" data-parsley-validate="">
                <template>

                    <div class="row">
                        <div class="col-md-9">
                            <div class="form-group">
                                <label>Curso</label>

                                <multiselect v-model="cursoRegular"
                                             v-bind:options="cursos"
                                             v-bind:allow-empty="false"
                                             v-on:search-change="searchCursoDebounce"
                                             track-by="id"
                                             placeholder="Seleccione un curso"
                                             v-bind:internal-search="false"
                                             v-bind:showNoOptions="true"
                                             v-bind:show-labels="false">

                                    <template slot="singleLabel" slot-scope="props">
                                        <span class="text-success h4">{{ props.option.nombre }}</span>
                                    </template>

                                    <template slot="option" slot-scope="props">
                                        <span class="block">{{ props.option.codigo }} - {{ props.option.nombre }}</span>
                                    </template>

                                    <template slot="noOptions">Lista vacía</template>
                                    <template slot="noResult">Sin resultados</template>

                                </multiselect>

                                <!--<input v-bind:value="getObjectId(cursoNiv.cursoCiclo.curso)" required="true" type="text" class="hide"/>-->

                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="form-group v-middle">
                                <label></label>
                                <button class="btn btn-primary" v-on:click="addCursoRegular(cursoRegular)">Agregar</button>
                            </div>
                        </div>
                    </div>

                    <!--                    <div class="row" v-if="cursosRegulares.size > 0">
                                            <table class="table table-striped">
                                                <thead class="panel panel-heading">
                                                    <tr>
                                                        <td>Curso</td>
                                                        <td>Curso</td>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <tr v-for="item in cursosRegulares">
                                                        <td class="v-middle">{{item.codigo}} {{item.nombre}}</td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </div>-->
                </template>
            </form>
        </div>
    </modal-vik>
</template>

<script>

    module.exports = {

        data() {
            return {
                form: "id-form-relacion-curso",
                title: "",
                cursosReplica: {curso: {id: '', codigo: '', nombre: ''}, cursosRegulares: [{id: '', codigo: '', nombre: ''}]},
                raptor: null,
                cursos: [],
                cursoRegular: '',
                modalRelacionCursoRegular: VUE_MODAL.structFormAjax({
                    id: "id-modal-relacion",
                    okbtn: "Guardar",
                    okclass: "btn-primary"
                })
            };
        },
        created() {
            this.searchCursoDebounce = debounce(this.searchCurso, 800);
        },
        mounted() {

        },
        methods: {

            abrirModalRelacion(item, raptor) {
                console.log("raptorrrrrrrrrrrrrrrrrrrr")
                console.dir(raptor);

                var form = $("#" + this.form);
                form.parsley().destroy();

                console.log("REFSSS")
                console.dir(this);

                this.title = 'Relacionar el curso ' + item.codigo + " " + item.nombre;
                console.log(this.title);

                this.$refs.modalRelacionCursoRegular.open();
                this.raptor = raptor;

            },

            searchCurso(nombre) {
                if (nombre) {
                    myUtils.axios(VUE_AXIOS.structGetData({
                        url: `/${rutaModulo}/searchCurso?nombre=${nombre}`
                    })).then((resp) => this.cursos = resp.data.data);
                }
            },
            addCursoRegular(curso) {
                console.dir(curso);
                cursosReplica.push(curso);

            },
            saveRelacionCurso() {
//                var form = $("#" + this.form);
//                if (!form.parsley().validate()) {
//                    return;
//                }
//
//                this.cursoListTemas.ids = this.seleccionados;
//
//                myUtils.axios(VUE_AXIOS.structModalClose({
//                    url: `/${rutaModulo}/saveRelacion`,
//                    modal: this.$refs.modalRelacionCursoConTema,
//                    raptor: this.raptor,
//                    body: this.cursoListTemas
//                }));
            }

        }
    };
</script>
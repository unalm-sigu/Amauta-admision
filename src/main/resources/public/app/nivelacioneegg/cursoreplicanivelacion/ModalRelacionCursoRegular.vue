<template>
    <modal-vik
        ref="modalRelacionCursoRegular"
        v-bind="modalRelacionCursoRegular"
        v-bind:okaction="saveRelacionCurso"
        >
        <div slot="body">
            <h3 class="text-dark block m-b-lg bold">{{title}}</h3>

            <form v-bind:id="form" data-parsley-validate="">
                <div class="row">
                    <div class="col-md-9">
                        <div class="form-group">
                            <label><!-- Curso --></label>

                            <multiselect
                                v-model="cursoRegularModel"
                                v-bind:options="cursos"
                                v-bind:allow-empty="false"
                                v-on:search-change="searchCursoDebounce"
                                track-by="id"
                                placeholder="Seleccione un curso"
                                v-bind:internal-search="false"
                                v-bind:showNoOptions="true"
                                v-bind:show-labels="false">
                                <template slot="singleLabel" slot-scope="props">
                                    <span class="text-primary h5 bold"
                                          >{{ props.option.codigo }} - {{ props.option.nombre }}</span>
                                </template>

                                <template slot="option" slot-scope="props">
                                    <span class="block"
                                          >{{ props.option.codigo }} - {{ props.option.nombre }}</span>
                                </template>

                                <template slot="noOptions">Lista vacía</template>
                                <template slot="noResult">Sin resultados</template>
                            </multiselect>
                            <input type="hidden" v-model="cursoRegularModel" required="true"/>         
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="form-group v-middle">
                            <label></label>
                            <a class="btn btn-primary"
                               v-on:click="addCursoRegular(cursoRegularModel)">
                                Agregar
                            </a>
                        </div>
                    </div>
                </div>

                <div
                    class="row form-group v-middle"
                    v-if="cursosReplica && cursosReplica.cursosRegulares.length > 0">
                    <div class="form-group v-middle col-md-12">
                        <table class="table table-striped text-primary">
                            <thead
                                class="panel panel-heading"
                                style="background: darkcyan; color: aliceblue">
                                <tr>
                                    <td class="v-middle bold">Curso Regular</td>
                                    <td></td>
                                </tr>
                            </thead>
                            <tbody v-if="cursosReplica.cursosRegulares.length > 0">
                                <tr v-for="(item,index) in cursosReplica.cursosRegulares">
                                    <td class="v-middle">{{ item.codigo }} {{ item.nombre }}</td>
                                    <td class="text-danger pointer bold" >
                                        <i class="fa fa-trash-o" v-on:click="eliminar(item,index)"></i>
                                    </td>
                                </tr>
                            <tbody v-else="">
                                <tr><td>sin cursos</td></tr>
                            </tbody>
                        </table>
                    </div>
                </div>
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
                cursosReplica: {
                    curso: {id: '', nombre: ''},
                    cursosRegulares: [],
                },
                raptor: null,
                cursos: [],
                cursoRegularModel: "",
                modalRelacionCursoRegular: VUE_MODAL.structFormAjax({
                    id: "id-modal-relacion",
                    okbtn: "Guardar",
                    okclass: "btn-success",
                }),
            };
        },
        created() {
            this.searchCursoDebounce = debounce(this.searchCurso, 800);
        },
        mounted() {},
        methods: {
            abrirModalRelacion(item, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.title = item.codigo + " " + item.nombre;
                this.cursosReplica.curso.id = item.id;
                this.cursosReplica.curso.nombre = item.nombre;
                this.cursosReplica.cursosRegulares = [];

                if (item.cursosReplica.length === 0) {
                    this.cursosReplica.cursosRegulares = [];
                } else {
                    item.cursosReplica.forEach((data, index) => {
                        this.cursosReplica.cursosRegulares.push(data.cursoRegular);
                    });
                }

                this.$refs.modalRelacionCursoRegular.open();
                this.raptor = raptor;

            },

            searchCurso(nombre) {
                if (nombre) {
                    myUtils.axios(VUE_AXIOS.structGetData({
                        url: `/${rutaModulo}/searchCurso?nombre=${nombre}`,
                    })).then((resp) => (this.cursos = resp.data.data));
                }
            },
            addCursoRegular(curso) {
                if (curso) {
                    const cursoExistente = this.cursosReplica.cursosRegulares.some(
                            (cursoForm) => cursoForm.codigo === curso.codigo
                    );

                    if (!cursoExistente) {
                        this.cursosReplica.cursosRegulares.push(curso);
                    }
                }

            }, eliminar(curso, index) {
                this.cursosReplica.cursosRegulares.splice(index, 1);
            },
            saveRelacionCurso() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                myUtils.axios(
                        VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/saveRelacionRegular`,
                            modal: this.$refs.modalRelacionCursoRegular,
                            raptor: this.raptor,
                            body: this.cursosReplica,
                        })
                        );
            },
        },
    };
</script>

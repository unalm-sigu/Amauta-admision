<template>
    <div>
        <div>
            <h3 class="m-b-lg">Plan de intervención tutorial</h3>

            <div class="row" v-if="esConsejero">
                <div class="col-md-1 v-middle">
                    <div class="pull-right text-primary bold">
                        Editar
                    </div>
                </div>
                <div class="col-md-1">
                    <label class="switch">
                        <input type="checkbox" v-model="editar" v-on:change="changeEditar" />
                        <span></span>
                    </label>
                </div>
                <div class="col-md-3">
                    <span v-if="editar" class="text-danger bold">Máximo 100 objetivos</span>
                </div>
            </div>

            <form v-bind:id="form">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Necesidad del estudiante</th>
                            <th>Objetivo</th>
                            <th>Estrategia tutorial</th>
                            <th>Acciones implicadas</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(item,index) in planes">
                            <td>{{item.codigo}}</td>
                            <td>
                                <template v-if="editar">
                                    <textarea v-model="item.necesidad" 
                                              v-bind:id="'necesidad-'+index"
                                              v-on:keyup="resize(1,'necesidad-'+index)" class="form-control" required="yes"></textarea>
                                </template>
                                <template v-else="">
                                    {{item.necesidad}}
                                </template>
                            </td>
                            <td>
                                <template v-if="editar">
                                    <textarea v-model="item.objetivo" 
                                              v-bind:id="'objetivo-'+index"
                                              v-on:keyup="resize(1,'objetivo-'+index)" class="form-control" required="yes"></textarea>
                                </template>
                                <template v-else="">
                                    {{item.objetivo}}
                                </template>
                            </td>
                            <td>
                                <template v-if="editar">
                                    <textarea v-model="item.estrategiaTutorial" 
                                              v-bind:id="'estrategiaTutorial-'+index"
                                              v-on:keyup="resize(1,'estrategiaTutorial-'+index)" class="form-control"></textarea>
                                </template>
                                <template v-else="">
                                    {{item.estrategiaTutorial}}
                                </template>
                            </td>
                            <td>
                                <template v-if="editar">
                                    <textarea v-model="item.accionesImplicadas" 
                                              v-bind:id="'accionesImplicadas-'+index"
                                              v-on:keyup="resize(1,'accionesImplicadas-'+index)" class="form-control"></textarea>
                                </template>
                                <template v-else="">
                                    {{item.accionesImplicadas}}
                                </template>
                            </td>
                            <td class="v-middle text-center">
                                <template v-if="editar">
                                    <i v-if="item.id" 
                                       v-on:click="deletePlan(item,index)" class="fa fa-trash fa-2x text-danger pointer" aria-hidden="true"></i>
                                    <i v-else=""
                                       v-on:click="clerPlan(item,index)" class="fa fa-trash fa-2x text-primary pointer" aria-hidden="true"></i>
                                </template>
                            </td>
                        </tr>
                        <tr v-if="editar">
                            <td></td>
                            <td colspan="4" class="text-center">
                                <button v-on:click="addPlan" class="btn btn-success">Agregar registro</button>
                            </td>
                            <td></td>
                        </tr>
                    </tbody>
                </table>
            </form>

            <div class="block" v-if="editar">
                <button v-on:click.prevent="saveDatos" class="btn btn-primary">Guadar datos</button>
            </div>
        </div>

        <modal-confirm ref="modalConfirm"></modal-confirm>
        <modal-info ref="modalInfo"></modal-info>
    </div>

</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component("date-picker", window.VueBootstrapDatetimePicker.default);

    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');

    module.exports = {
        props: {
            alumno: {}
        },

        components: {
            ModalConfirm, ModalInfo
        },
        data() {
            return {
                planes: [],
                editar: false,
                esConsejero: esConsejero,
                form: "id-form-plan-intervencion",
                idModalConfirm: "id-modal-confirm-plan-intervencion"
            };
        },
        mounted() {
            this.loadPlanesTutoria();
        },
        computed: {
        },
        methods: {
            loadPlanesTutoria() {
                console.log("loadPlanesTutoria tiempo=", new Date().getTime())
                
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allPlanesTutoria`,
                    body: {id: this.alumno.id}
                })).then((resp) => this.planes = resp.data.data);
            },
            changeEditar() {
                setTimeout(() => {
                    if (!this.editar) {
                        this.loadPlanesTutoria();
                    } else {
                        for (let idx in this.planes) {
                            this.resize(1, 'necesidad-' + idx);
                            this.resize(1, 'objetivo-' + idx);
                            this.resize(1, 'estrategiaTutorial-' + idx);
                            this.resize(1, 'accionesImplicadas-' + idx);
                        }
                    }
                }, 400);
            },
            addPlan() {
                this.planes.push({});
            },
            clerPlan(item, index) {
                this.planes.splice(index, 1);
            },
            saveDatos() {
                console.log("saveDatos tiempo=", new Date().getTime())
                
                let form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: "¿Seguro que desea guardar los datos del plan de intervención?",
                    okbtn: "Si, guardar",
                    okclass: "btn-primary",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/${this.alumno.id}/savePlan`,
                            modal: this.$refs.modalConfirm.getModal(),
                            body: this.planes
                        })).then(() => {
                            this.loadPlanesTutoria();
                            this.editar = false;
                        });
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            deletePlan(item, index) {
                console.log("deletePlan tiempo=", new Date().getTime())

                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea eliminar el registro <strong class="text-danger">${item.codigo}</strong>?`,
                    okbtn: "Si, eliminar",
                    okclass: "btn-danger",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/${this.alumno.id}/deletePlan`,
                            modal: this.$refs.modalConfirm.getModal(),
                            body: {id: item.id}
                        })).then(() => {
                            this.loadPlanesTutoria();
                            this.editar = false;
                        });
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            resize(min, id) {
                const texta = $("#" + id);
                texta.prop("rows", min);

                const scroll = texta.prop("scrollHeight");
                const rows = Math.ceil(scroll / 23);

                let rowsFinal = rows;
                if (rows < min) {
                    rowsFinal = min;
                }

                texta.prop("rows", rowsFinal);
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>
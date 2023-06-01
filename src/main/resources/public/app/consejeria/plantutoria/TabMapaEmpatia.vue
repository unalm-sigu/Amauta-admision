<template>
    <div>
        <div>
            <h3 class="m-b-lg">Mapa de empatía del Estudiante</h3>

            <div class="row" v-if="esConsejero">
                <div class="col-md-1 v-middle">
                    <div class="pull-right text-primary bold">
                        Editar
                    </div>
                </div>
                <div class="col-md-11">
                    <label class="switch">
                        <input type="checkbox" v-model="editar" v-on:change="changeEditar" />
                        <span></span>
                    </label>
                </div>
            </div>

            <form v-bind:id="form">
                <table class="table table-hover">
                    <tbody>
                        <tr v-for="item in datosAlumno">
                            <td class="col-md-3">{{item.tipoCualidadAlumno.nombre}}</td>
                            <td class="col-md-9">
                                <template v-if="editar">
                                    <input v-model="item.descripcion" type="text" class="form-control" required="yes"/>
                                </template>
                                <template v-else="">
                                    {{item.descripcionTemporal}}
                                </template>
                            </td>
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
            alumno: {},
            cualidades: {},
            tipos: {}
        },

        components: {
            ModalConfirm, ModalInfo
        },
        data() {
            return {
                editar: false,
                esConsejero: esConsejero,
                form: "id-form-empatia",
                idModalConfirm: "id-modal-confirm-empatia",
                datosAlumno: []
            };
        },
        mounted() {

        },
        computed: {
        },
        methods: {
            changeEditar() {
                this.crearData();
            },
            crearData() {
                this.datosAlumno = [];
                for (let idx in this.tipos) {
                    let tipo = this.tipos[idx];
                    if (tipo.tipoCualidad === 'MAPA_EMPATIA') {
                        let item = this.buscarItem(tipo);
                        item.descripcionTemporal = item.descripcion;
                        item.tipoCualidadAlumno = tipo;
                        item.alumno = {id: this.alumno.id};
                        this.datosAlumno.push(item);
                    }
                }
            },
            buscarItem(tipo) {
                let item = this.cualidades.find(e => e.tipoCualidadAlumno.id == tipo.id);
                if (item) {
                    return item;
                }
                return {};
            },
            revisarDatos(datos) {
                if (datos === 0) {
                    this.crearData();
                    this.editar = false;
                } else if (this.cualidades.length == datos) {
                    this.crearData();
                    this.editar = false;
                } else {
                    setTimeout(() => this.revisarDatos(datos), 100);
                }
            },
            saveDatos() {
                console.log("saveDatos tiempo=", new Date().getTime())
                let form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: "¿Seguro que desea guardar el mapa de empatía del tutorado?",
                    okbtn: "Si, guardar",
                    okclass: "btn-primary",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/${this.alumno.id}/saveCaracteristicas`,
                            modal: this.$refs.modalConfirm.getModal(),
                            body: this.datosAlumno
                        })).then(() => {
                            this.$parent.loadCualidadesAlumno();
                        });
                    }
                });

                this.$refs.modalConfirm.open(config);
            },

            // metodos genericos
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>
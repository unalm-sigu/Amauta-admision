<template>
    <modal-vik ref="modalAsistenciaCita"
               v-bind="modalAsistenciaCita"
               v-bind:okaction="saveCita">
        <div slot="body">

            <h3 class="text-primary block">{{title}}</h3>

            <form v-bind:id="form">
                <template v-if='visible'>
                    <div class="row m-t">
                        <div class="col-md-4">
                            <div class="form-group">
                                <label>Fecha cita</label>
                                <div class="item-form-control item-form-gray text-primary">{{citaNueva.fecha}}</div>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="form-group">
                                <label>Hora</label>
                                <div class="item-form-control item-form-gray text-primary">{{citaNueva.hora}}</div>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="form-group">
                                <label>Estado cita</label>
                                <div class="item-form-control item-form-gray text-primary">{{citaNueva.estadoEnum.value}}</div>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Objetivos de la cita</label>
                        <ul>
                            <li v-for="item in citaNueva.planesTutoriales">
                                {{item.objetivo}}
                            </li>
                        </ul>
                    </div>

                    <div class="form-group">
                        <label>Asunto</label>
                        <div class="item-form-control item-form-gray text-primary">{{citaNueva.asunto}}</div>
                    </div>

                    <h4 class="text-primary bold">¿Asistió el tutorado a la cita?</h4>

                    <div class="row">
                        <div class="col-sm-1"></div>
                        <div class="col-md-3">
                            <div class="form-group">
                                <label class="radio inline text-success">

                                    <input  type="radio"
                                            required="true"
                                            name="asistencia"
                                            v-on:click="verificarRpta"
                                            v-model="citaNueva.estado"
                                            id="inlineCheckbox1"
                                            value="REALIZADA"
                                            /> Asistió
                                </label>

                            </div>
                        </div>

                        <div class="col-md-3 text-danger">
                            <div class="form-group">
                                <label class="radio inline">
                                    <input  type="radio"
                                            required="true"
                                            name="asistencia"
                                            v-on:click="verificarRpta"
                                            v-model="citaNueva.estado"
                                            id="inlineCheckbox2"
                                            value="NO_ASISTIO"
                                            /> No asistió
                                </label>

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
                alumno: null,
                citaNueva: null,
                raptor: null,
                visible: false,
                editar: false,
                objetivos: [],
                form: "id-form-asistencia-cita",
                title: "Marcar asistencia de cita tutorizada",
                modalAsistenciaCita: VUE_MODAL.structFormAjax({
                    id: "id-modal-asistencia-cita",
                    okbtn: "Marcar asistencia",
                    okclass: "btn-success",
                    modalsize: "modal-lg",
                    showaccept: false
                })
            };
        },
        methods: {
            open(config, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.raptor = raptor;
                this.citaNueva = config.cita;
                this.alumno = config.alumno;
                this.visible = true;
                this.modalAsistenciaCita.showaccept = false;

                this.$refs.modalAsistenciaCita.open();
            },
            verificarRpta() {
                setTimeout(() => {
                    this.modalAsistenciaCita.showaccept = true;
                }, 300);
            },
            saveCita() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/marcarAsistenciaCita`,
                    modal: this.$refs.modalAsistenciaCita,
                    raptor: this.raptor,
                    body: this.citaNueva
                }));
            },
            getModal() {
                return this.$refs.modalAsistenciaCita;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>
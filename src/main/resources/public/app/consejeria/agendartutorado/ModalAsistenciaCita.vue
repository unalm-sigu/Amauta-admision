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
                                <label>Fecha programada</label>
                                <div class="item-form-control item-form-gray text-primary">{{citaNueva.fecha}}</div>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="form-group">
                                <label>Hora programada</label>
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

                    <div v-if="['REALIZADA','NO_ASISTIO'].includes(citaNueva.estado) " class="form-group">
                        <div v-if="citaNueva.estado == 'REALIZADA' " class="row">
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Fecha realizada</label>
                                    <div class="input-group date">
                                        <date-picker v-model="citaNueva.fechaRealizada"
                                                     required="true"
                                                     v-bind:config="configDate"
                                                     v-bind:wrap="true" >
                                        </date-picker>
                                        <div class="input-group-addon">
                                            <span class="fa fa-calendar"></span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Hora inicio</label>
                                    <date-picker v-model="citaNueva.horaInicio"
                                                 required="true"
                                                 v-bind:config="configDateStd">
                                    </date-picker>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Hora fin</label>
                                    <date-picker v-model="citaNueva.horaFin"
                                                 required="true"
                                                 v-bind:config="configDateStd">
                                    </date-picker>
                                </div>
                            </div>
                        </div>


                        <label> Comentarios de la cita (opcional)</label>
                        <textarea v-model="citaNueva.conclusiones" class="form-control" rows="4"></textarea>
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
                }),
                configDateStd: {
                    format: 'HH:mm',
                    locale: 'es'
                },
                configDate: {
                    format: 'DD/MM/YYYY',
                    locale: 'es'
                }
            };
        },
        methods: {
            open(config, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.citaNueva = config.cita;
                this.citaNueva.fechaRealizada = this.getFechaActual();
                this.citaNueva.horaFin = this.getHoraActual();
                
                this.raptor = raptor;
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
            getHoraActual() {
                const hoy = new Date();

                let horas = hoy.getHours();
                let minutos = hoy.getMinutes();

                horas = horas < 10 ? "0" + horas : horas;
                minutos = minutos < 10 ? "0" + minutos : minutos;

                return `${horas}:${minutos}`;
            },
            getFechaActual() {
                const hoy = new Date();

                let dia = hoy.getDate();
                let mes = hoy.getMonth() + 1; // Los meses en JavaScript van de 0 a 11, por lo que se suma 1.
                let anio = hoy.getFullYear();

                dia = dia < 10 ? "0" + dia : dia;
                mes = mes < 10 ? "0" + mes : mes;

                return `${dia}/${mes}/${anio}`;
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
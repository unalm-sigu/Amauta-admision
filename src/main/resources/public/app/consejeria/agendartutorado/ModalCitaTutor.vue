<template>
    <modal-vik ref="modalCitaTutor"
               v-bind="modalCitaTutor">
        <div slot="body">

            <h4 class="text-primary block">{{title}}</h4>

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

                    <div class="form-group m-b-lg">
                        <label>Asunto</label>
                        <div class="item-form-control item-form-gray text-primary">{{citaNueva.asunto}}</div>
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
                form: "id-form-cita-tutor",
                title: "Información de cita tutorizada",
                modalCitaTutor: VUE_MODAL.structInfo({
                    id: "id-modal-cita-tutor"
                })
            };
        },
        methods: {
            open(cita) {
                this.citaNueva = cita;
                this.visible = true;
                this.$refs.modalCitaTutor.open();
            },
            getModal() {
                return this.$refs.modalCitaTutor;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>
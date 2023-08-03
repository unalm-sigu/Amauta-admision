<template>
    <div>
        <div class="col-md-10">
            <div class="pull-right m-b-sm m-t-sm">
                <button v-if="esConsejero && ['PEN','OBS'].includes(informe.estado) "
                        v-on:click="calcularCantidades" class="btn btn-primary">
                    Calcular cantidades
                </button>
            </div>

            <h3 class="text-primary">Actividades realizadas en el ciclo</h3>

            <table class="table table-hover table-striped m-t-lg">
                <thead>
                    <tr>
                        <th class="col-md-9">Actividad</th>
                        <th class="col-md-3 text-center">Cantidad</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="item in informe.itemsInforme">
                        <td class="v-middle">{{item.parteInformeTutoria.nombre}}</td>
                        <td class="v-middle text-center">{{item.cantidad}}</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <modal-confirm ref="modalConfirm"></modal-confirm>
        <modal-info ref="modalInfo"></modal-info>
    </div>
</template>

<script>
    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');

    module.exports = {

        components: {
            ModalConfirm, ModalInfo
        },

        props: {
            informe: {}
        },

        data() {
            return {
                esConsejero: esConsejero,
                form: "id-form-partes-informe",
                idModalConfirm: "id-modal-confirm-partes-informe",
                title: "Modificar información de cita tutorizada"
            };
        },
        methods: {
            iniciar() {
                
            },
            calcularCantidades() {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: "¿Seguro que desea calcular las actividades de la tutoría?",
                    okbtn: "Si, calcular",
                    okclass: "btn-primary",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/calcularCantidadesInforme`,
                            modal: this.$refs.modalConfirm.getModal(),
                            body: {id: this.informe.id}
                        })).then(() => this.$parent.loadInforme());
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            validar() {
                if (this.informe.itemsInforme.length === 0) {
                    notify("No hay actividades que cuantificar", "error");
                    return false;
                }

                let cantidad = 0;
                for (let idx in this.informe.itemsInforme) {
                    let item = this.informe.itemsInforme[idx];
                    if (item.cantidad != "" && !isNaN(item.cantidad)) {
                        cantidad += parseInt(item.cantidad);
                    }
                }

                if (cantidad == 0) {
                    notify("Debe ejecutar el cálculo de actividades de la tutoría", "error");
                }

                return cantidad > 0;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>
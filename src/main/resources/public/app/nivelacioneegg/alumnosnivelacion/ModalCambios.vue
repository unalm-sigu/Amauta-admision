<template>
    <modal-vik ref="modalCambios"
               v-bind="modalCambios">
        <div slot="body">

            <h4 class="text-primary block m-b-xs">{{title}} {{ciclo.descripcion}}</h4>

            <template v-if='visible'>
                <span class="block text-primary h4 m-t-xs m-b-lg">
                    {{alumnoNiv.alumno.persona.apellidosNombres}}
                </span>

                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th class="text-center">Fecha</th>
                            <th class="text-center">Hora</th>
                            <th class="text-center">Estado</th>
                            <th class="text-center">Puntaje final</th>
                            <th class="text-center">Nota final</th>
                            <th class="text-center">Motivo cambio</th>
                            <th class="text-center">Operador</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in cambios">
                            <td class="text-center">{{item.fechaRegistro.split(' ')[0]}}</td>
                            <td class="text-center">{{item.fechaRegistro.split(' ')[1]}}</td>
                            <td class="text-center">{{item.estadoEnum.value}}</td>
                            <td class="text-center">{{item.puntajeFinal}}</td>
                            <td class="text-center">{{item.notaFinal}}</td>
                            <td class="text-center">{{item.motivo}}</td>
                            <td class="text-center">{{item.userRegistro.persona.nombreCompleto}}</td>
                        </tr>
                    </tbody>
                </table>
            </template>
        </div>
    </modal-vik>
</template>

<script>

    module.exports = {

        data() {
            return {
                cambios: [],
                alumnoNiv: null,
                visible: false,
                ciclo: JSON.parse(cicloJson),
                title: "Histórico de cambios ",
                modalCambios: VUE_MODAL.structInfo({
                    id: "id-modal-cambios",
                    modalsize: "modal-lg"
                })
            };
        },
        methods: {
            open(item) {
                this.alumnoNiv = JSON.parse(JSON.stringify(item));
                this.cambios = this.alumnoNiv.cambios;
                this.ciclo = JSON.parse(cicloJson);
                this.visible = true;
                this.$refs.modalCambios.open();
            },

            getModal() {
                return this.$refs.modalCambios;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>
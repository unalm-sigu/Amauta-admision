<template>
    <modal-vik ref="modalCambios"
               v-bind="modalCambios">
        <div slot="body">

            <h4 class="text-primary block m-b-xs">{{title}} {{ciclo.descripcion}}</h4>

            <template v-if='visible'>
                <div class="row m-b-md">
                    <div class="col-md-9">
                        <span class="item-form-control item-form-gray text-primary">
                            {{cursoNiv.cursoCiclo.curso.codigo}} -
                            {{cursoNiv.cursoCiclo.curso.nombre}}
                            <br>
                            Sección: {{cursoNiv.codigo}}
                            &nbsp;&nbsp; | &nbsp;&nbsp;
                            {{cursoNiv.horasDictado}} horas
                            &nbsp;&nbsp; | &nbsp;&nbsp;
                            Del {{cursoNiv.fechaInicio}} al {{cursoNiv.fechaFin}}
                        </span>
                    </div>

                    <div class="col-md-3">
                        <span class="item-form-control item-form-gray text-primary">
                            Plantilla
                            <br>
                            {{cursoNiv.plantilla.codigo}}
                        </span>
                    </div>
                </div>

                <div v-if="cambios.length == 0" class="alert alert-primary">
                    <h4>No hay cambios para esta sección</h4>
                </div>

                <table v-else="" class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th class="text-center wd-10">Fecha</th>
                            <th class="text-center wd-10">Hora</th>
                            <th class="text-center">Cambio</th>
                            <th class="text-center">Motivo cambio</th>
                            <th class="text-center wd-20">Operador</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in cambios">
                            <td class="v-middle text-center">{{item.fecha}}</td>
                            <td class="v-middle text-center">{{item.hora}}</td>
                            <td class="v-middle">{{item.cambio}}</td>
                            <td class="v-middle">{{item.motivo}}</td>
                            <td class="v-middle text-center">{{item.userRegistro.persona.nomPaterno}}</td>
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
                cursoNiv: null,
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
                this.cursoNiv = JSON.parse(JSON.stringify(item));
                this.cambios = [];
                this.visible = true;
                this.$refs.modalCambios.open();
                this.loadCambios();
            },

            loadCambios() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allCambios`,
                    body: {id: this.cursoNiv.id}
                })).then((resp) => this.cambios = resp.data.data);
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